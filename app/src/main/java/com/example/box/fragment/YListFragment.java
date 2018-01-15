package com.example.box.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.box.MainActivity;
import com.example.box.R;
import com.example.box.adapter.MyItemDecoration;
import com.example.box.adapter.SqAdapter;
import com.example.box.gson.SqData;
import com.example.box.gson.SqInfo;
import com.example.box.recycler.MySq;
import com.example.box.util.HttpUtil;
import com.example.box.util.Util;

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

    RecyclerView recyclerView;
    SqAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    private List<MySq> sqList = new ArrayList<>();
    private LinearLayoutManager manager;

    @SuppressLint("ResourceAsColor")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.y_list_fragment,container,false);
        recyclerView = view.findViewById(R.id.y_recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.yc_swipe);

        manager = new LinearLayoutManager(container.getContext());
        adapter = new SqAdapter(sqList);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new MyItemDecoration());
        swipeRefreshLayout.setColorSchemeColors(R.color.menu_sz_color);



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
     * 发送请求数据
     */
    private void sendRequest() {
        HttpUtil.sendGetRequestWithHttp(YCSQ_URI + MainActivity.token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final SqInfo sqInfo = Util.handleSqInfo(response.body().string()) ;
                if(sqInfo!=null&&sqInfo.error == null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initSqList(sqInfo);
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }else {
                    swipeRefreshLayout.setRefreshing(false);
                }

            }
        });
    }

    /**
     * 解析数据 并通知view刷新
     * @param sqInfo
     */
    private void initSqList(SqInfo sqInfo) {

        sqList.clear();
        for(SqData sqData :sqInfo.sqDataList){

            MySq mySq = new MySq(sqData.user,sqData.user,sqData.code,sqData.date);
            sqList.add(mySq);

        }
        adapter.notifyDataSetChanged();

    }
}
