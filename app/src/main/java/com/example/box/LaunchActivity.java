package com.example.box;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_launch);
        new Thread(new Runnable() {
            @Override
            public void run() {
                //耗时任务，比如加载网络数据
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        //跳转至 MainActivity
                        Intent intent = new Intent(LaunchActivity.this, LoginActivity.class);
                        startActivity(intent);
                        //结束当前的 Activity
                        LaunchActivity.this.finish();
                    }
                });
            }
        }).start();
    }
}
