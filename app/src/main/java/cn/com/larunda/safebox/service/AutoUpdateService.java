package cn.com.larunda.safebox.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import java.io.IOException;

import cn.com.larunda.safebox.LoginActivity;
import cn.com.larunda.safebox.MainActivity;
import cn.com.larunda.safebox.gson.Home;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {

    private SharedPreferences preferences;
    private String token;
    private static final String BOX_URI = Util.URL + "app/home" + Util.TOKEN;

    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateHomeInfo();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 5 * 60 * 1000;//5分钟毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 更新首页信息
     */
    private void updateHomeInfo() {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = preferences.getString("token", null);
        if (token != null) {
            HttpUtil.sendGetRequestWithHttp(BOX_URI + token, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String content = response.body().string();
                    if (Util.isGoodJson(content)) {
                        final Home home = Util.handleHomeInfo(content);
                        if (home != null && home.error == null) {
                            preferences.edit().putString("homeInfo", content).commit();
                        }

                    }
                }
            });
        }
    }
}
