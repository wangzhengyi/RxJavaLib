# DiskLruCache源码详解

-------
# 创建DiskLruCache

DiskLruCache的构造函数是private权限,需要使用DiskLruCache::open来创建DiskLruCache对象.
```java
public final class DiskLruCache implements Closeable {
    public static DiskLruCache open(File directory, int appVersion, int valueCount, long maxSize)
            throws IOException {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize <= 0");
        }
        if (valueCount <= 0) {
            throw new IllegalArgumentException("valueCount <= 0");
        }

        // prefer to pick up where we left off
        DiskLruCache cache = new DiskLruCache(directory, appVersion, valueCount, maxSize);
        if (cache.journalFile.exists()) {
            try {
                cache.readJournal();
                cache.processJournal();
                cache.journalWriter = new BufferedWriter(new FileWriter(cache.journalFile, true),
                        IO_BUFFER_SIZE);
                return cache;
            } catch (IOException journalIsCorrupt) {
//                System.logW("DiskLruCache " + directory + " is corrupt: "
//                        + journalIsCorrupt.getMessage() + ", removing");
                cache.delete();
            }
        }

        // create a new empty cache
        directory.mkdirs();
        cache = new DiskLruCache(directory, appVersion, valueCount, maxSize);
        cache.rebuildJournal();
        return cache;
    }
}
```

其中：

1. directory：硬盘缓存的目录。
2. appVersion：应用版本号。
3. valueCount：每个key对应的文件个数.
4. maxSize: 硬盘缓存的最大容量.

如果directory目录已经存在了journal记录文件，则需要根据journal文件构建硬盘缓存。否则就创建一个journal记录文件.

回归到构造函数:
```java
public final class DiskLruCache implements Closeable {
    private DiskLruCache(File directory, int appVersion, int valueCount, long maxSize) {
        this.directory = directory;
        this.appVersion = appVersion;
        this.journalFile = new File(directory, JOURNAL_FILE);
        this.journalFileTmp = new File(directory, JOURNAL_FILE_TMP);
        this.valueCount = valueCount;
        this.maxSize = maxSize;
    }
}
```

-------
# 写入缓存

写入缓存的步骤一般是：

1. 获取DiskLruCache.Editor对象。
2. 通过Editor::newOutputStream对象创建输入流
3. 通过DiskLruCache::commit保存.

## 创建Editor对象

```java
public final class DiskLruCache implements Closeable {
    public Editor edit(String key) throws IOException {
        return edit(key, ANY_SEQUENCE_NUMBER);
    }

    private synchronized Editor edit(String key, long expectedSequenceNumber) throws IOException {
        checkNotClosed();   // 检测journal文件句柄是否关闭
        validateKey(key);   // 检测key值是否合法
        Entry entry = lruEntries.get(key);
        // 请求序列号与参数序列号不匹配则不进行写入操作
        if (expectedSequenceNumber != ANY_SEQUENCE_NUMBER
                && (entry == null || entry.sequenceNumber != expectedSequenceNumber)) {
            return null; // snapshot is stale
        }
        // 如果不存在key对应的Entry，则创建一个Entry.
        if (entry == null) {
            entry = new Entry(key);
            lruEntries.put(key, entry);
        } else if (entry.currentEditor != null) {
            return null; // 有其他入口在编辑entry，返回null
        }

        // 创建一个新的Editor
        Editor editor = new Editor(entry);
        entry.currentEditor = editor;

        // 将该key作为DIRTY项纪录到journal文件中
        journalWriter.write(DIRTY + ' ' + key + '\n');
        journalWriter.flush();
        return editor;
    }
}
```

创建一个新的Editor，并且key进行关联，同时将DIRTY key记录写入到journal文件中.

## Editor.newOutputStream创建输出流

```java
public final class Editor {
    public OutputStream newOutputStream(int index) throws IOException {
        synchronized (DiskLruCache.this) {
            if (entry.currentEditor != this) {
                throw new IllegalStateException();
            }
            return new FaultHidingOutputStream(new FileOutputStream(entry.getDirtyFile(index)));
        }
    }
}
```

通过entry.getDirtyFile获取缓存文件，并根据文件创建输出流。
```java
public final class Entry {
    public File getDirtyFile(int i) {
        return new File(directory, key + "." + i + ".tmp");
    }
}
```

FaultHidingOutputStream继承自FileOutputStream，用来捕捉异常后不抛出，只做标记hasErrors，防止程序崩溃.

## Editor.commit提交变更

```java
public final class Editor {
    public void commit() throws IOException {
        if (hasErrors) {
            completeEdit(this, false);
            remove(entry.key); // the previous entry is stale
        } else {
            completeEdit(this, true);
        }
    }
}
```

如果FaultHidingOutputStream写入文件时发生错误，则hasErrors为true，需要删除key对应的键值对。如果成功，则调用DiskLruCache::completeEdit方法.
```java
public final class DiskLruCache implements Closeable{
    private synchronized void completeEdit(Editor editor, boolean success) throws IOException {
        Entry entry = editor.entry;
        if (entry.currentEditor != editor) {
            throw new IllegalStateException();
        }

        // if this edit is creating the entry for the first time, every index must have a value
        // 第一次创建key对应的entry时，readable为false，需要确保缓存文件存在.
        if (success && !entry.readable) {
            for (int i = 0; i < valueCount; i++) {
                if (!entry.getDirtyFile(i).exists()) {
                    editor.abort();
                    throw new IllegalStateException("edit didn't create file " + i);
                }
            }
        }

        for (int i = 0; i < valueCount; i++) {
            File dirty = entry.getDirtyFile(i);
            if (success) {
                if (dirty.exists()) {
                    // 缓存文件写入成功后，需要将临时缓存文件更名为正式缓存文件，并计算缓存大小
                    File clean = entry.getCleanFile(i);
                    dirty.renameTo(clean);
                    long oldLength = entry.lengths[i];
                    long newLength = clean.length();
                    entry.lengths[i] = newLength;
                    size = size - oldLength + newLength;
                }
            } else {
                deleteIfExists(dirty);
            }
        }

        redundantOpCount++;
        entry.currentEditor = null;
        if (entry.readable | success) {
            // 缓存文件更名成功后需要标记缓存文件可读，并在journal文件中增加CLEAN KEY SIZE记录
            entry.readable = true;
            journalWriter.write(CLEAN + ' ' + entry.key + entry.getLengths() + '\n');
            if (success) {
                entry.sequenceNumber = nextSequenceNumber++;
            }
        } else {
            lruEntries.remove(entry.key);
            journalWriter.write(REMOVE + ' ' + entry.key + '\n');
        }

        // 如果缓存大小超过阈值，则需要启动清理缓存服务.
        if (size > maxSize || journalRebuildRequired()) {
            executorService.submit(cleanupCallable);
        }
    }
}
```

-------
# 读取缓存

读取缓存的一般步骤是：

1. 获取DiskLruCache.Snapshot对象
2. 通过Snapshot对象获取缓存文件输入流.

## 获取Snapshot对象

```java
public final class DiskLruCache implements Closeable {
    public synchronized Snapshot get(String key) throws IOException {
        checkNotClosed();   // 查看Journal文件句柄是否关闭
        validateKey(key);   // 查看key值是否合法
        // 根据key值获取Entry对象
        Entry entry = lruEntries.get(key);
        if (entry == null) {
            return null;
        }

        if (!entry.readable) {
            return null;
        }

        /*
         * Open all streams eagerly to guarantee that we see a single published
         * snapshot. If we opened streams lazily then the streams could come
         * from different edits.
         */
        // 打开该key对应的输入文件流
        InputStream[] ins = new InputStream[valueCount];
        try {
            for (int i = 0; i < valueCount; i++) {
                ins[i] = new FileInputStream(entry.getCleanFile(i));
            }
        } catch (FileNotFoundException e) {
            // a file must have been deleted manually!
            return null;
        }

        redundantOpCount++;
        // 将journal文件中的键值对应项设置为READ状态
        journalWriter.append(READ + ' ' + key + '\n');
        if (journalRebuildRequired()) {
            executorService.submit(cleanupCallable);
        }

        return new Snapshot(key, entry.sequenceNumber, ins);
    }
}

public final class Snapshot implements Closeable {
    private final String key;
    private final long sequenceNumber;
    private final InputStream[] ins;

    private Snapshot(String key, long sequenceNumber, InputStream[] ins) {
        this.key = key;
        this.sequenceNumber = sequenceNumber;
        this.ins = ins;
    }
}
```

## 通过Snapshot对象获取输入流

```java
public final class Snapshot implements Closeable {
    public InputStream getInputStream(int index) {
        return ins[index];
    }
}
```
有了缓存文件输入流之后，就可以对文件进行读取操作了.

-------
# 删除缓存

删除缓存的步骤就是调用DiskLruCache::remove方法.

```java
public final class DiskLruCache implements Closeable {
    public synchronized boolean remove(String key) throws IOException {
        checkNotClosed();
        validateKey(key);
        Entry entry = lruEntries.get(key);
        if (entry == null || entry.currentEditor != null) {
            return false;
        }

        // 获取key对应的缓存文件进行删除，并更新当前的size总量
        for (int i = 0; i < valueCount; i++) {
            File file = entry.getCleanFile(i);
            if (!file.delete()) {
                throw new IOException("failed to delete " + file);
            }
            size -= entry.lengths[i];
            entry.lengths[i] = 0;
        }

        redundantOpCount++;
        // journal文件中增加REMOVE KEY记录
        journalWriter.append(REMOVE + ' ' + key + '\n');
        lruEntries.remove(key);

        if (journalRebuildRequired()) {
            executorService.submit(cleanupCallable);
        }

        return true;
    }
}
```