package cn.com.larunda.safebox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
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
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.tu.loadingdialog.LoadingDailog;
import com.larunda.safebox.R;
import com.larunda.selfdialog.ChooseDialog;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import cn.com.larunda.safebox.adapter.DestinationPersonAdapter;
import cn.com.larunda.safebox.gson.DestinationData;
import cn.com.larunda.safebox.gson.TaskInfo;
import cn.com.larunda.safebox.recycler.Person;
import cn.com.larunda.safebox.util.ActivityCollector;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ManagerActivity extends AppCompatActivity implements View.OnClickListener {

    private final String USER_URL = Util.URL + "user/map" + Util.TOKEN;

    private TitleBar titleBar;
    private SharedPreferences preferences;
    private String token;

    private SwipeRefreshLayout swipe;
    private RecyclerView personGroup;
    private RelativeLayout personAddButton;
    private DestinationPersonAdapter personAdapter;
    private LinearLayoutManager personManager;
    private List<Person> personList = new ArrayList<>();
    private LoadingDailog dialog;
    private ChooseDialog userDialog;
    private JSONObject userObject;
    private int taskId;
    private int processId;

    private List<Integer> deleteList = new ArrayList<>();
    private List<String> userData = new ArrayList<>();
    private List<Integer> userId = new ArrayList<>();
    private String userKey;
    private String userValue;

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        taskId = getIntent().getIntExtra("taskId", 0);
        processId = getIntent().getIntExtra("processId", 0);
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

        titleBar = findViewById(R.id.manager_title_bar);
        titleBar.setTextViewText("收件人管理");
        titleBar.setRightButtonSrc(0);
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);

        personGroup = findViewById(R.id.manager_person_layout);
        personAddButton = findViewById(R.id.manager_person_add);
        personAdapter = new DestinationPersonAdapter(this, personList);
        personManager = new LinearLayoutManager(this);
        personGroup.setAdapter(personAdapter);
        personGroup.setLayoutManager(personManager);

        swipe = findViewById(R.id.manager_swipe);
        swipe.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        swipe.setEnabled(false);//设置swipe不可用

        LoadingDailog.Builder loadBuilder = new LoadingDailog.Builder(this)
                .setMessage("上传中...")
                .setCancelable(false)
                .setCancelOutside(false);
        dialog = loadBuilder.create();

        button = findViewById(R.id.manager_button);
    }

    /**
     * 初始化点击事件
     */
    private void initEvent() {
        button.setOnClickListener(this);
        personAdapter.setItemOnclickListener(new DestinationPersonAdapter.ItemOnclickListener() {
            @Override
            public void nameOnclick(View v, final int position) {
                final Person person = personList.get(position);
                userDialog = new ChooseDialog(ManagerActivity.this, userData);
                userDialog.setOnClickListener(new ChooseDialog.OnClickListener() {
                    @Override
                    public void OnClick(View v, int position1) {
                        person.setName(userData.get(position1));
                        person.setUserId(userId.get(position1));
                        personAdapter.notifyItemChanged(position);
                        userDialog.cancel();
                    }
                });
                userDialog.show();
            }

            @Override
            public void dynamicOnclick(View v, int position) {
                Person person = personList.get(position);
                person.setUseDynamic(!person.isUseDynamic());
                personAdapter.notifyItemChanged(position);
            }

            @Override
            public void fingerprintOnclick(View v, int position) {
                Person person = personList.get(position);
                person.setUseFingerprint(!person.isUseFingerprint());
                personAdapter.notifyItemChanged(position);
            }

            @Override
            public void pwdOnclick(View v, int position) {
                Person person = personList.get(position);
                person.setUsePwd(!person.isUsePwd());
                personAdapter.notifyItemChanged(position);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count, int position) {
                Person person = personList.get(position);
                person.setPwd(s.toString());
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

            }
        });

        personAddButton.setOnClickListener(this);

        //为RecycleView绑定触摸事件
        ItemTouchHelper helper2 = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                //首先回调的方法 返回int表示是否监听该方向
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;//拖拽
                int swipeFlags = personList.size() > 1 ? (ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) : 0;//侧滑删除
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //滑动事件
                Collections.swap(personList, viewHolder.getAdapterPosition(), target.getAdapterPosition());
                personAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                //侧滑事件
                if (personList.get(viewHolder.getAdapterPosition()).getId() > 0) {
                    deleteList.add(personList.get(viewHolder.getAdapterPosition()).getId());
                }
                personList.remove(viewHolder.getAdapterPosition());
                personAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                personAdapter.notifyDataSetChanged();
            }

            @Override
            public boolean isLongPressDragEnabled() {
                //是否可拖拽
                return true;
            }
        });
        helper2.attachToRecyclerView(personGroup);
    }

    /**
     * 点击事件处理
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.manager_person_add:
                addPerson();
                break;
            case R.id.manager_button:
                sendPostRequest();
                break;
            default:
                break;
        }
    }

    private void addPerson() {
        Person person = new Person();
        personList.add(person);
        personAdapter.notifyDataSetChanged();
    }

    private void send() {
        swipe.setRefreshing(true);
        HttpUtil.sendGetRequestWithHttp(USER_URL + token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipe.setRefreshing(false);
                        Toast.makeText(ManagerActivity.this, "网络异常!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String content = response.body().string();
                final int code = response.code();
                if (code == 200) {
                    try {
                        userObject = new JSONObject(content);
                        userData.clear();
                        userId.clear();
                        try {
                            JSONObject jsonObject = new JSONObject(content);
                            Iterator iterator = jsonObject.keys();
                            while (iterator.hasNext()) {
                                userKey = (String) iterator.next();
                                userValue = jsonObject.getString(userKey);
                                userData.add(userValue);
                                userId.add(Integer.valueOf(userKey));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                                Intent intent = new Intent(ManagerActivity.this, LoginActivity.class);
                                intent.putExtra("token_timeout", "登录超时");
                                preferences.edit().putString("token", null).commit();
                                startActivity(intent);
                                ActivityCollector.finishAllActivity();
                            } else if (code == 422) {
                                try {
                                    JSONObject js = new JSONObject(content);
                                    Toast.makeText(ManagerActivity.this, js.get("message") + "", Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                swipe.setRefreshing(false);
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 请求收件人列表
     */
    private void sendRequest() {
        HttpUtil.sendGetRequestWithHttp(Util.URL + "task/" + taskId + "/process/" + processId
                + Util.TOKEN + token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipe.setRefreshing(false);
                        Toast.makeText(ManagerActivity.this, "网络异常!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String content = response.body().string();
                int code = response.code();
                if (code == 200 && Util.isGoodJson(content)) {
                    final DestinationData info = Util.handleDestinationData(content);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            parseInfo(info);
                            swipe.setRefreshing(false);
                        }
                    });
                } else if (code == 401 || code == 412) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(ManagerActivity.this, LoginActivity.class);
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
                                Toast.makeText(ManagerActivity.this, js.get("message") + "", Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            swipe.setRefreshing(false);
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
    private void parseInfo(DestinationData info) {
        personList.clear();
        if (info != null) {
            for (DestinationData.AddresseeBean bean : info.getAddressee()) {
                Person person = new Person();
                person.setUserId(bean.getUser_id() != null ? Integer.parseInt(bean.getUser_id()) : 0);
                if (userObject != null) {
                    try {
                        person.setName(bean.getUser_id() != null ? userObject.getString(bean.getUser_id()) : null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                person.setId(bean.getId());
                person.setUsePwd((bean.getF_is_password() != null && bean.getF_is_password().equals("1")));
                person.setPwd((bean.getF_is_password() != null && bean.getF_is_password().equals("1")) ? "******" : null);
                person.setUseDynamic((bean.getF_is_dynamic() != null && bean.getF_is_dynamic().equals("1")));
                personList.add(person);
            }
        }
        personAdapter.notifyDataSetChanged();
    }

    /**
     * 发送提交请求
     */
    private void sendPostRequest() {
        if (checkList()) {
            if (checkPassword()) {
                JSONObject jsonObject = new JSONObject();
                JSONArray storeJsonArray = new JSONArray();
                JSONArray updateJsonArray = new JSONArray();
                JSONArray destroyJsonArray = new JSONArray();
                try {
                    for (Person person : personList) {
                        if (person.getId() > 0 && person.getUserId() > 0) {
                            //更新数据
                            JSONObject updateJson = new JSONObject();
                            updateJson.put("id", person.getId());
                            updateJson.put("user_id", person.getUserId());
                            updateJson.put("f_is_dynamic", person.isUseDynamic() ? 1 : 0);
                            if (person.isUsePwd() && person.getPwd() != null && !person.getPwd().equals("******")) {
                                updateJson.put("f_password", person.getPwd());
                            }
                            updateJsonArray.put(updateJson);
                        } else if (person.getUserId() > 0) {
                            //新增数据
                            JSONObject storeJson = new JSONObject();
                            storeJson.put("user_id", person.getUserId());
                            storeJson.put("f_is_dynamic", person.isUseDynamic() ? 1 : 0);
                            if (person.isUsePwd()) {
                                storeJson.put("f_password", person.getPwd());
                            }
                            storeJsonArray.put(storeJson);
                        }
                    }
                    for (int id : deleteList) {
                        JSONObject destroyJson = new JSONObject();
                        destroyJson.put("id", id);
                        destroyJsonArray.put(destroyJson);
                    }
                    jsonObject.put("store", storeJsonArray);
                    jsonObject.put("update", updateJsonArray);
                    jsonObject.put("destroy", destroyJsonArray);
                    dialog.show();
                    HttpUtil.sendPostRequestWithHttp(Util.URL + "process/" +
                            processId + "/addressee" + Util.TOKEN + token, jsonObject.toString(), new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            if (dialog != null && dialog.isShowing()) {
                                dialog.cancel();
                            }
                            Toast.makeText(ManagerActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String content = response.body().string();
                            final int code = response.code();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (dialog != null && dialog.isShowing()) {
                                        dialog.cancel();
                                    }
                                    if (code == 200) {
                                        setResult(RESULT_OK);
                                        finish();
                                    } else if (code == 401 || code == 412) {
                                        Intent intent = new Intent(ManagerActivity.this, LoginActivity.class);
                                        intent.putExtra("token_timeout", "登录超时");
                                        preferences.edit().putString("token", null).commit();
                                        startActivity(intent);
                                        ActivityCollector.finishAllActivity();
                                    } else if (code == 422) {
                                        try {
                                            JSONObject js = new JSONObject(content);
                                            Toast.makeText(ManagerActivity.this, js.get("message") + "", Toast.LENGTH_SHORT).show();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        Toast.makeText(ManagerActivity.this, "上传失败！", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        } else {
            Toast.makeText(this, "至少需要一个收件人", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 检查密码
     *
     * @return
     */
    private boolean checkPassword() {
        for (Person person : personList) {
            if (person.isUsePwd() && (person.getPwd() == null || person.getPwd().length() != 6)) {
                Toast.makeText(this, "密码必须为6位", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    /**
     * 检查集合是否有至少一个收件人
     *
     * @return
     */
    private boolean checkList() {
        boolean has = false;
        for (Person person : personList) {
            if (person.getUserId() > 0) {
                has = true;
            }
        }
        return has;
    }
}
