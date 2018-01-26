package cn.com.larunda.safebox.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import cn.com.larunda.safebox.LeavingBindPersonActivity;
import com.larunda.safebox.R;

/**
 * Created by sddt on 18-1-23.
 */

public class LeavingInfoFragment extends Fragment implements View.OnClickListener {

    private RelativeLayout bindButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.leaving_info_fragment, container, false);
        initView(view);
        return view;
    }

    /**
     * 初始化view
     *
     * @param view
     */
    private void initView(View view) {
        bindButton = view.findViewById(R.id.leaving_info_bind_layout);

        bindButton.setOnClickListener(this);
    }

    /**
     * 点击事件监听
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.leaving_info_bind_layout:
                Intent intent = new Intent(getContext(), LeavingBindPersonActivity.class);
                startActivity(intent);
                break;
        }
    }
}
