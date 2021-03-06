package cn.com.larunda.safebox.recycler;

/**
 * Created by sddt on 18-1-17.
 */

public class Enclosure {

    private String id;
    private String name;
    private boolean imgIsChecked = false;

    public boolean isImgIsChecked() {
        return imgIsChecked;
    }

    public void setImgIsChecked(boolean imgIsChecked) {
        this.imgIsChecked = imgIsChecked;
    }

    public Enclosure(String name) {
        this.name = name;
    }

    public Enclosure() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
