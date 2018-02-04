package cn.com.larunda.safebox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.larunda.safebox.R;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

import java.io.IOException;

import cn.com.larunda.safebox.gson.Config;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PersonalSettingActivity extends AppCompatActivity implements View.OnClickListener {

    private TitleBar titleBar;
    private Switch changeName;
    private Switch changePwd;
    private Switch changePhone;
    private Switch changeEmail;
    private Switch changeCompany;
    private Switch changeDepartment;
    private Switch changeFingerPrint;
    public static final String CONFIG_URL = Util.URL + "config" + Util.TOKEN;
    private SharedPreferences preferences;
    private String token;

    public SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout loodingErrorLayout;
    private ImageView loodingLayout;
    private LinearLayout layout;

    private Button postButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_setting);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        initView();
        initEvent();
        //每次fragment创建时还没有网络数据 设置载入背景为可见
        loodingLayout.setVisibility(View.VISIBLE);
        loodingErrorLayout.setVisibility(View.GONE);
        layout.setVisibility(View.GONE);
        sendRequest();
    }

    /**
     * 初始化点击事件
     */
    private void initEvent() {
        loodingErrorLayout.setOnClickListener(this);
    }

    /**
     * 发送网络请求
     */
    private void sendRequest() {
        HttpUtil.sendGetRequestWithHttp(CONFIG_URL + token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        loodingErrorLayout.setVisibility(View.VISIBLE);
                        loodingLayout.setVisibility(View.GONE);
                        layout.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String content = response.body().string();
                final Config config = Util.handleConfig(content);
                if (config != null && config.error == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initData(config);
                            swipeRefreshLayout.setRefreshing(false);
                            layout.setVisibility(View.VISIBLE);
                            loodingErrorLayout.setVisibility(View.GONE);
                            loodingLayout.setVisibility(View.GONE);
                        }
                    });

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(PersonalSettingActivity.this, LoginActivity.class);
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
     * @param config
     */
    private void initData(Config config) {
        if (config.user.change_user != null) {
            if (config.user.change_user.equals("1")) {
                changeName.setChecked(true);
            } else {
                changeName.setChecked(false);
            }
        } else {
            changeName.setChecked(false);
        }

        if (config.user.change_pwd != null) {
            if (config.user.change_pwd.equals("1")) {
                changePwd.setChecked(true);
            } else {
                changePwd.setChecked(false);
            }
        } else {
            changePwd.setChecked(false);
        }

        if (config.user.change_phone != null) {
            if (config.user.change_phone.equals("1")) {
                changePhone.setChecked(true);
            } else {
                changePhone.setChecked(false);
            }
        } else {
            changePhone.setChecked(false);
        }

        if (config.user.change_mail != null) {
            if (config.user.change_mail.equals("1")) {
                changeEmail.setChecked(true);
            } else {
                changeEmail.setChecked(false);
            }
        } else {
            changeEmail.setChecked(false);
        }

        if (config.user.change_company != null) {
            if (config.user.change_company.equals("1")) {
                changeCompany.setChecked(true);
            } else {
                changeCompany.setChecked(false);
            }
        } else {
            changeCompany.setChecked(false);
        }

        if (config.user.change_department != null) {
            if (config.user.change_department.equals("1")) {
                changeDepartment.setChecked(true);
            } else {
                changeDepartment.setChecked(false);
            }
        } else {
            changeDepartment.setChecked(false);
        }

        if (config.user.upload_fingerprint != null) {
            if (config.user.upload_fingerprint.equals("1")) {
                changeFingerPrint.setChecked(true);
            } else {
                changeFingerPrint.setChecked(false);
            }
        } else {
            changeFingerPrint.setChecked(false);
        }

    }

    /**
     * 初始化view
     */
    private void initView() {

        preferences = PreferenceManager.getDefaultSharedPreferences(PersonalSettingActivity.this);
        token = preferences.getString("token", null);

        changeName = findViewById(R.id.personal_setting_name);
        changePwd = findViewById(R.id.personal_setting_pwd);
        changePhone = findViewById(R.id.personal_setting_phone);
        changeEmail = findViewById(R.id.personal_setting_email);
        changeCompany = findViewById(R.id.personal_setting_company);
        changeDepartment = findViewById(R.id.personal_setting_department);
        changeFingerPrint = findViewById(R.id.personal_setting_fingerprint);

        loodingErrorLayout = findViewById(R.id.personal_setting_loading_error_layout);
        loodingLayout = findViewById(R.id.personal_setting_loading_layout);
        layout = findViewById(R.id.personal_setting_layout);
        swipeRefreshLayout = findViewById(R.id.personal_setting_swipe);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        swipeRefreshLayout.setEnabled(false);//设置swipe不可用

        postButton = findViewById(R.id.personal_setting_button);

        titleBar = findViewById(R.id.personal_setting_title_bar);
        titleBar.setTextViewText("设置");
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setRightButtonSrc(0);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);
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
    }

    /**
     * 点击事件监听
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.personal_setting_loading_error_layout:
                sendRequest();
                break;
            default:
                break;
        }
    }
}
