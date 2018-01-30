package cn.com.larunda.safebox;


import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


import com.larunda.horizontalprogressbar.HorizontalProgressBarWithNunber;
import com.larunda.safebox.R;

import java.io.IOException;

import cn.com.larunda.safebox.gson.DynamicPassword;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class DynamicPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    public final String PASSWORD_URL = "http://safebox.dsmcase.com:90/api/box/show_dynamic_password?_token=";
    static HorizontalProgressBarWithNunber progressBar;
    Button backButton;
    private String id;
    private SharedPreferences.Editor editor;
    public final int MSG_PROGRESS_UPDATE = 0x110;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            int progress = progressBar.getProgress();
            progressBar.setProgress(++progress);
            if (progress >= 1000) {
                progressBar.setProgress(0);
                sendRequest();
            }
            mHandler.sendEmptyMessageDelayed(MSG_PROGRESS_UPDATE, 60);
        }


    };
    private SharedPreferences preferences;
    private String token;
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private TextView textView4;
    private TextView textView5;
    private TextView textView6;
    private long last_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_password);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        id = getIntent().getStringExtra("id");
        initView();
        last_time = preferences.getLong(id + "time", 0);
        if (last_time != 0) {
            long time = System.currentTimeMillis() - last_time;
            Log.d("main", time + "");
            if (time < 60000) {
                Log.d("main", time / 60 + "");
                progressBar.setProgress((int) (time / 60));
                String passwordString = preferences.getString(id + "password", null);
                if (passwordString != null) {
                    char[] password = passwordString.toCharArray();
                    if (password != null && password.length == 6) {
                        textView1.setText(password[0] + "");
                        textView2.setText(password[1] + "");
                        textView3.setText(password[2] + "");
                        textView4.setText(password[3] + "");
                        textView5.setText(password[4] + "");
                        textView6.setText(password[5] + "");
                        mHandler.sendEmptyMessage(MSG_PROGRESS_UPDATE);
                    }
                }

            } else {
                sendRequest();
            }
        } else {
            sendRequest();
        }


    }

    /**
     * 发送网络请求
     */
    private void sendRequest() {
        HttpUtil.sendGetRequestWithHttp(PASSWORD_URL + token + "&box_id=" + id, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView1.setText("0");
                        textView2.setText("0");
                        textView3.setText("0");
                        textView4.setText("0");
                        textView5.setText("0");
                        textView6.setText("0");
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final DynamicPassword dynamicPassword = Util.handleDynamicPassword(response.body().string());
                if (dynamicPassword != null && dynamicPassword.error == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initPassword(dynamicPassword);
                        }
                    });
                }
            }
        });
    }

    /**
     * 解析数据
     *
     * @param dynamicPassword
     */
    private void initPassword(DynamicPassword dynamicPassword) {
        if (dynamicPassword.password != null) {
            char[] password = dynamicPassword.password.toCharArray();
            if (password != null && password.length == 6) {
                textView1.setText(password[0] + "");
                textView2.setText(password[1] + "");
                textView3.setText(password[2] + "");
                textView4.setText(password[3] + "");
                textView5.setText(password[4] + "");
                textView6.setText(password[5] + "");
                mHandler.sendEmptyMessage(MSG_PROGRESS_UPDATE);
                long time = System.currentTimeMillis();
                editor = preferences.edit();
                editor.putLong(id + "time", time);
                editor.putString(id + "password", dynamicPassword.password);
                editor.apply();
            }

        }
    }

    /**
     * 初始化view
     */
    private void initView() {
        preferences = PreferenceManager.getDefaultSharedPreferences(DynamicPasswordActivity.this);
        token = preferences.getString("token", null);

        backButton = findViewById(R.id.dt_back_button);
        progressBar = findViewById(R.id.progress_bar);
        backButton.setOnClickListener(this);

        textView1 = findViewById(R.id.password_text_1);
        textView2 = findViewById(R.id.password_text_2);
        textView3 = findViewById(R.id.password_text_3);
        textView4 = findViewById(R.id.password_text_4);
        textView5 = findViewById(R.id.password_text_5);
        textView6 = findViewById(R.id.password_text_6);
    }

    /**
     * 点击事件监听
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dt_back_button:

                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);

    }
}
