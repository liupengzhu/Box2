package com.example.box;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.box.adapter.UserInfoAdapter;
import com.example.box.recycler.UserInfo;
import com.example.box.util.HttpUtil;
import com.example.titlebar.TitleBar;
import com.example.titlebar.TitleListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String USER_INFO_URI = "http://safebox.dsmcase.com:90/api/user/lists?_token=";

    TitleBar titleBar;

    List<UserInfo> userInfoList = new ArrayList<>();
    LinearLayoutManager manager;
    RecyclerView recyclerView;
    UserInfoAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);


        }
        initData();
        initView();
        initEvent();

        sendRequest();
    }

    /**
     * 初始化点击事件
     */
    private void initEvent() {
        adapter.setUserInfoOnClickListener(new UserInfoAdapter.UserInfoOnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserInfoActivity.this, EditUserActivity.class);
                startActivity(intent);
            }
        });
        titleBar.setOnClickListener(new TitleListener() {
            @Override
            public void onLeftButtonClickListener(View v) {


            }

            @Override
            public void onLeftBackButtonClickListener(View v) {
                finish();
            }

            @Override
            public void onRightButtonClickListener(View v) {

                Intent intent = new Intent(UserInfoActivity.this, AddUserActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 测试方法
     */
    private void initData() {
        for (int i = 0; i < 20; i++) {
            UserInfo userInfo = new UserInfo("张三" + i, "zq024502" + i, "一般用户", null);
            userInfoList.add(userInfo);
        }
    }

    /**
     * 发送网络请求
     */
    private void sendRequest() {
        HttpUtil.sendGetRequestWithHttp(USER_INFO_URI + MainActivity.token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });


    }

    /**
     * 初始化View
     */
    private void initView() {

        titleBar = findViewById(R.id.user_info_title_bar);
        titleBar.setTextViewText("用户管理");
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);

        manager = new LinearLayoutManager(this);
        adapter = new UserInfoAdapter(userInfoList);

        swipeRefreshLayout = findViewById(R.id.user_info_swiper);
        recyclerView = findViewById(R.id.user_info_recycler);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

    }

    /**
     * 点击事件监听
     *
     * @param v
     */
    @Override
    public void onClick(View v) {

    }
}
