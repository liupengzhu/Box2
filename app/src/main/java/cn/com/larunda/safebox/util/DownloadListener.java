package cn.com.larunda.safebox.util;

/**
 * Created by sddt on 18-3-1.
 */

public interface DownloadListener {
    void onProgress(int progress);
    void onSuccess();
    void onFailed();
    void onCancled();
    void onPaused();
}
