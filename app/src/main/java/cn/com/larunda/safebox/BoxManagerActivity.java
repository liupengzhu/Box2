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
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.tu.loadingdialog.LoadingDailog;
import com.larunda.safebox.R;
import com.larunda.selfdialog.ChooseDialog;
import com.larunda.selfdialog.TimeDialog;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.com.larunda.safebox.recycler.Person;
import cn.com.larunda.safebox.util.ActivityCollector;
import cn.com.larunda.safebox.util.BaseActivity;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class BoxManagerActivity extends BaseActivity implements View.OnClickListener {

    private int processId;
    private int taskId;
    private SharedPreferences preferences;
    private String token;
    private TitleBar titleBar;
    private String interval;
    private String isUseLeaving;
    private String isUseDefence;
    private String releaseTime;
    private String area;
    private String areaString;

    private EditText intervalText;
    private TextView areaText;
    private TextView releaseText;
    private RadioGroup leavingGroup;
    private RadioGroup defenceGroup;
    private int leavingType;
    private int defenceType;

    private TimeDialog timeDialog;
    private RelativeLayout timeButton;
    private List<String> enclosureData = new ArrayList<>();
    private List<Integer> enclosureId = new ArrayList<>();
    private String key;
    private String value;

    private RelativeLayout areaButton;
    private ChooseDialog enclosureDialog;
    private int areaId;

    private Button addButton;
    private LoadingDailog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box_manager);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        taskId = getIntent().getIntExtra("taskId", 0);
        processId = getIntent().getIntExtra("processId", 0);
        interval = getIntent().getStringExtra("interval");
        isUseLeaving = getIntent().getStringExtra("isUseLeaving");
        isUseDefence = getIntent().getStringExtra("isUseDefence");
        releaseTime = getIntent().getStringExtra("releaseTime");
        area = getIntent().getStringExtra("area");
        areaString = getIntent().getStringExtra("areaString");
        areaId = getIntent().getIntExtra("areaId",0);
        initView();
        initEvent();
    }

    /**
     * 初始化view
     */
    private void initView() {

        LoadingDailog.Builder loadBuilder = new LoadingDailog.Builder(this)
                .setMessage("上传中...")
                .setCancelable(false)
                .setCancelOutside(false);
        dialog = loadBuilder.create();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = preferences.getString("token", null);

        titleBar = findViewById(R.id.box_manager_title_bar);
        titleBar.setTextViewText("安全箱管理");
        titleBar.setRightButtonSrc(0);
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);

        intervalText = findViewById(R.id.box_manager_interval);
        areaText = findViewById(R.id.box_manager_area);
        releaseText = findViewById(R.id.box_manager_time);
        leavingGroup = findViewById(R.id.box_manager_leaving_group);
        defenceGroup = findViewById(R.id.box_manager_defence_group);

        intervalText.setText(interval != null ? interval : "");
        areaText.setText(area != null ? area : "");
        releaseText.setText(releaseTime != null ? releaseTime : "");
        leavingGroup.check(isUseLeaving == null ? R.id.box_manager_leaving_normal_button
                : (isUseLeaving.equals("0") ? R.id.box_manager_leaving_close_button
                : R.id.box_manager_leaving_open_button));
        defenceGroup.check(isUseDefence == null ? R.id.box_manager_defence_normal_button
                : (isUseDefence.equals("0") ? R.id.box_manager_defence_close_button
                : R.id.box_manager_defence_open_button));

        timeButton = findViewById(R.id.box_manager_time_button);
        timeDialog = new TimeDialog(this);

        areaButton = findViewById(R.id.box_manager_area_button);

        addButton = findViewById(R.id.box_manager_button);

        enclosureData.clear();
        enclosureId.clear();
        try {
            JSONObject jsonObject = new JSONObject(areaString);
            Iterator iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                key = (String) iterator.next();
                value = jsonObject.getString(key);
                enclosureData.add(value);
                enclosureId.add(Integer.valueOf(key));
            }
        } catch (JSONException e) {
            e.printStackTrace();
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

        timeButton.setOnClickListener(this);
        timeDialog.setOnCancelClickListener(new TimeDialog.OnCancelClickListener() {
            @Override
            public void OnClick(View view) {
                timeDialog.cancel();
            }
        });
        timeDialog.setOnOkClickListener(new TimeDialog.OnOkClickListener() {
            @Override
            public void OnClick(View view, String date, String time) {
                releaseText.setText(date + " " + time);
                timeDialog.cancel();
            }
        });

        areaButton.setOnClickListener(this);

        addButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.box_manager_time_button:
                timeDialog.show();
                break;
            case R.id.box_manager_area_button:
                enclosureDialog = new ChooseDialog(this, enclosureData);
                enclosureDialog.setOnClickListener(new ChooseDialog.OnClickListener() {
                    @Override
                    public void OnClick(View v, int position) {
                        areaId = enclosureId.get(position);
                        areaText.setText(enclosureData.get(position));
                        enclosureDialog.cancel();

                    }
                });
                enclosureDialog.show();
                break;
            case R.id.box_manager_button:
                sendPostRequest();
                break;
            default:
                break;
        }
    }

    /**
     * 发送网络请求
     */
    private void sendPostRequest() {
        getRadioType();
        String interval = intervalText.getText().toString().trim();
        String time = releaseText.getText().toString().trim();
        String area = areaText.getText().toString().trim();
        if (!isEmpty(interval, time)) {
            final JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("f_release_time", time);
                if (interval.isEmpty()) {
                    jsonObject.put("f_upload_interval", -1);
                } else {
                    jsonObject.put("f_upload_interval", interval);
                }
                jsonObject.put("f_use_dislocation", leavingType);
                jsonObject.put("f_use_defense", defenceType);
                if (!area.isEmpty()) {
                    jsonObject.put("fence_id", areaId);
                }
                dialog.show();
                HttpUtil.sendPutRequestWithHttp(Util.URL + "task/" + taskId + "/process/" + processId
                        + Util.TOKEN + token, jsonObject.toString(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (dialog != null && dialog.isShowing()) {
                                    dialog.cancel();
                                }
                                Toast.makeText(BoxManagerActivity.this, "网络异常!", Toast.LENGTH_SHORT).show();
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
                                    Intent intent = new Intent(BoxManagerActivity.this, LoginActivity.class);
                                    intent.putExtra("token_timeout", "登录超时");
                                    preferences.edit().putString("token", null).commit();
                                    startActivity(intent);
                                    ActivityCollector.finishAllActivity();
                                } else if (code == 422) {
                                    try {
                                        JSONObject js = new JSONObject(content);
                                        Toast.makeText(BoxManagerActivity.this, js.get("message") + "", Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Toast.makeText(BoxManagerActivity.this, "添加失败！", Toast.LENGTH_SHORT).show();
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
     * @param interval
     * @param time
     * @return
     */
    private boolean isEmpty(String interval, String time) {
        if (!interval.isEmpty() && (Integer.parseInt(interval) > 1800 || Integer.parseInt(interval) < 30)) {
            Toast.makeText(this, "通讯间隔必须在30-1800之间", Toast.LENGTH_SHORT).show();
            return true;
        } else if (time.isEmpty()) {
            Toast.makeText(this, "截止时间不能为空", Toast.LENGTH_SHORT).show();
            return true;
        }

        return false;
    }


    public void getRadioType() {
        if (leavingGroup.getCheckedRadioButtonId() == R.id.box_manager_leaving_close_button) {
            leavingType = 0;
        } else if (leavingGroup.getCheckedRadioButtonId() == R.id.box_manager_leaving_open_button) {
            leavingType = 1;
        } else {
            leavingType = -1;
        }

        if (defenceGroup.getCheckedRadioButtonId() == R.id.box_manager_defence_close_button) {
            defenceType = 0;
        } else if (defenceGroup.getCheckedRadioButtonId() == R.id.box_manager_defence_open_button) {
            defenceType = 1;
        } else {
            defenceType = -1;
        }
    }
}
