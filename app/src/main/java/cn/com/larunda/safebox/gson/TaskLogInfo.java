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
         * title : 任务添加目的地
         * info : 添加了新的目的地，从 浙江省 衢州市 江山市 到 浙江省 衢州市 江山市
         * process_name : {"origin":null,"destination":null}
         * time : 2018-04-26 16:50:18
         */

        private String title;
        private String info;
        private ProcessNameBean process_name;
        private String time;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public ProcessNameBean getProcess_name() {
            return process_name;
        }

        public void setProcess_name(ProcessNameBean process_name) {
            this.process_name = process_name;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public static class ProcessNameBean {
            /**
             * origin : null
             * destination : null
             */

            private Object origin;
            private Object destination;

            public Object getOrigin() {
                return origin;
            }

            public void setOrigin(Object origin) {
                this.origin = origin;
            }

            public Object getDestination() {
                return destination;
            }

            public void setDestination(Object destination) {
                this.destination = destination;
            }
        }
    }
}
