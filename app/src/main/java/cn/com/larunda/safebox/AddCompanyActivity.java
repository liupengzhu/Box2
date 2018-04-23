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
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.tu.loadingdialog.LoadingDailog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.larunda.safebox.R;
import com.larunda.selfdialog.PhotoDialog;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import cn.com.larunda.safebox.util.ActivityCollector;
import cn.com.larunda.safebox.util.BaseActivity;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AddCompanyActivity extends BaseActivity implements View.OnClickListener {

    private static final int TAKE_PHOTO = 2;
    private static final int CHOOSE_ALBUM = 3;
    private final String URL = Util.URL + "company" + Util.TOKEN;
    private final String UPLOAD = Util.URL + "upload/logo" + Util.TOKEN;
    private TitleBar titleBar;
    private ImageView pic;
    private EditText nameText;
    private EditText salesAddressText;
    private EditText addressText;
    private EditText faxText;
    private EditText emailText;
    private EditText contactsText;
    private EditText telText;
    private EditText letterText;
    private Button saveButton;
    private String src;
    private SharedPreferences preferences;
    private String token;
    private LoadingDailog dialog;
    private PhotoDialog photoDialog;
    private Uri imageUri;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_company);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        initView();
        intEvent();
    }

    /**
     * 初始化view
     */
    private void initView() {

        photoDialog = new PhotoDialog(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = preferences.getString("token", null);

        titleBar = findViewById(R.id.add_company_title_bar);
        titleBar.setTextViewText("添加企业");
        titleBar.setRightButtonSrc(0);
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);

        pic = findViewById(R.id.add_company_pic);
        nameText = findViewById(R.id.add_company_name);
        salesAddressText = findViewById(R.id.add_company_sales_address);
        addressText = findViewById(R.id.add_company_address);
        faxText = findViewById(R.id.add_company_fax);
        emailText = findViewById(R.id.add_company_email);
        contactsText = findViewById(R.id.add_company_contacts);
        telText = findViewById(R.id.add_company_tel);
        letterText = findViewById(R.id.add_company_letter);
        saveButton = findViewById(R.id.add_company_button);

        LoadingDailog.Builder loadBuilder = new LoadingDailog.Builder(this)
                .setMessage("上传中...")
                .setCancelable(false)
                .setCancelOutside(false);
        dialog = loadBuilder.create();

    }

    /**
     * 初始化点击事件
     */
    private void intEvent() {

        photoDialog.setPhotoButtonOnClick(new PhotoDialog.PhotoOnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFromAlbum();
            }
        });
        photoDialog.setCameraButtonOnClick(new PhotoDialog.CameraOnClickListener() {
            @Override
            public void onClick(View v) {
                cameraPhoto();
            }
        });

        pic.setOnClickListener(this);
        saveButton.setOnClickListener(this);

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
    }

    /**
     * 点击事件处理
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_company_button:
                sendPostRequest();
                break;
            case R.id.add_company_pic:
                if (photoDialog != null) {
                    photoDialog.show();
                }
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
        String salesAddress = salesAddressText.getText().toString().trim();
        String address = addressText.getText().toString().trim();
        String fax = faxText.getText().toString().trim();
        String email = emailText.getText().toString().trim();
        final String contacts = contactsText.getText().toString().trim();
        String tel = telText.getText().toString().trim();
        String letter = letterText.getText().toString().trim();
        if (!isEmpty(name, salesAddress, address, fax, email, contacts, tel, letter)) {
            dialog.show();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("f_name", name);
                jsonObject.put("f_pic", src);
                jsonObject.put("f_tel", tel);
                jsonObject.put("f_add", address);
                jsonObject.put("f_letter", letter);
                jsonObject.put("f_fax", fax);
                jsonObject.put("f_sales_add", salesAddress);
                jsonObject.put("f_email", email);
                jsonObject.put("f_contacts", contacts);
                HttpUtil.sendPostRequestWithHttp(URL + token, jsonObject.toString(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (dialog != null && dialog.isShowing()) {
                                    dialog.cancel();
                                }
                                Toast.makeText(AddCompanyActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
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
                                    if(content.equals("true")) {
                                        finish();
                                    }else {
                                        Toast.makeText(AddCompanyActivity.this, "上传失败！", Toast.LENGTH_SHORT).show();
                                    }
                                } else if (code == 422) {
                                    Toast.makeText(AddCompanyActivity.this, "名称或简码重复！", Toast.LENGTH_SHORT).show();
                                } else if (code == 401) {
                                    Intent intent = new Intent(AddCompanyActivity.this, LoginActivity.class);
                                    intent.putExtra("token_timeout", "登录超时");
                                    preferences.edit().putString("token", null).commit();
                                    startActivity(intent);
                                    ActivityCollector.finishAllActivity();
                                }else {
                                    Toast.makeText(AddCompanyActivity.this, "上传失败！", Toast.LENGTH_SHORT).show();
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

    public boolean isEmpty(String name, String salesAddress, String address, String fax, String email, String contacts, String tel, String letter) {
        if (src == null || src.equals("")) {
            Toast.makeText(this, "企业图片不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (name.isEmpty()) {
            Toast.makeText(this, "名称不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (salesAddress.isEmpty()) {
            Toast.makeText(this, "销售中心不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (address.isEmpty()) {
            Toast.makeText(this, "地址不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (fax.isEmpty()) {
            Toast.makeText(this, "传真不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (email.isEmpty()) {
            Toast.makeText(this, "邮箱不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (!Util.isValidEmail(email)) {
            Toast.makeText(this, "邮箱格式不正确", Toast.LENGTH_SHORT).show();
            return true;
        } else if (contacts.isEmpty()) {
            Toast.makeText(this, "联系人不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (tel.isEmpty()) {
            Toast.makeText(this, "联系电话不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (letter.isEmpty()) {
            Toast.makeText(this, "简码不能为空", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
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
     * 从相册选取照片的方法
     */
    private void chooseFromAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_ALBUM);
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
                        //swipeRefreshLayout.setRefreshing(true);
                        if (dialog != null && !dialog.isShowing()) {
                            dialog.show();
                        }
                        HttpUtil.sendPostImageWithHttp(UPLOAD + token, path, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (dialog != null && dialog.isShowing()) {
                                            dialog.cancel();
                                        }
                                        Toast.makeText(AddCompanyActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
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
                                        } else if (code == 401) {
                                            Intent intent = new Intent(AddCompanyActivity.this, LoginActivity.class);
                                            intent.putExtra("token_timeout", "登录超时");
                                            preferences.edit().putString("token", null).commit();
                                            startActivity(intent);
                                            ActivityCollector.finishAllActivity();
                                        } else {
                                            Toast.makeText(AddCompanyActivity.this, "图片上传失败", Toast.LENGTH_SHORT).show();
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
            if (dialog != null && !dialog.isShowing()) {
                dialog.show();
            }
            HttpUtil.sendPostImageWithHttp(UPLOAD + token, imagePath, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (dialog != null && dialog.isShowing()) {
                                dialog.cancel();
                            }
                            Toast.makeText(AddCompanyActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
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
                            } else if (code == 401) {
                                Intent intent = new Intent(AddCompanyActivity.this, LoginActivity.class);
                                intent.putExtra("token_timeout", "登录超时");
                                preferences.edit().putString("token", null).commit();
                                startActivity(intent);
                                ActivityCollector.finishAllActivity();
                            } else {
                                Toast.makeText(AddCompanyActivity.this, "图片上传失败", Toast.LENGTH_SHORT).show();
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
        src = content;
        Glide.with(this).load(Util.PATH + src)
                .skipMemoryCache(true) // 不使用内存缓存
                .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁盘缓存
                .error(R.drawable.company_bull).into(pic);
        Toast.makeText(this, "图片上传成功", Toast.LENGTH_SHORT).show();
    }
}
