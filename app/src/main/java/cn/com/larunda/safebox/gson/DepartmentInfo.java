package cn.com.larunda.safebox.gson;

import java.util.List;

/**
 * Created by sddt on 18-2-1.
 */

public class DepartmentInfo {

    public String error;
    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 27
         * f_name : 采购部
         */

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
}
