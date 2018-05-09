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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.tu.loadingdialog.LoadingDailog;
import com.larunda.safebox.R;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.com.larunda.safebox.util.ActivityCollector;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AddTaskActivity extends AppCompatActivity implements View.OnClickListener {
    private int id;
    private TitleBar titleBar;
    private SharedPreferences preferences;
    private String token;
    private LoadingDailog dialog;
    private EditText nameText;
    private EditText intervalText;
    private RadioGroup leavingGroup;
    private RadioGroup defenceGroup;
    private int leavingType;
    private int defenceType;

    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
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
    }

    /**
     * 初始化view
     */
    private void initView() {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = preferences.getString("token", null);

        titleBar = findViewById(R.id.add_task_title_bar);
        titleBar.setTextViewText("添加任务");
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setRightButtonSrc(0);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);

        LoadingDailog.Builder loadBuilder = new LoadingDailog.Builder(this)
                .setMessage("上传中...")
                .setCancelable(false)
                .setCancelOutside(false);
        dialog = loadBuilder.create();

        nameText = findViewById(R.id.add_task_name);
        saveButton = findViewById(R.id.add_task_button);

        intervalText = findViewById(R.id.add_task_interval);
        leavingGroup = findViewById(R.id.add_task_leaving_group);
        defenceGroup = findViewById(R.id.add_task_defence_group);
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

        saveButton.setOnClickListener(this);
    }

    /**
     * 点击事件处理
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_task_button:
                sendPostRequest();
                break;
            default:
                break;
        }
    }

    /**
     * 发送post请求
     */
    private void sendPostRequest() {
        getRadioType();
        String name = nameText.getText().toString().trim();
        String interval = intervalText.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, "任务名称不能为空！", Toast.LENGTH_SHORT).show();
        } else if (interval.isEmpty()) {
            Toast.makeText(this, "通讯间隔不能为空！", Toast.LENGTH_SHORT).show();
        } else if (Integer.parseInt(interval) > 1800 || Integer.parseInt(interval) < 30) {
            Toast.makeText(this, "通讯间隔必须在30到1800之间！", Toast.LENGTH_SHORT).show();
        } else {
            final JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("f_name", name);
                jsonObject.put("f_upload_interval", interval);
                jsonObject.put("f_use_dislocation", leavingType);
                jsonObject.put("f_use_defense", defenceType);
                dialog.show();
                HttpUtil.sendPostRequestWithHttp(Util.URL + "box/" + id + "/task" + Util.TOKEN + token, jsonObject.toString(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (dialog != null && dialog.isShowing()) {
                                    dialog.cancel();
                                }
                                Toast.makeText(AddTaskActivity.this, "网络异常!", Toast.LENGTH_SHORT).show();
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
                                if (dialog != null && dialog.isShowing()) {
                                    dialog.cancel();
                                }
                                if (code == 200) {
                                    setResult(RESULT_OK);
                                    finish();
                                } else if (code == 401 || code == 412) {
                                    Intent intent = new Intent(AddTaskActivity.this, LoginActivity.class);
                                    intent.putExtra("token_timeout", "登录超时");
                                    preferences.edit().putString("token", null).commit();
                                    startActivity(intent);
                                    ActivityCollector.finishAllActivity();
                                } else if (code == 422) {
                                    try {
                                        JSONObject js = new JSONObject(content);
                                        Toast.makeText(AddTaskActivity.this, js.get("message") + "", Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Toast.makeText(AddTaskActivity.this, "添加失败！", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void getRadioType() {
        if (leavingGroup.getCheckedRadioButtonId() == R.id.add_task_leaving_close_button) {
            leavingType = 0;
        } else {
            leavingType = 1;
        }

        if (defenceGroup.getCheckedRadioButtonId() == R.id.add_task_defence_close_button) {
            defenceType = 0;
        } else {
            defenceType = 1;
        }
    }

}
