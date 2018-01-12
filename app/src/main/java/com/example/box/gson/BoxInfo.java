package com.example.box.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sddt on 18-1-12.
 */

public class BoxInfo {

    @SerializedName("data")
    public List<BoxData> boxDataList;

    public String error;
}
