package cn.com.larunda.safebox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.larunda.safebox.R;
import com.larunda.selfdialog.ConfirmDialog;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

import cn.com.larunda.safebox.util.ActivityCollector;
import cn.com.larunda.safebox.util.BaseActivity;

public class SystemSettingActivity extends BaseActivity implements View.OnClickListener {

    private TitleBar titleBar;
    private Switch fingerprint;
    private SharedPreferences preferences;
    private String token;
    private ImageView fingerprint_img;
    private RelativeLayout fingerprint_layout;
    private FingerprintManager manager;
    private String userId;

    private RelativeLayout quitButton;
    private ConfirmDialog confirmDialog;

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
        userId = preferences.getString("user_id", null);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            manager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
            if (!manager.isHardwareDetected()) {
                fingerprint_img.setVisibility(View.GONE);
                fingerprint_layout.setVisibility(View.GONE);
            }
        } else {
            fingerprint_img.setVisibility(View.GONE);
            fingerprint_layout.setVisibility(View.GONE);
        }
        boolean fingerprintIs = preferences.getBoolean(userId + "fingerprint", false);
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
        quitButton.setOnClickListener(this);
        confirmDialog.setNoOnclickListener(new ConfirmDialog.onNoOnclickListener() {
            @Override
            public void onNoClick(View v) {
                confirmDialog.cancel();
            }
        });
        confirmDialog.setYesOnclickListener(new ConfirmDialog.onYesOnclickListener() {
            @Override
            public void onYesClick(View v) {
                //退出程序
                preferences.edit().putBoolean("isUpdate", true).commit();
                preferences.edit().putString("homeInfo", null).commit();
                preferences.edit().putString("boxInfo", null).commit();
                preferences.edit().putString("userLogInfo", null).commit();
                preferences.edit().putString("boxLogInfo", null).commit();
                preferences.edit().putString("appLogInfo", null).commit();
                preferences.edit().putString("menuInfo", null).commit();

                ActivityCollector.finishAllActivity();
                System.exit(0);
            }
        });
    }

    /**
     * 初始化view
     */
    private void initView() {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = preferences.getString("token", null);

        quitButton = findViewById(R.id.system_setting_quit);
        confirmDialog = new ConfirmDialog(this);

        titleBar = findViewById(R.id.system_setting_title_bar);
        titleBar.setTextViewText("");
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setRightButtonSrc(0);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);
        fingerprint = findViewById(R.id.system_setting_fingerprint);
        fingerprint_img = findViewById(R.id.system_setting_fingerprint_img);
        fingerprint_layout = findViewById(R.id.system_setting_fingerprint_layout);
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
                    preferences.edit().putString(userId + "user_password", null).apply();
                    preferences.edit().putBoolean(userId + "fingerprint", false).apply();
                }
                break;
            case R.id.system_setting_quit:
                confirmDialog.show();
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
                    preferences.edit().putBoolean(userId + "fingerprint", false).apply();
                } else if (resultCode == 1) {
                    fingerprint.setChecked(true);
                    preferences.edit().putBoolean(userId + "fingerprint", true).apply();
                }
                break;
        }
    }
}
