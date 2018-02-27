package cn.com.larunda.safebox;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.larunda.safebox.R;

import cn.com.larunda.safebox.adapter.HomeAdapter;
import cn.com.larunda.safebox.fragment.HomeFragment;
import cn.com.larunda.safebox.fragment.DListFragment;
import cn.com.larunda.safebox.fragment.NewHomeFragment;
import cn.com.larunda.safebox.fragment.SListFragment;
import cn.com.larunda.safebox.fragment.TotalLogFragment;
import cn.com.larunda.safebox.gson.BoxInfo;
import cn.com.larunda.safebox.gson.MenuUserInfo;
import cn.com.larunda.safebox.service.AutoUpdateService;
import cn.com.larunda.safebox.util.ActivityCollector;
import cn.com.larunda.safebox.util.BaseActivity;
import cn.com.larunda.safebox.util.CustomViewPager;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;

import com.larunda.selfdialog.SelfDialog;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    public static CustomViewPager viewPager;
    public static TabLayout tabLayout;

    private CircleImageView menu_user_img;
    private TextView menu_user_id;
    private TextView menu_user_name;
    private TextView menu_user_tell;
    private TitleBar titleBar;

    public static DrawerLayout drawerLayout;

    private LinearLayout user_info;
    private LinearLayout enclosure;
    private LinearLayout leaving_alarm;
    private LinearLayout sound;
    private LinearLayout setting;
    private LinearLayout ble;

    private RelativeLayout systemSettingButton;


    public static final String MENU_URI = Util.URL + "app/user_info" + Util.TOKEN;
    public static final String IMG_URI = Util.PATH;

    private String[] titles = {"总览", "箱体列表", "日志"};
    private int[] icons = {R.drawable.sy1, R.drawable.dsx1, R.drawable.rz1};
    private List<Fragment> fragments = new ArrayList<>();
    private HomeAdapter adapter;
    public static String token = "";
    //是否退出程序的标志位
    private boolean isExit = false;


    public static SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private RelativeLayout systemBackButton;


    private String id;
    private String img_uri;


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
        editor = preferences.edit();
        //获取当前的token
        getToken();
        initView();

        adapter = new HomeAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        initTabs();
        setMenuClick();
        Intent serviceIntent = new Intent(this, AutoUpdateService.class);
        startService(serviceIntent);
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

        leaving_alarm.setOnClickListener(new View.OnClickListener() {
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

        systemSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SystemSettingActivity.class);
                startActivity(intent);
            }
        });

        systemBackButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                preferences.edit().putString("token", null).commit();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        menu_user_img.setOnClickListener(this);
        setting.setOnClickListener(this);
        menu_user_id.setOnClickListener(this);
        menu_user_name.setOnClickListener(this);
        menu_user_tell.setOnClickListener(this);
        ble.setOnClickListener(this);

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
                final String content = response.body().string();
                if (Util.isGoodJson(content)) {
                    final MenuUserInfo menuUserInfo = Util.handleMenuUserInfo(content);
                    if (menuUserInfo != null && menuUserInfo.error == null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showMenuInfo(menuUserInfo);
                                preferences.edit().putString("menuInfo", content).apply();
                            }
                        });

                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
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
     * 显示menu页面信息
     *
     * @param menuUserInfo
     */

    private void showMenuInfo(MenuUserInfo menuUserInfo) {
        if (menuUserInfo.userId != null) {
            menu_user_id.setText(menuUserInfo.userId);
            editor.putString("user_name", menuUserInfo.userId);
            editor.apply();
        }
        if (menuUserInfo.userName != null) {
            menu_user_name.setText(menuUserInfo.userName);

        }
        if (menuUserInfo.userTell != null) {
            menu_user_tell.setText(menuUserInfo.userTell);
        }
        if (menuUserInfo.userImg != null) {
            img_uri = menuUserInfo.userImg.replace('\\', ' ');
            Glide.with(this).load(IMG_URI + img_uri).placeholder(R.drawable.user)
                    .dontAnimate()
                    .error(R.mipmap.user_img)
                    .into(menu_user_img);

        }
        id = menuUserInfo.id;
        editor.putString("user_id", id);
        editor.apply();
        if (menuUserInfo.level.equals("2")) {
            user_info.setVisibility(View.GONE);
        }


    }

    @Override
    public void onBackPressed() {
        //判断递送箱列表是否是多选状态
        if (DListFragment.isLongClick) {
            DListFragment.cancleLongClick();
        } else {
            exitByTwoClick();
        }
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
                tabLayout.setVisibility(View.VISIBLE);
                titleBar.setVisibility(View.GONE);
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
                if (DListFragment.isLongClick) {
                    tabLayout.setVisibility(View.GONE);
                } else {
                    tabLayout.setVisibility(View.VISIBLE);
                }
                titleBar.setVisibility(View.VISIBLE);
                titleBar.setRightButtonSrc(R.drawable.menu);
                titleBar.setTextViewText("箱体列表");
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
                        Intent intent = new Intent(MainActivity.this, BoxInitActivity.class);
                        startActivity(intent);
                    }
                });
                break;
            case 2:

                tabLayout.setVisibility(View.VISIBLE);
                titleBar.setVisibility(View.VISIBLE);
                titleBar.setRightButtonSrc(R.drawable.menu);
                titleBar.setTextViewText("授权处理");
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
            case 3:
                tabLayout.setVisibility(View.VISIBLE);
                titleBar.setVisibility(View.VISIBLE);
                titleBar.setRightButtonSrc(R.drawable.menu);
                titleBar.setTextViewText("日志列表");
                titleBar.setRightButtonSrc(R.drawable.add);
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
            default:
                break;


        }


    }


    //初始化控件
    private void initView() {

        user_info = findViewById(R.id.user_info_layout);
        enclosure = findViewById(R.id.enclosure_layout);
        leaving_alarm = findViewById(R.id.leaving_alarm_layout);
        sound = findViewById(R.id.sound_layout);
        setting = findViewById(R.id.setting_layout);
        ble = findViewById(R.id.ble_layout);

        systemSettingButton = findViewById(R.id.menu_set_button);
        systemBackButton = findViewById(R.id.menu_back_button);


        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        menu_user_img = findViewById(R.id.menu_user_img);
        menu_user_id = findViewById(R.id.menu_user_id);
        menu_user_name = findViewById(R.id.menu_user_name);
        menu_user_tell = findViewById(R.id.menu_user_tell);
        titleBar = findViewById(R.id.title_bar);
        drawerLayout = findViewById(R.id.drawer_layout);


        fragments.add(new NewHomeFragment());
        fragments.add(new DListFragment());
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
            titleBar.setVisibility(View.GONE);
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

    /**
     * 点击事件监听
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_user_img:
            case R.id.menu_user_id:
            case R.id.menu_user_name:
            case R.id.menu_user_tell:
                Intent intent = new Intent(MainActivity.this, PersonalInfoActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
                break;
            case R.id.setting_layout:
                Intent settingIntent = new Intent(MainActivity.this, PersonalSettingActivity.class);
                startActivity(settingIntent);
                break;
            case R.id.ble_layout:
                Intent bleIntent = new Intent(MainActivity.this, BLEActivity.class);
                startActivity(bleIntent);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String content = preferences.getString("menuInfo", null);
        if (content != null) {
            if (Util.isGoodJson(content)) {
                MenuUserInfo menuUserInfo = Util.handleMenuUserInfo(content);
                showMenuInfo(menuUserInfo);
            } else {
                //请求服务器菜单数据
                queryMenuInfo();
            }
        } else {
            //请求服务器菜单数据
            queryMenuInfo();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        preferences.edit().putString("homeInfo", null).commit();
        preferences.edit().putString("boxInfo", null).commit();
        preferences.edit().putString("userLogInfo", null).commit();
        preferences.edit().putString("boxLogInfo", null).commit();
        preferences.edit().putString("appLogInfo", null).commit();
        preferences.edit().putString("menuInfo", null).commit();
    }
}
