package cn.com.larunda.safebox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.larunda.safebox.R;

import cn.com.larunda.safebox.adapter.EnclosureAdapter;
import cn.com.larunda.safebox.gson.EnclosureInfo;
import cn.com.larunda.safebox.gson.UserInfo;
import cn.com.larunda.safebox.recycler.Enclosure;
import cn.com.larunda.safebox.util.ActivityCollector;
import cn.com.larunda.safebox.util.BaseActivity;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import com.larunda.selfdialog.ConfirmDialog;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class EnclosureActivity extends BaseActivity implements View.OnClickListener {

    TitleBar titleBar;

    SwipeRefreshLayout refreshLayout;
    private RelativeLayout errorLayout;
    private SwipeMenuRecyclerView recyclerView;
    EnclosureAdapter adapter;
    LinearLayoutManager manager;
    List<Enclosure> enclosureList = new ArrayList<>();


    private EditText searchText;
    private ImageView cancelButton;
    private TextView ensureButton;

    public static final String ENCLOSURE_URL = Util.URL + "fence" + Util.TOKEN;
    private SharedPreferences preferences;
    private String token;

    private String search;

    private int page;
    private int maxPage;
    private ConfirmDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enclosure);

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
        search = null;
        sendRequest();


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
        cancelButton.setOnClickListener(this);
        ensureButton.setOnClickListener(this);

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

    }


    /**
     * 初始化View
     */
    private void initView() {

        preferences = PreferenceManager.getDefaultSharedPreferences(EnclosureActivity.this);
        token = preferences.getString("token", null);

        titleBar = findViewById(R.id.enclosure_title_bar);
        titleBar.setTextViewText("地理围栏");
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setRightButtonSrc(0);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);


        searchText = findViewById(R.id.enclosure_serch_edit);
        cancelButton = findViewById(R.id.enclosure_cancel_button);
        ensureButton = findViewById(R.id.enclosure_ensure_button);

        errorLayout = findViewById(R.id.enclosure_error_layout);

        adapter = new EnclosureAdapter(enclosureList);
        recyclerView = findViewById(R.id.enclosure_recycler);
        recyclerView.addItemDecoration(new DefaultItemDecoration(getResources()
                .getColor(R.color.line), MATCH_PARENT, 2));
        refreshLayout = findViewById(R.id.enclosure_swipe);

        manager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(manager);

        // 创建菜单：
        SwipeMenuCreator mSwipeMenuCreator = new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int viewType) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(EnclosureActivity.this); // 各种文字和图标属性设置。
                deleteItem.setBackground(R.color.log_wdu);
                deleteItem.setText("删除");
                deleteItem.setWidth(250);
                deleteItem.setTextColor(Color.WHITE);
                deleteItem.setHeight(MATCH_PARENT);
                rightMenu.addMenuItem(deleteItem); // 在Item右侧添加一个菜单。
            }
        };
        // 设置监听器。
        recyclerView.setSwipeMenuCreator(mSwipeMenuCreator);

        // 菜单点击监听。
        recyclerView.setSwipeMenuItemClickListener(new SwipeMenuItemClickListener() {
            @Override
            public void onItemClick(SwipeMenuBridge menuBridge) {
                // 任何操作必须先关闭菜单，否则可能出现Item菜单打开状态错乱。
                menuBridge.closeMenu();
                int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
                int id = enclosureList.get(adapterPosition).getId();
                String name = enclosureList.get(adapterPosition).getName();
                showDialog(id, name);
            }
        });

        recyclerView.setAdapter(adapter);
        setAdapterClick(adapter);

        recyclerView.useDefaultLoadMore(); // 使用默认的加载更多的View。
        recyclerView.setLoadMoreListener(new SwipeMenuRecyclerView.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                sendLoadRequest();
            }
        });
    }

    /**
     * 设置adapter的点击事件
     *
     * @param adapter
     */
    private void setAdapterClick(final EnclosureAdapter adapter) {

        adapter.setEnclosureOnClickListener(new EnclosureAdapter.EnclosureOnClickListener() {
            @Override
            public void onClick(View v, int id) {
                Intent intent = new Intent(EnclosureActivity.this, EnclosureInfoActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });


    }


    /**
     * 点击事件监听
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.enclosure_cancel_button:
                if (searchText != null) {
                    searchText.setText("");
                }
                break;
            case R.id.enclosure_ensure_button:
                if (searchText != null) {
                    search = searchText.getText().toString().trim();
                    sendRequest();
                }
                break;
            default:
                break;

        }
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
        HttpUtil.sendGetRequestWithHttp(ENCLOSURE_URL + token + searchText + "&page = 1" + Util.TYPE, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                        errorLayout.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String content = response.body().string();
                int code = response.code();
                if (code == 200 && Util.isGoodJson(content)) {
                    final EnclosureInfo info = Util.handleEnclosureInfo(content);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initEnclosureInfo(info);
                            refreshLayout.setRefreshing(false);
                            errorLayout.setVisibility(View.GONE);
                        }
                    });

                } else if (code == 401 || code == 412) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(EnclosureActivity.this, LoginActivity.class);
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
                            refreshLayout.setRefreshing(false);
                            try {
                                JSONObject js = new JSONObject(content);
                                Toast.makeText(EnclosureActivity.this, js.get("message") + "", Toast.LENGTH_SHORT).show();
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
     * 解析地理围栏信息
     */
    private void initEnclosureInfo(EnclosureInfo enclosureInfo) {
        page = enclosureInfo.getCurrent_page() + 1;
        maxPage = enclosureInfo.getLast_page();
        enclosureList.clear();
        for (EnclosureInfo.DataBean dataBean : enclosureInfo.getData()) {
            Enclosure enclosure = new Enclosure();
            if (dataBean.getF_name() != null) {
                enclosure.setName(dataBean.getF_name());
            } else {
                enclosure.setName("");
            }
            enclosure.setId(dataBean.getId());
            enclosureList.add(enclosure);
        }
        recyclerView.loadMoreFinish(enclosureInfo.getData().size() == 0, maxPage >= page);
        adapter.notifyDataSetChanged();
    }

    /**
     * 发送网络请求
     */
    private void sendLoadRequest() {
        String searchText;
        if (search != null) {
            searchText = "&search=" + search;
        } else {
            searchText = "";
        }
        HttpUtil.sendGetRequestWithHttp(ENCLOSURE_URL + token + searchText + "&page = " + page + Util.TYPE, new Callback() {
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
                    final EnclosureInfo info = Util.handleEnclosureInfo(content);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initLoadEnclosureInfo(info);
                        }
                    });

                } else if (code == 401 || code == 412) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(EnclosureActivity.this, LoginActivity.class);
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
                                Toast.makeText(EnclosureActivity.this, js.get("message") + "", Toast.LENGTH_SHORT).show();
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
     * 解析地理围栏信息
     */
    private void initLoadEnclosureInfo(EnclosureInfo enclosureInfo) {
        page = enclosureInfo.getCurrent_page() + 1;
        maxPage = enclosureInfo.getLast_page();
        for (EnclosureInfo.DataBean dataBean : enclosureInfo.getData()) {
            Enclosure enclosure = new Enclosure();
            if (dataBean.getF_name() != null) {
                enclosure.setName(dataBean.getF_name());
            } else {
                enclosure.setName("");
            }
            enclosure.setId(dataBean.getId());
            enclosureList.add(enclosure);
        }
        recyclerView.loadMoreFinish(enclosureInfo.getData().size() == 0, maxPage >= page);
        adapter.notifyDataSetChanged();
    }

    /**
     * 显示弹窗
     *
     * @param id
     * @param name
     */
    private void showDialog(final int id, String name) {
        dialog = new ConfirmDialog(this);
        dialog.setContentText("此操作将永久删除地理围栏：" + name);
        dialog.setNoOnclickListener(new ConfirmDialog.onNoOnclickListener() {
            @Override
            public void onNoClick(View v) {
                dialog.cancel();
            }
        });
        dialog.setYesOnclickListener(new ConfirmDialog.onYesOnclickListener() {
            @Override
            public void onYesClick(View v) {
                sendDeleteRequest(id);
                dialog.cancel();
            }
        });
        dialog.show();
    }

    /**
     * 发送删除请求
     *
     * @param id
     */
    private void sendDeleteRequest(int id) {
        refreshLayout.setRefreshing(true);
        JsonObject jsonObject = new JsonObject();
        HttpUtil.sendDeleteWithHttp(Util.URL + "fence/" + id
                + Util.TOKEN + token, jsonObject.toString(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                        Toast.makeText(EnclosureActivity.this, "网络异常!", Toast.LENGTH_SHORT).show();
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
                            if (content.equals("false")) {
                                refreshLayout.setRefreshing(false);
                                Toast.makeText(EnclosureActivity.this, "删除失败！", Toast.LENGTH_SHORT).show();
                            } else {
                                sendRequest();
                            }

                        } else if (code == 401 || code == 412) {
                            Intent intent = new Intent(EnclosureActivity.this, LoginActivity.class);
                            intent.putExtra("token_timeout", "登录超时");
                            preferences.edit().putString("token", null).commit();
                            startActivity(intent);
                            ActivityCollector.finishAllActivity();
                        } else if (code == 422) {
                            try {
                                JSONObject js = new JSONObject(content);
                                Toast.makeText(EnclosureActivity.this, js.get("message") + "", Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            refreshLayout.setRefreshing(false);
                        } else {
                            Toast.makeText(EnclosureActivity.this, "删除失败！", Toast.LENGTH_SHORT).show();
                            refreshLayout.setRefreshing(false);
                        }
                    }
                });

            }
        });
    }
}


