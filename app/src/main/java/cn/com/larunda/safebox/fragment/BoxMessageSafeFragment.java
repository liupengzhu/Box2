package cn.com.larunda.safebox.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.com.larunda.safebox.AddEnclosureActivity;

import com.larunda.safebox.R;

import cn.com.larunda.safebox.AreaInfoActivity;
import cn.com.larunda.safebox.BoxActivity;
import cn.com.larunda.safebox.LoginActivity;
import cn.com.larunda.safebox.MainActivity;
import cn.com.larunda.safebox.TrackActivity;
import cn.com.larunda.safebox.gson.BoxMessage;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import com.larunda.selfdialog.ChooseDialog;
import com.larunda.selfdialog.TimeDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sddt on 18-1-18.
 */

public class BoxMessageSafeFragment extends BaseFragment implements View.OnClickListener {

    RelativeLayout enclosureButton;
    RelativeLayout trackButton;

    RelativeLayout levelButton;
    TextView levelText;

    RelativeLayout lockButton;
    TextView lockText;

    TextView bfText;
    TextView leavingText;

    LinearLayout startTimeButton;
    TextView startDateText;
    TextView startTimeText;

    LinearLayout endTimeButton;
    TextView endDateText;
    TextView endTimeText;

    List<String> levelList = new ArrayList<>();
    List<String> lockList = new ArrayList<>();

    private ChooseDialog chooseDialog;
    private ChooseDialog lockChooseDialog;
    private TimeDialog startDialog;
    private TimeDialog endDialog;

    public static final String MESSAGE_URI = "http://safebox.dsmcase.com:90/api/box/";
    private TextView bind_area_text;

    public SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout loodingErrorLayout;
    private ImageView loodingLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.box_message_safe_fragment, container, false);
        initData();
        initView(view);
        initEvent();
        //每次fragment创建时还没有网络数据 设置载入背景为可见
        loodingLayout.setVisibility(View.VISIBLE);
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
        HttpUtil.sendGetRequestWithHttp(MESSAGE_URI + BoxActivity.ID + "?_token=" + BoxActivity.token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //
                        swipeRefreshLayout.setRefreshing(false);
                        loodingErrorLayout.setVisibility(View.VISIBLE);
                        loodingLayout.setVisibility(View.INVISIBLE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final BoxMessage boxMessage = Util.handleBoxMessage(response.body().string());
                if (boxMessage != null && boxMessage.error == null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initBoxMessage(boxMessage);
                            swipeRefreshLayout.setRefreshing(false);
                            loodingErrorLayout.setVisibility(View.INVISIBLE);
                            loodingLayout.setVisibility(View.INVISIBLE);
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
                            getActivity().finish();
                        }
                    });
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
            if (boxMessage.encrypt_level.equals("2")) {
                levelText.setText("三级加密");
            } else if (boxMessage.encrypt_level.equals("1")) {
                levelText.setText("二级加密");
            } else if (boxMessage.encrypt_level.equals("0")) {
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
                if (dates[0] != null) {
                    startDateText.setText(dates[0]);
                }
                if (dates[1] != null) {
                    startTimeText.setText(dates[1]);
                }
            }

            if (boxMessage.boxLockTime.end_time != null) {
                String[] dates = boxMessage.boxLockTime.end_time.split(" ");
                if (dates[0] != null) {
                    endDateText.setText(dates[0]);
                }
                if (dates[1] != null) {
                    endTimeText.setText(dates[1]);
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

        loodingErrorLayout = view.findViewById(R.id.box_message_safe_loading_error_layout);
        loodingLayout = view.findViewById(R.id.box_message_safe_loading_layout);
        swipeRefreshLayout = view.findViewById(R.id.box_message_safe_swipe);
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

                startDateText.setText(date);
                startTimeText.setText(time);
                startDialog.cancel();
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

                endDateText.setText(date);
                endTimeText.setText(time);
                endDialog.cancel();
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
            case R.id.box_message_enclosure:
                if (BoxActivity.ID != null) {
                    Intent intent = new Intent(getContext(), AreaInfoActivity.class);
                    intent.putExtra("id", BoxActivity.ID);
                    startActivity(intent);
                }
                break;
            case R.id.box_message_track:
                if (BoxActivity.ID != null) {
                    Intent trackIntent = new Intent(getContext(), TrackActivity.class);
                    trackIntent.putExtra("id", BoxActivity.ID);
                    startActivity(trackIntent);
                }
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
                endDialog.show();
                break;
            default:
                break;

        }

    }

    @Override
    protected void loadData() {
        sendHttpRequest();
    }
}
