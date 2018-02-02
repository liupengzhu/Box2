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
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.larunda.safebox.R;

import cn.com.larunda.safebox.adapter.EnclosureAdapter;
import cn.com.larunda.safebox.gson.EnclosureData;
import cn.com.larunda.safebox.gson.EnclosureInfo;
import cn.com.larunda.safebox.recycler.Enclosure;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EnclosureActivity extends AppCompatActivity implements View.OnClickListener {

    TitleBar titleBar;
    EnclosureAdapter adapter;
    RecyclerView recyclerView;

    SwipeRefreshLayout refreshLayout;
    private RelativeLayout loodingErrorLayout;
    private ImageView loodingLayout;

    LinearLayoutManager manager;
    List<Enclosure> enclosureList = new ArrayList<>();
    private RelativeLayout top_layout;
    private LinearLayout bottom_layout;

    private EditText searchText;
    private ImageView cancelButton;
    private TextView ensureButton;

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
    public static final String ENCLOSURE_URL = "http://safebox.dsmcase.com:90/api/area?_token=";
    private SharedPreferences preferences;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enclosure);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        initView();
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sendRequest();

            }
        });

        //每次fragment创建时还没有网络数据 设置载入背景为可见
        loodingLayout.setVisibility(View.VISIBLE);
        loodingErrorLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        sendRequest();


    }

    /**
     * 发送网络请求
     */
    private void sendRequest() {
        refreshLayout.setRefreshing(true);
        HttpUtil.sendGetRequestWithHttp(ENCLOSURE_URL + token, new Callback() {
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
                final EnclosureInfo enclosureInfo = Util.handleEnclosureInfo(response.body().string());
                if (enclosureInfo != null && enclosureInfo.error == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initEnclosureInfo(enclosureInfo);
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
                            Intent intent = new Intent(EnclosureActivity.this, LoginActivity.class);
                            intent.putExtra("token_timeout", "登录超时");
                            preferences.edit().putString("token", null).commit();
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }
        });
    }

    /**
     * 解析地理围栏信息
     */
    private void initEnclosureInfo(EnclosureInfo enclosureInfo) {
        enclosureList.clear();
        if (enclosureList != null) {
            for (EnclosureData enclosureData : enclosureInfo.enclosureDataList) {
                Enclosure enclosure = new Enclosure();
                enclosure.setId(enclosureData.id);
                if (enclosureData.name != null) {
                    enclosure.setName(enclosureData.name);
                } else {
                    enclosure.setName("");
                }
                enclosureList.add(enclosure);
            }
        }
        if (enclosureList.size() == 0) {
            Toast.makeText(this, "地理围栏不存在", Toast.LENGTH_SHORT).show();
        }
        adapter.notifyDataSetChanged();
    }


    /**
     * 初始化View
     */
    private void initView() {

        preferences = PreferenceManager.getDefaultSharedPreferences(EnclosureActivity.this);
        token = preferences.getString("token", null);
        titleBar = findViewById(R.id.enclosure_title_bar);
        titleBar.setTextViewText("地理围栏");
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setRightButtonSrc(0);
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

        searchText = findViewById(R.id.enclosure_serch_edit);
        cancelButton = findViewById(R.id.enclosure_cancel_button);
        ensureButton = findViewById(R.id.enclosure_ensure_button);

        top_layout = findViewById(R.id.enclosure_top_layout);
        bottom_layout = findViewById(R.id.enclosure_bottom_layout);


        allCheckedImage = findViewById(R.id.enclosure_all_checked_image);
        allCheckedText = findViewById(R.id.enclosure_all_checked_text);

        adapter = new EnclosureAdapter(enclosureList);
        recyclerView = findViewById(R.id.enclosure_recycler);
        refreshLayout = findViewById(R.id.enclosure_swiper);
        loodingErrorLayout = findViewById(R.id.enclosure_loading_error_layout);
        loodingLayout = findViewById(R.id.enclosure_loading_layout);

        manager = new LinearLayoutManager(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        setAdapterClick(adapter);
        allCheckedImage.setOnClickListener(this);
        allCheckedText.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        ensureButton.setOnClickListener(this);
    }

    /**
     * 设置adapter的点击事件
     *
     * @param adapter
     */
    private void setAdapterClick(final EnclosureAdapter adapter) {
        adapter.setOnLongClickListener(new EnclosureAdapter.EnclosureLongClickListener() {
            @Override
            public void onLongClick(View v) {
                isLongClick = true;
                adapter.setCheckedLayout(true);
                adapter.notifyDataSetChanged();
                top_layout.setVisibility(View.GONE);
                bottom_layout.setVisibility(View.VISIBLE);
            }
        });

        adapter.setEnclosureOnClickListener(new EnclosureAdapter.EnclosureOnClickListener() {
            @Override
            public void onClick(View v, String id) {
                Intent intent = new Intent(EnclosureActivity.this, EnclosureInfoActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });


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
            List<Enclosure> enclosures = adapter.getEnclosureList();
            for (Enclosure enclosure : enclosures) {
                enclosure.setImgIsChecked(false);
            }
            adapter.notifyDataSetChanged();


        } else {
            isAllChecked = true;
            allCheckedImage.setImageResource(R.mipmap.checked);
            List<Enclosure> enclosures = adapter.getEnclosureList();
            for (Enclosure enclosure : enclosures) {
                enclosure.setImgIsChecked(true);
            }
            adapter.notifyDataSetChanged();

        }

    }

    /**
     * 点击事件监听
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.enclosure_all_checked_image:
            case R.id.enclosure_all_checked_text:
                allCheckedClick();
                break;
            case R.id.enclosure_cancel_button:
                if (searchText != null) {
                    searchText.setText("");
                }
                break;
            case R.id.enclosure_ensure_button:
                if (searchText != null && !TextUtils.isEmpty(searchText.getText().toString().trim())) {
                    sendSearchRequest(searchText.getText().toString().trim());
                }
                break;
            default:
                break;

        }
    }

    /**
     * 搜索
     *
     * @param name
     */
    private void sendSearchRequest(String name) {

        refreshLayout.setRefreshing(true);
        HttpUtil.sendGetRequestWithHttp(ENCLOSURE_URL + token + "&search=" + name, new Callback() {
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
                final EnclosureInfo enclosureInfo = Util.handleEnclosureInfo(response.body().string());
                if (enclosureInfo != null && enclosureInfo.error == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initEnclosureInfo(enclosureInfo);
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
                            Intent intent = new Intent(EnclosureActivity.this, LoginActivity.class);
                            intent.putExtra("token_timeout", "登录超时");
                            preferences.edit().putString("token", null).commit();
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }
        });
    }
}


