package cn.com.larunda.safebox.gson;


import java.util.List;

/**
 * Created by sddt on 18-1-30.
 */

public class EnclosureInfo {
    private int current_page;
    private int last_page;
    private List<DataBean> data;

    public class DataBean {
        private int id;
        private String f_name;

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
    }

    public int getCurrent_page() {
        return current_page;
    }

    public void setCurrent_page(int current_page) {
        this.current_page = current_page;
    }

    public int getLast_page() {
        return last_page;
    }

    public void setLast_page(int last_page) {
        this.last_page = last_page;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public List<DataBean> getData() {
        return data;
    }
}
