package cn.com.larunda.safebox.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.larunda.safebox.R;

import java.io.IOException;

import cn.com.larunda.safebox.LoginActivity;
import cn.com.larunda.safebox.MainActivity;
import cn.com.larunda.safebox.gson.NewHomeInfo;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static cn.com.larunda.safebox.MainActivity.drawerLayout;

/**
 * Created by sddt on 18-2-24.
 */

public class NewHomeFragment extends Fragment implements View.OnClickListener {
    private Button menuButton;
    private TextView deviceButton;
    public static final String BOX_URI = Util.URL + "app/home" + Util.TOKEN;
    private SharedPreferences preferences;
    private String token;

    private CircleImageView companyImg;
    private TextView companyName;
    private TextView companyTel;

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

    @Override
    public void onResume() {
        super.onResume();
        queryInfo();
    }

    /**
     * 初始化view
     *
     * @param view
     */
    private void initView(View view) {

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        token = preferences.getString("token", null);

        menuButton = view.findViewById(R.id.new_home_left_button);
        deviceButton = view.findViewById(R.id.device_status_button);

        companyImg = view.findViewById(R.id.company_info_img);
        companyName = view.findViewById(R.id.company_info_name);
        companyTel = view.findViewById(R.id.company_info_tel);
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

    public void queryInfo() {
        HttpUtil.sendGetRequestWithHttp(BOX_URI + token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String content = response.body().string();
                Log.d("main", content);
                if (Util.isGoodJson(content)) {
                    final NewHomeInfo home = Util.handleNewHomeInfo(content);
                    if (home != null && home.error == null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showInfo(home);
                            }
                        });

                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                intent.putExtra("token_timeout", "登录超时");
                                MainActivity.preferences.edit().putString("token", null).commit();
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
     * 显示首页信息
     *
     * @param home
     */
    private void showInfo(NewHomeInfo home) {
        /*if(home.Info.)*/

    }
}
