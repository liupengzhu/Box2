package cn.com.larunda.safebox.gson;

import java.util.List;

public class FingerInfo {
    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public class DataBean {
        private int id;
        private String f_fingerprint;
        private User user;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getF_fingerprint() {
            return f_fingerprint;
        }

        public void setF_fingerprint(String f_fingerprint) {
            this.f_fingerprint = f_fingerprint;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public class User {
            private String f_name;

            public String getF_name() {
                return f_name;
            }

            public void setF_name(String f_name) {
                this.f_name = f_name;
            }
        }
    }
}
