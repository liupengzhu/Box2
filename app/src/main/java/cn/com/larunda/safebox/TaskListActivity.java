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
import android.widget.Toast;

import com.android.tu.loadingdialog.LoadingDailog;
import com.larunda.safebox.R;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.com.larunda.safebox.adapter.TaskAdapter;
import cn.com.larunda.safebox.gson.BoxInfo;
import cn.com.larunda.safebox.gson.TaskInfo;
import cn.com.larunda.safebox.recycler.Task;
import cn.com.larunda.safebox.util.ActivityCollector;
import cn.com.larunda.safebox.util.BaseActivity;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class TaskListActivity extends BaseActivity {
    private int id;
    private TitleBar titleBar;
    private SharedPreferences preferences;
    private String token;

    private boolean isCompleted = true;
    private String status;

    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private TaskAdapter adapter;
    private List<Task> taskList = new ArrayList<>();
    private int maxPage;
    private int page;
    private final int ADD_REQUEST = 1;
    private final int FINISH_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        id = getIntent().getIntExtra("id", 0);
        status = getIntent().getStringExtra("status");
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

        titleBar = findViewById(R.id.task_list_title_bar);
        titleBar.setTextViewText("任务列表");
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);

        recyclerView = findViewById(R.id.task_list_recycler);
        adapter = new TaskAdapter(this, taskList);
        manager = new LinearLayoutManager(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);
    }

    /**
     * 初始化点击事件
     */
    private void initEvent() {

        adapter.setItemOnclickListener(new TaskAdapter.ItemOnclickListener() {
            @Override
            public void onClick(View v, int id, String name, String createTime, String completedTime) {
                Intent intent = new Intent(TaskListActivity.this, TaskDetailActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("name", name);
                intent.putExtra("createTime", createTime);
                intent.putExtra("completedTime", completedTime);
                startActivityForResult(intent, FINISH_REQUEST);
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
                if (!isCompleted) {
                    Toast.makeText(TaskListActivity.this, "当前任务未完成!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(TaskListActivity.this, AddTaskActivity.class);
                    intent.putExtra("id", id);
                    startActivityForResult(intent, ADD_REQUEST);
                }
            }
        });
    }

    /**
     * 发送网络请求
     */
    private void sendRequest() {
        HttpUtil.sendGetRequestWithHttp(Util.URL + "box/" + id + "/tasks" + Util.TOKEN + token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String content = response.body().string();
                int code = response.code();
                if (code == 200 && Util.isGoodJson(content)) {
                    final TaskInfo info = Util.handleTaskInfo(content);
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
                            Intent intent = new Intent(TaskListActivity.this, LoginActivity.class);
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
                                Toast.makeText(TaskListActivity.this, js.get("message") + "", Toast.LENGTH_SHORT).show();
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
    private void parseInfo(TaskInfo info) {
        page = info.getCurrent_page() + 1;
        maxPage = info.getLast_page();
        taskList.clear();
        if (info.getData() != null) {
            for (TaskInfo.DataBean dataBean : info.getData()) {
                if (dataBean.getCompleted_at() == null) {
                    isCompleted = false;
                    break;
                }
            }
            for (TaskInfo.DataBean dataBean : info.getData()) {
                Task task = new Task();
                task.setId(dataBean.getId());
                task.setCreatedTime(dataBean.getCreated_at());
                task.setCompletedTime(dataBean.getCompleted_at());
                task.setOriginCity(dataBean.getF_origin_city());
                task.setDestinationCity(dataBean.getF_destination_city());
                task.setName(dataBean.getF_name());
                taskList.add(task);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ADD_REQUEST:
                if (resultCode == RESULT_OK) {
                    sendRequest();
                }
                break;
            case FINISH_REQUEST:
                if (resultCode == RESULT_OK) {
                    sendRequest();
                }
                break;
        }
    }
}
