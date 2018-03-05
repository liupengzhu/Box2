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


public class SettingStatesActivity extends BaseActivity implements View.OnClickListener {

    private TitleBar titleBar;
    RelativeLayout lockButton;
    TextView lockText;
    List<String> lockList = new ArrayList<>();
    private ArrayList<String> idList = new ArrayList<>();
    private ChooseDialog lockChooseDialog;
    private Button button;
    public static final String SETTING_STATUS = Util.URL + "box/set_lock_status" + Util.TOKEN;
    private SharedPreferences preferences;
    private String token;
    private SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_states);
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
        lockButton.setOnClickListener(this);
        lockChooseDialog.setOnClickListener(new ChooseDialog.OnClickListener() {
            @Override
            public void OnClick(View v, int positon) {
                lockText.setText(lockList.get(positon));
                lockChooseDialog.cancel();
            }
        });

        button.setOnClickListener(this);

    }

    /**
     * 初始化数据
     */
    private void initData() {
        lockList.add("未锁定");
        lockList.add("已锁定");

    }

    /**
     * 初始化view
     */

    private void initView() {

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = preferences.getString("token", null);


        titleBar = findViewById(R.id.setting_states_title_bar);
        titleBar.setTextViewText("设定状态");
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setRightButtonSrc(0);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);

        lockButton = findViewById(R.id.setting_lock);
        lockText = findViewById(R.id.setting_lock_text);
        lockChooseDialog = new ChooseDialog(this, lockList);
        button = findViewById(R.id.setting_states_button);

        refreshLayout = findViewById(R.id.setting_status_refresh);
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
            case R.id.setting_lock:
                lockChooseDialog.show();
                break;

            case R.id.setting_states_button:
                if (lockText != null) {
                    String lock = lockText.getText().toString().trim();
                    if (!isEmpty(lock)) {
                        sendPostRequest(lock);
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
     * @param lock
     */
    private void sendPostRequest(String lock) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("code", Util.listToString(idList));
            if (lock.equals("已锁定")) {
                jsonObject.put("status", 1);
            } else {
                jsonObject.put("status", 0);
            }
            refreshLayout.setRefreshing(true);
            HttpUtil.sendPostRequestWithHttp(SETTING_STATUS + token, jsonObject.toString(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshLayout.setRefreshing(false);
                            Toast.makeText(SettingStatesActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
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
     * 解析返回数据
     *
     * @param content
     */
    private void parseMessage(String content) {
        if (content.equals("success")) {
            Toast.makeText(this, "设置成功", Toast.LENGTH_SHORT).show();
        } else if (Util.isGoodJson(content)) {
            cn.com.larunda.safebox.gson.Message message = Util.handleMessage(content);
            if (message != null && message.error == null) {
                if (message.message != null) {
                    Toast.makeText(this, message.message, Toast.LENGTH_SHORT).show();
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
     * 判断是否选中锁定状态的方法
     *
     * @param lock
     * @return
     */
    private boolean isEmpty(String lock) {
        if (TextUtils.isEmpty(lock) || lock.equals("请选择是否锁定")) {
            Toast.makeText(this, "请选择锁定状态", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}
