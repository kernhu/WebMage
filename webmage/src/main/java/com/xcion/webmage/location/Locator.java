package com.xcion.webmage.location;

import android.app.Activity;
import android.content.DialogInterface;
import android.text.TextUtils;

import androidx.appcompat.app.AlertDialog;

import java.lang.ref.WeakReference;

import com.xcion.webmage.R;

/**
 * @Author: Kern Hu
 * @E-mail: sky580@126.com
 * @CreateDate: 2020/11/21 11:29
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/11/21 11:29
 * @Version: 1.0
 * @Description: java类作用描述
 * @UpdateRemark: 更新说明
 */
public class Locator {

    private static Locator mLocator;
    private AlertDialog.Builder mBuilder;
    private AlertDialog mAlertDialog;

    private WeakReference<Activity> activity;
    private String message;
    private Callback callback;
    private boolean isShowing;

    public interface Callback {

        void onAllow();

        void onRefuse();
    }

    public Locator setCallback(Callback callback) {
        this.callback = callback;
        return this;
    }

    public Locator setMessage(String message) {
        this.message = message;
        return this;
    }

    public boolean isShowing() {
        return isShowing;
    }

    public static Locator getInstance(Activity activity) {
        if (mLocator == null) {
            mLocator = new Locator(activity);
        }
        return mLocator;
    }

    public Locator(Activity activity) {
        this.activity = new WeakReference<>(activity);

        if (this.activity == null || this.activity.get() == null) {
            return;
        }

        mBuilder = new AlertDialog.Builder(this.activity.get());
        mBuilder.setIcon(R.drawable.ic_tooltip_locating)
                .setTitle(this.activity.get().getResources().getString(R.string.location_tips))
                .setCancelable(false)
                .setPositiveButton(this.activity.get().getResources().getString(R.string.location_allow), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (callback != null) {
                            callback.onAllow();
                        }
                    }
                })
                .setNegativeButton(this.activity.get().getResources().getString(R.string.location_refuse), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (callback != null) {
                            callback.onRefuse();
                        }
                    }
                });
    }

    public void build() {

        if (this.activity == null || this.activity.get() == null) {
            return;
        }
        if (isShowing) {
            return;
        }
        String host = TextUtils.isEmpty(message) ? activity.get().getResources().getString(R.string.js_alert_host_unknown) : message;
        mAlertDialog = mBuilder
                .setMessage(String.format(this.activity.get().getResources().getString(R.string.location_message), host))
                .create();
        mAlertDialog.show();

    }

    public void onDestroy() {

        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
        activity = null;
        mAlertDialog = null;
        mBuilder = null;
        mLocator = null;

    }

}
