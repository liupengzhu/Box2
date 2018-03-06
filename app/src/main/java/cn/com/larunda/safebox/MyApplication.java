package cn.com.larunda.safebox;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;

import com.baidu.mapapi.SDKInitializer;

import org.litepal.LitePal;


public class MyApplication extends Application {

    public static final String VERSION = "V1.0.1";
    private static Context context;
    public static BluetoothManager bluetoothManager;
    public static BluetoothAdapter mBluetoothAdapter;
    public static BluetoothGatt bluetoothGatt;
    public static boolean isLinked = false;

    @Override
    public void onCreate() {
        super.onCreate();

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
