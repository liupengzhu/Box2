package cn.com.larunda.safebox.gson;

import java.util.List;

public class TaskInfo {

    /**
     * current_page : 1
     * data : [{"rn":"1","id":1,"box_id":"366","f_name":"安全会议文件邀请函保密配送","created_at":"2018-03-09 15:52:36","updated_at":"2018-04-16 17:48:26","company_id":"22","f_origin_city":"厦门","f_destination_city":"北京","completed_at":"2018-04-23 16:37:13"}]
     * first_page_url : http://192.168.188.129:8082/api/box/366/tasks?page=1
     * from : 1
     * last_page : 1
     * last_page_url : http://192.168.188.129:8082/api/box/366/tasks?page=1
     * next_page_url : null
     * path : http://192.168.188.129:8082/api/box/366/tasks
     * per_page : 15
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
         * rn : 1
         * id : 1
         * box_id : 366
         * f_name : 安全会议文件邀请函保密配送
         * created_at : 2018-03-09 15:52:36
         * updated_at : 2018-04-16 17:48:26
         * company_id : 22
         * f_origin_city : 厦门
         * f_destination_city : 北京
         * completed_at : 2018-04-23 16:37:13
         */

        private String rn;
        private int id;
        private String box_id;
        private String f_name;
        private String created_at;
        private String updated_at;
        private String company_id;
        private String f_origin_city;
        private String f_destination_city;
        private String completed_at;

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

        public String getBox_id() {
            return box_id;
        }

        public void setBox_id(String box_id) {
            this.box_id = box_id;
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

        public String getCompany_id() {
            return company_id;
        }

        public void setCompany_id(String company_id) {
            this.company_id = company_id;
        }

        public String getF_origin_city() {
            return f_origin_city;
        }

        public void setF_origin_city(String f_origin_city) {
            this.f_origin_city = f_origin_city;
        }

        public String getF_destination_city() {
            return f_destination_city;
        }

        public void setF_destination_city(String f_destination_city) {
            this.f_destination_city = f_destination_city;
        }

        public String getCompleted_at() {
            return completed_at;
        }

        public void setCompleted_at(String completed_at) {
            this.completed_at = completed_at;
        }
    }
}
