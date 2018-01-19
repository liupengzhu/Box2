package com.example.box;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class LoginOrRegisterActivity extends AppCompatActivity {

    Button loginButton;
    Button registerButton;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login_or_register);
        initView();
        initviewEvent();
        //若果token不为空 则直接进入主界面
        if (preferences.getString("token", null) != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void initviewEvent() {

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginOrRegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    /**
     * 初始化view
     */
    private void initView() {

        loginButton = findViewById(R.id.register_login_button);
        registerButton = findViewById(R.id.register_register_button);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

}
