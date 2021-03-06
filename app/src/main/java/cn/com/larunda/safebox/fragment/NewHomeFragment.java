package cn.com.larunda.safebox.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.circletextview.CircleTextView;
import com.larunda.safebox.R;

import java.io.IOException;

import cn.com.larunda.safebox.LoginActivity;
import cn.com.larunda.safebox.MainActivity;
import cn.com.larunda.safebox.gson.Home;
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

    private CircleTextView circleTextView;
    private TextView shippingText;
    private TextView alarmText;
    private TextView exceptionText;
    private TextView totalText;
    private TextView toUsedText;

    private TextView areaText;
    private TextView leavingText;
    private TextView defenceText;

    private SwipeRefreshLayout refreshLayout;

    private LinearLayout loadingErrorLayout;
    private ImageView loadingLayout;
    private LinearLayout layout;

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
        String content = preferences.getString("homeInfo", null);
        if (content != null) {
            if (Util.isGoodJson(content)) {
                NewHomeInfo home = Util.handleNewHomeInfo(content);
                showInfo(home);
            } else {
                queryInfo();
            }
        } else {
            //每次fragment创建时还没有网络数据 设置载入背景为可见
            loadingLayout.setVisibility(View.VISIBLE);
            loadingErrorLayout.setVisibility(View.GONE);
            layout.setVisibility(View.GONE);
            queryInfo();
        }
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

        circleTextView = view.findViewById(R.id.device_status_circleTextView);
        shippingText = view.findViewById(R.id.device_status_shipping_text);
        alarmText = view.findViewById(R.id.device_status_alarm_text);
        exceptionText = view.findViewById(R.id.device_status_exception_text);
        totalText = view.findViewById(R.id.device_status_total_text);
        toUsedText = view.findViewById(R.id.device_status_to_used_text);

        areaText = view.findViewById(R.id.warning_count_area);
        leavingText = view.findViewById(R.id.warning_count_leaving);
        defenceText = view.findViewById(R.id.warning_count_defence);

        refreshLayout = view.findViewById(R.id.new_home_refresh);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        refreshLayout.setProgressViewOffset(false, 100, 400);//设置刷新进度条偏移量
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryInfo();
            }
        });

        loadingErrorLayout = view.findViewById(R.id.new_home_loading_error_layout);
        loadingLayout = view.findViewById(R.id.new_home_loading_layout);
        layout = view.findViewById(R.id.new_home_layout);
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

    /**
     * 查询首页数据
     */
    public void queryInfo() {
        refreshLayout.setRefreshing(true);
        HttpUtil.sendGetRequestWithHttp(BOX_URI + token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadingLayout.setVisibility(View.GONE);
                            loadingErrorLayout.setVisibility(View.VISIBLE);
                            layout.setVisibility(View.GONE);
                            refreshLayout.setRefreshing(false);
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String content = response.body().string();
                if (Util.isGoodJson(content)) {
                    final NewHomeInfo home = Util.handleNewHomeInfo(content);
                    if (getActivity() != null) {
                        if (home != null && home.error == null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showInfo(home);
                                    preferences.edit().putString("homeInfo", content).commit();
                                    loadingLayout.setVisibility(View.GONE);
                                    loadingErrorLayout.setVisibility(View.GONE);
                                    layout.setVisibility(View.VISIBLE);
                                    refreshLayout.setRefreshing(false);
                                }
                            });

                        } else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                                    intent.putExtra("token_timeout", "登录超时");
                                    preferences.edit().putString("token", null).commit();
                                    startActivity(intent);
                                    getActivity().finish();
                                }
                            });
                        }
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
        if (home.info != null) {
            if (home.info.company_name != null) {
                companyName.setText(home.info.company_name);
            } else {
                companyName.setText("");
            }
            if (home.info.company_tel != null) {
                companyTel.setText(home.info.company_tel);
            } else {
                companyTel.setText("");
            }
            if (home.info.company_pic != null) {
                Glide.with(this).load(Util.PATH + home.info.company_pic).placeholder(R.drawable.company)
                        .dontAnimate()
                        .error(R.drawable.company)
                        .into(companyImg);
            }
            circleTextView.setNumber(home.info.on_line + "");
            shippingText.setText(home.info.shipping + "");
            alarmText.setText(home.info.alarm + "");
            exceptionText.setText((home.info.shipping - home.info.on_line) + "");
            totalText.setText((home.info.total - home.info.to_used) + "");
            toUsedText.setText(home.info.to_used + "");
            if (home.info.on_line == 0) {
                circleTextView.setCircleAngle(0);
            } else {
                circleTextView.setCircleAngle((float) home.info.on_line / (float) home.info.shipping * 360);
            }
            areaText.setText(home.alarm_num.area_alarm + "");
            leavingText.setText(home.alarm_num.leaving_alarm + "");
            defenceText.setText(home.alarm_num.defence_alarm + "");
        }

    }
}
