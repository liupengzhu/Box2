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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.larunda.safebox.R;

import cn.com.larunda.safebox.adapter.EnclosureAdapter;
import cn.com.larunda.safebox.adapter.FootAdapter;
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

import org.json.JSONException;
import org.json.JSONObject;

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


    private EditText searchText;
    private ImageView cancelButton;
    private TextView ensureButton;

    private Button deleteButton;

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
    private RelativeLayout top_layout;
    private LinearLayout bottom_layout;

    public static final String ENCLOSURE_URL = Util.URL + "area" + Util.TOKEN;
    private SharedPreferences preferences;
    private String token;

    private ArrayList<String> idList = new ArrayList<>();

    private String search;

    private int page;
    private int lastVisibleItem;
    private int count;
    private static FootAdapter footAdapter;

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
        initEvent();
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                search = null;
                sendRequest();

            }
        });

        //每次fragment创建时还没有网络数据 设置载入背景为可见
        loodingLayout.setVisibility(View.VISIBLE);
        loodingErrorLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        search = null;
        sendRequest();


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
        allCheckedImage.setOnClickListener(this);
        allCheckedText.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        ensureButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //在newState为滑到底部时
                if (lastVisibleItem + 1 == footAdapter.getItemCount()) {
                    if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                        footAdapter.setHasMore(true);
                        footAdapter.notifyDataSetChanged();
                    }
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        if (enclosureList.size() < count) {
                            search = null;
                            sendRequest();
                        } else {
                            sendAddRequest();
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
     * 请求下一页
     */
    private void sendAddRequest() {
        String searchText;
        if (search != null) {
            searchText = "&search=" + search;
        } else {
            searchText = "";
        }
        refreshLayout.setRefreshing(true);
        HttpUtil.sendGetRequestWithHttp(ENCLOSURE_URL + token + searchText + "&page =" + page, new Callback() {
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
                            addEnclosureInfo(enclosureInfo);
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
     * 添加地理围栏
     *
     * @param enclosureInfo
     */
    private void addEnclosureInfo(EnclosureInfo enclosureInfo) {
        page = enclosureInfo.current_page + 1;
        if (enclosureInfo.enclosureDataList.size() == 0) {
            footAdapter.setHasMore(false);
        }
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
        footAdapter.notifyDataSetChanged();

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
        HttpUtil.sendGetRequestWithHttp(ENCLOSURE_URL + token + searchText + "&page = 1", new Callback() {
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
        page = enclosureInfo.current_page + 1;
        count = enclosureInfo.per_page;
        if (enclosureInfo.enclosureDataList.size() == 0 || enclosureInfo.enclosureDataList.size() < count) {
            footAdapter.setHasMore(false);
        }
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
        footAdapter.notifyDataSetChanged();
    }


    /**
     * 初始化View
     */
    private void initView() {

        preferences = PreferenceManager.getDefaultSharedPreferences(EnclosureActivity.this);
        token = preferences.getString("token", null);

        deleteButton = findViewById(R.id.enclosure_delete_button);
        titleBar = findViewById(R.id.enclosure_title_bar);
        titleBar.setTextViewText("地理围栏");
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setRightButtonSrc(0);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);


        searchText = findViewById(R.id.enclosure_serch_edit);
        cancelButton = findViewById(R.id.enclosure_cancel_button);
        ensureButton = findViewById(R.id.enclosure_ensure_button);

        top_layout = findViewById(R.id.enclosure_top_layout);
        bottom_layout = findViewById(R.id.enclosure_bottom_layout);


        allCheckedImage = findViewById(R.id.enclosure_all_checked_image);
        allCheckedText = findViewById(R.id.enclosure_all_checked_text);

        adapter = new EnclosureAdapter(enclosureList);
        footAdapter = new FootAdapter(this, adapter);
        recyclerView = findViewById(R.id.enclosure_recycler);
        refreshLayout = findViewById(R.id.enclosure_swiper);
        loodingErrorLayout = findViewById(R.id.enclosure_loading_error_layout);
        loodingLayout = findViewById(R.id.enclosure_loading_layout);

        manager = new LinearLayoutManager(this);
        recyclerView.setAdapter(footAdapter);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        setAdapterClick(adapter);

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
        footAdapter.notifyDataSetChanged();
        top_layout.setVisibility(View.VISIBLE);
        bottom_layout.setVisibility(View.GONE);
    }


    @Override
    public void onBackPressed() {
        //判断递送箱列表是否是多选状态
        if (isLongClick) {
            cancleLongClick();

        } else {
            finish();

        }
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
            footAdapter.notifyDataSetChanged();


        } else {
            isAllChecked = true;
            allCheckedImage.setImageResource(R.mipmap.checked);
            List<Enclosure> enclosures = adapter.getEnclosureList();
            for (Enclosure enclosure : enclosures) {
                enclosure.setImgIsChecked(true);
            }
            footAdapter.notifyDataSetChanged();

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
                    search = searchText.getText().toString().trim();
                    sendRequest();
                } else {
                    Toast.makeText(EnclosureActivity.this, "请输入搜索内容", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.enclosure_delete_button:
                checkIsChecked();
                if (idList.size() == 0) {
                    Toast.makeText(EnclosureActivity.this, "还没有选择地理围栏", Toast.LENGTH_SHORT).show();
                } else {
                    sendDeleteRequest();
                }
                break;
            default:
                break;

        }
    }

    /**
     * 发送删除请求
     */
    private void sendDeleteRequest() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", Util.listToString(idList));
            refreshLayout.setRefreshing(true);
            HttpUtil.sendDeleteWithHttp(ENCLOSURE_URL + token, jsonObject.toString(), new Callback() {
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
                            parseResponse(content);
                        }
                    });
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析返回信息
     *
     * @param content
     */
    private void parseResponse(String content) {
        if (content != null && content.equals("true")) {
            sendRequest();
            Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
        } else if (content != null && content.equals("false")) {
            refreshLayout.setRefreshing(false);
            Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show();
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

    /**
     * 检查选中的条目
     */
    private void checkIsChecked() {
        idList.clear();
        for (Enclosure enclosure : enclosureList) {
            if (enclosure.isImgIsChecked()) {
                idList.add(enclosure.getId());
            }
        }
    }

}


