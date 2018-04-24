package cn.com.larunda.safebox.gson;

import java.util.List;

public class BoxInfo {

    /**
     * current_page : 1
     * data : [{"rn":"1","id":366,"f_electricity":"3","f_is_locked":"1","f_is_defence":"0","f_is_leaving":null,"created_at":"2018-03-22 19:27:24","updated_at":"2018-04-20 17:19:54","f_sn":"3837393231365108003E001E","f_is_area_alarm":"0","f_status":"1","f_latitude":"31.414486","f_longitude":"120.6587619","f_alias":"本地测试箱","company_id":"22","f_in_delivery":"0"},{"rn":"2","id":383,"f_electricity":"3","f_is_locked":"0","f_is_defence":"0","f_is_leaving":null,"created_at":"2018-03-26 13:30:24","updated_at":"2018-04-04 10:45:16","f_sn":"3837393231365108003C001E","f_is_area_alarm":null,"f_status":"2","f_latitude":null,"f_longitude":null,"f_alias":"实际箱","company_id":"22","f_in_delivery":"0"}]
     * first_page_url : http://192.168.188.129:8082/api/box?page=1
     * from : 1
     * last_page : 1
     * last_page_url : http://192.168.188.129:8082/api/box?page=1
     * next_page_url : null
     * path : http://192.168.188.129:8082/api/box
     * per_page : 15
     * prev_page_url : null
     * to : 2
     * total : 2
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
         * id : 366
         * f_electricity : 3
         * f_is_locked : 1
         * f_is_defence : 0
         * f_is_leaving : null
         * created_at : 2018-03-22 19:27:24
         * updated_at : 2018-04-20 17:19:54
         * f_sn : 3837393231365108003E001E
         * f_is_area_alarm : 0
         * f_status : 1
         * f_latitude : 31.414486
         * f_longitude : 120.6587619
         * f_alias : 本地测试箱
         * company_id : 22
         * f_in_delivery : 0
         */

        private String rn;
        private int id;
        private String f_electricity;
        private String f_is_locked;
        private String f_is_defence;
        private Object f_is_leaving;
        private String created_at;
        private String updated_at;
        private String f_sn;
        private String f_is_area_alarm;
        private String f_status;
        private String f_latitude;
        private String f_longitude;
        private String f_alias;
        private String company_id;
        private String f_in_delivery;

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

        public String getF_electricity() {
            return f_electricity;
        }

        public void setF_electricity(String f_electricity) {
            this.f_electricity = f_electricity;
        }

        public String getF_is_locked() {
            return f_is_locked;
        }

        public void setF_is_locked(String f_is_locked) {
            this.f_is_locked = f_is_locked;
        }

        public String getF_is_defence() {
            return f_is_defence;
        }

        public void setF_is_defence(String f_is_defence) {
            this.f_is_defence = f_is_defence;
        }

        public Object getF_is_leaving() {
            return f_is_leaving;
        }

        public void setF_is_leaving(Object f_is_leaving) {
            this.f_is_leaving = f_is_leaving;
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

        public String getF_sn() {
            return f_sn;
        }

        public void setF_sn(String f_sn) {
            this.f_sn = f_sn;
        }

        public String getF_is_area_alarm() {
            return f_is_area_alarm;
        }

        public void setF_is_area_alarm(String f_is_area_alarm) {
            this.f_is_area_alarm = f_is_area_alarm;
        }

        public String getF_status() {
            return f_status;
        }

        public void setF_status(String f_status) {
            this.f_status = f_status;
        }

        public String getF_latitude() {
            return f_latitude;
        }

        public void setF_latitude(String f_latitude) {
            this.f_latitude = f_latitude;
        }

        public String getF_longitude() {
            return f_longitude;
        }

        public void setF_longitude(String f_longitude) {
            this.f_longitude = f_longitude;
        }

        public String getF_alias() {
            return f_alias;
        }

        public void setF_alias(String f_alias) {
            this.f_alias = f_alias;
        }

        public String getCompany_id() {
            return company_id;
        }

        public void setCompany_id(String company_id) {
            this.company_id = company_id;
        }

        public String getF_in_delivery() {
            return f_in_delivery;
        }

        public void setF_in_delivery(String f_in_delivery) {
            this.f_in_delivery = f_in_delivery;
        }
    }
}
