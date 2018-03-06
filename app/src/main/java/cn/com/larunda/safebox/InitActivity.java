package cn.com.larunda.safebox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.LoginFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.larunda.safebox.R;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.com.larunda.safebox.gson.Message;
import cn.com.larunda.safebox.util.ActivityCollector;
import cn.com.larunda.safebox.util.BaseActivity;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class InitActivity extends BaseActivity implements View.OnClickListener {

    private static final String POST_URL = Util.URL + "box/binding" + Util.TOKEN;
    private static final String INIT_URL = Util.URL + "box/initialize" + Util.TOKEN;
    private static final String STATUS_URL = Util.URL + "box/get_initialize_status" + Util.TOKEN;

    private TitleBar titleBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String id;

    private EditText text;
    private EditText rePassword;
    private EditText timeText;
    private Button button;
    private SharedPreferences preferences;
    private String token;
    private String code;

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            sendStatusRequest();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        id = getIntent().getStringExtra("id");
        code = getIntent().getStringExtra("code");
        initView();
        initEvent();
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
                setResult(0, getIntent());
                finish();
            }

            @Override
            public void onRightButtonClickListener(View v) {


            }
        });
        button.setOnClickListener(this);
    }

    /**
     * 初始化view
     */
    private void initView() {

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = preferences.getString("token", null);

        text = findViewById(R.id.init_edit);
        rePassword = findViewById(R.id.init_re_password_edit);
        timeText = findViewById(R.id.init_time_edit);
        button = findViewById(R.id.init_button);

        swipeRefreshLayout = findViewById(R.id.init_swiper);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        swipeRefreshLayout.setEnabled(false);//设置swipe不可用


        titleBar = findViewById(R.id.init_title_bar);
        titleBar.setTextViewText("递送箱初始化");
        titleBar.setRightButtonSrc(0);
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);
    }

    /**
     * 点击事件拦截
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.init_button:
                if (!TextUtils.isEmpty(text.getText().toString().trim())
                        && !TextUtils.isEmpty(timeText.getText().toString().trim())
                        && !TextUtils.isEmpty(rePassword.getText().toString().trim())) {
                    if (text.getText().toString().trim().length() != 6) {
                        Toast.makeText(InitActivity.this, "密码必须为6位", Toast.LENGTH_SHORT).show();
                    } else if (!text.getText().toString().trim().equals(rePassword.getText().toString().trim())) {
                        Toast.makeText(InitActivity.this, "确认密码和密码不一致", Toast.LENGTH_SHORT).show();
                    } else if (Integer.parseInt(timeText.getText().toString().trim()) < 30
                            || Integer.parseInt(timeText.getText().toString().trim()) > 1800) {
                        Toast.makeText(InitActivity.this, "上传周期必须在30到18000之间", Toast.LENGTH_SHORT).show();
                    } else {
                        sendRequest(text.getText().toString().trim(), rePassword.getText().toString().trim(), timeText.getText().toString().trim());
                    }
                } else {
                    Toast.makeText(InitActivity.this, "密码或上传周期不能为空", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 发送网络请求
     *
     * @param password
     * @param time
     */
    private void sendRequest(final String password, final String rePassword, final String time) {
        swipeRefreshLayout.setRefreshing(true);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", code);
            HttpUtil.sendPostRequestWithHttp(POST_URL + token, jsonObject.toString(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(InitActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sendInitRequest(password, rePassword, time);
                        }
                    });
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 发送初始化请求
     *
     * @param password
     * @param time
     */
    private void sendInitRequest(String password, String rePassword, String time) {

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("password", password);
            jsonObject.put("cycle", time);
            jsonObject.put("re_password", password);
            jsonObject.put("code", code);
            swipeRefreshLayout.setRefreshing(true);
            HttpUtil.sendPostRequestWithHttp(INIT_URL + token, jsonObject.toString(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(InitActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    sendStatusRequest();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    private void sendStatusRequest() {
        swipeRefreshLayout.setRefreshing(true);
        HttpUtil.sendGetRequestWithHttp(STATUS_URL + token + "&code=" + code, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(InitActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String content = response.body().string();
                if (Util.isGoodJson(content)) {
                    final Message message = Util.handleMessage(content);
                    if (message != null && message.error == null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                parseMessage(message);
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(InitActivity.this, LoginActivity.class);
                                intent.putExtra("token_timeout", "登录超时");
                                preferences.edit().putString("token", null).commit();
                                startActivity(intent);
                                ActivityCollector.finishAllActivity();
                            }
                        });
                    }
                }
            }
        });
    }

    private void parseMessage(Message message) {
        Log.d("main", message.message);
        if (message.message.equals("正在初始化中，请稍后")) {
            handler.postDelayed(runnable, 5000);
        } else if (message.message.equals("成功")) {
            finish();
        } else {
            Toast.makeText(InitActivity.this, message.message + "", Toast.LENGTH_SHORT).show();
            handler.removeCallbacks(runnable);
        }
    }


}
