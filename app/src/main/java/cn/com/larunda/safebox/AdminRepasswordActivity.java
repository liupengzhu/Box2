package cn.com.larunda.safebox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.tu.loadingdialog.LoadingDailog;
import com.google.gson.JsonObject;
import com.larunda.safebox.R;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.com.larunda.safebox.util.ActivityCollector;
import cn.com.larunda.safebox.util.BaseActivity;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AdminRepasswordActivity extends BaseActivity implements View.OnClickListener {

    private TitleBar titleBar;
    private EditText editText1;
    private EditText editText2;
    private EditText editText3;
    private Button saveButton;
    private int id;
    private SharedPreferences preferences;
    private String token;
    private LoadingDailog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_repassword);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        id = getIntent().getIntExtra("id", 0);
        initView();
        intEvent();
    }

    /**
     * 初始化view
     */
    private void initView() {

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = preferences.getString("token", null);

        titleBar = findViewById(R.id.admin_re_password_title_bar);
        titleBar.setTextViewText("重置管理员密码");
        titleBar.setRightButtonSrc(0);
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);

        editText1 = findViewById(R.id.re_password_edit1);
        editText2 = findViewById(R.id.re_password_edit2);
        editText3 = findViewById(R.id.re_password_edit3);
        saveButton = findViewById(R.id.re_password_button);

        LoadingDailog.Builder loadBuilder = new LoadingDailog.Builder(this)
                .setMessage("上传中...")
                .setCancelable(false)
                .setCancelOutside(false);
        dialog = loadBuilder.create();
    }

    /**
     * 初始化点击事件
     */
    private void intEvent() {
        saveButton.setOnClickListener(this);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.re_password_button:
                sendRequest();
                break;
        }
    }

    /**
     * 发送请求
     */
    private void sendRequest() {
        String password = editText1.getText().toString().trim();
        String password_confirmation = editText2.getText().toString().trim();
        String check_pwd = editText3.getText().toString().trim();
        if (!isEmpty(password, password_confirmation, check_pwd)) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("password", password);
                jsonObject.put("password_confirmation", password_confirmation);
                jsonObject.put("check_pwd", check_pwd);
                dialog.show();
                HttpUtil.sendPostRequestWithHttp(Util.URL + "company/" + id + "/reset_pwd" + Util.TOKEN + token,
                        jsonObject.toString(), new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (dialog != null && dialog.isShowing()) {
                                            dialog.cancel();
                                        }
                                        Toast.makeText(AdminRepasswordActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
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
                                            if (content.equals("true")) {
                                                finish();
                                            } else {
                                                Toast.makeText(AdminRepasswordActivity.this, "密码重置失败", Toast.LENGTH_SHORT).show();
                                            }
                                        } else if (code == 401) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Intent intent = new Intent(AdminRepasswordActivity.this, LoginActivity.class);
                                                    intent.putExtra("token_timeout", "登录超时");
                                                    preferences.edit().putString("token", null).commit();
                                                    startActivity(intent);
                                                    ActivityCollector.finishAllActivity();
                                                }
                                            });
                                        } else if (code == 422) {
                                            Toast.makeText(AdminRepasswordActivity.this, "密码重置失败", Toast.LENGTH_SHORT).show();
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

    private boolean isEmpty(String password, String password_confirmation, String check_pwd) {
        if (password.isEmpty()) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (password.length() < 8) {
            Toast.makeText(this, "密码长度不能小于8位", Toast.LENGTH_SHORT).show();
            return true;
        } else if (password_confirmation.isEmpty()) {
            Toast.makeText(this, "确认密码不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (!password.equals(password_confirmation)) {
            Toast.makeText(this, "新密码与确认密码不一致", Toast.LENGTH_SHORT).show();
            return true;
        } else if (check_pwd.isEmpty()) {
            Toast.makeText(this, "验证密码不能为空", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}

