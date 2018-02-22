package cn.com.larunda.safebox;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.larunda.safebox.R;
import com.larunda.selfdialog.ChooseDialog;
import com.larunda.selfdialog.PhotoDialog;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.com.larunda.safebox.gson.Company;
import cn.com.larunda.safebox.gson.Department;
import cn.com.larunda.safebox.gson.DepartmentInfo;
import cn.com.larunda.safebox.gson.EditUserInfo;
import cn.com.larunda.safebox.gson.PhotoUrl;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class EditUserActivity extends AppCompatActivity implements View.OnClickListener {

    private TitleBar titleBar;

    private RelativeLayout settingPhoto;

    private RelativeLayout levelButton;
    private TextView levelText;
    private ChooseDialog levelDialog;
    private List<String> levelData = new ArrayList<>();

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

    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_ALBUM = 0;
    private PhotoDialog photoDialog;
    private Uri imageUri;
    CircleImageView photo;

    EditText userText;
    EditText nameText;
    EditText passwordText;
    EditText repasswordText;
    EditText telText;
    EditText emailText;

    public SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout loodingErrorLayout;
    private ImageView loodingLayout;
    private LinearLayout layout;
    private Button putButton;

    private SharedPreferences preferences;
    private String token;

    public static final String EDIT_USER_URL = Util.URL + "user/";
    public static final String COMPANY_URL = Util.URL + "company/";
    public static final String DEPARTMENT_LIST_URL = Util.URL + "app/user_info/department_lists" + Util.TOKEN;
    public static final String DEPARTMENT_URL = Util.URL + "department/";
    public static final String IMG_URL = "http://safebox.dsmcase.com:90";

    public static final String UPLOAD = Util.URL + "upload/file" + Util.TOKEN;
    private String userId = "";
    private String path;
    private String url = null;
    private int id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        userId = getIntent().getStringExtra("id");
        initData();
        initView();
        //每次fragment创建时还没有网络数据 设置载入背景为可见
        loodingLayout.setVisibility(View.VISIBLE);
        loodingErrorLayout.setVisibility(View.GONE);
        layout.setVisibility(View.GONE);
        if (userId != null) {
            sendRequest();
        }
        initEvent();

    }

    /**
     * 初始化数据
     */
    private void initData() {
        levelData.add("一般用户");
        levelData.add("管理员");
    }

    /**
     * 发送网络请求
     */
    private void sendRequest() {
        swipeRefreshLayout.setRefreshing(true);
        HttpUtil.sendGetRequestWithHttp(EDIT_USER_URL + userId + Util.TOKEN + token, new Callback() {
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
                if (Util.isGoodJson(content)) {
                    final EditUserInfo userInfo = Util.handleEditUserInfo(content);
                    if (userInfo != null && userInfo.error == null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initUserInfo(userInfo);
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
                                Intent intent = new Intent(EditUserActivity.this, LoginActivity.class);
                                intent.putExtra("token_timeout", "登录超时");
                                preferences.edit().putString("token", null).commit();
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * 解析用户信息
     *
     * @param userInfo
     */
    private void initUserInfo(EditUserInfo userInfo) {

        companyData.clear();
        companyId.clear();
        if (userInfo.companyList != null) {
            for (EditUserInfo.Company company : userInfo.companyList) {
                if (company.id != null && company.name != null) {
                    companyId.add(Integer.parseInt(company.id));
                    companyData.add(company.name.trim());
                }
            }
        }
        String imgUrl = null;
        if (userInfo.pic != null) {
            imgUrl = userInfo.pic.replace('\\', ' ');
            url = imgUrl;
            Glide.with(this).load(IMG_URL + imgUrl).error(R.mipmap.user_img).into(photo);
        }
        if (userInfo.user != null) {
            userText.setText(userInfo.user);
        } else {
            userText.setText("");
        }
        if (userInfo.name != null) {
            nameText.setText(userInfo.name);
        } else {
            nameText.setText("");
        }
        if (userInfo.tel != null) {
            telText.setText(userInfo.tel);
        } else {
            telText.setText("");
        }
        if (userInfo.email != null) {
            emailText.setText(userInfo.email);
        } else {
            emailText.setText("");
        }
        if (userInfo.level != null) {
            if (userInfo.level.equals("1")) {
                levelText.setText("管理员");
            } else {
                levelText.setText("一般用户");
            }
        } else {
            levelText.setText("一般用户");
        }

        if (userInfo.company_id != "") {
            sendRequestForCompany(userInfo.company_id);
            sendRequestForDepartmentList(userInfo.company_id);
        } else {
            companyText.setText("");
        }
        if (userInfo.department_id != "") {
            id = Integer.parseInt(userInfo.department_id);
            sendRequestForDepartment(userInfo.department_id);
        } else {
            departmentText.setText("");
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
                if (Util.isGoodJson(content)) {
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
                                Intent intent = new Intent(EditUserActivity.this, LoginActivity.class);
                                intent.putExtra("token_timeout", "登录超时");
                                preferences.edit().putString("token", null).commit();
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * 解析部门列表
     *
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
     * 请求部门信息
     *
     * @param department_id
     */
    private void sendRequestForDepartment(String department_id) {
        HttpUtil.sendGetRequestWithHttp(DEPARTMENT_URL + department_id + Util.TOKEN + token, new Callback() {
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
                if (Util.isGoodJson(content)) {
                    final Department department = Util.handleDepartment(content);
                    if (department != null && department.error == null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                departmentText.setText(department.f_name);
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
                                Intent intent = new Intent(EditUserActivity.this, LoginActivity.class);
                                intent.putExtra("token_timeout", "登录超时");
                                preferences.edit().putString("token", null).commit();
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * 请求公司信息
     *
     * @param company_id
     */
    private void sendRequestForCompany(String company_id) {
        HttpUtil.sendGetRequestWithHttp(COMPANY_URL + company_id + Util.TOKEN + token, new Callback() {
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
                if (Util.isGoodJson(content)) {
                    final Company company = Util.handleCompany(content);
                    if (company != null && company.error == null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                companyText.setText(company.f_name);
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
                                Intent intent = new Intent(EditUserActivity.this, LoginActivity.class);
                                intent.putExtra("token_timeout", "登录超时");
                                preferences.edit().putString("token", null).commit();
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                }
            }
        });
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
        levelButton.setOnClickListener(this);
        levelDialog.setOnClickListener(new ChooseDialog.OnClickListener() {
            @Override
            public void OnClick(View v, int positon) {
                levelText.setText(levelData.get(positon));
                levelDialog.cancel();
            }
        });
        companyButton.setOnClickListener(this);
        departmentButton.setOnClickListener(this);
        settingPhoto.setOnClickListener(this);

        photoDialog.setCameraButtonOnClick(new PhotoDialog.CameraOnClickListener() {
            @Override
            public void onClick(View v) {
                cameraPhoto();
            }
        });
        photoDialog.setPhotoButtonOnClick(new PhotoDialog.PhotoOnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFromAlbum();
            }
        });
        loodingErrorLayout.setOnClickListener(this);
        putButton.setOnClickListener(this);
    }

    /**
     * 相册选择照片方法
     */
    private void chooseFromAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_ALBUM);
    }

    /**
     * 拍照方法
     */
    private void cameraPhoto() {
        File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();


        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(this,
                    "cn.com.larunda.cameraalbumtest.fileprovider", outputImage);
        } else {
            imageUri = Uri.fromFile(outputImage);
        }
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }


    /**
     * 初始化View
     */
    private void initView() {
        preferences = PreferenceManager.getDefaultSharedPreferences(EditUserActivity.this);
        token = preferences.getString("token", null);
        settingPhoto = findViewById(R.id.edit_user_setting_photo);
        photo = findViewById(R.id.edit_user_photo);

        putButton = findViewById(R.id.edit_user_button);
        loodingErrorLayout = findViewById(R.id.edit_user_loading_error_layout);
        loodingLayout = findViewById(R.id.edit_user_loading_layout);
        layout = findViewById(R.id.edit_user_layout);
        swipeRefreshLayout = findViewById(R.id.edit_user_swipe);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        swipeRefreshLayout.setEnabled(false);//设置swipe不可用

        levelButton = findViewById(R.id.edit_user_level);
        levelText = findViewById(R.id.edit_user_level_text);
        levelDialog = new ChooseDialog(this, levelData);

        companyButton = findViewById(R.id.edit_user_company);
        companyText = findViewById(R.id.edit_user_company_text);


        departmentButton = findViewById(R.id.edit_user_department);
        departmentText = findViewById(R.id.edit_user_department_text);

        photoDialog = new PhotoDialog(this);

        userText = findViewById(R.id.edit_user_user_text);
        nameText = findViewById(R.id.edit_user_name_text);
        telText = findViewById(R.id.edit_user_tel_text);
        emailText = findViewById(R.id.edit_user_email_text);
        passwordText = findViewById(R.id.edit_user_password_text);
        repasswordText = findViewById(R.id.edit_user_repassword_text);

        titleBar = findViewById(R.id.edit_user_title_bar);
        titleBar.setTextViewText("编辑用户");
        titleBar.setRightButtonSrc(0);
        titleBar.setLeftButtonVisible(View.GONE);
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
            case R.id.edit_user_setting_photo:
                photoDialog.show();
                break;
            case R.id.edit_user_level:
                levelDialog.show();
                break;
            case R.id.edit_user_company:
                companyDialog = new ChooseDialog(this, companyData);
                companyDialog.setOnClickListener(new ChooseDialog.OnClickListener() {
                    @Override
                    public void OnClick(View v, int positon) {
                        String company = companyData.get(positon).trim();
                        if (companyText.getText().toString().trim().equals(company)) {
                            companyDialog.cancel();
                        } else {
                            companyText.setText(company);
                            Integer id = companyId.get(positon);
                            departmentText.setText("请选择部门");
                            sendRequestForDepartmentList(id + "");
                            companyDialog.cancel();
                        }
                    }
                });
                companyDialog.show();
                break;
            case R.id.edit_user_department:
                if (isCheckedCompany()) {
                    if (departmentData.size() == 0) {
                        Toast.makeText(this, "没有更多部门", Toast.LENGTH_SHORT).show();
                    } else {
                        departmentDialog = new ChooseDialog(EditUserActivity.this, departmentData);
                        departmentDialog.setOnClickListener(new ChooseDialog.OnClickListener() {
                            @Override
                            public void OnClick(View v, int positon) {
                                departmentText.setText(departmentData.get(positon));
                                id = departmentId.get(positon);
                                departmentDialog.cancel();
                            }
                        });
                        departmentDialog.show();
                    }
                }

                break;
            case R.id.edit_user_loading_error_layout:
                sendRequest();
                break;
            case R.id.edit_user_button:
                sendPostRequest();
                break;
            default:
                break;
        }

    }

    /**
     * 发送post请求
     */
    private void sendPostRequest() {
        String name = nameText.getText().toString().trim();
        String user = userText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();
        String repassword = repasswordText.getText().toString().trim();
        String tel = telText.getText().toString().trim();
        String email = emailText.getText().toString().trim();
        String level = levelText.getText().toString().trim();
        String company = companyText.getText().toString().trim();
        String department = departmentText.getText().toString().trim();
        if (!isEmpty(name, user, tel, email, level, company, department)) {
            final JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("f_name", name);
                jsonObject.put("f_user", user);
                jsonObject.put("f_tel", tel);
                jsonObject.put("f_email", email);
                if (level.equals("管理员")) {
                    jsonObject.put("f_level", 1);
                } else {
                    jsonObject.put("f_level", 2);
                }

                jsonObject.put("department_id", id);
                jsonObject.put("f_pic", url);
                jsonObject.put("f_fingerencode", null);
                if (password != null && repassword != null) {
                    if (password.equals(repassword)) {
                        jsonObject.put("f_password", password);
                        jsonObject.put("re_password", repassword);
                    } else {
                        Toast.makeText(this, "密码于确认密码不一致", Toast.LENGTH_SHORT).show();
                    }
                }
                swipeRefreshLayout.setRefreshing(true);
                HttpUtil.sendPutRequestWithHttp(EDIT_USER_URL + userId + Util.TOKEN + token, jsonObject.toString(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                swipeRefreshLayout.setRefreshing(false);
                                layout.setVisibility(View.GONE);
                                loodingErrorLayout.setVisibility(View.VISIBLE);
                                loodingLayout.setVisibility(View.GONE);
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String content = response.body().string();
                        if (Util.isGoodJson(content)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    parseUpdata(content);
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            });

                        }
                    }
                });


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    private void parseUpdata(String content) {
        if (content != null && content.equals("true")) {
            finish();
        } else if (content != null && content.equals("false")) {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(this, "更新失败", Toast.LENGTH_SHORT).show();
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(EditUserActivity.this, LoginActivity.class);
                    intent.putExtra("token_timeout", "登录超时");
                    preferences.edit().putString("token", null).commit();
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    /**
     * 判断是否为空
     *
     * @param name
     * @param user
     * @param tel
     * @param email
     * @param level
     * @param company
     * @param department @return
     */
    private boolean isEmpty(String name, String user, String tel, String email, String level, String company, String department) {
        if (url == null) {
            Toast.makeText(this, "头像不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(user)) {
            Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "姓名不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(tel)) {
            Toast.makeText(this, "电话不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "邮箱不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(level) || level.equals("请选择权限等级")) {
            Toast.makeText(this, "权限等级不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(company) || company.equals("请选择单位")) {
            Toast.makeText(this, "单位不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(department) || department.equals("请选择部门")) {
            Toast.makeText(this, "部门不能为空", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    /**
     * 判断选择企业的方法
     *
     * @return
     */
    private boolean isCheckedCompany() {
        if (companyText.getText().toString().trim() == null) {
            Toast.makeText(this, "请先选择单位", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    /**
     * activity回调方法
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        path = "/sdcard/Android/data/com.example.box//cache/output_image.jpg";
                        swipeRefreshLayout.setRefreshing(true);
                        HttpUtil.sendPostImageWithHttp(UPLOAD + token + "&folder_type=" + "user", path, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(false);
                                        Toast.makeText(EditUserActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                final String content = response.body().string();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        parseContent(content);
                                        swipeRefreshLayout.setRefreshing(false);
                                    }
                                });
                            }
                        });
                        photoDialog.cancel();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }


                }
                break;
            case CHOOSE_ALBUM:
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        handleImageOnKitKat(data);
                    } else {
                        handleImageBeforeKitKat(data);
                    }
                }
                break;

        }
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);

    }


    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            //如果是document类型的uri，则通过documentId处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);

            }

        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是content了类型的Uri，则使用普通方法处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是file 类型的uri 直接获取图片路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath);


    }


    private void displayImage(String imagePath) {
        if (imagePath != null) {
            path = imagePath;
            swipeRefreshLayout.setRefreshing(true);
            HttpUtil.sendPostImageWithHttp(UPLOAD + token + "&folder_type=" + "user", imagePath, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(EditUserActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String content = response.body().string();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            parseContent(content);
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });


                }
            });
            photoDialog.cancel();
        }

    }

    /**
     * 解析服务器返回数据
     *
     * @param content
     */
    private void parseContent(String content) {
        if (Util.isGoodJson(content)) {
            PhotoUrl photoUrl = Util.handlePhotoUrl(content);
            if (photoUrl != null && photoUrl.getError() == null) {
                if (photoUrl.getMessage() != null) {
                    Toast.makeText(this, photoUrl.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Intent intent = new Intent(EditUserActivity.this, LoginActivity.class);
                intent.putExtra("token_timeout", "登录超时");
                preferences.edit().putString("token", null).commit();
                startActivity(intent);
                finish();
            }


        } else {

            if (content != null) {
                url = content;
                Glide.with(this).load(path)
                        .skipMemoryCache(true) // 不使用内存缓存
                        .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁盘缓存
                        .error(R.mipmap.user_img).into(photo);
                Toast.makeText(this, "头像上传成功", Toast.LENGTH_SHORT).show();

            }

        }
    }


    private String getImagePath(Uri externalContentUri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(externalContentUri, null, selection, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;

    }
}
