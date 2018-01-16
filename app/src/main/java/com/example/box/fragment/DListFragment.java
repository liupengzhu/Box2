package com.example.box.fragment;

import android.content.Intent;
import android.os.Bundle;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.box.LoginActivity;
import com.example.box.MainActivity;
import com.example.box.R;
import com.example.box.adapter.BoxAdapter;
import com.example.box.gson.BoxData;
import com.example.box.gson.BoxInfo;
import com.example.box.recycler.MyBox;
import com.example.box.util.HttpUtil;
import com.example.box.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class DListFragment extends Fragment {


    public static final String BOX_URI = "http://safebox.dsmcase.com:90/api/box?_token=";
    public static final String IMG_URI = "http://safebox.dsmcase.com:90";
    private List<MyBox> myBoxList = new ArrayList<>();
    private RecyclerView recyclerView;
    private BoxAdapter adapter;
    private LinearLayoutManager manager;
    private SwipeRefreshLayout refreshLayout;
    private RelativeLayout loodingErrorLayout;
    private ImageView loodingLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.d_list_fragment, container, false);
        recyclerView = view.findViewById(R.id.dsx_list);
        refreshLayout = view.findViewById(R.id.box_list_swiper);

        loodingErrorLayout = view.findViewById(R.id.d_loading_error_layout);
        loodingLayout = view.findViewById(R.id.d_loading_layout);

        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        manager = new LinearLayoutManager(container.getContext());
        adapter = new BoxAdapter(myBoxList);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sendRequest();

            }
        });

        //每次fragment创建时还没有网络数据 设置载入背景为可见
        loodingLayout.setVisibility(View.VISIBLE);

        sendRequest();
        return view;
    }

    //发送网络请求
    private void sendRequest() {
        refreshLayout.setRefreshing(true);
        HttpUtil.sendGetRequestWithHttp(BOX_URI + MainActivity.token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {


                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                        loodingErrorLayout.setVisibility(View.VISIBLE);
                        loodingLayout.setVisibility(View.INVISIBLE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final BoxInfo boxInfo = Util.handleBoxInfo(response.body().string());

                if (boxInfo != null && boxInfo.error == null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initBoxList(boxInfo);
                            refreshLayout.setRefreshing(false);
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

    //解析BoxInfo
    private void initBoxList(BoxInfo boxInfo) {
        myBoxList.clear();
        for (BoxData boxData : boxInfo.boxDataList) {


            String img_uri = boxData.f_pic.replace('\\', ' ');
            MyBox box = new MyBox();
            box.setBox_name(boxData.name);
            box.setBox_dl(boxData.electricity);
            box.setBox_img(IMG_URI + img_uri);
            box.setBox_qx(Integer.parseInt(boxData.level));
            if (boxData.is_defence == "0") {
                box.setIs_bf(false);
            } else {
                box.setIs_bf(true);
            }
            if (boxData.is_locked == "0") {
                box.setIs_sd(false);
            } else {
                box.setIs_sd(true);
            }
            myBoxList.add(box);

        }
        adapter.notifyDataSetChanged();


    }
}
