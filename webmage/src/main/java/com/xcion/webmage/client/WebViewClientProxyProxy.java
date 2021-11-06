package com.xcion.webmage.client;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.ClientCertRequest;
import android.webkit.HttpAuthHandler;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.SafeBrowsingResponse;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.xcion.webmage.WebMage;
import com.xcion.webmage.WebMageController;
import com.xcion.webmage.WebMageView;
import com.xcion.webmage.deeplink.DeeplinkScheduler;
import com.xcion.webmage.errorpage.ErrorPages;
import com.xcion.webmage.hybrid.Hybrider;
import com.xcion.webmage.resource.ResourceInterceptor;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * Author: Kern
 * E-mail: sky580@126.com
 * DateTime: 2021/10/7  23:04
 * Intro:This is the proxy class of  WebViewClient
 */
public class WebViewClientProxyProxy extends WebViewClient implements IClientProxy {

    private Activity activity;
    private WebViewClient client;
    private WebMageController controller;
    private ResourceInterceptor mResourceInterceptor = new ResourceInterceptor();
    private DeeplinkScheduler mDeeplinkScheduler = new DeeplinkScheduler();

    private Hybrider.HybridCallback mHybridCallback;
    private List<String> mJsTarget;
    private ErrorPages mErrorPages;
    private boolean hasError;
    private boolean isHardware = false;

    public WebViewClientProxyProxy(Activity activity, WebViewClient client, WebMageController controller) {
        this.activity = activity;
        this.client = client;
        this.controller = controller;
    }

    @Override
    public void onDestroy() {

        if (mDeeplinkScheduler != null) {
            mDeeplinkScheduler.onDestroy();
            mDeeplinkScheduler = null;
        }

        if (mResourceInterceptor != null) {
            mResourceInterceptor.onDestroy();
            mResourceInterceptor = null;
        }

    }

    @Override
    public void onTrimMemory(int level) {

    }

    @Override
    public void onLowMemory() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return shouldOverrideUrlLoading(view, request.getUrl().toString());
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.i(WebMage.TAG, "LOADING_URL=" + url);
        if (url.startsWith(WebMageView.HTTP) || url.startsWith(WebMageView.HTTPS)) {
            //URL request and load
            return super.shouldOverrideUrlLoading(view, url);
        } else if (url.startsWith(WebMageView.ABOUT_BLANK)) {
            //default about blank
            return true;
        } else {
            //other deep link
            if (WebMage.getWebOptions().isDeepLinkEnable()) {
                return mDeeplinkScheduler
                        .with(activity)
                        .setTooltip(WebMage.getWebOptions().isDeepLinkTooltip())
                        .setDeeplink(url)
                        .setAnchorView(view)
                        .commit();
            }
            return true;
        }
        //  return mWebViewClient.shouldOverrideUrlLoading(view, url);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        if (controller != null) {
            controller.onReceivedWebSource(view, url);
        }

        if (!isHardware) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                isHardware = true;
            }
            if (activity != null) {
                activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                        WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
            }
        }

        this.hasError = false;
        /*****************************************************/
        //destroy error pager
        if (mErrorPages != null) {
            mErrorPages.dismiss();
            mErrorPages = null;
        } else {
            ViewGroup viewGroup = (ViewGroup) view.getParent();
            if (viewGroup != null) {
                if (viewGroup.indexOfChild(view) != -1) {
                    view.bringToFront();
                } else {
                    viewGroup.addView(view);
                }
            }
        }

        /*****************************************************/
        client.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        if (isHardware) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                isHardware = false;
            }
        }

        /*****************************************************/
        //destroy error pager
        if (!hasError) {
            if (mErrorPages != null) {
                mErrorPages.dismiss();
                mErrorPages = null;
            } else {
                ViewGroup viewGroup = (ViewGroup) view.getParent();
                if (viewGroup != null) {
                    if (viewGroup.indexOfChild(view) != -1) {
                        view.bringToFront();
                    } else {
                        viewGroup.addView(view);
                    }
                }
            }
        }
        /*****************************************************/
        // client.onPageFinished(view, url);
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        client.onLoadResource(view, url);
    }

    @Override
    public void onPageCommitVisible(WebView view, String url) {
        client.onPageCommitVisible(view, url);
    }

    /***********************************************************************************************/
    /**********************************  拦截网络资源，加载本地资源  *********************************/
    /***********************************************************************************************/
    @Nullable
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        return this.shouldInterceptRequest(view, request.getUrl().toString());
    }

    @Nullable
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        Uri uri = Uri.parse(url);
        WebResourceResponse response = mResourceInterceptor.onIntercept(activity, uri);
        if (response != null) {
            return response;
        }
        return client.shouldInterceptRequest(view, url);
    }

    @Override
    public void onTooManyRedirects(WebView view, Message cancelMsg, Message continueMsg) {
        client.onTooManyRedirects(view, cancelMsg, continueMsg);
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        this.onReceivedError(view, error.getErrorCode(), error.getDescription().toString(), request.getUrl().toString());
        client.onReceivedError(view, request, error);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        Log.e("sos", "onReceivedError>>>" + errorCode + ";;;description>>>" + description + ";;failingUrl>>>" + failingUrl);
        /*****************************************************/
        //-2;;;description>>>net::ERR_INTERNET_DISCONNECTED
        if (WebViewClient.ERROR_HOST_LOOKUP == errorCode ||
                WebViewClient.ERROR_TIMEOUT == errorCode ||
                WebViewClient.ERROR_UNSUPPORTED_SCHEME == errorCode) {

            //show  error pager
            this.hasError = true;
            if (mErrorPages == null) {
                mErrorPages = ErrorPages.getInstance(activity);
            }
            mErrorPages.setErrorCode(errorCode)
                    .setDescription(description)
                    .setController(controller)
                    .setWebView(view)
                    .setViewParent((ViewGroup) view.getParent())
                    .show();

        }

        /*****************************************************/
        client.onReceivedError(view, errorCode, description, failingUrl);
    }

    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        Log.e("sos", "onReceivedHttpError>>>" + errorResponse.getStatusCode() + ";;failingUrl>>>" + request.getUrl());

        client.onReceivedHttpError(view, request, errorResponse);
    }

    @Override
    public void onFormResubmission(WebView view, Message dontResend, Message resend) {
        client.onFormResubmission(view, dontResend, resend);
    }

    @Override
    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
        client.doUpdateVisitedHistory(view, url, isReload);
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        client.onReceivedSslError(view, handler, error);
        handler.proceed();// 接受所有网站的证书
    }

    @Override
    public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
        client.onReceivedClientCertRequest(view, request);
    }

    @Override
    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
        client.onReceivedHttpAuthRequest(view, handler, host, realm);
    }

    @Override
    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
        return client.shouldOverrideKeyEvent(view, event);
    }

    @Override
    public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
        client.onUnhandledKeyEvent(view, event);
    }


    @Override
    public void onScaleChanged(WebView view, float oldScale, float newScale) {
        client.onScaleChanged(view, oldScale, newScale);
    }

    @Override
    public void onReceivedLoginRequest(WebView view, String realm, @Nullable String account, String args) {
        client.onReceivedLoginRequest(view, realm, account, args);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
        return client.onRenderProcessGone(view, detail);
    }

    @RequiresApi(api = Build.VERSION_CODES.O_MR1)
    @Override
    public void onSafeBrowsingHit(WebView view, WebResourceRequest request, int threatType, SafeBrowsingResponse callback) {
        client.onSafeBrowsingHit(view, request, threatType, callback);
    }
}
