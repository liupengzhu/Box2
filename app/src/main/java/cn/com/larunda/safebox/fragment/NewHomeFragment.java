package cn.com.larunda.safebox.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.larunda.safebox.R;

import cn.com.larunda.safebox.MainActivity;

import static cn.com.larunda.safebox.MainActivity.drawerLayout;

/**
 * Created by sddt on 18-2-24.
 */

public class NewHomeFragment extends Fragment implements View.OnClickListener {
    private Button menuButton;
    private TextView deviceButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_home_fragment, container, false);
        initView(view);
        initEvent();
        return view;
    }

    /**
     * 初始化点击事件
     */
    private void initEvent() {
        menuButton.setOnClickListener(this);
        deviceButton.setOnClickListener(this);
    }

    /**
     * 初始化view
     *
     * @param view
     */
    private void initView(View view) {
        menuButton = view.findViewById(R.id.new_home_left_button);
        deviceButton = view.findViewById(R.id.device_status_button);
    }

    /**
     * 点击事件监听
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_home_left_button:
                drawerLayout.openDrawer(Gravity.START);
                break;
            case R.id.device_status_button:
                MainActivity.viewPager.setCurrentItem(1);
                break;
            default:
                break;
        }
    }
}
