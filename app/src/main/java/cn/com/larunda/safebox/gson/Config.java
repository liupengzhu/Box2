package cn.com.larunda.safebox.gson;

/**
 * Created by sddt on 18-2-1.
 */

public class Config {

    /**
     * allow_change_user : true
     * allow_change_pwd : true
     * allow_change_avatar : true
     * allow_change_tel : false
     * allow_change_email : false
     * allow_change_name : true
     * allow_change_fingerprint : false
     * allow_change_face : true
     * allow_play_sound : true
     * customized_sound : null
     */

    private String allow_change_user;
    private String allow_change_pwd;
    private String allow_change_avatar;
    private String allow_change_tel;
    private String allow_change_email;
    private String allow_change_name;
    private String allow_change_fingerprint;
    private String allow_change_face;
    private String allow_play_sound;
    private Object customized_sound;

    public String getAllow_change_user() {
        return allow_change_user;
    }

    public void setAllow_change_user(String allow_change_user) {
        this.allow_change_user = allow_change_user;
    }

    public String getAllow_change_pwd() {
        return allow_change_pwd;
    }

    public void setAllow_change_pwd(String allow_change_pwd) {
        this.allow_change_pwd = allow_change_pwd;
    }

    public String getAllow_change_avatar() {
        return allow_change_avatar;
    }

    public void setAllow_change_avatar(String allow_change_avatar) {
        this.allow_change_avatar = allow_change_avatar;
    }

    public String getAllow_change_tel() {
        return allow_change_tel;
    }

    public void setAllow_change_tel(String allow_change_tel) {
        this.allow_change_tel = allow_change_tel;
    }

    public String getAllow_change_email() {
        return allow_change_email;
    }

    public void setAllow_change_email(String allow_change_email) {
        this.allow_change_email = allow_change_email;
    }

    public String getAllow_change_name() {
        return allow_change_name;
    }

    public void setAllow_change_name(String allow_change_name) {
        this.allow_change_name = allow_change_name;
    }

    public String getAllow_change_fingerprint() {
        return allow_change_fingerprint;
    }

    public void setAllow_change_fingerprint(String allow_change_fingerprint) {
        this.allow_change_fingerprint = allow_change_fingerprint;
    }

    public String getAllow_change_face() {
        return allow_change_face;
    }

    public void setAllow_change_face(String allow_change_face) {
        this.allow_change_face = allow_change_face;
    }

    public String getAllow_play_sound() {
        return allow_play_sound;
    }

    public void setAllow_play_sound(String allow_play_sound) {
        this.allow_play_sound = allow_play_sound;
    }

    public Object getCustomized_sound() {
        return customized_sound;
    }

    public void setCustomized_sound(Object customized_sound) {
        this.customized_sound = customized_sound;
    }
}
