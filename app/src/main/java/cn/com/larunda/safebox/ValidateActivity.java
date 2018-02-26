package cn.com.larunda.safebox;

import android.app.KeyguardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.larunda.safebox.R;
import com.larunda.selfdialog.FingerprintDialog;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.com.larunda.safebox.gson.ValidateData;
import cn.com.larunda.safebox.util.ActivityCollector;
import cn.com.larunda.safebox.util.BaseActivity;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ValidateActivity extends BaseActivity implements View.OnClickListener {

    private SharedPreferences preferences;
    private String token;
    private TitleBar titleBar;

    private EditText text;
    private Button button;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FingerprintDialog fingerprintDialog;
    private boolean isSeccess = false;
    private String userName;
    private String userId;
    public static final String VALIDATE_URL = Util.URL + "app/fingerprint" + Util.TOKEN;
    private String password;
    private FingerprintManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = preferences.getString("token", null);
        userName = preferences.getString("user_name", null);
        userId = preferences.getString("user_id", null);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            manager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
        }
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
        text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                password = text.getText().toString().trim();
                if (password != null && !TextUtils.isEmpty(password)) {
                    sendRequest(password);
                } else {
                    Toast.makeText(ValidateActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }

    /**
     * 初始化view
     */
    private void initView() {


        text = findViewById(R.id.validate_edit);
        button = findViewById(R.id.validate_button);

        swipeRefreshLayout = findViewById(R.id.validate_swiper);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        swipeRefreshLayout.setEnabled(false);//设置swipe不可用


        titleBar = findViewById(R.id.validate_title_bar);
        titleBar.setTextViewText("身份验证");
        titleBar.setRightButtonSrc(0);
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);
    }

    /**
     * 点击事件监听
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.validate_button:
                password = text.getText().toString().trim();
                if (password != null && !TextUtils.isEmpty(password)) {
                    sendRequest(password);
                } else {
                    Toast.makeText(ValidateActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                }
                break;


        }
    }

    /**
     * 发送验证密码的请求
     *
     * @param password
     */
    private void sendRequest(String password) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", userId);
            jsonObject.put("user_name", userName);
            jsonObject.put("user_password", password);
            swipeRefreshLayout.setRefreshing(true);
            HttpUtil.sendPostRequestWithHttp(VALIDATE_URL + token, jsonObject.toString(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(ValidateActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String content = response.body().string();
                    runOnUiThread(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void run() {
                            parseContent(content);
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    /**
     * 解析数据
     *
     * @param content
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void parseContent(String content) {
        if (Util.isGoodJson(content)) {
            ValidateData data = Util.handleValidatedata(content);
            if (data != null && data.error == null) {
                if (data.data.equals("success")) {
                    if (manager.hasEnrolledFingerprints()) {
                        startDialog();
                    } else {
                        Toast.makeText(this, "请先注册指纹", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(this, "密码错误，请重新输入", Toast.LENGTH_SHORT).show();
                }
            } else {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra("token_timeout", "登录超时");
                preferences.edit().putString("token", null).commit();
                startActivity(intent);
                ActivityCollector.finishAllActivity();
            }


        }
    }

    private void startDialog() {
        fingerprintDialog = new FingerprintDialog(this);
        fingerprintDialog.setCancelButtonOnclickListener(new FingerprintDialog.CancelButtonOnclickListener() {
            @Override
            public void onClick(View v) {
                fingerprintDialog.cancel();
            }
        });
        fingerprintDialog.setValidateSeccessListener(new FingerprintDialog.ValidateSeccessListener() {
            @Override
            public void seccess(FingerprintManager.AuthenticationResult result) {
                isSeccess = true;
                preferences.edit().putString(userId + "user_password", password).apply();
                setResult(1, getIntent());
                Toast.makeText(ValidateActivity.this, "验证成功", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        fingerprintDialog.show();
    }
}
