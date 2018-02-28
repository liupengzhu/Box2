package cn.com.larunda.safebox;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.TimedText;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.larunda.horizontalprogressbar.HorizontalProgressBarWithNunber;
import com.larunda.safebox.R;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.com.larunda.safebox.adapter.BoxInfoSoundAdapter;
import cn.com.larunda.safebox.adapter.FootAdapter;
import cn.com.larunda.safebox.gson.DetailedSoundData;
import cn.com.larunda.safebox.gson.DetailedSoundInfo;
import cn.com.larunda.safebox.recycler.BoxInfoSound;
import cn.com.larunda.safebox.util.ActivityCollector;
import cn.com.larunda.safebox.util.BaseActivity;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class BoxInfoSoundActivity extends BaseActivity implements View.OnClickListener {

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

    private RadioButton weekButton;
    private RadioButton monthButton;
    private RadioButton yearButton;
    private RadioButton allButton;
    private String date;
    private SimpleDateFormat format;
    private Calendar c;

    private int page;
    private int lastVisibleItem;
    private int count;
    private FootAdapter footAdapter;

    private MediaPlayer mediaPlayer;
    private String lastId;
    private CheckBox lastButton;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (mediaPlayer != null) {
                        int position = mediaPlayer.getCurrentPosition();
                        int time = mediaPlayer.getDuration();
                        progressBar.setProgress(100 * position / time);
                        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");//初始化Formatter的转换格式。
                        String hms = formatter.format(position);
                        textView.setText(hms);
                    }
                    break;

            }
        }
    };
    private TextView textView;
    private HorizontalProgressBarWithNunber progressBar;
    private TextView lastTextView;
    private HorizontalProgressBarWithNunber lastProgressBar;
    private int total;

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
                date = null;
                allButton.setChecked(true);
                sendRequest();

            }
        });

        //每次创建时还没有网络数据 设置载入背景为可见
        loodingLayout.setVisibility(View.VISIBLE);
        loodingErrorLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        date = null;
        //后台线程发送消息进行更新进度条
        final int milliseconds = 100;
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        sleep(milliseconds);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    mHandler.sendEmptyMessage(0);
                }
            }
        }.start();

        sendRequest();
    }

    /**
     * 请求录音列表
     */
    private void sendRequest() {
        String timeText;
        if (date != null) {
            timeText = "&type=app&time=" + date;
        } else {
            timeText = "";
        }
        refreshLayout.setRefreshing(true);
        HttpUtil.sendGetRequestWithHttp(SOUND_URL + token + "&id=" + id + timeText + "&page=1" + Util.TYPE, new Callback() {
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
                                Intent intent = new Intent(BoxInfoSoundActivity.this, LoginActivity.class);
                                intent.putExtra("token_timeout", "登录超时");
                                preferences.edit().putString("token", null).commit();
                                startActivity(intent);
                                ActivityCollector.finishAllActivity();
                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * 初始化数据
     *
     * @param detailedSoundInfo
     */
    private void initSound(DetailedSoundInfo detailedSoundInfo) {
        page = detailedSoundInfo.current_page + 1;
        count = detailedSoundInfo.per_page;
        total = detailedSoundInfo.last_page;
        if (detailedSoundInfo.detailedSoundDataList.size() == 0 || detailedSoundInfo.detailedSoundDataList.size() < count) {
            footAdapter.setHasMore(false);
        }
        boxInfoSoundList.clear();
        if (detailedSoundInfo.detailedSoundDataList != null) {
            for (DetailedSoundData data : detailedSoundInfo.detailedSoundDataList) {
                BoxInfoSound boxInfoSound = new BoxInfoSound();
                if (data.id != null) {
                    boxInfoSound.setId(data.id);
                } else {
                    boxInfoSound.setId("");
                }
                if (data.f_record != null) {
                    boxInfoSound.setPath(Util.PATH + data.f_record);
                } else {
                    boxInfoSound.setPath(null);
                }
                if (data.f_is_play != null && data.f_is_play.equals("0")) {
                    boxInfoSound.setSoundIsPlay(false);
                } else {
                    boxInfoSound.setSoundIsPlay(true);
                }
                if (data.f_time_length != null) {
                    long ms = (Integer.parseInt(data.f_time_length)) * 1000;//毫秒数

                    SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");//初始化Formatter的转换格式。

                    String hms = formatter.format(ms);
                    boxInfoSound.setSoundTime(hms);
                } else {
                    boxInfoSound.setSoundTime("00:00");
                }
                if (data.created_at != null) {
                    boxInfoSound.setSoundDate(data.created_at);
                } else {
                    boxInfoSound.setSoundDate("");
                }
                if (data.is_exist != null && data.is_exist.equals("1")) {
                    boxInfoSound.setIs_exist(true);
                } else {
                    boxInfoSound.setIs_exist(false);
                }
                boxInfoSoundList.add(boxInfoSound);
            }
            footAdapter.notifyDataSetChanged();

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
        weekButton.setOnClickListener(this);
        monthButton.setOnClickListener(this);
        yearButton.setOnClickListener(this);
        allButton.setOnClickListener(this);
        adapter.setBoxInfoSoundOnClickListener(new BoxInfoSoundAdapter.BoxInfoSoundOnClickListener() {

            @Override
            public void onClick(View view, String path, String id, boolean isExist, int position) {
                final CheckBox checkBox = (CheckBox) view;
                if (path != null && isExist) {
                    textView = manager.findViewByPosition(position).findViewById(R.id.box_info_sound_current_time);
                    progressBar = manager.findViewByPosition(position).findViewById(R.id.box_info_sound_progress);
                    try {
                        if (mediaPlayer == null) {
                            mediaPlayer = new MediaPlayer();
                            mediaPlayer.setDataSource(path);
                            mediaPlayer.prepare();
                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    checkBox.setChecked(false);
                                    progressBar.setProgress(100);
                                    mediaPlayer = null;

                                }
                            });
                        } else {
                            if (lastId != id) {
                                mediaPlayer.stop();
                                mediaPlayer = null;
                                lastButton.setChecked(false);
                                lastTextView.setText("00:00");
                                lastProgressBar.setProgress(0);
                                mediaPlayer = new MediaPlayer();
                                mediaPlayer.setDataSource(path);
                                mediaPlayer.prepare();
                                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        checkBox.setChecked(false);
                                        progressBar.setProgress(100);
                                        mediaPlayer = null;
                                    }
                                });
                            }

                        }
                        if (checkBox.isChecked()) {
                            lastId = id;
                            lastButton = checkBox;
                            lastTextView = textView;
                            lastProgressBar = progressBar;
                            mediaPlayer.start();

                        } else {
                            lastId = id;
                            lastButton = checkBox;
                            lastTextView = textView;
                            lastProgressBar = progressBar;
                            mediaPlayer.pause();
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(BoxInfoSoundActivity.this, "录音不存在", Toast.LENGTH_SHORT).show();
                    checkBox.setChecked(false);
                }

            }

        });
    }


    /**
     * 初始化View
     */
    @SuppressLint("ResourceType")
    private void initView() {
        weekButton = findViewById(R.id.box_info_sound_search_week);
        monthButton = findViewById(R.id.box_info_sound_search_month);
        yearButton = findViewById(R.id.box_info_sound_search_year);
        allButton = findViewById(R.id.box_info_sound_search_all);

        refreshLayout = findViewById(R.id.box_info_sound_swipe);
        loodingErrorLayout = findViewById(R.id.box_info_sound_loading_error_layout);
        loodingLayout = findViewById(R.id.box_info_sound_loading_layout);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = preferences.getString("token", null);

        recyclerView = findViewById(R.id.box_info_sound_recycler);
        manager = new LinearLayoutManager(this);
        adapter = new BoxInfoSoundAdapter(this, boxInfoSoundList);
        footAdapter = new FootAdapter(this, adapter);
        recyclerView.setAdapter(footAdapter);
        recyclerView.setLayoutManager(manager);

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
                            if (boxInfoSoundList.size() < count) {
                                footAdapter.setHasMore(true);
                                sendRequest();
                            } else {
                                footAdapter.setHasMore(true);
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

        titleBar = findViewById(R.id.box_info_sound_title_bar);
        titleBar.setTextViewText("");
        titleBar.setRightButtonSrc(0);
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);


    }

    /**
     * 发送请求
     */
    private void sendAddRequest() {
        String timeText;
        if (date != null) {
            timeText = "&type=app&time=" + date;
        } else {
            timeText = "";
        }
        refreshLayout.setRefreshing(true);
        HttpUtil.sendGetRequestWithHttp(SOUND_URL + token + "&id=" + id + timeText + "&page=" + page + Util.TYPE, new Callback() {
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
                                Intent intent = new Intent(BoxInfoSoundActivity.this, LoginActivity.class);
                                intent.putExtra("token_timeout", "登录超时");
                                preferences.edit().putString("token", null).commit();
                                startActivity(intent);
                                ActivityCollector.finishAllActivity();
                            }
                        });
                    }
                }
            }
        });

    }

    /**
     * 添加数据
     *
     * @param detailedSoundInfo
     */
    private void addSound(DetailedSoundInfo detailedSoundInfo) {
        page = detailedSoundInfo.current_page + 1;
        if (detailedSoundInfo.detailedSoundDataList.size() == 0 || detailedSoundInfo.detailedSoundDataList.size() < count) {
            footAdapter.setHasMore(false);
        }
        if (detailedSoundInfo.detailedSoundDataList != null) {
            for (DetailedSoundData data : detailedSoundInfo.detailedSoundDataList) {
                BoxInfoSound boxInfoSound = new BoxInfoSound();
                if (data.id != null) {
                    boxInfoSound.setId(data.id);
                } else {
                    boxInfoSound.setId("");
                }
                if (data.f_record != null) {
                    boxInfoSound.setPath(Util.PATH + data.f_record);
                } else {
                    boxInfoSound.setPath(null);
                }
                if (data.f_is_play != null && data.f_is_play.equals("0")) {
                    boxInfoSound.setSoundIsPlay(false);
                } else {
                    boxInfoSound.setSoundIsPlay(true);
                }
                if (data.f_time_length != null) {
                    long ms = (Integer.parseInt(data.f_time_length)) * 1000;//毫秒数

                    SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");//初始化Formatter的转换格式。

                    String hms = formatter.format(ms);
                    boxInfoSound.setSoundTime(hms);
                } else {
                    boxInfoSound.setSoundTime("00:00");
                }
                if (data.created_at != null) {
                    boxInfoSound.setSoundDate(data.created_at);
                } else {
                    boxInfoSound.setSoundDate("");
                }
                if (data.is_exist != null && data.is_exist.equals("1")) {
                    boxInfoSound.setIs_exist(true);
                } else {
                    boxInfoSound.setIs_exist(false);
                }
                boxInfoSoundList.add(boxInfoSound);
            }
            footAdapter.notifyDataSetChanged();

        }

    }

    /**
     * 点击事件监听
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.box_info_sound_search_week:
                format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                c = Calendar.getInstance();
                c.add(Calendar.DATE, -7);
                date = format.format(c.getTime());
                sendRequest();
                break;
            case R.id.box_info_sound_search_month:
                format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                c = Calendar.getInstance();
                c.add(Calendar.MONTH, -1);
                date = format.format(c.getTime());
                sendRequest();
                break;
            case R.id.box_info_sound_search_year:
                format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                c = Calendar.getInstance();
                c.add(Calendar.YEAR, -1);
                date = format.format(c.getTime());
                sendRequest();
                break;
            case R.id.box_info_sound_search_all:
                date = null;
                sendRequest();
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
