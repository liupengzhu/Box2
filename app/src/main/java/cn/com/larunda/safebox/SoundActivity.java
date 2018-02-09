package cn.com.larunda.safebox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.larunda.safebox.R;

import cn.com.larunda.safebox.adapter.FootAdapter;
import cn.com.larunda.safebox.adapter.SoundInfoAdapter;
import cn.com.larunda.safebox.gson.BoxData;
import cn.com.larunda.safebox.gson.BoxInfo;
import cn.com.larunda.safebox.recycler.SoundInfo;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SoundActivity extends AppCompatActivity implements View.OnClickListener {

    private TitleBar titleBar;

    private SoundInfoAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private List<SoundInfo> soundInfoList = new ArrayList<>();

    public static final String BOX_URL = Util.URL + "box" + Util.TOKEN;
    public static final String IMG_URL = "http://safebox.dsmcase.com:90";
    private SharedPreferences preferences;
    private String token;

    private SwipeRefreshLayout refreshLayout;
    private RelativeLayout loodingErrorLayout;
    private ImageView loodingLayout;

    private EditText searchText;
    private ImageView cancelButton;
    private TextView ensureButton;

    private String search;

    private int page;
    private int lastVisibleItem;
    private int count;
    private static FootAdapter footAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        initView();
        initEvent();
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
            searchText = "&search=" + search;
        } else {
            searchText = "";
        }
        refreshLayout.setRefreshing(true);
        HttpUtil.sendGetRequestWithHttp(BOX_URL + token + searchText + "&page=1", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                        loodingErrorLayout.setVisibility(View.VISIBLE);
                        loodingLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final BoxInfo boxInfo = Util.handleBoxInfo(response.body().string());

                if (boxInfo != null && boxInfo.error == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initBoxList(boxInfo);
                            refreshLayout.setRefreshing(false);
                            loodingErrorLayout.setVisibility(View.GONE);
                            loodingLayout.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);

                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(SoundActivity.this, LoginActivity.class);
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
     * 解析数据
     *
     * @param boxInfo
     */
    private void initBoxList(BoxInfo boxInfo) {
        page = boxInfo.current_page + 1;
        count = boxInfo.per_page;
        if (boxInfo.boxDataList.size() == 0 || boxInfo.boxDataList.size() < count) {
            footAdapter.setHasMore(false);
            footAdapter.notifyDataSetChanged();
        }
        soundInfoList.clear();
        if (boxInfo.boxDataList != null) {
            for (BoxData boxData : boxInfo.boxDataList) {
                SoundInfo soundInfo = new SoundInfo();
                String img_url = null;
                if (boxData.f_pic != null) {
                    img_url = boxData.f_pic.replace('\\', ' ');
                    soundInfo.setBox_img(IMG_URL + img_url);
                } else {
                    soundInfo.setBox_img(null);
                }
                if (boxData.code != null) {
                    soundInfo.setBoxName(boxData.code);
                } else {
                    soundInfo.setBoxName("");
                }
                if (boxData.id != null) {
                    soundInfo.setId(boxData.id);
                }
                if (boxData.readNum != null && boxData.unReadNum != null) {
                    int total = Integer.parseInt(boxData.readNum) + Integer.parseInt(boxData.unReadNum);
                    soundInfo.setTotal("共" + total + "条录音");
                } else if (boxData.readNum != null) {
                    soundInfo.setTotal("共" + boxData.readNum + "条录音");
                } else if (boxData.unReadNum != null) {
                    soundInfo.setTotal("共" + boxData.unReadNum + "条录音");
                } else {
                    soundInfo.setTotal("共0条录音");
                }
                if (boxData.unReadNum != null) {
                    soundInfo.setUnRead(boxData.unReadNum);
                } else {
                    soundInfo.setUnRead("0");
                }

                soundInfoList.add(soundInfo);
            }

        }
        footAdapter.notifyDataSetChanged();
    }


    /**
     * 初始化点击事件
     */
    private void initEvent() {
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
        adapter.setSoundInfoOnClickListener(new SoundInfoAdapter.SoundInfoOnClickListener() {
            @Override
            public void onClick(View view, String id, String img, String code) {
                Intent intent = new Intent(SoundActivity.this, DetailedSoundActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("img", img);
                intent.putExtra("code", code);
                startActivity(intent);

            }
        });

        cancelButton.setOnClickListener(this);
        ensureButton.setOnClickListener(this);
    }

    private void initView() {
        preferences = PreferenceManager.getDefaultSharedPreferences(SoundActivity.this);
        token = preferences.getString("token", null);

        searchText = findViewById(R.id.sound_serch_edit);
        cancelButton = findViewById(R.id.sound_cancel_button);
        ensureButton = findViewById(R.id.sound_ensure_button);

        refreshLayout = findViewById(R.id.sound_swiper);
        loodingErrorLayout = findViewById(R.id.sound_loading_error_layout);
        loodingLayout = findViewById(R.id.sound_loading_layout);

        titleBar = findViewById(R.id.sound_title_bar);
        titleBar.setTextViewText("录音列表");
        titleBar.setRightButtonSrc(0);
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);

        adapter = new SoundInfoAdapter(this, soundInfoList);
        footAdapter = new FootAdapter(this, adapter);
        recyclerView = findViewById(R.id.sound_recycler);
        manager = new LinearLayoutManager(this);
        recyclerView.setAdapter(footAdapter);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //在newState为滑到底部时
                if (lastVisibleItem + 1 == footAdapter.getItemCount()) {
                    if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                        footAdapter.setHasMore(true);
                        footAdapter.notifyDataSetChanged();
                    }
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        if (soundInfoList.size() < count) {
                            search = null;
                            sendRequest();
                        } else {
                            sendAddRequest();
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
     * 加载下一页
     */
    private void sendAddRequest() {
        String searchText;
        if (search != null) {
            searchText = "&search=" + search;
        } else {
            searchText = "";
        }
        refreshLayout.setRefreshing(true);
        HttpUtil.sendGetRequestWithHttp(BOX_URL + token + searchText + "&page=" + page, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                        loodingErrorLayout.setVisibility(View.VISIBLE);
                        loodingLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final BoxInfo boxInfo = Util.handleBoxInfo(response.body().string());

                if (boxInfo != null && boxInfo.error == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addBoxList(boxInfo);
                            refreshLayout.setRefreshing(false);
                            loodingErrorLayout.setVisibility(View.GONE);
                            loodingLayout.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);

                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(SoundActivity.this, LoginActivity.class);
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
     * @param boxInfo
     */
    private void addBoxList(BoxInfo boxInfo) {
        page = boxInfo.current_page + 1;
        if (boxInfo.boxDataList.size() == 0) {
            footAdapter.setHasMore(false);
        }
        if (boxInfo.boxDataList != null) {
            for (BoxData boxData : boxInfo.boxDataList) {
                SoundInfo soundInfo = new SoundInfo();
                String img_url = null;
                if (boxData.f_pic != null) {
                    img_url = boxData.f_pic.replace('\\', ' ');
                    soundInfo.setBox_img(IMG_URL + img_url);
                } else {
                    soundInfo.setBox_img(null);
                }
                if (boxData.code != null) {
                    soundInfo.setBoxName(boxData.code);
                } else {
                    soundInfo.setBoxName("");
                }
                if (boxData.id != null) {
                    soundInfo.setId(boxData.id);
                }
                if (boxData.readNum != null && boxData.unReadNum != null) {
                    int total = Integer.parseInt(boxData.readNum) + Integer.parseInt(boxData.unReadNum);
                    soundInfo.setTotal("共" + total + "条录音");
                } else if (boxData.readNum != null) {
                    soundInfo.setTotal("共" + boxData.readNum + "条录音");
                } else if (boxData.unReadNum != null) {
                    soundInfo.setTotal("共" + boxData.unReadNum + "条录音");
                } else {
                    soundInfo.setTotal("共0条录音");
                }
                if (boxData.unReadNum != null) {
                    soundInfo.setUnRead(boxData.unReadNum);
                } else {
                    soundInfo.setUnRead("0");
                }

                soundInfoList.add(soundInfo);
            }

        }
        footAdapter.notifyDataSetChanged();
    }

    /**
     * 点击事件拦截
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sound_cancel_button:
                if (searchText != null) {
                    searchText.setText("");
                }
                break;
            case R.id.sound_ensure_button:
                if (searchText != null && !TextUtils.isEmpty(searchText.getText().toString().trim())) {
                    search = searchText.getText().toString().trim();
                    sendRequest();
                } else {
                    Toast.makeText(SoundActivity.this, "请输入搜索内容", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;

        }

    }


}
