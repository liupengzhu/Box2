package com.example.box;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.box.adapter.LeavingAlarmAdapter;
import com.example.box.recycler.LeavingAlarm;
import com.example.titlebar.TitleBar;
import com.example.titlebar.TitleListener;

import java.util.ArrayList;
import java.util.List;

public class LeavingAlarmActivity extends AppCompatActivity implements View.OnClickListener {

    private TitleBar titleBar;
    LeavingAlarmAdapter adapter;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayoutManager manager;
    List<LeavingAlarm> leavingAlarmArrayList = new ArrayList<>();


    private CardView top_layout;
    private LinearLayout bottom_layout;

    /**
     * 是否在长按状态
     */
    public boolean isLongClick = false;

    /**
     * 是否在全选状态
     */
    private boolean isAllChecked = false;
    private ImageView allCheckedImage;
    private TextView allCheckedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaving_alarm);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        initData();
        initView();
    }

    /**
     * 测试方法 初始化数据
     */
    private void initData() {
        for (int i = 0; i < 10; i++) {
            LeavingAlarm leavingAlarm = new LeavingAlarm("XXX00" + i, "10" + i + "m",
                    "张三" + i, "否");
            leavingAlarmArrayList.add(leavingAlarm);
        }

    }

    /**
     * 初始化view
     */
    private void initView() {

        titleBar = findViewById(R.id.leaving_alarm_title_bar);
        titleBar.setTextViewText("离位报警");
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
        adapter = new LeavingAlarmAdapter(leavingAlarmArrayList);
        recyclerView = findViewById(R.id.leaving_alarm_recycler);
        swipeRefreshLayout = findViewById(R.id.leaving_alarm_swiper);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        setAdapterClick(adapter);


        top_layout = findViewById(R.id.leaving_alarm_top_layout);
        bottom_layout = findViewById(R.id.leaving_alarm_bottom_layout);

        allCheckedImage = findViewById(R.id.leaving_alarm_all_checked_image);
        allCheckedText = findViewById(R.id.leaving_alarm_all_checked_text);
        allCheckedImage.setOnClickListener(this);
        allCheckedText.setOnClickListener(this);
    }

    /**
     * 设置adapter的点击事件
     *
     * @param adapter
     */
    private void setAdapterClick(final LeavingAlarmAdapter adapter) {
        adapter.setOnLongClickListener(new LeavingAlarmAdapter.LeavingAlarmLongClickListener() {
            @Override
            public void onLongClick(View v) {
                isLongClick = true;
                adapter.setCheckedLayout(true);
                adapter.notifyDataSetChanged();
                top_layout.setVisibility(View.GONE);
                bottom_layout.setVisibility(View.VISIBLE);
            }
        });
        adapter.setOnClickListener(new LeavingAlarmAdapter.LeavingAlarmOnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LeavingAlarmActivity.this, LeavingInfoActivity.class);
                startActivity(intent);
            }
        });

    }

    /**
     * 点击事件监听
     *
     * @param v
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.leaving_alarm_all_checked_image:
            case R.id.enclosure_all_checked_text:
                allCheckedClick();
                break;

        }
    }

    /**
     * 取消多选状态
     */
    public void cancleLongClick() {
        isLongClick = false;
        adapter.setCheckedLayout(false);
        adapter.notifyDataSetChanged();
        top_layout.setVisibility(View.VISIBLE);
        bottom_layout.setVisibility(View.GONE);
    }

    /**
     * 按键事件监听
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //判断递送箱列表是否是多选状态
            if (isLongClick) {
                cancleLongClick();

            } else {
                finish();
            }
        }
        return false;
    }

    /**
     * 处理全选按钮的点击事件
     */
    private void allCheckedClick() {
        //判断当前全选是否是选中状态
        if (isAllChecked) {
            isAllChecked = false;
            allCheckedImage.setImageResource(R.mipmap.unchecked);
            List<LeavingAlarm> leavingAlarms = adapter.getLeavingAlarms();
            for (LeavingAlarm leavingAlarm : leavingAlarms) {
                leavingAlarm.setImgIsChecked(false);
            }
            adapter.notifyDataSetChanged();


        } else {
            isAllChecked = true;
            allCheckedImage.setImageResource(R.mipmap.checked);
            List<LeavingAlarm> leavingAlarms = adapter.getLeavingAlarms();
            for (LeavingAlarm leavingAlarm : leavingAlarms) {
                leavingAlarm.setImgIsChecked(true);
            }
            adapter.notifyDataSetChanged();

        }

    }


}
