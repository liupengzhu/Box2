package cn.com.larunda.safebox.gson;

import java.util.List;

public class CompanyInfo {

    /**
     * current_page : 1
     * data : [{"id":24,"f_name":"家乐福","f_pic":"/api/file/image?","f_tel":null}]
     * first_page_url : http://safebox.dsmcase.com:90/api/company?page=1
     * from : 1
     * last_page : 1
     * last_page_url : http://safebox.dsmcase.com:90/api/company?page=1
     * next_page_url : null
     * path : http://safebox.dsmcase.com:90/api/company
     * per_page : 8
     * prev_page_url : null
     * to : 1
     * total : 1
     */

    private int current_page;
    private int from;
    private int last_page;
    private int per_page;
    private int to;
    private int total;
    private List<DataBean> data;
    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getCurrent_page() {
        return current_page;
    }

    public void setCurrent_page(int current_page) {
        this.current_page = current_page;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getLast_page() {
        return last_page;
    }

    public void setLast_page(int last_page) {
        this.last_page = last_page;
    }

    public int getPer_page() {
        return per_page;
    }

    public void setPer_page(int per_page) {
        this.per_page = per_page;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 24
         * f_name : 家乐福
         * f_pic : /api/file/image?
         * f_tel : null
         */

        private int id;
        private String f_name;
        private String f_pic;
        private String f_tel;

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

        public String getF_pic() {
            return f_pic;
        }

        public void setF_pic(String f_pic) {
            this.f_pic = f_pic;
        }

        public String getF_tel() {
            return f_tel;
        }

        public void setF_tel(String f_tel) {
            this.f_tel = f_tel;
        }
    }
}
