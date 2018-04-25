package cn.com.larunda.safebox.util;


import android.content.res.Resources;
import android.support.design.widget.TabLayout;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

import cn.com.larunda.safebox.gson.AddPerson;
import cn.com.larunda.safebox.gson.AreaInfo;
import cn.com.larunda.safebox.gson.BindAreaInfo;
import cn.com.larunda.safebox.gson.BoxInfo;
import cn.com.larunda.safebox.gson.BoxMessage;
import cn.com.larunda.safebox.gson.Company;
import cn.com.larunda.safebox.gson.CompanyData;
import cn.com.larunda.safebox.gson.CompanyInfo;
import cn.com.larunda.safebox.gson.CompanyList;
import cn.com.larunda.safebox.gson.Config;
import cn.com.larunda.safebox.gson.CoordinateInfo;
import cn.com.larunda.safebox.gson.Department;
import cn.com.larunda.safebox.gson.DepartmentInfo;
import cn.com.larunda.safebox.gson.DestinationInfo;
import cn.com.larunda.safebox.gson.DetailedSoundInfo;
import cn.com.larunda.safebox.gson.DynamicPassword;
import cn.com.larunda.safebox.gson.EditUserInfo;
import cn.com.larunda.safebox.gson.EnclosureInfo;
import cn.com.larunda.safebox.gson.FingerprintInfo;
import cn.com.larunda.safebox.gson.Home;
import cn.com.larunda.safebox.gson.LocationInfo;
import cn.com.larunda.safebox.gson.MenuUserInfo;
import cn.com.larunda.safebox.gson.Message;
import cn.com.larunda.safebox.gson.NewHomeInfo;
import cn.com.larunda.safebox.gson.PhotoUrl;
import cn.com.larunda.safebox.gson.Result;
import cn.com.larunda.safebox.gson.SqInfo;
import cn.com.larunda.safebox.gson.SqLsInfo;
import cn.com.larunda.safebox.gson.TaskInfo;
import cn.com.larunda.safebox.gson.TotalLogInfo;
import cn.com.larunda.safebox.gson.UserInfo;
import cn.com.larunda.safebox.gson.UserToken;
import cn.com.larunda.safebox.gson.ValidateData;
import cn.com.larunda.safebox.gson.VersionInfo;
import cn.com.larunda.safebox.recycler.Task;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import java.lang.reflect.Field;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2018/1/8.
 */

public class Util {

    public static final String URL = "http://192.168.188.129:8082/api/";
    public static final String TOKEN = "?_token=";
    public static final String PATH = "http://192.168.188.129:8082/api/file/image?";
    public static String TYPE = "&request_type=app";

    public static Home handleHomeInfo(String response) {

        Gson gson = new Gson();
        Home home = gson.fromJson(response, Home.class);
        return home;
    }

    public static NewHomeInfo handleNewHomeInfo(String response) {

        Gson gson = new Gson();
        NewHomeInfo home = gson.fromJson(response, NewHomeInfo.class);
        return home;
    }

    public static UserToken handleLoginInfo(String response) {
        Gson gson = new Gson();
        UserToken userToken = gson.fromJson(response, UserToken.class);
        return userToken;
    }

    public static MenuUserInfo handleMenuUserInfo(String response) {
        Gson gson = new Gson();
        MenuUserInfo menuUserInfo = gson.fromJson(response, MenuUserInfo.class);
        return menuUserInfo;
    }


    public static SqInfo handleSqInfo(String response) {
        Gson gson = new Gson();
        SqInfo sqInfo = gson.fromJson(response, SqInfo.class);
        return sqInfo;
    }


    public static SqLsInfo handleSqLsInfo(String response) {
        Gson gson = new Gson();
        SqLsInfo sqLsInfo = gson.fromJson(response, SqLsInfo.class);
        return sqLsInfo;
    }


    public static TotalLogInfo handleTotalLogInfo(String response) {
        Gson gson = new Gson();
        TotalLogInfo totalLogInfo = gson.fromJson(response, TotalLogInfo.class);
        return totalLogInfo;

    }

    public static BoxMessage handleBoxMessage(String response) {
        Gson gson = new Gson();
        BoxMessage message = gson.fromJson(response, BoxMessage.class);
        return message;
    }

    public static UserInfo handleUserInfo(String response) {
        Gson gson = new Gson();
        UserInfo userInfo = gson.fromJson(response, UserInfo.class);
        return userInfo;
    }

    public static EditUserInfo handleEditUserInfo(String response) {
        Gson gson = new Gson();
        EditUserInfo userInfo = gson.fromJson(response, EditUserInfo.class);
        return userInfo;
    }

    public static Company handleCompany(String response) {
        Gson gson = new Gson();
        Company company = gson.fromJson(response, Company.class);
        return company;
    }

    public static Department handleDepartment(String response) {
        Gson gson = new Gson();
        Department department = gson.fromJson(response, Department.class);
        return department;
    }

    public static EnclosureInfo handleEnclosureInfo(String response) {
        Gson gson = new Gson();
        EnclosureInfo enclosureInfo = gson.fromJson(response, EnclosureInfo.class);
        return enclosureInfo;
    }

    public static CoordinateInfo handleCoordinateInfo(String response) {
        Gson gson = new Gson();
        CoordinateInfo coordinateInfo = gson.fromJson(response, CoordinateInfo.class);
        return coordinateInfo;
    }

    public static DetailedSoundInfo handleDetailedSoundInfo(String response) {
        Gson gson = new Gson();
        DetailedSoundInfo detailedSoundInfo = gson.fromJson(response, DetailedSoundInfo.class);
        return detailedSoundInfo;
    }

    public static DynamicPassword handleDynamicPassword(String response) {
        Gson gson = new Gson();
        DynamicPassword dynamicPassword = gson.fromJson(response, DynamicPassword.class);
        return dynamicPassword;
    }


    public static LocationInfo handleLocationInfo(String response) {
        Gson gson = new Gson();
        LocationInfo locationInfo = gson.fromJson(response, LocationInfo.class);
        return locationInfo;
    }

    public static BindAreaInfo handleBindAreaInfo(String response) {
        Gson gson = new Gson();
        BindAreaInfo bindAreaInfo = gson.fromJson(response, BindAreaInfo.class);
        return bindAreaInfo;
    }

    public static AreaInfo handleAreaInfo(String response) {
        Gson gson = new Gson();
        AreaInfo areaInfo = gson.fromJson(response, AreaInfo.class);
        return areaInfo;
    }

    public static DepartmentInfo handleDepartmentInfo(String response) {
        Gson gson = new Gson();
        DepartmentInfo departmentInfo = gson.fromJson(response, DepartmentInfo.class);
        return departmentInfo;
    }

    public static Config handleConfig(String response) {
        Gson gson = new Gson();
        Config config = gson.fromJson(response, Config.class);
        return config;
    }

    public static CompanyList handleCompanyList(String response) {
        Gson gson = new Gson();
        CompanyList companyList = gson.fromJson(response, CompanyList.class);
        return companyList;
    }

    public static AddPerson handleAddPerson(String response) {
        Gson gson = new Gson();
        AddPerson addPerson = gson.fromJson(response, AddPerson.class);
        return addPerson;
    }

    public static Result handleResult(String response) {
        Gson gson = new Gson();
        Result result = gson.fromJson(response, Result.class);
        return result;
    }

    public static Message handleMessage(String response) {
        Gson gson = new Gson();
        Message message = gson.fromJson(response, Message.class);
        return message;
    }

    public static BoxInfo handleBoxInfo(String response) {
        Gson gson = new Gson();
        BoxInfo boxInfo = gson.fromJson(response, BoxInfo.class);
        return boxInfo;

    }

    public static TaskInfo handleTaskInfo(String response) {
        Gson gson = new Gson();
        TaskInfo taskInfo = gson.fromJson(response, TaskInfo.class);
        return taskInfo;
    }

    public static String listToString(List<String> stringList) {
        if (stringList == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        boolean flag = false;
        for (String string : stringList) {
            if (flag) {
                result.append(",");
            } else {
                flag = true;
            }
            result.append(string);
        }
        return result.toString();
    }

    public static PhotoUrl handlePhotoUrl(String response) {
        Gson gson = new Gson();
        PhotoUrl photoUrl = gson.fromJson(response, PhotoUrl.class);
        return photoUrl;
    }

    public static ValidateData handleValidatedata(String response) {
        Gson gson = new Gson();
        ValidateData validateData = gson.fromJson(response, ValidateData.class);
        return validateData;
    }

    public static VersionInfo handleVersionInfo(String response) {
        Gson gson = new Gson();
        VersionInfo versionInfo = gson.fromJson(response, VersionInfo.class);
        return versionInfo;
    }

    public static FingerprintInfo handleFingerprintInfo(String response) {
        Gson gson = new Gson();
        FingerprintInfo fingerprintInfo = gson.fromJson(response, FingerprintInfo.class);
        return fingerprintInfo;
    }

    public static CompanyInfo handleCompanyInfo(String response) {
        Gson gson = new Gson();
        CompanyInfo info = gson.fromJson(response, CompanyInfo.class);
        return info;
    }

    public static CompanyData handleCompanyData(String response) {
        Gson gson = new Gson();
        CompanyData info = gson.fromJson(response, CompanyData.class);
        return info;
    }

    public static DestinationInfo handleDestinationInfo(String response) {
        Gson gson = new Gson();
        DestinationInfo info = gson.fromJson(response, DestinationInfo.class);
        return info;
    }

    /**
     * 判断是否是json对象
     *
     * @param json
     * @return
     */
    public static boolean isGoodJson(String json) {
        try {
            new JsonParser().parse(json);
            return true;
        } catch (JsonParseException e) {
            return false;
        }
    }

    /**
     * 设置tablayout下划线宽度的方法
     *
     * @param tabs
     * @param leftDip
     * @param rightDip
     */
    public static void setIndicator(TabLayout tabs, int leftDip, int rightDip) {
        Class<?> tabLayout = tabs.getClass();
        Field tabStrip = null;
        try {
            tabStrip = tabLayout.getDeclaredField("mTabStrip");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        tabStrip.setAccessible(true);
        LinearLayout llTab = null;
        try {
            llTab = (LinearLayout) tabStrip.get(tabs);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        int left = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, leftDip, Resources.getSystem().getDisplayMetrics());
        int right = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightDip, Resources.getSystem().getDisplayMetrics());

        for (int i = 0; i < llTab.getChildCount(); i++) {
            View child = llTab.getChildAt(i);
            child.setPadding(0, 0, 0, 0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
            params.leftMargin = left;
            params.rightMargin = right;
            child.setLayoutParams(params);
            child.invalidate();
        }


    }

    /**
     * 验证是否为手机号
     *
     * @param mobileNo
     * @return
     */
    public static boolean isValidMobileNo(String mobileNo) {
        // 1、(13[0-9])|(15[02789])|(18[679])|(17[0-9]) 13段 或者15段 18段17段的匹配
        // 2、\\d{8} 整数出现8次
        boolean flag = false;
        Pattern p = Pattern.compile("^((13[0-9])|(15[0-9])|(18[0-9])|(17[0-9]))\\d{8}$");
        Matcher match = p.matcher(mobileNo);
        if (mobileNo != null) {
            flag = match.matches();
        }
        return flag;
    }


    /**
     * 验证是否为正确的邮箱号
     *
     * @param email
     * @return
     */
    public static boolean isValidEmail(String email) {
        // 1、\\w+表示@之前至少要输入一个匹配字母或数字或下划线 \\w 单词字符：[a-zA-Z_0-9]
        // 2、(\\w+\\.)表示域名. 如新浪邮箱域名是sina.com.cn
        // {1,3}表示可以出现一次或两次或者三次.
        String reg = "\\w+@(\\w+\\.){1,3}\\w+";
        Pattern pattern = Pattern.compile(reg);
        boolean flag = false;
        if (email != null) {
            Matcher matcher = pattern.matcher(email);
            flag = matcher.matches();
        }
        return flag;
    }

}
