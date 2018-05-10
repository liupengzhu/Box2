package cn.com.larunda.safebox;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.larunda.safebox.R;
import com.larunda.selfdialog.UpdateDialog;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.com.larunda.safebox.adapter.HomeAdapter;
import cn.com.larunda.safebox.fragment.CollectorFragment;
import cn.com.larunda.safebox.fragment.CourierFragment;
import cn.com.larunda.safebox.fragment.SystemLogFragment;
import cn.com.larunda.safebox.gson.VersionInfo;
import cn.com.larunda.safebox.service.AutoUpdateService;
import cn.com.larunda.safebox.service.DownloadService;
import cn.com.larunda.safebox.util.ActivityCollector;
import cn.com.larunda.safebox.util.BaseActivity;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class NormalUserActivity extends BaseActivity implements View.OnClickListener {

    public static ViewPager viewPager;
    public static TabLayout tabLayout;
    public static TitleBar titleBar;
    private String[] titles = {"快递员", "收件员", "系统日志"};
    private int[] icons = {R.drawable.courier1, R.drawable.collector1, R.drawable.rz1};
    private List<Fragment> fragments = new ArrayList<>();
    private SharedPreferences preferences;
    private String token;
    private HomeAdapter adapter;

    private RelativeLayout backButton;

    private DrawerLayout drawerLayout;

    private LinearLayout bleButton;

    private UpdateDialog updateDialog;
    private boolean isUpdate;
    public static final String VERSION_CHECK = Util.URL + "version" + Util.TOKEN;

    private DownloadService service;
    private DownloadService.DownloadBinder binder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            isBind = true;
            binder = (DownloadService.DownloadBinder) service;
            binder.startDownload(Util.PATH + updateUrl);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBind = false;
        }
    };

    private String updateUrl;
    private boolean isBind = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_user);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        Intent serviceIntent = new Intent(this, AutoUpdateService.class);
        startService(serviceIntent);

        initView();
        isUpdate = preferences.getBoolean("isUpdate", true);
        initTabs();
        initEvent();
        sendVersionRequest();
    }

    /**
     * 初始化点击事件
     */
    private void initEvent() {
        backButton.setOnClickListener(this);
        bleButton.setOnClickListener(this);
    }

    //初始化控件
    private void initView() {

        drawerLayout = findViewById(R.id.normal_user_drawer_layout);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = preferences.getString("token", null);


        viewPager = findViewById(R.id.normal_user_view_pager);
        tabLayout = findViewById(R.id.normal_user_tab_layout);
        titleBar = findViewById(R.id.normal_user_title_bar);

        fragments.add(new CourierFragment());
        fragments.add(new CollectorFragment());
        fragments.add(new SystemLogFragment());

        adapter = new HomeAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        backButton = findViewById(R.id.normal_user_menu_back_button);

        bleButton = findViewById(R.id.normal_user_ble_layout);

        updateDialog = new UpdateDialog(this);
        updateDialog.setYesOnclickListener(new UpdateDialog.onYesOnclickListener() {
            @Override
            public void onYesClick(View v) {
                preferences.edit().putBoolean("isUpdate", false).commit();
                Intent intent = new Intent(NormalUserActivity.this, DownloadService.class);
                bindService(intent, connection, BIND_AUTO_CREATE);
                updateDialog.cancel();
            }
        });
        updateDialog.setNoOnclickListener(new UpdateDialog.onNoOnclickListener() {
            @Override
            public void onNoClick(View v) {
                preferences.edit().putBoolean("isUpdate", false).commit();
                updateDialog.cancel();
            }
        });

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
                        tab.getCustomView().findViewById(R.id.tab_image_view).setBackgroundResource(R.drawable.courier2);
                        setTitleInfo(0);
                        break;
                    case 1:
                        tab.getCustomView().findViewById(R.id.tab_image_view).setBackgroundResource(R.drawable.collector2);
                        setTitleInfo(1);
                        break;
                    case 2:
                        tab.getCustomView().findViewById(R.id.tab_image_view).setBackgroundResource(R.drawable.rz2);
                        setTitleInfo(2);
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
                        tab.getCustomView().findViewById(R.id.tab_image_view).setBackgroundResource(R.drawable.courier1);
                        break;
                    case 1:
                        tab.getCustomView().findViewById(R.id.tab_image_view).setBackgroundResource(R.drawable.collector1);
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
            case 2:

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
            imageView.setBackgroundResource(R.drawable.courier2);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.normal_user_menu_back_button:
                Intent intent = new Intent(this, LoginActivity.class);
                preferences.edit().putString("token", null).commit();
                startActivity(intent);
                ActivityCollector.finishAllActivity();
                break;
            case R.id.normal_user_ble_layout:
                Intent bleIntent = new Intent(this, BLEActivity.class);
                startActivity(bleIntent);
                break;
        }
    }

    /**
     * 发送版本验证请求
     */
    private void sendVersionRequest() {
        /*JSONObject jsonObject = new JSONObject();
            HttpUtil.sendGetRequestWithHttp(VERSION_CHECK + token, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String content = response.body().string();
                    Log.d("main", content);
                    if (Util.isGoodJson(content)) {
                        parseVersion(content);
                    }
                }
            });
*/
    }

    /**
     * 解析版本信息
     *
     * @param content
     */
    private void parseVersion(final String content) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                VersionInfo versionInfo = Util.handleVersionInfo(content);
                if (versionInfo.getUrl() != null) {
                    updateUrl = versionInfo.getUrl();
                    showUpdateDialog(versionInfo);
                }
            }
        });

    }


    /**
     * 显示更新弹窗
     */
    private void showUpdateDialog(VersionInfo versionInfo) {
        if (isUpdate) {
            if (updateDialog != null) {
                updateDialog.setTitleText("发现新版本" + versionInfo.getVersion());
                StringBuilder content = new StringBuilder();
                if (versionInfo.getUpdated_list().getAdd() != null && versionInfo.getUpdated_list().getAdd().size() != 0) {
                    for (int i = 0; i < versionInfo.getUpdated_list().getAdd().size(); i++) {
                        content.append("【新增】" + versionInfo.getUpdated_list().getAdd().get(i) + "\n");
                    }
                }
                if (versionInfo.getUpdated_list().getFix() != null && versionInfo.getUpdated_list().getFix().size() != 0) {
                    for (int i = 0; i < versionInfo.getUpdated_list().getFix().size(); i++) {
                        content.append("【修复】" + versionInfo.getUpdated_list().getFix().get(i) + "\n");
                    }
                }
                content.append("为了不影响您的正常使用,请尽快更新最新版本");
                updateDialog.setContentText(content.toString());
                updateDialog.show();
            }
            updateDialog.show();
        }
    }

}
