package cn.com.larunda.safebox.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sddt on 18-2-26.
 */

public class NewHomeInfo {
    public Info info;
    public Alarm alarm_num;
    public String error;

    public class Info {
        public int on_line;          // 在线
        public int exception;        // 通信异常
        public int to_used;          // 待使用
        public int shipping;         // 正在运送
        public int alarm;            // 正在报警
        public String company_name;     // 企业名称
        public String company_tel;      // 企业电话
        public String company_pic;      // 企业图片

    }

    public class Alarm {
        public int leaving_alarm;
        public int area_alarm;
        public int defence_alarm;
    }
}
