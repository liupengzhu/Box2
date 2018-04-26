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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.tu.loadingdialog.LoadingDailog;
import com.google.gson.JsonObject;
import com.larunda.safebox.R;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.com.larunda.safebox.util.ActivityCollector;
import cn.com.larunda.safebox.util.BaseActivity;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class TaskDetailActivity extends BaseActivity implements View.OnClickListener {

    private TitleBar titleBar;
    private SharedPreferences preferences;
    private String token;

    private TextView nameText;
    private TextView startText;
    private TextView endText;
    private Button endButton;
    private int id;
    private String name;
    private String createTime;
    private String completedTime;
    private LoadingDailog dialog;

    private RelativeLayout destinationButton;
    private RelativeLayout trackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        id = getIntent().getIntExtra("id", 0);
        name = getIntent().getStringExtra("name");
        createTime = getIntent().getStringExtra("createTime");
        completedTime = getIntent().getStringExtra("completedTime");
        initView();
        initEvent();
    }

    /**
     * 初始化View
     */
    private void initView() {

        destinationButton = findViewById(R.id.task_detail_destination_button);
        trackButton = findViewById(R.id.task_detail_track);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = preferences.getString("token", null);

        titleBar = findViewById(R.id.task_detail_title_bar);
        titleBar.setTextViewText("任务详情");
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setRightButtonSrc(0);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);

        LoadingDailog.Builder loadBuilder = new LoadingDailog.Builder(this)
                .setMessage("上传中...")
                .setCancelable(false)
                .setCancelOutside(false);
        dialog = loadBuilder.create();

        nameText = findViewById(R.id.task_detail_name);
        startText = findViewById(R.id.task_detail_start_time);
        endText = findViewById(R.id.task_detail_end_time);
        endButton = findViewById(R.id.task_detail_button);
        if (name != null) {
            nameText.setText(name);
        }
        if (createTime != null) {
            startText.setText(createTime);
        }
        if (completedTime != null) {
            endText.setText(completedTime);
            endButton.setVisibility(View.GONE);
        } else {
            endButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 点击事件处理
     */
    private void initEvent() {

        trackButton.setOnClickListener(this);
        destinationButton.setOnClickListener(this);

        endButton.setOnClickListener(this);

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
    }

    /**
     * 点击事件处理
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.task_detail_button:
                sendPostRequest();
                break;
            case R.id.task_detail_destination_button:
                Intent intent = new Intent(this, DestinationActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("completedTime", completedTime);
                startActivity(intent);
                break;
            case R.id.task_detail_track:
                Intent trackIntent = new Intent(this, TrackActivity.class);
                trackIntent.putExtra("id", id+"");
                startActivity(trackIntent);
                break;
            default:
                break;
        }
    }

    /**
     * 发送结束任务请求
     */
    private void sendPostRequest() {
        JsonObject jsonObject = new JsonObject();
        dialog.show();
        HttpUtil.sendPostRequestWithHttp(Util.URL + "task/" + id + "/make_it_complete" + Util.TOKEN + token, jsonObject.toString(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.cancel();
                        }
                        Toast.makeText(TaskDetailActivity.this, "网络异常!", Toast.LENGTH_SHORT).show();
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
                            if (content.equals("true")) {
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                Toast.makeText(TaskDetailActivity.this, "结束失败！", Toast.LENGTH_SHORT).show();
                            }
                        } else if (code == 401) {
                            Intent intent = new Intent(TaskDetailActivity.this, LoginActivity.class);
                            intent.putExtra("token_timeout", "登录超时");
                            preferences.edit().putString("token", null).commit();
                            startActivity(intent);
                            ActivityCollector.finishAllActivity();
                        } else if (code == 422) {
                            try {
                                JSONObject js = new JSONObject(content);
                                Toast.makeText(TaskDetailActivity.this, js.get("message") + "", Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(TaskDetailActivity.this, "结束失败！", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
