package com.example.box;


import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


import com.example.horizontalprogressbar.HorizontalProgressBarWithNunber;


public class DynamicPasswordActivity extends AppCompatActivity {

    HorizontalProgressBarWithNunber progressBar;
    public static final int MSG_PROGRESS_UPDATE = 0x110;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            int progress = progressBar.getProgress();
            progressBar.setProgress(++progress);
            if (progress >= 100) {
                mHandler.removeMessages(MSG_PROGRESS_UPDATE);

            }
            mHandler.sendEmptyMessageDelayed(MSG_PROGRESS_UPDATE, 30);
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_password);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        progressBar = findViewById(R.id.progress_bar);
        mHandler.sendEmptyMessage(MSG_PROGRESS_UPDATE);

    }
}
