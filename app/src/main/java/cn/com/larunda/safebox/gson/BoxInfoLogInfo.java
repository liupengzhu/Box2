package cn.com.larunda.safebox.gson;

import java.util.List;

/**
 * Created by sddt on 18-1-31.
 */

public class BoxInfoLogInfo {

    /**
     * current_page : 1
     * data : [{"rn":"1","f_title":"获取动态口令","f_info":"管理员在2018-01-30 16:05:46请求了2333的动态口令","created_at":"2018-01-30 16:05:46"},{"rn":"2","f_title":"获取动态口令","f_info":"管理员在2018-01-30 16:06:32请求了2333的动态口令","created_at":"2018-01-30 16:06:32"},{"rn":"3","f_title":"获取动态口令","f_info":"管理员在2018-01-30 16:14:00请求了2333的动态口令","created_at":"2018-01-30 16:14:00"},{"rn":"4","f_title":"获取动态口令","f_info":"管理员在2018-01-30 16:03:02请求了2333的动态口令","created_at":"2018-01-30 16:03:02"},{"rn":"5","f_title":"获取动态口令","f_info":"管理员在2018-01-30 16:05:07请求了2333的动态口令","created_at":"2018-01-30 16:05:07"}]
     * first_page_url : http://safebox.dsmcase.com:90/api/box/log?page=1
     * from : 1
     * last_page : 8
     * last_page_url : http://safebox.dsmcase.com:90/api/box/log?page=8
     * next_page_url : http://safebox.dsmcase.com:90/api/box/log?page=2
     * path : http://safebox.dsmcase.com:90/api/box/log
     * per_page : 5
     * prev_page_url : null
     * to : 5
     * total : 39
     */

    private List<DataBean> data;
    private String error;
    public int current_page;
    public int per_page;
    public int last_page;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * rn : 1
         * f_title : 获取动态口令
         * f_info : 管理员在2018-01-30 16:05:46请求了2333的动态口令
         * created_at : 2018-01-30 16:05:46
         */

        private String f_title;
        private String f_info;
        private String created_at;

        public String getF_title() {
            return f_title;
        }

        public void setF_title(String f_title) {
            this.f_title = f_title;
        }

        public String getF_info() {
            return f_info;
        }

        public void setF_info(String f_info) {
            this.f_info = f_info;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }
    }
}
