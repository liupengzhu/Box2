package cn.com.larunda.safebox.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;

import com.larunda.safebox.R;

import java.io.File;

import cn.com.larunda.safebox.MainActivity;
import cn.com.larunda.safebox.util.DownloadListener;
import cn.com.larunda.safebox.util.DownloadTask;

/**
 * Created by sddt on 18-3-1.
 */

public class DownloadService extends Service {
    private DownloadTask downloadTask;
    private String downloadUrl;


    private DownloadListener downloadLister = new DownloadListener() {
        @Override
        public void onProgress(int progress) {
            getNotificationManager().notify(1, getNotification("下载中....", progress));
        }


        @Override
        public void onSuccess() {
            downloadTask = null;
            stopForeground(true);
            getNotificationManager().notify(1, getNotification("下载成功！", -1));
            //Toast.makeText(DownloadService.this, "下载成功！", Toast.LENGTH_SHORT).show();
            String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));//String.LastIndexOf 方法
            //报告指定的 Unicode 字符或 String 在此实例中的最后一个匹配项的索引位置。String.Substring (Int32)从此实例检索子字符串。子字符串从指定的字符位置开始。
            String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            File file = new File(directory + fileName);
            if (file.exists()) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                // 由于没有在Activity环境下启动Activity,设置下面的标签
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
                Uri apkUri = FileProvider.getUriForFile(getApplicationContext(),
                        "cn.com.larunda.cameraalbumtest.fileprovider", file);
                //添加这一句表示对目标应用临时授权该Uri所代表的文件
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                getApplicationContext().startActivity(intent);
            }

            getNotificationManager().cancel(1);

        }

        @Override
        public void onFailed() {
            downloadTask = null;
            stopForeground(true);
            getNotificationManager().notify(1, getNotification("下载失败！", -1));
            //Toast.makeText(DownloadService.this, "下载失败！", Toast.LENGTH_SHORT).show();
        }


        @Override
        public void onCancled() {
            downloadTask = null;
            stopForeground(true);
            //Toast.makeText(DownloadService.this, "取消下载！", Toast.LENGTH_SHORT).show();

        }


        @Override
        public void onPaused() {
            downloadTask = null;
            // Toast.makeText(DownloadService.this, "暂停下载！", Toast.LENGTH_SHORT).show();
        }
    };
    private DownloadBinder mBinder = new DownloadBinder();

    public class DownloadBinder extends Binder {
        public void startDownload(String url) {
            if (downloadTask == null) {
                downloadUrl = url;
                downloadTask = new DownloadTask(downloadLister);
                downloadTask.execute(downloadUrl);
                startForeground(1, getNotification("Download....", 0));
                //Toast.makeText(DownloadService.this, "下载中...", Toast.LENGTH_SHORT).show();
            }
        }

        public void pauseDownload() {
            if (downloadTask != null) {
                downloadTask.pauseDownload();
            }
        }

        public void cancleDownload() {
            if (downloadTask != null) {
                downloadTask.cancleDownload();
            } else {
                if (downloadUrl != null) {
                    //取消下载是要讲文件删除
                    String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));//String.LastIndexOf 方法
                    //报告指定的 Unicode 字符或 String 在此实例中的最后一个匹配项的索引位置。String.Substring (Int32)从此实例检索子字符串。子字符串从指定的字符位置开始。
                    String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                    File file = new File(directory + fileName);
                    if (file.exists()) {
                        file.delete();
                    }
                    getNotificationManager().cancel(1);
                    stopForeground(true);
                    //Toast.makeText(DownloadService.this, "取消下载", Toast.LENGTH_SHORT).show();
                }


            }
        }


    }


    public DownloadService() {
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    private Notification getNotification(String title, int progress) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.app);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        /*builder.setContentIntent(pi);*/
        builder.setContentTitle(title);
        if (progress >= 0) {
            builder.setContentText(progress + "%");
            builder.setProgress(100, progress, false);
        }
        return builder.build();
    }

    @Override
    public void onDestroy() {
        if (downloadTask != null) {
            downloadTask.pauseDownload();
        }
        super.onDestroy();
    }
}
