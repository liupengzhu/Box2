package cn.com.larunda.safebox.recycler;

/**
 * Created by sddt on 18-1-26.
 */

public class BoxInfoSound {
    private String soundName;
    private String soundTime;
    private String soundDate;

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
}
