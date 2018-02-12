package cn.com.larunda.safebox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
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
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.com.larunda.safebox.adapter.BoxInitAdapter;
import cn.com.larunda.safebox.gson.BoxInitData;
import cn.com.larunda.safebox.gson.BoxInitInfo;
import cn.com.larunda.safebox.recycler.BoxInit;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class BoxInitActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences preferences;
    private String token;
    private TitleBar titleBar;

    private BoxInitAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private List<BoxInit> boxInitList = new ArrayList<>();

    private SwipeRefreshLayout refreshLayout;
    private RelativeLayout loodingErrorLayout;
    private ImageView loodingLayout;

    private EditText searchText;
    private ImageView cancelButton;
    private TextView ensureButton;
    public static final String INIT_URL = Util.URL + "box/add_box_lists" + Util.TOKEN;
    private String search;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box_init);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        initView();
        initEvent();
        //每次创建时还没有网络数据 设置载入背景为可见
        loodingLayout.setVisibility(View.VISIBLE);
        loodingErrorLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        search = null;
        sendRequest();
    }

    /**
     * 发送网络请求
     */
    private void sendRequest() {
        String searchText;
        if (search != null) {
            searchText = "&search=" + search;
        } else {
            searchText = "";
        }
        refreshLayout.setRefreshing(true);
        HttpUtil.sendGetRequestWithHttp(INIT_URL + token + searchText, new Callback() {
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
                final String content = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (Util.isGoodJson(content)) {
                            BoxInitInfo boxInitInfo = Util.handleBoxInitInfo(content);
                            if (boxInitInfo != null && boxInitInfo.error == null) {
                                initData(boxInitInfo);
                                loodingErrorLayout.setVisibility(View.GONE);
                                loodingLayout.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                refreshLayout.setRefreshing(false);
                            } else {
                                Intent intent = new Intent(BoxInitActivity.this, LoginActivity.class);
                                intent.putExtra("token_timeout", "登录超时");
                                preferences.edit().putString("token", null).commit();
                                startActivity(intent);
                                finish();
                            }


                        } else {
                            refreshLayout.setRefreshing(false);
                            Toast.makeText(BoxInitActivity.this, "服务器异常", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    /**
     * 解析数据
     *
     * @param boxInitInfo
     */
    private void initData(BoxInitInfo boxInitInfo) {
        boxInitList.clear();
        if (boxInitInfo.boxInitDataList != null) {
            for (BoxInitData boxInitData : boxInitInfo.boxInitDataList) {
                BoxInit boxInit = new BoxInit();
                if (boxInitData.code != null) {
                    boxInit.setCode(boxInitData.code);
                } else {
                    boxInit.setCode("");
                }
                if (boxInitData.created_at != null) {
                    boxInit.setTime(boxInitData.created_at);
                } else {
                    boxInit.setTime("");
                }
                if (boxInitData.id != null) {
                    boxInit.setId(boxInitData.id);
                } else {
                    boxInit.setId("");
                }
                boxInitList.add(boxInit);
            }

        }
        adapter.notifyDataSetChanged();
        if (boxInitList.size() == 0) {
            Toast.makeText(BoxInitActivity.this, "暂无未初始化递送箱", Toast.LENGTH_SHORT).show();
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
        cancelButton.setOnClickListener(this);
        ensureButton.setOnClickListener(this);


        adapter.setBoxInitAdapterOnClickListener(new BoxInitAdapter.BoxInitAdapterOnClickListener() {
            @Override
            public void onClick(View v, String id) {
                if (!TextUtils.isEmpty(id)) {
                    Intent intent = new Intent(BoxInitActivity.this, BoxAddActivity.class);
                    intent.putExtra("id", id);
                    startActivity(intent);
                }
            }
        });

        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                search = searchText.getText().toString().trim();
                sendRequest();
                return true;
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
    }

    /**
     * 初始化view
     */
    private void initView() {
        preferences = PreferenceManager.getDefaultSharedPreferences(BoxInitActivity.this);
        token = preferences.getString("token", null);

        refreshLayout = findViewById(R.id.box_init_swiper);
        loodingErrorLayout = findViewById(R.id.box_init_loading_error_layout);
        loodingLayout = findViewById(R.id.box_init_loading_layout);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                search = null;
                sendRequest();

            }
        });

        titleBar = findViewById(R.id.box_init_title_bar);
        titleBar.setTextViewText("递送箱初始化");
        titleBar.setRightButtonSrc(0);
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);

        searchText = findViewById(R.id.box_init_serch_edit);
        cancelButton = findViewById(R.id.box_init_cancel_button);
        ensureButton = findViewById(R.id.box_init_ensure_button);

        adapter = new BoxInitAdapter(this, boxInitList);
        recyclerView = findViewById(R.id.box_init_recycler);
        manager = new LinearLayoutManager(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

    }

    /**
     * 点击事件监听
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.box_init_cancel_button:
                if (searchText != null) {
                    searchText.setText("");
                }
                break;
            case R.id.box_init_ensure_button:
                if (searchText != null) {
                    search = searchText.getText().toString().trim();
                    sendRequest();
                }
                break;

            default:
                break;
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        sendRequest();
    }
}
