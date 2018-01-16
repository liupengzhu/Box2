package com.example.box.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.box.MyApplication;
import com.example.box.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/9.
 */

public class HomeAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragmentList = new ArrayList<>();


    public HomeAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
    }


    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }


}
