package cn.com.larunda.safebox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.text.UnicodeSetSpanner;
import android.media.MediaPlayer;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.larunda.safebox.R;
import com.larunda.selfdialog.DateDialog;

import cn.com.larunda.safebox.adapter.DetailedSoundAdapter;
import cn.com.larunda.safebox.adapter.FootAdapter;
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
    public static final String SOUND_URL = Util.URL + "box/record" + Util.TOKEN;
    private SharedPreferences preferences;
    private String token;
    private String code;
    private String img;
    private SwipeRefreshLayout refreshLayout;
    private RelativeLayout loodingErrorLayout;
    private ImageView loodingLayout;

    private int page;
    private int lastVisibleItem;
    private int count;
    private FootAdapter footAdapter;
    private MediaPlayer mediaPlayer;
    private String lastId;
    private CheckBox lastButton;
    private Button searchButton;
    private DateDialog dateDialog;
    private String search;
    private int total;

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
     * 发送网络请求
     */
    private void sendRequest() {
        String searchText;
        if (search != null) {
            searchText = "&type=app&time=" + search;
        } else {
            searchText = "";
        }
        refreshLayout.setRefreshing(true);
        HttpUtil.sendGetRequestWithHttp(SOUND_URL + token + "&id=" + id + searchText + "&page=1" + Util.TYPE, new Callback() {
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
                if (Util.isGoodJson(content)) {
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
                                Intent intent = new Intent(DetailedSoundActivity.this, LoginActivity.class);
                                intent.putExtra("token_timeout", "登录超时");
                                preferences.edit().putString("token", null).commit();
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
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
        page = detailedSoundInfo.current_page + 1;
        count = detailedSoundInfo.per_page;
        total = detailedSoundInfo.last_page;
        if (detailedSoundInfo.detailedSoundDataList.size() == 0 || detailedSoundInfo.detailedSoundDataList.size() < count) {
            footAdapter.setHasMore(false);
        }
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
                if (detailedSoundData.is_exist != null && detailedSoundData.is_exist.equals("1")) {
                    detailedSound.setExist(true);
                } else {
                    detailedSound.setExist(false);
                }
                if (detailedSoundData.f_record != null) {
                    detailedSound.setPath(Util.PATH + detailedSoundData.f_record);
                } else {
                    detailedSound.setPath(null);
                }

                detailedSoundList.add(detailedSound);

            }
        }
        footAdapter.notifyDataSetChanged();
    }

    /**
     * 初始化点击事件
     */
    private void iniEvent() {
        back_Button.setOnClickListener(this);
        searchButton.setOnClickListener(this);
        dateDialog.setOnCancelClickListener(new DateDialog.OnCancelClickListener() {
            @Override
            public void OnClick(View view) {
                dateDialog.cancel();
            }
        });
        dateDialog.setOnOkClickListener(new DateDialog.OnOkClickListener() {
            @Override
            public void OnClick(View view, String date) {
                search = date;
                sendRequest();
                dateDialog.cancel();
            }
        });

        adapter.setDetailedSoundOnClickListener(new DetailedSoundAdapter.DetailedSoundOnClickListener() {

            @Override
            public void onClick(View view, String path, String id, boolean isExist) {
                final CheckBox checkBox = (CheckBox) view;
                if (path != null && isExist) {
                    try {
                        if (mediaPlayer == null) {
                            mediaPlayer = new MediaPlayer();
                            mediaPlayer.setDataSource(path);
                            mediaPlayer.prepare();
                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    checkBox.setChecked(false);

                                }
                            });
                        } else {
                            if (lastId != id) {
                                mediaPlayer.stop();
                                mediaPlayer = null;
                                lastButton.setChecked(false);
                                mediaPlayer = new MediaPlayer();
                                mediaPlayer.setDataSource(path);
                                mediaPlayer.prepare();
                                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        checkBox.setChecked(false);
                                        mediaPlayer = null;
                                    }
                                });
                            }

                        }
                        if (checkBox.isChecked()) {
                            lastId = id;
                            lastButton = checkBox;
                            mediaPlayer.start();

                        } else {
                            lastId = id;
                            lastButton = checkBox;
                            mediaPlayer.pause();
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(DetailedSoundActivity.this, "录音不存在", Toast.LENGTH_SHORT).show();
                    checkBox.setChecked(false);
                }

            }
        });
    }

    /**
     * 初始化View
     */
    private void initView() {

        preferences = PreferenceManager.getDefaultSharedPreferences(DetailedSoundActivity.this);
        token = preferences.getString("token", null);

        searchButton = findViewById(R.id.detailed_sound_search_button);
        dateDialog = new DateDialog(this);

        refreshLayout = findViewById(R.id.detailed_sound_swiper);
        loodingErrorLayout = findViewById(R.id.detailed_sound_loading_error_layout);
        loodingLayout = findViewById(R.id.detailed_sound_loading_layout);

        back_Button = findViewById(R.id.detailed_sound_back);
        imageView = findViewById(R.id.detailed_sound_item_play_img);
        recyclerView = findViewById(R.id.detailed_sound_recycler);
        adapter = new DetailedSoundAdapter(this, detailedSoundList);
        footAdapter = new FootAdapter(this, adapter);
        manager = new LinearLayoutManager(this);
        recyclerView.setAdapter(footAdapter);
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
            Glide.with(this).load(img).error(R.mipmap.box).into(box_img);
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (page <= total) {
                    //在newState为滑到底部时
                    if (lastVisibleItem + 1 == footAdapter.getItemCount()) {
                        if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                            footAdapter.setHasMore(true);
                            footAdapter.notifyDataSetChanged();
                        }
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            if (detailedSoundList.size() < count) {
                                sendRequest();
                            } else {
                                sendAddRequest();
                            }
                        }

                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = manager.findLastVisibleItemPosition();
            }
        });

    }

    /**
     * 请求下一页参数
     */
    private void sendAddRequest() {
        String searchText;
        if (search != null) {
            searchText = "&type=app&time=" + search;
        } else {
            searchText = "";
        }
        refreshLayout.setRefreshing(true);
        HttpUtil.sendGetRequestWithHttp(SOUND_URL + token + "&id=" + id + searchText + "&page=" + page + Util.TYPE, new Callback() {
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
                if (Util.isGoodJson(content)) {
                    final DetailedSoundInfo detailedSoundInfo = Util.handleDetailedSoundInfo(content);
                    if (detailedSoundInfo != null && detailedSoundInfo.error == null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addSound(detailedSoundInfo);
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
                                Intent intent = new Intent(DetailedSoundActivity.this, LoginActivity.class);
                                intent.putExtra("token_timeout", "登录超时");
                                preferences.edit().putString("token", null).commit();
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                }
            }
        });
    }

    private void addSound(DetailedSoundInfo detailedSoundInfo) {

        page = detailedSoundInfo.current_page + 1;
        if (detailedSoundInfo.detailedSoundDataList.size() == 0 || detailedSoundInfo.detailedSoundDataList.size() < count) {
            footAdapter.setHasMore(false);
        }
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

                if (detailedSoundData.is_exist != null && detailedSoundData.is_exist.equals("1")) {
                    detailedSound.setExist(true);
                } else {
                    detailedSound.setExist(false);
                }
                if (detailedSoundData.f_record != null) {
                    detailedSound.setPath(Util.PATH + detailedSoundData.f_record);
                } else {
                    detailedSound.setPath(null);
                }

                detailedSoundList.add(detailedSound);

            }
        }
        footAdapter.notifyDataSetChanged();
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
            case R.id.detailed_sound_search_button:
                dateDialog.show();
                break;
            default:
                break;


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}
