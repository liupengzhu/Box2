package cn.com.larunda.safebox;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.baidu.mapapi.SDKInitializer;

import org.litepal.LitePal;


public class MyApplication extends Application {

    public static String VERSION;
    private static Context context;
    public static BluetoothManager bluetoothManager;
    public static BluetoothAdapter mBluetoothAdapter;
    public static BluetoothGatt bluetoothGatt;
    public static boolean isLinked = false;

    @Override
    public void onCreate() {
        super.onCreate();
        PackageManager pm = getPackageManager();
        PackageInfo pi = null;//getPackageName()是你当前类的包名，0代表是获取版本信息
        try {
            pi = pm.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        VERSION = pi.versionName;
        context = getApplicationContext();
        LitePal.initialize(context);
        SDKInitializer.initialize(context);
        initBle();
    }

    public static Context getContext() {
        return context;
    }

    private void initBle() {
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

}
