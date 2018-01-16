package com.example.box.recycler;

/**
 * Created by sddt on 18-1-16.
 */

public class UserLog {
    private String date;
    private String data;
    private String title;

    public UserLog(String date, String data,String title) {
        this.date = date;
        this.data = data;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
