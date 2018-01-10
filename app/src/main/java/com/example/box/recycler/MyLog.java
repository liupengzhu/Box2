package com.example.box.recycler;

/**
 * Created by Administrator on 2018/1/9.
 */

public class MyLog {
    private int imageId;
    private String title;
    private String time;
    private String content;

    public MyLog(int imageId, String title, String time, String content) {
        this.imageId = imageId;
        this.title = title;
        this.time = time;
        this.content = content;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
