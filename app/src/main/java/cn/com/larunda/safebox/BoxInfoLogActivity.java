package cn.com.larunda.safebox;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.larunda.safebox.R;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.com.larunda.safebox.adapter.BoxInfoLogAdapter;
import cn.com.larunda.safebox.gson.BoxInfoLogInfo;
import cn.com.larunda.safebox.recycler.BoxInfoLog;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class BoxInfoLogActivity extends AppCompatActivity {

    private TitleBar titleBar;

    private RecyclerView recyclerView;
    private BoxInfoLogAdapter adapter;
    private LinearLayoutManager manager;
    private List<BoxInfoLog> boxInfoLogList = new ArrayList<>();
    public static final String BOX_LOG_URL = "http://safebox.dsmcase.com:90/api/box/log?_token=";
    private String id;
    private SharedPreferences preferences;
    private String token;

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
        id = getIntent().getStringExtra("id");
        initView();
        initEvent();
        sendRequest();
    }

    /**
     * 发送网络请求
     */
    private void sendRequest() {
        HttpUtil.sendGetRequestWithHttp(BOX_LOG_URL + token + "&id=" + id, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String content = response.body().string();
                final BoxInfoLogInfo boxInfoLogInfo = Util.handleBoxInfoLogInfo(content);
                if (boxInfoLogInfo != null && boxInfoLogInfo.getError() == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initData(boxInfoLogInfo);
                        }
                    });
                }
            }
        });
    }

    /**
     * 解析数据
     *
     * @param boxInfoLogInfo
     */
    private void initData(BoxInfoLogInfo boxInfoLogInfo) {
        boxInfoLogList.clear();
        if (boxInfoLogInfo.getData() != null) {
            for (BoxInfoLogInfo.DataBean dataBean : boxInfoLogInfo.getData()) {
                BoxInfoLog boxInfoLog = new BoxInfoLog();
                if (dataBean.getCreated_at() != null) {
                    boxInfoLog.setLogTime(dataBean.getCreated_at());
                } else {
                    boxInfoLog.setLogTime("");
                }
                if (dataBean.getF_info() != null) {
                    boxInfoLog.setLogContent(dataBean.getF_info());
                } else {
                    boxInfoLog.setLogContent("");
                }
                if (dataBean.getF_title() != null) {
                    boxInfoLog.setLogName(dataBean.getF_title());
                } else {
                    boxInfoLog.setLogName("");
                }
                boxInfoLogList.add(boxInfoLog);
            }
        }
        adapter.notifyDataSetChanged();
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
     * 初始化View
     */
    private void initView() {
        preferences = PreferenceManager.getDefaultSharedPreferences(BoxInfoLogActivity.this);
        token = preferences.getString("token", null);

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
