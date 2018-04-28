package cn.com.larunda.safebox.util;

import android.util.Log;

public enum LogType {
    USER("用户日志", 1), DEVICE("设备日志", 2),

    SYSTEM("系统日志", 3);

    private String name;
    private int index;

    LogType(String name, int index) {
        this.name = name;
        this.index = index;
    }

    // 普通方法
    public static String getName(int index) {
        for (LogType c : LogType.values()) {
            if (c.getIndex() == index) {
                return c.name;
            }
        }
        return null;
    }

    // get set 方法
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
