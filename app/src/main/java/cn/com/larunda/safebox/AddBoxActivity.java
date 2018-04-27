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

public class AddBoxActivity extends BaseActivity implements View.OnClickListener {

    private final String URL = Util.URL + "box" + Util.TOKEN;
    private TitleBar titleBar;
    private SharedPreferences preferences;
    private String token;

    private EditText codeText;
    private EditText nameText;
    private Button saveButton;
    private LoadingDailog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_box);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        initView();
        initEvent();
    }

    /**
     * 初始化view
     */
    private void initView() {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = preferences.getString("token", null);

        titleBar = findViewById(R.id.add_box_title_bar);
        titleBar.setTextViewText("添加递送箱");
        titleBar.setRightButtonSrc(0);
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);

        codeText = findViewById(R.id.add_box_code);
        nameText = findViewById(R.id.add_box_name);
        saveButton = findViewById(R.id.add_box_button);

        LoadingDailog.Builder loadBuilder = new LoadingDailog.Builder(this)
                .setMessage("上传中...")
                .setCancelable(false)
                .setCancelOutside(false);
        dialog = loadBuilder.create();
    }

    /**
     * 初始化点击事件
     */
    private void initEvent() {

        saveButton.setOnClickListener(this);

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
            case R.id.add_box_button:
                sendPostRequest();
                break;
        }
    }

    /**
     * 发送post请求
     */
    private void sendPostRequest() {
        String name = nameText.getText().toString().trim();
        String code = codeText.getText().toString().trim();
        if (!isEmpty(name, code)) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("f_sn", code);
                jsonObject.put("f_alias", name);
                dialog.show();
                HttpUtil.sendPostRequestWithHttp(URL + token, jsonObject.toString(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (dialog != null && dialog.isShowing()) {
                                    dialog.cancel();
                                }
                                Toast.makeText(AddBoxActivity.this, "网络异常!", Toast.LENGTH_SHORT).show();
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
                                } else if (code == 401|| code == 412) {
                                    Intent intent = new Intent(AddBoxActivity.this, LoginActivity.class);
                                    intent.putExtra("token_timeout", "登录超时");
                                    preferences.edit().putString("token", null).commit();
                                    startActivity(intent);
                                    ActivityCollector.finishAllActivity();
                                } else if (code == 422) {
                                    try {
                                        JSONObject js = new JSONObject(content);
                                        Toast.makeText(AddBoxActivity.this, js.get("message") + "", Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Toast.makeText(AddBoxActivity.this, "添加失败！", Toast.LENGTH_SHORT).show();
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

    /**
     * 判断是否为空
     *
     * @param name
     * @param code
     * @return
     */
    private boolean isEmpty(String name, String code) {
        if (code.isEmpty()) {
            Toast.makeText(this, "序列号不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (name.isEmpty()) {
            Toast.makeText(this, "别名不能为空", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}
