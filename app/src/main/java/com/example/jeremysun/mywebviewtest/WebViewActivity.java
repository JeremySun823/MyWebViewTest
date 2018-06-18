package com.example.jeremysun.mywebviewtest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.jeremysun.mywebviewtest.utils.CommonUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Set;

public class WebViewActivity extends AppCompatActivity {

    private static final String TAG = "WebViewActivity";

    private static final String APP_CACHE_DIRNAME = "appCache";
    private LinearLayout mLlWebView;
    private WebView mWebView;
    private WebSettings mWebSettings;
    private long mExitTime = 0;

    private ProgressView mProgressView;//进度条
    private Toolbar mToolbar;
    private FrameLayout mErrorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_web_view);
        mErrorLayout = findViewById(R.id.fl_error_page);

        initToolBar();


        mLlWebView = findViewById(R.id.ll_web_view);

        initProgressView();
        initWebView();

        initWebViewSettings();
        initWebViewClient();
        initWebChromeClient();
        initJavaScriptInterface();


        String url = getIntent().getStringExtra("web_url");
        String type = getIntent().getStringExtra("type");

        loadUrl(url, type);

    }


    private void initToolBar() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.android_to_js1:
                        Log.d(TAG, "loadUrl with no params");
                        mWebView.loadUrl("javascript:testLoadUrl1()");
                        break;
                    case R.id.android_to_js2:
                        Log.d(TAG, "loadUrl with params");
                        String parameters = "Test LoadUrl With Params Successfully";
                        mWebView.loadUrl("javascript:testLoadUrl2(\"" + parameters + "\")");

                        break;
                    case R.id.android_to_js3:
                        Log.d(TAG, "evaluateJavascript with return");
                        mWebView.evaluateJavascript("javascript:testEvaluateJavascript()", new ValueCallback<String>() {

                            @Override
                            public void onReceiveValue(String value) {
                                Toast.makeText(WebViewActivity.this, "onReceiveValue = " + value, Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                }
                return false;
            }
        });

    }


    private void initProgressView() {
        Log.d(TAG, "initProgressView");

        //初始化进度条
        mProgressView = new ProgressView(getBaseContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, CommonUtils.dp2px(getBaseContext(), 4));

        mProgressView.setLayoutParams(params);
        mProgressView.setColor(Color.BLUE);
        mProgressView.setProgress(0);
        //把进度条加到WebView中
        mLlWebView.addView(mProgressView);
    }


    private void initWebView() {
        Log.d(TAG, "initWebView");

        // don't add WebView in layout, since it would cause memory leak
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        mWebView = new WebView(getApplicationContext());
//        mWebView = MyApplication.getInstance().getWebView();
        mWebView.setLayoutParams(params);
        mLlWebView.addView(mWebView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tool_menu, menu);
        return true;
    }

    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        if (menu != null) {
            if (menu.getClass() == MenuBuilder.class) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {

                }
            }
        }
        return true;
    }


    private void loadUrl(String url, String type) {
        Log.d(TAG, "loadUrl");

        if (type.equals("remote")) {
            // 加载web HTML
            mWebView.loadUrl(url);
        } else if (type.equals("local")) {

            // 加载apk包内的assets中的HTML
            mWebView.loadUrl("file:///android_asset/js_test.html");
        }

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
        Log.d(TAG, "onResume");

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
        Log.d(TAG, "onPause");

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
        } else {
            exit();
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
        Log.d(TAG, "onKeyDown");

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
        Log.d(TAG, "exit");
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            mExitTime = System.currentTimeMillis();
            Toast.makeText(WebViewActivity.this, "One More Press, then exit", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(WebViewActivity.this, "exit", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void clearCache() {
        Log.d(TAG, "clearCache");


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
        Log.d(TAG, "initWebViewSettings");

        mWebSettings = mWebView.getSettings();


        // JavaScript 相关
        /**
         * 如果访问的页面中要与Javascript交互，则WebView必须设置支持Javascript
         * 若加载的 html 里有JS 在执行动画等操作，会造成资源浪费（CPU、电量）
         * 在 onStop 和 onResume 里分别把 setJavaScriptEnabled() 给设置成 false 和 true 即可
         */
        // 设置WebView是否允许执行JavaScript脚本，默认false，不允许
        mWebSettings.setJavaScriptEnabled(true);

        // 设置WebView是否可以由JavaScript自动打开窗口，默认为false，通常与JavaScript的window.open()配合使用。
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);


        // 访问权限相关
        // 设置在WebView内部是否允许访问文件，默认允许访问。
        mWebSettings.setAllowFileAccess(true);
        // 设置在WebView内部是否允许访问ContentProvider，默认允许访问。
        mWebSettings.setAllowContentAccess(true);
        // 设置在WebView内部是否允许通过file url加载的 Js代码读取其他的本地文件
        // Android 4.1前默认允许，4.1后默认禁止
        mWebSettings.setAllowFileAccessFromFileURLs(false);
        // 设置WebView内部是否允许通过 file url 加载的 Javascript 可以访问其他的源(包括http、https等源)
        // Android 4.1前默认允许，4.1后默认禁止
        mWebSettings.setAllowUniversalAccessFromFileURLs(false);


        // 屏幕显示相关
        // 设置WebView是否使用viewport，将图片调整到适合WebView的大小
        // 当该属性被设置为false时，加载页面的宽度总是适应WebView控件宽度；
        // 当被设置为true，当前页面包含viewport属性标签，在标签中指定宽度值生效，
        // 如果页面不包含viewport标签，无法提供一个宽度值，这个时候该方法将被使用。
        mWebSettings.setUseWideViewPort(true);
        // 设置WebView是否使用预览模式加载界面，即缩放至屏幕的大小
        mWebSettings.setLoadWithOverviewMode(true);
        // 设置WebView是否支持多窗口, 如果为true，必须要重写WebChromeClient的onCreateWindow方法
        mWebSettings.setSupportMultipleWindows(true);
        // 设置WebView是否需要设置一个节点获取焦点当WebView#requestFocus(int,android.graphics.Rect)被调用时，默认true
        mWebSettings.setNeedInitialFocus(true);

        // 屏幕缩放相关
        // 设置WebView是否支持使用屏幕控件或手势进行缩放，默认是true，支持缩放, 是前面方法的前提
        mWebSettings.setSupportZoom(true);
        // 设置WebView是否使用其内置的变焦机制，该机制集合屏幕缩放控件使用，默认是false，不使用内置变焦机制。
        mWebSettings.setBuiltInZoomControls(true);
        // 设置WebView使用内置缩放机制时，是否展现在屏幕缩放控件上，默认true，展现在控件上。
        mWebSettings.setDisplayZoomControls(false);

        // 显示字体相关
        // 设置WebView加载页面文本内容的编码，默认"UTF-8"。
        mWebSettings.setDefaultTextEncodingName("UTF-8");
        // 设置标准的字体族，默认"sans-serif"。
        mWebSettings.setStandardFontFamily("sans-serif");
        // 设置混合字体族，默认"monospace"。
        mWebSettings.setFixedFontFamily("monospace");
        // 设置默认填充字体大小，默认16，取值区间为[1-72]，超过范围，使用其上限值。
        mWebSettings.setDefaultFixedFontSize(16);
        // 设置默认字体大小，默认16，取值区间[1-72]，超过范围，使用其上限值。
        mWebSettings.setDefaultFontSize(16);

        // 资源加载相关
        // 设置WebView代理字符串，如果String为null或为空，将使用系统默认值
        mWebSettings.setUserAgentString("");
        /**
         * 需要注意的是，如果设置是从禁止到允许的转变的话，图片数据并不会在设置改变后立刻去获取，
         * 而是在WebView调用reload()的时候才会生效。
         * 这个时候，需要确保这个app拥有访问Internet的权限，否则会抛出安全异常。
         * 通常没有禁止图片加载的需求的时候，完全不用管这个方法，
         * 因为当我们的app拥有访问Internet的权限时，这个flag的默认值就是false。
         */
        // 设置WebView是否以http、https方式访问从网络加载图片资源，默认false
        mWebSettings.setBlockNetworkImage(false);
        // 设置WebView是否从网络加载资源，Application需要设置访问网络权限，否则报异常
        mWebSettings.setBlockNetworkLoads(false);
        // 设置WebView是否加载图片资源，默认true，自动加载图片
        mWebSettings.setLoadsImagesAutomatically(true);
        /**
         * MIXED_CONTENT_ALWAYS_ALLOW：允许从任何来源加载内容，即使起源是不安全的；
         * MIXED_CONTENT_NEVER_ALLOW：不允许Https加载Http的内容，即不允许从安全的起源去加载一个不安全的资源；
         * MIXED_CONTENT_COMPATIBILITY_MODE：当涉及到混合式内容时，WebView 会尝试去兼容最新Web浏览器的风格。
         * Android 5.0以下，默认是MIXED_CONTENT_ALWAYS_ALLOW
         * Android 5.0已上，默认是MIXED_CONTENT_NEVER_ALLOW
         **/
        // 特别注意：5.1以上默认禁止了https和http混用
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 设置当一个安全站点企图加载来自一个不安全站点资源时WebView的行为
            mWebSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }


        // 缓存模式相关 （读取缓存）
        /**
         * LOAD_DEFAULT：默认的缓存使用模式。在进行页面前进或后退的操作时，如果缓存可用并未过期就优先加载缓存，否则从网络上加载数据。这样可以减少页面的网络请求次数。
         * LOAD_CACHE_ELSE_NETWORK：只要缓存可用就加载缓存，哪怕它们已经过期失效。如果缓存不可用就从网络上加载数据。
         * LOAD_NO_CACHE：不加载缓存，只从网络加载数据。
         * LOAD_CACHE_ONLY：不从网络加载数据，只从缓存加载数据。
         */
        // 用来设置WebView的缓存模式。当我们加载页面或从上一个页面返回的时候，会按照设置的缓存模式去检查并使用（或不使用）缓存
        if (CommonUtils.isNetworkAvailable(getBaseContext())) {
            // 根据cache-control决定是否从网络上取数据
            mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            // 没网，则从本地获取，即离线加载
            mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }


        // 缓存机制相关 （保存缓存）
        // 开启Application Caches 功能
        mWebSettings.setAppCacheEnabled(true);
        // 设置Application Caches 缓存目录
        // 这个路径必须是可以让app写入文件的。该方法应该只被调用一次，重复调用会被无视~
        String cacheDirPath = getFilesDir().getAbsolutePath() + APP_CACHE_DIRNAME;
        mWebSettings.setAppCachePath(cacheDirPath);
        // 开启 DOM storage API 功能
        mWebSettings.setDomStorageEnabled(true);
        // 开启 database storage API 功能
        mWebSettings.setDatabaseEnabled(true);


    }

    private long startTime = 0L;


    boolean loadingFinished = true;
    boolean redirect = false;

    private void initWebViewClient() {
        Log.d(TAG, "initWebViewClient");

        mWebView.setWebViewClient(new WebViewClient() {

            // Android 5.0之前的方法
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG, "shouldOverrideUrlLoading");

                if (!loadingFinished) {
                    redirect = true;
                }

                loadingFinished = false;

                // 根据协议的参数，判断是否是所需要的url
                // 一般根据scheme（协议格式） & authority（协议名）判断（前两个参数）
                // 假定传入进来的 url = "js://webview?arg1=111&arg2=222"（同时也是约定好的需要拦截的）
                Uri uri = Uri.parse(url);
                // 如果url的协议 = 预先约定的 js 协议
                // 就解析往下解析参数
                if (uri.getScheme().equals("js")) {
                    // 如果 authority  = 预先约定协议里的 WebView，即代表都符合约定的协议
                    // 所以拦截url,下面JS开始调用Android需要的方法
                    if (uri.getAuthority().equals("webview")) {
                        // 可以在协议上带有参数并传递到Android上
                        HashMap<String, String> params = new HashMap<>();
                        Set<String> collection = uri.getQueryParameterNames();
                        String arg1 = uri.getQueryParameter("arg1");
                        String arg2 = uri.getQueryParameter("arg2");
                        Toast.makeText(WebViewActivity.this, "arg1 = " + arg1 + ", arg2 = " + arg2, Toast.LENGTH_SHORT).show();
                    }

                    return true;
                }

                return false;
            }

            // Android 5.0之后的方法
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Log.d(TAG, "shouldOverrideUrlLoading 5.0");
                return this.shouldOverrideUrlLoading(view, request.getUrl().toString());
            }

            // Android 5.0之前的方法
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                Log.d(TAG, "shouldInterceptRequest");

                // 根据自定义逻辑，判断是否使用本地资源
                WebResourceResponse webResourceResponse = WebResourceHelper.getInstance().getCache(url);
                return webResourceResponse != null ? webResourceResponse : super.shouldInterceptRequest(view, url);
            }

            // Android 5.0之后的方法
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                Log.d(TAG, "shouldInterceptRequest 5.0");

                if (request.getMethod().toLowerCase().equals("get")) {
                    return this.shouldInterceptRequest(view, request.getUrl().toString());
                } else {
                    return super.shouldInterceptRequest(view, request);
                }
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                Log.d(TAG, "onLoadResource");

            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d(TAG, "onPageStarted");

                loadingFinished = false;
                //SHOW LOADING IF IT ISN'T ALREADY VISIBLE
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "onPageFinished");

                if (!redirect) {
                    loadingFinished = true;
                }

                if (loadingFinished && !redirect) {
                    //HIDE LOADING IT HAS FINISHED
                } else {
                    redirect = false;
                }
            }

            // Android 6.0之前的方法
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.d(TAG, "onReceivedError");

                // 断网或者网络连接超时
                if (errorCode == ERROR_HOST_LOOKUP || errorCode == ERROR_CONNECT || errorCode == ERROR_TIMEOUT) {
                    view.loadUrl("about:blank"); // 避免出现默认的错误界面
                    // 在这里显示自定义错误页
                    showErrorPage();
                }
            }

            // Android 6.0之后的方法
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.d(TAG, "onReceivedError 6.0");

                // 如果当前网络请求是为main frame创建的，则显示错误页
                if (request.isForMainFrame()) {
                    this.onReceivedError(view, error.getErrorCode(), error.getDescription().toString(), request.getUrl().toString());
                } else {
                    // main frame没有出错，正常显示
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                // 这个方法在6.0才出现
                int statusCode = errorResponse.getStatusCode();
                Log.d(TAG, "onReceivedHttpError code = " + statusCode);
                if (404 == statusCode || 500 == statusCode) {
                    view.loadUrl("about:blank");// 避免出现默认的错误界面
                    showErrorPage();
                }
            }

            @Override
            public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
                Log.d(TAG, "onReceivedSslError");

                final AlertDialog.Builder builder = new AlertDialog.Builder(WebViewActivity.this);
                builder.setMessage("SSL证书无效");
                builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.proceed();
                    }
                });
                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.cancel();
                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.show();
            }

            @Override
            public void onScaleChanged(WebView view, float oldScale, float newScale) {
                super.onScaleChanged(view, oldScale, newScale);
                Log.d(TAG, "onScaleChanged");

            }

            @Override
            public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                Log.d(TAG, "shouldOverrideKeyEvent");
                return super.shouldOverrideKeyEvent(view, event);
            }
        });
    }

    private void initWebChromeClient() {
        Log.d(TAG, "initWebChromeClient");

        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                Log.d(TAG, "onJsAlert");
                final AlertDialog.Builder builder = new AlertDialog.Builder(WebViewActivity.this);
                builder.setTitle("对话框")
                        .setMessage(message)
                        .setPositiveButton("确定", null);

                // 不需要绑定按键事件
                // 屏蔽keycode等于84之类的按键
                builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        Log.d(TAG, "onJsAlert keyCode==" + keyCode + "event=" + event);
                        return true;
                    }
                });
                // 禁止响应按back键的事件
                builder.setCancelable(false);
                AlertDialog dialog = builder.create();
                dialog.show();
                result.confirm();// 因为没有绑定事件，需要强行confirm,否则页面会变黑显示不了内容。
                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                Log.d(TAG, "onJsConfirm");
                final AlertDialog.Builder builder = new AlertDialog.Builder(WebViewActivity.this);
                builder.setTitle("对话框")
                        .setMessage(message)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm();
                            }
                        })
                        .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                result.cancel();
                            }
                        });

                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        result.cancel();
                    }
                });

                // 屏蔽keycode等于84之类的按键，避免按键后导致对话框消息而页面无法再弹出对话框的问题
                builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        Log.d(TAG, "onJsConfirm keyCode==" + keyCode + "event=" + event);
                        return true;
                    }
                });

                // 禁止响应按back键的事件
                builder.setCancelable(false);
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {
                Log.d(TAG, "onJsPrompt");
                final AlertDialog.Builder builder = new AlertDialog.Builder(WebViewActivity.this);

                builder.setTitle("对话框").setMessage(message);

                final EditText et = new EditText(view.getContext());
                et.setSingleLine();
                et.setText(defaultValue);
                builder.setView(et)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm(et.getText().toString());
                            }

                        })
                        .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                result.cancel();
                            }
                        });

                // 屏蔽keycode等于84之类的按键，避免按键后导致对话框消息而页面无法再弹出对话框的问题
                builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        Log.v(TAG, "onJsPrompt keyCode==" + keyCode + "event=" + event);
                        return true;
                    }
                });

                // 禁止响应按back键的事件
                builder.setCancelable(false);
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }

            @Override
            public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
                Log.d(TAG, "onJsBeforeUnload");

                return super.onJsBeforeUnload(view, url, message, result);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                Log.d(TAG, "newProgress = " + newProgress);
                if (mProgressView != null) {
                    if (newProgress == 100) {
                        //加载完毕进度条消失
                        mProgressView.setVisibility(View.GONE);
                    } else {
                        //更新进度
                        if (mProgressView.getVisibility() != View.VISIBLE) {
                            mProgressView.setVisibility(View.VISIBLE);
                        }
                        Log.d(TAG, "newProgress = " + newProgress);
                        mProgressView.setProgress(newProgress);
                    }
                }

            }


            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
                Log.d(TAG, "onReceivedIcon");
                mToolbar.setLogo(new BitmapDrawable(WebViewActivity.this.getResources(), icon));
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                Log.d(TAG, "onReceivedTitle");
                mToolbar.setTitle(title);

                // android 6.0 以下通过title获取
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    if (title.contains("404") || title.contains("500") || title.contains("Error")) {
                        view.loadUrl("about:blank"); // 避免出现默认的错误界面
                        // 在这里显示自定义错误页
                        showErrorPage();
                    }
                }
            }

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                super.onShowCustomView(view, callback);
            }

            @Override
            public void onHideCustomView() {
                super.onHideCustomView();
            }

            @Override
            public Bitmap getDefaultVideoPoster() {
                return super.getDefaultVideoPoster();
            }

            @Override
            public View getVideoLoadingProgressView() {
                return super.getVideoLoadingProgressView();
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
            }

            @Override
            public void onPermissionRequest(PermissionRequest request) {
                super.onPermissionRequest(request);
            }

            @Override
            public void onPermissionRequestCanceled(PermissionRequest request) {
                super.onPermissionRequestCanceled(request);
            }

            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                Log.d(TAG, "onCreateWindow");

                WebView newWebView = new WebView(view.getContext());
                view.addView(newWebView);
                newWebView.setWebViewClient(new WebViewClient());
                newWebView.setWebChromeClient(this);
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(newWebView);
                resultMsg.sendToTarget();
                return true;
            }

            @Override
            public void onCloseWindow(WebView window) {
                super.onCloseWindow(window);
            }
        });
    }

    /**
     * 避免内存泄漏，使用parent remove view，然后销毁webview
     */
    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");

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

    /**
     * 显示错误页面
     */
    public void showErrorPage() {
        Log.d(TAG, "showErrorPage");
        mErrorLayout.setVisibility(View.VISIBLE);
        mWebView.setVisibility(View.GONE);

    }


    private void initJavaScriptInterface() {
        if (mWebView != null) {
            mWebView.addJavascriptInterface(new JsTestInterface(), "test");
        }
    }

    private class JsTestInterface {
        public JsTestInterface() {
        }

        @JavascriptInterface
        public void jsiTestToast(String message) {
            Toast.makeText(WebViewActivity.this, "jsiTestToast: params = " + message, Toast.LENGTH_SHORT).show();
        }


        @JavascriptInterface
        public void jsiTestToast() {
            Toast.makeText(WebViewActivity.this, "jsiTestToast: no params", Toast.LENGTH_SHORT).show();
        }


        @JavascriptInterface
        public String jsiGetToastMessage() {
            Toast.makeText(WebViewActivity.this, "jsiGetToastMessage", Toast.LENGTH_SHORT).show();
            return "Toast Message";
        }

    }

}
