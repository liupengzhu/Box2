package cn.com.larunda.safebox.recycler;

/**
 * Created by sddt on 18-1-26.
 */

public class BoxAddUser {
    private String name;
    private String company;
    private String department;
    private String phone;
    private String pic;
    private boolean imgIsChecked = false;
    private String id;

    public BoxAddUser(String name, String company, String department, String phone) {
        this.name = name;
        this.company = company;
        this.department = department;
        this.phone = phone;
    }

    public BoxAddUser() {

    }

    public boolean isImgIsChecked() {
        return imgIsChecked;
    }

    public void setImgIsChecked(boolean imgIsChecked) {
        this.imgIsChecked = imgIsChecked;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
