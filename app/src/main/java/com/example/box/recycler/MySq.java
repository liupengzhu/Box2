package com.example.box.recycler;

/**
 * Created by sddt on 18-1-15.
 */

public class MySq {
    private String userImg;
    private String userName;
    private String userXLH;
    private String date;

    public MySq() {
        super();
    }

    public MySq(String userImg, String userName, String userXLH, String date) {
        this.userImg = userImg;
        this.userName = userName;
        this.userXLH = userXLH;
        this.date = date;

    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserXLH() {
        return userXLH;
    }

    public void setUserXLH(String userXLH) {
        this.userXLH = userXLH;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


}
