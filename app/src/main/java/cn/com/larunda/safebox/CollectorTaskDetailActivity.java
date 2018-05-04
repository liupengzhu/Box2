package cn.com.larunda.safebox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.larunda.safebox.R;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.com.larunda.safebox.adapter.CollectorTaskDestinationAdapter;
import cn.com.larunda.safebox.gson.CollectorTaskDetailInfo;
import cn.com.larunda.safebox.gson.TaskInfo;
import cn.com.larunda.safebox.recycler.Destination;
import cn.com.larunda.safebox.util.ActivityCollector;
import cn.com.larunda.safebox.util.BaseActivity;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CollectorTaskDetailActivity extends BaseActivity {

    private int id;
    private TitleBar titleBar;
    private SharedPreferences preferences;
    private String token;

    private TextView name;
    private TextView status;
    private TextView startTime;
    private TextView endTime;

    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private CollectorTaskDestinationAdapter adapter;
    private List<Destination> destinationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collector_task_detail);
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
     * 初始化view
     */
    private void initView() {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = preferences.getString("token", null);

        titleBar = findViewById(R.id.collector_task_detail_title_bar);
        titleBar.setTextViewText("任务详情");
        titleBar.setRightButtonSrc(0);
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);

        name = findViewById(R.id.collector_task_detail_name);
        status = findViewById(R.id.collector_task_detail_task_status);
        startTime = findViewById(R.id.collector_task_detail_start_time);
        endTime = findViewById(R.id.collector_task_detail_end_time);

        recyclerView = findViewById(R.id.collector_task_detail_recycler);
        adapter = new CollectorTaskDestinationAdapter(this, destinationList);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }

    /**
     * 初始化点击事件
     */
    private void initEvent() {

        adapter.setItemButtonOnclickListener(new CollectorTaskDestinationAdapter.ItemButtonOnclickListener() {
            @Override
            public void onClick(View v, int processId) {

            }
        });

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
     * 发送网络请求
     */
    private void sendRequest() {
        HttpUtil.sendGetRequestWithHttp(Util.URL + "user/task/" + id + Util.TOKEN + token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String content = response.body().string();
                int code = response.code();
                if (code == 200 && Util.isGoodJson(content)) {
                    final CollectorTaskDetailInfo info = Util.handleCollectorTaskDetailInfo(content);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            parseInfo(info);
                        }
                    });
                } else if (code == 401 || code == 412) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(CollectorTaskDetailActivity.this, LoginActivity.class);
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
                                Toast.makeText(CollectorTaskDetailActivity.this, js.get("message") + "", Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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
    private void parseInfo(CollectorTaskDetailInfo info) {
        destinationList.clear();
        StringBuffer users = new StringBuffer();
        StringBuffer destinationCity = new StringBuffer();
        StringBuffer originCity = new StringBuffer();
        if (info != null) {
            name.setText(info.getF_name() != null ? info.getF_name() : "");
            status.setText(info.getCompleted_at() != null ? "已完成" : "运送中");
            status.setBackground(getResources().getDrawable(info.getCompleted_at() != null ?
                    R.drawable.text_gray : R.drawable.text_green));
            startTime.setText(info.getCreated_at() != null ? info.getCreated_at() : "");
            endTime.setText(info.getCompleted_at() != null ? info.getCompleted_at() : "");
            if (info.getProcesses() != null) {
                for (CollectorTaskDetailInfo.ProcessesBean processesBean : info.getProcesses()) {
                    Destination destination = new Destination();
                    users.setLength(0);
                    destinationCity.setLength(0);
                    originCity.setLength(0);
                    for (int i = 0; i < processesBean.getAddressee().size(); i++) {
                        users.append(processesBean.getAddressee().get(i).getUser() != null ?
                                processesBean.getAddressee().get(i).getUser().getF_name() + " " : "");
                        destination.setDynamic(processesBean.getAddressee().get(i).getF_is_dynamic());
                    }
                    for (int i = 0; i < processesBean.getF_origin_city().size(); i++) {
                        originCity.append(processesBean.getF_origin_city().get(i) + " ");
                    }
                    for (int i = 0; i < processesBean.getF_destination_city().size(); i++) {
                        destinationCity.append(processesBean.getF_destination_city().get(i) + " ");
                    }
                    destination.setDestinationCity(destinationCity.toString());
                    destination.setOriginCity(originCity.toString());
                    destination.setPerson(users.toString());
                    destination.setStartTime(processesBean.getCreated_at() != null ? processesBean.getCreated_at() :
                            null);
                    destination.setEndTime(processesBean.getCompleted_at() != null ? processesBean.getCompleted_at() :
                            null);
                    destination.setId(processesBean.getId());
                    destinationList.add(destination);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}
