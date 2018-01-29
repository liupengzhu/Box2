package cn.com.larunda.safebox.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sddt on 18-1-12.
 */

public class BoxData {
    public String id;
    @SerializedName("aliases")
    public String name;
    @SerializedName("encrypt_level")
    public String level;

    public String is_locked;

    public String is_defence;

    public String electricity;

    public String f_pic;

}
