package com.example.box.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.box.R;
import com.example.box.adapter.LogFragmentAdapter;
import com.example.box.adapter.SqFragmentAdapter;
import com.example.box.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sddt on 18-1-16.
 */

public class TotalLogFragment extends Fragment {


    List<String> titleList = new ArrayList<>();
    List<Fragment> fragmentList = new ArrayList<>();
    TabLayout tabLayout;
    ViewPager viewPager;
    LogFragmentAdapter fragmentAdapter;
    private View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /**
         * 保存子布局
         */
        if(container.getTag(R.id.tag_second) == null){
            view = inflater.inflate(R.layout.total_log_fragment,container,false);
            initView(view);
            container.setTag(R.id.tag_second,view);
        }else {
            view = (View) container.getTag(R.id.tag_second);
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    /**
     * 初始化控件
     * @param view
     */
    private void initView(View view) {

        titleList.add("用户日志");
        titleList.add("箱体日志");
        titleList.add("应用程序日志");
        fragmentList.add(new UserLogFragment());
        fragmentList.add(new BoxLogFragment());
        fragmentList.add(new AppLogFragment());

        tabLayout = view.findViewById(R.id.total_log_list_tabLayout);
        viewPager = view.findViewById(R.id.total_log_list_viewPager);

        fragmentAdapter = new LogFragmentAdapter(getChildFragmentManager(),fragmentList,titleList);
        viewPager.setAdapter(fragmentAdapter);
        tabLayout.setupWithViewPager(viewPager);
        //设置tabLayout下划线长度
        tabLayout.post(new Runnable() {
            @Override
            public void run() {

                Util.setIndicator(tabLayout,20,20);
            }
        });


    }
}
