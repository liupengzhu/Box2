package com.example.box.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sddt on 18-1-15.
 */

public class SqLsInfo {

    public String error;
    @SerializedName("data")
    public List<SqLsData> sqLsDataList;

}
