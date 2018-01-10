package com.example.box.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2018/1/9.
 */

public class Home {

    public HomeInfo info;

    public HomeAlarm alarm_num;

    public OverView over_view;

    public String error;
    @SerializedName("log")
    public List<LogInfo> logList;


}
