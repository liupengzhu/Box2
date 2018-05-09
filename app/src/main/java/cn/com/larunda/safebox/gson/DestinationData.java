package cn.com.larunda.safebox.gson;

import java.util.List;

public class DestinationData {

    /**
     * id : 103
     * task_id : 44
     * f_destination : 44777
     * created_at : 2018-05-09 10:48:18
     * updated_at : 2018-05-09 14:27:42
     * completed_at : null
     * f_origin : 4444
     * f_release_time : 2018-05-09 10:47:00
     * f_upload_interval : 1
     * f_origin_city : ["湖北省","武汉市","东西湖区"]
     * f_destination_city : ["湖北省","武汉市","江夏区"]
     * f_sort : 1
     * fence_id : 434
     * f_use_dislocation : null
     * f_use_defense : null
     * addressee : [{"id":759,"user_id":"444","process_id":"103","created_at":"2018-05-09 10:48:18","updated_at":"2018-05-09 10:48:18","task_id":"44","f_is_dynamic":"0","f_is_password":1}]
     */

    private int id;
    private String task_id;
    private String f_destination;
    private String created_at;
    private String updated_at;
    private Object completed_at;
    private String f_origin;
    private String f_release_time;
    private String f_upload_interval;
    private String f_sort;
    private String fence_id;
    private Object f_use_dislocation;
    private Object f_use_defense;
    private List<String> f_origin_city;
    private List<String> f_destination_city;
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

    public Object getCompleted_at() {
        return completed_at;
    }

    public void setCompleted_at(Object completed_at) {
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

    public String getF_sort() {
        return f_sort;
    }

    public void setF_sort(String f_sort) {
        this.f_sort = f_sort;
    }

    public String getFence_id() {
        return fence_id;
    }

    public void setFence_id(String fence_id) {
        this.fence_id = fence_id;
    }

    public Object getF_use_dislocation() {
        return f_use_dislocation;
    }

    public void setF_use_dislocation(Object f_use_dislocation) {
        this.f_use_dislocation = f_use_dislocation;
    }

    public Object getF_use_defense() {
        return f_use_defense;
    }

    public void setF_use_defense(Object f_use_defense) {
        this.f_use_defense = f_use_defense;
    }

    public List<String> getF_origin_city() {
        return f_origin_city;
    }

    public void setF_origin_city(List<String> f_origin_city) {
        this.f_origin_city = f_origin_city;
    }

    public List<String> getF_destination_city() {
        return f_destination_city;
    }

    public void setF_destination_city(List<String> f_destination_city) {
        this.f_destination_city = f_destination_city;
    }

    public List<AddresseeBean> getAddressee() {
        return addressee;
    }

    public void setAddressee(List<AddresseeBean> addressee) {
        this.addressee = addressee;
    }

    public static class AddresseeBean {
        /**
         * id : 759
         * user_id : 444
         * process_id : 103
         * created_at : 2018-05-09 10:48:18
         * updated_at : 2018-05-09 10:48:18
         * task_id : 44
         * f_is_dynamic : 0
         * f_is_password : 1
         */

        private int id;
        private String user_id;
        private String process_id;
        private String created_at;
        private String updated_at;
        private String task_id;
        private String f_is_dynamic;
        private String f_is_password;

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

        public String getTask_id() {
            return task_id;
        }

        public void setTask_id(String task_id) {
            this.task_id = task_id;
        }

        public String getF_is_dynamic() {
            return f_is_dynamic;
        }

        public void setF_is_dynamic(String f_is_dynamic) {
            this.f_is_dynamic = f_is_dynamic;
        }

        public String getF_is_password() {
            return f_is_password;
        }

        public void setF_is_password(String f_is_password) {
            this.f_is_password = f_is_password;
        }
    }
}
