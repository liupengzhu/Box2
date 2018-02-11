package cn.com.larunda.safebox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.larunda.selfdialog.ChooseDialog;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.com.larunda.safebox.gson.AreaInfo;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class AddEnclosureActivity extends AppCompatActivity implements View.OnClickListener {

    private TitleBar titleBar;
    private String id;
    public static final String ADD_ENCLOSURE_URL = Util.URL + "app/box/area_add_lists" + Util.TOKEN;
    public static final String POST_URL = Util.URL + "box/add_bind_area" + Util.TOKEN;

    private RelativeLayout enclosureButton;
    private TextView enclosureText;
    private ChooseDialog enclosureDialog;
    private List<String> enclosureData = new ArrayList<>();
    private List<Integer> enclosureId = new ArrayList<>();

    private RelativeLayout positionButton;
    private TextView positionText;
    private ChooseDialog positionDialog;
    private List<String> positionData = new ArrayList<>();

    private SharedPreferences preferences;
    private String token;
    private int areaId;

    public SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout loodingErrorLayout;
    private ImageView loodingLayout;
    private LinearLayout layout;
    private Button postButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_enclosure);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        id = getIntent().getStringExtra("id");
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = preferences.getString("token", null);
        initData();
        initView();
        initEvent();
        //每次fragment创建时还没有网络数据 设置载入背景为可见
        loodingLayout.setVisibility(View.VISIBLE);
        loodingErrorLayout.setVisibility(View.GONE);
        layout.setVisibility(View.GONE);
        sendRequest();
    }

    /**
     * 发送网络请求
     */
    private void sendRequest() {
        swipeRefreshLayout.setRefreshing(true);
        HttpUtil.sendGetRequestWithHttp(ADD_ENCLOSURE_URL + token + "&id=" + id, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
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
                    final AreaInfo areaInfo = Util.handleAreaInfo(content);
                    if (areaInfo != null && areaInfo.getError() == null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initAreaInfo(areaInfo);
                                swipeRefreshLayout.setRefreshing(false);
                                layout.setVisibility(View.VISIBLE);
                                loodingErrorLayout.setVisibility(View.GONE);
                                loodingLayout.setVisibility(View.GONE);
                            }
                        });

                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(AddEnclosureActivity.this, LoginActivity.class);
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

    private void initAreaInfo(AreaInfo areaInfo) {
        enclosureData.clear();
        enclosureId.clear();
        if (areaInfo.getData() != null) {
            for (AreaInfo.DataBean data : areaInfo.getData()) {
                if (data.getF_name() != null) {
                    enclosureData.add(data.getF_name());
                    enclosureId.add(data.getId());
                }
            }
        }
        if (enclosureData.size() == 0) {
            Toast.makeText(this, "没有更多区域", Toast.LENGTH_SHORT).show();
        }
        enclosureText.setText("请选择区域");
        positionText.setText("请选择区域内外");

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
        enclosureButton.setOnClickListener(this);

        positionButton.setOnClickListener(this);
        positionDialog.setOnClickListener(new ChooseDialog.OnClickListener() {
            @Override
            public void OnClick(View v, int positon) {
                positionText.setText(positionData.get(positon));
                positionDialog.cancel();
            }
        });

        loodingErrorLayout.setOnClickListener(this);
        postButton.setOnClickListener(this);

    }

    /**
     * 初始化数据
     */
    private void initData() {

        positionData.add("内");
        positionData.add("外");
    }

    /**
     * 初始化view
     */
    private void initView() {

        loodingErrorLayout = findViewById(R.id.add_enclosure_loading_error_layout);
        loodingLayout = findViewById(R.id.add_enclosure_loading_layout);
        layout = findViewById(R.id.add_enclosure_layout);

        swipeRefreshLayout = findViewById(R.id.add_enclosure_swipe);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        swipeRefreshLayout.setEnabled(false);//设置swipe不可用

        enclosureButton = findViewById(R.id.add_enclosure_enclosure);
        enclosureText = findViewById(R.id.add_enclosure_enclosure_text);

        postButton = findViewById(R.id.add_enclosure_button);

        positionButton = findViewById(R.id.add_enclosure_position);
        positionText = findViewById(R.id.add_enclosure_position_text);
        positionDialog = new ChooseDialog(this, positionData);

        titleBar = findViewById(R.id.add_enclosure_title_bar);
        titleBar.setTextViewText("添加区域");
        titleBar.setRightButtonSrc(0);
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);

    }

    /**
     * 点击事件监听
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_enclosure_enclosure:
                enclosureDialog = new ChooseDialog(this, enclosureData);
                enclosureDialog.setOnClickListener(new ChooseDialog.OnClickListener() {
                    @Override
                    public void OnClick(View v, int positon) {
                        if (enclosureText.getText().toString().trim().equals(enclosureData.get(positon))) {
                            enclosureDialog.cancel();
                        } else {
                            positionText.setText("请选择区域内外");
                            areaId = enclosureId.get(positon);
                            enclosureText.setText(enclosureData.get(positon));
                            enclosureDialog.cancel();
                        }
                    }
                });
                enclosureDialog.show();
                break;
            case R.id.add_enclosure_position:
                if (isCheckedEnclosure()) {
                    positionDialog.show();
                }
                break;

            case R.id.add_enclosure_loading_error_layout:
                sendRequest();
                break;

            case R.id.add_enclosure_button:
                if (enclosureText != null && positionText != null) {
                    String enclosure = enclosureText.getText().toString().trim();
                    String position = positionText.getText().toString().trim();
                    if (!isEmpty(enclosure, position)) {
                        sendPostRequest(position);
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 发送post请求
     *
     * @param position
     */
    private void sendPostRequest(String position) {

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("area_id", areaId);
            jsonObject.put("box_id", id);
            if (position.equals("内")) {
                jsonObject.put("f_in_or_out", 0);
            } else {
                jsonObject.put("f_in_or_out", 1);
            }
            swipeRefreshLayout.setRefreshing(true);
            HttpUtil.sendPostRequestWithHttp(POST_URL + token, jsonObject.toString(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                            loodingErrorLayout.setVisibility(View.VISIBLE);
                            loodingLayout.setVisibility(View.GONE);
                            layout.setVisibility(View.GONE);
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
     * 解析post请求返回信息
     *
     * @param content
     */
    private void parseResponse(String content) {
        if (content != null && content.equals("true")) {
            finish();
        } else if (content != null && content.equals("false")) {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(this, "添加失败", Toast.LENGTH_SHORT).show();
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(AddEnclosureActivity.this, LoginActivity.class);
                    intent.putExtra("token_timeout", "登录超时");
                    preferences.edit().putString("token", null).commit();
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    /**
     * 判断是否为空
     *
     * @param enclosure
     * @param position
     * @return
     */
    private boolean isEmpty(String enclosure, String position) {
        if (TextUtils.isEmpty(enclosure) || enclosure.equals("请选择区域")) {
            Toast.makeText(this, "区域不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(position) || position.equals("请选择区域内外")) {
            Toast.makeText(this, "区域内外不能为空", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    /**
     * 判断是否选择区域的方法
     *
     * @return
     */
    private boolean isCheckedEnclosure() {
        if (enclosureText.getText().toString().trim().equals("请选择区域")) {
            Toast.makeText(this, "请先选择区域", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
}
