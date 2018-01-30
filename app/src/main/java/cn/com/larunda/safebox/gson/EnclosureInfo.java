package cn.com.larunda.safebox.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sddt on 18-1-30.
 */

public class EnclosureInfo {
    @SerializedName("data")
    public List<EnclosureData> enclosureDataList;

    public String error;
}
