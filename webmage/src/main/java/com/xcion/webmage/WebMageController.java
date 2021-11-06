package com.xcion.webmage;

import android.graphics.Bitmap;
import android.os.Message;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.xcion.webmage.download.listener.DownloadClient;

/**
 * @Author: Kern Hu
 * @E-mail: sky580@126.com
 * @CreateDate: 2020/9/18 14:57
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/9/18 14:57
 * @Version: 1.0
 * @Description: java类作用描述
 * @UpdateRemark: 更新说明
 */
public interface WebMageController {

    void loadUrl(String url);

    void loadData(String data, String mimeType, String encoding);

    void loadDataWithBaseURL(String baseUrl, String data, String mimeType, String encoding, String failUrl);

    void stopLoading();

    void reload();

    boolean canGoBack();

    void goBack();

    boolean canGoForward();

    void goForward();

    boolean canGoBackOrForward(int steps);

    void goBackOrForward(int steps);

    boolean pageUp(boolean top);

    boolean pageDown(boolean bottom);

    void clearView();

    float getScale();

    void setInitialScale(int scaleInPercent);

    void invokeZoomPicker();

    void requestFocusNodeHref(Message hrefMsg);

    void requestImageRef(Message msg);

    String getUrl();

    String getTitle();

    Bitmap getFavicon();

    int getProgress();

    int getContentHeight();

    void pauseTimers();


    void resumeTimers();

    void clearCache();

    void clearFormData();

    void clearHistory();

    void clearSslPreferences();

    String findAddress(String addr);

    void documentHasImages(Message response);

    void setWebViewClient(WebViewClient client);

    void setWebChromeClient(WebChromeClient client);

    void setDownloadClient(DownloadClient client);

    void addJavascriptInterface(Object obj, String interfaceName);

    View getZoomControls();

    boolean zoomIn();

    boolean zoomOut();

    /**************************************************************************/
    /******************************* 标题栏 ************************************/
    void onReceivedTitle(WebView view, String title);

    void onReceivedWebSource(WebView view, String url);

    void onProgressChanged(WebView view, int newProgress);

    /**************************************************************************/
    /**************************************************************************/

    /**************************************************************************/
    /***************************** 错误页事件 *********************************/
    void onErrorPagerReload(View view);

    void onErrorPagerCheckNetwork(View view);
    /**************************************************************************/
    /**************************************************************************/

    Bitmap getCapturePicture();
}
