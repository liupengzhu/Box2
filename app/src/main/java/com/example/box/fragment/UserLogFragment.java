package com.example.box.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.box.LoginActivity;
import com.example.box.MainActivity;
import com.example.box.R;
import com.example.box.adapter.UserLogAdapter;
import com.example.box.gson.TotalLogData;
import com.example.box.gson.TotalLogInfo;
import com.example.box.recycler.UserLog;
import com.example.box.util.HttpUtil;
import com.example.box.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by sddt on 18-1-16.
 */

public class UserLogFragment extends Fragment {
    List<UserLog> userLogList = new ArrayList<>();
    RecyclerView recyclerView;
    UserLogAdapter adapter;
    LinearLayoutManager manager;
    SwipeRefreshLayout swipeRefreshLayout;

    private RelativeLayout loodingErrorLayout;
    private ImageView loodingLayout;

    public static final String SQLS_URI = "http://safebox.dsmcase.com:90/api/log?_token=";
    public static final String TYPE = "&type=0";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_log_fragment, container, false);
        initView(view);
        //每次fragment创建时还没有网络数据 设置载入背景为可见
        loodingLayout.setVisibility(View.VISIBLE);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sendRequest();
            }
        });
        sendRequest();
    }

    /**
     * 发送网络请求并解析数据
     */
    private void sendRequest() {

        swipeRefreshLayout.setRefreshing(false);
        HttpUtil.sendGetRequestWithHttp(SQLS_URI + MainActivity.token + TYPE, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //
                        swipeRefreshLayout.setRefreshing(false);
                        loodingErrorLayout.setVisibility(View.VISIBLE);
                        loodingLayout.setVisibility(View.INVISIBLE);
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final TotalLogInfo totalLogInfo = Util.handleTotalLogInfo(response.body().string());
                if (totalLogInfo != null && totalLogInfo.error == null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showInfo(totalLogInfo);
                            swipeRefreshLayout.setRefreshing(false);
                            loodingErrorLayout.setVisibility(View.INVISIBLE);
                            loodingLayout.setVisibility(View.INVISIBLE);
                        }
                    });

                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            intent.putExtra("token_timeout", "登录超时");
                            MainActivity.preferences.edit().putString("token",null).commit();
                            startActivity(intent);
                            getActivity().finish();
                        }
                    });
                }


            }
        });

    }

    /**
     * 通知view更新信息
     *
     * @param totalLogInfo
     */
    private void showInfo(TotalLogInfo totalLogInfo) {

        userLogList.clear();
        for (TotalLogData totalLogData : totalLogInfo.totalLogData) {
            UserLog userLog = new UserLog(totalLogData.created_at, totalLogData.info, totalLogData.title);
            userLogList.add(userLog);
        }
        adapter.notifyDataSetChanged();

    }

    /**
     * 初始化view
     *
     * @param view
     */
    private void initView(View view) {
        swipeRefreshLayout = view.findViewById(R.id.user_log_swipe);
        recyclerView = view.findViewById(R.id.user_log_recycler_view);
        loodingErrorLayout = view.findViewById(R.id.user_log_loading_error_layout);
        loodingLayout = view.findViewById(R.id.user_log_loading_layout);
        adapter = new UserLogAdapter(userLogList);
        manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
    }
}
