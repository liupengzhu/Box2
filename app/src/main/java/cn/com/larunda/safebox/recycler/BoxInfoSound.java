package cn.com.larunda.safebox.recycler;

/**
 * Created by sddt on 18-1-26.
 */

public class BoxInfoSound {
    private String soundName;
    private String soundTime;
    private String soundDate;
    private boolean soundIsPlay;
    private boolean is_exist;
    private String id;
    private String path;
    public BoxInfoSound(){

    }

    public BoxInfoSound(String soundName, String soundTime, String soundDate) {
        this.soundName = soundName;
        this.soundTime = soundTime;
        this.soundDate = soundDate;
    }

    public String getSoundName() {
        return soundName;
    }

    public void setSoundName(String soundName) {
        this.soundName = soundName;
    }

    public String getSoundTime() {
        return soundTime;
    }

    public void setSoundTime(String soundTime) {
        this.soundTime = soundTime;
    }

    public String getSoundDate() {
        return soundDate;
    }

    public void setSoundDate(String soundDate) {
        this.soundDate = soundDate;
    }

    public boolean isSoundIsPlay() {
        return soundIsPlay;
    }

    public void setSoundIsPlay(boolean soundIsPlay) {
        this.soundIsPlay = soundIsPlay;
    }

    public boolean isIs_exist() {
        return is_exist;
    }

    public void setIs_exist(boolean is_exist) {
        this.is_exist = is_exist;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
