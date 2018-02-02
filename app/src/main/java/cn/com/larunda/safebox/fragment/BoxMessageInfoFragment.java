package cn.com.larunda.safebox.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import cn.com.larunda.safebox.BoxAddUserActivity;
import cn.com.larunda.safebox.BoxInfoLogActivity;
import cn.com.larunda.safebox.BoxInfoSoundActivity;
import cn.com.larunda.safebox.DynamicPasswordActivity;
import cn.com.larunda.safebox.LoginActivity;
import cn.com.larunda.safebox.MainActivity;
import cn.com.larunda.safebox.gson.BoxMessage;
import cn.com.larunda.safebox.gson.Result;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import com.larunda.safebox.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by sddt on 18-1-18.
 */

public class BoxMessageInfoFragment extends BaseFragment implements View.OnClickListener {

    RelativeLayout bindingUser_Button;
    RelativeLayout password_Button;
    RelativeLayout log_Button;
    RelativeLayout sound_Button;
    public static final String MESSAGE_URI = "http://safebox.dsmcase.com:90/api/box/";

    EditText material_text;
    EditText size_text;
    EditText protect_text;
    TextView electricity_text;
    TextView bind_user_text;

    public SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout loodingErrorLayout;
    private ImageView loodingLayout;
    private LinearLayout layout;
    private Button putButton;

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
        HttpUtil.sendGetRequestWithHttp(MESSAGE_URI + BoxActivity.ID + "?_token=" + BoxActivity.token, new Callback() {
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
        });
    }

    /**
     * 解析数据
     *
     * @param boxMessage
     */
    private void initBoxMessage(BoxMessage boxMessage) {
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
                    startActivity(bindingUserIntent);
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
                Intent soundIntent = new Intent(getContext(), BoxInfoSoundActivity.class);
                startActivity(soundIntent);
                break;
            case R.id.box_message_info_button:
                if (material_text != null && size_text != null && protect_text != null) {
                    String material = material_text.getText().toString().trim();
                    String size = size_text.getText().toString().trim();
                    String protect = protect_text.getText().toString().trim();
                    if (!isEmpty(material, size, protect)) {
                        sendPutRequest(material, size, protect);
                    }
                }
                break;
            case R.id.box_message_info_loading_error_layout:
                sendHttpRequest();
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
    private boolean isEmpty(String material, String size, String protect) {
        if (TextUtils.isEmpty(material)) {
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
    private void sendPutRequest(String trim, String trim1, String trim2) {
        swipeRefreshLayout.setRefreshing(true);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("f_material", trim);
            jsonObject.put("f_size", trim1);
            jsonObject.put("f_protect_grade", trim2);
            jsonObject.put("type", "app");
            HttpUtil.sendPutRequestWithHttp(MESSAGE_URI + BoxActivity.ID + "?_token=" + BoxActivity.token, jsonObject.toString(), new Callback() {
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
    protected void loadData() {
        sendHttpRequest();
    }
}
