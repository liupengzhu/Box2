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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.larunda.safebox.R;

import cn.com.larunda.safebox.adapter.FootAdapter;
import cn.com.larunda.safebox.adapter.UserInfoAdapter;
import cn.com.larunda.safebox.gson.UserData;
import cn.com.larunda.safebox.gson.UserInfo;
import cn.com.larunda.safebox.recycler.MyUserInfo;
import cn.com.larunda.safebox.util.ActivityCollector;
import cn.com.larunda.safebox.util.BaseActivity;
import cn.com.larunda.safebox.util.HttpUtil;

import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UserInfoActivity extends BaseActivity implements View.OnClickListener {

    public static final String USER_INFO_URL = Util.URL + "user" + Util.TOKEN;
    public static final String IMG_URL = Util.PATH;
    public static final int REQUEST = 2;
    private TitleBar titleBar;

    private List<MyUserInfo> myUserInfoList = new ArrayList<>();
    private LinearLayoutManager manager;
    private RecyclerView recyclerView;
    private UserInfoAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private RelativeLayout loodingErrorLayout;
    private ImageView loodingLayout;
    private SharedPreferences preferences;
    private String token;

    private EditText searchText;
    private ImageView cancelButton;
    private TextView ensureButton;

    /**
     * 是否在长按状态
     */
    public boolean isLongClick = false;

    /**
     * 是否在全选状态
     */
    private boolean isAllChecked = false;
    private ImageView allCheckedImage;
    private TextView allCheckedText;
    private RelativeLayout top_layout;
    private LinearLayout bottom_layout;

    private Button deleteButton;
    private ArrayList<String> idList = new ArrayList<>();
    private String serch;

    private int page;
    private int lastVisibleItem;
    private int count;
    private static FootAdapter footAdapter;
    private int total;
    private int lastPosition;
    private MyUserInfo lastUser;
    private static final int REQUEST_USERINFO = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
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
                serch = null;
                sendRequest();

            }
        });

        //每次创建时还没有网络数据 设置载入背景为可见
        loodingLayout.setVisibility(View.VISIBLE);
        loodingErrorLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        serch = null;
        sendRequest();

    }

    /**
     * 初始化点击事件
     */
    private void initEvent() {
        adapter.setUserInfoOnClickListener(new UserInfoAdapter.UserInfoOnClickListener() {
            @Override
            public void onClick(View v, String id, int position) {
                Intent intent = new Intent(UserInfoActivity.this, EditUserActivity.class);
                intent.putExtra("id", id);
                lastPosition = position;
                lastUser = adapter.getMyUserInfoList().get(lastPosition);
                startActivityForResult(intent, REQUEST_USERINFO);
            }
        });

        adapter.setUserInfoOnLongClickListener(new UserInfoAdapter.UserInfoOnLongClickListener() {
            @Override
            public void onClick(View v) {
                isLongClick = true;
                adapter.setCheckedLayout(true);
                footAdapter.notifyDataSetChanged();
                top_layout.setVisibility(View.GONE);
                bottom_layout.setVisibility(View.VISIBLE);
            }
        });

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

                Intent intent = new Intent(UserInfoActivity.this, AddUserActivity.class);
                startActivityForResult(intent, REQUEST);
            }
        });

        cancelButton.setOnClickListener(this);
        ensureButton.setOnClickListener(this);

        allCheckedImage.setOnClickListener(this);
        allCheckedText.setOnClickListener(this);
        deleteButton.setOnClickListener(this);

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
                            if (myUserInfoList.size() < count) {
                                serch = null;
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

        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                serch = searchText.getText().toString().trim();
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
     * 发送加载下一页
     */
    private void sendAddRequest() {
        String searchText;
        if (serch != null) {
            searchText = "&search=" + serch;
        } else {
            searchText = "";
        }

        refreshLayout.setRefreshing(true);
        HttpUtil.sendGetRequestWithHttp(USER_INFO_URL + token + searchText + "&page=" + page + Util.TYPE, new Callback() {
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
                final UserInfo userInfo = Util.handleUserInfo(response.body().string());
                if (userInfo != null && userInfo.error == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addUserInfo(userInfo);
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
                            Intent intent = new Intent(UserInfoActivity.this, LoginActivity.class);
                            intent.putExtra("token_timeout", "登录超时");
                            preferences.edit().putString("token", null).commit();
                            startActivity(intent);
                            ActivityCollector.finishAllActivity();
                        }
                    });
                }
            }
        });


    }

    /**
     * 添加数据
     *
     * @param userInfo
     */
    private void addUserInfo(UserInfo userInfo) {
        page = userInfo.current_page + 1;
        if (userInfo.userData.size() == 0 || userInfo.userData.size() < count) {
            footAdapter.setHasMore(false);
        }
        if (userInfo.userData != null) {
            for (UserData userData : userInfo.userData) {
                MyUserInfo myUserInfo = new MyUserInfo();
                String imgUrl = null;
                if (userData.pic != null) {
                    imgUrl = userData.pic.replace('\\', ' ');
                    myUserInfo.setUserImg(IMG_URL + imgUrl);
                } else {
                    myUserInfo.setUserImg(null);
                }
                if (userData.id != null) {
                    myUserInfo.setUserId(userData.id);
                }
                if (userData.level != null) {
                    if (userData.level.equals("1")) {
                        myUserInfo.setUserQx("管理员");
                    } else {
                        myUserInfo.setUserQx("一般用户");
                    }
                }
                if (userData.name != null) {
                    myUserInfo.setUserName(userData.name);
                }
                if (userData.user != null) {
                    myUserInfo.setUser(userData.user);
                }
                myUserInfoList.add(myUserInfo);
            }
        }
        footAdapter.notifyDataSetChanged();

    }


    /**
     * 发送网络请求
     */
    private void sendRequest() {
        String searchText;
        if (serch != null) {
            searchText = "&search=" + serch;
        } else {
            searchText = "";
        }

        refreshLayout.setRefreshing(true);
        HttpUtil.sendGetRequestWithHttp(USER_INFO_URL + token + searchText + "&page=1" + Util.TYPE, new Callback() {
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
                String content = response.body().string();
                if (Util.isGoodJson(content)) {
                    final UserInfo userInfo = Util.handleUserInfo(content);
                    if (userInfo != null && userInfo.error == null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initUserInfo(userInfo);
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
                                Intent intent = new Intent(UserInfoActivity.this, LoginActivity.class);
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
     * 解析用户数据
     *
     * @param userInfo
     */
    private void initUserInfo(UserInfo userInfo) {
        page = userInfo.current_page + 1;
        count = userInfo.per_page;
        total = userInfo.last_page;
        if (userInfo.userData.size() == 0 || userInfo.userData.size() < count) {
            footAdapter.setHasMore(false);
        }
        myUserInfoList.clear();
        if (userInfo.userData != null) {
            for (UserData userData : userInfo.userData) {
                MyUserInfo myUserInfo = new MyUserInfo();
                String imgUrl = null;
                if (userData.pic != null) {
                    imgUrl = userData.pic.replace('\\', ' ');
                    myUserInfo.setUserImg(IMG_URL + imgUrl);
                } else {
                    myUserInfo.setUserImg(null);
                }
                if (userData.id != null) {
                    myUserInfo.setUserId(userData.id);
                }
                if (userData.level != null) {
                    if (userData.level.equals("1")) {
                        myUserInfo.setUserQx("管理员");
                    } else {
                        myUserInfo.setUserQx("一般用户");
                    }
                }
                if (userData.name != null) {
                    myUserInfo.setUserName(userData.name);
                }
                if (userData.user != null) {
                    myUserInfo.setUser(userData.user);
                }
                myUserInfoList.add(myUserInfo);
            }
        }
        footAdapter.notifyDataSetChanged();
    }

    /**
     * 初始化View
     */
    private void initView() {
        preferences = PreferenceManager.getDefaultSharedPreferences(UserInfoActivity.this);
        token = preferences.getString("token", null);

        titleBar = findViewById(R.id.user_info_title_bar);
        titleBar.setTextViewText("用户管理");
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);

        allCheckedImage = findViewById(R.id.user_info_all_checked_image);
        allCheckedText = findViewById(R.id.user_info_all_checked_text);
        deleteButton = findViewById(R.id.user_info_delete_button);
        top_layout = findViewById(R.id.user_info_top_layout);
        bottom_layout = findViewById(R.id.user_info_bottom_layout);

        manager = new LinearLayoutManager(this);
        adapter = new UserInfoAdapter(myUserInfoList);
        footAdapter = new FootAdapter(this, adapter);

        refreshLayout = findViewById(R.id.user_info_swiper);
        loodingErrorLayout = findViewById(R.id.user_info_loading_error_layout);
        loodingLayout = findViewById(R.id.user_info_loading_layout);

        searchText = findViewById(R.id.user_info_serch_edit);
        cancelButton = findViewById(R.id.user_info_cancel_button);
        ensureButton = findViewById(R.id.user_info_ensure_button);

        recyclerView = findViewById(R.id.user_info_recycler);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(footAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

    }

    /**
     * 点击事件监听
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_info_cancel_button:
                if (searchText != null) {
                    searchText.setText("");
                }
                break;
            case R.id.user_info_ensure_button:
                if (searchText != null) {
                    serch = searchText.getText().toString().trim();
                    sendRequest();
                }
                break;
            case R.id.user_info_all_checked_image:
            case R.id.user_info_all_checked_text:
                allCheckedClick();
                break;
            case R.id.user_info_delete_button:
                checkIsChecked();
                if (idList.size() == 0) {
                    Toast.makeText(UserInfoActivity.this, "还没有选择用户", Toast.LENGTH_SHORT).show();
                } else {
                    sendDeleteRequest();
                }
                break;
            default:
                break;

        }
    }

    /**
     * 发送删除请求
     */
    private void sendDeleteRequest() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", Util.listToString(idList));
            refreshLayout.setRefreshing(true);
            HttpUtil.sendDeleteWithHttp(USER_INFO_URL + token, jsonObject.toString(), new Callback() {
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
                    final String content = response.body().string();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            parseResponse(content);
                        }
                    });
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 解析删除请求返回信息
     *
     * @param content
     */
    private void parseResponse(String content) {
        if (content != null && content.equals("true")) {
            sendRequest();
            Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
        } else if (content != null && content.equals("false")) {
            refreshLayout.setRefreshing(false);
            Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show();
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(UserInfoActivity.this, LoginActivity.class);
                    intent.putExtra("token_timeout", "登录超时");
                    preferences.edit().putString("token", null).commit();
                    startActivity(intent);
                    ActivityCollector.finishAllActivity();
                }
            });
        }
    }

    /**
     * 检查选中的用户
     */
    private void checkIsChecked() {
        idList.clear();
        for (MyUserInfo myUserInfo : myUserInfoList) {
            if (myUserInfo.isImgIsChecked()) {
                idList.add(myUserInfo.getUserId());
            }
        }
    }

    @Override
    public void onBackPressed() {
        //判断递送箱列表是否是多选状态
        if (isLongClick) {
            cancleLongClick();

        } else {
            finish();

        }
    }

    /**
     * 取消多选状态
     */
    public void cancleLongClick() {
        isLongClick = false;
        adapter.setCheckedLayout(false);
        footAdapter.notifyDataSetChanged();
        top_layout.setVisibility(View.VISIBLE);
        bottom_layout.setVisibility(View.GONE);
    }

    /**
     * 处理全选按钮的点击事件
     */
    private void allCheckedClick() {
        //判断当前全选是否是选中状态
        if (isAllChecked) {
            isAllChecked = false;
            allCheckedImage.setImageResource(R.mipmap.unchecked);
            List<MyUserInfo> myUserInfos = adapter.getMyUserInfoList();
            for (MyUserInfo myUserInfo : myUserInfos) {
                myUserInfo.setImgIsChecked(false);
            }
            footAdapter.notifyDataSetChanged();


        } else {
            isAllChecked = true;
            allCheckedImage.setImageResource(R.mipmap.checked);
            List<MyUserInfo> myUserInfos = adapter.getMyUserInfoList();
            for (MyUserInfo myUserInfo : myUserInfos) {
                myUserInfo.setImgIsChecked(true);
            }
            footAdapter.notifyDataSetChanged();

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_USERINFO:
                if (resultCode == RESULT_OK) {
                    String name = data.getStringExtra("name");
                    String user = data.getStringExtra("user");
                    String level = data.getStringExtra("level");
                    String url = IMG_URL + data.getStringExtra("url");
                    adapter.removeData(lastPosition);
                    lastUser.setUser(user);
                    lastUser.setUserName(name);
                    lastUser.setUserQx(level);
                    lastUser.setUserImg(url);
                    adapter.addData(lastPosition, lastUser);
                    footAdapter.notifyDataSetChanged();
                }
                break;
            case REQUEST:
                if (resultCode == RESULT_OK) {
                    sendRequest();
                }
                break;
            default:
                break;
        }
    }

}
