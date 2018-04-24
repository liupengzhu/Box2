package cn.com.larunda.safebox.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.larunda.safebox.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.com.larunda.safebox.LoginActivity;
import cn.com.larunda.safebox.TaskListActivity;
import cn.com.larunda.safebox.adapter.BoxAdapter;
import cn.com.larunda.safebox.gson.BoxInfo;
import cn.com.larunda.safebox.gson.CompanyInfo;
import cn.com.larunda.safebox.recycler.Box;
import cn.com.larunda.safebox.util.ActivityCollector;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class BoxListFragment extends Fragment {

    private final String URL = Util.URL + "box" + Util.TOKEN;

    private SharedPreferences preferences;
    private String token;
    private int page;
    private int maxPage;

    private RecyclerView recyclerView;
    private List<Box> boxList = new ArrayList<>();
    private LinearLayoutManager manager;
    private BoxAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_box_list, container, false);
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

        recyclerView = view.findViewById(R.id.fragment_box_list_recycler);
        adapter = new BoxAdapter(getContext(), boxList);
        manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }

    /**
     * 初始化点击事件
     */
    private void initEvent() {
        adapter.setItemOnClickListener(new BoxAdapter.ItemOnClickListener() {
            @Override
            public void onClick(View v, int id, String status) {
                Intent intent = new Intent(getContext(), TaskListActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("status", status);
                startActivity(intent);
            }
        });
    }

    /**
     * 发送网络请求
     */
    private void sendRequest() {
        HttpUtil.sendGetRequestWithHttp(URL + token + "&page=1", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String content = response.body().string();
                int code = response.code();
                if (getActivity() != null) {
                    if (code == 200 && Util.isGoodJson(content)) {
                        final BoxInfo info = Util.handleBoxInfo(content);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                parseInfo(info);
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
     * 解析服务器返回数据
     *
     * @param info
     */
    private void parseInfo(BoxInfo info) {
        page = info.getCurrent_page() + 1;
        maxPage = info.getLast_page();
        boxList.clear();
        if (info.getData() != null) {
            for (BoxInfo.DataBean dataBean : info.getData()) {
                Box box = new Box();
                box.setCode(dataBean.getF_sn());
                box.setId(dataBean.getId());
                box.setName(dataBean.getF_alias());
                box.setStatus(dataBean.getF_status());
                boxList.add(box);
            }
        }
        adapter.notifyDataSetChanged();
    }
}
