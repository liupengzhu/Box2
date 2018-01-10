package com.example.box.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018/1/9.
 */

public class OverView {

    public String database;

    public String mem_percent;

    public String hd_usage;
    @SerializedName("exception_box")
    public String computer;
}
