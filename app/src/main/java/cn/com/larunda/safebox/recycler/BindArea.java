package cn.com.larunda.safebox.recycler;

/**
 * Created by sddt on 18-1-31.
 */

public class BindArea {
    private String name;
    private String time;
    private String in_or_out;
    private boolean imgIsChecked = false;
    private String id;

    public BindArea() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIn_or_out() {
        return in_or_out;
    }

    public void setIn_or_out(String in_or_out) {
        this.in_or_out = in_or_out;
    }

    public boolean isImgIsChecked() {
        return imgIsChecked;
    }

    public void setImgIsChecked(boolean imgIsChecked) {
        this.imgIsChecked = imgIsChecked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
