package cn.com.larunda.safebox.recycler;

/**
 * Created by sddt on 18-1-12.
 */

public class MyBox {
    private String box_img;
    private String box_name;
    private int box_qx;
    private boolean is_bf;
    private boolean is_sd;
    private String box_dl;
    private String id;
    private String code;
    private boolean is_use = true;

    private boolean imgIsChecked = false;

    public MyBox(String box_img, String box_name, int box_qx, boolean is_bf, boolean is_sd, String box_dl) {
        this.box_img = box_img;
        this.box_name = box_name;
        this.box_qx = box_qx;
        this.is_bf = is_bf;
        this.is_sd = is_sd;
        this.box_dl = box_dl;
    }

    public MyBox() {

    }

    public boolean isImgIsChecked() {
        return imgIsChecked;
    }

    public void setImgIsChecked(boolean imgIsChecked) {
        this.imgIsChecked = imgIsChecked;
    }

    public String getBox_img() {
        return box_img;
    }

    public void setBox_img(String box_img) {
        this.box_img = box_img;
    }

    public String getBox_name() {
        return box_name;
    }

    public void setBox_name(String box_name) {
        this.box_name = box_name;
    }

    public int getBox_qx() {
        return box_qx;
    }

    public void setBox_qx(int box_qx) {
        this.box_qx = box_qx;
    }

    public boolean isIs_bf() {
        return is_bf;
    }

    public void setIs_bf(boolean is_bf) {
        this.is_bf = is_bf;
    }

    public boolean isIs_sd() {
        return is_sd;
    }

    public void setIs_sd(boolean is_sd) {
        this.is_sd = is_sd;
    }

    public String getBox_dl() {
        return box_dl;
    }

    public void setBox_dl(String box_dl) {
        this.box_dl = box_dl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isIs_use() {
        return is_use;
    }

    public void setIs_use(boolean is_use) {
        this.is_use = is_use;
    }
}
