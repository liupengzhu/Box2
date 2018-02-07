package com.larunda.selfdialog;

import android.app.Dialog;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.larunda.selfdialog.util.FingerprintUtil;

/**
 * Created by sddt on 18-2-7.
 */

public class FingerprintDialog extends Dialog {

    private Context context;
    private TextView textView;
    private Button button;
    private CancelButtonOnclickListener cancelButtonOnclickListener;
    private FingerprintUtil util;
    private ValidateSeccessListener validateSeccessListener;

    public FingerprintDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    /**
     * 创建dialog的时候调用的方法
     *
     * @param savedInstanceState
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fingprint_dialog);
        setCanceledOnTouchOutside(false);
        initView();
        initEvent();


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onStart() {
        super.onStart();
        util = new FingerprintUtil(context);
        util.start();
        util.setFingerprintOnClickListener(new FingerprintUtil.FingerprintOnClickListener() {
            @Override
            public void onError(int errorCode, CharSequence errString) {
                textView.setText("验证失败次数过多，请稍后重试！");
                textView.setTextColor(context.getResources().getColor(R.color.error));
            }

            @Override
            public void onSuccess(FingerprintManager.AuthenticationResult result) {
                textView.setText("指纹验证成功!");
                textView.setTextColor(context.getResources().getColor(R.color.text_black));
                if (validateSeccessListener != null) {
                    validateSeccessListener.seccess(result);
                }
                textView.setTextColor(context.getResources().getColor(R.color.text_black));
                cancel();
            }

            @Override
            public void onFailed() {
                textView.setText("指纹验证失败，请重试！");
                textView.setTextColor(context.getResources().getColor(R.color.error));
            }
        });
    }

    /**
     * 初始化点击事件
     */
    private void initEvent() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancelButtonOnclickListener != null) {
                    cancelButtonOnclickListener.onClick(v);
                }
            }
        });
    }

    /**
     * 初始化view
     */
    private void initView() {
        textView = findViewById(R.id.fingerprint_text);
        button = findViewById(R.id.fingerprint_button);
    }

    public interface CancelButtonOnclickListener {
        void onClick(View v);
    }

    public void setCancelButtonOnclickListener(CancelButtonOnclickListener cancelButtonOnclickListener) {
        this.cancelButtonOnclickListener = cancelButtonOnclickListener;
    }

    public interface ValidateSeccessListener {
        void seccess(FingerprintManager.AuthenticationResult result);
    }

    public void setValidateSeccessListener(ValidateSeccessListener validateSeccessListener) {
        this.validateSeccessListener = validateSeccessListener;

    }

    @Override
    protected void onStop() {
        super.onStop();
        util.stopListening();
    }

}
