package cn.com.larunda.safebox;

import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.larunda.safebox.R;
import com.larunda.selfdialog.ChooseDialog;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

import java.util.ArrayList;
import java.util.List;

public class SettingQxActivity extends AppCompatActivity implements View.OnClickListener {

    private TitleBar titleBar;
    private RelativeLayout levelButton;
    private TextView levelText;

    private List<String> levelList = new ArrayList<>();
    private ChooseDialog levelDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_qx);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        initData();
        initView();
        initEvent();

    }

    /**
     * 初始化点击事件
     */
    private void initEvent() {
        levelButton.setOnClickListener(this);
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
        levelDialog.setOnClickListener(new ChooseDialog.OnClickListener() {
            @Override
            public void OnClick(View v, int positon) {
                levelText.setText(levelList.get(positon));
                levelDialog.cancel();
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        levelList.add("一级加密");
        levelList.add("二级加密");
        levelList.add("三级加密");
    }

    /**
     * 初始化View
     */
    private void initView() {

        levelButton = findViewById(R.id.setting_level);
        levelDialog = new ChooseDialog(this, levelList);
        levelText = findViewById(R.id.setting_level_text);

        titleBar = findViewById(R.id.setting_qx_title_bar);
        titleBar.setTextViewText("设定权限");
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setRightButtonSrc(0);
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
            case R.id.setting_level:
                levelDialog.show();
                break;
        }
    }
}
