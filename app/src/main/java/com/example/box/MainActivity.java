package com.example.box;


import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.box.adapter.HomeAdapter;
import com.example.box.fragment.HomeFragment;
import com.example.box.fragment.ListFragment;
import com.example.box.fragment.ListFragment2;
import com.example.box.fragment.ListFragment3;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private String[] titles = {"总览", "递送箱列表","远程授权","日志"};
    private int[] icons = {R.drawable.sy1,R.drawable.dsx1,R.drawable.sq1,R.drawable.rz1};
    private List<Fragment> fragments = new ArrayList<>();
    private HomeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initView();
        adapter = new HomeAdapter(getSupportFragmentManager(),fragments);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        initTabs();


    }

    private void initTabs() {
        for (int i = 0; i <tabLayout.getTabCount() ; i++) {
            tabLayout.getTabAt(i).setCustomView(getView(i));
        }

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getPosition()){
                    case 0:
                        tab.getCustomView().findViewById(R.id.tab_image_view).setBackgroundResource(R.drawable.sy2);
                        break;
                    case 1:
                        tab.getCustomView().findViewById(R.id.tab_image_view).setBackgroundResource(R.drawable.dsx2);
                        break;
                    case 2:
                        tab.getCustomView().findViewById(R.id.tab_image_view).setBackgroundResource(R.drawable.sq2);
                        break;
                    case 3:
                        tab.getCustomView().findViewById(R.id.tab_image_view).setBackgroundResource(R.drawable.rz2);
                        break;
                    default:
                        break;
                }
               TextView textView =  tab.getCustomView().findViewById(R.id.tab_text_view);
                textView.setTextColor(getResources().getColor(R.color.normal));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {


                switch (tab.getPosition()){
                    case 0:
                        tab.getCustomView().findViewById(R.id.tab_image_view).setBackgroundResource(R.drawable.sy1);
                        break;
                    case 1:
                        tab.getCustomView().findViewById(R.id.tab_image_view).setBackgroundResource(R.drawable.dsx1);
                        break;
                    case 2:
                        tab.getCustomView().findViewById(R.id.tab_image_view).setBackgroundResource(R.drawable.sq1);
                        break;
                    case 3:
                        tab.getCustomView().findViewById(R.id.tab_image_view).setBackgroundResource(R.drawable.rz1);
                        break;
                    default:
                        break;
                }
                TextView textView =  tab.getCustomView().findViewById(R.id.tab_text_view);
                textView.setTextColor(getResources().getColor(R.color.ah));

            }




            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



    }

    private void initView() {

        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        fragments.add(new HomeFragment());
        fragments.add(new ListFragment());
        fragments.add(new ListFragment2());
        fragments.add(new ListFragment3());
    }




    public View getView(int position){
        View view = LayoutInflater.from(this).inflate(R.layout.tab_list,null);
        ImageView imageView = view.findViewById(R.id.tab_image_view);
        TextView textView = view.findViewById(R.id.tab_text_view);
        imageView.setBackgroundResource(icons[position]);
        textView.setText(titles[position]);
        if(position==0){
            imageView.setBackgroundResource(R.drawable.sy2);
            textView.setTextColor(getResources().getColor(R.color.normal));
        }
        return view;

    }

}
