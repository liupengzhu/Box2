package cn.com.larunda.safebox;

import android.app.Application;
import android.content.Context;

import com.baidu.mapapi.SDKInitializer;

import org.litepal.LitePal;


public class MyApplication extends Application {

    public static final String VERSION = "V1.0.1";
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        LitePal.initialize(context);
        SDKInitializer.initialize(context);
    }

    public static Context getContext() {
        return context;
    }
}
