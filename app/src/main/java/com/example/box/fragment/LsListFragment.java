package com.example.box.fragment;

import android.annotation.SuppressLint;
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

import com.example.box.MainActivity;
import com.example.box.R;
import com.example.box.adapter.MyItemDecoration;
import com.example.box.adapter.SqLsAdapter;
import com.example.box.gson.SqInfo;
import com.example.box.gson.SqLsData;
import com.example.box.gson.SqLsInfo;
import com.example.box.recycler.MySqLs;
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

public class LsListFragment extends Fragment {

    List<MySqLs> sqLsList = new ArrayList<>();
    RecyclerView recyclerView;
    SqLsAdapter adapter;
    LinearLayoutManager manager;
    SwipeRefreshLayout swipeRefreshLayout;

    public static final String SQLS_URI = "http://safebox.dsmcase.com:90/api/authorize/history?_token=";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ls_list_fragment,container,false);
        initView(view);
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

        HttpUtil.sendGetRequestWithHttp(SQLS_URI + MainActivity.token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                swipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final SqLsInfo sqLsInfo = Util.handleSqLsInfo(response.body().string());
                if(sqLsInfo!=null&&sqLsInfo.error==null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showInfo(sqLsInfo);
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
     * 通知view更新信息
     * @param sqLsInfo
     */
    private void showInfo(SqLsInfo sqLsInfo) {

        sqLsList.clear();
        for(SqLsData sqLsData : sqLsInfo.sqLsDataList){
            MySqLs mySqLs = new MySqLs(sqLsData.date,sqLsData.content);
            sqLsList.add(mySqLs);
        }
        adapter.notifyDataSetChanged();

    }

    /**
     * 初始化view
     * @param view
     */
    @SuppressLint("ResourceAsColor")
    private void initView(View view) {
        swipeRefreshLayout = view.findViewById(R.id.ls_swipe);
        recyclerView = view.findViewById(R.id.ls_recycler_view);
        adapter = new SqLsAdapter(sqLsList);
        manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        swipeRefreshLayout.setColorSchemeColors(R.color.colorPrimary);
    }
}
