package com.example.jeremysun.mywebviewtest;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.webkit.WebView;

/**
 * Created by jeremysun on 2018/5/11.
 */

public class MyApplication extends Application {

    private static MyApplication app;
    private static Context mContext;
    private static WebView sWebView;

    public static MyApplication getInstance() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        mContext = getApplicationContext();
        sWebView = new WebView(mContext);
    }

    public WebView getWebView() {
        return sWebView;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

}