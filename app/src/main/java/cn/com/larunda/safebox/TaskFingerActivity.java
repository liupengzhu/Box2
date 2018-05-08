package cn.com.larunda.safebox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.larunda.safebox.R;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.com.larunda.safebox.adapter.FingerAdapter;
import cn.com.larunda.safebox.gson.FingerInfo;
import cn.com.larunda.safebox.gson.TaskLogInfo;
import cn.com.larunda.safebox.recycler.Finger;
import cn.com.larunda.safebox.util.ActivityCollector;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class TaskFingerActivity extends AppCompatActivity {

    private TitleBar titleBar;
    private SharedPreferences preferences;
    private String token;
    private int id;

    private SwipeRefreshLayout refreshLayout;
    private RelativeLayout errorLayout;
    private SwipeMenuRecyclerView recyclerView;
    private FingerAdapter adapter;
    private LinearLayoutManager manager;
    private List<Finger> fingerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_finger);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        id = getIntent().getIntExtra("id", 0);
        initView();
        initEvent();
        sendRequest();
    }

    /**
     * 初始化View
     */
    private void initView() {

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = preferences.getString("token", null);

        titleBar = findViewById(R.id.finger_title_bar);
        titleBar.setTextViewText("指纹列表");
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);

        refreshLayout = findViewById(R.id.task_finger_swipe);
        errorLayout = findViewById(R.id.task_finger_error_layout);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sendRequest();

            }
        });

        recyclerView = findViewById(R.id.task_finger_recycler);
        recyclerView.addItemDecoration(new DefaultItemDecoration(getResources()
                .getColor(R.color.line), MATCH_PARENT, 2));
        adapter = new FingerAdapter(this, fingerList);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        recyclerView.useDefaultLoadMore(); // 使用默认的加载更多的View。
        /*recyclerView.setLoadMoreListener(new SwipeMenuRecyclerView.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                sendLoadRequest();
            }
        });*/
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
                Intent intent = new Intent(TaskFingerActivity.this, AddFingerprintActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
    }

    /**
     * 发送网络请求
     */
    private void sendRequest() {
        refreshLayout.setRefreshing(true);
        HttpUtil.sendGetRequestWithHttp(Util.URL + "task/" + id + "/fingerprints"
                + Util.TOKEN + token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        errorLayout.setVisibility(View.VISIBLE);
                        refreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String content = response.body().string();
                int code = response.code();
                if (code == 200 && Util.isGoodJson(content)) {
                    final FingerInfo info = Util.handleFingerInfo("{data :" + content + "}");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            parseInfo(info);
                            errorLayout.setVisibility(View.GONE);
                            refreshLayout.setRefreshing(false);
                        }
                    });
                } else if (code == 401 || code == 412) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(TaskFingerActivity.this, LoginActivity.class);
                            intent.putExtra("token_timeout", "登录超时");
                            preferences.edit().putString("token", null).commit();
                            startActivity(intent);
                            ActivityCollector.finishAllActivity();
                        }
                    });
                } else if (code == 422) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject js = new JSONObject(content);
                                Toast.makeText(TaskFingerActivity.this, js.get("message") + "", Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            refreshLayout.setRefreshing(false);
                        }
                    });

                }
            }
        });
    }

    /**
     * 解析服务器返回数据
     *
     * @param info
     */
    private void parseInfo(FingerInfo info) {
        fingerList.clear();
        if (info.getData() != null) {
            for (FingerInfo.DataBean dataBean : info.getData()) {
                Finger finger = new Finger();
                finger.setId(dataBean.getId());
                finger.setCode(dataBean.getF_fingerprint());
                if (dataBean.getUser() != null) {
                    finger.setName(dataBean.getUser().getF_name());
                }
                fingerList.add(finger);
            }
        }
        recyclerView.loadMoreFinish(info.getData().size() == 0, false);
        adapter.notifyDataSetChanged();
    }

}
