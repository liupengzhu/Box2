package com.example.box.recycler;

/**
 * Created by sddt on 18-1-23.
 */

public class LeavingAlarm {

    private String boxName;
    private String distance;
    private String name;
    private String isLeaving;
    private boolean imgIsChecked = false;

    public String getBoxName() {
        return boxName;
    }

    public void setBoxName(String boxName) {
        this.boxName = boxName;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsLeaving() {
        return isLeaving;
    }

    public void setIsLeaving(String isLeaving) {
        this.isLeaving = isLeaving;
    }

    public boolean isImgIsChecked() {
        return imgIsChecked;
    }

    public void setImgIsChecked(boolean imgIsChecked) {
        this.imgIsChecked = imgIsChecked;
    }

    public LeavingAlarm(String boxName, String distance, String name, String isLeaving) {
        this.boxName = boxName;
        this.distance = distance;
        this.name = name;
        this.isLeaving = isLeaving;
    }


}
