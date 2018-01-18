package com.example.selfdialog;

import android.app.Dialog;
import android.content.Context;
import android.icu.text.TimeZoneFormat;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by sddt on 18-1-18.
 */

public class ConfirmDialog extends Dialog {

    private onNoOnclickListener noOnclickListener;
    private onYesOnclickListener yesOnclickListener;

    private Button yesButton;
    private Button noButton;
    private TextView content_Text;

    private String content;

    public ConfirmDialog(@NonNull Context context) {
        super(context,R.style.MyDialog);
    }


    /**
     * 创建dialog的时候调用的方法
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_dialog);

        //按空白处不能取消动画
        setCanceledOnTouchOutside(false);


        initView();

        //初始化界面控件的事件
        initEvent();

        initViewData();

    }

    private void initViewData() {
        if(content!=null){
            content_Text.setText(content);
        }
    }

    /**
     * 控件点击事件监听
     */
    private void initEvent() {
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(yesOnclickListener!=null){
                    yesOnclickListener.onYesClick(v);
                }
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(noOnclickListener!=null){
                    noOnclickListener.onNoClick(v);
                }

            }
        });
    }

    public void setContentText(String text){
        content = text;
    }

    /**
     * 初始化View
     */
    private void initView() {

        yesButton = findViewById(R.id.yes_button);
        noButton = findViewById(R.id.no_button);
        content_Text = (TextView) findViewById(R.id.content_text);
    }

    /**
     * 设置取消按钮的显示内容和监听
     *
     * @param onNoOnclickListener
     */
    public void setNoOnclickListener(ConfirmDialog.onNoOnclickListener onNoOnclickListener) {

        this.noOnclickListener = onNoOnclickListener;
    }

    /**
     * 设置确定按钮的显示内容和监听
     *
     * @param onYesOnclickListener
     */
    public void setYesOnclickListener(ConfirmDialog.onYesOnclickListener onYesOnclickListener) {

        this.yesOnclickListener = onYesOnclickListener;
    }

    /**
     * 设置确定按钮和取消被点击的接口
     */
    public interface onYesOnclickListener {
        public void onYesClick(View v);
    }

    public interface onNoOnclickListener {
        public void onNoClick(View v);
    }

}
