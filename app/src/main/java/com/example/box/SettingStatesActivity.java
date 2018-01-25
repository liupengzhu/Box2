package com.example.box;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.selfdialog.ChooseDialog;
import com.example.titlebar.TitleBar;
import com.example.titlebar.TitleListener;

import java.util.ArrayList;
import java.util.List;

public class SettingStatesActivity extends AppCompatActivity implements View.OnClickListener {

    private TitleBar titleBar;

    RelativeLayout lockButton;
    TextView lockText;

    RelativeLayout bfButton;
    TextView bfText;

    List<String> lockList = new ArrayList<>();
    List<String> bfList = new ArrayList<>();

    private ChooseDialog lockChooseDialog;
    private ChooseDialog bfChooseDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_states);
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
        lockButton.setOnClickListener(this);
        bfButton.setOnClickListener(this);
        lockChooseDialog.setOnClickListener(new ChooseDialog.OnClickListener() {
            @Override
            public void OnClick(View v, int positon) {
                lockText.setText(lockList.get(positon));
                lockChooseDialog.cancel();
            }
        });
        bfChooseDialog.setOnClickListener(new ChooseDialog.OnClickListener() {
            @Override
            public void OnClick(View v, int positon) {
                bfText.setText(bfList.get(positon));
                bfChooseDialog.cancel();
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        lockList.add("未锁定");
        lockList.add("已锁定");
        bfList.add("未布防");
        bfList.add("已布防");
    }

    /**
     * 初始化view
     */

    private void initView() {

        titleBar = findViewById(R.id.setting_states_title_bar);
        titleBar.setTextViewText("设定状态");
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setRightButtonSrc(0);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);

        lockButton = findViewById(R.id.setting_lock);
        lockText = findViewById(R.id.setting_lock_text);
        bfButton = findViewById(R.id.setting_bf);
        bfText = findViewById(R.id.setting_bf_text);

        lockChooseDialog = new ChooseDialog(this, lockList);
        bfChooseDialog = new ChooseDialog(this, bfList);

    }

    /**
     * 点击事件监听
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_lock:
                lockChooseDialog.show();
                break;
            case R.id.setting_bf:
                bfChooseDialog.show();
                break;
            default:
                break;
        }
    }
}
