package cn.com.larunda.safebox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.larunda.safebox.R;
import com.larunda.selfdialog.BoxFingerprintDialog;
import com.larunda.selfdialog.ChooseDialog;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.com.larunda.safebox.util.ActivityCollector;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AddFingerprintActivity extends AppCompatActivity implements View.OnClickListener {

    private final String URL = Util.URL + "user/map" + Util.TOKEN;
    private int id;
    private SharedPreferences preferences;
    private String token;
    private TitleBar titleBar;

    private LinearLayout layout;
    private List<String> userData = new ArrayList<>();
    private List<Integer> userId = new ArrayList<>();
    private String key;
    private String value;
    private ChooseDialog userDialog;
    private int personId;
    private TextView text;
    private Button addButton;
    private BoxFingerprintDialog dialog;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_fingerprint);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        id = getIntent().getIntExtra("id", 0);
        initView();
        initEvent();
        sendRequest();
    }

    /**
     * 初始化view
     */
    private void initView() {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = preferences.getString("token", null);

        titleBar = findViewById(R.id.add_finger_title_bar);
        titleBar.setTextViewText("添加指纹");
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setRightButtonSrc(0);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);

        layout = findViewById(R.id.add_finger_layout);
        text = findViewById(R.id.add_finger_name);
        addButton = findViewById(R.id.add_finger_button);
        dialog = new BoxFingerprintDialog(this);
    }

    /**
     * 初始化点击事件
     */
    private void initEvent() {

        addButton.setOnClickListener(this);
        layout.setOnClickListener(this);

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
     * 请求人员列表
     */
    private void sendRequest() {
        HttpUtil.sendGetRequestWithHttp(URL + token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /*if (dialog != null && dialog.isShowing()) {
                            dialog.cancel();
                        }*/
                        Toast.makeText(AddFingerprintActivity.this, "网络异常!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String content = response.body().string();
                final int code = response.code();
                if (code == 200) {
                    userData.clear();
                    userId.clear();
                    try {
                        JSONObject jsonObject = new JSONObject(content);
                        Iterator iterator = jsonObject.keys();
                        while (iterator.hasNext()) {
                            key = (String) iterator.next();
                            value = jsonObject.getString(key);
                            userData.add(value);
                            userId.add(Integer.valueOf(key));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (code == 401 || code == 412) {
                                Intent intent = new Intent(AddFingerprintActivity.this, LoginActivity.class);
                                intent.putExtra("token_timeout", "登录超时");
                                preferences.edit().putString("token", null).commit();
                                startActivity(intent);
                                ActivityCollector.finishAllActivity();
                            } else if (code == 422) {
                                try {
                                    JSONObject js = new JSONObject(content);
                                    Toast.makeText(AddFingerprintActivity.this, js.get("message") + "", Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 点击事件处理
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_finger_layout:
                userDialog = new ChooseDialog(this, userData);
                userDialog.setOnClickListener(new ChooseDialog.OnClickListener() {
                    @Override
                    public void OnClick(View v, int position) {
                        personId = userId.get(position);
                        text.setText(userData.get(position));
                        userDialog.cancel();

                    }
                });
                userDialog.show();
                break;
            case R.id.add_finger_button:
                if (text.getText().toString().isEmpty()) {
                    Toast.makeText(this, "请先选择用户", Toast.LENGTH_SHORT).show();
                } else {
                    sendPostRequest();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 发送指纹录入请求
     */
    private void sendPostRequest() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", personId);
            dialog.show();
            dialog.setContent("正在录入指纹");
            HttpUtil.sendPostRequestWithHttp(Util.URL + "task/" + id + "/fingerprint" + Util.TOKEN + token,
                    jsonObject.toString(), new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (dialog != null && dialog.isShowing()) {
                                        dialog.setContent("网络异常!");
                                    }
                                }
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String content = response.body().string();
                            final int code = response.code();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (code == 200) {
                                        if (dialog != null && dialog.isShowing()) {
                                            dialog.setContent("指纹录入成功");
                                            //0.5秒后取消已经点击标记位
                                            timer = new Timer();
                                            timer.schedule(new TimerTask() {
                                                @Override
                                                public void run() {
                                                    if (dialog != null && dialog.isShowing()) {
                                                        dialog.cancel();
                                                    }
                                                    setResult(RESULT_OK);
                                                    finish();
                                                }
                                            }, 500);
                                        } else {
                                            setResult(RESULT_OK);
                                            finish();
                                        }
                                    } else if (code == 401 || code == 412) {
                                        Intent intent = new Intent(AddFingerprintActivity.this, LoginActivity.class);
                                        intent.putExtra("token_timeout", "登录超时");
                                        preferences.edit().putString("token", null).commit();
                                        startActivity(intent);
                                        ActivityCollector.finishAllActivity();
                                    } else if (code == 422) {
                                        try {
                                            JSONObject js = new JSONObject(content);
                                            if (dialog != null && dialog.isShowing()) {
                                                dialog.setContent(js.get("message") + "!");
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        if (dialog != null && dialog.isShowing()) {
                                            dialog.setContent("指纹添加失败!");
                                        }
                                    }
                                }
                            });
                    /*runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (dialog != null && dialog.isShowing()) {
                                if (content != null && content.equals("success")) {
                                    dialog.setContent("指纹录入成功");
                                    //0.5秒后取消已经点击标记位
                                    timer = new Timer();
                                    timer.schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            if (dialog != null && dialog.isShowing()) {
                                                dialog.cancel();

                                            }
                                        }
                                    }, 500);
                                    sendRequest();
                                } else if (content != null && content.equals("box_not_find")) {
                                    dialog.setContent("箱体未找到");
                                } else {
                                    dialog.setContent("服务器链接超时");
                                }

                            }
                        }
                    });*/

                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
