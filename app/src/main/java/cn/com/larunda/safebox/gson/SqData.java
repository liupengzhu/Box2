package cn.com.larunda.safebox.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sddt on 18-1-15.
 */

public class SqData {

    public String id;
    public String user_pic;
    public String user;
    public String code;
    @SerializedName("time")
    public String date;

}
