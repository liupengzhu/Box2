package com.example.box;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.titlebar.TitleBar;
import com.example.titlebar.TitleListener;

import java.util.ArrayList;
import java.util.List;

public class TrackActivity extends AppCompatActivity {

    private TitleBar titleBar;
    private LocationClient mLocationClient;
    private MapView mapView;
    private BaiduMap baiduMap;
    private MyLocationListener myListener = new MyLocationListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        //注册监听函数
        setContentView(R.layout.activity_track);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        initView();


        mapView = findViewById(R.id.map_view);
        baiduMap = mapView.getMap();
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //普通地图 ,mBaiduMap是地图控制器对象

        baiduMap.setMyLocationEnabled(true);


        LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // 未打开位置开关，可能导致定位失败或定位不准，提示用户或做相应处理
            Toast.makeText(TrackActivity.this, "未打开位置开关，可能导致定位失败或定位不准", Toast.LENGTH_SHORT).show();
        }


        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(TrackActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(TrackActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(TrackActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(TrackActivity.this, permissions, 1);
        } else {
            requestLocation();
        }
        showInfo();

    }

    /**
     * 请求定位信息
     */
    private void requestLocation() {
        mLocationClient.start();
    }

    private void showInfo() {

        //定义Maker坐标点

        LatLng point = new LatLng(39.965, 110.404);

        //构建Marker图标

        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.locatin_red);

        //构建MarkerOption，用于在地图上添加Marker

        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap);

        //在地图上添加Marker，并显示

        baiduMap.addOverlay(option);

        List<LatLng> points = new ArrayList<LatLng>();
        points.add(new LatLng(39.965, 110.404));
        points.add(new LatLng(39.925, 110.454));
        points.add(new LatLng(39.955, 110.494));
        points.add(new LatLng(39.905, 110.554));
        points.add(new LatLng(39.965, 110.604));

        //构建分段颜色索引数组

        List<Integer> colors = new ArrayList<>();
        colors.add(Integer.valueOf(Color.RED));


        OverlayOptions ooPolyline = new PolylineOptions()
                .colorsValues(colors)
                .width(5)
                .points(points);

        //添加在地图中

        Polyline mPolyline = (Polyline) baiduMap.addOverlay(ooPolyline);

        LatLng ll = new LatLng(39.965, 110.404);
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);//移动到我的经纬度
        baiduMap.animateMapStatus(update);
        update = MapStatusUpdateFactory.zoomTo(16f);//缩放大小
        baiduMap.animateMapStatus(update);


    }

    /**
     * 初始化View
     */
    private void initView() {

        titleBar = findViewById(R.id.track_title_bar);
        titleBar.setTextViewText("定位轨迹");
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
                            Toast.makeText(TrackActivity.this, "必须同意所有权限", Toast.LENGTH_SHORT).show();
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
