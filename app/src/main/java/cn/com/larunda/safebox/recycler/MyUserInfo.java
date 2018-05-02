package cn.com.larunda.safebox.recycler;

/**
 * Created by sddt on 18-1-16.
 */

public class MyUserInfo {

    private String userName;
    private String user;
    private int userId;
    private String userQx;
    private String userImg;
    private boolean imgIsChecked = false;

    public MyUserInfo() {

    }

    public MyUserInfo(String userName, String user, int userId, String userQx, String userImg) {
        this.userName = userName;
        this.userId = userId;
        this.userQx = userQx;
        this.userImg = userImg;
        this.user = user;
    }

    public boolean isImgIsChecked() {
        return imgIsChecked;
    }

    public void setImgIsChecked(boolean imgIsChecked) {
        this.imgIsChecked = imgIsChecked;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
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
