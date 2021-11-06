package com.xcion.webmage.client;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.xcion.webmage.Geolocation;
import com.xcion.webmage.R;
import com.xcion.webmage.WebMage;
import com.xcion.webmage.WebMageController;
import com.xcion.webmage.finder.FileDataFinder;
import com.xcion.webmage.location.Locator;
import com.xcion.webmage.theme.ThemeInjector;
import com.xcion.webmage.utils.PermissionsUtils;
import com.xcion.webmage.video.VideoPlayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

/**
 * Author: Kern
 * E-mail: sky580@126.com
 * DateTime: 2021/10/7  23:30
 * Intro: This is the proxy class of  WebChromeClient
 */
public class WebChromeClientProxyProxy extends WebChromeClient implements IClientProxy {

    private static final String[] PERMISSION_LOCATION = new String[]
            {
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
            };
    private static final int REQUEST_CODE = 30001;

    private Activity activity;
    private WebChromeClient client;
    private WebMageController controller;

    private PermissionsUtils permissionsUtils;
    private FileDataFinder fileDataFinder;
    private ThemeInjector themeInjector;

    public WebChromeClientProxyProxy(Activity activity, WebChromeClient client, WebMageController controller) {
        this.activity = activity;
        this.client = client;
        this.controller = controller;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onTrimMemory(int level) {

    }

    @Override
    public void onLowMemory() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (fileDataFinder != null) {
            fileDataFinder.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (permissionsUtils != null) {
            permissionsUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        if (fileDataFinder != null) {
            fileDataFinder.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {

        if (controller != null) {
            controller.onProgressChanged(view, newProgress);
        }

        client.onProgressChanged(view, newProgress);
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        if (controller != null) {
            controller.onReceivedTitle(view, title);
        }
        client.onReceivedTitle(view, title);
    }

    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
        client.onReceivedIcon(view, icon);
    }

    @Override
    public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
        client.onReceivedTouchIconUrl(view, url, precomposed);
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

        client.onShowCustomView(view, callback);
    }

    @Override
    public void onHideCustomView() {

        /********************************************************************/
        VideoPlayer.getInstance(activity).destroy();
        /********************************************************************/

        client.onHideCustomView();
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        return client.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
    }

    @Override
    public void onRequestFocus(WebView view) {
        client.onRequestFocus(view);
    }

    @Override
    public void onCloseWindow(WebView window) {
        client.onCloseWindow(window);
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {

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

        return client.onJsAlert(view, url, message, result);
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {

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

        return client.onJsConfirm(view, url, message, result);
    }

    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {

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

        return client.onJsPrompt(view, url, message, defaultValue, result);
    }

    @Override
    public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
        return client.onJsBeforeUnload(view, url, message, result);
    }

    @Override
    public void onExceededDatabaseQuota(String url, String databaseIdentifier, long quota, long estimatedDatabaseSize, long totalQuota, WebStorage.QuotaUpdater quotaUpdater) {
        client.onExceededDatabaseQuota(url, databaseIdentifier, quota, estimatedDatabaseSize, totalQuota, quotaUpdater);
    }

    @Override
    public void onReachedMaxAppCacheSize(long requiredStorage, long quota, WebStorage.QuotaUpdater quotaUpdater) {
        client.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
    }

    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // android 8.0/sdk26 及以上先动态申请权限
            if (permissionsUtils == null) {
                permissionsUtils = new PermissionsUtils(activity);
            }
            permissionsUtils.requestPermissions(PERMISSION_LOCATION, REQUEST_CODE, new PermissionsUtils.Callback() {
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
        } else {
            //android 8.0及以下则显示提示定位弹框
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
        client.onGeolocationPermissionsShowPrompt(origin, callback);
    }

    @Override
    public void onGeolocationPermissionsHidePrompt() {
        client.onGeolocationPermissionsHidePrompt();
    }

    @Override
    public void onPermissionRequest(PermissionRequest request) {
        client.onPermissionRequest(request);
    }

    @Override
    public void onPermissionRequestCanceled(PermissionRequest request) {
        client.onPermissionRequestCanceled(request);
    }

    @Override
    public boolean onJsTimeout() {
        return client.onJsTimeout();
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        return client.onConsoleMessage(consoleMessage);
    }

    @Override
    public void onConsoleMessage(String message, int lineNumber, String sourceID) {
        client.onConsoleMessage(message, lineNumber, sourceID);
    }

    @Nullable
    @Override
    public Bitmap getDefaultVideoPoster() {
        return client.getDefaultVideoPoster();
    }

    @Nullable
    @Override
    public View getVideoLoadingProgressView() {
        return client.getVideoLoadingProgressView();
    }

    @Override
    public void getVisitedHistory(ValueCallback<String[]> callback) {
        client.getVisitedHistory(callback);
    }

    /***********************************************************************************************/
    /***********************************************************************************************/
    /***********************************************************************************************/
    // For Android 3.0+
    public void openFileChooser(final ValueCallback<Uri> filePathCallback, String acceptType) {
        if (fileDataFinder == null) {
            fileDataFinder = new FileDataFinder(activity);
        }
        fileDataFinder
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
                        fileDataFinder.onDestroy();
                        fileDataFinder = null;
                    }

                    @Override
                    public void onCancel() {
                        filePathCallback.onReceiveValue(null);
                        fileDataFinder.onDestroy();
                        fileDataFinder = null;
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

        if (fileDataFinder == null) {
            fileDataFinder = new FileDataFinder(activity);
        }
        fileDataFinder
                .setAcceptType(fileChooserParams.getAcceptTypes())
                .setAcceptType(fileChooserParams.getAcceptTypes())
                .setTitle(fileChooserParams.getTitle() != null ? fileChooserParams.getTitle().toString() : activity.getResources().getString(R.string.file_chooser_title))
                .setFilePathCallback(filePathCallback)
                .setRequestCode(202)
                .setCallback(new FileDataFinder.Callback() {
                    @Override
                    public void onChoose(Uri[] filePath) {
                        filePathCallback.onReceiveValue(filePath);
                        fileDataFinder.onDestroy();
                        fileDataFinder = null;
                    }

                    @Override
                    public void onCancel() {
                        filePathCallback.onReceiveValue(null);
                        fileDataFinder.onDestroy();
                        fileDataFinder = null;
                    }
                })
                .build();
        return true;
    }


}
