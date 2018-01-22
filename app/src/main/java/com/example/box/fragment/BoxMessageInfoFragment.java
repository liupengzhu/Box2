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

import com.example.box.BindingUserActivity;
import com.example.box.R;

/**
 * Created by sddt on 18-1-18.
 */

public class BoxMessageInfoFragment extends Fragment implements View.OnClickListener {

    RelativeLayout bindingUser_Button;

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


        bindingUser_Button.setOnClickListener(this);
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
                Intent bindingUserIntent = new Intent(getContext(), BindingUserActivity.class);
                startActivity(bindingUserIntent);
                break;
            default:
                break;

        }


    }
}
