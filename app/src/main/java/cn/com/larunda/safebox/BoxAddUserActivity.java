package cn.com.larunda.safebox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.larunda.safebox.R;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.com.larunda.safebox.adapter.BoxAddUserAdapter;
import cn.com.larunda.safebox.gson.BoxAddUserData;
import cn.com.larunda.safebox.gson.BoxAddUserInfo;
import cn.com.larunda.safebox.recycler.BoxAddUser;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class BoxAddUserActivity extends AppCompatActivity implements View.OnClickListener {

    private TitleBar titleBar;
    private Button addButton;

    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private BoxAddUserAdapter adapter;
    private List<BoxAddUser> boxAddUserList = new ArrayList<>();
    private String id;
    private String BIND_USER_URL = "http://safebox.dsmcase.com:90/api/box/bind_user_lists?_token=";
    private String IMG_URL = "http://safebox.dsmcase.com:90";
    private SharedPreferences preferences;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box_add_user);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        id = getIntent().getStringExtra("id");
        initView();
        initEvent();

        sendRequest();
    }

    /**
     * 发送网络请求
     */
    private void sendRequest() {
        HttpUtil.sendGetRequestWithHttp(BIND_USER_URL + token + "&id=" + id, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String content = response.body().string();
                final BoxAddUserInfo boxAddUserInfo = Util.handleBoxAddUserInfo(content);
                if (boxAddUserInfo != null && boxAddUserInfo.error == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initData(boxAddUserInfo);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(BoxAddUserActivity.this, LoginActivity.class);
                            intent.putExtra("token_timeout", "登录超时");
                            preferences.edit().putString("token", null).commit();
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }
        });
    }

    /**
     * 解析数据
     *
     * @param boxAddUserInfo
     */
    private void initData(BoxAddUserInfo boxAddUserInfo) {
        boxAddUserList.clear();
        if (boxAddUserInfo.dataList != null) {
            for (BoxAddUserData boxAddUserData : boxAddUserInfo.dataList) {
                BoxAddUser boxAddUser = new BoxAddUser();
                String imgUrl = null;
                if (boxAddUserData.f_pic != null) {
                    imgUrl = boxAddUserData.f_pic.replace('\\', ' ');
                    boxAddUser.setPic(IMG_URL + imgUrl);
                } else {
                    boxAddUser.setPic(null);
                }

                if (boxAddUserData.company != null) {
                    boxAddUser.setCompany(boxAddUserData.company);
                } else {
                    boxAddUser.setCompany("");
                }
                if (boxAddUserData.department != null) {
                    boxAddUser.setDepartment(boxAddUserData.department);
                } else {
                    boxAddUser.setDepartment("");
                }
                if (boxAddUserData.f_name != null) {
                    boxAddUser.setName(boxAddUserData.f_name);
                } else {
                    boxAddUser.setName("");
                }
                if (boxAddUserData.f_tel != null) {
                    boxAddUser.setPhone(boxAddUserData.f_tel);
                } else {
                    boxAddUser.setPhone("");
                }
                boxAddUserList.add(boxAddUser);
            }
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 初始化点击事件
     */
    private void initEvent() {
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

            }
        });
        addButton.setOnClickListener(this);
    }


    /**
     * 初始化View
     */
    private void initView() {

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = preferences.getString("token", null);

        titleBar = findViewById(R.id.box_add_user_title_bar);
        titleBar.setTextViewText("");
        titleBar.setRightButtonSrc(0);
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);

        addButton = findViewById(R.id.box_add_user_add_button);

        recyclerView = findViewById(R.id.box_add_user_recycler);
        manager = new LinearLayoutManager(this);
        adapter = new BoxAddUserAdapter(this, boxAddUserList);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

    }

    /**
     * 点击事件监听
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.box_add_user_add_button:
                Intent bindingUserIntent = new Intent(this, BindingUserActivity.class);
                startActivity(bindingUserIntent);
                break;
            default:
                break;

        }

    }
}
