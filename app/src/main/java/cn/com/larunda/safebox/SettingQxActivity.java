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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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

import cn.com.larunda.safebox.util.ActivityCollector;
import cn.com.larunda.safebox.util.BaseActivity;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class SettingQxActivity extends BaseActivity implements View.OnClickListener {

    private TitleBar titleBar;
    private RelativeLayout levelButton;
    private TextView levelText;

    private List<String> levelList = new ArrayList<>();
    private ChooseDialog levelDialog;
    private ArrayList<String> idList = new ArrayList<>();

    private Button postButton;
    public static final String LEVEL_URL = Util.URL + "box/set_encrypt_level" + Util.TOKEN;
    private SharedPreferences preferences;
    private String token;

    private SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_qx);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        idList = getIntent().getStringArrayListExtra("id");
        initData();
        initView();
        initEvent();


    }

    /**
     * 初始化点击事件
     */
    private void initEvent() {
        levelButton.setOnClickListener(this);
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
        levelDialog.setOnClickListener(new ChooseDialog.OnClickListener() {
            @Override
            public void OnClick(View v, int positon) {
                levelText.setText(levelList.get(positon));
                levelDialog.cancel();
            }
        });
        postButton.setOnClickListener(this);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        levelList.add("一级加密");
        levelList.add("二级加密");
        levelList.add("三级加密");
    }

    /**
     * 初始化View
     */
    private void initView() {

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = preferences.getString("token", null);

        levelButton = findViewById(R.id.setting_level);
        levelDialog = new ChooseDialog(this, levelList);
        levelText = findViewById(R.id.setting_level_text);

        postButton = findViewById(R.id.setting_qx_button);

        titleBar = findViewById(R.id.setting_qx_title_bar);
        titleBar.setTextViewText("设定权限");
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setRightButtonSrc(0);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);
        refreshLayout = findViewById(R.id.setting_qx_refresh);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        refreshLayout.setEnabled(false);//设置swipe不可用

    }

    /**
     * 点击事件监听
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_level:
                levelDialog.show();
                break;
            case R.id.setting_qx_button:
                if (levelText != null) {
                    String level = levelText.getText().toString().trim();
                    if (!isEmpty(level)) {
                        sendPostRequest(level);
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 发送加密等级
     *
     * @param level
     */
    private void sendPostRequest(String level) {
        JSONObject jsonObject = new JSONObject();
        final String id = Util.listToString(idList);
        try {
            jsonObject.put("code", id);
            if (level.equals("三级加密")) {
                jsonObject.put("level", 3);
            } else if (level.equals("二级加密")) {
                jsonObject.put("level", 2);
            } else {
                jsonObject.put("level", 1);
            }
            refreshLayout.setRefreshing(true);
            HttpUtil.sendPostRequestWithHttp(LEVEL_URL + token, jsonObject.toString(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshLayout.setRefreshing(false);
                            Toast.makeText(SettingQxActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                        }
                    });

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String content = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            parseMessage(content);
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
     * 解析数据
     *
     * @param content
     */
    private void parseMessage(String content) {
        if (content.equals("success")) {
            Toast.makeText(SettingQxActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
        } else {
            cn.com.larunda.safebox.gson.Message message = Util.handleMessage(content);
            if (message != null && message.error == null) {
                if (message.message != null) {
                    Toast.makeText(SettingQxActivity.this, message.message, Toast.LENGTH_SHORT).show();
                }
            } else {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra("token_timeout", "登录超时");
                preferences.edit().putString("token", null).commit();
                startActivity(intent);
                ActivityCollector.finishAllActivity();
            }

        }
    }

    /**
     * 检测有没有选择等级
     *
     * @param level
     * @return
     */
    private boolean isEmpty(String level) {
        if (TextUtils.isEmpty(level) || level.equals("请选择加密等级")) {
            Toast.makeText(this, "请选择加密等级", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}
