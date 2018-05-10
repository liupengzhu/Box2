package cn.com.larunda.safebox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.larunda.safebox.R;
import com.larunda.selfdialog.DateDialog;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.com.larunda.safebox.adapter.DetailedSoundAdapter;
import cn.com.larunda.safebox.adapter.TaskSoundAdapter;
import cn.com.larunda.safebox.gson.TaskLogInfo;
import cn.com.larunda.safebox.gson.TaskSoundInfo;
import cn.com.larunda.safebox.recycler.Task;
import cn.com.larunda.safebox.recycler.TaskSound;
import cn.com.larunda.safebox.util.ActivityCollector;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class TaskSoundActivity extends AppCompatActivity implements View.OnClickListener {

    private TitleBar titleBar;
    private int id;
    private SharedPreferences preferences;
    private String token;

    private EditText searchText;
    private ImageView cancelButton;
    private TextView ensureButton;
    private String search = "";
    private DateDialog dateDialog;
    private int maxPage;
    private int page;

    private SwipeRefreshLayout refreshLayout;
    private RelativeLayout errorLayout;
    private SwipeMenuRecyclerView recyclerView;
    private LinearLayoutManager manager;
    private List<TaskSound> taskSoundList = new ArrayList<>();
    private TaskSoundAdapter adapter;

    private MediaPlayer mediaPlayer;
    private int lastId;
    private CheckBox lastButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_sound);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        id = getIntent().getIntExtra("id", 0);
        initView();
        initEvent();
        sendRequest();
    }

    /**
     * 初始化view
     */
    private void initView() {

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = preferences.getString("token", null);

        titleBar = findViewById(R.id.sound_title_bar);
        titleBar.setTextViewText("录音列表");
        titleBar.setRightButtonSrc(0);
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);

        searchText = findViewById(R.id.sound_search_edit);
        cancelButton = findViewById(R.id.sound_cancel_button);
        ensureButton = findViewById(R.id.sound_ensure_button);
        dateDialog = new DateDialog(this);

        refreshLayout = findViewById(R.id.task_sound_swipe);
        errorLayout = findViewById(R.id.task_sound_error_layout);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                search = "";
                sendRequest();

            }
        });

        recyclerView = findViewById(R.id.task_sound_recycler);
        recyclerView.addItemDecoration(new DefaultItemDecoration(getResources()
                .getColor(R.color.line), MATCH_PARENT, 2));
        adapter = new TaskSoundAdapter(this, taskSoundList);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);


        recyclerView.useDefaultLoadMore(); // 使用默认的加载更多的View。
        recyclerView.setLoadMoreListener(new SwipeMenuRecyclerView.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                sendLoadRequest();
            }
        });
    }

    /**
     * 初始化点击事件
     */
    private void initEvent() {

        adapter.setTaskSoundOnClickListener(new TaskSoundAdapter.TaskSoundOnClickListener(){

            @Override
            public void onClick(View view, final String path, final int id, boolean isExist) {
                final CheckBox checkBox = (CheckBox) view;
                if (path != null && isExist) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (mediaPlayer == null) {
                                    mediaPlayer = new MediaPlayer();
                                    mediaPlayer.setDataSource(path);
                                    mediaPlayer.prepare();
                                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mp) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    checkBox.setChecked(false);
                                                }
                                            });


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
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        checkBox.setChecked(false);
                                                    }
                                                });
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
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(TaskSoundActivity.this,"录音不存在!",Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }
                    }).start();

                }

            }
        });

        cancelButton.setOnClickListener(this);
        ensureButton.setOnClickListener(this);
        searchText.setOnClickListener(this);

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

        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                search = searchText.getText().toString().trim();
                sendRequest();
                return false;
            }
        });
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    if (cancelButton != null) {
                        cancelButton.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (cancelButton != null) {
                        cancelButton.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        dateDialog.setOnCancelClickListener(new DateDialog.OnCancelClickListener() {
            @Override
            public void OnClick(View view) {
                dateDialog.cancel();
            }
        });
        dateDialog.setOnOkClickListener(new DateDialog.OnOkClickListener() {
            @Override
            public void OnClick(View view, String date) {
                searchText.setText(date + "");
                dateDialog.cancel();
            }
        });
    }

    /**
     * 点击事件处理
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
                if (searchText != null) {
                    search = searchText.getText().toString().trim();
                    sendRequest();
                }
                break;
            case R.id.sound_search_edit:
                dateDialog.show();
                break;
            default:
                break;
        }
    }

    /**
     * 发送网络请求
     */
    private void sendRequest() {
        refreshLayout.setRefreshing(true);
        HttpUtil.sendGetRequestWithHttp(Util.URL + "task/" + id + "/recordings" + Util.TOKEN
                + token + "&time=" + search + "&page=1", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        errorLayout.setVisibility(View.VISIBLE);
                        refreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String content = response.body().string();
                int code = response.code();
                if (code == 200 && Util.isGoodJson(content)) {
                    final TaskSoundInfo info = Util.handleTaskSoundInfo(content);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            parseInfo(info);
                            errorLayout.setVisibility(View.GONE);
                            refreshLayout.setRefreshing(false);
                        }
                    });
                } else if (code == 401 || code == 412) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(TaskSoundActivity.this, LoginActivity.class);
                            intent.putExtra("token_timeout", "登录超时");
                            preferences.edit().putString("token", null).commit();
                            startActivity(intent);
                            ActivityCollector.finishAllActivity();
                        }
                    });
                } else if (code == 422) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject js = new JSONObject(content);
                                Toast.makeText(TaskSoundActivity.this, js.get("message") + "", Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            refreshLayout.setRefreshing(false);
                        }
                    });

                }
            }
        });
    }

    /**
     * 解析服务器返回数据
     *
     * @param info
     */
    private void parseInfo(TaskSoundInfo info) {
        taskSoundList.clear();
        page = info.getCurrent_page() + 1;
        maxPage = info.getLast_page();
        if (info != null) {
            if (info.getData() != null) {
                for (TaskSoundInfo.DataBean dataBean : info.getData()) {
                    TaskSound taskSound = new TaskSound();
                    taskSound.setId(dataBean.getId());
                    taskSound.setCreateTime(dataBean.getCreated_at() != null ? dataBean.getCreated_at() : "");
                    taskSound.setPath(dataBean.getF_path() != null ? dataBean.getF_path() : "");
                    taskSound.setUpdateTime(dataBean.getUpdated_at() != null ? dataBean.getUpdated_at() : "");
                    taskSoundList.add(taskSound);
                }
            }
        }
        recyclerView.loadMoreFinish(info.getData().size() == 0, maxPage >= page);
        adapter.notifyDataSetChanged();
    }

    /**
     * 发送网络请求
     */
    private void sendLoadRequest() {
        HttpUtil.sendGetRequestWithHttp(Util.URL + "task/" + id + "/recordings" + Util.TOKEN
                + token + "&time=" + search + "&page=" + page, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        errorLayout.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String content = response.body().string();
                int code = response.code();
                if (code == 200 && Util.isGoodJson(content)) {
                    final TaskSoundInfo info = Util.handleTaskSoundInfo(content);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            parseLoadInfo(info);
                        }
                    });
                } else if (code == 401 || code == 412) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(TaskSoundActivity.this, LoginActivity.class);
                            intent.putExtra("token_timeout", "登录超时");
                            preferences.edit().putString("token", null).commit();
                            startActivity(intent);
                            ActivityCollector.finishAllActivity();
                        }
                    });
                } else if (code == 422) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject js = new JSONObject(content);
                                Toast.makeText(TaskSoundActivity.this, js.get("message") + "", Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            }
        });
    }

    /**
     * 解析服务器返回数据
     *
     * @param info
     */
    private void parseLoadInfo(TaskSoundInfo info) {
        page = info.getCurrent_page() + 1;
        maxPage = info.getLast_page();
        if (info != null) {
            if (info.getData() != null) {
                for (TaskSoundInfo.DataBean dataBean : info.getData()) {
                    TaskSound taskSound = new TaskSound();
                    taskSound.setId(dataBean.getId());
                    taskSound.setCreateTime(dataBean.getCreated_at() != null ? dataBean.getCreated_at() : "");
                    taskSound.setPath(dataBean.getF_path() != null ? dataBean.getF_path() : "");
                    taskSound.setUpdateTime(dataBean.getUpdated_at() != null ? dataBean.getUpdated_at() : "");
                    taskSoundList.add(taskSound);
                }
            }
        }
        recyclerView.loadMoreFinish(info.getData().size() == 0, maxPage >= page);
        adapter.notifyDataSetChanged();
    }
}
