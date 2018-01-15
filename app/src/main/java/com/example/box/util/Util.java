package com.example.box.util;


import android.content.res.Resources;
import android.support.design.widget.TabLayout;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

import com.example.box.gson.BoxInfo;
import com.example.box.gson.Home;
import com.example.box.gson.HomeAlarm;
import com.example.box.gson.HomeInfo;
import com.example.box.gson.MenuUserInfo;
import com.example.box.gson.SqInfo;
import com.example.box.gson.UserToken;
import com.google.gson.Gson;

import java.lang.reflect.Field;

/**
 * Created by Administrator on 2018/1/8.
 */

public class Util {

    public static Home handleHomeInfo(String response){

        Gson gson = new Gson();
        Home home = gson.fromJson(response,Home.class);
        return home;
    }
    public static UserToken handleLoginInfo(String response){
        Gson gson = new Gson();
        UserToken userToken = gson.fromJson(response,UserToken.class);
        return userToken;
    }
    public static MenuUserInfo handleMenuUserInfo(String response){
        Gson gson = new Gson();
        MenuUserInfo menuUserInfo = gson.fromJson(response,MenuUserInfo.class);
        return menuUserInfo;
    }

    public static BoxInfo handleBoxInfo(String response){
        Gson gson = new Gson();
        BoxInfo boxInfo = gson.fromJson(response,BoxInfo.class);
        return boxInfo;

    }

    public static SqInfo handleSqInfo(String response){
        Gson gson = new Gson();
        SqInfo sqInfo = gson.fromJson(response,SqInfo.class);
        return sqInfo;
    }


    /**
     * 设置tablayout下划线宽度的方法
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
