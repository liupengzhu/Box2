package cn.com.larunda.safebox.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.larunda.safebox.R;
import com.larunda.selfdialog.ChooseDialog;
import com.larunda.selfdialog.ConfirmDialog;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMoveListener;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;

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
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static cn.com.larunda.safebox.CompanyActivity.ADD_REQUEST;

public class BoxListFragment extends Fragment {

    private final String URL = Util.URL + "box" + Util.TOKEN;

    private SharedPreferences preferences;
    private String token;
    private int page;
    private int maxPage;

    private SwipeRefreshLayout refreshLayout;
    private RelativeLayout errorLayout;
    private SwipeMenuRecyclerView recyclerView;
    private List<Box> boxList = new ArrayList<>();
    private LinearLayoutManager manager;
    private BoxAdapter adapter;
    private ConfirmDialog dialog;

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

        refreshLayout = view.findViewById(R.id.fragment_box_list_swipe);
        errorLayout = view.findViewById(R.id.fragment_box_list_error_layout);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sendRequest();

            }
        });
        recyclerView = view.findViewById(R.id.fragment_box_list_recycler);
        recyclerView.addItemDecoration(new DefaultItemDecoration(getResources().getColor(R.color.line), MATCH_PARENT, 2));
        // 创建菜单：
        SwipeMenuCreator mSwipeMenuCreator = new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int viewType) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(getContext()); // 各种文字和图标属性设置。
                deleteItem.setBackground(R.color.log_wdu);
                deleteItem.setText("删除");
                deleteItem.setWidth(250);
                deleteItem.setTextColor(Color.WHITE);
                deleteItem.setHeight(MATCH_PARENT);
                rightMenu.addMenuItem(deleteItem); // 在Item右侧添加一个菜单。
            }
        };
        // 设置监听器。
        recyclerView.setSwipeMenuCreator(mSwipeMenuCreator);
        adapter = new BoxAdapter(getContext(), boxList);
        manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);

        // 菜单点击监听。
        recyclerView.setSwipeMenuItemClickListener(new SwipeMenuItemClickListener() {
            @Override
            public void onItemClick(SwipeMenuBridge menuBridge) {
                // 任何操作必须先关闭菜单，否则可能出现Item菜单打开状态错乱。
                menuBridge.closeMenu();
                int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
                int id = boxList.get(adapterPosition).getId();
                String name = boxList.get(adapterPosition).getName();
                showDialog(id, name);
            }
        });

        recyclerView.setAdapter(adapter);

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
        refreshLayout.setRefreshing(true);
        HttpUtil.sendGetRequestWithHttp(URL + token + "&page=1", new Callback() {
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
                if (getActivity() != null) {
                    if (code == 200 && Util.isGoodJson(content)) {
                        final BoxInfo info = Util.handleBoxInfo(content);
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
        recyclerView.loadMoreFinish(info.getData().size() == 0, maxPage >= page);
        adapter.notifyDataSetChanged();
    }

    /**
     * 发送网络请求
     */
    private void sendLoadRequest() {
        HttpUtil.sendGetRequestWithHttp(URL + token + "&page=" + page, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (getActivity() != null) {
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
                        final BoxInfo info = Util.handleBoxInfo(content);
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
     * 解析服务器返回数据
     *
     * @param info
     */
    private void parseLoadInfo(BoxInfo info) {
        page = info.getCurrent_page() + 1;
        maxPage = info.getLast_page();
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
        recyclerView.loadMoreFinish(info.getData().size() == 0, maxPage >= page);
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
        refreshLayout.setRefreshing(true);
        HttpUtil.sendDeleteWithHttp(Util.URL + "box/" + id + Util.TOKEN + token, jsonObject.toString(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshLayout.setRefreshing(false);
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
                                    refreshLayout.setRefreshing(false);
                                    Toast.makeText(getContext(), "删除失败！", Toast.LENGTH_SHORT).show();
                                } else {
                                    sendRequest();
                                }

                            } else if (code == 401 || code == 412) {
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
                                refreshLayout.setRefreshing(false);
                            } else {
                                Toast.makeText(getContext(), "删除失败！", Toast.LENGTH_SHORT).show();
                                refreshLayout.setRefreshing(false);
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 显示弹窗
     *
     * @param id
     * @param name
     */
    private void showDialog(final int id, String name) {
        dialog = new ConfirmDialog(getContext());
        dialog.setContentText("此操作将永久删除递送箱：" + name);
        dialog.setNoOnclickListener(new ConfirmDialog.onNoOnclickListener() {
            @Override
            public void onNoClick(View v) {
                dialog.cancel();
            }
        });
        dialog.setYesOnclickListener(new ConfirmDialog.onYesOnclickListener() {
            @Override
            public void onYesClick(View v) {
                sendDeleteRequest(id);
                dialog.cancel();
            }
        });
        dialog.show();
    }
}
