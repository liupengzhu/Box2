package cn.com.larunda.safebox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.widget.NestedScrollView;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.larunda.safebox.R;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.com.larunda.safebox.adapter.BoxAddUserAdapter;
import cn.com.larunda.safebox.gson.BoxAddUserData;
import cn.com.larunda.safebox.gson.BoxAddUserInfo;
import cn.com.larunda.safebox.recycler.BoxAddUser;
import cn.com.larunda.safebox.util.ActivityCollector;
import cn.com.larunda.safebox.util.BaseActivity;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class BoxAddUserActivity extends BaseActivity implements View.OnClickListener {

    private TitleBar titleBar;
    private Button addButton;

    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private BoxAddUserAdapter adapter;
    private List<BoxAddUser> boxAddUserList = new ArrayList<>();
    private String id;
    private String BIND_USER_URL = Util.URL + "box/bind_user_lists" + Util.TOKEN;
    private String CANCEL_URL = Util.URL + "box/cancel_bind_user" + Util.TOKEN;

    private String IMG_URL = "http://safebox.dsmcase.com:90";
    private SharedPreferences preferences;
    private String token;

    private SwipeRefreshLayout refreshLayout;

    private NestedScrollView layout;
    private RelativeLayout loodingErrorLayout;
    private ImageView loodingLayout;

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
    private LinearLayout bottom_layout;

    private Button deleteButton;
    private ArrayList<String> idList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box_add_user);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        id = getIntent().getStringExtra("id");
        initView();
        initEvent();
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sendRequest();

            }
        });

        //每次创建时还没有网络数据 设置载入背景为可见
        loodingLayout.setVisibility(View.VISIBLE);
        loodingErrorLayout.setVisibility(View.GONE);
        layout.setVisibility(View.GONE);

        sendRequest();
    }

    /**
     * 发送网络请求
     */
    private void sendRequest() {
        refreshLayout.setRefreshing(true);
        HttpUtil.sendGetRequestWithHttp(BIND_USER_URL + token + "&id=" + id, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                        loodingErrorLayout.setVisibility(View.VISIBLE);
                        loodingLayout.setVisibility(View.GONE);
                        layout.setVisibility(View.GONE);

                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String content = response.body().string();
                if (Util.isGoodJson(content)) {
                    final BoxAddUserInfo boxAddUserInfo = Util.handleBoxAddUserInfo(content);
                    if (boxAddUserInfo != null && boxAddUserInfo.error == null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initData(boxAddUserInfo);
                                refreshLayout.setRefreshing(false);
                                loodingErrorLayout.setVisibility(View.GONE);
                                loodingLayout.setVisibility(View.GONE);
                                layout.setVisibility(View.VISIBLE);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(BoxAddUserActivity.this, LoginActivity.class);
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

    @Override
    protected void onRestart() {
        super.onRestart();
        sendRequest();
    }

    /**
     * 解析数据
     *
     * @param boxAddUserInfo
     */
    private void initData(BoxAddUserInfo boxAddUserInfo) {
        boxAddUserList.clear();
        if (boxAddUserInfo.dataList != null) {
            for (BoxAddUserData boxAddUserData : boxAddUserInfo.dataList) {
                BoxAddUser boxAddUser = new BoxAddUser();
                String imgUrl = null;
                if (boxAddUserData.f_pic != null) {
                    imgUrl = boxAddUserData.f_pic.replace('\\', ' ');
                    boxAddUser.setPic(IMG_URL + imgUrl);
                } else {
                    boxAddUser.setPic(null);
                }

                if (boxAddUserData.company != null) {
                    boxAddUser.setCompany(boxAddUserData.company);
                } else {
                    boxAddUser.setCompany("");
                }
                if (boxAddUserData.department != null) {
                    boxAddUser.setDepartment(boxAddUserData.department);
                } else {
                    boxAddUser.setDepartment("");
                }
                if (boxAddUserData.f_name != null) {
                    boxAddUser.setName(boxAddUserData.f_name);
                } else {
                    boxAddUser.setName("");
                }
                if (boxAddUserData.f_tel != null) {
                    boxAddUser.setPhone(boxAddUserData.f_tel);
                } else {
                    boxAddUser.setPhone("");
                }
                if (boxAddUserData.user_id != null) {
                    boxAddUser.setId(boxAddUserData.user_id);
                } else {
                    boxAddUser.setId("");
                }
                boxAddUserList.add(boxAddUser);
            }
        }
        adapter.notifyDataSetChanged();
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
                Intent intent = new Intent();
                intent.putExtra("count", adapter.getItemCount());
                setResult(RESULT_OK,intent);
                finish();
            }

            @Override
            public void onRightButtonClickListener(View v) {

            }
        });
        addButton.setOnClickListener(this);

        adapter.setBoxAddUserOnLongClickListener(new BoxAddUserAdapter.BoxAddUserOnLongClickListener() {
            @Override
            public void onClick(View v) {
                isLongClick = true;
                adapter.setCheckedLayout(true);
                adapter.notifyDataSetChanged();
                bottom_layout.setVisibility(View.VISIBLE);
            }
        });

        allCheckedImage.setOnClickListener(this);
        allCheckedText.setOnClickListener(this);

        deleteButton.setOnClickListener(this);
    }


    /**
     * 初始化View
     */
    private void initView() {

        allCheckedImage = findViewById(R.id.box_add_user_all_checked_image);
        allCheckedText = findViewById(R.id.box_add_user_all_checked_text);
        bottom_layout = findViewById(R.id.box_add_user_bottom_layout);
        deleteButton = findViewById(R.id.box_add_user_delete_button);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = preferences.getString("token", null);

        refreshLayout = findViewById(R.id.box_add_user_swipe);
        loodingErrorLayout = findViewById(R.id.box_add_user_loading_error_layout);
        loodingLayout = findViewById(R.id.box_add_user_loading_layout);
        layout = findViewById(R.id.box_add_user_layout);

        titleBar = findViewById(R.id.box_add_user_title_bar);
        titleBar.setTextViewText("");
        titleBar.setRightButtonSrc(0);
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);

        addButton = findViewById(R.id.box_add_user_add_button);

        recyclerView = findViewById(R.id.box_add_user_recycler);
        manager = new LinearLayoutManager(this);
        adapter = new BoxAddUserAdapter(this, boxAddUserList);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

    }

    /**
     * 点击事件监听
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.box_add_user_add_button:
                if (id != null) {
                    Intent bindingUserIntent = new Intent(this, BindingUserActivity.class);
                    bindingUserIntent.putExtra("id", id);
                    startActivity(bindingUserIntent);
                }
                break;
            case R.id.box_add_user_all_checked_image:
            case R.id.box_add_user_all_checked_text:
                allCheckedClick();
                break;
            case R.id.box_add_user_delete_button:
                checkIsChecked();
                if (idList.size() == 0) {
                    Toast.makeText(BoxAddUserActivity.this, "还没有选择用户", Toast.LENGTH_SHORT).show();
                } else {
                    sendDeleteRequest();
                }
                break;
            default:
                break;

        }

    }

    private void sendDeleteRequest() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("box_id", id);
            jsonObject.put("user_id", Util.listToString(idList));
            HttpUtil.sendPostRequestWithHttp(CANCEL_URL + token, jsonObject.toString(), new Callback() {
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
     * 解析post请求返回数据
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
                    Intent intent = new Intent(BoxAddUserActivity.this, LoginActivity.class);
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
        for (BoxAddUser boxAddUser : boxAddUserList) {
            if (boxAddUser.isImgIsChecked()) {
                idList.add(boxAddUser.getId());
            }
        }
    }


    @Override
    public void onBackPressed() {
        //判断递送箱列表是否是多选状态
        if (isLongClick) {
            cancleLongClick();

        } else {
            Intent intent = new Intent();
            intent.putExtra("count", adapter.getItemCount());
            setResult(RESULT_OK,intent);
            finish();

        }
    }

    /**
     * 取消多选状态
     */
    public void cancleLongClick() {
        isLongClick = false;
        adapter.setCheckedLayout(false);
        adapter.notifyDataSetChanged();
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
            List<BoxAddUser> boxAddUserList = adapter.getBoxAddUserList();
            for (BoxAddUser boxAddUser : boxAddUserList) {
                boxAddUser.setImgIsChecked(false);
            }
            adapter.notifyDataSetChanged();


        } else {
            isAllChecked = true;
            allCheckedImage.setImageResource(R.mipmap.checked);
            List<BoxAddUser> boxAddUserList = adapter.getBoxAddUserList();
            for (BoxAddUser boxAddUser : boxAddUserList) {
                boxAddUser.setImgIsChecked(true);
            }
            adapter.notifyDataSetChanged();

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
