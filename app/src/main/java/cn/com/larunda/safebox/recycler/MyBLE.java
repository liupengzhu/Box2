package cn.com.larunda.safebox.recycler;

/**
 * Created by sddt on 18-2-27.
 */

public class MyBLE {
    private String bleName;
    private int status;
    private String url;
    private String name;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MyBLE() {
        super();
    }

    public MyBLE(String bleName, int status) {
        this.bleName = bleName;
        this.status = status;
    }

    public MyBLE(String bleName, int status, String url, String name) {
        this.bleName = bleName;
        this.status = status;
        this.url = url;
        this.name = name;
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
