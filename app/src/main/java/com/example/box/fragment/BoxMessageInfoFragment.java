package com.example.box.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.box.BoxAddUserActivity;
import com.example.box.BoxInfoLogActivity;
import com.example.box.BoxInfoSoundActivity;
import com.example.box.DynamicPasswordActivity;
import com.example.box.R;

/**
 * Created by sddt on 18-1-18.
 */

public class BoxMessageInfoFragment extends Fragment implements View.OnClickListener {

    RelativeLayout bindingUser_Button;
    RelativeLayout password_Button;
    RelativeLayout log_Button;
    RelativeLayout sound_Button;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.box_message_info_fragment, container, false);
        initView(view);
        return view;
    }

    /**
     * 初始化View
     *
     * @param view
     */
    private void initView(View view) {
        bindingUser_Button = view.findViewById(R.id.box_message_binding_user);
        password_Button = view.findViewById(R.id.box_message_password);
        log_Button = view.findViewById(R.id.box_message_log);
        sound_Button = view.findViewById(R.id.box_message_sound);

        bindingUser_Button.setOnClickListener(this);
        password_Button.setOnClickListener(this);
        log_Button.setOnClickListener(this);
        sound_Button.setOnClickListener(this);
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
                Intent bindingUserIntent = new Intent(getContext(), BoxAddUserActivity.class);
                startActivity(bindingUserIntent);
                break;
            case R.id.box_message_password:
                Intent passwordIntent = new Intent(getContext(), DynamicPasswordActivity.class);
                startActivity(passwordIntent);
                break;
            case R.id.box_message_log:
                Intent logIntent = new Intent(getContext(), BoxInfoLogActivity.class);
                startActivity(logIntent);
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
