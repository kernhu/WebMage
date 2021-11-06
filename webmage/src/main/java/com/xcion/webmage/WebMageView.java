package com.xcion.webmage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.xcion.webmage.capture.Capturer;
import com.xcion.webmage.capture.OnCaptureListener;
import com.xcion.webmage.client.WebChromeClientProxyProxy;
import com.xcion.webmage.client.WebViewClientProxyProxy;
import com.xcion.webmage.download.Downloader;
import com.xcion.webmage.download.entity.FileInfo;
import com.xcion.webmage.download.listener.DownloadClient;
import com.xcion.webmage.hybrid.Hybrider;
import com.xcion.webmage.hybrid.JSCallback;
import com.xcion.webmage.indicator.ProgressIndicator;
import com.xcion.webmage.longpress.LongPressMenuControl;
import com.xcion.webmage.longpress.PopupMenuWindow;
import com.xcion.webmage.utils.DensityUtil;
import com.xcion.webmage.utils.NetworkUtils;
import com.xcion.webmage.utils.UnicodeUtils;
import com.xcion.webmage.utils.VibratorUtils;
import com.xcion.webmage.variate.IndicatorOptions;
import com.xcion.webmage.view.DampingLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @Author: Kern Hu
 * @E-mail: sky580@126.com
 * @CreateDate: 2020/9/17 20:53
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/9/17 20:53
 * @Version: 1.0
 * @Description: java类作用描述
 * @UpdateRemark: 更新说明
 */
public class WebMageView extends DampingLayout implements ILifecycle, WebMageController {

    public String TIPS = "\\u5df2\\u542f\\u7528\\u0057\\u0065\\u0062\\u004d\\u0061\\u0067\\u0065\\u5185\\u6838";
    public String WEB_MAGE = "\\u0057\\u0065\\u0062\\u004d\\u0061\\u0067\\u0065";
    public String SOURCE_HEADER = "\\u7f51\\u9875\\u7531\\u0020";
    public String SOURCE_ENDER = "\\u0020\\u63d0\\u4f9b";

    public static final String HTTP = "http://";
    public static final String HTTPS = "https://";
    public static final String ABOUT_BLANK = "about:blank";

    private WebChromeClientProxyProxy mWebChromeClientProxy;
    private WebViewClientProxyProxy mWebViewClientProxy;
    private WebView mWebView;

    private Capturer mCapturer;

    private Hybrider mHybrider = null;

    private ProgressIndicator mProgressIndicator;
    private LongPressMenuControl mLongPressMenuControl;

    private Downloader mDownloader = null;
    private DownloadClient mDownloadClient = null;

    public WebMageView(@NonNull Context context) {
        this(context, null);
    }

    public WebMageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WebMageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        onCreateWebCore();
        onCreateWebSetting();
        onCreateBrand();
        onCreateIndicator();
        onCreateDownloader();
        mCapturer = new Capturer(getContext());
    }

    @Override
    protected void onBindData() {
        super.onBindData();
    }

    @Override
    protected void onCreateDamping() {
        super.onCreateDamping();
    }

    @Override
    protected void onCreateBrand() {
        super.onCreateBrand();
        setDampingEnable(WebMage.getWebOptions().isDampingEnable());
        setSupplierIcon(getContext().getDrawable(R.drawable.webmage_logo)/*WebMage.getWebOptions().getSponsorLogo()*/);
        setSupplier(UnicodeUtils.decodeUnicode(TIPS));
    }

    @Override
    protected void onCreateIndicator() {
        super.onCreateIndicator();
        FrameLayout.LayoutParams mIndicatorParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, DensityUtil.dip2px(getContext(), IndicatorOptions.getHeight()));
        mProgressIndicator = new ProgressIndicator(getContext());
        mProgressIndicator.setProgressColor(IndicatorOptions.getProgressColor());
        this.addView(mProgressIndicator, mIndicatorParams);
        mProgressIndicator.bringToFront();
    }

    @Override
    protected void onCreateWebCore() {
        super.onCreateWebCore();
        /**
         * WebView Core create and add into parent ViewGroup
         * */
        FrameLayout.LayoutParams mWebParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mWebView = new WebView(getContext());
        if (mWebView.getParent() != null) {
            ((ViewGroup) mWebView.getParent()).removeAllViews();
        }
        this.addView(mWebView, mWebParams);

        /**
         * Hybrider init
         * */
        mHybrider = new Hybrider(jsCallback);

        /**
         *  WebView Core's setting
         * */
        mWebView.setLongClickable(true);
        mWebView.setFocusableInTouchMode(true);
        mWebView.setFocusable(true);
        mWebView.setFocusableInTouchMode(true);

        /**
         *  WebView Core's listener
         * */
        if (WebMage.getDownloadOptions().isDownloadEnable()) {
            mWebView.setDownloadListener(mDownloadListener);
        }
        if (WebMage.getWebOptions().isCmenuEnable()) {
            mWebView.setOnLongClickListener(mLongClickListener);
        }
    }

    @Override
    protected void onCreateWebSetting() {
        super.onCreateWebSetting();
        //允许Https
        HttpsTrustManager.getInstance().allowAllSSL();
        //scroll bar setting
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setScrollbarFadingEnabled(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        mWebView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        //
        CookieManager cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(mWebView, cookieManager.acceptCookie());
        } else {

        }
        //
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(WebMage.getWebOptions().isDebuggingEnabled());
        }

        /***** web setting *****/
        WebMageSetting.getInstance()
                .setContext(getContext())
                .setWebView(mWebView)
                .setBlockImage(WebMage.getWebOptions().isBlockImage())
                .setCanZoom(WebMage.getWebOptions().isCanZoom())
                .setTextZoom(WebMage.getWebOptions().getTextZoom())
                .setGeolocation(WebMage.getWebOptions().getGeolocation())
                .setSavePassword(WebMage.getWebOptions().isSavePassword())
                .setStoragePath(WebMage.getWebOptions().getStoragePath())
                .setStrategy(WebMage.getWebOptions().getSpeedStrategy())
                .setUserAgent(WebMage.getWebOptions().getUserAgent())
                .setStamp(WEB_MAGE)
                .build();

        setWebChromeClient(new WebChromeClient());
        setWebViewClient(new WebViewClient());
    }

    @Override
    protected void onCreateDownloader() {
        super.onCreateDownloader();
        /***** web download init. *****/
        mDownloader = new Downloader(getContext());
        mDownloader.setOptions(WebMage.getDownloadOptions());
    }

    /******************************************************************************************************************************/
    /******************************************************************************************************************************/

    DownloadListener mDownloadListener = new DownloadListener() {
        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            mDownloader
                    .setTask(new FileInfo(url))
                    .bind();
        }
    };

    /******************************************************************************************************************************/
    /******************************************************************************************************************************/

    OnLongClickListener mLongClickListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            WebView.HitTestResult result = ((WebView) v).getHitTestResult();
            if (null == result) {
                return false;
            }
            PopupMenuWindow
                    .getInstance(getContext())
                    .setWebView((WebView) v)
                    .setPointX(mPointX)
                    .setPointY(mPointY)
                    .setMenuControl(mLongPressMenuControl)
                    .build();

            if (WebMage.getWebOptions().isVibratorEnable()) {
                VibratorUtils.setVibrator(getContext(), VibratorUtils.Effect.LONG_PRESS);
            }

            return true;
        }
    };

    @Override
    public void onPause() {
        if (mWebView != null) {
            mWebView.pauseTimers();
            mWebView.onPause();
        }
    }

    @Override
    public void onResume() {
        if (mWebView != null) {
            mWebView.resumeTimers();
            mWebView.onResume();
        }
    }

    @Override
    public void onStop() {
        if (mWebView != null) {
            mWebView.stopLoading();
        }
    }

    @Override
    public void onDestroy() {

        if (mWebChromeClientProxy != null) {
            mWebChromeClientProxy.onDestroy();
            mWebChromeClientProxy = null;
        }

        if (mWebViewClientProxy != null) {
            mWebViewClientProxy.onDestroy();
            mWebViewClientProxy = null;
        }

        if (mWebView != null) {
            mWebView.setWebChromeClient(null);
            mWebView.setWebViewClient(null);
            mWebView.destroy();
        }

        if (mDownloader != null) {
            mDownloader.unBind();
        }
    }

    @Override
    public void onTrimMemory(int level) {
        if (mWebChromeClientProxy != null) {
            mWebChromeClientProxy.onTrimMemory(level);
        }

        if (mWebViewClientProxy != null) {
            mWebViewClientProxy.onTrimMemory(level);
        }
    }

    @Override
    public void onLowMemory() {
        if (mWebChromeClientProxy != null) {
            mWebChromeClientProxy.onLowMemory();
        }

        if (mWebViewClientProxy != null) {
            mWebViewClientProxy.onLowMemory();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mWebChromeClientProxy != null) {
            mWebChromeClientProxy.onActivityResult(requestCode, resultCode, data);
        }

        if (mWebViewClientProxy != null) {
            mWebViewClientProxy.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mWebChromeClientProxy != null) {
            mWebChromeClientProxy.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        if (mWebViewClientProxy != null) {
            mWebViewClientProxy.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        if (mDownloader != null) {
            mDownloader.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public boolean onBackPressed() {
        //
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Hybrider getHybrider() {
        return mHybrider;
    }

    @Override
    public void loadUrl(String url) {
        if (mWebView != null) {
            mWebView.loadUrl(url);
        }
    }

    @Override
    public void loadData(String data, String mimeType, String encoding) {
        if (mWebView != null) {
            mWebView.loadData(data, mimeType, encoding);
        }
    }

    @Override
    public void loadDataWithBaseURL(String baseUrl, String data, String mimeType, String encoding, String failUrl) {
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, failUrl);
        }
    }

    @Override
    public void stopLoading() {
        if (mWebView != null) {
            mWebView.stopLoading();
        }
    }

    @Override
    public void reload() {
        if (mWebView != null) {
            mWebView.reload();
        }
    }

    @Override
    public boolean canGoBack() {
        if (mWebView != null) {
            return mWebView.canGoBack();
        }
        return false;
    }

    @Override
    public void goBack() {
        if (mWebView != null) {
            mWebView.goBack();
        }
    }

    @Override
    public boolean canGoForward() {
        if (mWebView != null) {
            return mWebView.canGoForward();
        }
        return false;
    }

    @Override
    public void goForward() {
        if (mWebView != null) {
            mWebView.goForward();
        }
    }

    @Override
    public boolean canGoBackOrForward(int steps) {
        if (mWebView != null) {
            return mWebView.canGoBackOrForward(steps);
        }
        return false;
    }

    @Override
    public void goBackOrForward(int steps) {
        if (mWebView != null) {
            mWebView.goBackOrForward(steps);
        }
    }

    @Override
    public boolean pageUp(boolean top) {
        if (mWebView != null) {
            return mWebView.pageUp(top);
        }
        return false;
    }

    @Override
    public boolean pageDown(boolean bottom) {
        if (mWebView != null) {
            return mWebView.pageDown(bottom);
        }
        return false;
    }

    @Override
    public void clearView() {
        if (mWebView != null) {
            mWebView.clearView();
        }
    }

    @Override
    public Bitmap getCapturePicture() {
        return mCapturer.capture(this);
    }

    @Override
    public float getScale() {
        if (mWebView != null) {
            return mWebView.getScale();
        }
        return 0;
    }

    @Override
    public void setInitialScale(int scaleInPercent) {
        if (mWebView != null) {
            mWebView.setInitialScale(scaleInPercent);
        }
    }

    @Override
    public void invokeZoomPicker() {
        if (mWebView != null) {
            mWebView.invokeZoomPicker();
        }
    }

    @Override
    public void requestFocusNodeHref(Message hrefMsg) {
        if (mWebView != null) {
            mWebView.requestFocusNodeHref(hrefMsg);
        }
    }

    @Override
    public void requestImageRef(Message msg) {
        if (mWebView != null) {
            mWebView.requestImageRef(msg);
        }
    }

    @Override
    public String getUrl() {
        if (mWebView != null) {
            return mWebView.getUrl();
        }
        return null;
    }

    @Override
    public String getTitle() {
        if (mWebView != null) {
            return mWebView.getTitle();
        }
        return null;
    }

    @Override
    public Bitmap getFavicon() {
        if (mWebView != null) {
            return mWebView.getFavicon();
        }
        return null;
    }

    @Override
    public int getProgress() {
        if (mWebView != null) {
            return mWebView.getProgress();
        }
        return 0;
    }

    @Override
    public int getContentHeight() {
        if (mWebView != null) {
            return mWebView.getContentHeight();
        }
        return 0;
    }

    @Override
    public void pauseTimers() {
        if (mWebView != null) {
            mWebView.pauseTimers();
        }
    }

    @Override
    public void resumeTimers() {
        if (mWebView != null) {
            mWebView.resumeTimers();
        }
    }

    @Override
    public void clearCache() {
    }

    @Override
    public void clearFormData() {
        if (mWebView != null) {
            mWebView.clearFormData();
        }
    }

    @Override
    public void clearHistory() {
        if (mWebView != null) {
            mWebView.clearHistory();
        }
    }

    @Override
    public void clearSslPreferences() {
        if (mWebView != null) {
            mWebView.clearSslPreferences();
        }
    }

    @Override
    public String findAddress(String addr) {
        if (mWebView != null) {
            return mWebView.findAddress(addr);
        }
        return null;
    }

    @Override
    public void documentHasImages(Message response) {
        if (mWebView != null) {
            mWebView.documentHasImages(response);
        }
    }

    @Override
    public void setWebViewClient(WebViewClient client) {
        mWebView.setWebViewClient(mWebViewClientProxy = new WebViewClientProxyProxy((Activity) getContext(), client, this));
    }


    @Override
    public void setWebChromeClient(WebChromeClient client) {
        mWebView.setWebChromeClient(mWebChromeClientProxy = new WebChromeClientProxyProxy((Activity) getContext(), client, this));
    }


    @Override
    public void setDownloadClient(DownloadClient client) {
        if (client != null && mDownloader != null) {
            mDownloader.setDownloadClient(client);
        }
    }


    @SuppressLint("JavascriptInterface")
    @Override
    public void addJavascriptInterface(Object obj, String interfaceName) {
        if (mWebView != null) {
            mWebView.addJavascriptInterface(obj, interfaceName);
        }
    }

    @Override
    public View getZoomControls() {

        return null;
    }

    @Override
    public boolean zoomIn() {
        if (mWebView != null) {
            return mWebView.zoomIn();
        }
        return false;
    }

    @Override
    public boolean zoomOut() {
        if (mWebView != null) {
            return mWebView.zoomOut();
        }
        return false;
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(UnicodeUtils.decodeUnicode(SOURCE_HEADER)).append(Uri.parse(view.getUrl()).getHost()).append(UnicodeUtils.decodeUnicode(SOURCE_ENDER));
        setWebSource(buffer.toString());
    }

    @Override
    public void onReceivedWebSource(WebView view, String url) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(UnicodeUtils.decodeUnicode(SOURCE_HEADER)).append(Uri.parse(url).getHost()).append(UnicodeUtils.decodeUnicode(SOURCE_ENDER));
        setWebSource(buffer.toString());
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        mProgressIndicator.setProgress(newProgress);
    }

    /**************************************************************************************************/
    @Override
    public void onErrorPagerReload(View view) {
        reload();
    }

    @Override
    public void onErrorPagerCheckNetwork(View view) {
        if (!NetworkUtils.isWifiConnected(getContext())) {
            getContext().startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        } else if (!NetworkUtils.isGprsConnected(getContext())) {
            getContext().startActivity(new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS));
        } else {
            getContext().startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
        }
    }

    /**************************************************************************************************/
    /**************************************************************************************************/
    /**************************************************************************************************/
    public void setLongPressMenuControl(LongPressMenuControl longPressMenuControl) {
        this.mLongPressMenuControl = longPressMenuControl;
    }

    public void setOnCaptureListener(OnCaptureListener captureListener) {
//        if (mWebMageChromeClient != null) {
//            mWebMageChromeClient.setOnCaptureListener(mCapturer, captureListener);
//        }
    }

    /**************************************************************************************************/
    /**************************************************************************************************/
    /**************************************************************************************************/
    JSCallback jsCallback = new JSCallback() {
        @Override
        public void onCallback(String script) {
            if (mWebView != null) {
                mWebView.loadUrl(script);
            }
        }

        @Override
        public void onCallback(String script, ValueCallback<String> resultCallback) {
            if (mWebView != null) {
                mWebView.evaluateJavascript(script, resultCallback);
            }
        }

        @Override
        public void onCallback(Hybrider.HybridCallback callback, String... jsTargets) {
//            if (mWebMageViewClient != null) {
//                mWebMageViewClient.setRegisterInterceptor(Arrays.asList(jsTargets), callback);
//            }
        }

        @SuppressLint("JavascriptInterface")
        @Override
        public void onCallback(Object obj, String interfaceName) {
            if (mWebView != null) {
                mWebView.addJavascriptInterface(obj, interfaceName);
            }
        }
    };
}
