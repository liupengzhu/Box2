package cn.com.larunda.safebox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.larunda.safebox.R;

import cn.com.larunda.safebox.adapter.BoxMessageFragmentAdapter;
import cn.com.larunda.safebox.fragment.BoxMessageInfoFragment;
import cn.com.larunda.safebox.fragment.BoxMessageSafeFragment;
import cn.com.larunda.safebox.gson.Result;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static cn.com.larunda.safebox.fragment.BoxMessageInfoFragment.material_text;
import static cn.com.larunda.safebox.fragment.BoxMessageInfoFragment.name_text;
import static cn.com.larunda.safebox.fragment.BoxMessageInfoFragment.protect_text;
import static cn.com.larunda.safebox.fragment.BoxMessageInfoFragment.size_text;
import static cn.com.larunda.safebox.fragment.BoxMessageSafeFragment.endDateText;
import static cn.com.larunda.safebox.fragment.BoxMessageSafeFragment.endTimeText;
import static cn.com.larunda.safebox.fragment.BoxMessageSafeFragment.levelText;
import static cn.com.larunda.safebox.fragment.BoxMessageSafeFragment.lockText;
import static cn.com.larunda.safebox.fragment.BoxMessageSafeFragment.startDateText;
import static cn.com.larunda.safebox.fragment.BoxMessageSafeFragment.startTimeText;

public class BoxActivity extends AppCompatActivity implements View.OnClickListener {

    private TitleBar titleBar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private List<Fragment> fragmentList = new ArrayList<>();
    private List<String> titleList = new ArrayList<>();
    private BoxMessageFragmentAdapter adapter;
    public static String ID;
    public static SharedPreferences preferences;
    public static String token;
    private Button button;
    public static final String MESSAGE_URI = Util.URL + "box/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        ID = getIntent().getStringExtra("id");
        init();
        initView();
    }

    /**
     * 初始化fragmente
     */
    private void init() {
        fragmentList.add(new BoxMessageInfoFragment());
        fragmentList.add(new BoxMessageSafeFragment());
        titleList.add("基本信息");
        titleList.add("递送箱安全");
    }

    /**
     * 初始化view
     */
    private void initView() {
        preferences = PreferenceManager.getDefaultSharedPreferences(BoxActivity.this);
        token = preferences.getString("token", null);

        button = findViewById(R.id.box_message_button);
        button.setOnClickListener(this);

        tabLayout = findViewById(R.id.box_message_tabLayout);
        viewPager = findViewById(R.id.box_message_viewPager);
        adapter = new BoxMessageFragmentAdapter(getSupportFragmentManager(),
                fragmentList, titleList);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        //设置下划线长度
        tabLayout.post(new Runnable() {
            @Override
            public void run() {

                Util.setIndicator(tabLayout, 50, 50);
            }
        });

        titleBar = findViewById(R.id.box_message_title_bar);
        titleBar.setTextViewText("");
        titleBar.setRightButtonSrc(0);
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);
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
     * 点击事件监听
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.box_message_button:
                if (name_text != null && material_text != null && size_text != null && protect_text != null &&
                        levelText != null && startTimeText != null && startDateText != null && endDateText != null
                        && endTimeText != null && lockText != null) {
                    String name = name_text.getText().toString().trim();
                    String material = material_text.getText().toString().trim();
                    String size = size_text.getText().toString().trim();
                    String protect = protect_text.getText().toString().trim();
                    String level = levelText.getText().toString().trim();
                    String startData = startDateText.getText().toString().trim();
                    String startTime = startTimeText.getText().toString().trim();
                    String endData = endDateText.getText().toString().trim();
                    String endTime = endTimeText.getText().toString().trim();
                    String lock = lockText.getText().toString().trim();
                    if (!isEmpty(name, material, size, protect, level, startData, startTime, endData, endTime, lock)) {
                        sendPutRequest(name, material, size, protect, level, startData, startTime, endData, endTime, lock);
                    }
                }
                break;
        }
    }

    /**
     * 发送put请求
     *
     * @param name
     * @param material
     * @param size
     * @param protect
     * @param level
     * @param startData
     * @param startTime
     * @param endData
     * @param endTime
     * @param lock
     */
    private void sendPutRequest(String name, String material, String size, String protect, String level, String startData, String startTime, String endData, String endTime, String lock) {
        final JSONObject jsonObject = new JSONObject();
        final JSONObject js = new JSONObject();
        try {
            jsonObject.put("f_aliases", name);
            jsonObject.put("f_material", material);
            jsonObject.put("f_size", size);
            jsonObject.put("f_protect_grade", protect);

            if (level.equals("三级加密")) {
                jsonObject.put("f_encrypt_level", 3);
            } else if (level.equals("二级加密")) {
                jsonObject.put("f_encrypt_level", 2);
            } else {
                jsonObject.put("f_encrypt_level", 1);
            }
            js.put("start_time", startData + " " + startTime);
            js.put("end_time", endData + " " + endTime);
            jsonObject.put("f_lock_time", js);
            if (lock.equals("已锁定")) {
                jsonObject.put("f_is_locked", 1);
            } else {
                jsonObject.put("f_is_locked", 0);
            }
            jsonObject.put("type", "app");
            HttpUtil.sendPutRequestWithHttp(MESSAGE_URI + BoxActivity.ID + Util.TOKEN + BoxActivity.token, jsonObject.toString(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(BoxActivity.this, "网络异常", Toast.LENGTH_SHORT).show();

                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String content = response.body().string();
                    if (Util.isGoodJson(content)) {
                        final Result result = Util.handleResult(content);
                        if (result != null && result.error == null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    parseResult(result);

                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(BoxActivity.this, LoginActivity.class);
                                    intent.putExtra("token_timeout", "登录超时");
                                    BoxActivity.preferences.edit().putString("token", null).commit();
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }
                    }
                }

            });


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void parseResult(Result result) {
        if (result.data != null && result.data.equals("true")) {
            finish();
        } else {
            Toast.makeText(this, "更新失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 判断是否为空
     *
     * @param name
     * @param material
     * @param size
     * @param protect
     * @param level
     * @param startData
     * @param startTime
     * @param endData
     * @param endTime
     * @param lock
     * @return
     */
    private boolean isEmpty(String name, String material, String size, String protect, String level, String startData, String startTime, String endData, String endTime, String lock) {
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "别名不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(material)) {
            Toast.makeText(this, "材质不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(size)) {
            Toast.makeText(this, "尺寸不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(protect)) {
            Toast.makeText(this, "防护等级不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(level)) {
            Toast.makeText(this, "权限等级不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(startData) || startData.equals("开始日期")) {
            Toast.makeText(this, "开始日期不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(startTime) || startTime.equals("开始时间")) {
            Toast.makeText(this, "开始时间不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(endData) || endData.equals("结束日期")) {
            Toast.makeText(this, "结束日期不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(endTime) || endTime.equals("结束时间")) {
            Toast.makeText(this, "结束时间不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(lock)) {
            Toast.makeText(this, "锁定状态不能为空", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;


    }
}
