package cn.com.larunda.safebox.util;

public enum AlarmType {
    LEAVING("离位警报", 0), FENCE("区域警报", 1), DEFENCE("布防警报", 2);
    private String name;
    private int index;

    AlarmType(String name, int index) {
        this.name = name;
        this.index = index;
    }

    // 普通方法
    public static String getName(int index) {
        for (AlarmType c : AlarmType.values()) {
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
