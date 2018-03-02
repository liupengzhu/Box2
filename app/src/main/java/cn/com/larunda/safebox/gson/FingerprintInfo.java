package cn.com.larunda.safebox.gson;

import java.util.List;

/**
 * Created by sddt on 18-3-2.
 */

public class FingerprintInfo {

    private String code;
    private List<DataBean> data;
    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * finger_id : 1
         * time : 2018-01-22 16:08:08
         */

        private String finger_id;
        private String time;

        public String getFinger_id() {
            return finger_id;
        }

        public void setFinger_id(String finger_id) {
            this.finger_id = finger_id;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
