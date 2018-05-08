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
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.larunda.safebox.R;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.com.larunda.safebox.LoginActivity;
import cn.com.larunda.safebox.adapter.SystemLogAdapter;
import cn.com.larunda.safebox.gson.SystemLogInfo;
import cn.com.larunda.safebox.recycler.SystemLog;
import cn.com.larunda.safebox.util.ActivityCollector;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.LogType;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class SystemLogFragment extends Fragment {
    private final String URL = Util.URL + "log" + Util.TOKEN;

    private SwipeRefreshLayout refreshLayout;
    private RelativeLayout errorLayout;
    private SwipeMenuRecyclerView recyclerView;
    private SystemLogAdapter adapter;
    private LinearLayoutManager manager;
    private List<SystemLog> systemLogList = new ArrayList<>();
    private SharedPreferences preferences;
    private String token;
    private int page;
    private int maxPage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_system_log, container, false);
        initView(view);
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

        refreshLayout = view.findViewById(R.id.system_log_swipe);
        errorLayout = view.findViewById(R.id.system_log_error_layout);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sendRequest();

            }
        });
        recyclerView = view.findViewById(R.id.system_log_recycler);
        recyclerView.addItemDecoration(new DefaultItemDecoration(getResources().getColor(R.color.line), MATCH_PARENT, 2));
        adapter = new SystemLogAdapter(getContext(), systemLogList);
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
                final String content = response.body().string();
                int code = response.code();
                if (getActivity() != null) {
                    if (code == 200 && Util.isGoodJson(content)) {
                        final SystemLogInfo info = Util.handleSystemLogInfo(content);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                parseInfo(info);
                                errorLayout.setVisibility(View.GONE);
                                refreshLayout.setRefreshing(false);
                            }
                        });
                    } else if (code == 401 || code == 412) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                intent.putExtra("token_timeout", "登录超时");
                                preferences.edit().putString("token", null).commit();
                                startActivity(intent);
                                ActivityCollector.finishAllActivity();
                            }
                        });
                    } else if (code == 422) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject js = new JSONObject(content);
                                    Toast.makeText(getContext(), js.get("message") + "", Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                refreshLayout.setRefreshing(false);
                            }
                        });

                    }
                }
            }
        });
    }

    /**
     * 解析服务器返回数据
     *
     * @param info
     */
    private void parseInfo(SystemLogInfo info) {
        page = info.getCurrent_page() + 1;
        maxPage = info.getLast_page();
        systemLogList.clear();
        if (info != null) {
            for (SystemLogInfo.DataBean dataBean : info.getData()) {
                SystemLog systemLog = new SystemLog();
                StringBuffer content = new StringBuffer();
                if (dataBean.getF_info() != null) {
                    content.append(dataBean.getF_info());
                }
                if (dataBean.getBox() != null) {
                    content.append("，箱体名称：" + dataBean.getBox().getF_alias());
                }
                systemLog.setContent(content.toString());
                systemLog.setTitle(dataBean.getF_title());
                systemLog.setTime(dataBean.getCreated_at());
                systemLog.setType(LogType.getName(dataBean.getF_type()));
                if (dataBean.getUser() != null) {
                    systemLog.setUer(dataBean.getUser().getF_name());
                }

                systemLogList.add(systemLog);
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
                final String content = response.body().string();
                int code = response.code();
                if (getActivity() != null) {
                    if (code == 200 && Util.isGoodJson(content)) {
                        final SystemLogInfo info = Util.handleSystemLogInfo(content);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                parseLoadInfo(info);
                            }
                        });
                    } else if (code == 401 || code == 412) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                intent.putExtra("token_timeout", "登录超时");
                                preferences.edit().putString("token", null).commit();
                                startActivity(intent);
                                ActivityCollector.finishAllActivity();
                            }
                        });
                    } else if (code == 422) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject js = new JSONObject(content);
                                    Toast.makeText(getContext(), js.get("message") + "", Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    }
                }
            }
        });
    }

    /**
     * 解析服务器返回数据
     *
     * @param info
     */
    private void parseLoadInfo(SystemLogInfo info) {
        page = info.getCurrent_page() + 1;
        maxPage = info.getLast_page();
        if (info != null) {
            for (SystemLogInfo.DataBean dataBean : info.getData()) {
                SystemLog systemLog = new SystemLog();
                StringBuffer content = new StringBuffer();
                if (dataBean.getF_info() != null) {
                    content.append(dataBean.getF_info());
                }
                if (dataBean.getBox() != null) {
                    content.append("，箱体名称：" + dataBean.getBox().getF_alias());
                }
                systemLog.setContent(content.toString());
                systemLog.setTitle(dataBean.getF_title());
                systemLog.setTime(dataBean.getCreated_at());
                systemLog.setType(LogType.getName(dataBean.getF_type()));
                if (dataBean.getUser() != null) {
                    systemLog.setUer(dataBean.getUser().getF_name());
                }

                systemLogList.add(systemLog);
            }
        }
        recyclerView.loadMoreFinish(info.getData().size() == 0, maxPage >= page);
        adapter.notifyDataSetChanged();
    }
}
