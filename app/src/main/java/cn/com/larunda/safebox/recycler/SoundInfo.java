package cn.com.larunda.safebox.recycler;

/**
 * Created by sddt on 18-1-25.
 */

public class SoundInfo {
    private String boxName;
    private String total;
    private String id;

    public SoundInfo(String boxName, String total) {
        this.boxName = boxName;
        this.total = total;
    }

    public SoundInfo() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBoxName() {
        return boxName;
    }

    public void setBoxName(String boxName) {
        this.boxName = boxName;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
