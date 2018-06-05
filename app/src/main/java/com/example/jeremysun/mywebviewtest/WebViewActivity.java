package com.example.jeremysun.mywebviewtest;

import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.jeremysun.mywebviewtest.utils.CommonUtils;

import java.net.URL;

public class WebViewActivity extends AppCompatActivity {

    private static final String APP_CACAHE_DIRNAME = "appCache";
    private LinearLayout mLlWebView;
    private WebView mWebView;
    private WebSettings mWebSettings;
    private long mExitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        mLlWebView = findViewById(R.id.ll_web_view);

        // don't add WebView in layout, since it would cause memory leak
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        mWebView = new WebView(getApplicationContext());
//        mWebView = MyApplication.getInstance().getWebView();
        mWebView.setLayoutParams(params);
        mLlWebView.addView(mWebView);


        initWebViewSettings();
        initWebViewClient();
        initWebChromeClient();

        loadUrl();

    }

    private void loadUrl() {
        // 加载web HTML
        mWebView.loadUrl("https://www.hupu.com");

        // 加载apk包内的assets中的HTML
//        mWebView.loadUrl("file:///android_asset/index.html");

        // 加载手机本地存储的HTML
//        mWebView.loadUrl("content://com.android.test/sdcard/test.html");

        // 加载HTML代码块
        // data：需要截取展示的内容，内容里不能出现 ’#’, ‘%’, ‘\’ , ‘?’ 这四个字符，若出现了需用 %23, %25, %27, %3f 对应来替代，否则会出现异常
        // mimeType：展示内容的类型，比如image/png，text/plain等
        // encoding：字节码
//        mWebView.loadData(String data, String mimeType, String encoding)

        // 获取当前页面的Url
//        String url = mWebView.getUrl();

        // 获取当前页面的原始Url，因为可能会经过多次重定向
//        String originalUrl = mWebView.getOriginalUrl();

        // 重新reload当前的URL，即刷新
//        mWebView.reload();
    }


    @Override
    protected void onResume() {
        super.onResume();
        // 在先前调用onPause()后，
        // 我们可以调用该方法来恢复WebView的运行。
        // 激活WebView为活跃状态，能正常执行网页的响应
        mWebView.onResume();

        // 恢复pauseTimers时的所有操作。
        mWebView.resumeTimers();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 当页面被失去焦点被切换到后台不可见状态，需要执行onPause操作，
        // 通过onPause动作通知内核暂停所有的动作，比如DOM的解析、plugin的执行、JavaScript执行、动画的执行或定位的获取等。
        // 需要注意的是该方法并不会暂停JavaScript的执行。
        mWebView.onPause();

        // 该方法面向全局整个应用程序的WebView,
        // 它会暂停所有WebView的layout，parsing，JavaScript Timer。
        // 当程序进入后台时，该方法的调用可以降低CPU功耗。
        mWebView.pauseTimers();
    }

    private void goForward() {
        // 用来确认WebView是否还有可向前的历史记录
        if (mWebView != null && mWebView.canGoForward()) {
            // 在WebView历史记录里前进到下一项
            mWebView.goForward();
        }
    }

    private void goBack() {
        // 用来确认WebView里是否还有可回退的历史记录
        if (mWebView != null && mWebView.canGoBack()) {
            // 在WebView历史记录后退到上一项
            mWebView.goBack();
        }
    }

    private void goBackorForward(int steps) {
        // 以当前的页面为起始点，用来确认WebView的历史记录是否足以后退或前进给定的步数，正数为前进，负数为后退
        if (mWebView != null && mWebView.canGoBackOrForward(steps)) {
            // 以当前页面为起始点，前进或后退历史记录中指定的步数，正数为前进，负数为后退
            mWebView.goBackOrForward(steps);
        }
    }

    /**
     * 页面后退，双击返回
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView != null && mWebView.canGoBack()) {
                mWebView.goBack();
                return true;
            } else if (event.getRepeatCount() == 0) {
                // back activity
                exit();
                return true;
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(WebViewActivity.this, "One More Press, then exit", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }

    private void clearCache() {
        // 清空网页访问留下的缓存数据。
        // 需要注意的时，由于缓存是全局的，所以只要是WebView用到的缓存都会被清空，即便其他地方也会使用到。
        // 该方法接受一个参数，从命名即可看出作用。若设为false，则只清空内存里的资源缓存，而不清空磁盘里的
        mWebView.clearCache(true);

        // 清除当前WebView访问的历史记录
        // 只会WebView访问历史记录里的所有记录除了当前访问记录
        mWebView.clearHistory();

        // 清除自动完成填充的表单数据。
        // 需要注意的是，该方法仅仅清除当前表单域自动完成填充的表单数据，并不会清除WebView存储到本地的数据
        mWebView.clearFormData();
    }


    // 是否处于顶端
    private boolean isScrollTop() {
        if (mWebView == null) {
            return false;
        }

        // getScrollY(): 该方法返回的当前可见区域的顶端距整个页面顶端的距离，也就是当前内容滚动的距离
        boolean isTop = (mWebView.getScrollY() == 0);
        return isTop;
    }

    // 是否处于底端
    private boolean isScrollBottom() {
        if (mWebView == null) {
            return false;
        }

        // getHeight(): 方法都返回当前WebView这个容器的高度。其实以上两个方法都属于View
        // getContentHeight():该方法返回整个HTML页面的高度，但该高度值并不等同于当前整个页面的高度
        // 因为WebView有缩放功能， 所以当前整个页面的高度实际上应该是原始HTML的高度再乘上缩放比例
        // getScale()在sdk 17中已经被弃用，建议使用WebViewClient中的onScaleChanged获取缩放比例
        boolean isBottom = (mWebView.getContentHeight() * mWebView.getScale()
                == (mWebView.getHeight() + mWebView.getScrollY()));
        return isBottom;
    }

    // 向上滚动
    private void pageUp(boolean top) {
        if (mWebView != null) {
            // top为true时，将WebView展示的页面滑动至顶部
            // top为false时，将WebView展示的页面向上滚动一个页面高度
            mWebView.pageUp(top);
        }
    }

    // 向下滚动
    private void pageDown(boolean bottom) {
        if (mWebView != null) {
            // bottom为true时，将WebView展示的页面滑动至底部
            // top为false时，将WebView展示的页面向下滚动一个页面高度
            mWebView.pageDown(bottom);
        }
    }

    private void initWebViewSettings() {
        mWebSettings = mWebView.getSettings();

        //如果访问的页面中要与Javascript交互，则WebView必须设置支持Javascript
        mWebSettings.setJavaScriptEnabled(true);


        //设置自适应屏幕，两者合用
        //将图片调整到适合WebView的大小
        mWebSettings.setUseWideViewPort(true);
        // 缩放至屏幕的大小
        mWebSettings.setLoadWithOverviewMode(true);

        // 缩放操作
        // 支持缩放，默认为true。是下面那个的前提。
        mWebSettings.setSupportZoom(true);
        // 设置内置的缩放控件。若为false，则该WebView不可缩放
        mWebSettings.setBuiltInZoomControls(true);
        // 隐藏原生的缩放控件
        mWebSettings.setDisplayZoomControls(false);

        // 其他细节操作
        // 设置可以访问文件
        mWebSettings.setAllowFileAccess(true);
        // 支持通过JS打开新窗口
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        // 支持自动加载图片
        mWebSettings.setLoadsImagesAutomatically(true);
        // 设置编码格式
        mWebSettings.setDefaultTextEncodingName("utf-8");

        if (CommonUtils.isNetworkAvailable(getBaseContext())) {
            // 根据cache-control决定是否从网络上取数据
            mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            // 没网，则从本地获取，即离线加载
            mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }

        // 开启 DOM storage API 功能
        mWebSettings.setDomStorageEnabled(true);
        // 开启 database storage API 功能
        mWebSettings.setDatabaseEnabled(true);
        // 开启 Application Caches 功能
        mWebSettings.setAppCacheEnabled(true);

        // 设置  Application Caches 缓存目录
        String cacheDirPath = getFilesDir().getAbsolutePath() + APP_CACAHE_DIRNAME;
        mWebSettings.setAppCachePath(cacheDirPath);

    }

    private long startTime = 0L;

    private void initWebViewClient() {
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.d("sunwillfly", "onPageStarted url = " + url);
                startTime = System.currentTimeMillis();
                super.onPageStarted(view, url, favicon);


            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("sunwillfly", "onPageFinished url = " + url);
                Log.d("sunwillfly", "page start time = " + (System.currentTimeMillis() - startTime));
                super.onPageFinished(view, url);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                Log.d("sunwillfly", "onLoadResource url = " + url);
                super.onLoadResource(view, url);
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Log.d("sunwillfly", "shouldOverrideUrlLoading url = " + String.valueOf(request.getUrl()));
                Uri uri = request.getUrl();
                if ("shihuo".equals(uri.getScheme())) {
                    Log.d("sunwillfly", "shouldOverrideUrlLoading SchemeSpecificPart = " + uri.getSchemeSpecificPart());
                    return true;
                }

                view.loadUrl(String.valueOf(request.getUrl()));
                return true;
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                Log.d("sunwillfly", "shouldInterceptRequest url = " + request.getUrl());
                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                return super.shouldInterceptRequest(view, url);
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.d("sunwillfly", "onReceivedError error = " + error.getErrorCode());
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.d("sunwillfly", "onReceivedError deprecated");
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                Log.d("sunwillfly", "onReceivedHttpError");

            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                Log.d("sunwillfly", "onReceivedSslError");
                //表示等待证书响应
                handler.proceed();
                //表示挂起连接，为默认方式
                // handler.cancel();
                //可做其他处理
                // handler.handleMessage(null);
            }
        });
    }

    private void initWebChromeClient() {
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                Log.d("sunwillfly", "newProgress = " + newProgress);
            }
        });
    }

    /**
     * 避免内存泄漏，使用parent remove view，然后销毁webview
     */
    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();

            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            // 销毁WebView。需要注意的是：
            // 这个方法的调用应在WebView从父容器中被remove掉之后。我们可以手动地调用
//            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }
}
