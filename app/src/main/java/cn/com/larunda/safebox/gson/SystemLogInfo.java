package cn.com.larunda.safebox.gson;

import java.util.List;

public class SystemLogInfo {
    private List<DataBean> data;
    private int current_page;
    private int last_page;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
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

    public class DataBean<T> {
        private int f_type;
        private String f_title;
        private String f_info;
        private String created_at;
        private User user;
        private Task task;
        private Box box;

        public Task getTask() {
            return task;
        }

        public void setTask(Task task) {
            this.task = task;
        }

        public Box getBox() {
            return box;
        }

        public void setBox(Box box) {
            this.box = box;
        }

        public int getF_type() {
            return f_type;
        }

        public void setF_type(int f_type) {
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

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public class User {
            private String id;
            private String f_user;
            private String f_name;
            private String f_pic_render;

            public String getId() {
                return id;
            }

            public void setId(String id) {
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

        public class Task {
            private String f_name;

            public String getF_name() {
                return f_name;
            }

            public void setF_name(String f_name) {
                this.f_name = f_name;
            }
        }

        public class Box {
            private String f_sn;
            private String f_alias;

            public String getF_sn() {
                return f_sn;
            }

            public void setF_sn(String f_sn) {
                this.f_sn = f_sn;
            }

            public String getF_alias() {
                return f_alias;
            }

            public void setF_alias(String f_alias) {
                this.f_alias = f_alias;
            }
        }
    }
}
