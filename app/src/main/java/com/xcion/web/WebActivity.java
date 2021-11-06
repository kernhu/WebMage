package com.xcion.web;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.xcion.web.bean.ItemInfo;
import com.xcion.web.floatview.FloatTitleView;
import com.xcion.webmage.WebMageView;
import com.xcion.webmage.capture.OnCaptureListener;
import com.xcion.webmage.download.entity.FileInfo;
import com.xcion.webmage.download.entity.ThreadInfo;
import com.xcion.webmage.download.listener.DownloadClient;
import com.xcion.webmage.hybrid.Hybrider;
import com.xcion.webmage.longpress.LongPressMenuControl;
import com.xcion.webmage.longpress.MenuItem;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;

/**
 * @Author: Kern Hu
 * @E-mail: sky580@126.com
 * @CreateDate: 2020/9/18 14:39
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/9/18 14:39
 * @Version: 1.0
 * @Description: java类作用描述
 * @UpdateRemark: 更新说明
 */
public class WebActivity extends BaseActivity {

    public static final String KEY_FUN = "key_fun";
    public static final String KEY_URL = "key_url";

    @BindView(R.id.web_container)
    LinearLayout mWebContainer;
    @BindView(R.id.float_title_view)
    FloatTitleView mFloatTitleView;
    WebMageView mWebMageView;
    ImageView mCaptureView;
    private String mFunMode;
    private String mLoadUrl;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_web);
        mLoadUrl = getIntent().getStringExtra(KEY_URL);
        mFunMode = getIntent().getStringExtra(KEY_FUN);
        mFunMode = mFunMode == null ? "" : mFunMode;

        initWebMage();
        initFloatTitle();
    }

    private void initFloatTitle() {
        //
        mFloatTitleView.setOnReloadListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebMageView.reload();
            }
        });

    }

    private void initWebMage() {

        LinearLayout.LayoutParams lllp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mWebMageView = new WebMageView(this);
        mWebMageView.loadUrl(mLoadUrl);
        mWebContainer.addView(mWebMageView, lllp);
        mWebMageView.setWebChromeClient(mWebChromeClient);
        mWebMageView.setWebViewClient(mWebViewClient);
        mWebMageView.setLongPressMenuControl(mMenuControl);

        switch (mFunMode) {
            case ItemInfo.FUN_LOAD_H5:

                break;
            case ItemInfo.FUN_VIDEO_PLAY:

                break;
            case ItemInfo.FUN_DEEP_LINK:

                break;
            case ItemInfo.FUN_LOCATION:

                break;
            case ItemInfo.FUN_DOWNLOAD:

                mWebMageView.setDownloadClient(mDownloadClient);

                break;
            case ItemInfo.FUN_UPLOAD:

                break;
            case ItemInfo.FUN_JS_NATIVE:

                break;
            case ItemInfo.FUN_NATIVE_JS:

                break;
            case ItemInfo.FUN_RESOURCE_REPLACE:

                break;
            case ItemInfo.FUN_URL_INTERCEPT:

                break;
            case ItemInfo.FUN_SNAPSHOOT:

                mWebMageView.post(new Runnable() {
                    @Override
                    public void run() {
                        setCaptureWindow();
                        mWebMageView.setOnCaptureListener(mOnCaptureListener);
                    }
                });


                break;
        }


        //JS注入
        mWebMageView.getHybrider().loadJavascript("javascript:callJS()");
        //
        mWebMageView.getHybrider().evaluateJavascript("javascript:callJS()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {

            }
        });

        //拦截JS
        mWebMageView.getHybrider().registerInterceptor(new Hybrider.HybridCallback() {
            @Override
            public void onCallback(String data) {
                switch (data) {
                    case "//baidu:":


                        break;
                }
            }
        }, "//baidu:", "//toutiao:", "helloJs:", "//webmage:");

        //映射js函数
        mWebMageView.getHybrider().addJavascriptInterface(null, "helloworld");

    }

    @Override
    protected void onPause() {
        mWebMageView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mWebMageView.onResume();
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        mWebMageView.onDestroy();
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        if (mWebMageView.onBackPressed()) {

        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mWebMageView.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mWebMageView.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**************************************************************************************************/
    /**************************************************************************************************/
    WebViewClient mWebViewClient = new WebViewClient() {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Log.e("sos", "测试代理模式的 shouldOverrideUrlLoading");
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.e("sos", "测试代理模式的 shouldOverrideUrlLoading");
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.e("sos", "测试代理模式的 onPageFinished");
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.e("sos", "测试代理模式的 onPageStarted");
        }
    };

    /**************************************************************************************************/
    /**************************************************************************************************/
    WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            mFloatTitleView.setReceiveTitle(title);
        }

        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            super.onReceivedIcon(view, icon);
            mFloatTitleView.setFavicon(icon);
        }
    };

    /**************************************************************************************************/
    /**************************************************************************************************/
    LongPressMenuControl mMenuControl = new LongPressMenuControl() {
        @Override
        public void onTextControl(WebMageView view, MenuItem.TEXT text, String result) {
            super.onTextControl(view, text, result);

        }
    };
    /**************************************************************************************************/
    /**************************************************************************************************/

    /**************************************************************************************************/
    /**************************************************************************************************/
    DownloadClient mDownloadClient = new DownloadClient() {


        @Override
        public void onJoinQueue(FileInfo fileInfo) {
            Log.i("sos", "onJoinQueue>>>" + fileInfo.toString());
        }

        @Override
        public void onStarted(FileInfo fileInfo) {
            Log.i("sos", "onStarted>>>" + fileInfo.toString());
        }

        @Override
        public void onDownloading(FileInfo fileInfo, ArrayList<ThreadInfo> threadInfo) {
            long size = 0;
            for (ThreadInfo info : threadInfo) {
                //size = size + info.getCurrent();
                Log.i("sos", "onDownloading>>>" + info.toString());
            }
            Log.i("sos", "onDownloading>>>" + fileInfo.toString());
        }

        @Override
        public void onStopped(FileInfo fileInfo) {
            Log.i("sos", "onStopped>>>" + fileInfo.toString());
        }

        @Override
        public void onCancelled(FileInfo fileInfo) {
            Log.i("sos", "onCancelled>>>" + fileInfo.toString());
        }

        @Override
        public void onCompleted(FileInfo fileInfo) {
            Log.i("sos", "onCompleted>>>" + fileInfo.toString());
            Toast.makeText(getApplicationContext(), fileInfo.getFileName() + "下载完成", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailure(FileInfo fileInfo) {
            Log.i("sos", "onFailure>>>" + fileInfo.toString());
            if (fileInfo.getState() == FileInfo.STATE_REPEATED) {
                Toast.makeText(getApplicationContext(), fileInfo.getFileName() + "已在下载", Toast.LENGTH_SHORT).show();
            }
        }
    };

    /****************************************************************************************************/

    private void setCaptureWindow() {
        mCaptureView = new ImageView(this);
        mCaptureView.setBackgroundColor(getColor(R.color.colorAccent));
        mCaptureView.setClickable(true);
        PopupWindow mPopupWindow = new PopupWindow(mCaptureView, 400, 520);

        mPopupWindow.setOutsideTouchable(false);
        mPopupWindow.setTouchable(false);
        mPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setFocusable(true);
        mPopupWindow.showAtLocation(mWebContainer, Gravity.RIGHT | Gravity.BOTTOM, 0, 0);

        ViewGroup.LayoutParams lp = mCaptureView.getLayoutParams();
        lp.height = 520;
        lp.width = 400;
        mCaptureView.setLayoutParams(lp);

        mCaptureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap = mWebMageView.getCapturePicture();
                if (bitmap != null) {
                    mCaptureView.setImageBitmap(bitmap);
                }
            }
        });
    }

    OnCaptureListener mOnCaptureListener = new OnCaptureListener() {
        @Override
        public void onCapture(Bitmap bitmap) {
            if (mCaptureView != null) {
                mCaptureView.setImageBitmap(bitmap);
            }
        }
    };

}
