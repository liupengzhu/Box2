package cn.com.larunda.safebox.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.larunda.safebox.R;

import cn.com.larunda.safebox.AreaInfoActivity;
import cn.com.larunda.safebox.BoxActivity;
import cn.com.larunda.safebox.LoginActivity;
import cn.com.larunda.safebox.TrackActivity;
import cn.com.larunda.safebox.gson.BoxMessage;
import cn.com.larunda.safebox.gson.Result;
import cn.com.larunda.safebox.util.ActivityCollector;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import com.larunda.selfdialog.ChooseDialog;
import com.larunda.selfdialog.TimeDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sddt on 18-1-18.
 */

public class BoxMessageSafeFragment extends Fragment implements View.OnClickListener {

    RelativeLayout enclosureButton;
    RelativeLayout trackButton;

    RelativeLayout levelButton;
    public static TextView levelText;

    RelativeLayout lockButton;
    public static TextView lockText;

    TextView bfText;
    TextView leavingText;

    LinearLayout startTimeButton;
    public static TextView startDateText;
    public static TextView startTimeText;

    LinearLayout endTimeButton;
    public static TextView endDateText;
    public static TextView endTimeText;

    List<String> levelList = new ArrayList<>();
    List<String> lockList = new ArrayList<>();

    private ChooseDialog chooseDialog;
    private ChooseDialog lockChooseDialog;
    private TimeDialog startDialog;
    private TimeDialog endDialog;

    public static final String MESSAGE_URI = Util.URL + "box/";
    private TextView bind_area_text;

    public SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout loodingErrorLayout;
    private ImageView loodingLayout;
    private LinearLayout layout;

    private Button putButton;

    private boolean isInit = false;
    private static final int BIND_ENCLOSURE = 3;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.box_message_safe_fragment, container, false);
        initData();
        initView(view);
        initEvent();
        //每次fragment创建时还没有网络数据 设置载入背景为可见
        loodingLayout.setVisibility(View.VISIBLE);
        loodingErrorLayout.setVisibility(View.GONE);
        layout.setVisibility(View.GONE);
        isInit = true;
        sendHttpRequest();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * 发送网络请求
     */
    private void sendHttpRequest() {
        swipeRefreshLayout.setRefreshing(true);
        HttpUtil.sendGetRequestWithHttp(MESSAGE_URI + BoxActivity.ID + Util.TOKEN + BoxActivity.token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
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
                    final BoxMessage boxMessage = Util.handleBoxMessage(content);
                    if (boxMessage != null && boxMessage.error == null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initBoxMessage(boxMessage);
                                swipeRefreshLayout.setRefreshing(false);
                                layout.setVisibility(View.VISIBLE);
                                loodingErrorLayout.setVisibility(View.GONE);
                                loodingLayout.setVisibility(View.GONE);
                            }
                        });
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                intent.putExtra("token_timeout", "登录超时");
                                BoxActivity.preferences.edit().putString("token", null).commit();
                                startActivity(intent);
                                ActivityCollector.finishAllActivity();
                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * 解析数据
     *
     * @param boxMessage
     */
    private void initBoxMessage(BoxMessage boxMessage) {
        if (boxMessage.encrypt_level != null) {
            if (boxMessage.encrypt_level.equals("3")) {
                levelText.setText("三级加密");
            } else if (boxMessage.encrypt_level.equals("2")) {
                levelText.setText("二级加密");
            } else if (boxMessage.encrypt_level.equals("1")) {
                levelText.setText("一级加密");
            }
        } else {
            levelText.setText("");
        }
        bind_area_text.setText("已设置" + boxMessage.bind_area_num + "个区域");
        if (boxMessage.isLocked != null) {
            if (boxMessage.isLocked.equals("1")) {
                lockText.setText("已锁定");
            } else if (boxMessage.isLocked.equals("0")) {
                lockText.setText("未锁定");
            }

        } else {
            lockText.setText("");
        }
        if (boxMessage.isDefence != null) {
            if (boxMessage.isDefence.equals("1")) {
                bfText.setText("已布防");
            } else if (boxMessage.isDefence.equals("0")) {
                bfText.setText("未布防");
            }
        } else {
            bfText.setText("");
        }
        if (boxMessage.is_leaving != null) {
            if (boxMessage.is_leaving.equals("1")) {
                leavingText.setText("已离位");
            } else if (boxMessage.is_leaving.equals("0")) {
                leavingText.setText("未离位");
            }
        } else {
            leavingText.setText("");
        }
        if (boxMessage.boxLockTime != null) {
            if (boxMessage.boxLockTime.start_time != null) {
                String[] dates = boxMessage.boxLockTime.start_time.split(" ");
                if (dates.length > 1) {
                    if (dates[0] != null) {
                        startDateText.setText(dates[0]);
                    }
                    if (dates[1] != null) {
                        startTimeText.setText(dates[1]);
                    }
                }
            }

            if (boxMessage.boxLockTime.end_time != null) {
                String[] dates = boxMessage.boxLockTime.end_time.split(" ");
                if (dates.length > 1) {
                    if (dates[0] != null) {
                        endDateText.setText(dates[0]);
                    }
                    if (dates[1] != null) {
                        endTimeText.setText(dates[1]);
                    }
                }
            }


        }
    }

    /**
     * 初始化数据
     */
    private void initData() {
        levelList.add("一级加密");
        levelList.add("二级加密");
        levelList.add("三级加密");
        lockList.add("未锁定");
        lockList.add("已锁定");
    }

    /**
     * 初始化view
     *
     * @param view
     */
    private void initView(View view) {
        bind_area_text = view.findViewById(R.id.box_message_info_bind_area_text);
        enclosureButton = view.findViewById(R.id.box_message_enclosure);
        trackButton = view.findViewById(R.id.box_message_track);
        levelButton = view.findViewById(R.id.box_message_level);
        levelText = view.findViewById(R.id.box_message_level_text);
        lockButton = view.findViewById(R.id.box_message_lock);
        lockText = view.findViewById(R.id.box_message_lock_text);
        bfText = view.findViewById(R.id.box_message_bf_text);
        leavingText = view.findViewById(R.id.box_message_leaving_text);
        startTimeButton = view.findViewById(R.id.box_message_start);
        startDateText = view.findViewById(R.id.box_message_start_date);
        startTimeText = view.findViewById(R.id.box_message_start_time);
        endTimeButton = view.findViewById(R.id.box_message_end);
        endDateText = view.findViewById(R.id.box_message_end_date);
        endTimeText = view.findViewById(R.id.box_message_end_time);

        chooseDialog = new ChooseDialog(getContext(), levelList);
        lockChooseDialog = new ChooseDialog(getContext(), lockList);
        startDialog = new TimeDialog(getContext());
        endDialog = new TimeDialog(getContext());

        putButton = view.findViewById(R.id.box_message_safe_button);

        loodingErrorLayout = view.findViewById(R.id.box_message_safe_loading_error_layout);
        loodingLayout = view.findViewById(R.id.box_message_safe_loading_layout);
        layout = view.findViewById(R.id.box_message_safe_layout);

        swipeRefreshLayout = view.findViewById(R.id.box_message_safe_swipe);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        swipeRefreshLayout.setEnabled(false);//设置swipe不可用


    }

    /**
     * 初始化点击事件
     */
    private void initEvent() {
        trackButton.setOnClickListener(this);
        enclosureButton.setOnClickListener(this);
        levelButton.setOnClickListener(this);
        lockButton.setOnClickListener(this);
        startTimeButton.setOnClickListener(this);
        endTimeButton.setOnClickListener(this);
        putButton.setOnClickListener(this);

        chooseDialog.setOnClickListener(new ChooseDialog.OnClickListener() {
            @Override
            public void OnClick(View v, int positon) {
                levelText.setText(levelList.get(positon));
                chooseDialog.cancel();
            }
        });

        lockChooseDialog.setOnClickListener(new ChooseDialog.OnClickListener() {
            @Override
            public void OnClick(View v, int positon) {
                lockText.setText(lockList.get(positon));
                lockChooseDialog.cancel();
            }
        });

        startDialog.setOnCancelClickListener(new TimeDialog.OnCancelClickListener() {
            @Override
            public void OnClick(View view) {
                startDialog.cancel();
            }
        });
        startDialog.setOnOkClickListener(new TimeDialog.OnOkClickListener() {
            @Override
            public void OnClick(View view, String date, String time) {
                if (checkEndIsSelected()) {
                    String endDate = endDateText.getText().toString().trim() + " " +
                            endTimeText.getText().toString().trim();
                    String startDate = date + " " + time;
                    if (startDateIsSmall(startDate, endDate)) {
                        startDateText.setText(date);
                        startTimeText.setText(time);
                        startDialog.cancel();
                    }
                } else {
                    startDateText.setText(date);
                    startTimeText.setText(time);
                    startDialog.cancel();
                }
            }
        });
        endDialog.setOnCancelClickListener(new TimeDialog.OnCancelClickListener() {
            @Override
            public void OnClick(View view) {
                endDialog.cancel();
            }
        });
        endDialog.setOnOkClickListener(new TimeDialog.OnOkClickListener() {
            @Override
            public void OnClick(View view, String date, String time) {
                String startDate = startDateText.getText().toString().trim() + " " +
                        startTimeText.getText().toString().trim();
                String endDate = date + " " + time;
                if (endDateIsLarge(startDate, endDate)) {
                    endDateText.setText(date);
                    endTimeText.setText(time);
                    endDialog.cancel();
                }
            }
        });
        loodingErrorLayout.setOnClickListener(this);

    }

    /**
     * 检查是否是开始时间小
     *
     * @param startDate
     * @param endDate
     * @return
     */
    private boolean startDateIsSmall(String startDate, String endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            long start = sdf.parse(startDate).getTime();
            long end = sdf.parse(endDate).getTime();
            if (end > start) {
                return true;
            }
            Toast.makeText(getContext(), "开始时间必须小于结束时间", Toast.LENGTH_SHORT).show();
            return false;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        Toast.makeText(getContext(), "开始时间必须小于结束时间", Toast.LENGTH_SHORT).show();
        return false;

    }

    /**
     * 检查结束时间是否已经选择
     *
     * @return
     */
    private boolean checkEndIsSelected() {
        String endDate = endDateText.getText().toString().trim();
        String endTime = endTimeText.getText().toString().trim();
        if (TextUtils.isEmpty(endDate) || endDate.equals("结束日期")) {
            return false;
        } else if (TextUtils.isEmpty(endTime) || endTime.equals("结束时间")) {
            return false;
        }
        return true;
    }

    /**
     * 判断结束时间是否大于开始时间
     *
     * @param startDate
     * @param endDate
     * @return
     */
    private boolean endDateIsLarge(String startDate, String endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            long start = sdf.parse(startDate).getTime();
            long end = sdf.parse(endDate).getTime();
            if (end > start) {

                return true;
            }
            Toast.makeText(getContext(), "结束时间必须大于开始时间", Toast.LENGTH_SHORT).show();
            return false;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        Toast.makeText(getContext(), "结束时间必须大于开始时间", Toast.LENGTH_SHORT).show();
        return false;
    }

    /**
     * 点击事件监听
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.box_message_enclosure:
                if (BoxActivity.ID != null) {
                    Intent intent = new Intent(getContext(), AreaInfoActivity.class);
                    intent.putExtra("id", BoxActivity.ID);
                    startActivityForResult(intent, BIND_ENCLOSURE);
                }
                break;
            case R.id.box_message_track:
                if (BoxActivity.ID != null) {
                    Intent trackIntent = new Intent(getContext(), TrackActivity.class);
                    trackIntent.putExtra("id", BoxActivity.ID);
                    startActivity(trackIntent);
                }
                break;
            case R.id.box_message_safe_loading_error_layout:
                sendHttpRequest();
                break;
            case R.id.box_message_level:
                chooseDialog.show();
                break;

            case R.id.box_message_lock:
                lockChooseDialog.show();
                break;
            case R.id.box_message_start:
                startDialog.show();
                break;
            case R.id.box_message_end:
                if (checkStartIsSelected()) {
                    endDialog.show();
                }
                break;

            case R.id.box_message_safe_button:
                if (levelText != null && startTimeText != null && startDateText != null && endDateText != null
                        && endTimeText != null && lockText != null) {
                    String level = levelText.getText().toString().trim();
                    String startData = startDateText.getText().toString().trim();
                    String startTime = startTimeText.getText().toString().trim();
                    String endData = endDateText.getText().toString().trim();
                    String endTime = endTimeText.getText().toString().trim();
                    String lock = lockText.getText().toString().trim();
                    if (!isEmpty(level, startData, startTime, endData, endTime, lock)) {
                        sendPutRequest(level, startData + " " + startTime, endData + " " + endTime, lock);
                    }
                }
                break;
            default:
                break;

        }

    }

    /**
     * 检查开始时间是否已经选择
     *
     * @return
     */
    private boolean checkStartIsSelected() {
        String startDate = startDateText.getText().toString().trim();
        String startTime = startTimeText.getText().toString().trim();
        if (TextUtils.isEmpty(startDate) || startDate.equals("开始日期")) {
            Toast.makeText(getContext(), "开始日期不能为空", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(startTime) || startTime.equals("开始时间")) {
            Toast.makeText(getContext(), "开始时间不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * 发送put
     *
     * @param level
     * @param s
     * @param s1
     * @param lock
     */
    private void sendPutRequest(String level, String s, String s1, String lock) {
        swipeRefreshLayout.setRefreshing(true);
        final JSONObject jsonObject = new JSONObject();
        final JSONObject js = new JSONObject();
        try {
            if (level.equals("三级加密")) {
                jsonObject.put("f_encrypt_level", 3);
            } else if (level.equals("二级加密")) {
                jsonObject.put("f_encrypt_level", 2);
            } else {
                jsonObject.put("f_encrypt_level", 1);
            }
            js.put("start_time", s);
            js.put("end_time", s1);
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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "网络异常", Toast.LENGTH_SHORT).show();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    String content = response.body().string();
                    final Result result = Util.handleResult(content);
                    if (result != null && result.error == null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                parseResult(result);
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                intent.putExtra("token_timeout", "登录超时");
                                BoxActivity.preferences.edit().putString("token", null).commit();
                                startActivity(intent);
                                ActivityCollector.finishAllActivity();
                            }
                        });
                    }
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void parseResult(Result result) {
        if (result.data != null && result.data.equals("true")) {
            Toast.makeText(getContext(), "更新成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "更新失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 判断是否为空
     *
     * @param level
     * @param startData
     * @param starttime
     * @param endData
     * @param endTime
     * @param lock
     * @return
     */
    private boolean isEmpty(String level, String startData, String starttime, String endData, String endTime, String lock) {
        if (TextUtils.isEmpty(level)) {
            Toast.makeText(getContext(), "权限等级不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(startData) || startData.equals("开始日期")) {
            Toast.makeText(getContext(), "开始日期不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(starttime) || starttime.equals("开始时间")) {
            Toast.makeText(getContext(), "开始时间不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(endData) || endData.equals("结束日期")) {
            Toast.makeText(getContext(), "结束日期不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(endTime) || endTime.equals("结束时间")) {
            Toast.makeText(getContext(), "结束时间不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(lock)) {
            Toast.makeText(getContext(), "锁定状态不能为空", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;

    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BIND_ENCLOSURE:
                if (data != null) {
                    bind_area_text.setText("已设置" + data.getIntExtra("count", 0) + "个区域");
                }
                break;

        }
    }
}
