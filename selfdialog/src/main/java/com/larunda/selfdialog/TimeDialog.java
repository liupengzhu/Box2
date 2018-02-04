package com.larunda.selfdialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

/**
 * Created by sddt on 18-1-18.
 */

public class TimeDialog extends Dialog {


    private DatePicker datePicker;
    private TimePicker timePicker;
    private OnOkClickListener onOkClickListener;
    private OnCancelClickListener onCancelClickListener;
    private Button okButton;
    private Button cancelButton;

    public TimeDialog(@NonNull Context context) {
        super(context, R.style.MyDialog);
    }


    /**
     * 创建dialog的时候调用的方法
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_dialog);

        initView();

        //初始化界面控件的事件
        initEvent();


    }


    /**
     * 控件点击事件监听
     */
    private void initEvent() {

        okButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (onOkClickListener != null) {
                    String day = "";
                    if (datePicker.getDayOfMonth() < 10) {
                        day = "0" + datePicker.getDayOfMonth();
                    } else {
                        day = "" + datePicker.getDayOfMonth();
                    }
                    long moth = datePicker.getMonth() + 1;
                    String date = "";
                    if (moth < 10) {
                        date = datePicker.getYear() + "-0" + moth + "-" + day;
                    } else {
                        date = datePicker.getYear() + "-" + moth + "-" + day;
                    }
                    String hour = "";
                    if (timePicker.getHour() < 10) {
                        hour = "0" + timePicker.getHour();
                    } else {
                        hour = "" + timePicker.getHour();
                    }
                    String minute = "";
                    if (timePicker.getMinute() < 10) {
                        minute = "0" + timePicker.getMinute();
                    } else {
                        minute = "" + timePicker.getMinute();
                    }
                    String time = hour + ":" + minute + ":00";
                    onOkClickListener.OnClick(v, date, time);
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCancelClickListener != null) {
                    onCancelClickListener.OnClick(v);
                }
            }
        });

    }

    /**
     * 初始化View
     */
    private void initView() {

        datePicker = findViewById(R.id.date_picker);
        timePicker = findViewById(R.id.time_picker);
        timePicker.setIs24HourView(true);
        okButton = findViewById(R.id.time_ok_button);
        cancelButton = findViewById(R.id.time_cancel_button);
    }

    public interface OnOkClickListener {
        void OnClick(View view, String date, String time);
    }

    public interface OnCancelClickListener {
        void OnClick(View view);
    }

    public void setOnOkClickListener(OnOkClickListener onOkClickListener) {
        this.onOkClickListener = onOkClickListener;
    }

    public void setOnCancelClickListener(OnCancelClickListener onCancelClickListener) {
        this.onCancelClickListener = onCancelClickListener;
    }
}
