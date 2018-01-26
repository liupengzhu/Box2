package cn.com.larunda.safebox.recycler;

/**
 * Created by sddt on 18-1-15.
 */

public class MySqLs {
    private String date;
    private String data;

    public MySqLs(String date, String data) {
        this.date = date;
        this.data = data;
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
