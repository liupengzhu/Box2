package cn.com.larunda.safebox.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.com.larunda.safebox.BoxActivity;
import cn.com.larunda.safebox.BoxAddUserActivity;
import cn.com.larunda.safebox.BoxInfoLogActivity;
import cn.com.larunda.safebox.BoxInfoSoundActivity;
import cn.com.larunda.safebox.DynamicPasswordActivity;
import cn.com.larunda.safebox.LoginActivity;
import cn.com.larunda.safebox.MainActivity;
import cn.com.larunda.safebox.gson.BoxMessage;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import com.larunda.safebox.R;

import java.io.IOException;

/**
 * Created by sddt on 18-1-18.
 */

public class BoxMessageInfoFragment extends Fragment implements View.OnClickListener {

    RelativeLayout bindingUser_Button;
    RelativeLayout password_Button;
    RelativeLayout log_Button;
    RelativeLayout sound_Button;
    public static final String MESSAGE_URI = "http://safebox.dsmcase.com:90/api/box/";

    TextView material_text;
    TextView size_text;
    TextView protect_text;
    TextView electricity_text;
    TextView bind_user_text;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.box_message_info_fragment, container, false);
        initView(view);
        initEvent();
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        sendHttpRequest();
    }

    /**
     * 发送网络请求
     */
    private void sendHttpRequest() {

        HttpUtil.sendGetRequestWithHttp(MESSAGE_URI + BoxActivity.ID + "?_token=" + BoxActivity.token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

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
        bind_user_text.setText("已绑定" +boxMessage.bind_user_num+ "个用户");
    }

    /**
     * 初始化点击事件
     */
    private void initEvent() {
        bindingUser_Button.setOnClickListener(this);
        password_Button.setOnClickListener(this);
        log_Button.setOnClickListener(this);
        sound_Button.setOnClickListener(this);
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
        material_text = view.findViewById(R.id.box_message_info_meterial_text);
        size_text = view.findViewById(R.id.box_message_info_size_text);
        protect_text = view.findViewById(R.id.box_message_info_protect_text);
        electricity_text = view.findViewById(R.id.box_message_info_electricity_text);

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
            default:
                break;

        }


    }


}
