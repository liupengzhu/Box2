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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.larunda.safebox.R;
import com.larunda.selfdialog.DateDialog;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.com.larunda.safebox.adapter.TaskLogAdapter;
import cn.com.larunda.safebox.adapter.TaskWarningAdapter;
import cn.com.larunda.safebox.gson.Alarm;
import cn.com.larunda.safebox.gson.TaskLogInfo;
import cn.com.larunda.safebox.gson.TaskWarningInfo;
import cn.com.larunda.safebox.recycler.TaskLog;
import cn.com.larunda.safebox.recycler.TaskWarning;
import cn.com.larunda.safebox.util.ActivityCollector;
import cn.com.larunda.safebox.util.AlarmType;
import cn.com.larunda.safebox.util.BaseActivity;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class TaskWarningActivity extends BaseActivity implements View.OnClickListener {

    private final String URL = Util.URL + "box/alarm" + Util.TOKEN;
    private TitleBar titleBar;
    private int id;
    private SharedPreferences preferences;
    private String token;

    private SwipeRefreshLayout refreshLayout;
    private RelativeLayout errorLayout;
    private SwipeMenuRecyclerView recyclerView;
    private LinearLayoutManager manager;
    private TaskWarningAdapter adapter;
    private List<TaskWarning> taskWarningList = new ArrayList<>();

    private EditText searchText;
    private ImageView cancelButton;
    private TextView ensureButton;
    private String search = "";
    private DateDialog dateDialog;
    private int maxPage;
    private int page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_warning);
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

        titleBar = findViewById(R.id.warning_title_bar);
        titleBar.setTextViewText("警报列表");
        titleBar.setRightButtonSrc(0);
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);

        refreshLayout = findViewById(R.id.task_warning_swipe);
        errorLayout = findViewById(R.id.task_warning_error_layout);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                search = "";
                sendRequest();

            }
        });

        recyclerView = findViewById(R.id.task_warning_recycler);
        recyclerView.addItemDecoration(new DefaultItemDecoration(getResources()
                .getColor(R.color.line), MATCH_PARENT, 2));
        manager = new LinearLayoutManager(this);
        adapter = new TaskWarningAdapter(this, taskWarningList);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        recyclerView.useDefaultLoadMore(); // 使用默认的加载更多的View。
        recyclerView.setLoadMoreListener(new SwipeMenuRecyclerView.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                sendLoadRequest();
            }
        });

        searchText = findViewById(R.id.warning_search_edit);
        cancelButton = findViewById(R.id.warning_cancel_button);
        ensureButton = findViewById(R.id.warning_ensure_button);
        dateDialog = new DateDialog(this);
    }

    /**
     *
     */
    private void initEvent() {

        cancelButton.setOnClickListener(this);
        ensureButton.setOnClickListener(this);
        searchText.setOnClickListener(this);

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

        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                search = searchText.getText().toString().trim();
                sendRequest();
                return false;
            }
        });
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    if (cancelButton != null) {
                        cancelButton.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (cancelButton != null) {
                        cancelButton.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        dateDialog.setOnCancelClickListener(new DateDialog.OnCancelClickListener() {
            @Override
            public void OnClick(View view) {
                dateDialog.cancel();
            }
        });
        dateDialog.setOnOkClickListener(new DateDialog.OnOkClickListener() {
            @Override
            public void OnClick(View view, String date) {
                searchText.setText(date + "");
                dateDialog.cancel();
            }
        });
    }

    /**
     * 点击事件处理
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.warning_cancel_button:
                if (searchText != null) {
                    searchText.setText("");
                }
                break;
            case R.id.warning_ensure_button:
                if (searchText != null) {
                    search = searchText.getText().toString().trim();
                    sendRequest();
                }
                break;
            case R.id.warning_search_edit:
                dateDialog.show();
                break;
            default:
                break;
        }
    }

    /**
     * 发送网络请求
     */
    private void sendRequest() {
        refreshLayout.setRefreshing(true);
        HttpUtil.sendGetRequestWithHttp(Util.URL + "task/" + id + "/alarms" + Util.TOKEN + token
                + "&time=" + search + "&page=1", new Callback() {
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
                    final TaskWarningInfo info = Util.handleTaskWarningInfo(content);
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
                            Intent intent = new Intent(TaskWarningActivity.this, LoginActivity.class);
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
                                Toast.makeText(TaskWarningActivity.this, js.get("message") + "", Toast.LENGTH_SHORT).show();
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
     * 解析服务器返回信息
     *
     * @param info
     */
    private void parseInfo(TaskWarningInfo info) {

        page = info.getCurrent_page() + 1;
        maxPage = info.getLast_page();
        taskWarningList.clear();
        if (info.getData() != null) {
            for (TaskWarningInfo.DataBean dataBean : info.getData()) {
                TaskWarning taskWarning = new TaskWarning();
                taskWarning.setContent(dataBean.getF_content());
                taskWarning.setProcess(dataBean.getProcess().getF_origin_city() + " - - "
                        + dataBean.getProcess().getF_destination_city());
                taskWarning.setTime(dataBean.getCreated_at());
                taskWarning.setTitle(AlarmType.getName(dataBean.getF_type()));
                if (dataBean.getF_is_fixed() != null) {
                    if (dataBean.getF_is_fixed().equals("1")) {
                        taskWarning.setStatus("是");
                    } else {
                        taskWarning.setStatus("否");
                    }
                }
                taskWarningList.add(taskWarning);
            }
        }
        recyclerView.loadMoreFinish(info.getData().size() == 0, maxPage >= page);
        adapter.notifyDataSetChanged();
    }
    /**
     * 发送网络请求
     */
    private void sendLoadRequest() {
        HttpUtil.sendGetRequestWithHttp(Util.URL + "task/" + id + "/alarms" + Util.TOKEN + token
                + "&time=" + search + "&page="+page, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        errorLayout.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String content = response.body().string();
                int code = response.code();
                if (code == 200 && Util.isGoodJson(content)) {
                    final TaskWarningInfo info = Util.handleTaskWarningInfo(content);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            parseLoadInfo(info);
                        }
                    });
                } else if (code == 401 || code == 412) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(TaskWarningActivity.this, LoginActivity.class);
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
                                Toast.makeText(TaskWarningActivity.this, js.get("message") + "", Toast.LENGTH_SHORT).show();
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
     * 解析服务器返回信息
     *
     * @param info
     */
    private void parseLoadInfo(TaskWarningInfo info) {

        page = info.getCurrent_page() + 1;
        maxPage = info.getLast_page();
        if (info.getData() != null) {
            for (TaskWarningInfo.DataBean dataBean : info.getData()) {
                TaskWarning taskWarning = new TaskWarning();
                taskWarning.setContent(dataBean.getF_content());
                taskWarning.setProcess(dataBean.getProcess().getF_origin_city() + " - - "
                        + dataBean.getProcess().getF_destination_city());
                taskWarning.setTime(dataBean.getCreated_at());
                taskWarning.setTitle(AlarmType.getName(dataBean.getF_type()));
                if (dataBean.getF_is_fixed() != null) {
                    if (dataBean.getF_is_fixed().equals("1")) {
                        taskWarning.setStatus("是");
                    } else {
                        taskWarning.setStatus("否");
                    }
                }
                taskWarningList.add(taskWarning);
            }
        }
        recyclerView.loadMoreFinish(info.getData().size() == 0, maxPage >= page);
        adapter.notifyDataSetChanged();
    }
}