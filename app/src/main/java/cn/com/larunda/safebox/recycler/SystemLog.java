package cn.com.larunda.safebox.recycler;

public class SystemLog {
    private String time;
    private String content;
    private String type;
    private String uer;
    private String title;

    public SystemLog() {
        super();
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUer() {
        return uer;
    }

    public void setUer(String uer) {
        this.uer = uer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
