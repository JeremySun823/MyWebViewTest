package com.example.jeremysun.mywebviewtest;

import android.webkit.WebResourceResponse;

/**
 * Created by jeremysun on 2018/6/16.
 */

public class WebResourceHelper {

    private WebResourceHelper() {
    }

    private static class SingletonHolder {
        private static final WebResourceHelper mInstance = new WebResourceHelper();

        private SingletonHolder() {
        }
    }

    public static WebResourceHelper getInstance() {
        return WebResourceHelper.SingletonHolder.mInstance;
    }

    public WebResourceResponse getCache(String url) {
        // 自定义逻辑
        return null;
    }
}
