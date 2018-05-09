package cn.com.larunda.safebox.gson;

import java.util.List;

public class TaskWarningInfo {

    /**
     * current_page : 1
     * data : [{"rn":"1","id":366,"box_id":"366","f_type":"1","f_content":"编号为：3837393231365108003E001E的安全箱发生离位警报","created_at":"2000-01-01 00:00:24","updated_at":"2000-01-01 00:00:24","f_is_fixed":"1","process_id":"1","task_id":"1","process":{"id":1,"f_origin_city":"苏州","f_destination_city":"苏州"}},{"rn":"2","id":367,"box_id":"366","f_type":"1","f_content":"编号为：3837393231365108003E001E的安全箱发生离位警报","created_at":"2000-01-01 00:00:24","updated_at":"2000-01-01 00:00:24","f_is_fixed":"1","process_id":"1","task_id":"1","process":{"id":1,"f_origin_city":"苏州","f_destination_city":"苏州"}},{"rn":"3","id":381,"box_id":"366","f_type":"0","f_content":"编号为：3837393231365108003E001E的安全箱发生离位警报","created_at":"2000-01-01 00:00:24","updated_at":"2000-01-01 00:00:24","f_is_fixed":"1","process_id":"1","task_id":"1","process":{"id":1,"f_origin_city":"苏州","f_destination_city":"苏州"}},{"rn":"4","id":368,"box_id":"366","f_type":"1","f_content":"编号为：3837393231365108003E001E的安全箱发生离位警报","created_at":"2000-01-01 00:00:24","updated_at":"2000-01-01 00:00:24","f_is_fixed":"1","process_id":"1","task_id":"1","process":{"id":1,"f_origin_city":"苏州","f_destination_city":"苏州"}}]
     * first_page_url : http://192.168.188.129:8082/api/task/1/alarms?page=1
     * from : 1
     * last_page : 1
     * last_page_url : http://192.168.188.129:8082/api/task/1/alarms?page=1
     * next_page_url : null
     * path : http://192.168.188.129:8082/api/task/1/alarms
     * per_page : 15
     * prev_page_url : null
     * to : 4
     * total : 4
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
         * id : 366
         * box_id : 366
         * f_type : 1
         * f_content : 编号为：3837393231365108003E001E的安全箱发生离位警报
         * created_at : 2000-01-01 00:00:24
         * updated_at : 2000-01-01 00:00:24
         * f_is_fixed : 1
         * process_id : 1
         * task_id : 1
         * process : {"id":1,"f_origin_city":"苏州","f_destination_city":"苏州"}
         */

        private String rn;
        private int id;
        private String box_id;
        private int f_type;
        private String f_content;
        private String created_at;
        private String updated_at;
        private String f_is_fixed;
        private String process_id;
        private String task_id;
        private ProcessBean process;

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

        public int getF_type() {
            return f_type;
        }

        public void setF_type(int f_type) {
            this.f_type = f_type;
        }

        public String getF_content() {
            return f_content;
        }

        public void setF_content(String f_content) {
            this.f_content = f_content;
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

        public String getF_is_fixed() {
            return f_is_fixed;
        }

        public void setF_is_fixed(String f_is_fixed) {
            this.f_is_fixed = f_is_fixed;
        }

        public String getProcess_id() {
            return process_id;
        }

        public void setProcess_id(String process_id) {
            this.process_id = process_id;
        }

        public String getTask_id() {
            return task_id;
        }

        public void setTask_id(String task_id) {
            this.task_id = task_id;
        }

        public ProcessBean getProcess() {
            return process;
        }

        public void setProcess(ProcessBean process) {
            this.process = process;
        }

        public static class ProcessBean {
            /**
             * id : 1
             * f_origin_city : 苏州
             * f_destination_city : 苏州
             */

            private int id;
            private String[] f_origin_city;
            private String[] f_destination_city;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
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
        }
    }
}
