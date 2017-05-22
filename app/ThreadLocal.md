# ThreadLocal的实现原理

ThreadLocal是线程内部的数据存储类，通过它可以指定的线程中存储数据，数据存储以后，只有在指定线程中可以获取到存储的数据，对于其他线程来说则无法获取数据.
这里基于JDK7进行ThreadLocal的源码实现分析.

--------
# 自定义ThreadLocal实现

如何让大家去实现一个ThreadLocal，我相信很多同学第一时间会写出如下代码：
```java
public class ThreadLocal<T> {
    private Map<Thread, T> values = new java.util.WeakHashMap<Thread, T>();
    
    public synchronized void set(T value) {
        values.put(Thread.currentThread(), value);
    }
    
    public synchronized T get() {
        return values.get(Thread.currentThread());
    }
}
```
上述ThreadLocal其实是可以完成ThreadLocal功能的，但是在性能上却不是最优的。毕竟多线程访问ThreadLocal的map对象会导致并发冲突，用synchronized加锁会导致性能上的损失。
因此，JDK7里是将values这个Map对象保存在线程中，这样每个线程去取自己的数据，就不需要加锁保护的，具体实现需要大家继续向下阅读.

--------
# ThreadLocal源码实现

ThreadLocal的核心代码就是ThreadLocal的set和get方法，在深入分析源码之前，我们需要知道Thread类中有一个threadLocals变量，源码如下：
```java
public class Thread {
    ThreadLocal.ThreadLocalMap threadLocals = null;
}
```
这个变量就是上面说的values对象，至于这个变量如何被初始化，如何使用，大家继续看文章就会清楚了.

## ThreadLocal的set方法

```java
public class ThreadLocal<T> {
    public void set(T value) {
        Thread t = Thread.currentThread();
        // 从当前的Thread对象中取出ThreadLocalMap成员，key是ThreadLocal，value是set的值.
        ThreadLocalMap map = getMap(t);
        if (map != null)
            map.set(this, value);
        else
            createMap(t, value);
    }
}
```

在上面的set方法中，首先会通过getMap方法来获取当前线程中的ThreadLocalMap对象。
获取方法也很简单，在Thread类内部，有一个专门的成员用于存储线程的ThreadLocalMap对象：Thread.threadLocals,因此获取当前线程的ThreadLocal数据就变得异常简单了。
如果获取到的ThreadLocalMap初始值为null，那么就要对其进行初始化，调用createMap方法，我们来看一下这个方法的具体实现.

```java
public class ThreadLocal<T> {
    void createMap(Thread t, T firstValue) {
        t.threadLocals = new ThreadLocalMap(this, firstValue);
    }
    
    static class ThreadLocalMap {
        // 自定义Entry类用于存储<ThreadLocal, Value>键值对.
        static class Entry extends WeakReference<ThreadLocal> {
            Object value;
            
            Entry(ThreadLocal k, Object v) {
                super(k);
                value = v;
            }
        }
        
        private Entry[] table;
        private static final int INITIAL_CAPACITY = 16;
        private int threshold;
        
        ThreadLocalMap(ThreadLocal firstKey, Object firstValue) {
            // 使用数组来模拟实现Map.
            table = new Entry[INITIAL_CAPACITY];
            // 使用ThreadLocal的HashCode来生成下标，尽量减少哈希碰撞
            int i = firstKey.threadLocalHashCode & (INITIAL_CAPACITY - 1);
            table[i] = new Entry(firstKey, firstValue);
            size = 1;
            setThreshold(INITIAL_CAPACITY);
        }
        
        // 设置扩容resize时的阈值
        private void setThreshold(int len) {
            threshold = len * 2 / 3;
        }
    }
}
```

从上述代码中，我们可以看到，Thread的threadLocals其实是ThreadLocalMap对象。
这个ThreadLocalMap对象中，是通过一个Entry的数组来保存ThreadLocal的值。
其中，Entry对象中使用WeakReference来保存ThreadLocal，防止出现内存泄露的情况.

了解了ThreadLocalMap数据结构之后，我们来看一下这个数据结构是如何保存ThreadLocal的值的.

```java
static class ThreadLocalMap {
    private void set(ThreadLocal key, Object value) {

        // We don't use a fast path as with get() because it is at
        // least as common to use set() to create new entries as
        // it is to replace existing ones, in which case, a fast
        // path would fail more often than not.

        Entry[] tab = table;
        int len = tab.length;
        int i = key.threadLocalHashCode & (len-1);

        for (Entry e = tab[i];
             e != null;
             e = tab[i = nextIndex(i, len)]) {
            ThreadLocal k = e.get();

            if (k == key) {
                e.value = value;
                return;
            }

            if (k == null) {
                replaceStaleEntry(key, value, i);
                return;
            }
        }

        tab[i] = new Entry(key, value);
        int sz = ++size;
        if (!cleanSomeSlots(i, sz) && sz >= threshold)
            rehash();
    }
    
    private static int nextIndex(int i, int len) {
        return ((i + 1 < len) ? i + 1 : 0);
    }
}
```

通过上述代码，我们可以得到如下结论：
ThreadMap中数据存储不是用HashMap实现的，而是用Entry[]数组实现，用ThreadLocal的hash值来&长度作为下标，模拟Map。

------
## ThreadLocal的get实现

源码如下：
```java
public class ThreadLocal<T> {
    public T get() {
        Thread t = Thread.currentThread();
        ThreadLocalMap map = getMap(t);
        if (map != null) {
            ThreadLocalMap.Entry e = map.getEntry(this);
            if (e != null)
                return (T)e.value;
        }
        return setInitialValue();
    }
}
```

如果Thread的ThreadLocalMap对象不为空，则调用其getEntry方法获取ThreadLocal对应的值。
ThreadLocalMap的getEntry方法如下：
```java
static class ThreadLocalMap {
    private Entry getEntry(ThreadLocal key) {
        int i = key.threadLocalHashCode & (table.length - 1);
        Entry e = table[i];
        if (e != null && e.get() == key)
            return e;
        else
            return getEntryAfterMiss(key, i, e);
    }

    private Entry getEntryAfterMiss(ThreadLocal key, int i, Entry e) {
        Entry[] tab = table;
        int len = tab.length;

        while (e != null) {
            ThreadLocal k = e.get();
            if (k == key)
                return e;
            if (k == null)
                expungeStaleEntry(i);
            else
                i = nextIndex(i, len);
            e = tab[i];
        }
        return null;
    }
}
```


