package cn.com.larunda.safebox.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sddt on 18-2-6.
 */

public class BoxInitInfo {
    public String error;
    @SerializedName("data")
    public List<BoxInitData> boxInitDataList;
}
