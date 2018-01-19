package com.example.box;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.box.gson.UserToken;
import com.example.box.util.HttpUtil;
import com.example.box.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String LOGIN_NAME = "login_name";
    public static final String LOGIN_PASSWORD = "login_password";
    public static final String LOGIN_URI = "http://safebox.dsmcase.com:90/api/app_login";
    EditText loginName;
    EditText loginPassword;
    CheckBox checkBox;
    Button loginButton;
    SharedPreferences.Editor editor;
    SharedPreferences preferences;
    LinearLayout registerButton;
    Button backButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        //初始化界面控件
        initView();


        //从数据库获取登录名和密码并设置到界面
        String login_Name = preferences.getString(LOGIN_NAME, null);
        String login_Password = preferences.getString(LOGIN_PASSWORD, null);

        String token_time = getIntent().getStringExtra("token_timeout");


        if (login_Name != null && login_Password != null) {
            checkBox.setChecked(true);
            loginName.setText(login_Name);
            loginPassword.setText(login_Password);
            loginName.setSelection(login_Name.length());
            loginPassword.setSelection(login_Password.length());

        } else if (login_Name != null) {
            loginName.setText(login_Name);
            loginName.setSelection(login_Name.length());
        }
        if (token_time != null) {
            Toast.makeText(this, "登录超时,请重新登录", Toast.LENGTH_SHORT).show();
        }


        //登录按钮监听
        loginButton.setOnClickListener(this);
        //注册按钮监听
        registerButton.setOnClickListener(this);

        backButton.setOnClickListener(this);


    }

    /**
     * 登录方法
     */

    private void login() {
        String username = loginName.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();

        if (!TextUtils.isEmpty(username) &&
                !TextUtils.isEmpty(password)) {

            if (checkBox.isChecked()) {
                editor.putString(LOGIN_NAME, username);
                editor.putString(LOGIN_PASSWORD, password);
                editor.apply();
                loginServer(username, password);

            } else {
                editor.clear().commit();
                editor.putString(LOGIN_NAME, username);
                editor.apply();
                loginServer(username, password);

            }

        } else {
            Toast.makeText(this, "账号或者密码不能为空", Toast.LENGTH_SHORT).show();
        }


    }

    //向服务器提交用户名 密码 尝试登录
    private void loginServer(String name, String password) {


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user", name);
            jsonObject.put("pwd", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        HttpUtil.sendPostRequestWithHttp(LOGIN_URI, jsonObject.toString(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {


            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {


                UserToken userToken = Util.handleLoginInfo(response.body().string());


                if (userToken != null && userToken.message == null) {


                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    editor.putString("token", userToken.token);
                    editor.apply();
                    startActivity(intent);
                    finish();
                } else {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "账号或者密码不正确！", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });


    }

    private void initView() {
        loginName = findViewById(R.id.login_name);
        loginPassword = findViewById(R.id.login_password);
        checkBox = findViewById(R.id.check_button);
        loginButton = findViewById(R.id.login_button);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        registerButton = findViewById(R.id.login_register_layout);
        backButton = findViewById(R.id.back_button);
    }

    /**
     * 监听点击事件的方法
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_register_layout:
                Intent registerIntent = new Intent(this, RegisterActivity.class);
                startActivity(registerIntent);
                break;

            case R.id.login_button:
                login();
                break;

            case R.id.back_button:
                finish();
                break;

            default:
                break;

        }


    }
}
