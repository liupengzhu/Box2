package cn.com.larunda.safebox.gson;

import java.util.List;

public class CourierInfo {

    /**
     * id : 44
     * box_id : 547
     * f_name : test
     * created_at : 2018-04-27 15:10:59
     * updated_at : 2018-05-03 17:49:59
     * company_id : 22
     * f_origin_city : 天津市
     * f_destination_city : 北京市
     * completed_at : null
     * f_upload_interval : null
     * f_use_dislocation : null
     * f_use_defense : null
     * processes : [{"id":51,"task_id":"44","f_destination":"beijing","created_at":"2018-05-02 14:12:00","updated_at":"2018-05-07 10:09:32","completed_at":null,"f_origin":"suzhou","f_release_time":"2018-10-10 00:00:00","f_upload_interval":"30","f_origin_city":["jiangsu","suzhou","xiangchen"],"f_destination_city":["wuxi"],"f_sort":"0","fence_id":null,"f_use_dislocation":"0","f_use_defense":"0","addressee":[]},{"id":50,"task_id":"44","f_destination":"哈哈哈","created_at":"2018-05-02 14:09:34","updated_at":"2018-05-07 10:09:32","completed_at":"2018-05-04 15:50:54","f_origin":"哇哈哈","f_release_time":"2018-05-02 14:09:22","f_upload_interval":"300","f_origin_city":["天津市","市辖区","和平区"],"f_destination_city":["北京市","市辖区","东城区"],"f_sort":"3","fence_id":"442","f_use_dislocation":"0","f_use_defense":"0","addressee":[{"id":708,"user_id":"442","process_id":"50","created_at":"2018-05-02 14:09:34","updated_at":"2018-05-02 14:09:34","task_id":"44","f_is_dynamic":"1","f_is_password":1}]},{"id":71,"task_id":"44","f_destination":"瞎胡闹","created_at":"2018-05-03 18:02:03","updated_at":"2018-05-07 10:09:32","completed_at":null,"f_origin":"不知道","f_release_time":"2018-05-03 18:01:50","f_upload_interval":"300","f_origin_city":["山西省","太原市","迎泽区"],"f_destination_city":["北京市","市辖区","西城区"],"f_sort":"2","fence_id":"422","f_use_dislocation":"0","f_use_defense":"0","addressee":[{"id":730,"user_id":"442","process_id":"71","created_at":"2018-05-03 18:02:03","updated_at":"2018-05-03 18:02:03","task_id":"44","f_is_dynamic":"0","f_is_password":1},{"id":731,"user_id":"429","process_id":"71","created_at":"2018-05-03 18:02:03","updated_at":"2018-05-03 18:02:03","task_id":"44","f_is_dynamic":"1","f_is_password":0}]},{"id":49,"task_id":"44","f_destination":"我就要看你","created_at":"2018-05-02 14:07:52","updated_at":"2018-05-07 10:09:32","completed_at":null,"f_origin":"你不要看我","f_release_time":"2018-05-02 14:07:38","f_upload_interval":"300","f_origin_city":["北京市","市辖区","东城区"],"f_destination_city":["北京市","市辖区","西城区"],"f_sort":"1","fence_id":"422","f_use_dislocation":"0","f_use_defense":"0","addressee":[{"id":707,"user_id":"442","process_id":"49","created_at":"2018-05-02 14:07:53","updated_at":"2018-05-02 17:03:34","task_id":"44","f_is_dynamic":"1","f_is_password":1},{"id":728,"user_id":"429","process_id":"49","created_at":"2018-05-03 08:25:09","updated_at":"2018-05-03 08:25:09","task_id":"44","f_is_dynamic":"1","f_is_password":0}]}]
     */

    private int id;
    private String box_id;
    private String f_name;
    private String created_at;
    private String updated_at;
    private String company_id;
    private String f_origin_city;
    private String f_destination_city;
    private String completed_at;
    private String f_upload_interval;
    private String f_use_dislocation;
    private String f_use_defense;
    private List<ProcessesBean> processes;

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

    public String getF_upload_interval() {
        return f_upload_interval;
    }

    public void setF_upload_interval(String f_upload_interval) {
        this.f_upload_interval = f_upload_interval;
    }

    public String getF_use_dislocation() {
        return f_use_dislocation;
    }

    public void setF_use_dislocation(String f_use_dislocation) {
        this.f_use_dislocation = f_use_dislocation;
    }

    public String getF_use_defense() {
        return f_use_defense;
    }

    public void setF_use_defense(String f_use_defense) {
        this.f_use_defense = f_use_defense;
    }

    public List<ProcessesBean> getProcesses() {
        return processes;
    }

    public void setProcesses(List<ProcessesBean> processes) {
        this.processes = processes;
    }

    public static class ProcessesBean {
        /**
         * id : 51
         * task_id : 44
         * f_destination : beijing
         * created_at : 2018-05-02 14:12:00
         * updated_at : 2018-05-07 10:09:32
         * completed_at : null
         * f_origin : suzhou
         * f_release_time : 2018-10-10 00:00:00
         * f_upload_interval : 30
         * f_origin_city : ["jiangsu","suzhou","xiangchen"]
         * f_destination_city : ["wuxi"]
         * f_sort : 0
         * fence_id : null
         * f_use_dislocation : 0
         * f_use_defense : 0
         * addressee : []
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
        private String f_sort;
        private String fence_id;
        private String f_use_dislocation;
        private String f_use_defense;
        private List<String> f_origin_city;
        private List<String> f_destination_city;
        private List<?> addressee;

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

        public String getF_use_dislocation() {
            return f_use_dislocation;
        }

        public void setF_use_dislocation(String f_use_dislocation) {
            this.f_use_dislocation = f_use_dislocation;
        }

        public String getF_use_defense() {
            return f_use_defense;
        }

        public void setF_use_defense(String f_use_defense) {
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

        public List<?> getAddressee() {
            return addressee;
        }

        public void setAddressee(List<?> addressee) {
            this.addressee = addressee;
        }
    }
}
