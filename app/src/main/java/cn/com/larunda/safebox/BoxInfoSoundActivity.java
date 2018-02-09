package cn.com.larunda.safebox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.larunda.safebox.R;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.com.larunda.safebox.adapter.BoxInfoSoundAdapter;
import cn.com.larunda.safebox.gson.DetailedSoundData;
import cn.com.larunda.safebox.gson.DetailedSoundInfo;
import cn.com.larunda.safebox.recycler.BoxInfoSound;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class BoxInfoSoundActivity extends AppCompatActivity {

    private TitleBar titleBar;

    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private BoxInfoSoundAdapter adapter;
    private List<BoxInfoSound> boxInfoSoundList = new ArrayList<>();

    private SwipeRefreshLayout refreshLayout;
    private RelativeLayout loodingErrorLayout;
    private ImageView loodingLayout;

    private String id;
    public static final String SOUND_URL = Util.URL + "box/record" + Util.TOKEN;
    private SharedPreferences preferences;
    private String token;
    private String search;


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
        id = getIntent().getStringExtra("id");
        initView();
        iniEvent();
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                search = null;
                sendRequest();

            }
        });

        //每次创建时还没有网络数据 设置载入背景为可见
        loodingLayout.setVisibility(View.VISIBLE);
        loodingErrorLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        search = null;
        sendRequest();
    }

    /**
     * 请求录音列表
     */
    private void sendRequest() {
        refreshLayout.setRefreshing(true);
        HttpUtil.sendGetRequestWithHttp(SOUND_URL + token + "&id=" + id, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                        loodingLayout.setVisibility(View.GONE);
                        loodingErrorLayout.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String content = response.body().string();
                Log.d("main", content);
                final DetailedSoundInfo detailedSoundInfo = Util.handleDetailedSoundInfo(content);
                if (detailedSoundInfo != null && detailedSoundInfo.error == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initSound(detailedSoundInfo);
                            refreshLayout.setRefreshing(false);
                            loodingLayout.setVisibility(View.GONE);
                            loodingErrorLayout.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(BoxInfoSoundActivity.this, LoginActivity.class);
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

    private void initSound(DetailedSoundInfo detailedSoundInfo) {
        boxInfoSoundList.clear();
        if (detailedSoundInfo.detailedSoundDataList != null) {
            for (DetailedSoundData data : detailedSoundInfo.detailedSoundDataList) {
                BoxInfoSound boxInfoSound = new BoxInfoSound();
                if (data.id != null) {
                    boxInfoSound.setSoundName(data.id);
                } else {
                    boxInfoSound.setId("");
                }
                if (data.f_record != null) {
                    boxInfoSound.setPath(data.f_record);
                } else {
                    boxInfoSound.setPath("");
                }
                if (data.f_is_play != null && data.f_is_play.equals("0")) {
                    boxInfoSound.setSoundIsPlay(false);
                } else {
                    boxInfoSound.setSoundIsPlay(true);
                }
                if (data.f_time_length != null) {
                    boxInfoSound.setSoundTime(data.f_time_length);
                } else {
                    boxInfoSound.setSoundTime("");
                }
                if (data.created_at != null) {
                    boxInfoSound.setSoundDate(data.created_at);
                } else {
                    boxInfoSound.setSoundDate("");
                }
                if (data.is_exist != null && data.is_exist.equals("1")) {
                    boxInfoSound.setSoundIsPlay(true);
                } else {
                    boxInfoSound.setSoundIsPlay(false);
                }
                boxInfoSoundList.add(boxInfoSound);
            }
            adapter.notifyDataSetChanged();

        }

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
     * 初始化View
     */
    private void initView() {

        refreshLayout = findViewById(R.id.box_info_sound_swipe);
        loodingErrorLayout = findViewById(R.id.box_info_sound_loading_error_layout);
        loodingLayout = findViewById(R.id.box_info_sound_loading_layout);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = preferences.getString("token", null);

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
