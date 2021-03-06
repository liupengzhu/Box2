package cn.com.larunda.safebox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.larunda.safebox.R;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.com.larunda.safebox.adapter.BoxInfoLogAdapter;
import cn.com.larunda.safebox.adapter.FootAdapter;
import cn.com.larunda.safebox.gson.BoxInfoLogInfo;
import cn.com.larunda.safebox.recycler.BoxInfoLog;
import cn.com.larunda.safebox.util.ActivityCollector;
import cn.com.larunda.safebox.util.BaseActivity;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class BoxInfoLogActivity extends BaseActivity implements View.OnClickListener {

    private TitleBar titleBar;

    private RecyclerView recyclerView;
    private BoxInfoLogAdapter adapter;
    private LinearLayoutManager manager;
    private List<BoxInfoLog> boxInfoLogList = new ArrayList<>();
    public static final String BOX_LOG_URL = Util.URL + "box/log" + Util.TOKEN;
    private String id;
    private SharedPreferences preferences;
    private String token;

    private SwipeRefreshLayout refreshLayout;
    private RelativeLayout loodingErrorLayout;
    private ImageView loodingLayout;

    private RadioButton weekButton;
    private RadioButton monthButton;
    private RadioButton yearButton;
    private RadioButton allButton;
    private String date;
    private SimpleDateFormat format;
    private Calendar c;

    private int page;
    private int lastVisibleItem;
    private int count;
    private FootAdapter footAdapter;
    private int total;

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
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                date = null;
                allButton.setChecked(true);
                sendRequest();

            }
        });

        //每次创建时还没有网络数据 设置载入背景为可见
        loodingLayout.setVisibility(View.VISIBLE);
        loodingErrorLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        date = null;
        sendRequest();
    }

    /**
     * 发送网络请求
     */
    private void sendRequest() {
        String timeText;
        if (date != null) {
            timeText = "&type=app&time=" + date;
        } else {
            timeText = "";
        }
        refreshLayout.setRefreshing(true);
        HttpUtil.sendGetRequestWithHttp(BOX_LOG_URL + token + "&page=1" + "&id=" + id + timeText + Util.TYPE, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                        loodingErrorLayout.setVisibility(View.VISIBLE);
                        loodingLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String content = response.body().string();
                if (Util.isGoodJson(content)) {
                    final BoxInfoLogInfo boxInfoLogInfo = Util.handleBoxInfoLogInfo(content);
                    if (boxInfoLogInfo != null && boxInfoLogInfo.getError() == null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initData(boxInfoLogInfo);
                                refreshLayout.setRefreshing(false);
                                loodingErrorLayout.setVisibility(View.GONE);
                                loodingLayout.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(BoxInfoLogActivity.this, LoginActivity.class);
                                intent.putExtra("token_timeout", "登录超时");
                                preferences.edit().putString("token", null).commit();
                                startActivity(intent);
                                ActivityCollector.finishAllActivity();
                            }
                        });
                    }
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

        page = boxInfoLogInfo.current_page + 1;
        count = boxInfoLogInfo.per_page;
        total = boxInfoLogInfo.last_page;
        if (boxInfoLogInfo.getData().size() == 0 || boxInfoLogInfo.getData().size() < count) {
            footAdapter.setHasMore(false);
        }
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
        footAdapter.notifyDataSetChanged();
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
        weekButton.setOnClickListener(this);
        monthButton.setOnClickListener(this);
        yearButton.setOnClickListener(this);
        allButton.setOnClickListener(this);
    }


    /**
     * 初始化View
     */
    private void initView() {
        preferences = PreferenceManager.getDefaultSharedPreferences(BoxInfoLogActivity.this);
        token = preferences.getString("token", null);

        weekButton = findViewById(R.id.box_info_log_search_week);
        monthButton = findViewById(R.id.box_info_log_search_month);
        yearButton = findViewById(R.id.box_info_log_search_year);
        allButton = findViewById(R.id.box_info_log_search_all);

        refreshLayout = findViewById(R.id.box_info_log_swipe);
        loodingErrorLayout = findViewById(R.id.box_info_log_loading_error_layout);
        loodingLayout = findViewById(R.id.box_info_log_loading_layout);

        recyclerView = findViewById(R.id.box_info_log_recycler);
        adapter = new BoxInfoLogAdapter(this, boxInfoLogList);
        footAdapter = new FootAdapter(this, adapter);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(footAdapter);


        titleBar = findViewById(R.id.box_info_log_title_bar);
        titleBar.setTextViewText("");
        titleBar.setRightButtonSrc(0);
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (page <= total) {
                    //在newState为滑到底部时
                    if (lastVisibleItem + 1 == footAdapter.getItemCount()) {
                        if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                            footAdapter.setHasMore(true);
                            footAdapter.notifyDataSetChanged();
                        }
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            if (boxInfoLogList.size() < count) {
                                footAdapter.setHasMore(true);
                                sendRequest();
                            } else {
                                footAdapter.setHasMore(true);
                                sendAddRequest();
                            }
                        }
                    }

                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = manager.findLastVisibleItemPosition();
            }
        });


    }

    /**
     * 请求下一页数据
     */
    private void sendAddRequest() {
        String timeText;
        if (date != null) {
            timeText = "&type=app&time=" + date;
        } else {
            timeText = "";
        }
        refreshLayout.setRefreshing(true);
        HttpUtil.sendGetRequestWithHttp(BOX_LOG_URL + token + "&page=" + page + "&id=" + id + timeText + Util.TYPE, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                        loodingErrorLayout.setVisibility(View.VISIBLE);
                        loodingLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String content = response.body().string();
                if (Util.isGoodJson(content)) {
                    final BoxInfoLogInfo boxInfoLogInfo = Util.handleBoxInfoLogInfo(content);
                    if (boxInfoLogInfo != null && boxInfoLogInfo.getError() == null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addData(boxInfoLogInfo);
                                refreshLayout.setRefreshing(false);
                                loodingErrorLayout.setVisibility(View.GONE);
                                loodingLayout.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(BoxInfoLogActivity.this, LoginActivity.class);
                                intent.putExtra("token_timeout", "登录超时");
                                preferences.edit().putString("token", null).commit();
                                startActivity(intent);
                                ActivityCollector.finishAllActivity();
                            }
                        });
                    }
                }
            }
        });
    }


    private void addData(BoxInfoLogInfo boxInfoLogInfo) {

        page = boxInfoLogInfo.current_page + 1;
        if (boxInfoLogInfo.getData().size() == 0 || boxInfoLogInfo.getData().size() < count) {
            footAdapter.setHasMore(false);
        }
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
        footAdapter.notifyDataSetChanged();
    }

    /**
     * 点击事件监听
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.box_info_log_search_week:
                format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                c = Calendar.getInstance();
                c.add(Calendar.DATE, -7);
                date = format.format(c.getTime());
                sendRequest();
                break;
            case R.id.box_info_log_search_month:
                format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                c = Calendar.getInstance();
                c.add(Calendar.MONTH, -1);
                date = format.format(c.getTime());
                sendRequest();
                break;
            case R.id.box_info_log_search_year:
                format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                c = Calendar.getInstance();
                c.add(Calendar.YEAR, -1);
                date = format.format(c.getTime());
                sendRequest();
                break;
            case R.id.box_info_log_search_all:
                date = null;
                sendRequest();
                break;
            default:
                break;
        }
    }


}
