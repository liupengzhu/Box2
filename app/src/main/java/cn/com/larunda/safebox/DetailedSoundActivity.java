package cn.com.larunda.safebox;

import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.larunda.safebox.R;

import cn.com.larunda.safebox.adapter.DetailedSoundAdapter;
import cn.com.larunda.safebox.recycler.DetailedSound;

import java.util.ArrayList;
import java.util.List;

public class DetailedSoundActivity extends AppCompatActivity implements View.OnClickListener {

    Button back_Button;
    ImageView imageView;
    RecyclerView recyclerView;
    DetailedSoundAdapter adapter;
    LinearLayoutManager manager;
    List<DetailedSound> detailedSoundList = new ArrayList<>();

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

            intData();
            initView();
            iniEvent();

        }
    }

    /**
     * 初始化点击事件
     */
    private void iniEvent() {
        back_Button.setOnClickListener(this);
        adapter.setDetailedSoundOnClickListener(new DetailedSoundAdapter.DetailedSoundOnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    /**
     * 测试方法
     */
    private void intData() {
        for (int i = 0; i < 20; i++) {
            DetailedSound detailedSound = new DetailedSound("录音ID",
                    "2017/11/21 12:00:00", false);
            detailedSoundList.add(detailedSound);
        }
    }

    /**
     * 初始化View
     */
    private void initView() {
        back_Button = findViewById(R.id.detailed_sound_back);
        imageView = findViewById(R.id.detailed_sound_item_play_img);
        recyclerView = findViewById(R.id.detailed_sound_recycler);
        adapter = new DetailedSoundAdapter(this, detailedSoundList);
        manager = new LinearLayoutManager(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));


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
            default:
                break;


        }
    }
}
