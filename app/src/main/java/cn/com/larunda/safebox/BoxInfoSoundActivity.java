package cn.com.larunda.safebox;

import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.larunda.safebox.R;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

import java.util.ArrayList;
import java.util.List;

import cn.com.larunda.safebox.adapter.BoxInfoSoundAdapter;
import cn.com.larunda.safebox.recycler.BoxInfoSound;

public class BoxInfoSoundActivity extends AppCompatActivity {

    private TitleBar titleBar;

    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private BoxInfoSoundAdapter adapter;
    private List<BoxInfoSound> boxInfoSoundList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box_info_sound);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        initData();
        initView();
        iniEvent();
    }

    /**
     * 初始化点击事件
     */
    private void iniEvent() {
        titleBar.setOnClickListener(new TitleListener() {
            @Override
            public void onLeftButtonClickListener(View v) {

            }

            @Override
            public void onLeftBackButtonClickListener(View v) {
                finish();
            }

            @Override
            public void onRightButtonClickListener(View v) {

            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        for (int i = 0; i < 20; i++) {
            BoxInfoSound sound = new BoxInfoSound("新录音", "00:00:30", "2017-11-13");
            boxInfoSoundList.add(sound);
        }
    }

    /**
     * 初始化View
     */
    private void initView() {

        recyclerView = findViewById(R.id.box_info_sound_recycler);
        manager = new LinearLayoutManager(this);
        adapter = new BoxInfoSoundAdapter(this, boxInfoSoundList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);

        titleBar = findViewById(R.id.box_info_sound_title_bar);
        titleBar.setTextViewText("");
        titleBar.setRightButtonSrc(0);
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);


    }
}
