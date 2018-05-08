package cn.com.larunda.safebox.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.larunda.safebox.R;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.com.larunda.safebox.CollectorTaskDetailActivity;
import cn.com.larunda.safebox.LoginActivity;
import cn.com.larunda.safebox.adapter.CollectorTaskAdapter;
import cn.com.larunda.safebox.gson.BoxInfo;
import cn.com.larunda.safebox.gson.CollectorTaskInfo;
import cn.com.larunda.safebox.recycler.CollectorTask;
import cn.com.larunda.safebox.util.ActivityCollector;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class CollectorFragment extends Fragment {
    private final String URL = Util.URL + "user/tasks" + Util.TOKEN;
    private SharedPreferences preferences;
    private String token;

    private SwipeRefreshLayout refreshLayout;
    private RelativeLayout errorLayout;
    private SwipeMenuRecyclerView recyclerView;
    private LinearLayoutManager manager;
    private CollectorTaskAdapter adapter;
    private List<CollectorTask> collectorTaskList = new ArrayList<>();
    private int page;
    private int maxPage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collector, container, false);
        initView(view);
        initEvent();
        sendRequest();
        return view;
    }

    /**
     * 初始化view
     *
     * @param view
     */
    private void initView(View view) {
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        token = preferences.getString("token", null);

        refreshLayout = view.findViewById(R.id.collector_swipe);
        errorLayout = view.findViewById(R.id.collector_error_layout);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sendRequest();

            }
        });

        recyclerView = view.findViewById(R.id.collector_recycler);
        recyclerView.addItemDecoration(new DefaultItemDecoration(getResources()
                .getColor(R.color.line), MATCH_PARENT, 2));
        adapter = new CollectorTaskAdapter(getContext(), collectorTaskList);
        manager = new LinearLayoutManager(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);

        recyclerView.useDefaultLoadMore(); // 使用默认的加载更多的View。
        recyclerView.setLoadMoreListener(new SwipeMenuRecyclerView.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                sendLoadRequest();
            }
        });
    }

    /**
     * 初始化点击事件
     */
    private void initEvent() {
        adapter.setOnItemClick(new CollectorTaskAdapter.OnItemClick() {
            @Override
            public void onClick(View v, int id) {
                Intent intent = new Intent(getContext(), CollectorTaskDetailActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
    }

    /**
     * 发送网络请求
     */
    private void sendRequest() {
        refreshLayout.setRefreshing(true);
        HttpUtil.sendGetRequestWithHttp(URL + token + "&page=1", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if(getActivity()!=null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            errorLayout.setVisibility(View.VISIBLE);
                            refreshLayout.setRefreshing(false);
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String content = response.body().string();
                int code = response.code();
                if (getActivity() != null) {
                    if (code == 200 && Util.isGoodJson(content)) {
                        final CollectorTaskInfo info = Util.handleCollectorTaskInfo(content);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                parseInfo(info);
                                errorLayout.setVisibility(View.GONE);
                                refreshLayout.setRefreshing(false);
                            }
                        });
                    } else if (code == 401) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                intent.putExtra("token_timeout", "登录超时");
                                preferences.edit().putString("token", null).commit();
                                startActivity(intent);
                                ActivityCollector.finishAllActivity();
                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * 解析服务器返回信息
     *
     * @param info
     */
    private void parseInfo(CollectorTaskInfo info) {
        page = info.getCurrent_page() + 1;
        maxPage = info.getLast_page();
        collectorTaskList.clear();
        if (info.getData() != null) {
            for (CollectorTaskInfo.DataBean dataBean : info.getData()) {
                CollectorTask collectorTask = new CollectorTask();
                collectorTask.setId(dataBean.getId());
                if (dataBean.getBox() != null) {
                    collectorTask.setCode(dataBean.getBox().getF_sn());
                    collectorTask.setName(dataBean.getBox().getF_alias());
                }
                collectorTask.setCreatedTime(dataBean.getCreated_at());
                collectorTask.setCompletedTime(dataBean.getCompleted_at());
                collectorTask.setDestinationCity(dataBean.getF_destination_city());
                collectorTask.setOriginCity(dataBean.getF_origin_city());
                collectorTaskList.add(collectorTask);
            }
        }
        recyclerView.loadMoreFinish(info.getData().size() == 0, maxPage >= page);
        adapter.notifyDataSetChanged();
    }

    /**
     * 发送网络请求
     */
    private void sendLoadRequest() {
        HttpUtil.sendGetRequestWithHttp(URL + token + "&page="+page, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if(getActivity()!=null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            errorLayout.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String content = response.body().string();
                int code = response.code();
                if (getActivity() != null) {
                    if (code == 200 && Util.isGoodJson(content)) {
                        final CollectorTaskInfo info = Util.handleCollectorTaskInfo(content);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                parseLoadInfo(info);
                            }
                        });
                    } else if (code == 401) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                intent.putExtra("token_timeout", "登录超时");
                                preferences.edit().putString("token", null).commit();
                                startActivity(intent);
                                ActivityCollector.finishAllActivity();
                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * 解析服务器返回信息
     *
     * @param info
     */
    private void parseLoadInfo(CollectorTaskInfo info) {
        page = info.getCurrent_page() + 1;
        maxPage = info.getLast_page();
        if (info.getData() != null) {
            for (CollectorTaskInfo.DataBean dataBean : info.getData()) {
                CollectorTask collectorTask = new CollectorTask();
                collectorTask.setId(dataBean.getId());
                if (dataBean.getBox() != null) {
                    collectorTask.setCode(dataBean.getBox().getF_sn());
                    collectorTask.setName(dataBean.getBox().getF_alias());
                }
                collectorTask.setCreatedTime(dataBean.getCreated_at());
                collectorTask.setCompletedTime(dataBean.getCompleted_at());
                collectorTask.setDestinationCity(dataBean.getF_destination_city());
                collectorTask.setOriginCity(dataBean.getF_origin_city());
                collectorTaskList.add(collectorTask);
            }
        }
        recyclerView.loadMoreFinish(info.getData().size() == 0, maxPage >= page);
        adapter.notifyDataSetChanged();
    }
}
