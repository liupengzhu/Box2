package cn.com.larunda.safebox.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sddt on 18-1-31.
 */

public class PathData {
    @SerializedName("f_lon_bd")
    public String longitude;
    @SerializedName("f_lat_bd")
    public String latitude;
    public String type;

}
