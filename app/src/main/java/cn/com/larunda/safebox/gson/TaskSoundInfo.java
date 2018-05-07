package cn.com.larunda.safebox.gson;

import java.util.List;

public class TaskSoundInfo {

    /**
     * current_page : 1
     * data : [{"rn":"1","id":401,"box_id":"547","f_path":"http://wwwsss","created_at":"1662-01-01 12:22:11","updated_at":"2018-05-07 13:42:03","f_length":"11","process_id":null,"task_id":"44"}]
     * first_page_url : http://192.168.188.129:8082/api/task/44/recordings?page=1
     * from : 1
     * last_page : 1
     * last_page_url : http://192.168.188.129:8082/api/task/44/recordings?page=1
     * next_page_url : null
     * path : http://192.168.188.129:8082/api/task/44/recordings
     * per_page : 15
     * prev_page_url : null
     * to : 1
     * total : 1
     */

    private int current_page;
    private String first_page_url;
    private int from;
    private int last_page;
    private String last_page_url;
    private Object next_page_url;
    private String path;
    private int per_page;
    private Object prev_page_url;
    private int to;
    private int total;
    private List<DataBean> data;

    public int getCurrent_page() {
        return current_page;
    }

    public void setCurrent_page(int current_page) {
        this.current_page = current_page;
    }

    public String getFirst_page_url() {
        return first_page_url;
    }

    public void setFirst_page_url(String first_page_url) {
        this.first_page_url = first_page_url;
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

    public String getLast_page_url() {
        return last_page_url;
    }

    public void setLast_page_url(String last_page_url) {
        this.last_page_url = last_page_url;
    }

    public Object getNext_page_url() {
        return next_page_url;
    }

    public void setNext_page_url(Object next_page_url) {
        this.next_page_url = next_page_url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getPer_page() {
        return per_page;
    }

    public void setPer_page(int per_page) {
        this.per_page = per_page;
    }

    public Object getPrev_page_url() {
        return prev_page_url;
    }

    public void setPrev_page_url(Object prev_page_url) {
        this.prev_page_url = prev_page_url;
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
         * id : 401
         * box_id : 547
         * f_path : http://wwwsss
         * created_at : 1662-01-01 12:22:11
         * updated_at : 2018-05-07 13:42:03
         * f_length : 11
         * process_id : null
         * task_id : 44
         */

        private String rn;
        private int id;
        private String box_id;
        private String f_path;
        private String created_at;
        private String updated_at;
        private String f_length;
        private String process_id;
        private String task_id;

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

        public String getF_path() {
            return f_path;
        }

        public void setF_path(String f_path) {
            this.f_path = f_path;
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

        public String getF_length() {
            return f_length;
        }

        public void setF_length(String f_length) {
            this.f_length = f_length;
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
    }
}
