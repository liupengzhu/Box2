package cn.com.larunda.safebox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Switch;

import com.larunda.safebox.R;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

public class SystemSettingActivity extends AppCompatActivity implements View.OnClickListener {

    private TitleBar titleBar;
    private Switch fingerprint;
    private SharedPreferences preferences;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_setting);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        initView();
        initEvent();
        boolean fingerprintIs = preferences.getBoolean("fingerprint", false);
        if (fingerprintIs) {
            fingerprint.setChecked(true);
        } else {
            fingerprint.setChecked(false);
        }
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
        fingerprint.setOnClickListener(this);
    }

    /**
     * 初始化view
     */
    private void initView() {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = preferences.getString("token", null);

        titleBar = findViewById(R.id.system_setting_title_bar);
        titleBar.setTextViewText("");
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setRightButtonSrc(0);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);
        fingerprint = findViewById(R.id.system_setting_fingerprint);
    }

    /**
     * 点击事件监听
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.system_setting_fingerprint:
                if (fingerprint.isChecked()) {
                    Intent intent = new Intent(SystemSettingActivity.this, ValidateActivity.class);
                    startActivityForResult(intent, 1);
                } else {
                    preferences.edit().putString("user_password", null).apply();
                    preferences.edit().putBoolean("fingerprint", false).apply();
                }
                break;

            default:
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == 0) {
                    fingerprint.setChecked(false);
                    preferences.edit().putBoolean("fingerprint", false).apply();
                } else if (resultCode == 1) {
                    fingerprint.setChecked(true);
                    preferences.edit().putBoolean("fingerprint", true).apply();
                }
                break;
        }
    }
}
