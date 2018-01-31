package cn.com.larunda.safebox.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sddt on 18-1-31.
 */

public class BoxAddUserInfo {

    public String error;
    @SerializedName("data")
    public List<BoxAddUserData> dataList;


}
