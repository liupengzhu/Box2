package cn.com.larunda.safebox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.larunda.safebox.R;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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

public class DestinationActivity extends BaseActivity {

    private TitleBar titleBar;
    private int id;
    private String completedTime;
    private SharedPreferences preferences;
    private String token;

    private RecyclerView recyclerView;
    private DestinationAdapter adapter;
    private LinearLayoutManager manager;
    private List<Destination> destinationList = new ArrayList<>();
    private static final int ADD_DESTINATION = 1;

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
        sendRequest();
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

        recyclerView = findViewById(R.id.destination_recycler);
        adapter = new DestinationAdapter(this, destinationList);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }

    /**
     * 初始化点击事件
     */
    private void initEvent() {

        adapter.setItemButtonOnclickListener(new DestinationAdapter.ItemButtonOnclickListener() {
            @Override
            public void onClick(View v, int id) {
                Intent intent = new Intent(DestinationActivity.this, ManagerActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        //为RecycleView绑定触摸事件
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                //首先回调的方法 返回int表示是否监听该方向
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;//拖拽
                int swipeFlags = 0;//ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;//侧滑删除
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //滑动事件
                Collections.swap(destinationList, viewHolder.getAdapterPosition(), target.getAdapterPosition());
                adapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                List<Destination> list = adapter.getDestinationList();
                sendPostRequest(list);
                return false;
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

    /**
     * 发送网络请求
     */
    private void sendRequest() {
        HttpUtil.sendGetRequestWithHttp(Util.URL + "task/" + id + "/processes" + Util.TOKEN + token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

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
                destination.setOriginCity(info.getF_origin_city());
                destination.setDestinationCity(info.getF_destination());
                destination.setEndTime(info.getCompleted_at());
                destination.setStartTime(info.getCreated_at());
                for (int j = 0; j < info.getAddressee().size(); j++) {
                    DestinationInfo.AddresseeBean bean = info.getAddressee().get(j);
                    if (j == info.getAddressee().size() - 1) {
                        name.append(bean.getUser().getF_name() + "");
                    } else {
                        name.append(bean.getUser().getF_name() + "，");
                    }
                }
                destination.setPerson(name.toString());
                destinationList.add(destination);
            }
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
        }
    }

}
