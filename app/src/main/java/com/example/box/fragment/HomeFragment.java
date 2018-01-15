package com.example.box.fragment;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.example.box.MainActivity;
import com.example.box.R;
import com.example.box.adapter.LogAdapter;
import com.example.box.adapter.MyItemDecoration;
import com.example.box.gson.Home;
import com.example.box.gson.LogInfo;
import com.example.box.recycler.MyLog;
import com.example.box.util.HttpUtil;
import com.example.box.util.Util;
import com.example.titlebar.TitleBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    public static final String BOX_URI = "http://safebox.dsmcase.com:90/api/app/home?_token=";
    TextView totalView;
    TextView defendView;
    TextView lockedView;
    TextView bindView;

    TextView leaving_alarm_view;
    TextView area_alarm_view;

    ImageView serverState;
    ImageView dbState;
    ImageView computerState;
    TextView memoryText;
    TextView diskText;

    RecyclerView recyclerView;

    List<MyLog> myLogList = new ArrayList<>();
    private LogAdapter adapter;
    private LinearLayoutManager manager;

    public SwipeRefreshLayout swipeRefreshLayout;



    private RelativeLayout loodingErrorLayout;

    private ImageView loodingLayout;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        initView(view);
        return view;
    }

    /**
     * 初始化View
     * @param view
     */
    private void initView(View view) {

        totalView = view.findViewById(R.id.total_text);
        defendView = view.findViewById(R.id.defend_text);
        lockedView = view.findViewById(R.id.locked_text);
        bindView = view.findViewById(R.id.bind_text);

        leaving_alarm_view = view.findViewById(R.id.leaving_alarm_text);
        area_alarm_view = view.findViewById(R.id.area_alarm_text);

        serverState = view.findViewById(R.id.server_img);
        dbState = view.findViewById(R.id.db_img);
        computerState = view.findViewById(R.id.computer_img);
        memoryText = view.findViewById(R.id.memory_text);
        diskText = view.findViewById(R.id.disk_text);
        loodingErrorLayout = view.findViewById(R.id.loading_error_layout);
        loodingLayout = view.findViewById(R.id.loading_layout);
        recyclerView = view.findViewById(R.id.log_recycler);
        manager = new LinearLayoutManager(getActivity());
        adapter = new LogAdapter(myLogList);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout = view.findViewById(R.id.swiper);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));

        //每次fragment创建时还没有网络数据 设置载入背景为可见
        loodingLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryInfo();

            }
        });

        queryInfo();
    }

    public void queryInfo(){
        swipeRefreshLayout.setRefreshing(true);

        HttpUtil.sendGetRequestWithHttp(BOX_URI  + MainActivity.token, new Callback() {
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
                String content = response.body().string();
                final Home home = Util.handleHomeInfo(content);
                if (home != null && home.error == null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showInfo(home);
                            swipeRefreshLayout.setRefreshing(false);
                            loodingErrorLayout.setVisibility(View.INVISIBLE);
                            loodingLayout.setVisibility(View.INVISIBLE);

                        }


                    });


                } else {

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


            }
        });


    }


    private void showInfo(Home home) {
        initList(home);

        totalView.setText(home.info.total_num);
        defendView.setText(home.info.defend_num);
        lockedView.setText(home.info.locked_num);
        bindView.setText(home.info.bind_num);
        leaving_alarm_view.setText(home.alarm_num.area_alarm);
        area_alarm_view.setText(home.alarm_num.leaving_alarm);
        memoryText.setText(home.over_view.mem_percent + "%");
        diskText.setText(home.over_view.hd_usage + "%");
        if (home.over_view.database.equals("1")) {
            dbState.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.normal));
        } else {
            dbState.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.warning));
        }
        if (home.over_view.computer.equals("1")) {
            computerState.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.normal));
        } else {
            computerState.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.warning));
        }


    }

    private void initList(Home home) {
        myLogList.clear();
        List<LogInfo> logList = home.logList;
        for (LogInfo logInfo : logList) {
            if (logInfo.type.equals("0")) {
                MyLog log = new MyLog(R.drawable.message, logInfo.title, logInfo.time, logInfo.info);
                myLogList.add(log);
            } else if (logInfo.type.equals("1")) {
                MyLog log = new MyLog(R.drawable.re, logInfo.title, logInfo.time, logInfo.info);
                myLogList.add(log);
            } else if (logInfo.type.equals("2")) {
                MyLog log = new MyLog(R.drawable.data, logInfo.title, logInfo.time, logInfo.info);
                myLogList.add(log);
            }
        }
        adapter.notifyDataSetChanged();


    }

}
