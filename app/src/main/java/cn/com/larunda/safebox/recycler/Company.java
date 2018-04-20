package cn.com.larunda.safebox.recycler;

import java.lang.ref.SoftReference;

public class Company {
    private int id;
    private String name;
    private String pic;
    private String tel;
    private String address;
    private String letter;
    private String salesAddress;
    private String email;
    private String contacts;
    private String fax;

    public Company() {
        super();
    }

    public Company(int id, String name, String pic, String tel, String address, String letter, String salesAddress, String email, String contacts) {
        this.id = id;
        this.name = name;
        this.pic = pic;
        this.tel = tel;
        this.address = address;
        this.letter = letter;
        this.salesAddress = salesAddress;
        this.email = email;
        this.contacts = contacts;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public String getSalesAddress() {
        return salesAddress;
    }

    public void setSalesAddress(String salesAddress) {
        this.salesAddress = salesAddress;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }
}
