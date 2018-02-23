package cn.com.larunda.safebox.fragment;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.com.larunda.safebox.BoxActivity;
import cn.com.larunda.safebox.BoxAddActivity;
import cn.com.larunda.safebox.BoxAddUserActivity;
import cn.com.larunda.safebox.BoxInfoLogActivity;
import cn.com.larunda.safebox.BoxInfoSoundActivity;
import cn.com.larunda.safebox.DynamicPasswordActivity;
import cn.com.larunda.safebox.EditUserActivity;
import cn.com.larunda.safebox.LoginActivity;
import cn.com.larunda.safebox.MainActivity;
import cn.com.larunda.safebox.gson.BoxMessage;
import cn.com.larunda.safebox.gson.PhotoUrl;
import cn.com.larunda.safebox.gson.Result;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.larunda.safebox.R;
import com.larunda.selfdialog.PhotoDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

/**
 * Created by sddt on 18-1-18.
 */

public class BoxMessageInfoFragment extends Fragment implements View.OnClickListener {

    RelativeLayout bindingUser_Button;
    RelativeLayout password_Button;
    RelativeLayout log_Button;
    RelativeLayout sound_Button;
    public static final String MESSAGE_URI = Util.URL + "box/";

    public static final String UPLOAD = Util.URL + "upload/file" + Util.TOKEN;
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_ALBUM = 0;

    public static EditText name_text;
    public static EditText material_text;
    public static EditText size_text;
    public static EditText protect_text;
    TextView electricity_text;
    TextView bind_user_text;

    public SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout loodingErrorLayout;
    private ImageView loodingLayout;
    private LinearLayout layout;
    private Button putButton;

    private TextView soundText;

    private boolean isInit = false;

    private ImageView photo;
    private PhotoDialog photoDialog;
    private Uri imageUri;

    private String path;
    public static String url = null;
    private static final int BIND_USER_REQUEST = 2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.box_message_info_fragment, container, false);
        initView(view);
        initEvent();
        //每次fragment创建时还没有网络数据 设置载入背景为可见
        loodingLayout.setVisibility(View.VISIBLE);
        loodingErrorLayout.setVisibility(View.GONE);
        layout.setVisibility(View.GONE);
        isInit = true;
        sendHttpRequest();
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * 发送网络请求
     */
    private void sendHttpRequest() {
        swipeRefreshLayout.setRefreshing(true);
        HttpUtil.sendGetRequestWithHttp(MESSAGE_URI + BoxActivity.ID + Util.TOKEN + BoxActivity.token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
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
                    final BoxMessage boxMessage = Util.handleBoxMessage(content);
                    if (boxMessage != null && boxMessage.error == null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initBoxMessage(boxMessage);
                                swipeRefreshLayout.setRefreshing(false);
                                layout.setVisibility(View.VISIBLE);
                                loodingErrorLayout.setVisibility(View.GONE);
                                loodingLayout.setVisibility(View.GONE);
                            }
                        });
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                intent.putExtra("token_timeout", "登录超时");
                                BoxActivity.preferences.edit().putString("token", null).commit();
                                startActivity(intent);
                                getActivity().finish();
                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * 解析数据
     *
     * @param boxMessage
     */
    private void initBoxMessage(BoxMessage boxMessage) {
        if (boxMessage.name != null) {
            name_text.setText(boxMessage.name);
        } else {
            name_text.setText("");
        }
        if (boxMessage.material != null) {
            material_text.setText(boxMessage.material);
        } else {
            material_text.setText("");
        }
        if (boxMessage.size != null) {
            size_text.setText(boxMessage.size);
        } else {
            size_text.setText("");
        }
        if (boxMessage.protext_level != null) {
            protect_text.setText(boxMessage.protext_level);
        } else {
            protect_text.setText("");
        }
        if (boxMessage.electricity != null) {
            electricity_text.setText(boxMessage.electricity + "%");
        } else {
            electricity_text.setText("");
        }
        bind_user_text.setText("已绑定" + boxMessage.bind_user_num + "个用户");
        if (boxMessage.record_num != 0) {
            soundText.setVisibility(View.VISIBLE);
            soundText.setText(boxMessage.record_num + "");
        } else {
            soundText.setVisibility(View.GONE);
        }
        if (boxMessage.f_pic != null) {
            url = boxMessage.f_pic;
            Glide.with(this).load(Util.PATH + url).placeholder(R.drawable.box_null).error(R.drawable.box_null).into(photo);
        } else {
            url = null;
        }
    }

    /**
     * 初始化点击事件
     */
    private void initEvent() {
        bindingUser_Button.setOnClickListener(this);
        password_Button.setOnClickListener(this);
        log_Button.setOnClickListener(this);
        sound_Button.setOnClickListener(this);
        putButton.setOnClickListener(this);
        loodingErrorLayout.setOnClickListener(this);

        photo.setOnClickListener(this);
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
    }

    /**
     * 初始化View
     *
     * @param view
     */
    private void initView(View view) {
        bind_user_text = view.findViewById(R.id.box_message_info_bind_user_text);
        bindingUser_Button = view.findViewById(R.id.box_message_binding_user);
        password_Button = view.findViewById(R.id.box_message_password);
        log_Button = view.findViewById(R.id.box_message_log);
        sound_Button = view.findViewById(R.id.box_message_sound);

        photo = view.findViewById(R.id.box_message_info_img);
        photoDialog = new PhotoDialog(getContext());


        soundText = view.findViewById(R.id.box_message_info_sound_count_text);

        name_text = view.findViewById(R.id.box_message_info_name_text);
        material_text = view.findViewById(R.id.box_message_info_material_text);
        size_text = view.findViewById(R.id.box_message_info_size_text);
        protect_text = view.findViewById(R.id.box_message_info_protect_text);
        electricity_text = view.findViewById(R.id.box_message_info_electricity_text);

        putButton = view.findViewById(R.id.box_message_info_button);

        loodingErrorLayout = view.findViewById(R.id.box_message_info_loading_error_layout);
        loodingLayout = view.findViewById(R.id.box_message_info_loading_layout);
        layout = view.findViewById(R.id.box_message_info_layout);

        swipeRefreshLayout = view.findViewById(R.id.box_message_info_swipe);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        swipeRefreshLayout.setEnabled(false);//设置swipe不可用
    }

    /**
     * 点击事件处理
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.box_message_binding_user:
                if (BoxActivity.ID != null) {
                    Intent bindingUserIntent = new Intent(getContext(), BoxAddUserActivity.class);
                    bindingUserIntent.putExtra("id", BoxActivity.ID);
                    startActivityForResult(bindingUserIntent, BIND_USER_REQUEST);
                }
                break;
            case R.id.box_message_password:
                if (BoxActivity.ID != null) {
                    Intent passwordIntent = new Intent(getContext(), DynamicPasswordActivity.class);
                    passwordIntent.putExtra("id", BoxActivity.ID);
                    startActivity(passwordIntent);
                }
                break;
            case R.id.box_message_log:
                if (BoxActivity.ID != null) {
                    Intent logIntent = new Intent(getContext(), BoxInfoLogActivity.class);
                    logIntent.putExtra("id", BoxActivity.ID);
                    startActivity(logIntent);
                }
                break;
            case R.id.box_message_sound:
                if (BoxActivity.ID != null) {
                    Intent soundIntent = new Intent(getContext(), BoxInfoSoundActivity.class);
                    soundIntent.putExtra("id", BoxActivity.ID);
                    startActivity(soundIntent);
                }
                break;
            case R.id.box_message_info_button:
                if (name_text != null && material_text != null && size_text != null && protect_text != null) {
                    String name = name_text.getText().toString().trim();
                    String material = material_text.getText().toString().trim();
                    String size = size_text.getText().toString().trim();
                    String protect = protect_text.getText().toString().trim();
                    if (!isEmpty(name, material, size, protect)) {
                        sendPutRequest(name, material, size, protect);
                    }
                }
                break;
            case R.id.box_message_info_loading_error_layout:
                sendHttpRequest();
                break;
            case R.id.box_message_info_img:
                photoDialog.show();
                photoDialog.setText("编辑图片");
                break;
            default:
                break;

        }


    }

    /**
     * 判断字符串是否为空
     *
     * @param material
     * @param size
     * @param protect
     * @return
     */
    private boolean isEmpty(String name, String material, String size, String protect) {
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getContext(), "别名不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(material)) {
            Toast.makeText(getContext(), "材质不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(size)) {
            Toast.makeText(getContext(), "尺寸不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(protect)) {
            Toast.makeText(getContext(), "防护等级不能为空", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    /**
     * 更新递送箱信息
     *
     * @param trim
     * @param trim1
     * @param trim2
     */
    private void sendPutRequest(String name, String trim, String trim1, String trim2) {
        swipeRefreshLayout.setRefreshing(true);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("f_aliases", name);
            jsonObject.put("f_material", trim);
            jsonObject.put("f_size", trim1);
            jsonObject.put("f_protect_grade", trim2);
            jsonObject.put("type", "app");
            HttpUtil.sendPutRequestWithHttp(MESSAGE_URI + BoxActivity.ID + Util.TOKEN + BoxActivity.token, jsonObject.toString(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "网络异常", Toast.LENGTH_SHORT).show();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String content = response.body().string();
                    if (Util.isGoodJson(content)) {
                        final Result result = Util.handleResult(content);
                        if (result != null && result.error == null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    parseResult(result);
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            });
                        } else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                                    intent.putExtra("token_timeout", "登录超时");
                                    BoxActivity.preferences.edit().putString("token", null).commit();
                                    startActivity(intent);
                                    getActivity().finish();
                                }
                            });
                        }
                    }
                }

            });


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void parseResult(Result result) {
        if (result.data != null && result.data.equals("true")) {
            Toast.makeText(getContext(), "更新成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "更新失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
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
        File outputImage = new File(getContext().getExternalCacheDir(), "output_image.jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();


        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(getContext(),
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(imageUri));
                        path = "/sdcard/Android/data/com.example.box//cache/output_image.jpg";
                        swipeRefreshLayout.setRefreshing(true);
                        HttpUtil.sendPostImageWithHttp(UPLOAD + BoxActivity.token + "&folder_type=" + "box", path, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            swipeRefreshLayout.setRefreshing(false);
                                            Toast.makeText(getContext(), "网络错误", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                final String content = response.body().string();
                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            parseContent(content);
                                        }
                                    });
                                }
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
            case BIND_USER_REQUEST:
                if (data != null) {
                    bind_user_text.setText("已绑定" + data.getExtras().getInt("count",0) + "个用户");
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
        if (DocumentsContract.isDocumentUri(getContext(), uri)) {
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
            HttpUtil.sendPostImageWithHttp(UPLOAD + BoxActivity.token + "&folder_type=" + "box", imagePath, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                swipeRefreshLayout.setRefreshing(false);
                                Toast.makeText(getContext(), "网络错误", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String content = response.body().string();
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                parseContent(content);
                            }
                        });
                    }
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
                    Toast.makeText(getContext(), photoUrl.getMessage(), Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            } else {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.putExtra("token_timeout", "登录超时");
                BoxActivity.preferences.edit().putString("token", null).commit();
                startActivity(intent);
                getActivity().finish();
            }


        } else {
            if (content != null) {
                url = content;
                Glide.with(this).load(Util.PATH + url)
                        .skipMemoryCache(true) // 不使用内存缓存
                        .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁盘缓存
                        .placeholder(R.drawable.box_null)
                        .error(R.drawable.box_null).into(photo);
                Toast.makeText(getContext(), "图片上传成功", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }

        }
    }


    private String getImagePath(Uri externalContentUri, String selection) {
        String path = null;
        Cursor cursor = getContext().getContentResolver().query(externalContentUri, null, selection, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;

    }
}
