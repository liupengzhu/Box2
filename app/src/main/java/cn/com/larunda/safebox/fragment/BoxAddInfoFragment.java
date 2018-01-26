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

import cn.com.larunda.safebox.BoxAddUserActivity;
import com.larunda.safebox.R;

/**
 * Created by sddt on 18-1-18.
 */

public class BoxAddInfoFragment extends Fragment implements View.OnClickListener {

    RelativeLayout addButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.box_add_info_fragment, container, false);
        initView(view);
        return view;
    }

    /**
     * 初始化View
     *
     * @param view
     */
    private void initView(View view) {
        addButton = view.findViewById(R.id.box_add_info_add_button);

        addButton.setOnClickListener(this);
    }

    /**
     * 点击事件监听
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.box_add_info_add_button:
                Intent intent = new Intent(getContext(), BoxAddUserActivity.class);
                startActivity(intent);
                break;
            default:
                break;

        }
    }
}
