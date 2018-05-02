package cn.com.larunda.safebox.gson;

import java.util.List;

/**
 * Created by sddt on 18-1-29.
 */

public class UserInfo {

    /**
     * current_page : 1
     * data : [{"rn":"1","id":334,"f_user":"observe@lrd","f_name":"observe","f_tel":"12345678901","f_email":"observe@observe.com","f_level":"5","created_at":"2018-04-13 17:43:33","updated_at":"2018-04-19 11:30:14","company_id":"22","f_pic":"f","f_pic_render":"/api/file/image?f"},{"rn":"2","id":442,"f_user":"lijun@lrd","f_name":"李俊","f_tel":"18952554298","f_email":"123456@qq.com","f_level":"5","created_at":"2018-05-02 09:34:07","updated_at":"2018-05-02 09:34:52","company_id":"22","f_pic":"avatars/JKgHLRVAecsEShb6bvvlsH1qImfdYQNWALVXUez8.jpeg","f_pic_render":"/api/file/image?avatars/JKgHLRVAecsEShb6bvvlsH1qImfdYQNWALVXUez8.jpeg"},{"rn":"3","id":429,"f_user":"my@lrd","f_name":"my","f_tel":"13456789000","f_email":"my@my.com","f_level":"5","created_at":"2018-04-28 12:49:13","updated_at":"2018-04-28 12:49:13","company_id":"22","f_pic":null,"f_pic_render":"/api/file/image?"}]
     * first_page_url : http://192.168.188.129:8082/api/user?page=1
     * from : 1
     * last_page : 1
     * last_page_url : http://192.168.188.129:8082/api/user?page=1
     * next_page_url : null
     * path : http://192.168.188.129:8082/api/user
     * per_page : 15
     * prev_page_url : null
     * to : 3
     * total : 3
     */

    private int current_page;
    private int last_page;
    private List<DataBean> data;

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * rn : 1
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

        private String rn;
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

        public String getRn() {
            return rn;
        }

        public void setRn(String rn) {
            this.rn = rn;
        }

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
}
