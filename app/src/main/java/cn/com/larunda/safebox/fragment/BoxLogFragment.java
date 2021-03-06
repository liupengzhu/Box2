package cn.com.larunda.safebox.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.Toast;

import cn.com.larunda.safebox.LoginActivity;
import cn.com.larunda.safebox.MainActivity;

import com.larunda.safebox.R;

import cn.com.larunda.safebox.adapter.BoxLogAdapter;
import cn.com.larunda.safebox.adapter.FootAdapter;
import cn.com.larunda.safebox.gson.TotalLogData;
import cn.com.larunda.safebox.gson.TotalLogInfo;
import cn.com.larunda.safebox.recycler.BoxLog;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by sddt on 18-1-16.
 */

public class BoxLogFragment extends Fragment {

    List<BoxLog> boxLogList = new ArrayList<>();
    RecyclerView recyclerView;
    BoxLogAdapter adapter;
    LinearLayoutManager manager;
    SwipeRefreshLayout swipeRefreshLayout;

    private RelativeLayout loodingErrorLayout;
    private ImageView loodingLayout;

    public static final String SQLS_URI = Util.URL + "log" + Util.TOKEN;
    public static final String TYPE = "&type=1";
    private int page;
    private int lastVisibleItem;
    private int count;
    private FootAdapter footAdapter;
    private int total;
    private boolean isInit = false;
    private SharedPreferences preferences;
    private String token;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.box_log_fragment, container, false);
        initView(view);
        isInit = true;
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

    }

    /**
     * 发送网络请求并解析数据
     */
    private void sendRequest() {

        swipeRefreshLayout.setRefreshing(true);
        HttpUtil.sendGetRequestWithHttp(SQLS_URI + MainActivity.token + TYPE + "&page=1" + Util.TYPE, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                            loodingErrorLayout.setVisibility(View.VISIBLE);
                            loodingLayout.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.GONE);
                        }
                    });
                }

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String content = response.body().string();
                if (Util.isGoodJson(content)) {
                    final TotalLogInfo totalLogInfo = Util.handleTotalLogInfo(content);
                    if (getActivity() != null) {
                        if (totalLogInfo != null && totalLogInfo.error == null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showInfo(totalLogInfo);
                                    preferences.edit().putString("boxLogInfo", content).commit();
                                    swipeRefreshLayout.setRefreshing(false);
                                    loodingErrorLayout.setVisibility(View.GONE);
                                    loodingLayout.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);
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


                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                swipeRefreshLayout.setRefreshing(false);
                                loodingErrorLayout.setVisibility(View.VISIBLE);
                                loodingLayout.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.GONE);
                            }
                        });
                    }
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
        page = totalLogInfo.current_page + 1;
        count = totalLogInfo.per_page;
        total = totalLogInfo.last_page;
        if (totalLogInfo.totalLogData.size() == 0 || totalLogInfo.totalLogData.size() < count) {
            footAdapter.setHasMore(false);
        }
        boxLogList.clear();
        for (TotalLogData totalLogData : totalLogInfo.totalLogData) {
            BoxLog boxLog = new BoxLog(totalLogData.created_at, totalLogData.info, totalLogData.title);
            boxLogList.add(boxLog);
        }
        footAdapter.notifyDataSetChanged();

    }

    /**
     * 初始化view
     *
     * @param view
     */
    private void initView(View view) {

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        token = preferences.getString("token", null);

        swipeRefreshLayout = view.findViewById(R.id.box_log_swipe);
        recyclerView = view.findViewById(R.id.box_log_recycler_view);
        loodingErrorLayout = view.findViewById(R.id.box_log_loading_error_layout);
        loodingLayout = view.findViewById(R.id.box_log_loading_layout);
        adapter = new BoxLogAdapter(boxLogList);
        footAdapter = new FootAdapter(getContext(), adapter);
        manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(footAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (page <= total) {
                    //在newState为滑到底部时
                    if (lastVisibleItem + 1 == footAdapter.getItemCount()) {
                        if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                            footAdapter.setHasMore(true);
                            footAdapter.notifyDataSetChanged();
                        }
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            footAdapter.setHasMore(true);
                            sendAddRequest();

                        }
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = manager.findLastVisibleItemPosition();
            }
        });
    }

    /**
     * 请求下一页数据
     */
    private void sendAddRequest() {
        swipeRefreshLayout.setRefreshing(true);
        HttpUtil.sendGetRequestWithHttp(SQLS_URI + MainActivity.token + TYPE + "&page=" + page + Util.TYPE, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        loodingErrorLayout.setVisibility(View.VISIBLE);
                        loodingLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String content = response.body().string();
                if (Util.isGoodJson(content)) {
                    final TotalLogInfo totalLogInfo = Util.handleTotalLogInfo(content);
                    if (totalLogInfo != null && totalLogInfo.error == null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addInfo(totalLogInfo);
                                swipeRefreshLayout.setRefreshing(false);
                                loodingErrorLayout.setVisibility(View.GONE);
                                loodingLayout.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
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
     * 添加数据
     *
     * @param totalLogInfo
     */
    private void addInfo(TotalLogInfo totalLogInfo) {
        page = totalLogInfo.current_page + 1;
        if (totalLogInfo.totalLogData.size() == 0 || totalLogInfo.totalLogData.size() < count) {
            footAdapter.setHasMore(false);
        }
        for (TotalLogData totalLogData : totalLogInfo.totalLogData) {
            BoxLog boxLog = new BoxLog(totalLogData.created_at, totalLogData.info, totalLogData.title);
            boxLogList.add(boxLog);
        }
        footAdapter.notifyDataSetChanged();

    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.scrollToPosition(0);
        String content = preferences.getString("boxLogInfo", null);
        if (content != null) {
            if (Util.isGoodJson(content)) {
                TotalLogInfo totalLogInfo = Util.handleTotalLogInfo(content);
                showInfo(totalLogInfo);
            } else {
                sendRequest();
            }
        } else {
            //每次fragment创建时还没有网络数据 设置载入背景为可见
            loodingLayout.setVisibility(View.VISIBLE);
            loodingErrorLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            sendRequest();
        }

    }

}
