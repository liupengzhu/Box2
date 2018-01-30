package cn.com.larunda.safebox;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.larunda.safebox.R;
import com.larunda.selfdialog.ChooseDialog;
import com.larunda.selfdialog.PhotoDialog;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.com.larunda.safebox.gson.Company;
import cn.com.larunda.safebox.gson.Department;
import cn.com.larunda.safebox.gson.EditUserInfo;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PersonalInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private TitleBar titleBar;

    RelativeLayout settingButton;
    RelativeLayout photoButton;
    private Uri imageUri;
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_ALBUM = 0;
    CircleImageView photo;
    private PhotoDialog photoDialog;

    private RelativeLayout companyButton;
    private TextView companyText;
    private ChooseDialog companyDialog;
    private List<String> companyData = new ArrayList<>();

    private RelativeLayout departmentButton;
    private TextView departmentText;
    private ChooseDialog departmentDialog;
    private List<String> departmentData = new ArrayList<>();

    EditText userText;
    EditText nameText;
    EditText telText;
    EditText emailText;
    TextView fingerprintText;
    TextView levelText;

    public static final String PERSONSL_INFO_URL = "http://safebox.dsmcase.com:90/api/user/";
    public static final String COMPANY_URL = "http://safebox.dsmcase.com:90/api/company/";
    public static final String DEPARTMENT_URL = "http://safebox.dsmcase.com:90/api/department/";
    public static final String IMG_URL = "http://safebox.dsmcase.com:90";
    private String userId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

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
        initEvent();
        if (userId != null) {
            sendRequest();
        }
    }

    /**
     * 请求网络数据
     */
    private void sendRequest() {
        HttpUtil.sendGetRequestWithHttp(PERSONSL_INFO_URL + userId + "?_token=" + MainActivity.token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final EditUserInfo userInfo = Util.handleEditUserInfo(response.body().string());
                if (userInfo != null && userInfo.error == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initUserInfo(userInfo);
                        }
                    });

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(PersonalInfoActivity.this, LoginActivity.class);
                            intent.putExtra("token_timeout", "登录超时");
                            PreferenceManager.getDefaultSharedPreferences(PersonalInfoActivity.this).edit().putString("token", null).commit();
                            startActivity(intent);
                            finish();
                        }
                    });
                }

            }
        });
    }

    /**
     * 解析网络数据
     *
     * @param userInfo
     */
    private void initUserInfo(EditUserInfo userInfo) {
        String imgUrl = null;
        if (userInfo.pic != null) {
            imgUrl = userInfo.pic.replace('\\', ' ');
            Glide.with(this).load(IMG_URL + imgUrl).into(photo);
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
        if (userInfo.fingerprint != null) {
            if (userInfo.fingerprint.equals("1")) {
                fingerprintText.setText("已录入");
            } else {
                fingerprintText.setText("未录入");
            }
        } else {
            fingerprintText.setText("未录入");
        }
        if (userInfo.company_id != "") {
            sendRequestForCompany(userInfo.company_id);
        } else {
            companyText.setText("");
        }
        if (userInfo.department_id != "") {
            sendRequestForDepartment(userInfo.department_id);
        } else {
            departmentText.setText("");
        }

    }

    /**
     * 请求部门信息
     *
     * @param department_id
     */
    private void sendRequestForDepartment(String department_id) {

        HttpUtil.sendGetRequestWithHttp(DEPARTMENT_URL + department_id + "?_token=" + MainActivity.token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final Department department = Util.handleDepartment(response.body().string());
                if (department != null && department.error == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            departmentText.setText(department.f_name);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(PersonalInfoActivity.this, LoginActivity.class);
                            intent.putExtra("token_timeout", "登录超时");
                            PreferenceManager.getDefaultSharedPreferences(PersonalInfoActivity.this).edit().putString("token", null).commit();
                            startActivity(intent);
                            finish();
                        }
                    });
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
        HttpUtil.sendGetRequestWithHttp(COMPANY_URL + company_id + "?_token=" + MainActivity.token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final Company company = Util.handleCompany(response.body().string());
                if (company != null && company.error == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            companyText.setText(company.f_name);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(PersonalInfoActivity.this, LoginActivity.class);
                            intent.putExtra("token_timeout", "登录超时");
                            PreferenceManager.getDefaultSharedPreferences(PersonalInfoActivity.this).edit().putString("token", null).commit();
                            startActivity(intent);
                            finish();
                        }
                    });
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
        settingButton.setOnClickListener(this);
        photoButton.setOnClickListener(this);

        companyButton.setOnClickListener(this);
        companyDialog.setOnClickListener(new ChooseDialog.OnClickListener() {
            @Override
            public void OnClick(View v, int positon) {
                if (companyText.getText().toString().trim().equals(companyData.get(positon))) {
                    companyDialog.cancel();
                } else {
                    companyText.setText(companyData.get(positon));
                    departmentText.setText("请选择部门");
                    companyDialog.cancel();
                }
            }
        });
        departmentButton.setOnClickListener(this);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        companyData.add("家乐福");
        companyData.add("朗润达");
        companyData.add("沃尔玛");
    }

    /**
     * 初始化view
     */
    private void initView() {

        companyButton = findViewById(R.id.personal_info_company);
        companyText = findViewById(R.id.personal_info_company_text);
        companyDialog = new ChooseDialog(this, companyData);

        departmentButton = findViewById(R.id.personal_info_department);
        departmentText = findViewById(R.id.personal_info_department_text);

        userText = findViewById(R.id.personal_info_user_text);
        nameText = findViewById(R.id.personal_info_name_text);
        telText = findViewById(R.id.personal_info_tel_text);
        emailText = findViewById(R.id.personal_info_email_text);
        fingerprintText = findViewById(R.id.personal_info_fingerprint_text);

        levelText = findViewById(R.id.personal_info_level_text);

        titleBar = findViewById(R.id.personal_info_title_bar);
        settingButton = findViewById(R.id.personal_info_setting);
        photoButton = findViewById(R.id.personal_info_setting_photo);
        photo = findViewById(R.id.personal_info_photo);
        titleBar.setTextViewText("个人信息");
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
            case R.id.personal_info_setting:
                Intent settingIntent = new Intent(this, PersonalSettingActivity.class);
                startActivity(settingIntent);
                break;
            case R.id.personal_info_setting_photo:
                photoDialog = new PhotoDialog(this);
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
                photoDialog.show();
                break;
            case R.id.personal_info_company:
                companyDialog.show();
                break;
            case R.id.personal_info_department:
                if (isCheckedCompany()) {
                    departmentDialog = new ChooseDialog(PersonalInfoActivity.this, departmentData);
                    departmentDialog.setOnClickListener(new ChooseDialog.OnClickListener() {
                        @Override
                        public void OnClick(View v, int positon) {
                            departmentText.setText(departmentData.get(positon));
                            departmentDialog.cancel();
                        }
                    });
                    departmentDialog.show();
                }

                break;
            default:
                break;

        }

    }

    /**
     * 判断选择公司的方法
     *
     * @return
     */
    private boolean isCheckedCompany() {
        if (companyText.getText().toString().trim() == null) {
            Toast.makeText(this, "请先选择单位", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (companyText.getText().toString().trim().equals("家乐福")) {
                departmentData.clear();
                departmentData.add("导购员");
            } else if (companyText.getText().toString().trim().equals("朗润达")) {
                departmentData.clear();
                departmentData.add("软件部");
            } else if (companyText.getText().toString().trim().equals("沃尔玛")) {
                departmentData.clear();
                departmentData.add("采购部");
            }
            return true;
        }
    }

    /**
     * 调用相机拍照
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
            imageUri = FileProvider.getUriForFile(PersonalInfoActivity.this,
                    "cn.com.larunda.cameraalbumtest.fileprovider", outputImage);
        } else {
            imageUri = Uri.fromFile(outputImage);
        }
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    /**
     * 根据返回的请求码设置头像
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
                        photo.setImageBitmap(bitmap);
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

    /**
     * 从相册选取照片
     */
    private void chooseFromAlbum() {

        if (ContextCompat.checkSelfPermission(PersonalInfoActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            openAlbum();
        }
    }

    /**
     * 打开相册
     */
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_ALBUM);

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
            photo.setImageBitmap(BitmapFactory.decodeFile(imagePath));
            photoDialog.cancel();
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
