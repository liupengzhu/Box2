package cn.com.larunda.safebox.recycler;

/**
 * Created by sddt on 18-2-27.
 */

public class MyBLE {
    private String bleName;
    private int status;

    public MyBLE() {
        super();
    }

    public MyBLE(String bleName, int status) {
        this.bleName = bleName;
        this.status = status;
    }

    public String getBleName() {
        return bleName;
    }

    public void setBleName(String bleName) {
        this.bleName = bleName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
