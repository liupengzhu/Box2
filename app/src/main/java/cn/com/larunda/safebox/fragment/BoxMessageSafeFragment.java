package cn.com.larunda.safebox.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.com.larunda.safebox.AddEnclosureActivity;
import com.larunda.safebox.R;
import cn.com.larunda.safebox.TrackActivity;
import com.larunda.selfdialog.ChooseDialog;
import com.larunda.selfdialog.TimeDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sddt on 18-1-18.
 */

public class BoxMessageSafeFragment extends Fragment implements View.OnClickListener {

    RelativeLayout enclosureButton;
    RelativeLayout trackButton;

    RelativeLayout levelButton;
    TextView levelText;

    RelativeLayout lockButton;
    TextView lockText;

    RelativeLayout bfButton;
    TextView bfText;

    LinearLayout startTimeButton;
    TextView startDateText;
    TextView startTimeText;

    LinearLayout endTimeButton;
    TextView endDateText;
    TextView endTimeText;

    List<String> levelList = new ArrayList<>();
    List<String> lockList = new ArrayList<>();
    List<String> bfList = new ArrayList<>();

    private ChooseDialog chooseDialog;
    private ChooseDialog lockChooseDialog;
    private ChooseDialog bfChooseDialog;
    private TimeDialog startDialog;
    private TimeDialog endDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.box_message_safe_fragment, container, false);
        initData();
        initView(view);
        initEvent();
        return view;
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
        bfList.add("未布防");
        bfList.add("已布防");
    }

    /**
     * 初始化view
     *
     * @param view
     */
    private void initView(View view) {
        enclosureButton = view.findViewById(R.id.box_message_enclosure);
        trackButton = view.findViewById(R.id.box_message_track);
        levelButton = view.findViewById(R.id.box_message_level);
        levelText = view.findViewById(R.id.box_message_level_text);
        lockButton = view.findViewById(R.id.box_message_lock);
        lockText = view.findViewById(R.id.box_message_lock_text);
        bfButton = view.findViewById(R.id.box_message_bf);
        bfText = view.findViewById(R.id.box_message_bf_text);
        startTimeButton = view.findViewById(R.id.box_message_start);
        startDateText = view.findViewById(R.id.box_message_start_date);
        startTimeText = view.findViewById(R.id.box_message_start_time);
        endTimeButton = view.findViewById(R.id.box_message_end);
        endDateText = view.findViewById(R.id.box_message_end_date);
        endTimeText = view.findViewById(R.id.box_message_end_time);

        chooseDialog = new ChooseDialog(getContext(), levelList);
        lockChooseDialog = new ChooseDialog(getContext(), lockList);
        bfChooseDialog = new ChooseDialog(getContext(), bfList);
        startDialog = new TimeDialog(getContext());
        endDialog = new TimeDialog(getContext());


    }

    /**
     * 初始化点击事件
     */
    private void initEvent() {
        trackButton.setOnClickListener(this);
        enclosureButton.setOnClickListener(this);
        levelButton.setOnClickListener(this);
        lockButton.setOnClickListener(this);
        bfButton.setOnClickListener(this);
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
        bfChooseDialog.setOnClickListener(new ChooseDialog.OnClickListener() {
            @Override
            public void OnClick(View v, int positon) {
                bfText.setText(bfList.get(positon));
                bfChooseDialog.cancel();
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
                Intent intent = new Intent(getContext(), AddEnclosureActivity.class);
                startActivity(intent);
                break;
            case R.id.box_message_track:
                Intent trackIntent = new Intent(getContext(), TrackActivity.class);
                startActivity(trackIntent);
                break;
            case R.id.box_message_level:
                chooseDialog.show();
                break;

            case R.id.box_message_lock:
                lockChooseDialog.show();
                break;
            case R.id.box_message_bf:
                bfChooseDialog.show();
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
}
