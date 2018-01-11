package com.example.box;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.box.adapter.HomeAdapter;
import com.example.box.fragment.HomeFragment;
import com.example.box.fragment.ListFragment;
import com.example.box.fragment.ListFragment2;
import com.example.box.fragment.ListFragment3;
import com.example.box.gson.MenuUserInfo;
import com.example.box.util.HttpUtil;
import com.example.box.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private CircleImageView menu_user_img;
    private TextView menu_user_id;
    private TextView menu_user_name;
    private TextView menu_user_tell;


    public static final String MENU_URI = "http://safebox.dsmcase.com:90/api/app/user_info?_token=";
    private String[] titles = {"总览", "递送箱列表", "远程授权", "日志"};
    private int[] icons = {R.drawable.sy1, R.drawable.dsx1, R.drawable.sq1, R.drawable.rz1};
    private List<Fragment> fragments = new ArrayList<>();
    private HomeAdapter adapter;
    public static String token = "";
    //是否退出程序的标志位
    private boolean isExit = false;
    private MenuUserInfo menuUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        //获取当前的token
        getToken();
        initView();

        adapter = new HomeAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        initTabs();

        //请求服务器菜单数据
        queryMenuInfo();

    }

    private void queryMenuInfo() {

        HttpUtil.sendGetRequestWithHttp(MENU_URI + token, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                menuUserInfo = Util.handleMenuUserInfo(response.body().string());
                if(menuUserInfo !=null&& menuUserInfo.error==null){
                      runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              //显示用户信息
                              showMenuInfo(menuUserInfo);
                          }
                      });

                }

            }
        });


    }

    private void showMenuInfo(MenuUserInfo menuUserInfo) {
        
        menu_user_id.setText(menuUserInfo.userId);
        menu_user_name.setText(menuUserInfo.userName);
        menu_user_tell.setText(menuUserInfo.userTell);

    }

    /**
     * 按键按下监听事件
     * @param keyCode
     * @param event
     * @return
     */

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            exitByTwoClick();
        }


        return false;
    }

    /**
     * 双击退出方法
     */

    private void exitByTwoClick() {
        Timer timer = null;
        //如果isExit为false 提示再次点击退出
        if(isExit == false){
            isExit = true;
            Toast.makeText(this,"再次点击退出程序",Toast.LENGTH_SHORT).show();
            //俩秒后没有点击 取消退出
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;//取消退出
                }
            },2000);


        }else {
            //退出程序
            finish();
            System.exit(0);
        }


    }

    private void getToken() {
        Intent intent = getIntent();
        token = intent.getStringExtra("token");


    }

    //初始化Tab；
    private void initTabs() {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            tabLayout.getTabAt(i).setCustomView(getView(i));
        }
      //tablayout切换监听事件
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getPosition()) {
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
                TextView textView = tab.getCustomView().findViewById(R.id.tab_text_view);
                textView.setTextColor(getResources().getColor(R.color.normal));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {


                switch (tab.getPosition()) {
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
                TextView textView = tab.getCustomView().findViewById(R.id.tab_text_view);
                textView.setTextColor(getResources().getColor(R.color.ah));

            }


            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }
    //初始化控件
    private void initView() {

        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        menu_user_img = findViewById(R.id.menu_user_img);
        menu_user_id = findViewById(R.id.menu_user_id);
        menu_user_name = findViewById(R.id.menu_user_name);
        menu_user_tell = findViewById(R.id.menu_user_tell);


        fragments.add(new HomeFragment());
        fragments.add(new ListFragment());
        fragments.add(new ListFragment2());
        fragments.add(new ListFragment3());
    }

    //获取position位置的tab的子控件
    public View getView(int position) {
        View view = LayoutInflater.from(this).inflate(R.layout.tab_list, null);
        ImageView imageView = view.findViewById(R.id.tab_image_view);
        TextView textView = view.findViewById(R.id.tab_text_view);
        imageView.setBackgroundResource(icons[position]);
        textView.setText(titles[position]);
        if (position == 0) {
            imageView.setBackgroundResource(R.drawable.sy2);
            textView.setTextColor(getResources().getColor(R.color.normal));
        }
        return view;

    }

}
