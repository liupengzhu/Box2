package cn.com.larunda.safebox.gson;

import java.util.List;

public class TaskLogInfo {

    /**
     * current_page : 1
     * data : [{"title":"任务添加目的地","info":"添加了新的目的地，从 浙江省 衢州市 江山市 到 浙江省 衢州市 江山市","process_name":{"origin":null,"destination":null},"time":"2018-04-26 16:50:18"},{"title":"正常上线","info":"编号为：3837393231365108003E001E的安全箱正常上线","process_name":{"origin":"苏州火车站","destination":null},"time":"2018-04-26 16:18:54"},{"title":"正常上线","info":"编号为：3837393231365108003E001E的安全箱正常上线","process_name":{"origin":"苏州火车站","destination":null},"time":"2018-04-26 15:51:28"},{"title":"正常上线","info":"编号为：3837393231365108003E001E的安全箱正常上线","process_name":{"origin":"苏州火车站","destination":null},"time":"2018-04-26 15:16:11"},{"title":"正常上线","info":"编号为：3837393231365108003E001E的安全箱正常上线","process_name":{"origin":"苏州火车站","destination":null},"time":"2018-04-26 15:16:04"},{"title":"任务添加目的地","info":"添加了新的目的地，从 北京市 北京市 东城区 到 北京市 北京市 东城区","process_name":{"origin":null,"destination":null},"time":"2018-04-26 13:55:23"},{"title":"任务添加目的地","info":"添加了新的目的地，从 北京市 北京市 东城区 到 北京市 北京市 东城区","process_name":{"origin":null,"destination":null},"time":"2018-04-26 13:50:39"},{"title":"任务添加目的地","info":"添加了新的目的地，从 北京市 北京市 东城区 到 北京市 北京市 东城区","process_name":{"origin":null,"destination":null},"time":"2018-04-26 13:50:25"},{"title":"正常上线","info":"编号为：3837393231365108003E001E的安全箱正常上线","process_name":{"origin":"苏州火车站","destination":null},"time":"2018-04-26 08:51:03"},{"title":"正常上线","info":"编号为：3837393231365108003E001E的安全箱正常上线","process_name":{"origin":"苏州火车站","destination":null},"time":"2018-04-26 08:26:27"}]
     * first_page_url : http://192.168.188.129:8082/api/box/log?page=1
     * from : 1
     * last_page : 47
     * last_page_url : http://192.168.188.129:8082/api/box/log?page=47
     * next_page_url : http://192.168.188.129:8082/api/box/log?page=2
     * path : http://192.168.188.129:8082/api/box/log
     * per_page : 10
     * prev_page_url : null
     * to : 10
     * total : 461
     */

    private int current_page;
    private int last_page;


    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * rn : 1
         * id : 8214
         * user_id : 1
         * box_id : 366
         * f_type : 0
         * f_title : 绑定区域
         * f_info : 管理员在2018-04-13 17:10:48将递送箱：3837393231365108003E001E与地理围栏：常州市进行了绑定
         * created_at : 2018-04-13 17:10:48
         * updated_at : 2018-04-13 17:10:48
         * process_id : 1
         * task_id : 1
         * process : {"id":1,"f_origin_city":"苏州","f_destination_city":"苏州"}
         */

        private String rn;
        private int id;
        private String user_id;
        private String box_id;
        private String f_type;
        private String f_title;
        private String f_info;
        private String created_at;
        private String updated_at;
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

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getBox_id() {
            return box_id;
        }

        public void setBox_id(String box_id) {
            this.box_id = box_id;
        }

        public String getF_type() {
            return f_type;
        }

        public void setF_type(String f_type) {
            this.f_type = f_type;
        }

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

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
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
}
