package cn.com.larunda.safebox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.larunda.safebox.R;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import cn.com.larunda.safebox.adapter.DestinationAdapter;
import cn.com.larunda.safebox.gson.DestinationInfo;
import cn.com.larunda.safebox.gson.TaskInfo;
import cn.com.larunda.safebox.recycler.Destination;
import cn.com.larunda.safebox.util.ActivityCollector;
import cn.com.larunda.safebox.util.BaseActivity;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class DestinationActivity extends BaseActivity {

    private final String URL = Util.URL + "fence/map" + Util.TOKEN;
    private TitleBar titleBar;
    private int id;
    private String completedTime;
    private SharedPreferences preferences;
    private String token;

    private SwipeRefreshLayout refreshLayout;
    private RelativeLayout errorLayout;
    private SwipeMenuRecyclerView recyclerView;
    private DestinationAdapter adapter;
    private LinearLayoutManager manager;
    private List<Destination> destinationList = new ArrayList<>();
    private static final int ADD_DESTINATION = 1;
    private static final int PERSON_MANAGER = 2;
    private int total;
    private JSONObject areaObject;
    private String areaString;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        id = getIntent().getIntExtra("id", 0);
        completedTime = getIntent().getStringExtra("completedTime");
        initView();
        initEvent();
        send();

    }

    /**
     * 初始化view
     */
    private void initView() {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = preferences.getString("token", null);


        titleBar = findViewById(R.id.destination_title_bar);
        titleBar.setTextViewText("目的地列表");
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);
        if (completedTime == null) {
            titleBar.setRightButtonSrc(R.drawable.add);
        } else {
            titleBar.setRightButtonSrc(0);
        }

        refreshLayout = findViewById(R.id.destination_swipe);
        errorLayout = findViewById(R.id.destination_error_layout);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sendRequest();

            }
        });

        recyclerView = findViewById(R.id.destination_recycler);
        recyclerView.addItemDecoration(new DefaultItemDecoration(getResources()
                .getColor(R.color.line), MATCH_PARENT, 2));
        adapter = new DestinationAdapter(this, destinationList);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        recyclerView.useDefaultLoadMore(); // 使用默认的加载更多的View。
        recyclerView.setLoadMoreListener(new SwipeMenuRecyclerView.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                //sendLoadRequest();
            }
        });
    }

    /**
     * 初始化点击事件
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initEvent() {

        adapter.setItemButtonOnclickListener(new DestinationAdapter.ItemButtonOnclickListener() {
            @Override
            public void onClick(View v, int processId) {
                Intent intent = new Intent(DestinationActivity.this, ManagerActivity.class);
                intent.putExtra("processId", processId);
                intent.putExtra("taskId", id);
                startActivityForResult(intent, PERSON_MANAGER);
            }
        });

        adapter.setItemBoxButtonOnclickListener(new DestinationAdapter.ItemBoxButtonOnclickListener() {
            @Override
            public void onClick(View v, int processId, int position) {
                Destination destination = destinationList.get(position);
                Intent intent = new Intent(DestinationActivity.this, BoxManagerActivity.class);
                intent.putExtra("processId", processId);
                intent.putExtra("taskId", id);
                intent.putExtra("interval", destination.getInterval());
                intent.putExtra("isUseLeaving", destination.getUseLeaving());
                intent.putExtra("isUseDefence", destination.getUseDefence());
                intent.putExtra("releaseTime", destination.getReleaseTime());
                intent.putExtra("area", destination.getArea());
                intent.putExtra("areaString", areaString);
                intent.putExtra("areaId",destination.getAreaId());
                startActivityForResult(intent, PERSON_MANAGER);
            }
        });

        //为RecycleView绑定触摸事件
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                //首先回调的方法 返回int表示是否监听该方向
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;//拖拽
                int swipeFlags = 0;//ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;//侧滑删除
                refreshLayout.setEnabled(false);
                total = 0;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //滑动事件
                if (viewHolder.getAdapterPosition() < destinationList.size() && target.getAdapterPosition() < destinationList.size()) {
                    Collections.swap(destinationList, viewHolder.getAdapterPosition(), target.getAdapterPosition());
                    adapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                    List<Destination> list = adapter.getDestinationList();
                    sendPostRequest(list);
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                /*//侧滑事件
                destinationList.remove(viewHolder.getAdapterPosition());
                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());*/
            }

            @Override
            public boolean isLongPressDragEnabled() {
                //是否可拖拽
                return true;
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);
                total++;
                if (total == 2) {
                    refreshLayout.setEnabled(true);
                }
            }
        });
        helper.attachToRecyclerView(recyclerView);


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
                Intent intent = new Intent(DestinationActivity.this, AddDestinationActivity.class);
                intent.putExtra("id", id);
                startActivityForResult(intent, ADD_DESTINATION);
            }
        });
    }

    /**
     * 提交排序
     *
     * @param list
     */
    private void sendPostRequest(List<Destination> list) {
        JSONObject jsonObject = new JSONObject();
        try {
            for (int i = 0; i < list.size(); i++) {
                jsonObject.put(list.get(i).getId() + "", i);
            }
            HttpUtil.sendPostRequestWithHttp(Util.URL + "task/" + id + "/process/sorting" + Util.TOKEN + token, jsonObject.toString(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(DestinationActivity.this, "网络异常!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String content = response.body().string();
                    final int code = response.code();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (code == 200) {

                            } else if (code == 401 || code == 412) {
                                Intent intent = new Intent(DestinationActivity.this, LoginActivity.class);
                                intent.putExtra("token_timeout", "登录超时");
                                preferences.edit().putString("token", null).commit();
                                startActivity(intent);
                                ActivityCollector.finishAllActivity();
                            }
                        }
                    });
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void send() {
        HttpUtil.sendGetRequestWithHttp(URL + token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DestinationActivity.this, "网络异常!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String content = response.body().string();
                final int code = response.code();
                if (code == 200) {
                    try {
                        areaObject = new JSONObject(content);
                        areaString = content;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                sendRequest();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (code == 401 || code == 412) {
                                Intent intent = new Intent(DestinationActivity.this, LoginActivity.class);
                                intent.putExtra("token_timeout", "登录超时");
                                preferences.edit().putString("token", null).commit();
                                startActivity(intent);
                                ActivityCollector.finishAllActivity();
                            } else if (code == 422) {
                                try {
                                    JSONObject js = new JSONObject(content);
                                    Toast.makeText(DestinationActivity.this, js.get("message") + "", Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 发送网络请求
     */
    private void sendRequest() {
        refreshLayout.setRefreshing(true);
        HttpUtil.sendGetRequestWithHttp(Util.URL + "task/" + id + "/processes" + Util.TOKEN + token, new Callback() {
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            parseInfo(content);
                            errorLayout.setVisibility(View.GONE);
                            refreshLayout.setRefreshing(false);
                        }
                    });
                } else if (code == 401 || code == 412) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(DestinationActivity.this, LoginActivity.class);
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
                                Toast.makeText(DestinationActivity.this, js.get("message") + "", Toast.LENGTH_SHORT).show();
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
     * @param content
     */
    private void parseInfo(String content) {
        destinationList.clear();
        StringBuffer name = new StringBuffer();
        try {
            JSONArray jsonArray = new JSONArray(content);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                DestinationInfo info = Util.handleDestinationInfo(jsonObject.toString());
                Destination destination = new Destination();
                name.setLength(0);
                destination.setId(info.getId());
                destination.setOriginCity(Util.arrayToString(info.getF_origin_city()));
                destination.setDestinationCity(info.getF_destination());
                destination.setEndTime(info.getCompleted_at());
                destination.setStartTime(info.getCreated_at());
                for (int j = 0; j < info.getAddressee().size(); j++) {
                    DestinationInfo.AddresseeBean bean = info.getAddressee().get(j);
                    if (j == info.getAddressee().size() - 1) {
                        if (bean.getUser() != null) {
                            name.append(bean.getUser().getF_name() + "");
                        }
                    } else {
                        if (bean.getUser() != null) {
                            name.append(bean.getUser().getF_name() + "，");
                        }
                    }
                }
                if (areaObject != null) {
                    destination.setArea(info.getFence_id() != null ? areaObject.getString(info.getFence_id()) : null);
                    destination.setAreaId(info.getFence_id() != null ? Integer.parseInt(info.getFence_id()) : 0);
                }
                destination.setReleaseTime(info.getF_release_time());
                destination.setInterval(info.getF_upload_interval());
                destination.setUseDefence(info.getF_use_defense());
                destination.setUseLeaving(info.getF_use_dislocation());
                destination.setPerson(name.toString());
                destinationList.add(destination);
            }
            recyclerView.loadMoreFinish(jsonArray.length() == 0, false);
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {

            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ADD_DESTINATION:
                if (resultCode == RESULT_OK) {
                    sendRequest();
                }
                break;
            case PERSON_MANAGER:
                if (resultCode == RESULT_OK) {
                    sendRequest();
                }
                break;
        }
    }

}
