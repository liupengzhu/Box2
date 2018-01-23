package com.example.box.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.box.LoginActivity;
import com.example.box.MainActivity;
import com.example.box.R;
import com.example.box.adapter.SqAdapter;
import com.example.box.gson.SqData;
import com.example.box.gson.SqInfo;
import com.example.box.recycler.MySq;
import com.example.box.util.HttpUtil;
import com.example.box.util.Util;
import com.example.selfdialog.ConfirmDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by sddt on 18-1-15.
 */

public class YListFragment extends Fragment {

    public static final String YCSQ_URI = "http://safebox.dsmcase.com:90/api/authorize?_token=";

    public static final String IMG_URI = "http://safebox.dsmcase.com:90";
    RecyclerView recyclerView;
    SqAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    private List<MySq> sqList = new ArrayList<>();
    private LinearLayoutManager manager;

    private RelativeLayout loodingErrorLayout;
    private ImageView loodingLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.y_list_fragment, container, false);
        initView(view);
        //每次fragment创建时还没有网络数据 设置载入背景为可见
        loodingLayout.setVisibility(View.VISIBLE);
        return view;
    }


    /**
     * 初始化view
     * @param view
     */
    private void initView(View view) {

        recyclerView = view.findViewById(R.id.y_recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.yc_swipe);

        loodingErrorLayout = view.findViewById(R.id.y_loading_error_layout);
        loodingLayout = view.findViewById(R.id.y_loading_layout);
        manager = new LinearLayoutManager(getContext());
        adapter = new SqAdapter(sqList);
        setAdapterClick(adapter);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
    }

    /**
     * 设置点击事件
     *
     * @param adapter
     */
    private void setAdapterClick(SqAdapter adapter) {

        //确定按钮的点击事件
        adapter.setQrOnClickListener(new SqAdapter.QrOnClickListener() {
            @Override
            public void onClick(View v, MySq sq) {

                final ConfirmDialog dialog = new ConfirmDialog(getContext());
                String content = sq.getUserName() + "于" + sq.getDate() + "对递送箱序列号为" + sq.getUserXLH() +
                        "发起远程授权开箱，是否授权!";

                dialog.setContentText(content);
                dialog.setNoOnclickListener(new ConfirmDialog.onNoOnclickListener() {
                    @Override
                    public void onNoClick(View v) {
                        dialog.cancel();
                    }
                });

                dialog.show();
            }
        });
        //取消按钮的点击事件
        adapter.setQxOnClickListener(new SqAdapter.QxOnClickListener() {
            @Override
            public void onClick(View v, MySq sq) {

                final ConfirmDialog dialog = new ConfirmDialog(getContext());
                String content = "驳回" + sq.getUserName() + "于" + sq.getDate() + "对递送箱序列号为" + sq.getUserXLH() +
                        "发起远程授权开箱请求！";

                dialog.setContentText(content);
                dialog.setNoOnclickListener(new ConfirmDialog.onNoOnclickListener() {
                    @Override
                    public void onNoClick(View v) {
                        dialog.cancel();
                    }
                });

                dialog.show();
            }
        });
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
     * 发送请求数据
     */
    private void sendRequest() {
        swipeRefreshLayout.setRefreshing(true);
        HttpUtil.sendGetRequestWithHttp(YCSQ_URI + MainActivity.token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        swipeRefreshLayout.setRefreshing(false);
                        loodingErrorLayout.setVisibility(View.VISIBLE);
                        loodingLayout.setVisibility(View.INVISIBLE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final SqInfo sqInfo = Util.handleSqInfo(response.body().string());
                if (sqInfo != null && sqInfo.error == null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initSqList(sqInfo);
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
                            MainActivity.preferences.edit().putString("token", null).commit();
                            startActivity(intent);
                            getActivity().finish();
                        }
                    });
                }

            }
        });
    }

    /**
     * 解析数据 并通知view刷新
     *
     * @param sqInfo
     */
    private void initSqList(SqInfo sqInfo) {

        sqList.clear();
        for (SqData sqData : sqInfo.sqDataList) {

            //Log.d("main",MainActivity.token);
            String img_uri = sqData.user_pic.replace('\\', ' ');
            MySq mySq = new MySq(IMG_URI + img_uri, sqData.user, sqData.code, sqData.date);
            sqList.add(mySq);

        }
        adapter.notifyDataSetChanged();

    }
}
