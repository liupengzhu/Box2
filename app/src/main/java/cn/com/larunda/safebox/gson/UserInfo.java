package cn.com.larunda.safebox.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sddt on 18-1-29.
 */

public class UserInfo {
    @SerializedName("data")
    public List<UserData> userData;
    public String error;
    public int current_page;
    public int per_page;
}
