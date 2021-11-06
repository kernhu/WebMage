package com.xcion.webmage;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.xcion.webmage.deeplink.DeeplinkScheduler;
import com.xcion.webmage.errorpage.ErrorPages;
import com.xcion.webmage.hybrid.Hybrider;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * @Author: Kern Hu
 * @E-mail: sky580@126.com
 * @CreateDate: 2020/9/19 14:43
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/9/19 14:43
 * @Version: 1.0
 * @Description: java类作用描述
 * @UpdateRemark: 更新说明
 */
public class WebMageViewClient extends WebViewClient {

    private static final String TAG = "WebMageViewClient";
    private WebMageController controller;
    private Activity activity;

    private Hybrider.HybridCallback mHybridCallback;
    private List<String> mJsTarget;

    private DeeplinkScheduler mDeeplinkScheduler;
    private ErrorPages mErrorPages;
    private boolean hasError;
    private boolean isHardware = false;

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setController(WebMageController controller) {
        this.controller = controller;
    }

    public void setRegisterInterceptor(List<String> jsTargets, Hybrider.HybridCallback hybridCallback) {
        this.mJsTarget = jsTargets;
        this.mHybridCallback = hybridCallback;
    }

    public void destroy() {
        if (mDeeplinkScheduler != null) {
            mDeeplinkScheduler = null;
        }

        if (mErrorPages != null) {
            mErrorPages.onDestroy();
            mErrorPages = null;
        }
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return shouldOverrideUrlLoading(view, request.getUrl().toString());
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url.startsWith(WebMageView.HTTP) || url.startsWith(WebMageView.HTTPS)) {
            //URL request and load
            return super.shouldOverrideUrlLoading(view, url);
        } else if (url.startsWith(WebMageView.ABOUT_BLANK)) {
            //default about blank
            return true;
        } else if (mJsTarget != null && mJsTarget.contains(url)) {
            //js call native
            if (mHybridCallback != null) {
                mHybridCallback.onCallback(url);
            }
            return true;
        } else {
            //other deep link
            return true;
        }
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        super.onReceivedSslError(view, handler, error);
        handler.proceed();// 接受所有网站的证书
    }

    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        Log.e("sos", "onReceivedHttpError>>>" + errorResponse.toString());
//        /*****************************************************/
//        Log.e("sos", "onReceivedHttpError>>>" + errorResponse.getStatusCode());
//        //show  error pager
//        this.hasError = true;
//        if (mErrorPager == null) {
//            mErrorPager = ErrorPager.getInstance(activity);
//        }
//        mErrorPager.setErrorCode(errorResponse.getStatusCode())
//                .setDescription("http error status code" + errorResponse.getStatusCode())
//                .setController(controller)
//                .setWebView(view)
//                .setViewParent((ViewGroup) view.getParent())
//                .show();
//        /*****************************************************/
        super.onReceivedHttpError(view, request, errorResponse);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        this.onReceivedError(view, error.getErrorCode(), error.getDescription().toString(), request.getUrl().toString());
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        Log.e("sos", "onReceivedError>>>" + errorCode + ";;;description>>>" + description);
        Log.e("sos", "onReceivedError>>>" + "failingUrl==" + failingUrl);
        Log.e("sos", "onReceivedError>>>" + "URL==" + view.getUrl());
        Log.e("sos", "onReceivedError>>>" + "OriginalUrl==" + view.getOriginalUrl());
        /*****************************************************/
        if (WebViewClient.ERROR_HOST_LOOKUP == errorCode) {
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
        super.onReceivedError(view, errorCode, description, failingUrl);
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
            if (activity != null)
                activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                        WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        }

        this.hasError = false;
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
        super.onPageStarted(view, url, favicon);

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
        super.onPageFinished(view, url);
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
        WebResourceResponse response = checkLocalWebResourceResponse(uri);
        if (response != null) {
            return response;
        }
        return super.shouldInterceptRequest(view, url);
    }

    /**
     * 拦截网络资源，获取本地资源
     *
     * @param uri
     * @return
     */
    private WebResourceResponse checkLocalWebResourceResponse(Uri uri) {
        WebResourceResponse resourceResponse = null;
        String path = uri.getPath();
        if (!TextUtils.isEmpty(path)) {
            path = path.substring(1);
        }
        Log.i(TAG, "shouldInterceptRequest>>>" + path);
        try {
            InputStream input = activity.getAssets().open(path);
            if (input != null) {
                String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(path.substring(path.lastIndexOf(".") + 1));
                HashMap<String, String> header = new HashMap<>();
                header.put("Access-Control-Allow-Origin", "*");
                header.put("Access-Control-Allow-Headers", "Content-Type");
                resourceResponse = new WebResourceResponse(mimeType, "", 200, "ok", header, input);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resourceResponse;
    }
    @Override
    public void onLoadResource(WebView view, String url) {
        super.onLoadResource(view, url);
        Log.i(TAG, "onLoadResource>>>" + url);
    }

    /***********************************************************************************************/
    /***********************************************************************************************/
}
