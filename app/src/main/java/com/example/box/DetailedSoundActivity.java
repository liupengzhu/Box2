package com.example.box;

import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

public class DetailedSoundActivity extends AppCompatActivity implements View.OnClickListener {

    Button back_Button;
    CheckBox play_Button;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_sound);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);

            initView();

        }
    }

    /**
     * 初始化View
     */
    private void initView() {
        back_Button = findViewById(R.id.detailed_sound_back);
        play_Button = findViewById(R.id.detailed_sound_item_play);
        imageView = findViewById(R.id.detailed_sound_item_play_img);

        back_Button.setOnClickListener(this);
        play_Button.setOnClickListener(this);
    }

    /**
     * 监听点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.detailed_sound_back:
                finish();
                break;
            case R.id.detailed_sound_item_play:
                imageView.setVisibility(View.GONE);
                break;


        }
    }
}
