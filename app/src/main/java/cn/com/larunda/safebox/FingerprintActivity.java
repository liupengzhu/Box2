package cn.com.larunda.safebox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.NestedScrollView;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.larunda.safebox.R;
import com.larunda.selfdialog.BoxFingerprintDialog;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.com.larunda.safebox.adapter.FingerprintAdapter;
import cn.com.larunda.safebox.gson.BoxAddUserInfo;
import cn.com.larunda.safebox.gson.FingerprintInfo;
import cn.com.larunda.safebox.gson.Message;
import cn.com.larunda.safebox.recycler.Fingerprint;
import cn.com.larunda.safebox.util.ActivityCollector;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FingerprintActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences preferences;
    private String token;
    private TitleBar titleBar;
    private String boxId;
    private String userId;
    public static final String FINGERPRINT_URL = Util.URL + "app/box_finger_lists" + Util.TOKEN;
    public static final String ADD_FINGERPRINT_URL = Util.URL + "box/get_add_finger_status" + Util.TOKEN;
    public static final String POST_FINGERPRINT_URL = Util.URL + "box/add_box_finger" + Util.TOKEN;

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            sendGetStatusRequest();
        }
    };

    private NestedScrollView layout;
    private RelativeLayout loodingErrorLayout;
    private ImageView loodingLayout;
    private SwipeRefreshLayout refreshLayout;

    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private FingerprintAdapter adapter;
    private List<Fingerprint> fingerprintList = new ArrayList<>();

    private LinearLayout addButton;
    private BoxFingerprintDialog dialog;
    private String code;
    private boolean isLinked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        boxId = getIntent().getStringExtra("boxId");
        userId = getIntent().getStringExtra("userId");
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
        HttpUtil.sendGetRequestWithHttp(FINGERPRINT_URL + token + "&box_id=" + boxId
                + "&user_id=" + userId, new Callback() {
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
                    final FingerprintInfo fingerprintInfo = Util.handleFingerprintInfo(content);
                    if (fingerprintInfo != null && fingerprintInfo.getError() == null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initData(fingerprintInfo);
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
                                Intent intent = new Intent(FingerprintActivity.this, LoginActivity.class);
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
     * 解析指纹列表信息
     *
     * @param fingerprintInfo
     */
    private void initData(FingerprintInfo fingerprintInfo) {
        fingerprintList.clear();
        code = fingerprintInfo.getCode();
        for (FingerprintInfo.DataBean dataBean : fingerprintInfo.getData()) {
            Fingerprint fingerprint = new Fingerprint();
            if (dataBean.getFinger_id() != null) {
                fingerprint.setId(dataBean.getFinger_id());
            }
            if (dataBean.getTime() != null) {
                fingerprint.setTime(dataBean.getTime());
            }
            fingerprintList.add(fingerprint);
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
        addButton.setOnClickListener(this);
        dialog.setButtonOnClickListener(new BoxFingerprintDialog.ButtonOnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog != null) {
                    dialog.cancel();
                }
            }
        });
    }

    /**
     * 初始化view
     */
    private void initView() {

        recyclerView = findViewById(R.id.fingerprint_recycler);
        manager = new LinearLayoutManager(this);
        adapter = new FingerprintAdapter(this, fingerprintList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        refreshLayout = findViewById(R.id.fingerprint_swipe);
        loodingErrorLayout = findViewById(R.id.fingerprint_loading_error_layout);
        loodingLayout = findViewById(R.id.fingerprint_loading_layout);
        layout = findViewById(R.id.fingerprint_layout);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = preferences.getString("token", null);

        addButton = findViewById(R.id.fingerprint_add_button);

        titleBar = findViewById(R.id.fingerprint_title_bar);
        titleBar.setTextViewText("箱体中的指纹列表");
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setRightButtonSrc(0);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);

        dialog = new BoxFingerprintDialog(this);
    }

    /**
     * 点击事件拦截
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fingerprint_add_button:
                dialog.show();
                if (!isLinked) {
                    sendPostRequest();
                }
                sendGetStatusRequest();
                break;
            default:
                break;
        }

    }

    /**
     * 查询指纹录入状态
     */
    private void sendGetStatusRequest() {
        refreshLayout.setRefreshing(true);
        HttpUtil.sendGetRequestWithHttp(ADD_FINGERPRINT_URL + token + "&box_id=" + boxId + "&code=" + code, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.cancel();
                        }
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
                    final Message message = Util.handleMessage(content);
                    if (message != null && message.error == null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                parseMessage(message);
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
                                Intent intent = new Intent(FingerprintActivity.this, LoginActivity.class);
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
     * 处理返回信息
     *
     * @param message
     */
    private void parseMessage(Message message) {
        if (dialog != null && dialog.isShowing()) {
            dialog.setContent(message.message);

        }
        if (message.message.equals("正在录入指纹中，请稍后")) {
            isLinked = true;
            handler.postDelayed(runnable, 5000);
        } else {
            handler.removeCallbacks(runnable);
            isLinked = false;
        }

    }

    /**
     * 发送指纹录入请求
     */
    private void sendPostRequest() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("box_id", boxId);
            jsonObject.put("code", code);
            HttpUtil.sendPostRequestWithHttp(POST_FINGERPRINT_URL + token, jsonObject.toString(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
