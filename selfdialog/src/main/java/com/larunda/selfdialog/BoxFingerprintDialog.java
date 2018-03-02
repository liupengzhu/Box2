package com.larunda.selfdialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by sddt on 18-2-7.
 */

public class BoxFingerprintDialog extends Dialog {

    private Context context;
    private ImageView img;
    private ImageView loadImg;
    private TextView textView;
    private LinearLayout button;
    private AnimationDrawable drawable;
    private ButtonOnClickListener buttonOnClickListener;

    public BoxFingerprintDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    /**
     * 创建dialog的时候调用的方法
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.box_fingerprint_dialog);
        setCanceledOnTouchOutside(false);
        initView();
        initEvent();

    }

    /**
     * 初始化点击事件
     */
    private void initEvent() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonOnClickListener != null) {
                    buttonOnClickListener.onClick(v);
                }
            }
        });

    }

    /**
     * 初始化view
     */
    private void initView() {
        img = findViewById(R.id.box_fingerprint_img);
        loadImg = findViewById(R.id.box_fingerprint_load_img);
        textView = findViewById(R.id.box_fingerprint_load_text);
        button = findViewById(R.id.box_fingerprint_button);
        /*img.setVisibility(View.GONE);
        textView.setText("正在录入指纹");
        loadImg.setVisibility(View.VISIBLE);
        drawable = (AnimationDrawable) loadImg.getBackground();
        if (drawable != null && !drawable.isRunning()) {
            drawable.start();
        }*/
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void setStatus(int status) {
        if (status == 1) {
            img.setVisibility(View.VISIBLE);
            img.setImageResource(R.drawable.fail_icon);
            textView.setText("指纹添加失败!");
            if (drawable != null && drawable.isRunning()) {
                drawable.stop();
            }
            loadImg.setVisibility(View.GONE);
        } else if (status == 2) {
            img.setVisibility(View.VISIBLE);
            img.setImageResource(R.drawable.success_icon);
            textView.setText("指纹添加成功!");
            if (drawable != null && drawable.isRunning()) {
                drawable.stop();
            }
            loadImg.setVisibility(View.GONE);
        }
    }

    public interface ButtonOnClickListener {
        void onClick(View view);
    }

    public void setButtonOnClickListener(ButtonOnClickListener buttonOnClickListener) {
        this.buttonOnClickListener = buttonOnClickListener;
    }

    public void setContent(String content) {
        if (textView != null && content != null) {
            textView.setText(content);
        }
    }
}
