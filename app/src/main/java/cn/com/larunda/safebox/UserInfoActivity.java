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
import cn.com.larunda.safebox.gson.TaskInfo;
import cn.com.larunda.safebox.gson.UserData;
import cn.com.larunda.safebox.gson.UserInfo;
import cn.com.larunda.safebox.recycler.MyUserInfo;
import cn.com.larunda.safebox.util.ActivityCollector;
import cn.com.larunda.safebox.util.BaseActivity;
import cn.com.larunda.safebox.util.HttpUtil;

import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class UserInfoActivity extends BaseActivity implements View.OnClickListener {

    public static final String USER_INFO_URL = Util.URL + "user" + Util.TOKEN;
    public static final String IMG_URL = Util.PATH;
    public static final int REQUEST = 2;
    private TitleBar titleBar;

    private List<MyUserInfo> myUserInfoList = new ArrayList<>();
    private LinearLayoutManager manager;
    private RelativeLayout errorLayout;
    private SwipeMenuRecyclerView recyclerView;
    private UserInfoAdapter adapter;
    private SwipeRefreshLayout refreshLayout;

    private SharedPreferences preferences;
    private String token;

    private EditText searchText;
    private ImageView cancelButton;
    private TextView ensureButton;


    private ArrayList<String> idList = new ArrayList<>();
    private String search;

    private int page;
    private int maxPage;

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
        adapter.setUserInfoOnClickListener(new UserInfoAdapter.UserInfoOnClickListener() {
            @Override
            public void onClick(View v, int id, int position) {
                Intent intent = new Intent(UserInfoActivity.this, EditUserActivity.class);
                intent.putExtra("id", id);
                lastPosition = position;
                lastUser = adapter.getMyUserInfoList().get(lastPosition);
                startActivityForResult(intent, REQUEST_USERINFO);
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
        preferences = PreferenceManager.getDefaultSharedPreferences(UserInfoActivity.this);
        token = preferences.getString("token", null);

        titleBar = findViewById(R.id.user_info_title_bar);
        titleBar.setTextViewText("用户管理");
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);


        manager = new LinearLayoutManager(this);
        adapter = new UserInfoAdapter(myUserInfoList);

        refreshLayout = findViewById(R.id.user_info_swipe);
        errorLayout = findViewById(R.id.user_info_error_layout);

        searchText = findViewById(R.id.user_info_serch_edit);
        cancelButton = findViewById(R.id.user_info_cancel_button);
        ensureButton = findViewById(R.id.user_info_ensure_button);

        recyclerView = findViewById(R.id.user_info_recycler);
        recyclerView.addItemDecoration(new DefaultItemDecoration(getResources()
                .getColor(R.color.line), MATCH_PARENT, 2));
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
                    search = searchText.getText().toString().trim();
                    sendRequest();
                }
                break;

            default:
                break;

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
                    adapter.notifyDataSetChanged();
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
        HttpUtil.sendGetRequestWithHttp(USER_INFO_URL + token + searchText + "&page=1" + Util.TYPE, new Callback() {
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
                    final UserInfo info = Util.handleUserInfo(content);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initUserInfo(info);
                            refreshLayout.setRefreshing(false);
                            errorLayout.setVisibility(View.GONE);
                        }
                    });

                } else if (code == 401 || code == 412) {
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
                } else if (code == 422) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject js = new JSONObject(content);
                                Toast.makeText(UserInfoActivity.this, js.get("message") + "", Toast.LENGTH_SHORT).show();
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
     * 解析用户数据
     *
     * @param userInfo
     */
    private void initUserInfo(UserInfo userInfo) {
        page = userInfo.getCurrent_page() + 1;
        maxPage = userInfo.getLast_page();
        myUserInfoList.clear();
        if (userInfo.getData() != null) {
            for (UserInfo.DataBean userData : userInfo.getData()) {
                MyUserInfo myUserInfo = new MyUserInfo();
                String imgUrl = null;

                imgUrl = userData.getF_pic();
                myUserInfo.setUserImg(IMG_URL + imgUrl);

                myUserInfo.setUserId(userData.getId());

                if (userData.getF_level() != null) {
                    if (userData.getF_level().equals("7")) {
                        myUserInfo.setUserQx("管理员");
                    } else {
                        myUserInfo.setUserQx("一般用户");
                    }
                }
                if (userData.getF_name() != null) {
                    myUserInfo.setUserName(userData.getF_name());
                }
                if (userData.getF_user() != null) {
                    myUserInfo.setUser(userData.getF_user());
                }
                myUserInfoList.add(myUserInfo);
            }
        }
        recyclerView.loadMoreFinish(userInfo.getData().size() == 0, maxPage >= page);
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

        HttpUtil.sendGetRequestWithHttp(USER_INFO_URL + token + searchText + "&page=" + page + Util.TYPE, new Callback() {
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
                    final UserInfo info = Util.handleUserInfo(content);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initLoadUserInfo(info);
                        }
                    });

                } else if (code == 401 || code == 412) {
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
                } else if (code == 422) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject js = new JSONObject(content);
                                Toast.makeText(UserInfoActivity.this, js.get("message") + "", Toast.LENGTH_SHORT).show();
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
     * 解析用户数据
     *
     * @param userInfo
     */
    private void initLoadUserInfo(UserInfo userInfo) {
        page = userInfo.getCurrent_page() + 1;
        maxPage = userInfo.getLast_page();
        if (userInfo.getData() != null) {
            for (UserInfo.DataBean userData : userInfo.getData()) {
                MyUserInfo myUserInfo = new MyUserInfo();
                String imgUrl = null;

                imgUrl = userData.getF_pic();
                myUserInfo.setUserImg(IMG_URL + imgUrl);

                myUserInfo.setUserId(userData.getId());

                if (userData.getF_level() != null) {
                    if (userData.getF_level().equals("7")) {
                        myUserInfo.setUserQx("管理员");
                    } else {
                        myUserInfo.setUserQx("一般用户");
                    }
                }
                if (userData.getF_name() != null) {
                    myUserInfo.setUserName(userData.getF_name());
                }
                if (userData.getF_user() != null) {
                    myUserInfo.setUser(userData.getF_user());
                }
                myUserInfoList.add(myUserInfo);
            }
        }
        recyclerView.loadMoreFinish(userInfo.getData().size() == 0, maxPage >= page);
        adapter.notifyDataSetChanged();
    }
}
