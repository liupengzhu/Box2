package cn.com.larunda.safebox.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sddt on 18-2-1.
 */

public class Config {
    public User user;
    public static class User {
        public String change_user;
        public String change_pwd;
        public String change_phone;
        public String change_mail;
        public String change_company;
        public String change_department;
        public String upload_fingerprint;

    }
    public String error;
}
