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

import com.android.tu.loadingdialog.LoadingDailog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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

import cn.com.larunda.safebox.gson.CompanyList;
import cn.com.larunda.safebox.gson.DepartmentInfo;
import cn.com.larunda.safebox.gson.PhotoUrl;
import cn.com.larunda.safebox.gson.Result;
import cn.com.larunda.safebox.util.ActivityCollector;
import cn.com.larunda.safebox.util.BaseActivity;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AddUserActivity extends BaseActivity implements View.OnClickListener {

    private TitleBar titleBar;

    private RelativeLayout settingPhoto;

    private RelativeLayout levelButton;
    private TextView levelText;
    private ChooseDialog levelDialog;
    private List<String> levelData = new ArrayList<>();


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

    private Button putButton;

    private SharedPreferences preferences;
    private String token;

    public static final String UPLOAD = Util.URL + "upload/avatar" + Util.TOKEN;
    public static final String POST_URL = Util.URL + "user" + Util.TOKEN;


    private String path;
    private String url = null;
    private LoadingDailog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        initData();
        initView();
        initEvent();
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
            public void OnClick(View v, int position) {
                levelText.setText(levelData.get(position));
                levelDialog.cancel();
            }
        });

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

        putButton.setOnClickListener(this);

    }

    /**
     * 初始化数据
     */
    private void initData() {
        levelData.add("一般用户");
        levelData.add("管理员");
    }

    /**
     * 初始化View
     */
    private void initView() {
        putButton = findViewById(R.id.add_user_button);


        userText = findViewById(R.id.add_user_user);
        nameText = findViewById(R.id.add_user_name);
        telText = findViewById(R.id.add_user_tel);
        emailText = findViewById(R.id.add_user_email);
        passwordText = findViewById(R.id.add_user_password);
        repasswordText = findViewById(R.id.add_user_repassword);

        preferences = PreferenceManager.getDefaultSharedPreferences(AddUserActivity.this);
        token = preferences.getString("token", null);

        settingPhoto = findViewById(R.id.add_user_setting_photo);
        photo = findViewById(R.id.add_user_photo);

        levelButton = findViewById(R.id.add_user_level);
        levelText = findViewById(R.id.add_user_level_text);
        levelDialog = new ChooseDialog(this, levelData);

        photoDialog = new PhotoDialog(this);

        titleBar = findViewById(R.id.add_user_title_bar);
        titleBar.setTextViewText("添加用户");
        titleBar.setRightButtonSrc(0);
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);

        LoadingDailog.Builder loadBuilder = new LoadingDailog.Builder(this)
                .setMessage("上传中...")
                .setCancelable(false)
                .setCancelOutside(false);
        dialog = loadBuilder.create();

    }

    /**
     * 点击事件监听
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_user_setting_photo:
                photoDialog.show();
                break;
            case R.id.add_user_level:
                levelDialog.show();
                break;
            case R.id.add_user_button:
                sendPostRequest();
                break;
            default:
                break;
        }

    }


    /**
     * 从相册选取照片的方法
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
                        dialog.show();
                        HttpUtil.sendPostImageWithHttp(UPLOAD + token + "&folder_type=" + "user", path, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (dialog != null && dialog.isShowing()) {
                                            dialog.cancel();
                                        }
                                        Toast.makeText(AddUserActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
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
                                        //parseContent(content);
                                        if (dialog != null && dialog.isShowing()) {
                                            dialog.cancel();
                                        }
                                        if (code == 200) {
                                            parseContent(content);
                                        } else if (code == 401 || code == 412) {
                                            Intent intent = new Intent(AddUserActivity.this, LoginActivity.class);
                                            intent.putExtra("token_timeout", "登录超时");
                                            preferences.edit().putString("token", null).commit();
                                            startActivity(intent);
                                            ActivityCollector.finishAllActivity();
                                        } else {
                                            Toast.makeText(AddUserActivity.this, "头像上传失败", Toast.LENGTH_SHORT).show();
                                        }
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
            dialog.show();
            HttpUtil.sendPostImageWithHttp(UPLOAD + token + "&folder_type=" + "user", imagePath, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (dialog != null && dialog.isShowing()) {
                                dialog.cancel();
                            }
                            Toast.makeText(AddUserActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
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
                            //parseContent(content);
                            if (dialog != null && dialog.isShowing()) {
                                dialog.cancel();
                            }
                            if (code == 200) {
                                parseContent(content);
                            } else if (code == 401 || code == 412) {
                                Intent intent = new Intent(AddUserActivity.this, LoginActivity.class);
                                intent.putExtra("token_timeout", "登录超时");
                                preferences.edit().putString("token", null).commit();
                                startActivity(intent);
                                ActivityCollector.finishAllActivity();
                            } else {
                                Toast.makeText(AddUserActivity.this, "头像上传失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }
            });
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


    private void parseContent(String content) {
        url = content;
        Glide.with(this).load(Util.PATH + url)
                .skipMemoryCache(true) // 不使用内存缓存
                .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁盘缓存
                .error(R.mipmap.user_img).into(photo);
        Toast.makeText(this, "图片上传成功", Toast.LENGTH_SHORT).show();
    }

    /**
     * 发送提交请求
     */
    private void sendPostRequest() {
        String name = nameText.getText().toString().trim();
        String user = userText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();
        String repassword = repasswordText.getText().toString().trim();
        String tel = telText.getText().toString().trim();
        String email = emailText.getText().toString().trim();
        String level = levelText.getText().toString().trim();
        if (!isEmpty(name, user, tel, email, level, password, repassword)) {
            final JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("f_name", name);
                jsonObject.put("f_user", user);
                jsonObject.put("f_tel", tel);
                jsonObject.put("f_email", email);
                if (level.equals("管理员")) {
                    jsonObject.put("f_level", 7);
                } else {
                    jsonObject.put("f_level", 5);
                }

                jsonObject.put("f_pic", url);

                jsonObject.put("f_password", password);
                jsonObject.put("f_password_confirmation", repassword);

                dialog.show();
                HttpUtil.sendPostRequestWithHttp(POST_URL + token, jsonObject.toString(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (dialog != null && dialog.isShowing()) {
                                    dialog.cancel();
                                }
                                Toast.makeText(AddUserActivity.this, "网络异常!", Toast.LENGTH_SHORT).show();
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
                                if (dialog != null && dialog.isShowing()) {
                                    dialog.cancel();
                                }
                                if (code == 200) {
                                    setResult(RESULT_OK);
                                    finish();
                                } else if (code == 401 || code == 412) {
                                    Intent intent = new Intent(AddUserActivity.this, LoginActivity.class);
                                    intent.putExtra("token_timeout", "登录超时");
                                    preferences.edit().putString("token", null).commit();
                                    startActivity(intent);
                                    ActivityCollector.finishAllActivity();
                                } else if (code == 422) {
                                    try {
                                        JSONObject js = new JSONObject(content);
                                        Toast.makeText(AddUserActivity.this, js.get("message") + "", Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Toast.makeText(AddUserActivity.this, "添加失败！", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });


            } catch (JSONException e) {
                e.printStackTrace();
            }
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
     * @param password
     * @param repassword
     * @return
     */
    private boolean isEmpty(String name, String user, String tel, String email, String level, String password, String repassword) {
        if (url == null) {
            Toast.makeText(this, "头像不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(user)) {
            Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "姓名不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (password.length() < 6) {
            Toast.makeText(this, "密码不能低于6位", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(repassword)) {
            Toast.makeText(this, "确认密码不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (!password.equals(repassword)) {
            Toast.makeText(this, "密码于确认密码不一致", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(tel)) {
            Toast.makeText(this, "电话不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "邮箱不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (!Util.isValidEmail(email)) {
            Toast.makeText(this, "邮箱格式不正确", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(level) || level.equals("请选择权限等级")) {
            Toast.makeText(this, "权限等级不能为空", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;


    }

}
