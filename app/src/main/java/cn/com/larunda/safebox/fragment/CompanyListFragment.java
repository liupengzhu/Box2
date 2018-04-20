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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.larunda.safebox.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.com.larunda.safebox.AdminRepasswordActivity;
import cn.com.larunda.safebox.EditCompanyActivity;
import cn.com.larunda.safebox.LoginActivity;
import cn.com.larunda.safebox.SuperAdminActivity;
import cn.com.larunda.safebox.adapter.CompanyAdapter;
import cn.com.larunda.safebox.gson.CompanyInfo;
import cn.com.larunda.safebox.recycler.Company;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CompanyListFragment extends Fragment {

    private final String URL = Util.URL + "company" + Util.TOKEN;
    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private CompanyAdapter adapter;
    private List<Company> companyList = new ArrayList<>();

    private SharedPreferences preferences;
    private String token;
    private String search = "";
    private int page;
    private int maxPage;

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

        recyclerView = view.findViewById(R.id.company_list_recycler_view);
        manager = new LinearLayoutManager(getContext());
        adapter = new CompanyAdapter(getContext(), companyList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);
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
    }

    /**
     * 发送网络请求
     */
    private void sendRequest() {
        HttpUtil.sendGetRequestWithHttp(URL + token + "&search=" + search + "&page=1", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String content = response.body().string();
                int code = response.code();
                if (getActivity() != null) {
                    if (code == 200) {
                        final CompanyInfo info = Util.handleCompanyInfo(content);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                parseInfo(info);
                            }
                        });
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                intent.putExtra("token_timeout", "登录超时");
                                preferences.edit().putString("token", null).commit();
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
     * 解析数据
     *
     * @param info
     */
    private void parseInfo(CompanyInfo info) {
        page = info.getCurrent_page();
        maxPage = info.getLast_page();
        companyList.clear();
        if (info.getData() != null) {
            for (CompanyInfo.DataBean dataBean : info.getData()) {
                Company company = new Company();
                company.setId(dataBean.getId());
                company.setName(dataBean.getF_name());
                company.setTel(dataBean.getF_tel());
                if (dataBean.getF_pic() != null) {
                    company.setPic(Util.PATH + dataBean.getF_pic());
                }
                company.setContacts(dataBean.getF_contacts());
                company.setEmail(dataBean.getF_email());
                company.setFax(dataBean.getF_fax());
                company.setLetter(dataBean.getF_letter());
                company.setSalesAddress(dataBean.getF_sales_add());
                companyList.add(company);
            }
        }
        adapter.notifyDataSetChanged();
    }
}
