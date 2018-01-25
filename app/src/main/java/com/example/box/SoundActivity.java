package com.example.box;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.box.adapter.SoundInfoAdapter;
import com.example.box.recycler.SoundInfo;
import com.example.titlebar.TitleBar;
import com.example.titlebar.TitleListener;

import java.util.ArrayList;
import java.util.List;

public class SoundActivity extends AppCompatActivity implements View.OnClickListener {

    private TitleBar titleBar;
    private SoundInfoAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private List<SoundInfo> soundInfoList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound);
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
     * 测试方法
     */
    private void initData() {
        for (int i = 0; i < 20; i++) {
            SoundInfo soundInfo = new SoundInfo("XXX000000" + i, "共" + i + "条录音");
            soundInfoList.add(soundInfo);
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
        adapter.setSoundInfoOnClickListener(new SoundInfoAdapter.SoundInfoOnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SoundActivity.this, DetailedSoundActivity.class);
                startActivity(intent);

            }
        });
    }

    private void initView() {

        titleBar = findViewById(R.id.sound_title_bar);
        titleBar.setTextViewText("录音列表");
        titleBar.setRightButtonSrc(0);
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);

        adapter = new SoundInfoAdapter(this, soundInfoList);
        recyclerView = findViewById(R.id.sound_recycler);
        manager = new LinearLayoutManager(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

    }

    /**
     * 点击事件拦截
     *
     * @param v
     */
    @Override
    public void onClick(View v) {


    }
}
