package cn.com.larunda.safebox.gson;

/**
 * Created by sddt on 18-1-29.
 */

public class EditUserInfo {

    /**
     * id : 334
     * f_user : observe@lrd
     * f_name : observe
     * f_tel : 12345678901
     * f_email : observe@observe.com
     * f_level : 5
     * created_at : 2018-04-13 17:43:33
     * updated_at : 2018-04-19 11:30:14
     * company_id : 22
     * f_pic : f
     * f_pic_render : /api/file/image?f
     */

    private int id;
    private String f_user;
    private String f_name;
    private String f_tel;
    private String f_email;
    private String f_level;
    private String created_at;
    private String updated_at;
    private String company_id;
    private String f_pic;
    private String f_pic_render;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getF_user() {
        return f_user;
    }

    public void setF_user(String f_user) {
        this.f_user = f_user;
    }

    public String getF_name() {
        return f_name;
    }

    public void setF_name(String f_name) {
        this.f_name = f_name;
    }

    public String getF_tel() {
        return f_tel;
    }

    public void setF_tel(String f_tel) {
        this.f_tel = f_tel;
    }

    public String getF_email() {
        return f_email;
    }

    public void setF_email(String f_email) {
        this.f_email = f_email;
    }

    public String getF_level() {
        return f_level;
    }

    public void setF_level(String f_level) {
        this.f_level = f_level;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getF_pic() {
        return f_pic;
    }

    public void setF_pic(String f_pic) {
        this.f_pic = f_pic;
    }

    public String getF_pic_render() {
        return f_pic_render;
    }

    public void setF_pic_render(String f_pic_render) {
        this.f_pic_render = f_pic_render;
    }
}
