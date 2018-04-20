package cn.com.larunda.safebox;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.larunda.safebox.R;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

import java.util.ArrayList;
import java.util.List;

import cn.com.larunda.safebox.adapter.HomeAdapter;
import cn.com.larunda.safebox.fragment.CompanyListFragment;
import cn.com.larunda.safebox.fragment.DListFragment;
import cn.com.larunda.safebox.fragment.NewHomeFragment;
import cn.com.larunda.safebox.fragment.TotalLogFragment;
import cn.com.larunda.safebox.service.DownloadService;
import cn.com.larunda.safebox.util.BaseActivity;
import cn.com.larunda.safebox.util.CustomViewPager;
import de.hdodenhof.circleimageview.CircleImageView;

public class SuperAdminActivity extends BaseActivity {

    public static CustomViewPager viewPager;
    public static TabLayout tabLayout;
    public static TitleBar titleBar;
    private String[] titles = {"总览", "企业管理"};
    private int[] icons = {R.drawable.sy1, R.drawable.dsx1};
    private List<Fragment> fragments = new ArrayList<>();
    private SharedPreferences preferences;
    private String token;
    private HomeAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_admin);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }


        initView();
        initTabs();
    }

    //初始化控件
    private void initView() {

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = preferences.getString("token", null);


        viewPager = findViewById(R.id.super_view_pager);
        tabLayout = findViewById(R.id.super_tab_layout);
        titleBar = findViewById(R.id.super_title_bar);

        fragments.add(new NewHomeFragment());
        fragments.add(new CompanyListFragment());

        adapter = new HomeAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
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
                        //drawerLayout.openDrawer(Gravity.START);

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

                titleBar.setVisibility(View.VISIBLE);
                titleBar.setRightButtonSrc(R.drawable.menu);
                titleBar.setTextViewText("企业列表");
                titleBar.setRightButtonSrc(R.drawable.add);
                titleBar.setOnClickListener(new TitleListener() {
                    @Override
                    public void onLeftButtonClickListener(View v) {
                        //drawerLayout.openDrawer(Gravity.START);

                    }

                    @Override
                    public void onLeftBackButtonClickListener(View v) {

                    }

                    @Override
                    public void onRightButtonClickListener(View v) {
                        /*Intent intent = new Intent(MainActivity.this, BoxInitActivity.class);
                        startActivity(intent);*/
                    }
                });
                break;

            default:
                break;


        }
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
                    //drawerLayout.openDrawer(Gravity.START);

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
