package cn.com.larunda.safebox.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.com.larunda.safebox.AddEnclosureActivity;

import com.larunda.safebox.R;
import com.larunda.selfdialog.ChooseDialog;
import com.larunda.selfdialog.TimeDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sddt on 18-1-18.
 */

public class BoxAddSafeFragment extends BaseFragment implements View.OnClickListener {

    RelativeLayout enclosureButton;
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

    private EditText materialText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.box_add_safe_fragment, container, false);
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
     * 初始化View
     *
     * @param view
     */
    private void initView(View view) {
        enclosureButton = view.findViewById(R.id.box_add_safe_enclosure);
        levelButton = view.findViewById(R.id.box_add_safe_level);
        levelText = view.findViewById(R.id.box_add_safe_level_text);
        lockButton = view.findViewById(R.id.box_add_safe_lock);
        lockText = view.findViewById(R.id.box_add_safe_lock_text);
        bfButton = view.findViewById(R.id.box_add_safe_bf);
        bfText = view.findViewById(R.id.box_add_safe_bf_text);
        startTimeButton = view.findViewById(R.id.box_add_safe_start);
        startDateText = view.findViewById(R.id.box_add_safe_start_date);
        startTimeText = view.findViewById(R.id.box_add_safe_start_time);
        endTimeButton = view.findViewById(R.id.box_add_safe_end);
        endDateText = view.findViewById(R.id.box_add_safe_end_date);
        endTimeText = view.findViewById(R.id.box_add_safe_end_time);

        chooseDialog = new ChooseDialog(getContext(), levelList);
        lockChooseDialog = new ChooseDialog(getContext(), lockList);
        bfChooseDialog = new ChooseDialog(getContext(), bfList);
        startDialog = new TimeDialog(getContext());
        endDialog = new TimeDialog(getContext());

        materialText = view.findViewById(R.id.box_add_info_material_text);
    }

    /**
     * 初始化点击事件
     */
    private void initEvent() {

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
            case R.id.box_add_safe_enclosure:

                break;
            case R.id.box_add_safe_level:
                chooseDialog.show();
                break;

            case R.id.box_add_safe_lock:
                lockChooseDialog.show();
                break;
            case R.id.box_add_safe_bf:
                bfChooseDialog.show();
                break;
            case R.id.box_add_safe_start:
                startDialog.show();
                break;
            case R.id.box_add_safe_end:
                endDialog.show();
                break;
            default:
                break;
        }
    }

    @Override
    protected void loadData() {

    }
}
