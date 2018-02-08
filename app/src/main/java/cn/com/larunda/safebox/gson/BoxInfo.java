package cn.com.larunda.safebox.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sddt on 18-1-12.
 */

public class BoxInfo {

    @SerializedName("data")
    public List<BoxData> boxDataList;

    public String error;

    public int current_page;
    public int per_page;
}
