package cn.com.larunda.safebox.recycler;

/**
 * Created by sddt on 18-1-26.
 */

public class BoxInfoLog {
    private String logName;
    private String logContent;
    private String logTime;

    public BoxInfoLog(String logName, String logContent, String logTime) {
        this.logName = logName;
        this.logContent = logContent;
        this.logTime = logTime;
    }
    public BoxInfoLog(){

    }

    public String getLogName() {
        return logName;
    }

    public void setLogName(String logName) {
        this.logName = logName;
    }

    public String getLogContent() {
        return logContent;
    }

    public void setLogContent(String logContent) {
        this.logContent = logContent;
    }

    public String getLogTime() {
        return logTime;
    }

    public void setLogTime(String logTime) {
        this.logTime = logTime;
    }
}
