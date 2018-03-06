package cn.com.larunda.safebox.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sddt on 18-1-11.
 */

public class MenuUserInfo {

    public String id;
    @SerializedName("f_user")
    public String userId;

    @SerializedName("f_name")
    public String userName;

    @SerializedName("f_tel")
    public String userTell;

    @SerializedName("f_pic")
    public String userImg;
    @SerializedName("f_level")
    public String level;
    @SerializedName("related_box")
    public List<RelatedBox> boxList;
    public String error;
}
