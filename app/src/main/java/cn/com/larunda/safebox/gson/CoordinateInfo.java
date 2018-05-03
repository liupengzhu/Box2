package cn.com.larunda.safebox.gson;

/**
 * Created by sddt on 18-1-30.
 */

public class CoordinateInfo {


    /**
     * id : 457
     * f_name : 阳澄湖
     * created_at : 2018-04-27 13:33:56
     * updated_at : 2018-04-27 13:33:56
     * f_data : [[{"lng":120.751926,"lat":31.495865},{"lng":120.78987,"lat":31.507688},{"lng":120.848799,"lat":31.491185},{"lng":120.887893,"lat":31.418734},{"lng":120.828677,"lat":31.372869},{"lng":120.760262,"lat":31.378048},{"lng":120.655052,"lat":31.353381}],[]]
     * f_is_manual : 1
     * company_id : 22
     */

    private int id;
    private String f_name;
    private String created_at;
    private String updated_at;
    private String f_data;
    private String f_is_manual;
    private String company_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getF_name() {
        return f_name;
    }

    public void setF_name(String f_name) {
        this.f_name = f_name;
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

    public String getF_data() {
        return f_data;
    }

    public void setF_data(String f_data) {
        this.f_data = f_data;
    }

    public String getF_is_manual() {
        return f_is_manual;
    }

    public void setF_is_manual(String f_is_manual) {
        this.f_is_manual = f_is_manual;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }
}
