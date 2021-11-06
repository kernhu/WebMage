package com.xcion.webmage;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.xcion.webmage.capture.CaptureStrategy;
import com.xcion.webmage.capture.Capturer;
import com.xcion.webmage.capture.OnCaptureListener;
import com.xcion.webmage.finder.FileDataFinder;
import com.xcion.webmage.location.Locator;
import com.xcion.webmage.theme.ThemeInjector;
import com.xcion.webmage.utils.PermissionsUtils;
import com.xcion.webmage.video.VideoPlayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;


/**
 * @Author: Kern Hu
 * @E-mail: sky580@126.com
 * @CreateDate: 2020/9/19 14:40
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/9/19 14:40
 * @Version: 1.0
 * @Description: java类作用描述
 * @UpdateRemark: 更新说明
 */
public class WebMageChromeClient extends WebChromeClient {

    private static final String TAG = "WebMageChromeClient";
    private static final int REQUEST_CODE = 30001;
    private WebMageController controller;
    private Activity activity;

    private ThemeInjector mThemeInjector;
    private static FileDataFinder mFileDataFinder;

    private Capturer mCapturer = null;
    private boolean captureInterceptor;
    private static OnCaptureListener mOnCaptureListener;

    /****************************************************************/
    private String[] permissions = new String[]
            {
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
            };
    private PermissionsUtils mPermissionsUtils = null;

    /****************************************************************/

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setController(WebMageController controller) {
        this.controller = controller;
    }

    public void setOnCaptureListener(Capturer capturer, OnCaptureListener captureListener) {
        this.mCapturer = capturer;
        this.mOnCaptureListener = captureListener;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mPermissionsUtils != null) {
            mPermissionsUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        if (mFileDataFinder != null) {
            mFileDataFinder.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mFileDataFinder != null) {
            mFileDataFinder.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void destroy() {
        mPermissionsUtils = null;
        controller = null;
        mFileDataFinder = null;
        activity = null;
        mCapturer = null;
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        if (controller != null) {
            controller.onReceivedTitle(view, title);
        }
        super.onReceivedTitle(view, title);
    }

    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
        super.onReceivedIcon(view, icon);
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        //
        if (controller != null) {
            controller.onProgressChanged(view, newProgress);
        }
        //
        setCapture(view, newProgress);
        //
//        if (mThemeInjector == null) {
//            mThemeInjector = new ThemeInjector(activity);
//        }
//        mThemeInjector.changeMode(view, false);


        super.onProgressChanged(view, newProgress);
    }

    @Override
    public void onGeolocationPermissionsShowPrompt(final String origin, final GeolocationPermissions.Callback callback) {
        //location setting
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {  //android 8.0/sdk26 及以上先动态申请权限
            if (mPermissionsUtils == null) {
                mPermissionsUtils = new PermissionsUtils(activity);
            }
            mPermissionsUtils.requestPermissions(permissions, REQUEST_CODE, new PermissionsUtils.Callback() {
                @Override
                public void onPermission(int requestCode, String[] deniedPermissions) {
                    if (deniedPermissions.length == 0) {
                        callback.invoke(origin, true, false);
                    } else {
                        //无授权
                        Toast.makeText(activity, activity.getResources().getString(R.string.permission_location_fail), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {//android 8.0及以下则显示提示定位弹框
            if (WebMage.getWebOptions().getGeolocation() == Geolocation.ALWAYS) {
                callback.invoke(origin, true, false);
            } else if (WebMage.getWebOptions().getGeolocation() == Geolocation.ASK_ME) {
                Locator.getInstance(activity)
                        .setMessage(Uri.parse(origin).getHost())
                        .setCallback(new Locator.Callback() {
                            @Override
                            public void onAllow() {
                                callback.invoke(origin, true, false);
                            }

                            @Override
                            public void onRefuse() {
                                Locator.getInstance(activity).onDestroy();
                            }
                        })
                        .build();
            } else if (WebMage.getWebOptions().getGeolocation() == Geolocation.NEVER) {
            }
        }
        super.onGeolocationPermissionsShowPrompt(origin, callback);
    }

    /**
     * 当网页调用alert()来弹出alert弹出框前回调，用以拦截alert()函数
     *
     * @param view
     * @param url
     * @param message
     * @param result
     * @return
     */
    @Override
    public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {

        String host = TextUtils.isEmpty(Uri.parse(url).getHost()) ? activity.getResources().getString(R.string.js_alert_host_unknown) : Uri.parse(url).getHost();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getResources().getString(R.string.js_alert_tips))
                .setMessage(String.format(activity.getResources().getString(R.string.js_alert_message), host, message))
                .setPositiveButton(activity.getResources().getString(R.string.js_alert_sure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_tooltip_info);
        AlertDialog dialog = builder.create();
        dialog.show();

        return true;
    }

    /**
     * 当网页调用confirm()来弹出confirm弹出框前回调，用以拦截confirm()函数
     *
     * @param view
     * @param url
     * @param message
     * @param result
     * @return
     */
    @Override
    public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
        String host = TextUtils.isEmpty(Uri.parse(url).getHost()) ? activity.getResources().getString(R.string.js_alert_host_unknown) : Uri.parse(url).getHost();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getResources().getString(R.string.js_alert_tips))
                .setMessage(String.format(activity.getResources().getString(R.string.js_alert_message), host, message))
                .setPositiveButton(activity.getResources().getString(R.string.js_alert_sure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                })
                .setNegativeButton(activity.getResources().getString(R.string.js_alert_cancel), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.cancel();
                    }
                });
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_tooltip_info);
        AlertDialog dialog = builder.create();
        dialog.show();

        return true /*super.onJsConfirm(view, url, message, result)*/;
    }

    /**
     * 当网页调用prompt()来弹出prompt弹出框前回调，用以拦截prompt()函数
     *
     * @param view
     * @param url
     * @param message
     * @param defaultValue
     * @param result
     * @return
     */
    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {

        String host = TextUtils.isEmpty(Uri.parse(url).getHost()) ? activity.getResources().getString(R.string.js_alert_host_unknown) : Uri.parse(url).getHost();
        final EditText mEditText = new EditText(activity);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getResources().getString(R.string.js_alert_tips))
                .setMessage(String.format(activity.getResources().getString(R.string.js_alert_message), host, message))
                .setPositiveButton(activity.getResources().getString(R.string.js_alert_sure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm(mEditText.getText().toString());
                    }
                });
        builder.setView(mEditText);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_tooltip_info);
        AlertDialog dialog = builder.create();
        dialog.show();

        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mEditText.getLayoutParams();
        lp.leftMargin = 20;
        lp.rightMargin = 20;
        mEditText.setLayoutParams(lp);

        return true /*super.onJsPrompt(view, url, message, defaultValue, result)*/;
    }

    /**
     * 打印 console 信息
     *
     * @param consoleMessage
     * @return
     */
    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        return super.onConsoleMessage(consoleMessage);
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        /********************************************************************/
        VideoPlayer
                .getInstance(activity)
                .setRotation(VideoPlayer.Rotation.AUTO_BY_SENSOR)
                .setBackgroundColor(WebMage.getVideoOptions().getVideoPlayerBackground())
                .setTargetView(view)
                .build();
        /********************************************************************/
        super.onShowCustomView(view, callback);
    }

    @Override
    public void onHideCustomView() {
        /********************************************************************/
        VideoPlayer.getInstance(activity).destroy();
        /********************************************************************/
        super.onHideCustomView();
    }

    /***********************************************************************************************/
    /***********************************************************************************************/
    /***********************************************************************************************/
    // For Android 3.0+
    public void openFileChooser(final ValueCallback<Uri> filePathCallback, String acceptType) {
        if (mFileDataFinder == null) {
            mFileDataFinder = new FileDataFinder(activity);
        }
        mFileDataFinder
                .setAcceptType(new String[]{acceptType})
                .setTitle(activity.getResources().getString(R.string.file_chooser_title))
                //.setFilePathCallback((ValueCallback<Uri[]>) filePathCallback)
                .setRequestCode(202)
                .setCallback(new FileDataFinder.Callback() {
                    @Override
                    public void onChoose(Uri[] filePath) {
                        if (filePath != null && filePath.length >= 1) {
                            filePathCallback.onReceiveValue(filePath[0]);
                        }
                        mFileDataFinder.onDestroy();
                        mFileDataFinder = null;
                    }

                    @Override
                    public void onCancel() {
                        filePathCallback.onReceiveValue(null);
                        mFileDataFinder.onDestroy();
                        mFileDataFinder = null;
                    }
                })
                .build();
    }

    // For Android < 3.0
    public void openFileChooser(ValueCallback<Uri> filePathCallback) {
        openFileChooser(filePathCallback, "");
    }

    // For Android  > 4.1.1
    public void openFileChooser(ValueCallback<Uri> filePathCallback, String acceptType, String capture) {
        openFileChooser(filePathCallback, acceptType);
    }

    @Override
    // For Android 5.0+
    public boolean onShowFileChooser(final WebView webView, final ValueCallback<Uri[]> filePathCallback, final FileChooserParams fileChooserParams) {
        if (mFileDataFinder == null) {
            mFileDataFinder = new FileDataFinder(activity);
        }
        mFileDataFinder
                .setAcceptType(fileChooserParams.getAcceptTypes())
                .setTitle(fileChooserParams.getTitle() != null ? fileChooserParams.getTitle().toString() : activity.getResources().getString(R.string.file_chooser_title))
                .setFilePathCallback(filePathCallback)
                .setRequestCode(202)
                .setCallback(new FileDataFinder.Callback() {
                    @Override
                    public void onChoose(Uri[] filePath) {
                        filePathCallback.onReceiveValue(filePath);
                        mFileDataFinder.onDestroy();
                        mFileDataFinder = null;
                    }

                    @Override
                    public void onCancel() {
                        filePathCallback.onReceiveValue(null);
                        mFileDataFinder.onDestroy();
                        mFileDataFinder = null;
                    }
                })
                .build();
        return true;
        //return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
    }

    /******************************************************************************************************************/
    /******************************************************************************************************************/
    /******************************************************************************************************************/
    private void setCapture(WebView view, int newProgress) {
        if (CaptureStrategy.STARTED_AND_FINISH == WebMage.getCaptureStrategy()) {                   //STARTED_AND_FINISH
            if (mOnCaptureListener != null) {
                if (newProgress <= 10) {
                    if (!captureInterceptor) {
                        captureInterceptor = true;
                        mOnCaptureListener.onCapture(mCapturer.capture(view));
                    }
                } else if (newProgress == 100) {
                    if (!captureInterceptor) {
                        captureInterceptor = true;
                        mOnCaptureListener.onCapture(mCapturer.capture(view));
                    }
                } else {
                    captureInterceptor = false;
                }
            }
        } else if (CaptureStrategy.WHEN_FINISHED == WebMage.getCaptureStrategy()) {                 //WHEN_FINISHED
            if (mOnCaptureListener != null) {
                if (newProgress == 100) {
                    if (!captureInterceptor) {
                        captureInterceptor = true;
                        mOnCaptureListener.onCapture(mCapturer.capture(view));
                    }
                } else {
                    captureInterceptor = false;
                }
            }
        } else if (CaptureStrategy.EVERY_PERCENT_10 == WebMage.getCaptureStrategy()) {              //EVERY_PERCENT_10
            if (mOnCaptureListener != null) {
                if (newProgress == 100) {
                    if (!captureInterceptor) {
                        captureInterceptor = true;
                        mOnCaptureListener.onCapture(mCapturer.capture(view));
                    }
                } else if (newProgress % 10 < 5) {
                    if (!captureInterceptor) {
                        captureInterceptor = true;
                        mOnCaptureListener.onCapture(mCapturer.capture(view));
                    }
                } else {
                    captureInterceptor = false;
                }
            }
        } else if (CaptureStrategy.EVERY_PERCENT_20 == WebMage.getCaptureStrategy()) {              //EVERY_PERCENT_20
            if (mOnCaptureListener != null) {
                if (newProgress == 100) {
                    if (!captureInterceptor) {
                        captureInterceptor = true;
                        mOnCaptureListener.onCapture(mCapturer.capture(view));
                    }
                } else if (newProgress % 20 < 5) {
                    if (!captureInterceptor) {
                        captureInterceptor = true;
                        mOnCaptureListener.onCapture(mCapturer.capture(view));
                    }
                } else {
                    captureInterceptor = false;
                }
            }
        } else if (CaptureStrategy.INCESSANT_BY_PROGRESS == WebMage.getCaptureStrategy()) {         //INCESSANT_BY_PROGRESS
            if (mOnCaptureListener != null) {
                mOnCaptureListener.onCapture(mCapturer.capture(view));
            }
        } else if (CaptureStrategy.NEVER == WebMage.getCaptureStrategy()) {                         //NEVER

        }
    }
    /******************************************************************************************************************/
    /******************************************************************************************************************/
    /******************************************************************************************************************/

}
