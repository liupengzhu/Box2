package cn.com.larunda.safebox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.larunda.safebox.R;

import cn.com.larunda.safebox.adapter.DetailedSoundAdapter;
import cn.com.larunda.safebox.gson.DetailedSoundData;
import cn.com.larunda.safebox.gson.DetailedSoundInfo;
import cn.com.larunda.safebox.recycler.DetailedSound;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DetailedSoundActivity extends AppCompatActivity implements View.OnClickListener {

    Button back_Button;
    ImageView imageView;
    TextView textView;
    ImageView box_img;
    RecyclerView recyclerView;
    DetailedSoundAdapter adapter;
    LinearLayoutManager manager;
    List<DetailedSound> detailedSoundList = new ArrayList<>();
    private String id;
    public static final String SOUND_URL = "http://safebox.dsmcase.com:90/api/box/record?_token=";
    private SharedPreferences preferences;
    private String token;
    private String code;
    private String img;

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

        }
        id = getIntent().getStringExtra("id");
        code = getIntent().getStringExtra("code");
        img = getIntent().getStringExtra("img");
        initView();
        iniEvent();

        sendRequest();
    }

    /**
     * 发送网络请求
     */
    private void sendRequest() {
        HttpUtil.sendGetRequestWithHttp(SOUND_URL + token + "&id=" + id, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final DetailedSoundInfo detailedSoundInfo = Util.handleDetailedSoundInfo(response.body().string());
                if (detailedSoundInfo != null && detailedSoundInfo.error == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initSound(detailedSoundInfo);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(DetailedSoundActivity.this, LoginActivity.class);
                            intent.putExtra("token_timeout", "登录超时");
                            preferences.edit().putString("token", null).commit();
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }
        });
    }

    /**
     * 解析声音信息
     *
     * @param detailedSoundInfo
     */
    private void initSound(DetailedSoundInfo detailedSoundInfo) {
        detailedSoundList.clear();
        if (detailedSoundInfo.detailedSoundDataList != null) {
            for (DetailedSoundData detailedSoundData : detailedSoundInfo.detailedSoundDataList) {
                DetailedSound detailedSound = new DetailedSound();
                if (detailedSoundData.id != null) {
                    detailedSound.setSoundId(detailedSoundData.id);
                } else {
                    detailedSound.setSoundId("");
                }
                if (detailedSoundData.created_at != null) {
                    detailedSound.setTime(detailedSoundData.created_at);
                } else {
                    detailedSound.setTime("");
                }
                if (detailedSoundData.f_is_play != null) {
                    if (detailedSoundData.f_is_play.equals("0")) {
                        detailedSound.setDownload(false);
                    } else {
                        detailedSound.setDownload(true);
                    }
                } else {
                    detailedSound.setDownload(false);
                }
                detailedSoundList.add(detailedSound);

            }
        }
        adapter.notifyDataSetChanged();
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
     * 初始化View
     */
    private void initView() {
        preferences = PreferenceManager.getDefaultSharedPreferences(DetailedSoundActivity.this);
        token = preferences.getString("token", null);

        back_Button = findViewById(R.id.detailed_sound_back);
        imageView = findViewById(R.id.detailed_sound_item_play_img);
        recyclerView = findViewById(R.id.detailed_sound_recycler);
        adapter = new DetailedSoundAdapter(this, detailedSoundList);
        manager = new LinearLayoutManager(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        textView = findViewById(R.id.detailed_sound_text);
        box_img = findViewById(R.id.detailed_sound_box_img);
        if (code != null) {
            textView.setText(code);
        } else {
            textView.setText("");
        }
        if (img != null) {
            Glide.with(this).load(img).into(box_img);
        }

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
