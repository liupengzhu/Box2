package cn.com.larunda.safebox.recycler;

/**
 * Created by sddt on 18-3-2.
 */

public class Fingerprint {
    private String time;
    private String id;

    public Fingerprint() {
        super();
    }

    public Fingerprint(String time, String id) {
        this.time = time;
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
