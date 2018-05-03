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
import com.baidu.mapapi.model.LatLngBounds;
import com.google.gson.JsonObject;
import com.larunda.safebox.R;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.com.larunda.safebox.gson.CoordinateInfo;
import cn.com.larunda.safebox.gson.EnclosureInfo;
import cn.com.larunda.safebox.util.ActivityCollector;
import cn.com.larunda.safebox.util.BaseActivity;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class EnclosureInfoActivity extends BaseActivity {

    private TitleBar titleBar;
    private LocationClient mLocationClient;
    private MapView mapView;
    private BaiduMap baiduMap;
    private MyLocationListener myListener = new MyLocationListener();
    private int id;
    public static final String ENCLOSURE_INFO_URL = Util.URL + "fence/";
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
        id = getIntent().getIntExtra("id", 0);
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
        HttpUtil.sendGetRequestWithHttp(ENCLOSURE_INFO_URL + id + Util.TOKEN + token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(EnclosureInfoActivity.this, "网络异常!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String content = response.body().string();
                int code = response.code();
                if (code == 200 && Util.isGoodJson(content)) {
                    final CoordinateInfo info = Util.handleCoordinateInfo(content);
                    showInfo(info);

                } else if (code == 401 || code == 412) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(EnclosureInfoActivity.this, LoginActivity.class);
                            intent.putExtra("token_timeout", "登录超时");
                            preferences.edit().putString("token", null).commit();
                            startActivity(intent);
                            ActivityCollector.finishAllActivity();
                        }
                    });
                } else if (code == 422) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject js = new JSONObject(content);
                                Toast.makeText(EnclosureInfoActivity.this, js.get("message") + "", Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

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

    private void showInfo(final CoordinateInfo coordinateInfo) {
        //LatLng position = null;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (coordinateInfo.getF_name() != null) {
                    textView.setText(coordinateInfo.getF_name());
                } else {
                    textView.setText("");
                }
            }
        });

        if (coordinateInfo.getF_data() != null) {
            try {
                JSONArray outJsonArray = new JSONArray(coordinateInfo.getF_data());
                for (int i = 0; i < outJsonArray.length(); i++) {
                    points.clear();
                    JSONArray innerJsonArray = outJsonArray.getJSONArray(i);
                    for (int j = 0; j < innerJsonArray.length(); j++) {
                        JSONObject jsonObject = innerJsonArray.getJSONObject(j);
                        if (jsonObject.getString("lat") != null && jsonObject.getString("lng") != null) {
                            LatLng latLng = new LatLng(Float.parseFloat(jsonObject.getString("lat")),
                                    Float.parseFloat(jsonObject.getString("lng")));
                            points.add(latLng);
                            builder.include(latLng);
                        }
                    }
                    if (points.size() >= 3) {
                        drawOverlay(points);
                    }
                }
                baiduMap.setMapStatus(MapStatusUpdateFactory
                        .newLatLngBounds(builder.build()));
            } catch (JSONException e) {
                e.printStackTrace();
            }

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
