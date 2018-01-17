package com.example.box;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.box.adapter.HomeAdapter;
import com.example.box.adapter.UserInfoAdapter;
import com.example.box.fragment.HomeFragment;
import com.example.box.fragment.DListFragment;
import com.example.box.fragment.SListFragment;
import com.example.box.fragment.TotalLogFragment;
import com.example.box.gson.MenuUserInfo;
import com.example.box.recycler.UserInfo;
import com.example.box.util.HttpUtil;
import com.example.box.util.Util;
import com.example.titlebar.TitleBar;
import com.example.titlebar.TitleListener;

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

    private ViewPager viewPager;
    private TabLayout tabLayout;

    private CircleImageView menu_user_img;
    private TextView menu_user_id;
    private TextView menu_user_name;
    private TextView menu_user_tell;
    private TitleBar titleBar;

    private DrawerLayout drawerLayout;

    private LinearLayout user_info;
    private LinearLayout enclosure;
    private LinearLayout leaving_alram;
    private LinearLayout sound;


    public static final String MENU_URI = "http://safebox.dsmcase.com:90/api/app/user_info?_token=";
    public static final String IMG_URI = "http://safebox.dsmcase.com:90";

    private String[] titles = {"总览", "递送箱列表", "远程授权", "日志"};
    private int[] icons = {R.drawable.sy1, R.drawable.dsx1, R.drawable.sq1, R.drawable.rz1};
    private List<Fragment> fragments = new ArrayList<>();
    private HomeAdapter adapter;
    public static String token = "";
    //是否退出程序的标志位
    private boolean isExit = false;
    private MenuUserInfo menuUserInfo;

    public static SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //获取当前的token
        getToken();
        initView();

        adapter = new HomeAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        initTabs();

        //请求服务器菜单数据
        queryMenuInfo();

        setMenuClick();


    }

    /**
     * 菜单页面的点击事件
     */

    private void setMenuClick() {

        user_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
                startActivity(intent);

            }
        });
        enclosure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EnclosureActivity.class);
                startActivity(intent);
            }
        });

        leaving_alram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LeavingAlarmActivity.class);
                startActivity(intent);
            }
        });

        sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SoundActivity.class);
                startActivity(intent);
            }
        });

    }

    /**
     * 查询服务器信息
     */
    private void queryMenuInfo() {

        HttpUtil.sendGetRequestWithHttp(MENU_URI + token, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                menuUserInfo = Util.handleMenuUserInfo(response.body().string());
                if (menuUserInfo != null && menuUserInfo.error == null) {
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

    /**
     * 显示menu页面信息
     *
     * @param menuUserInfo
     */

    private void showMenuInfo(MenuUserInfo menuUserInfo) {

        menu_user_id.setText(menuUserInfo.userId);
        menu_user_name.setText(menuUserInfo.userName);
        menu_user_tell.setText(menuUserInfo.userTell);
        String img_uri = menuUserInfo.userImg.replace('\\', ' ');
        Glide.with(this).load(IMG_URI + img_uri).into(menu_user_img);

        if (menuUserInfo.level.equals("2")) {
            user_info.setVisibility(View.GONE);
        }

    }


    /**
     * 按键按下监听事件
     *
     * @param keyCode
     * @param event
     * @return
     */

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
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
        if (isExit == false) {
            isExit = true;
            Toast.makeText(this, "再次点击退出程序", Toast.LENGTH_SHORT).show();
            //俩秒后没有点击 取消退出
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;//取消退出
                }
            }, 2000);


        } else {
            //退出程序
            finish();
            System.exit(0);
        }


    }

    private void getToken() {
        token = preferences.getString("token", null);


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
                        setTitleInfo(0);
                        break;
                    case 1:
                        tab.getCustomView().findViewById(R.id.tab_image_view).setBackgroundResource(R.drawable.dsx2);
                        setTitleInfo(1);
                        break;
                    case 2:
                        tab.getCustomView().findViewById(R.id.tab_image_view).setBackgroundResource(R.drawable.sq2);
                        setTitleInfo(2);
                        break;
                    case 3:
                        tab.getCustomView().findViewById(R.id.tab_image_view).setBackgroundResource(R.drawable.rz2);
                        setTitleInfo(3);
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

    /**
     * 根据tablayout返回值重置titlebar信息
     *
     * @param i
     */
    private void setTitleInfo(int i) {
        switch (i) {
            case 0:
                titleBar.setRightButtonSrc(R.drawable.menu);
                titleBar.setTextViewText("");
                titleBar.setRightButtonSrc(0);
                titleBar.setOnClickListener(new TitleListener() {

                    @Override
                    public void onLeftButtonClickListener(View v) {
                        drawerLayout.openDrawer(Gravity.START);

                    }

                    @Override
                    public void onLeftBackButtonClickListener(View v) {

                    }

                    @Override
                    public void onRightButtonClickListener(View v) {

                    }
                });
                break;
            case 1:
                titleBar.setRightButtonSrc(R.drawable.menu);
                titleBar.setTextViewText("递送箱列表");
                titleBar.setRightButtonSrc(R.drawable.add);
                titleBar.setOnClickListener(new TitleListener() {
                    @Override
                    public void onLeftButtonClickListener(View v) {
                        drawerLayout.openDrawer(Gravity.START);

                    }

                    @Override
                    public void onLeftBackButtonClickListener(View v) {

                    }

                    @Override
                    public void onRightButtonClickListener(View v) {

                        Toast.makeText(MainActivity.this, "点击了递送箱列表添加", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case 2:
                titleBar.setRightButtonSrc(R.drawable.menu);
                titleBar.setTextViewText("授权处理");
                titleBar.setRightButtonSrc(R.drawable.add);
                titleBar.setOnClickListener(new TitleListener() {
                    @Override
                    public void onLeftButtonClickListener(View v) {
                        drawerLayout.openDrawer(Gravity.START);


                    }

                    @Override
                    public void onLeftBackButtonClickListener(View v) {

                    }

                    @Override
                    public void onRightButtonClickListener(View v) {

                        Toast.makeText(MainActivity.this, "点击了授权处理添加", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case 3:
                titleBar.setRightButtonSrc(R.drawable.menu);
                titleBar.setTextViewText("日志列表");
                titleBar.setRightButtonSrc(R.drawable.add);
                titleBar.setOnClickListener(new TitleListener() {
                    @Override
                    public void onLeftButtonClickListener(View v) {
                        drawerLayout.openDrawer(Gravity.START);

                    }

                    @Override
                    public void onLeftBackButtonClickListener(View v) {

                    }

                    @Override
                    public void onRightButtonClickListener(View v) {
                        Toast.makeText(MainActivity.this, "点击了日志列表添加", Toast.LENGTH_SHORT).show();

                    }
                });
                break;
            default:
                break;


        }


    }


    //初始化控件
    private void initView() {

        user_info = findViewById(R.id.user_info_layout);
        enclosure = findViewById(R.id.enclosure_layout);
        leaving_alram = findViewById(R.id.leaving_alarm_layout);
        sound = findViewById(R.id.sound_layout);

        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        menu_user_img = findViewById(R.id.menu_user_img);
        menu_user_id = findViewById(R.id.menu_user_id);
        menu_user_name = findViewById(R.id.menu_user_name);
        menu_user_tell = findViewById(R.id.menu_user_tell);
        titleBar = findViewById(R.id.title_bar);
        drawerLayout = findViewById(R.id.drawer_layout);


        fragments.add(new HomeFragment());
        fragments.add(new DListFragment());
        fragments.add(new SListFragment());
        fragments.add(new TotalLogFragment());
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
            //初始化tab时同时初始化title
            titleBar.setRightButtonSrc(R.drawable.menu);
            titleBar.setTextViewText("");
            titleBar.setRightButtonSrc(0);
            titleBar.setOnClickListener(new TitleListener() {
                @Override
                public void onLeftButtonClickListener(View v) {
                    drawerLayout.openDrawer(Gravity.START);

                }

                @Override
                public void onLeftBackButtonClickListener(View v) {

                }

                @Override
                public void onRightButtonClickListener(View v) {

                }
            });

        }
        return view;

    }

}
