package cn.com.larunda.safebox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.larunda.safebox.R;
import com.larunda.selfdialog.ChooseDialog;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

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
    public static final String ADD_ENCLOSURE_URL = "http://safebox.dsmcase.com:90/api/app/box/area_add_lists?_token=";
    private RelativeLayout enclosureButton;
    private TextView enclosureText;
    private ChooseDialog enclosureDialog;
    private List<String> enclosureData = new ArrayList<>();

    private RelativeLayout positionButton;
    private TextView positionText;
    private ChooseDialog positionDialog;
    private List<String> positionData = new ArrayList<>();
    private SharedPreferences preferences;
    private String token;

    public SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout loodingErrorLayout;
    private ImageView loodingLayout;
    private LinearLayout layout;


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
        });
    }

    private void initAreaInfo(AreaInfo areaInfo) {
        enclosureData.clear();
        if (areaInfo.getData() != null) {
            for (AreaInfo.DataBean data : areaInfo.getData()) {
                if (data.getF_name() != null) {
                    enclosureData.add(data.getF_name());
                }
            }
        }
        if (enclosureData.size() == 0) {
            Toast.makeText(this, "没有更多区域", Toast.LENGTH_SHORT).show();
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
            default:
                break;
        }
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
