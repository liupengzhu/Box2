package cn.com.larunda.safebox;

import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.larunda.safebox.R;

import cn.com.larunda.safebox.adapter.BoxMessageFragmentAdapter;
import cn.com.larunda.safebox.fragment.BoxMessageInfoFragment;
import cn.com.larunda.safebox.fragment.BoxMessageSafeFragment;
import cn.com.larunda.safebox.util.Util;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

import java.util.ArrayList;
import java.util.List;

public class BoxActivity extends AppCompatActivity {

    private TitleBar titleBar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private List<Fragment> fragmentList = new ArrayList<>();
    private List<String> titleList = new ArrayList<>();
    private BoxMessageFragmentAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        init();
        initView();
    }

    /**
     * 初始化fragmente
     */
    private void init() {
        fragmentList.add(new BoxMessageInfoFragment());
        fragmentList.add(new BoxMessageSafeFragment());
        titleList.add("基本信息");
        titleList.add("递送箱安全");
    }

    /**
     * 初始化view
     */
    private void initView() {

        tabLayout = findViewById(R.id.box_message_tabLayout);
        viewPager = findViewById(R.id.box_message_viewPager);
        adapter = new BoxMessageFragmentAdapter(getSupportFragmentManager(),
                fragmentList, titleList);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        //设置下划线长度
        tabLayout.post(new Runnable() {
            @Override
            public void run() {

                Util.setIndicator(tabLayout, 50, 50);
            }
        });

        titleBar = findViewById(R.id.box_message_title_bar);
        titleBar.setTextViewText("");
        titleBar.setRightButtonSrc(0);
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);
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
}
