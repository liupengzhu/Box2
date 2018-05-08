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
     * processes : [{"id":51,"task_id":"44","f_destination":"beijing","created_at":"2018-05-02 14:12:00","updated_at":"2018-05-08 15:33:05","completed_at":null,"f_origin":"suzhou","f_release_time":"2018-10-10 00:00:00","f_upload_interval":"30","f_origin_city":["jiangsu","suzhou","xiangchen"],"f_destination_city":["wuxi"],"f_sort":"1","fence_id":null,"f_use_dislocation":"0","f_use_defense":"0","addressee":[]},{"id":50,"task_id":"44","f_destination":"哈哈哈","created_at":"2018-05-02 14:09:34","updated_at":"2018-05-08 15:33:05","completed_at":"2018-05-04 15:50:54","f_origin":"哇哈哈","f_release_time":"2018-05-02 14:09:22","f_upload_interval":"300","f_origin_city":["天津市","市辖区","和平区"],"f_destination_city":["北京市","市辖区","东城区"],"f_sort":"0","fence_id":"442","f_use_dislocation":"0","f_use_defense":"0","addressee":[{"id":708,"user_id":"442","process_id":"50","created_at":"2018-05-02 14:09:34","updated_at":"2018-05-02 14:09:34","task_id":"44","f_is_dynamic":"1","f_is_password":1,"user":{"id":442,"f_user":"lijun@lrd","f_name":"李俊","f_tel":"18952554298","f_email":"123456@qq.com","f_level":"5","created_at":"2018-05-02 09:34:07","updated_at":"2018-05-02 09:34:52","company_id":"22","f_pic":"avatars/JKgHLRVAecsEShb6bvvlsH1qImfdYQNWALVXUez8.jpeg","f_pic_render":"/api/file/image?avatars/JKgHLRVAecsEShb6bvvlsH1qImfdYQNWALVXUez8.jpeg"}}]}]
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
         * updated_at : 2018-05-08 15:33:05
         * completed_at : null
         * f_origin : suzhou
         * f_release_time : 2018-10-10 00:00:00
         * f_upload_interval : 30
         * f_origin_city : ["jiangsu","suzhou","xiangchen"]
         * f_destination_city : ["wuxi"]
         * f_sort : 1
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
        private List<AddresseeBean> addressee;

        public class AddresseeBean{

            /**
             * id : 708
             * user_id : 442
             * process_id : 50
             * created_at : 2018-05-02 14:09:34
             * updated_at : 2018-05-02 14:09:34
             * task_id : 44
             * f_is_dynamic : 1
             * f_is_password : 1
             * user : {"id":442,"f_user":"lijun@lrd","f_name":"李俊","f_tel":"18952554298","f_email":"123456@qq.com","f_level":"5","created_at":"2018-05-02 09:34:07","updated_at":"2018-05-02 09:34:52","company_id":"22","f_pic":"avatars/JKgHLRVAecsEShb6bvvlsH1qImfdYQNWALVXUez8.jpeg","f_pic_render":"/api/file/image?avatars/JKgHLRVAecsEShb6bvvlsH1qImfdYQNWALVXUez8.jpeg"}
             */

            private int id;
            private String user_id;
            private String process_id;
            private String created_at;
            private String updated_at;
            private String task_id;
            private String f_is_dynamic;
            private int f_is_password;
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

            public int getF_is_password() {
                return f_is_password;
            }

            public void setF_is_password(int f_is_password) {
                this.f_is_password = f_is_password;
            }

            public UserBean getUser() {
                return user;
            }

            public void setUser(UserBean user) {
                this.user = user;
            }

            public  class UserBean {
                /**
                 * id : 442
                 * f_user : lijun@lrd
                 * f_name : 李俊
                 * f_tel : 18952554298
                 * f_email : 123456@qq.com
                 * f_level : 5
                 * created_at : 2018-05-02 09:34:07
                 * updated_at : 2018-05-02 09:34:52
                 * company_id : 22
                 * f_pic : avatars/JKgHLRVAecsEShb6bvvlsH1qImfdYQNWALVXUez8.jpeg
                 * f_pic_render : /api/file/image?avatars/JKgHLRVAecsEShb6bvvlsH1qImfdYQNWALVXUez8.jpeg
                 */

                private int id;
                private String f_user;
                private String f_name;
                private String f_tel;
                private String f_email;
                private String f_level;
                private String created_at;
                private String updated_at;
                private String company_id;
                private String f_pic;
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

                public String getF_tel() {
                    return f_tel;
                }

                public void setF_tel(String f_tel) {
                    this.f_tel = f_tel;
                }

                public String getF_email() {
                    return f_email;
                }

                public void setF_email(String f_email) {
                    this.f_email = f_email;
                }

                public String getF_level() {
                    return f_level;
                }

                public void setF_level(String f_level) {
                    this.f_level = f_level;
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

                public String getF_pic() {
                    return f_pic;
                }

                public void setF_pic(String f_pic) {
                    this.f_pic = f_pic;
                }

                public String getF_pic_render() {
                    return f_pic_render;
                }

                public void setF_pic_render(String f_pic_render) {
                    this.f_pic_render = f_pic_render;
                }
            }
        }
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

        public List<AddresseeBean> getAddressee() {
            return addressee;
        }

        public void setAddressee(List<AddresseeBean> addressee) {
            this.addressee = addressee;
        }
    }
}
