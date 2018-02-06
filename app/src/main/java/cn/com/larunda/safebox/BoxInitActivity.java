package cn.com.larunda.safebox;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.larunda.safebox.R;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

public class BoxInitActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    private String token;
    private TitleBar titleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box_init);
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
    }

    /**
     * 初始化view
     */
    private void initView() {
        preferences = PreferenceManager.getDefaultSharedPreferences(BoxInitActivity.this);
        token = preferences.getString("token", null);

        titleBar = findViewById(R.id.box_init_title_bar);
        titleBar.setTextViewText("递送箱初始化");
        titleBar.setRightButtonSrc(0);
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);
    }

}
