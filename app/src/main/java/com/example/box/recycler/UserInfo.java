package com.example.box.recycler;

/**
 * Created by sddt on 18-1-16.
 */

public class UserInfo {

    private String userName;
    private String userId;
    private String userQx;
    private String userImg;

    public UserInfo(String userName, String userId, String userQx, String userImg) {
        this.userName = userName;
        this.userId = userId;
        this.userQx = userQx;
        this.userImg = userImg;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserQx() {
        return userQx;
    }

    public void setUserQx(String userQx) {
        this.userQx = userQx;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }
}
