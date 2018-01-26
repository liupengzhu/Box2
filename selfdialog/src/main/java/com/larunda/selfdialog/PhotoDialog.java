package com.larunda.selfdialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by sddt on 18-1-18.
 */

public class PhotoDialog extends Dialog {


    private RelativeLayout choose_photo_button;
    private RelativeLayout choose_camera_button;
    private PhotoOnClickListener photoOnClickListener;
    private CameraOnClickListener cameraOnClickListener;


    public PhotoDialog(@NonNull Context context) {
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
        setContentView(R.layout.photo_dialog);

        initView();

        //初始化界面控件的事件
        initEvent();


    }


    /**
     * 控件点击事件监听
     */
    private void initEvent() {
        choose_camera_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cameraOnClickListener != null) {
                    cameraOnClickListener.onClick(v);
                }
            }
        });

        choose_photo_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photoOnClickListener != null) {
                    photoOnClickListener.onClick(v);
                }
            }
        });
    }


    /**
     * 初始化View
     */
    private void initView() {
        choose_photo_button = findViewById(R.id.choose_photo);
        choose_camera_button = findViewById(R.id.choose_camera);
    }

    public void setPhotoButtonOnClick(PhotoOnClickListener photoOnClickListener) {
        this.photoOnClickListener = photoOnClickListener;
    }

    public void setCameraButtonOnClick(CameraOnClickListener cameraOnClickListener) {
        this.cameraOnClickListener = cameraOnClickListener;
    }


    //点击事件接口
    public interface PhotoOnClickListener {
        void onClick(View v);
    }

    public interface CameraOnClickListener {
        void onClick(View v);
    }


}
