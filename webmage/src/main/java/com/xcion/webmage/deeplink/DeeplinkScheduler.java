package com.xcion.webmage.deeplink;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xcion.webmage.R;
import com.google.android.material.snackbar.Snackbar;


import java.lang.ref.WeakReference;
import java.net.URISyntaxException;

/**
 * @Author: Kern Hu
 * @E-mail: sky580@126.com
 * @CreateDate: 2020/10/9 10:59
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/10/9 10:59
 * @Version: 1.0
 * @Description: java类作用描述
 * @UpdateRemark: 更新说明   deeplink管理类
 */
public class DeeplinkScheduler {

    enum Type {

        /**
         * tel call
         */
        TEL("tel:"),
        /**
         * sms
         */
        SMS("sms:"),
        /**
         * email
         */
        EMAIL("mailto:");

        String name;

        Type(String name) {
            this.name = name;
        }

    }

    public static boolean isShowing = false;
    private DeeplinkWindow mDeeplinkWindow;
    private WeakReference<Context> reference;
    private boolean tooltip;
    private String deeplink;
    private ViewGroup anchorView;
    private boolean resultValue = true;


    public DeeplinkScheduler with(Context context) {
        this.reference = new WeakReference<>(context);
        mDeeplinkWindow = new DeeplinkWindow(context);
        return this;
    }

    public DeeplinkScheduler setTooltip(boolean tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    public DeeplinkScheduler setDeeplink(String deeplink) {
        this.deeplink = deeplink;
        return this;
    }

    public ViewGroup getAnchorView() {
        return anchorView;
    }

    public DeeplinkScheduler setAnchorView(ViewGroup anchorView) {
        this.anchorView = anchorView;
        return this;
    }

    public void onDestroy() {
        isShowing = false;
        reference = null;
        mDeeplinkWindow = null;
    }

    public boolean commit() {
        if (TextUtils.isEmpty(deeplink)) {
            return false;
        }
        if (reference == null || reference.get() == null) {
            return false;
        }
        // 打电话、发短信、发邮件
        deeplink = deeplink.trim();
        Log.e("sos", "deeplink===" + deeplink);
        String title = "";
        String message = "";
        String stamp = "";
        String content = "";
        Intent intent;
        if (deeplink.startsWith(Type.TEL.name)) {
            title = reference.get().getResources().getString(R.string.deeplink_tooltip_title);
            stamp = Type.TEL.name;
            content = deeplink.substring(deeplink.lastIndexOf(":") + 1);
            message = String.format(reference.get().getResources().getString(R.string.deeplink_tel_message), content);
            intent = new Intent(Intent.ACTION_DIAL, Uri.parse(stamp + content));
        } else if (deeplink.startsWith(Type.SMS.name)) {
            title = reference.get().getResources().getString(R.string.deeplink_tooltip_title);
            stamp = Type.SMS.name;
            content = deeplink.substring(deeplink.lastIndexOf(":") + 1);
            message = String.format(reference.get().getResources().getString(R.string.deeplink_sms_message), content);
            intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(stamp + content));
        } else if (deeplink.startsWith(Type.EMAIL.name)) {
            title = reference.get().getResources().getString(R.string.deeplink_tooltip_title);
            stamp = Type.EMAIL.name;
            content = deeplink.substring(deeplink.lastIndexOf(":") + 1);
            message = String.format(reference.get().getResources().getString(R.string.deeplink_email_message), content);
            intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(stamp + content));
        } else {
            String appLabel = checkedInstalled();
            if (appLabel == null) {
                return true;
            }
            title = reference.get().getString(R.string.deeplink_tooltip_title);
            message = String.format(reference.get().getString(R.string.deeplink_tooltip_message), appLabel);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(deeplink));
        }
        if (tooltip) {
            return showTipWindow(reference.get(), intent, title, message, stamp);
        } else {
            return redirect(reference.get(), intent, stamp);
        }
    }


    /**
     * @param context
     * @param intent
     * @param title
     * @param message
     * @return
     */
    private boolean showTipWindow(Context context, Intent intent, String title, String message, String stamp) {
        if (isShowing) {
            return resultValue;
        }
        mDeeplinkWindow
                .setTitle(title)
                .setMessage(message)
                .setIcon(R.drawable.ic_tooltip_info)
                .setPositiveBtn(context.getResources().getString(R.string.deeplink_tooltip_positive))
                .setNegativeBtn(context.getResources().getString(R.string.deeplink_tooltip_negative))
                .setAnchorView(anchorView)
                .setCallback(new DeeplinkWindow.Callback() {
                    @Override
                    public void onPositive() {
                        isShowing = false;
                        try {
                            (context).startActivity(intent);
                            resultValue = true;
                        } catch (Exception e) {
                            String text = "";
                            if (Type.TEL.name.equals(stamp)) {
                                text = context.getResources().getString(R.string.deeplink_tel_tip);
                                resultValue = true;
                            } else if (Type.SMS.name.equals(stamp)) {
                                text = context.getResources().getString(R.string.deeplink_sms_tip);
                                resultValue = true;
                            } else if (Type.EMAIL.name.equals(stamp)) {
                                text = context.getResources().getString(R.string.deeplink_email_tip);
                                resultValue = true;
                            } else {
                                resultValue = false;
                            }
                            showSnackBar(context, text, R.drawable.ic_tooltip_info_lightful);
                        } finally {
                            onDestroy();
                        }
                    }

                    @Override
                    public void onNegative() {
                        isShowing = false;
                        onDestroy();
                        resultValue = true;
                    }

                    @Override
                    public void onDismiss() {
                        isShowing = false;
                        onDestroy();
                        resultValue = true;
                    }
                })
                .show();
        isShowing = true;
        return resultValue;
    }

    /**
     * skip next another app
     *
     * @param context
     * @param intent
     * @param stamp
     * @return
     */
    private boolean redirect(Context context, Intent intent, String stamp) {
        if (intent != null) {
            try {
                context.startActivity(intent);
                resultValue = true;
            } catch (Exception e) {
                String text = "";
                if (Type.TEL.name.equals(stamp)) {
                    text = context.getResources().getString(R.string.deeplink_tel_tip);
                    resultValue = true;
                } else if (Type.SMS.name.equals(stamp)) {
                    text = context.getResources().getString(R.string.deeplink_sms_tip);
                    resultValue = true;
                } else if (Type.EMAIL.name.equals(stamp)) {
                    text = context.getResources().getString(R.string.deeplink_email_tip);
                    resultValue = true;
                } else {
                    resultValue = false;
                }
                showSnackBar(context, text, R.drawable.ic_tooltip_info_lightful);
            }
        } else {
            resultValue = false;
        }
        return resultValue;
    }

    /**
     * if exist the app
     *
     * @return
     */
    private String checkedInstalled() {
        try {
            PackageManager packageManager = reference.get().getPackageManager();
            Intent intent = Intent.parseUri(deeplink, Intent.URI_INTENT_SCHEME);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ResolveInfo info = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (info != null) {
                return info.loadLabel(packageManager).toString();
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * show custom snack bar
     *
     * @param context
     * @param message
     * @param icon
     */
    private void showSnackBar(Context context, String message, int icon) {
        Drawable drawable = context.getResources().getDrawable(icon, null);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        Snackbar snackbar = Snackbar.make(((Activity) context).getWindow().getDecorView(), message, Snackbar.LENGTH_SHORT);
        TextView textView = snackbar.getView().findViewById(R.id.snackbar_text);
        textView.setCompoundDrawablePadding(10);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setCompoundDrawables(drawable, null, null, null);
        snackbar.show();
    }
}
