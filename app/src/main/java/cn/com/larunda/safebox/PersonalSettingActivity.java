package cn.com.larunda.safebox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.android.tu.loadingdialog.LoadingDailog;
import com.larunda.safebox.R;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;

import cn.com.larunda.safebox.gson.Config;
import cn.com.larunda.safebox.util.ActivityCollector;
import cn.com.larunda.safebox.util.BaseActivity;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PersonalSettingActivity extends BaseActivity implements View.OnClickListener {

    private TitleBar titleBar;
    private Switch changeName;
    private Switch changePwd;
    private Switch changePic;
    private Switch changePhone;
    private Switch changeEmail;
    private Switch changeFingerprint;
    public static final String CONFIG_URL = Util.URL + "config" + Util.TOKEN;
    private SharedPreferences preferences;
    private String token;

    public SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout loadingErrorLayout;
    private ImageView loadingLayout;
    private LinearLayout layout;

    private Button postButton;
    private LoadingDailog dialog;

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
        loadingLayout.setVisibility(View.VISIBLE);
        loadingErrorLayout.setVisibility(View.GONE);
        layout.setVisibility(View.GONE);
        sendRequest();
    }

    /**
     * 初始化点击事件
     */
    private void initEvent() {
        loadingErrorLayout.setOnClickListener(this);
        postButton.setOnClickListener(this);
    }

    /**
     * 发送网络请求
     */
    private void sendRequest() {
        swipeRefreshLayout.setRefreshing(true);
        HttpUtil.sendGetRequestWithHttp(CONFIG_URL + token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        loadingErrorLayout.setVisibility(View.VISIBLE);
                        loadingLayout.setVisibility(View.GONE);
                        layout.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String content = response.body().string();
                final int code = response.code();
                if (code == 200) {
                    final Config config = Util.handleConfig(content);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initData(config);
                            swipeRefreshLayout.setRefreshing(false);
                            loadingLayout.setVisibility(View.GONE);
                            loadingErrorLayout.setVisibility(View.GONE);
                            layout.setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (code == 401 || code == 412) {
                                Intent intent = new Intent(PersonalSettingActivity.this, LoginActivity.class);
                                intent.putExtra("token_timeout", "登录超时");
                                preferences.edit().putString("token", null).commit();
                                startActivity(intent);
                                ActivityCollector.finishAllActivity();
                            } else if (code == 422) {
                                try {
                                    JSONObject js = new JSONObject(content);
                                    Toast.makeText(PersonalSettingActivity.this, js.get("message") + "", Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            swipeRefreshLayout.setRefreshing(false);
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
        if (config.getAllow_change_user() != null) {
            if (config.getAllow_change_user().equals("true")) {
                changeName.setChecked(true);
            } else {
                changeName.setChecked(false);
            }
        } else {
            changeName.setChecked(false);
        }

        if (config.getAllow_change_pwd() != null) {
            if (config.getAllow_change_pwd().equals("true")) {
                changePwd.setChecked(true);
            } else {
                changePwd.setChecked(false);
            }
        } else {
            changePwd.setChecked(false);
        }
        if (config.getAllow_change_avatar() != null) {
            if (config.getAllow_change_avatar().equals("true")) {
                changePic.setChecked(true);
            } else {
                changePic.setChecked(false);
            }
        } else {
            changePic.setChecked(false);
        }

        if (config.getAllow_change_tel() != null) {
            if (config.getAllow_change_tel().equals("true")) {
                changePhone.setChecked(true);
            } else {
                changePhone.setChecked(false);
            }
        } else {
            changePhone.setChecked(false);
        }

        if (config.getAllow_change_email() != null) {
            if (config.getAllow_change_email().equals("true")) {
                changeEmail.setChecked(true);
            } else {
                changeEmail.setChecked(false);
            }
        } else {
            changeEmail.setChecked(false);
        }

        if (config.getAllow_change_fingerprint() != null) {
            if (config.getAllow_change_fingerprint().equals("true")) {
                changeFingerprint.setChecked(true);
            } else {
                changeFingerprint.setChecked(false);
            }
        } else {
            changeFingerprint.setChecked(false);
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
        changePic = findViewById(R.id.personal_setting_pic);
        changePhone = findViewById(R.id.personal_setting_phone);
        changeEmail = findViewById(R.id.personal_setting_email);
        changeFingerprint = findViewById(R.id.personal_setting_fingerprint);

        loadingErrorLayout = findViewById(R.id.personal_setting_loading_error_layout);
        loadingLayout = findViewById(R.id.personal_setting_loading_layout);
        layout = findViewById(R.id.personal_setting_layout);
        swipeRefreshLayout = findViewById(R.id.personal_setting_swipe);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        swipeRefreshLayout.setEnabled(false);//设置swipe不可用

        postButton = findViewById(R.id.personal_setting_button);

        titleBar = findViewById(R.id.personal_setting_title_bar);
        titleBar.setTextViewText("系统设置");
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

        LoadingDailog.Builder loadBuilder = new LoadingDailog.Builder(this)
                .setMessage("上传中...")
                .setCancelable(false)
                .setCancelOutside(false);
        dialog = loadBuilder.create();
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
            case R.id.personal_setting_button:
                sendPostRequest();
                break;
            default:
                break;
        }
    }

    /**
     * 发送post请求
     */
    private void sendPostRequest() {
        final JSONObject jsonObject = new JSONObject();

        try {
            if (changeName.isChecked()) {
                jsonObject.put("allow_change_user", "true");
            } else {
                jsonObject.put("allow_change_user", "false");
            }
            if (changePwd.isChecked()) {
                jsonObject.put("allow_change_pwd", "true");
            } else {
                jsonObject.put("allow_change_pwd", "false");
            }
            if (changePic.isChecked()) {
                jsonObject.put("allow_change_avatar", "true");
            } else {
                jsonObject.put("allow_change_avatar", "false");
            }
            if (changePhone.isChecked()) {
                jsonObject.put("allow_change_tel", "true");
            } else {
                jsonObject.put("allow_change_tel", "false");
            }
            if (changeEmail.isChecked()) {
                jsonObject.put("allow_change_email", "true");
            } else {
                jsonObject.put("allow_change_email", "false");
            }
            if (changeFingerprint.isChecked()) {
                jsonObject.put("allow_change_fingerprint", "true");
            } else {
                jsonObject.put("allow_change_fingerprint", "false");
            }

        dialog.show();
        HttpUtil.sendPostRequestHttp(Util.URL + "config", token, jsonObject.toString(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.cancel();
                        }
                        Toast.makeText(PersonalSettingActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String content = response.body().string();
                final int code = response.code();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.cancel();
                        }
                        if (code == 200) {
                            finish();
                        } else if (code == 401 || code == 412) {
                            Intent intent = new Intent(PersonalSettingActivity.this, LoginActivity.class);
                            intent.putExtra("token_timeout", "登录超时");
                            preferences.edit().putString("token", null).commit();
                            startActivity(intent);
                            ActivityCollector.finishAllActivity();
                        } else if (code == 422) {
                            try {
                                JSONObject js = new JSONObject(content);
                                Toast.makeText(PersonalSettingActivity.this, js.get("message") + "", Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(PersonalSettingActivity.this, "修改失败！", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
