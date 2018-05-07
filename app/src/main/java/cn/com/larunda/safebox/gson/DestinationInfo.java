package cn.com.larunda.safebox.gson;

import java.util.List;

public class DestinationInfo {

    /**
     * id : 1
     * task_id : 1
     * f_destination : 苏州博览中心A座502
     * created_at : 2018-04-09 16:02:44
     * updated_at : 2018-04-11 14:09:43
     * completed_at : 2018-04-24 16:45:19
     * f_origin : 苏州火车站
     * f_release_time : 2018-04-20 18:23:33
     * f_upload_interval : 300
     * f_origin_city : 苏州
     * f_destination_city : 苏州
     * f_sort : 1
     * addressee : [{"id":682,"user_id":"355","process_id":"1","user":{"id":355,"f_user":"observe@lrd","f_name":"test","f_pic_render":"/api/file/image?"}},{"id":661,"user_id":"25","process_id":"1","user":{"id":25,"f_user":"observe@lrd","f_name":"observe","f_pic_render":"/api/file/image?"}}]
     */

    private int id;
    private String task_id;
    private String f_destination;
    private String created_at;
    private String updated_at;
    private String completed_at;
    private String f_origin;
    private String f_release_time;
    private String f_upload_interval;
    private String[] f_origin_city;
    private String[] f_destination_city;
    private String f_sort;
    private List<AddresseeBean> addressee;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getF_destination() {
        return f_destination;
    }

    public void setF_destination(String f_destination) {
        this.f_destination = f_destination;
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

    public String getCompleted_at() {
        return completed_at;
    }

    public void setCompleted_at(String completed_at) {
        this.completed_at = completed_at;
    }

    public String getF_origin() {
        return f_origin;
    }

    public void setF_origin(String f_origin) {
        this.f_origin = f_origin;
    }

    public String getF_release_time() {
        return f_release_time;
    }

    public void setF_release_time(String f_release_time) {
        this.f_release_time = f_release_time;
    }

    public String getF_upload_interval() {
        return f_upload_interval;
    }

    public void setF_upload_interval(String f_upload_interval) {
        this.f_upload_interval = f_upload_interval;
    }

    public String[] getF_origin_city() {
        return f_origin_city;
    }

    public void setF_origin_city(String[] f_origin_city) {
        this.f_origin_city = f_origin_city;
    }

    public String[] getF_destination_city() {
        return f_destination_city;
    }

    public void setF_destination_city(String[] f_destination_city) {
        this.f_destination_city = f_destination_city;
    }

    public String getF_sort() {
        return f_sort;
    }

    public void setF_sort(String f_sort) {
        this.f_sort = f_sort;
    }

    public List<AddresseeBean> getAddressee() {
        return addressee;
    }

    public void setAddressee(List<AddresseeBean> addressee) {
        this.addressee = addressee;
    }

    public static class AddresseeBean {
        /**
         * id : 682
         * user_id : 355
         * process_id : 1
         * user : {"id":355,"f_user":"observe@lrd","f_name":"test","f_pic_render":"/api/file/image?"}
         */

        private int id;
        private String user_id;
        private String process_id;
        private UserBean user;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getProcess_id() {
            return process_id;
        }

        public void setProcess_id(String process_id) {
            this.process_id = process_id;
        }

        public UserBean getUser() {
            return user;
        }

        public void setUser(UserBean user) {
            this.user = user;
        }

        public static class UserBean {
            /**
             * id : 355
             * f_user : observe@lrd
             * f_name : test
             * f_pic_render : /api/file/image?
             */

            private int id;
            private String f_user;
            private String f_name;
            private String f_pic_render;

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

            public String getF_pic_render() {
                return f_pic_render;
            }

            public void setF_pic_render(String f_pic_render) {
                this.f_pic_render = f_pic_render;
            }
        }
    }
}
