package cn.com.larunda.safebox;

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

import com.larunda.safebox.R;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

import java.util.ArrayList;
import java.util.List;

import cn.com.larunda.safebox.adapter.BoxInfoLogAdapter;
import cn.com.larunda.safebox.recycler.BoxInfoLog;

public class BoxInfoLogActivity extends AppCompatActivity {

    private TitleBar titleBar;

    private RecyclerView recyclerView;
    private BoxInfoLogAdapter adapter;
    private LinearLayoutManager manager;
    private List<BoxInfoLog> boxInfoLogList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box_info_log);
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
    }

    /**
     * 测试方法
     */
    private void initData() {
        for (int i = 0; i < 20; i++) {
            BoxInfoLog log = new BoxInfoLog("日常维护", "张三于2017/11/16对xxxx",
                    "2017-11-13 13:16:17");
            boxInfoLogList.add(log);
        }
    }

    /**
     * 初始化View
     */
    private void initView() {

        recyclerView = findViewById(R.id.box_info_log_recycler);
        adapter = new BoxInfoLogAdapter(this, boxInfoLogList);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);


        titleBar = findViewById(R.id.box_info_log_title_bar);
        titleBar.setTextViewText("");
        titleBar.setRightButtonSrc(0);
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);


    }
}
