package cn.com.larunda.safebox;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.larunda.safebox.R;

import cn.com.larunda.safebox.adapter.UserInfoAdapter;
import cn.com.larunda.safebox.gson.UserData;
import cn.com.larunda.safebox.gson.UserInfo;
import cn.com.larunda.safebox.recycler.MyUserInfo;
import cn.com.larunda.safebox.util.HttpUtil;

import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String USER_INFO_URL = "http://safebox.dsmcase.com:90/api/user?_token=";
    public static final String IMG_URL = "http://safebox.dsmcase.com:90";
    TitleBar titleBar;

    List<MyUserInfo> myUserInfoList = new ArrayList<>();
    LinearLayoutManager manager;
    RecyclerView recyclerView;
    UserInfoAdapter adapter;
    SwipeRefreshLayout refreshLayout;

    private RelativeLayout loodingErrorLayout;
    private ImageView loodingLayout;


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
        initView();
        initEvent();

        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sendRequest();

            }
        });

        //每次fragment创建时还没有网络数据 设置载入背景为可见
        loodingLayout.setVisibility(View.VISIBLE);


    }

    @Override
    protected void onStart() {
        super.onStart();
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
     * 发送网络请求
     */
    private void sendRequest() {
        HttpUtil.sendGetRequestWithHttp(USER_INFO_URL + MainActivity.token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                        loodingErrorLayout.setVisibility(View.VISIBLE);
                        loodingLayout.setVisibility(View.INVISIBLE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final UserInfo userInfo = Util.handleUserInfo(response.body().string());
                if (userInfo != null && userInfo.error == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initUserInfo(userInfo);
                            refreshLayout.setRefreshing(false);
                            loodingErrorLayout.setVisibility(View.INVISIBLE);
                            loodingLayout.setVisibility(View.INVISIBLE);
                        }
                    });

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(UserInfoActivity.this, LoginActivity.class);
                            intent.putExtra("token_timeout", "登录超时");
                            MainActivity.preferences.edit().putString("token", null).commit();
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }
        });


    }

    /**
     * 解析用户数据
     *
     * @param userInfo
     */
    private void initUserInfo(UserInfo userInfo) {
        myUserInfoList.clear();
        if (userInfo.userData != null) {
            for (UserData userData : userInfo.userData) {
                MyUserInfo myUserInfo = new MyUserInfo();
                String imgUrl = null;
                if (userData.pic != null) {
                    imgUrl = userData.pic.replace('\\', ' ');
                    myUserInfo.setUserImg(IMG_URL + imgUrl);
                } else {
                    myUserInfo.setUserImg(null);
                }
                if (userData.id != null) {
                    myUserInfo.setUserId(userData.id);
                }
                if (userData.level != null) {
                    if (userData.level.equals("1")) {
                        myUserInfo.setUserQx("管理员");
                    } else {
                        myUserInfo.setUserQx("一般用户");
                    }
                }
                if (userData.name != null) {
                    myUserInfo.setUserName(userData.name);
                }
                if (userData.user != null) {
                    myUserInfo.setUser(userData.user);
                }
                myUserInfoList.add(myUserInfo);
            }
        }
        adapter.notifyDataSetChanged();
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
        adapter = new UserInfoAdapter(myUserInfoList);

        refreshLayout = findViewById(R.id.user_info_swiper);
        loodingErrorLayout = findViewById(R.id.user_info_loading_error_layout);
        loodingLayout = findViewById(R.id.user_info_loading_layout);

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
