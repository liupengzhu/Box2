package cn.com.larunda.safebox.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sddt on 18-1-29.
 */

public class BoxMessage {
    public String id;
    @SerializedName("f_aliases")
    public String name;
    @SerializedName("f_material")
    public String material;
    @SerializedName("f_size")
    public String size;
    @SerializedName("f_protect_grade")
    public String protext_level;
    @SerializedName("f_encrypt_level")
    public String encrypt_level;
    @SerializedName("f_is_locked")
    public String isLocked;
    @SerializedName("f_is_defence")
    public String isDefence;
    @SerializedName("f_electricity")
    public String electricity;
    @SerializedName("f_lock_time")
    public BoxLockTime boxLockTime;
    @SerializedName("f_is_leaving")
    public String is_leaving;

    public String f_pic;
    public int bind_area_num;
    public int bind_user_num;
    public int record_num;

    public String error;


    public String f_pic_orig;
}
