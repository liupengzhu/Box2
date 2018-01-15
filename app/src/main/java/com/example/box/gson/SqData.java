package com.example.box.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sddt on 18-1-15.
 */

public class SqData {

    public String user;
    public String code;
    @SerializedName("time")
    public String date;

}
