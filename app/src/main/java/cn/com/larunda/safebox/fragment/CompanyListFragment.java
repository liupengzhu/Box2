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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.larunda.recycler.OnLoadListener;
import com.larunda.recycler.PTLLinearLayoutManager;
import com.larunda.recycler.PullToLoadRecyclerView;
import com.larunda.safebox.R;
import com.larunda.selfdialog.ConfirmDialog;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.com.larunda.safebox.AdminRepasswordActivity;
import cn.com.larunda.safebox.EditCompanyActivity;
import cn.com.larunda.safebox.LoginActivity;
import cn.com.larunda.safebox.SuperAdminActivity;
import cn.com.larunda.safebox.UserInfoActivity;
import cn.com.larunda.safebox.adapter.CompanyAdapter;
import cn.com.larunda.safebox.gson.CompanyInfo;
import cn.com.larunda.safebox.recycler.Company;
import cn.com.larunda.safebox.util.ActivityCollector;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CompanyListFragment extends Fragment {

    private final String URL = Util.URL + "company" + Util.TOKEN;
    private SwipeMenuRecyclerView recyclerView;
    private LinearLayoutManager manager;
    private CompanyAdapter adapter;
    private List<Company> companyList = new ArrayList<>();
    private SwipeRefreshLayout refreshLayout;
    private RelativeLayout errorLayout;

    private SharedPreferences preferences;
    private String token;
    private String search = "";
    private int page;
    private int maxPage;
    private ConfirmDialog dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_company_list, container, false);
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

        recyclerView = view.findViewById(R.id.company_list_recycler);
        manager = new LinearLayoutManager(getContext());
        adapter = new CompanyAdapter(getContext(), companyList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);

        refreshLayout = view.findViewById(R.id.company_list_swipe);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                sendRequest();
            }
        });
        errorLayout = view.findViewById(R.id.company_list_error_layout);

        recyclerView.useDefaultLoadMore(); // 使用默认的加载更多的View。
        recyclerView.setLoadMoreListener(new SwipeMenuRecyclerView.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                sendAddRequest();
            }
        });
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        adapter.setLayoutOnclick(new CompanyAdapter.CompanyLayoutOnclick() {
            @Override
            public void onClick(View v, int id) {
                Intent intent = new Intent(getContext(), EditCompanyActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
        adapter.setButtonOnclick(new CompanyAdapter.CompanyButtonOnclick() {
            @Override
            public void onclick(View v, int id) {
                Intent intent = new Intent(getContext(), AdminRepasswordActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        adapter.setDeleteButtonOnclick(new CompanyAdapter.CompanyDeleteButtonOnclick() {
            @Override
            public void onclick(View v, int id, String name) {
                showDialog(id, name);
            }
        });

    }

    /**
     * 发送网络请求
     */
    private void sendRequest() {
        refreshLayout.setRefreshing(true);
        HttpUtil.sendGetRequestWithHttp(URL + token + "&search=" + search + "&page=1", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshLayout.setRefreshing(false);
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
                        final CompanyInfo info = Util.handleCompanyInfo(content);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                parseInfo(info);
                                refreshLayout.setRefreshing(false);
                                errorLayout.setVisibility(View.GONE);
                            }
                        });
                    } else if (code == 401 || code == 412) {
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
     * 解析数据
     *
     * @param info
     */
    private void parseInfo(CompanyInfo info) {
        page = info.getCurrent_page() + 1;
        maxPage = info.getLast_page();
        companyList.clear();
        if (info.getData() != null) {
            for (CompanyInfo.DataBean dataBean : info.getData()) {
                Company company = new Company();
                company.setId(dataBean.getId());
                company.setName(dataBean.getF_name());
                company.setTel(dataBean.getF_tel());
                company.setPic(Util.PATH + dataBean.getF_pic());
                company.setContacts(dataBean.getF_contacts());
                company.setEmail(dataBean.getF_email());
                company.setFax(dataBean.getF_fax());
                company.setAddress(dataBean.getF_add());
                company.setLetter(dataBean.getF_letter());
                company.setSalesAddress(dataBean.getF_sales_add());
                companyList.add(company);
            }
        }
        recyclerView.loadMoreFinish(info.getData().size() == 0, maxPage >= page);
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    /**
     * 发送网络请求
     */
    private void sendAddRequest() {
        HttpUtil.sendGetRequestWithHttp(URL + token + "&search=" + search + "&page=" + page, new Callback() {
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
                String content = response.body().string();
                int code = response.code();
                if (getActivity() != null) {
                    if (code == 200 && Util.isGoodJson(content)) {
                        final CompanyInfo info = Util.handleCompanyInfo(content);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                parseAddInfo(info);

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
     * 解析数据
     *
     * @param info
     */
    private void parseAddInfo(CompanyInfo info) {
        page = info.getCurrent_page() + 1;
        maxPage = info.getLast_page();
        if (info.getData() != null) {
            for (CompanyInfo.DataBean dataBean : info.getData()) {
                Company company = new Company();
                company.setId(dataBean.getId());
                company.setName(dataBean.getF_name());
                company.setTel(dataBean.getF_tel());
                company.setPic(Util.PATH + dataBean.getF_pic());
                company.setContacts(dataBean.getF_contacts());
                company.setEmail(dataBean.getF_email());
                company.setFax(dataBean.getF_fax());
                company.setAddress(dataBean.getF_add());
                company.setLetter(dataBean.getF_letter());
                company.setSalesAddress(dataBean.getF_sales_add());
                companyList.add(company);
            }
        }
        recyclerView.loadMoreFinish(info.getData().size() == 0, maxPage >= page);
        adapter.notifyDataSetChanged();
    }

    /**
     * 显示弹窗
     *
     * @param id
     * @param name
     */
    private void showDialog(final int id, String name) {
        dialog = new ConfirmDialog(getContext());
        dialog.setContentText("此操作将永久删除企业：" + name);
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

    /**
     * 发送删除请求
     *
     * @param id
     */
    private void sendDeleteRequest(int id) {
        refreshLayout.setRefreshing(true);
        JsonObject jsonObject = new JsonObject();
        HttpUtil.sendDeleteWithHttp(Util.URL + "company/" + id
                + Util.TOKEN + token, jsonObject.toString(), new Callback() {
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
}
