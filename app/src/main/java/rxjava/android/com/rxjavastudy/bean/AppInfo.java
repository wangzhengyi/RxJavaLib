package rxjava.android.com.rxjavastudy.bean;

import android.support.annotation.NonNull;

public class AppInfo implements Comparable<Object> {
    private long lastUpdateTime;
    private String name;
    private String icon;

    public AppInfo(String name, String icon, long lastUpdateTime) {
        this.name = name;
        this.icon = icon;
        this.lastUpdateTime = lastUpdateTime;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        AppInfo anotherApp = (AppInfo)o;
        return getName().compareTo(anotherApp.getName());
    }

    @Override
    public String toString() {
        return this.getName() + ":" + this.getIcon() + ":" + this.getLastUpdateTime();
    }
}
