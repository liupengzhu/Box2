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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import cn.com.larunda.safebox.util.BaseActivity;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FingerprintActivity extends BaseActivity implements View.OnClickListener {

    private SharedPreferences preferences;
    private String token;
    private TitleBar titleBar;
    private String boxId;
    private String userId;
    public static final String FINGERPRINT_URL = Util.URL + "app/box_finger_lists" + Util.TOKEN;
    public static final String ADD_FINGERPRINT_URL = Util.URL + "box/get_add_finger_status" + Util.TOKEN;
    public static final String POST_FINGERPRINT_URL = Util.URL + "box/add_box_finger" + Util.TOKEN;
    public static final String DELETE_URL = Util.URL + "box/del_box_finger" + Util.TOKEN;

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
    private List<String> idList = new ArrayList<>();

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
        /**
         * 长按点击事件
         */
        adapter.setFingerprintOnLongClickListener(new FingerprintAdapter.FingerprintOnLongClickListener() {
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
     * 初始化view
     */
    private void initView() {

        allCheckedImage = findViewById(R.id.fingerprint_all_checked_image);
        allCheckedText = findViewById(R.id.fingerprint_all_checked_text);
        bottom_layout = findViewById(R.id.fingerprint_bottom_layout);
        deleteButton = findViewById(R.id.fingerprint_delete_button);

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
                if (!isLinked) {
                    sendPostRequest();
                } else {
                    sendGetStatusRequest();
                }
                break;
            case R.id.fingerprint_all_checked_image:
            case R.id.fingerprint_all_checked_text:
                allCheckedClick();
                break;
            case R.id.fingerprint_delete_button:
                checkIsChecked();
                if (idList.size() == 0) {
                    Toast.makeText(FingerprintActivity.this, "还没有选择指纹", Toast.LENGTH_SHORT).show();
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
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("code", code);
            jsonObject.put("finger_id", Util.listToString(idList));
            jsonObject.put("user_id",userId);
            refreshLayout.setRefreshing(true);
            HttpUtil.sendPostRequestWithHttp(DELETE_URL + token, jsonObject.toString(), new Callback() {
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
                            refreshLayout.setRefreshing(false);
                        }
                    });

                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析删除返回状态
     *
     * @param content
     */
    private void parseResponse(String content) {
        if (Util.isGoodJson(content)) {
            Message message = Util.handleMessage(content);
            if (message != null && message.error == null) {
                if (message.message.equals("成功")) {
                    sendRequest();
                } else {
                    Toast.makeText(FingerprintActivity.this, message.message, Toast.LENGTH_SHORT).show();
                }
            } else {
                Intent intent = new Intent(FingerprintActivity.this, LoginActivity.class);
                intent.putExtra("token_timeout", "登录超时");
                preferences.edit().putString("token", null).commit();
                startActivity(intent);
                ActivityCollector.finishAllActivity();
            }
        }
    }

    /**
     * 查询指纹录入状态
     */
    private void sendGetStatusRequest() {
        refreshLayout.setRefreshing(true);
        HttpUtil.sendGetRequestWithHttp(ADD_FINGERPRINT_URL + token + "&box_id=" + boxId
                + "&code=" + code + "&user_id=" + userId, new Callback() {
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
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
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
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("box_id", boxId);
            jsonObject.put("code", code);
            jsonObject.put("user_id",userId);
            refreshLayout.setRefreshing(true);
            HttpUtil.sendPostRequestWithHttp(POST_FINGERPRINT_URL + token, jsonObject.toString(), new Callback() {
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sendGetStatusRequest();
                        }
                    });

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
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
            List<Fingerprint> fingerprintList = adapter.getFingerprintList();
            for (Fingerprint fingerprint : fingerprintList) {
                fingerprint.setImgIsChecked(false);
            }
            adapter.notifyDataSetChanged();


        } else {
            isAllChecked = true;
            allCheckedImage.setImageResource(R.mipmap.checked);
            List<Fingerprint> fingerprintList = adapter.getFingerprintList();
            for (Fingerprint fingerprint : fingerprintList) {
                fingerprint.setImgIsChecked(true);
            }
            adapter.notifyDataSetChanged();

        }

    }

    /**
     * 检查选中的指纹
     */
    private void checkIsChecked() {
        idList.clear();
        for (Fingerprint fingerprint : fingerprintList) {
            if (fingerprint.isImgIsChecked()) {
                idList.add(fingerprint.getId());
            }
        }
    }

}
