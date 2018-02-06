package cn.com.larunda.safebox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.larunda.selfdialog.ChooseDialog;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.com.larunda.safebox.gson.AddPerson;
import cn.com.larunda.safebox.gson.CompanyList;
import cn.com.larunda.safebox.gson.DepartmentInfo;
import cn.com.larunda.safebox.gson.UserToken;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class BindingUserActivity extends AppCompatActivity implements View.OnClickListener {

    private TitleBar titleBar;

    public static final String BIND_USER_URL = Util.URL + "app/user_info/company_lists" + Util.TOKEN;
    public static final String ADD_USER_URL = Util.URL + "box/add_bind_user" + Util.TOKEN;
    public static final String DEPARTMENT_LIST_URL = Util.URL + "app/user_info/department_lists" + Util.TOKEN;
    public static final String PERSON_LIST_URL = Util.URL + "app/box/user_add_lists" + Util.TOKEN;
    private RelativeLayout companyButton;
    private TextView companyText;
    private ChooseDialog companyDialog;
    private List<String> companyData = new ArrayList<>();
    private List<Integer> companyId = new ArrayList<>();

    private RelativeLayout departmentButton;
    private TextView departmentText;
    private ChooseDialog departmentDialog;
    private List<String> departmentData = new ArrayList<>();
    private List<Integer> departmentId = new ArrayList<>();

    private RelativeLayout personButton;
    private TextView personText;
    private ChooseDialog personDialog;
    private List<String> personData = new ArrayList<>();
    private List<Integer> personId = new ArrayList<>();
    private SharedPreferences preferences;
    private String token;

    private String id;
    private int userId;

    public SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout loodingErrorLayout;
    private ImageView loodingLayout;
    private LinearLayout layout;

    private Button postButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binding_user);
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
        //每次fragment创建时还没有网络数据 设置载入背景为可见
        loodingLayout.setVisibility(View.VISIBLE);
        loodingErrorLayout.setVisibility(View.GONE);
        layout.setVisibility(View.GONE);
        sendRequest();
    }

    /**
     * 发送网络请求
     */
    private void sendRequest() {
        swipeRefreshLayout.setRefreshing(true);
        HttpUtil.sendGetRequestWithHttp(BIND_USER_URL + token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        loodingErrorLayout.setVisibility(View.VISIBLE);
                        loodingLayout.setVisibility(View.GONE);
                        layout.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String content = response.body().string();
                final CompanyList companyList = Util.handleCompanyList(content);
                if (companyList != null && companyList.getError() == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initData(companyList);
                            swipeRefreshLayout.setRefreshing(false);
                            layout.setVisibility(View.VISIBLE);
                            loodingErrorLayout.setVisibility(View.GONE);
                            loodingLayout.setVisibility(View.GONE);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(BindingUserActivity.this, LoginActivity.class);
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
     * 解析公司数据
     *
     * @param companyList
     */
    private void initData(CompanyList companyList) {
        companyData.clear();
        companyId.clear();
        if (companyList.getData() != null) {
            for (CompanyList.DataBean dataBean : companyList.getData()) {
                if (dataBean.getF_name() != null) {
                    companyData.add(dataBean.getF_name());
                    companyId.add(dataBean.getId());
                }
            }

        }
        companyText.setText("请选择单位");
        departmentText.setText("请选择部门");
        personText.setText("请选择姓名");
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

        companyButton.setOnClickListener(this);

        departmentButton.setOnClickListener(this);

        personButton.setOnClickListener(this);

        loodingErrorLayout.setOnClickListener(this);

        postButton.setOnClickListener(this);

    }


    /**
     * 初始化view
     */
    private void initView() {

        loodingErrorLayout = findViewById(R.id.binding_user_loading_error_layout);
        loodingLayout = findViewById(R.id.binding_user_loading_layout);
        layout = findViewById(R.id.binding_user_layout);
        swipeRefreshLayout = findViewById(R.id.binding_user_swipe);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        swipeRefreshLayout.setEnabled(false);//设置swipe不可用

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = preferences.getString("token", null);

        companyButton = findViewById(R.id.binding_user_company);
        companyText = findViewById(R.id.binding_user_company_text);


        departmentButton = findViewById(R.id.binding_user_department);
        departmentText = findViewById(R.id.binding_user_department_text);

        personButton = findViewById(R.id.binding_user_person);
        personText = findViewById(R.id.binding_user_person_text);

        postButton = findViewById(R.id.binding_user_button);

        titleBar = findViewById(R.id.binding_user_title_bar);
        titleBar.setTextViewText("绑定用户");
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setRightButtonSrc(0);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);

    }

    /**
     * 点击事件监听
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.binding_user_company:
                if (companyData.size() == 0) {
                    Toast.makeText(this, "没有更多单位", Toast.LENGTH_SHORT).show();
                }
                companyDialog = new ChooseDialog(this, companyData);
                companyDialog.setOnClickListener(new ChooseDialog.OnClickListener() {
                    @Override
                    public void OnClick(View v, int positon) {
                        if (companyText.getText().toString().trim().equals(companyData.get(positon))) {
                            companyDialog.cancel();
                        } else {
                            companyText.setText(companyData.get(positon));
                            int id = companyId.get(positon);
                            departmentText.setText("请选择部门");
                            personText.setText("请选择姓名");
                            sendRequestForDepartmentList(id + "");
                            companyDialog.cancel();
                        }
                    }
                });
                companyDialog.show();
                break;
            case R.id.binding_user_department:
                if (isCheckedCompany()) {
                    if (departmentData.size() == 0) {
                        Toast.makeText(this, "没有更多部门", Toast.LENGTH_SHORT).show();
                    }
                    departmentDialog = new ChooseDialog(BindingUserActivity.this, departmentData);
                    departmentDialog.setOnClickListener(new ChooseDialog.OnClickListener() {
                        @Override
                        public void OnClick(View v, int positon) {
                            if (departmentText.getText().toString().trim().equals(departmentData.get(positon))) {
                                departmentDialog.cancel();
                            } else {
                                departmentText.setText(departmentData.get(positon));
                                int id = departmentId.get(positon);
                                personText.setText("请选择姓名");
                                sendRequestForPersonList(id + "");
                                departmentDialog.cancel();
                            }
                        }
                    });
                    departmentDialog.show();
                }
                break;

            case R.id.binding_user_person:
                if (isCheckedDepartment()) {
                    if (personData.size() == 0) {
                        Toast.makeText(this, "没有更多人员", Toast.LENGTH_SHORT).show();
                    }
                    personDialog = new ChooseDialog(BindingUserActivity.this, personData);
                    personDialog.setOnClickListener(new ChooseDialog.OnClickListener() {
                        @Override
                        public void OnClick(View v, int positon) {
                            userId = personId.get(positon);
                            personText.setText(personData.get(positon));
                            personDialog.cancel();
                        }
                    });
                    personDialog.show();
                }
                break;
            case R.id.binding_user_loading_error_layout:
                sendRequest();
                break;
            case R.id.binding_user_button:
                if (isCheckedDepartment()) {
                    if (personText.getText().toString().trim().equals("请选择姓名")) {
                        Toast.makeText(this, "姓名不能为空", Toast.LENGTH_SHORT).show();
                    } else {
                        sendPostRequest();
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 提交绑定人员信息
     */
    private void sendPostRequest() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", userId);
            jsonObject.put("box_id", id);
            swipeRefreshLayout.setRefreshing(true);
            HttpUtil.sendPostRequestWithHttp(ADD_USER_URL + token, jsonObject.toString(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(BindingUserActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                            swipeRefreshLayout.setRefreshing(false);
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
     * 解析添加人员返回信息
     *
     * @param content
     */
    private void parseResponse(String content) {
        if (content != null && content.equals("true")) {
            sendRequest();
            Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
        } else if (content != null && content.equals("false")) {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(this, "添加失败", Toast.LENGTH_SHORT).show();
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(BindingUserActivity.this, LoginActivity.class);
                    intent.putExtra("token_timeout", "登录超时");
                    preferences.edit().putString("token", null).commit();
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    /**
     * 请求人员信息
     *
     * @param s
     */
    private void sendRequestForPersonList(String s) {
        swipeRefreshLayout.setRefreshing(true);
        HttpUtil.sendGetRequestWithHttp(PERSON_LIST_URL + token + "&id=" + id + "&department_id=" + s, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        loodingErrorLayout.setVisibility(View.VISIBLE);
                        loodingLayout.setVisibility(View.GONE);
                        layout.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String content = response.body().string();
                final AddPerson addPerson = Util.handleAddPerson(content);
                if (addPerson != null && addPerson.getError() == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initPersonData(addPerson);
                            swipeRefreshLayout.setRefreshing(false);
                            layout.setVisibility(View.VISIBLE);
                            loodingErrorLayout.setVisibility(View.GONE);
                            loodingLayout.setVisibility(View.GONE);
                        }
                    });

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(BindingUserActivity.this, LoginActivity.class);
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
     * 解析人员信息
     *
     * @param addPerson
     */
    private void initPersonData(AddPerson addPerson) {
        personData.clear();
        personId.clear();
        if (addPerson.getData() != null) {
            for (AddPerson.DataBean dataBean : addPerson.getData()) {
                if (dataBean.getF_name() != null) {
                    personData.add(dataBean.getF_name());
                    personId.add(dataBean.getId());
                }
            }

        }
    }

    /**
     * 请求部门列表
     *
     * @param company_id
     */
    private void sendRequestForDepartmentList(String company_id) {
        swipeRefreshLayout.setRefreshing(true);
        HttpUtil.sendGetRequestWithHttp(DEPARTMENT_LIST_URL + token + "&company_id=" + company_id, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        loodingErrorLayout.setVisibility(View.VISIBLE);
                        loodingLayout.setVisibility(View.GONE);
                        layout.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String content = response.body().string();
                final DepartmentInfo departmentInfo = Util.handleDepartmentInfo(content);

                if (departmentInfo != null && departmentInfo.error == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initDepartmentList(departmentInfo);
                            swipeRefreshLayout.setRefreshing(false);
                            layout.setVisibility(View.VISIBLE);
                            loodingErrorLayout.setVisibility(View.GONE);
                            loodingLayout.setVisibility(View.GONE);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(BindingUserActivity.this, LoginActivity.class);
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
     * 解析部门信息
     * @param departmentInfo
     */
    private void initDepartmentList(DepartmentInfo departmentInfo) {
        departmentData.clear();
        departmentId.clear();
        if (departmentInfo.getData() != null) {
            for (DepartmentInfo.DataBean data : departmentInfo.getData()) {
                if (data.getF_name() != null) {
                    departmentData.add(data.getF_name());
                    departmentId.add(data.getId());
                }
            }
        }
    }

    /**
     * 判断是否选择部门方法
     *
     * @return
     */
    private boolean isCheckedDepartment() {
        if (companyText.getText().toString().trim().equals("请选择单位")) {
            Toast.makeText(this, "请先选择单位", Toast.LENGTH_SHORT).show();
            return false;
        } else if (departmentText.getText().toString().trim().equals("请选择部门")) {
            Toast.makeText(this, "请先选择部门", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    /**
     * 判断是否选择公司
     *
     * @return
     */
    private boolean isCheckedCompany() {
        if (companyText.getText().toString().trim().equals("请选择单位")) {
            Toast.makeText(this, "请先选择单位", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
}
