package cn.com.larunda.safebox.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sddt on 18-1-15.
 */

public class SqInfo {

    public String error;

    @SerializedName("data")
    public List<SqData> sqDataList;


}
