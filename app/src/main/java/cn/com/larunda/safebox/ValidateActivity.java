package cn.com.larunda.safebox;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.larunda.safebox.R;
import com.larunda.selfdialog.FingerprintDialog;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

public class ValidateActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences preferences;
    private String token;
    private TitleBar titleBar;

    private EditText text;
    private Button button;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FingerprintDialog fingerprintDialog;
    private boolean isSeccess = false;

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
                    }
                });
                fingerprintDialog.show();
                break;
        }
    }
}
