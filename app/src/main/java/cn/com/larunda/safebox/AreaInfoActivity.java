package cn.com.larunda.safebox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

import cn.com.larunda.safebox.adapter.BindAreaAdapter;
import cn.com.larunda.safebox.gson.BindAreaData;
import cn.com.larunda.safebox.gson.BindAreaInfo;
import cn.com.larunda.safebox.recycler.BindArea;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AreaInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private TitleBar titleBar;
    private Button addButton;

    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private BindAreaAdapter adapter;
    private List<BindArea> bindAreaList = new ArrayList<>();
    private String id;
    private final String BIND_USER_URL = Util.URL + "box/bind_area_lists" + Util.TOKEN;
    private final String CANCEL_URL = Util.URL + "box/cancel_bind_area" + Util.TOKEN;
    private SharedPreferences preferences;
    private String token;

    private SwipeRefreshLayout refreshLayout;
    private RelativeLayout loodingErrorLayout;
    private ImageView loodingLayout;
    private NestedScrollView layout;

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
    private LinearLayout bottom_layout;

    private Button deleteButton;
    private ArrayList<String> idList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_info);

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
                sendRequest();

            }
        });

        //每次创建时还没有网络数据 设置载入背景为可见
        loodingLayout.setVisibility(View.VISIBLE);
        loodingErrorLayout.setVisibility(View.GONE);
        layout.setVisibility(View.GONE);

        sendRequest();
    }

    /**
     * 发送网络请求
     */
    private void sendRequest() {
        refreshLayout.setRefreshing(true);
        HttpUtil.sendGetRequestWithHttp(BIND_USER_URL + token + "&id=" + id, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                        loodingErrorLayout.setVisibility(View.VISIBLE);
                        loodingLayout.setVisibility(View.GONE);
                        layout.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String content = response.body().string();
                if (Util.isGoodJson(content)) {
                    final BindAreaInfo bindAreaInfo = Util.handleBindAreaInfo(content);
                    if (bindAreaInfo != null && bindAreaInfo.error == null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initData(bindAreaInfo);
                                refreshLayout.setRefreshing(false);
                                loodingErrorLayout.setVisibility(View.GONE);
                                loodingLayout.setVisibility(View.GONE);
                                layout.setVisibility(View.VISIBLE);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(AreaInfoActivity.this, LoginActivity.class);
                                intent.putExtra("token_timeout", "登录超时");
                                preferences.edit().putString("token", null).commit();
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        sendRequest();
    }

    /**
     * 解析数据
     */
    private void initData(BindAreaInfo bindAreaInfo) {
        bindAreaList.clear();
        if (bindAreaInfo.dataList != null) {
            for (BindAreaData data : bindAreaInfo.dataList) {
                BindArea bindArea = new BindArea();
                if (data.area_id != null) {
                    bindArea.setId(data.area_id);
                } else {
                    bindArea.setId("");
                }
                if (data.f_name != null) {
                    bindArea.setName(data.f_name);
                } else {
                    bindArea.setName("");
                }
                if (data.created_at != null) {
                    bindArea.setTime(data.created_at);
                } else {
                    bindArea.setTime("");
                }
                if (data.in_or_out != null) {
                    if (data.in_or_out.equals("0")) {
                        bindArea.setIn_or_out("内");
                    } else {
                        bindArea.setIn_or_out("外");
                    }
                } else {
                    bindArea.setIn_or_out("");
                }
                bindAreaList.add(bindArea);
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
                Intent intent = new Intent();
                intent.putExtra("count", adapter.getItemCount());
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onRightButtonClickListener(View v) {

            }
        });
        addButton.setOnClickListener(this);

        adapter.setBindAreaOnLongClickListener(new BindAreaAdapter.BindAreaOnLongClickListener() {
            @Override
            public void onClick(View v) {
                isLongClick = true;
                adapter.setCheckedLayout(true);
                adapter.notifyDataSetChanged();
                bottom_layout.setVisibility(View.VISIBLE);
            }
        });

        allCheckedImage.setOnClickListener(this);
        allCheckedText.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
    }


    /**
     * 初始化View
     */
    private void initView() {

        allCheckedImage = findViewById(R.id.area_info_all_checked_image);
        allCheckedText = findViewById(R.id.area_info_all_checked_text);
        bottom_layout = findViewById(R.id.area_info_bottom_layout);
        deleteButton = findViewById(R.id.area_info_delete_button);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = preferences.getString("token", null);

        refreshLayout = findViewById(R.id.area_swipe);
        loodingErrorLayout = findViewById(R.id.area_loading_error_layout);
        loodingLayout = findViewById(R.id.area_loading_layout);
        layout = findViewById(R.id.area_layout);

        titleBar = findViewById(R.id.area_title_bar);
        titleBar.setTextViewText("");
        titleBar.setRightButtonSrc(0);
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);

        addButton = findViewById(R.id.area_add_button);

        recyclerView = findViewById(R.id.area_recycler);
        manager = new LinearLayoutManager(this);
        adapter = new BindAreaAdapter(this, bindAreaList);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
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
            case R.id.area_add_button:
                if (id != null) {
                    Intent intent = new Intent(this, AddEnclosureActivity.class);
                    intent.putExtra("id", id);
                    startActivity(intent);
                }
                break;
            case R.id.area_info_all_checked_image:
            case R.id.area_info_all_checked_text:
                allCheckedClick();
                break;
            case R.id.area_info_delete_button:
                checkIsChecked();
                if (idList.size() == 0) {
                    Toast.makeText(AreaInfoActivity.this, "还没有选择区域", Toast.LENGTH_SHORT).show();
                } else {
                    sendDeleteRequest();
                }
                break;
            default:
                break;

        }

    }

    private void sendDeleteRequest() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("box_id", id);
            jsonObject.put("area_id", Util.listToString(idList));
            HttpUtil.sendPostRequestWithHttp(CANCEL_URL + token, jsonObject.toString(), new Callback() {
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
                    if (Util.isGoodJson(content)) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                parseResponse(content);
                            }
                        });
                    }
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析post请求返回数据
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
                    Intent intent = new Intent(AreaInfoActivity.this, LoginActivity.class);
                    intent.putExtra("token_timeout", "登录超时");
                    preferences.edit().putString("token", null).commit();
                    startActivity(intent);
                    finish();
                }
            });
        }
    }


    /**
     * 检查选中的用户
     */
    private void checkIsChecked() {
        idList.clear();
        for (BindArea bindArea : bindAreaList) {
            if (bindArea.isImgIsChecked()) {
                idList.add(bindArea.getId());
            }
        }
    }


    @Override
    public void onBackPressed() {
        //判断递送箱列表是否是多选状态
        if (isLongClick) {
            cancleLongClick();

        } else {
            Intent intent = new Intent();
            intent.putExtra("count", adapter.getItemCount());
            setResult(RESULT_OK, intent);
            finish();

        }
    }

    /**
     * 取消多选状态
     */
    public void cancleLongClick() {
        isLongClick = false;
        adapter.setCheckedLayout(false);
        adapter.notifyDataSetChanged();
        bottom_layout.setVisibility(View.GONE);
    }

    /**
     * 处理全选按钮的点击事件
     */
    private void allCheckedClick() {
        //判断当前全选是否是选中状态
        if (isAllChecked) {
            isAllChecked = false;
            allCheckedImage.setImageResource(R.mipmap.unchecked);
            List<BindArea> bindAreaList = adapter.getBindAreaList();
            for (BindArea bindArea : bindAreaList) {
                bindArea.setImgIsChecked(false);
            }
            adapter.notifyDataSetChanged();


        } else {
            isAllChecked = true;
            allCheckedImage.setImageResource(R.mipmap.checked);
            List<BindArea> bindAreaList = adapter.getBindAreaList();
            for (BindArea bindArea : bindAreaList) {
                bindArea.setImgIsChecked(true);
            }
            adapter.notifyDataSetChanged();

        }

    }
}
