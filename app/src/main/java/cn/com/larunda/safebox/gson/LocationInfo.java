package cn.com.larunda.safebox.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sddt on 18-1-31.
 */

public class LocationInfo {

    public String error;
    @SerializedName("path")
    public List<PathData> pathDataList;
}
