package cn.com.larunda.safebox.util;


import android.content.res.Resources;
import android.support.design.widget.TabLayout;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

import cn.com.larunda.safebox.gson.AddPerson;
import cn.com.larunda.safebox.gson.AreaInfo;
import cn.com.larunda.safebox.gson.BindAreaInfo;
import cn.com.larunda.safebox.gson.BoxAddUserInfo;
import cn.com.larunda.safebox.gson.BoxInfo;
import cn.com.larunda.safebox.gson.BoxInfoLogInfo;
import cn.com.larunda.safebox.gson.BoxMessage;
import cn.com.larunda.safebox.gson.Company;
import cn.com.larunda.safebox.gson.CompanyList;
import cn.com.larunda.safebox.gson.Config;
import cn.com.larunda.safebox.gson.CoordinateInfo;
import cn.com.larunda.safebox.gson.Department;
import cn.com.larunda.safebox.gson.DepartmentInfo;
import cn.com.larunda.safebox.gson.DetailedSoundInfo;
import cn.com.larunda.safebox.gson.DynamicPassword;
import cn.com.larunda.safebox.gson.EditUserInfo;
import cn.com.larunda.safebox.gson.EnclosureInfo;
import cn.com.larunda.safebox.gson.Home;
import cn.com.larunda.safebox.gson.LocationInfo;
import cn.com.larunda.safebox.gson.MenuUserInfo;
import cn.com.larunda.safebox.gson.Message;
import cn.com.larunda.safebox.gson.PhotoUrl;
import cn.com.larunda.safebox.gson.Result;
import cn.com.larunda.safebox.gson.SqInfo;
import cn.com.larunda.safebox.gson.SqLsInfo;
import cn.com.larunda.safebox.gson.TotalLogInfo;
import cn.com.larunda.safebox.gson.UserInfo;
import cn.com.larunda.safebox.gson.UserToken;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by Administrator on 2018/1/8.
 */

public class Util {

    public static final String URL = "http://safebox.dsmcase.com:90/api/";
    public static final String TOKEN = "?_token=";

    public static Home handleHomeInfo(String response) {

        Gson gson = new Gson();
        Home home = gson.fromJson(response, Home.class);
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

    public static BoxInfo handleBoxInfo(String response) {
        Gson gson = new Gson();
        BoxInfo boxInfo = gson.fromJson(response, BoxInfo.class);
        return boxInfo;

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

    public static BoxAddUserInfo handleBoxAddUserInfo(String response) {
        Gson gson = new Gson();
        BoxAddUserInfo boxAddUserInfo = gson.fromJson(response, BoxAddUserInfo.class);
        return boxAddUserInfo;
    }

    public static BoxInfoLogInfo handleBoxInfoLogInfo(String response) {
        Gson gson = new Gson();
        BoxInfoLogInfo boxInfoLogInfo = gson.fromJson(response, BoxInfoLogInfo.class);
        return boxInfoLogInfo;
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
    public static Result handleResult(String response){
        Gson gson = new Gson();
        Result result = gson.fromJson(response,Result.class);
        return result;
    }

    public static Message handleMessage(String response){
        Gson gson = new Gson();
        Message message = gson.fromJson(response,Message.class);
        return message;
    }

    public static String listToString(List<String> stringList){
        if(stringList==null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        boolean flag=false;
        for(String string : stringList) {
            if(flag) {
                result.append(",");
            }else{
                flag=true;
            }
            result.append(string);
        }
        return result.toString();
    }
    public static PhotoUrl handlePhotoUrl(String response){
        Gson gson = new Gson();
        PhotoUrl photoUrl = gson.fromJson(response,PhotoUrl.class);
        return photoUrl;
    }


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


}
