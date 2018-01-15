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
import com.example.box.adapter.SqFragmentAdapter;
import com.example.box.util.Util;

import java.util.ArrayList;
import java.util.List;

public class SListFragment extends Fragment {
    List<String> titleList = new ArrayList<>();
    List<Fragment> fragmentList = new ArrayList<>();
    TabLayout tabLayout;
    ViewPager viewPager;
    SqFragmentAdapter fragmentAdapter;
    private View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(container.getTag() == null){
            view = inflater.inflate(R.layout.s_list_fragment,container,false);
            initView(view);
            container.setTag(view);
        }else {
            view = (View) container.getTag();
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

        titleList.add("远程授权");
        titleList.add("授权历史");
        fragmentList.add(new YListFragment());
        fragmentList.add(new LsListFragment());

        tabLayout = view.findViewById(R.id.s_list_tabLayout);
        viewPager = view.findViewById(R.id.s_list_viewPager);

        fragmentAdapter = new SqFragmentAdapter(getChildFragmentManager(),fragmentList,titleList);
        viewPager.setAdapter(fragmentAdapter);
        tabLayout.setupWithViewPager(viewPager);
        //设置tabLayout下划线长度
        tabLayout.post(new Runnable() {
            @Override
            public void run() {

                Util.setIndicator(tabLayout,50,50);
            }
        });


    }
}
