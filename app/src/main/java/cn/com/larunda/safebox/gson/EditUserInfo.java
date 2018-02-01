package cn.com.larunda.safebox.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sddt on 18-1-29.
 */

public class EditUserInfo {
    @SerializedName("f_user")
    public String user;
    @SerializedName("f_name")
    public String name;
    @SerializedName("f_tel")
    public String tel;
    @SerializedName("f_email")
    public String email;
    public String company_id;
    public String department_id;
    @SerializedName("f_level")
    public String level;
    @SerializedName("f_fingerprint")
    public String fingerprint;
    @SerializedName("f_pic")
    public String pic;
    public String error;
    @SerializedName("company_array")
    public List<Company> companyList;

    public static class Company{
        public String id;
        public String name;
    }
}
