package cn.com.larunda.safebox.recycler;

/**
 * Created by sddt on 18-1-25.
 */

public class DetailedSound {
    private String soundId;
    private String time;
    private boolean isDownload;
    private boolean isExist;
    private String path;

    public DetailedSound(String soundId, String time, boolean isDownload) {
        this.soundId = soundId;
        this.time = time;
        this.isDownload = isDownload;
    }
    public DetailedSound(){

    }

    public String getSoundId() {
        return soundId;
    }

    public void setSoundId(String soundId) {
        this.soundId = soundId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isDownload() {
        return isDownload;
    }

    public void setDownload(boolean download) {
        isDownload = download;
    }

    public boolean isExist() {
        return isExist;
    }

    public void setExist(boolean exist) {
        isExist = exist;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
