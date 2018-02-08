package cn.com.larunda.safebox.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sddt on 18-1-16.
 */

public class TotalLogInfo {

    public String error;
    @SerializedName("data")
    public List<TotalLogData> totalLogData;
    public int current_page;

}
