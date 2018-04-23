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
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import cn.com.larunda.safebox.BoxAddActivity;
import cn.com.larunda.safebox.LoginActivity;
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

public class BoxAddInfoFragment extends Fragment implements View.OnClickListener {

    public static EditText name_text;
    public static EditText material_text;
    public static EditText size_text;
    public static EditText protect_text;

    private Button putButton;

    public SwipeRefreshLayout swipeRefreshLayout;

    public static final String MESSAGE_URI = Util.URL + "box/";

    public static final String UPLOAD = Util.URL + "upload/file" + Util.TOKEN;
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_ALBUM = 0;

    private ImageView photo;
    private PhotoDialog photoDialog;
    private Uri imageUri;

    private String path;
    public static String url = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.box_add_info_fragment, container, false);
        initView(view);
        initEvent();
        return view;
    }

    /**
     * 初始化点击事件
     */
    private void initEvent() {
        putButton.setOnClickListener(this);

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
        name_text = view.findViewById(R.id.box_add_info_name_text);
        material_text = view.findViewById(R.id.box_add_info_material_text);
        size_text = view.findViewById(R.id.box_add_info_size_text);
        protect_text = view.findViewById(R.id.box_add_info_protect_text);

        photo = view.findViewById(R.id.box_add_info_img);
        photoDialog = new PhotoDialog(getContext());

        putButton = view.findViewById(R.id.box_add_info_button);

        swipeRefreshLayout = view.findViewById(R.id.box_add_info_swipe);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        swipeRefreshLayout.setEnabled(false);//设置swipe不可用

    }

    /**
     * 点击事件监听
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.box_add_info_button:
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
            case R.id.box_add_info_img:
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
            HttpUtil.sendPutRequestWithHttp(MESSAGE_URI + BoxAddActivity.id + Util.TOKEN + BoxAddActivity.token, jsonObject.toString(), new Callback() {
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
                                    BoxAddActivity.preferences.edit().putString("token", null).commit();
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
                        HttpUtil.sendPostImageWithHttp(UPLOAD + BoxAddActivity.token + "&folder_type=" + "box", path, new Callback() {
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
            HttpUtil.sendPostImageWithHttp(UPLOAD + BoxAddActivity.token + "&folder_type=" + "box", imagePath, new Callback() {
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
                BoxAddActivity.preferences.edit().putString("token", null).commit();
                startActivity(intent);
                getActivity().finish();
            }


        } else {
            if (content != null) {
                url = content;
                Glide.with(this).load(Util.PATH + url)
                        .skipMemoryCache(true) // 不使用内存缓存
                        .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁盘缓存
                        .placeholder(R.drawable.company_bull)
                        .error(R.drawable.company_bull).into(photo);
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
