package cn.com.larunda.safebox.gson;

import java.util.List;

public class CompanyInfo {

    /**
     * current_page : 1
     * data : [{"rn":"1","id":124,"f_name":"ascdfas","created_at":null,"updated_at":"2018-04-18 08:43:35","f_pic":"/1/manage/20180418084333098921.jpg","f_tel":"18911111111","f_add":"ascdfas","f_letter":"abcd","f_fax":"ascdfas","f_sales_add":"ascdfas","f_email":"ascdfas@qq.com","f_contacts":"1212","f_pic_render":"/api/file/image?/1/manage/20180418084333098921.jpg"},{"rn":"2","id":126,"f_name":"aab","created_at":"2018-04-17 17:10:45","updated_at":"2018-04-17 17:10:45","f_pic":"aa","f_tel":"aa","f_add":"aa","f_letter":"aa","f_fax":"aa","f_sales_add":"aa","f_email":null,"f_contacts":null,"f_pic_render":"/api/file/image?aa"},{"rn":"3","id":127,"f_name":"aabc","created_at":"2018-04-17 17:11:30","updated_at":"2018-04-17 17:11:30","f_pic":"aa","f_tel":"aa","f_add":"aa","f_letter":"aabc","f_fax":"aa","f_sales_add":"aa","f_email":null,"f_contacts":null,"f_pic_render":"/api/file/image?aa"},{"rn":"4","id":128,"f_name":"aabcd","created_at":"2018-04-17 17:14:35","updated_at":"2018-04-17 17:14:35","f_pic":"aa","f_tel":"aa","f_add":"aa","f_letter":"aabcd","f_fax":"aa","f_sales_add":"aa","f_email":null,"f_contacts":null,"f_pic_render":"/api/file/image?aa"},{"rn":"5","id":125,"f_name":"aa","created_at":null,"updated_at":null,"f_pic":"aa","f_tel":"aa","f_add":"aa","f_letter":"aa","f_fax":"aa","f_sales_add":"aa","f_email":"aa","f_contacts":"aa","f_pic_render":"/api/file/image?aa"},{"rn":"6","id":22,"f_name":"朗润达","created_at":"2017-11-24 01:49:55","updated_at":"2017-11-24 01:49:55","f_pic":null,"f_tel":null,"f_add":null,"f_letter":"lrd","f_fax":null,"f_sales_add":null,"f_email":null,"f_contacts":null,"f_pic_render":"/api/file/image?"},{"rn":"7","id":25,"f_name":"沃尔玛","created_at":"2017-11-24 01:54:45","updated_at":"2017-11-24 01:54:45","f_pic":null,"f_tel":null,"f_add":null,"f_letter":null,"f_fax":null,"f_sales_add":null,"f_email":null,"f_contacts":null,"f_pic_render":"/api/file/image?"},{"rn":"8","id":1,"f_name":"万达广场","created_at":null,"updated_at":"2018-04-12 16:25:04","f_pic":"/api/file/image?/api/file/image?/1/manage/20180412161905147426.jpg","f_tel":"18806202833","f_add":"ewgfe","f_letter":"jima","f_fax":"gewg","f_sales_add":"gewg","f_email":null,"f_contacts":null,"f_pic_render":"/api/file/image?/api/file/image?/api/file/image?/1/manage/20180412161905147426.jpg"},{"rn":"9","id":24,"f_name":"家乐福","created_at":"2017-11-24 01:53:42","updated_at":"2018-04-03 14:22:40","f_pic":null,"f_tel":null,"f_add":null,"f_letter":null,"f_fax":null,"f_sales_add":null,"f_email":null,"f_contacts":null,"f_pic_render":"/api/file/image?"},{"rn":"10","id":121,"f_name":"sdfgsdg","created_at":null,"updated_at":null,"f_pic":"/1/manage/20180417124300797976.jpg","f_tel":"15111111111","f_add":"sdegre","f_letter":"erh","f_fax":"asd","f_sales_add":"esdrhb","f_email":"as@qq.com","f_contacts":"a","f_pic_render":"/api/file/image?/1/manage/20180417124300797976.jpg"},{"rn":"11","id":23,"f_name":"华润苏果","created_at":"2017-11-24 01:53:33","updated_at":"2017-12-15 09:01:02","f_pic":null,"f_tel":null,"f_add":null,"f_letter":null,"f_fax":null,"f_sales_add":null,"f_email":null,"f_contacts":null,"f_pic_render":"/api/file/image?"},{"rn":"12","id":122,"f_name":"a","created_at":null,"updated_at":"2018-04-18 08:44:38","f_pic":"a","f_tel":"a","f_add":"a","f_letter":"ab","f_fax":"a","f_sales_add":"a","f_email":"a","f_contacts":"a","f_pic_render":"/api/file/image?a"}]
     * first_page_url : http://192.168.188.129:8082/api/company?page=1
     * from : 1
     * last_page : 1
     * last_page_url : http://192.168.188.129:8082/api/company?page=1
     * next_page_url : null
     * path : http://192.168.188.129:8082/api/company
     * per_page : 15
     * prev_page_url : null
     * to : 12
     * total : 12
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
         * id : 124
         * f_name : ascdfas
         * created_at : null
         * updated_at : 2018-04-18 08:43:35
         * f_pic : /1/manage/20180418084333098921.jpg
         * f_tel : 18911111111
         * f_add : ascdfas
         * f_letter : abcd
         * f_fax : ascdfas
         * f_sales_add : ascdfas
         * f_email : ascdfas@qq.com
         * f_contacts : 1212
         * f_pic_render : /api/file/image?/1/manage/20180418084333098921.jpg
         */

        private String rn;
        private int id;
        private String f_name;
        private Object created_at;
        private String updated_at;
        private String f_pic;
        private String f_tel;
        private String f_add;
        private String f_letter;
        private String f_fax;
        private String f_sales_add;
        private String f_email;
        private String f_contacts;
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

        public String getF_name() {
            return f_name;
        }

        public void setF_name(String f_name) {
            this.f_name = f_name;
        }

        public Object getCreated_at() {
            return created_at;
        }

        public void setCreated_at(Object created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
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

        public String getF_add() {
            return f_add;
        }

        public void setF_add(String f_add) {
            this.f_add = f_add;
        }

        public String getF_letter() {
            return f_letter;
        }

        public void setF_letter(String f_letter) {
            this.f_letter = f_letter;
        }

        public String getF_fax() {
            return f_fax;
        }

        public void setF_fax(String f_fax) {
            this.f_fax = f_fax;
        }

        public String getF_sales_add() {
            return f_sales_add;
        }

        public void setF_sales_add(String f_sales_add) {
            this.f_sales_add = f_sales_add;
        }

        public String getF_email() {
            return f_email;
        }

        public void setF_email(String f_email) {
            this.f_email = f_email;
        }

        public String getF_contacts() {
            return f_contacts;
        }

        public void setF_contacts(String f_contacts) {
            this.f_contacts = f_contacts;
        }

        public String getF_pic_render() {
            return f_pic_render;
        }

        public void setF_pic_render(String f_pic_render) {
            this.f_pic_render = f_pic_render;
        }
    }
}
