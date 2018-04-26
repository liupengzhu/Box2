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
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.larunda.safebox.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.com.larunda.safebox.AddBoxActivity;
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

import static android.app.Activity.RESULT_OK;
import static cn.com.larunda.safebox.CompanyActivity.ADD_REQUEST;

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
        //为RecycleView绑定触摸事件
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                //首先回调的方法 返回int表示是否监听该方向
                int dragFlags = 0;// ItemTouchHelper.UP|ItemTouchHelper.DOWN;//拖拽
                int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;//侧滑删除
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //滑动事件
                Collections.swap(boxList, viewHolder.getAdapterPosition(), target.getAdapterPosition());
                adapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                sendDeleteRequest(boxList.get(viewHolder.getAdapterPosition()).getId());
                //侧滑事件
                //adapter.notifyItemRangeChanged(viewHolder.getAdapterPosition(), boxList.size());
                boxList.remove(viewHolder.getAdapterPosition());
                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                adapter.notifyDataSetChanged();

            }

            @Override
            public boolean isLongPressDragEnabled() {
                //是否可拖拽
                return true;
            }
        });
        helper.attachToRecyclerView(recyclerView);


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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ADD_REQUEST:
                if (resultCode == RESULT_OK) {
                    sendRequest();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 发送删除请求
     *
     * @param id
     */
    private void sendDeleteRequest(int id) {
        JsonObject jsonObject = new JsonObject();
        HttpUtil.sendDeleteWithHttp(Util.URL + "box/" + id + Util.TOKEN + token, jsonObject.toString(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "网络异常!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String content = response.body().string();
                final int code = response.code();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (code == 200) {
                                if (content.equals("false")) {
                                    Toast.makeText(getContext(), "删除失败！", Toast.LENGTH_SHORT).show();
                                }

                            } else if (code == 401) {
                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                intent.putExtra("token_timeout", "登录超时");
                                preferences.edit().putString("token", null).commit();
                                startActivity(intent);
                                ActivityCollector.finishAllActivity();
                            } else if (code == 422) {
                                try {
                                    JSONObject js = new JSONObject(content);
                                    Toast.makeText(getContext(), js.get("message") + "", Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(getContext(), "删除失败！", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
