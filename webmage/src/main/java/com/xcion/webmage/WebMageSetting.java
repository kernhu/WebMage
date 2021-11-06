package com.xcion.webmage;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.xcion.webmage.utils.NetworkUtils;
import com.xcion.webmage.utils.UnicodeUtils;


/**
 * @Author: Kern Hu
 * @E-mail: sky580@126.com
 * @CreateDate: 2020/9/18 16:17
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/9/18 16:17
 * @Version: 1.0
 * @Description: java类作用描述
 * @UpdateRemark: 更新说明
 */
public class WebMageSetting {

    private Context context;
    private WebView webView;
    private boolean savePassword;
    private boolean canZoom;
    private int textZoom;
    private boolean blockImage;
    private Geolocation geolocation;
    private SpeedStrategy strategy;
    private String[] userAgent;
    private String storagePath;
    private String stamp;

    public static WebMageSetting getInstance() {
        return new WebMageSetting();
    }

    public WebMageSetting setContext(Context context) {
        this.context = context;
        return this;
    }

    public WebMageSetting setWebView(WebView webView) {
        this.webView = webView;
        return this;
    }

    public WebMageSetting setSavePassword(boolean savePassword) {
        this.savePassword = savePassword;
        return this;
    }

    public WebMageSetting setCanZoom(boolean canZoom) {
        this.canZoom = canZoom;
        return this;
    }

    public WebMageSetting setTextZoom(int textZoom) {
        this.textZoom = textZoom;
        return this;
    }

    public WebMageSetting setBlockImage(boolean blockImage) {
        this.blockImage = blockImage;
        return this;
    }

    public WebMageSetting setGeolocation(Geolocation geolocation) {
        this.geolocation = geolocation;
        return this;
    }

    public WebMageSetting setStrategy(SpeedStrategy strategy) {
        this.strategy = strategy;
        return this;
    }

    public WebMageSetting setUserAgent(String... userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public WebMageSetting setStoragePath(String storagePath) {
        this.storagePath = storagePath;
        return this;
    }

    public WebMageSetting setStamp(String stamp) {
        this.stamp = stamp;
        return this;
    }

    public void build() {
        WebSettings settings = webView.getSettings();
        setConfig(settings);
    }


    private void setConfig(WebSettings settings) {

        //  webview settings  config
        settings.setAllowContentAccess(true);
        settings.setSaveFormData(false);
        settings.setUseWideViewPort(true);
        settings.setPluginState(WebSettings.PluginState.ON_DEMAND);
        //allow add js
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        // Enable the built-in zoom
        settings.setBuiltInZoomControls(true);
        //
        settings.setDomStorageEnabled(true);
        //
        settings.setSupportZoom(true);
        settings.setSavePassword(savePassword);
        //提高渲染的优先级
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        //跨域问题适配
        settings.setAllowUniversalAccessFromFileURLs(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //适配5.0不允许http和https混合使用情况
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            // webview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // webview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            // webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        settings.setTextZoom(textZoom);//default 100
        //离线缓存
        settings.setDatabaseEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setLoadsImagesAutomatically(true);
        //use network if has network, else use cache first;
        settings.setCacheMode(NetworkUtils.isConnected(context) ? WebSettings.LOAD_DEFAULT : WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //
        settings.setSupportMultipleWindows(false);
        // 是否阻塞加载网络图片  协议http or https
        settings.setBlockNetworkImage(blockImage);
        // 允许加载本地文件html  file协议
        settings.setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // 通过 file url 加载的 Javascript 读取其他的本地文件 .建议关闭
            settings.setAllowFileAccessFromFileURLs(false);
            // 允许通过 file url 加载的 Javascript 可以访问其他的源，包括其他的文件和 http，https 等其他的源
            settings.setAllowUniversalAccessFromFileURLs(false);
        }

        //
        settings.setLayoutAlgorithm((Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) ? WebSettings.LayoutAlgorithm.SINGLE_COLUMN : WebSettings.LayoutAlgorithm.NORMAL);
        //
        settings.setLoadWithOverviewMode(true);
        settings.setNeedInitialFocus(true);
        settings.setDefaultTextEncodingName("utf-8");//设置编码格式

        //缓存文件最大值
        settings.setAppCacheMaxSize(Long.MAX_VALUE);
        //location
        if (geolocation == Geolocation.ALWAYS || geolocation == Geolocation.ASK_ME) {
            settings.setGeolocationEnabled(true);
            String dir = context.getDir("database", Context.MODE_PRIVATE).getPath();
            settings.setGeolocationDatabasePath(dir);
        } else {
            settings.setGeolocationEnabled(false);
        }

        //
        //String dir = context.getCacheDir().getAbsolutePath() + storagePath;
        String dir = context.getDir("database", Context.MODE_PRIVATE).getPath();
        //设置定位数据库路径  api19 已经废弃,这里只针对 webkit 起作用
        settings.setDatabasePath(dir);
        settings.setAppCachePath(dir);

        /// M: Add to disable overscroll mode
        PackageManager pm = context.getPackageManager();
        boolean supportsMultiTouch = pm.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH) || pm.hasSystemFeature(PackageManager.FEATURE_FAKETOUCH_MULTITOUCH_DISTINCT);
        settings.setDisplayZoomControls(!supportsMultiTouch);

        // config user agent
        settings.setUserAgentString(settings.getUserAgentString().concat(UnicodeUtils.decodeUnicode(stamp)));
        if (userAgent != null) {
            for (String ua : userAgent) {
                settings.setUserAgentString(settings.getUserAgentString().concat(ua));
            }
        }
    }

}
