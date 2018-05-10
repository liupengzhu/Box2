package cn.com.larunda.safebox.gson;

import java.util.List;

/**
 * Created by sddt on 18-3-2.
 */

public class VersionInfo {

    /**
     * version : V1.0
     * url : 123
     * updated_list : {"add":["增加了指纹识别功能"],"fix":["修复了部分页面按钮点击事件无效的问题"]}
     */

    private String version_number;
    private String url;
    private UpdatedListBean updated_list;
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getVersion() {
        return version_number;
    }

    public void setVersion(String version) {
        this.version_number = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public UpdatedListBean getUpdated_list() {
        return updated_list;
    }

    public void setUpdated_list(UpdatedListBean updated_list) {
        this.updated_list = updated_list;
    }

    public static class UpdatedListBean {
        private List<String> add;
        private List<String> fix;

        public List<String> getAdd() {
            return add;
        }

        public void setAdd(List<String> add) {
            this.add = add;
        }

        public List<String> getFix() {
            return fix;
        }

        public void setFix(List<String> fix) {
            this.fix = fix;
        }
    }
}
