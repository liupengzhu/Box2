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
import android.widget.TextView;
import android.widget.Toast;

import com.larunda.safebox.R;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.com.larunda.safebox.CollectorTaskDetailActivity;
import cn.com.larunda.safebox.LoginActivity;
import cn.com.larunda.safebox.TrackActivity;
import cn.com.larunda.safebox.UserInfoActivity;
import cn.com.larunda.safebox.adapter.CourierDestinationAdapter;
import cn.com.larunda.safebox.gson.CollectorTaskDetailInfo;
import cn.com.larunda.safebox.gson.CourierInfo;
import cn.com.larunda.safebox.recycler.Destination;
import cn.com.larunda.safebox.util.ActivityCollector;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class CourierFragment extends Fragment implements View.OnClickListener {
    private final String URL = Util.URL + "user/delivery" + Util.TOKEN;
    private SharedPreferences preferences;
    private String token;

    private TextView name;
    private TextView startTime;
    private TextView endTime;

    private SwipeRefreshLayout refreshLayout;
    private RelativeLayout errorLayout;
    private SwipeMenuRecyclerView recyclerView;
    private CourierDestinationAdapter adapter;
    private LinearLayoutManager manager;
    private List<Destination> destinationList = new ArrayList<>();

    private RelativeLayout trackButton;
    private int id;
    private View headView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_courier, container, false);
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

        headView = LayoutInflater.from(getContext()).inflate(R.layout.head_courier, null);

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        token = preferences.getString("token", null);

        name = headView.findViewById(R.id.courier_name);
        startTime = headView.findViewById(R.id.courier_start_time);
        endTime = headView.findViewById(R.id.courier_end_time);
        trackButton = headView.findViewById(R.id.courier_track);

        refreshLayout = view.findViewById(R.id.courier_swipe);
        errorLayout = view.findViewById(R.id.courier_error_layout);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sendRequest();

            }
        });

        recyclerView = view.findViewById(R.id.courier_recycler);
        recyclerView.addItemDecoration(new DefaultItemDecoration(getResources().getColor(R.color.line), MATCH_PARENT, 2));
        manager = new LinearLayoutManager(getContext());
        adapter = new CourierDestinationAdapter(getContext(), destinationList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);

        recyclerView.useDefaultLoadMore(); // 使用默认的加载更多的View。
        recyclerView.addHeaderView(headView);
    }

    /**
     * 初始化点击事件
     */
    private void initEvent() {
        trackButton.setOnClickListener(this);
    }

    /**
     * 发送网络请求
     */
    private void sendRequest() {
        refreshLayout.setRefreshing(true);
        HttpUtil.sendGetRequestWithHttp(URL + token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (getActivity() != null) {
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
                if (code == 200 && Util.isGoodJson(content)) {
                    final CourierInfo info = Util.handleCourierInfo(content);
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                parseInfo(info);
                                errorLayout.setVisibility(View.GONE);
                                refreshLayout.setRefreshing(false);
                            }
                        });
                    }
                } else if (code == 401 || code == 412) {
                    if (getActivity() != null) {
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
                    }
                } else if (code == 422) {
                    if (getActivity() != null) {
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
    private void parseInfo(CourierInfo info) {
        destinationList.clear();
        StringBuffer users = new StringBuffer();
        StringBuffer destinationCity = new StringBuffer();
        StringBuffer originCity = new StringBuffer();
        if (info != null) {
            name.setText(info.getF_name() != null ? info.getF_name() : "");
            startTime.setText(info.getCreated_at() != null ? info.getCreated_at() : "");
            endTime.setText(info.getCompleted_at() != null ? info.getCompleted_at() : "");
            id = info.getId();
            if (info.getProcesses() != null) {
                for (CourierInfo.ProcessesBean processesBean : info.getProcesses()) {
                    Destination destination = new Destination();
                    users.setLength(0);
                    destinationCity.setLength(0);
                    originCity.setLength(0);
                    for (int i = 0; i < processesBean.getAddressee().size(); i++) {
                        users.append(processesBean.getAddressee().get(i).getUser() != null ?
                                processesBean.getAddressee().get(i).getUser().getF_name() + " " : "");
                    }
                    for (int i = 0; i < processesBean.getF_origin_city().size(); i++) {
                        originCity.append(processesBean.getF_origin_city().get(i) + " ");
                    }
                    for (int i = 0; i < processesBean.getF_destination_city().size(); i++) {
                        destinationCity.append(processesBean.getF_destination_city().get(i) + " ");
                    }
                    destination.setDestinationCity(destinationCity.toString());
                    destination.setOriginCity(originCity.toString());
                    destination.setPerson(users.toString());
                    destination.setStartTime(processesBean.getCreated_at() != null ? processesBean.getCreated_at() :
                            null);
                    destination.setEndTime(processesBean.getCompleted_at() != null ? processesBean.getCompleted_at() :
                            null);
                    destination.setId(processesBean.getId());
                    destinationList.add(destination);
                }
            }
        }
        recyclerView.loadMoreFinish(info.getProcesses().size() == 0, false);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.courier_track:
                Intent intent = new Intent(getContext(), TrackActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("type", "user/");
                startActivity(intent);
                break;
        }
    }
}
