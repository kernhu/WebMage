package com.xcion.webmage.permission;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import java.io.Serializable;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.PermissionChecker;

/**
 * Author: Kern
 * E-mail: sky580@126.com
 * DateTime: 2021/10/10  21:20
 * Intro:
 */
public class PermissionsApplicant {

    public static final String TAG = PermissionsApplicant.class.getName();
    private static HashMap<String, PermissionCallback> callbackMap = new HashMap();

    /**
     * 申请授权，当用户拒绝时，会显示默认一个默认的Dialog提示用户
     *
     * @param context
     * @param callback
     * @param permission 要申请的权限
     */
    public static void requestPermission(Context context, PermissionCallback callback, String... permission) {
        requestPermission(context, callback, permission, true, null);
    }

    /**
     * 申请授权，当用户拒绝时，可以设置是否显示Dialog提示用户，也可以设置提示用户的文本内容
     *
     * @param context
     * @param callback
     * @param permission 需要申请授权的权限
     * @param showTip    当用户拒绝授权时，是否显示提示
     * @param tip        当用户拒绝时要显示Dialog设置
     */
    public static void requestPermission(@NonNull Context context, @NonNull PermissionCallback callback
            , @NonNull String[] permission, boolean showTip, @Nullable TipInfo tip) {

        if (callback == null) {
            throw new NullPointerException("PermissionCallback couldn't be null");
        }

        if (PermissionsApplicant.hasPermission(context, permission)) {
            callback.permissionGranted(permission);
        } else {
            if (Build.VERSION.SDK_INT < 23) {
                callback.permissionDenied(permission);
            } else {
                String key = String.valueOf(System.currentTimeMillis());
                callbackMap.put(key, callback);
                Intent intent = new Intent(context, PermissionActivity.class);
                intent.putExtra(PermissionActivity.EXTRA_PERMISSION, permission);
                intent.putExtra(PermissionActivity.EXTRA_KEY, key);
                intent.putExtra(PermissionActivity.EXTRA_SHOW_TIP, showTip);
                intent.putExtra(PermissionActivity.EXTRA_TIP, tip);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }
    }


    /**
     * 判断权限是否授权
     *
     * @param context
     * @param permissions
     * @return
     */
    public static boolean hasPermission(@NonNull Context context, @NonNull String... permissions) {

        if (permissions.length == 0) {
            return false;
        }

        for (String per : permissions) {
            int result = PermissionChecker.checkSelfPermission(context, per);
            if (result != PermissionChecker.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    /**
     * 判断一组授权结果是否为授权通过
     *
     * @param grantResult
     * @return
     */
    public static boolean isGranted(@NonNull int... grantResult) {

        if (grantResult.length == 0) {
            return false;
        }

        for (int result : grantResult) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 跳转到当前应用对应的设置页面
     *
     * @param context
     */
    public static void gotoSetting(@NonNull Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }

    /**
     * @param key
     * @return
     */
    static PermissionCallback fetchListener(String key) {
        return callbackMap.remove(key);
    }


    public static class TipInfo implements Serializable {

        private static final long serialVersionUID = 1L;

        String title;
        String content;
        String cancel;
        String ensure;

        public TipInfo(@Nullable String title, @Nullable String content, @Nullable String cancel, @Nullable String ensure) {
            this.title = title;
            this.content = content;
            this.cancel = cancel;
            this.ensure = ensure;
        }
    }

}
