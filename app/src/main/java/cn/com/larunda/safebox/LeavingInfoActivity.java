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

import cn.com.larunda.safebox.adapter.LeavingInfoFragmentAdapter;
import cn.com.larunda.safebox.fragment.LeavingInfoFragment;
import cn.com.larunda.safebox.fragment.LeavingMapFragment;
import cn.com.larunda.safebox.util.Util;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

import java.util.ArrayList;
import java.util.List;


public class LeavingInfoActivity extends AppCompatActivity {

    private TitleBar titleBar;
    private List<Fragment> fragmentList = new ArrayList<>();
    private List<String> titleList = new ArrayList<>();
    private ViewPager viewPager;
    private TabLayout tabLayout;
    LeavingInfoFragmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaving_info);
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
     * 初始化view
     */
    private void initView() {

        tabLayout = findViewById(R.id.leaving_info_tabLayout);
        viewPager = findViewById(R.id.leaving_info_viewPager);
        adapter = new LeavingInfoFragmentAdapter(getSupportFragmentManager(),
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

        titleBar = findViewById(R.id.leaving_info_title_bar);
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

    /**
     * 初始化fragment
     */
    private void init() {
        fragmentList.add(new LeavingInfoFragment());
        fragmentList.add(new LeavingMapFragment());
        titleList.add("详细信息");
        titleList.add("离位地图");
    }
}
