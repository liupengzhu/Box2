package cn.com.larunda.safebox;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.larunda.safebox.R;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.com.larunda.safebox.gson.CoordinateInfo;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class EnclosureInfoActivity extends AppCompatActivity {

    private TitleBar titleBar;
    private LocationClient mLocationClient;
    private MapView mapView;
    private BaiduMap baiduMap;
    private MyLocationListener myListener = new MyLocationListener();
    private String id;
    public static final String ENCLOSURE_INFO_URL = "http://safebox.dsmcase.com:90/api/area/";
    private List<LatLng> points = new ArrayList<>();
    private TextView textView;

    private SharedPreferences preferences;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        //注册监听函数
        setContentView(R.layout.activity_enclosure_info);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        id = getIntent().getStringExtra("id");
        initView();


        mapView = findViewById(R.id.enclosure_info_map_view);
        baiduMap = mapView.getMap();
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //普通地图 ,mBaiduMap是地图控制器对象

        baiduMap.setMyLocationEnabled(true);


        LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // 未打开位置开关，可能导致定位失败或定位不准，提示用户或做相应处理
            Toast.makeText(this, "未打开位置开关，可能导致定位失败或定位不准", Toast.LENGTH_SHORT).show();
        }


        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this, permissions, 1);
        } else {
            requestLocation();
        }
        sendRequest();


    }

    /**
     * 请求数据
     */
    private void sendRequest() {
        HttpUtil.sendGetRequestWithHttp(ENCLOSURE_INFO_URL + id + "?_token=" + token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final CoordinateInfo coordinateInfo = Util.handleCoordinateInfo(response.body().string());
                if (coordinateInfo != null && coordinateInfo.getError() == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showInfo(coordinateInfo);
                        }
                    });
                }
            }
        });
    }

    /**
     * 请求定位信息
     */
    private void requestLocation() {
        mLocationClient.start();
    }

    private void showInfo(CoordinateInfo coordinateInfo) {

        if (coordinateInfo.getF_name() != null) {
            textView.setText(coordinateInfo.getF_name());
        } else {
            textView.setText("");
        }
        if (coordinateInfo.getF_data() != null) {
            for (int i = 0; i < coordinateInfo.getF_data().size(); i++) {
                List<CoordinateInfo.FDataBean> fDataBeanList = coordinateInfo.getF_data().get(i);
                if (fDataBeanList != null) {
                    points.clear();
                    for (int j = 0; j < fDataBeanList.size(); j++) {
                        CoordinateInfo.FDataBean fDataBean = fDataBeanList.get(j);
                        if (fDataBean.getLat() != null && fDataBean.getLng() != null) {

                            LatLng latLng = new LatLng(Float.parseFloat(fDataBean.getLat()), Float.parseFloat(fDataBean.getLng()));
                            if (i == 0 && j == 0) {
                                MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(latLng);//移动到我的经纬度
                                baiduMap.animateMapStatus(update);
                                if (fDataBeanList.size() > 50) {
                                    update = MapStatusUpdateFactory.zoomTo(8f);//缩放大小
                                } else {
                                    update = MapStatusUpdateFactory.zoomTo(16f);//缩放大小
                                }
                                baiduMap.animateMapStatus(update);
                            }
                            points.add(latLng);
                        }
                    }
                    drawOverlay(points);

                }
            }
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(EnclosureInfoActivity.this, LoginActivity.class);
                    intent.putExtra("token_timeout", "登录超时");
                    preferences.edit().putString("token", null).commit();
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    /**
     * 画多边形
     *
     * @param points
     */
    private void drawOverlay(List<LatLng> points) {

        //构建用户绘制多边形的Option对象
        OverlayOptions polygonOption = new PolygonOptions()
                .points(points)
                .stroke(new Stroke(5, 0xFFF00F0E))
                .fillColor(0x5A7c8092);

        //在地图上添加多边形Option，用于显示
        baiduMap.addOverlay(polygonOption);
    }

    /**
     * 初始化View
     */
    private void initView() {
        preferences = PreferenceManager.getDefaultSharedPreferences(EnclosureInfoActivity.this);
        token = preferences.getString("token", null);
        textView = findViewById(R.id.enclosure_info_text);
        titleBar = findViewById(R.id.enclosure_info_title_bar);
        titleBar.setTextViewText("详细信息");
        titleBar.setRightButtonSrc(0);
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);
        titleBar.setOnClickListener(new TitleListener() {
            @Override
            public void onLeftButtonClickListener(View v) {


            }

            @Override
            public void onLeftBackButtonClickListener(View v) {
                finish();
            }

            @Override
            public void onRightButtonClickListener(View v) {

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(EnclosureInfoActivity.this, "必须同意所有权限", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    finish();
                }
        }
    }

    /**
     * 监听类
     */
    private class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
        mapView.onDestroy();
        // 当不需要定位图层时关闭定位图层
        baiduMap.setMyLocationEnabled(false);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mLocationClient.restart();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
