package com.xcion.webmage.feature.phone;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.android.material.snackbar.Snackbar;
import com.xcion.webmage.R;

import java.lang.ref.WeakReference;

/**
 * @author: Kern
 * E-mail: sky580@126.com
 * DateTime: 2021/11/11  22:50
 * Intro: This is ... all the items of long press menu about tel;
 */
public class PhoneFeature {

    private static final String TAG = PhoneFeature.class.getCanonicalName();
    private static final String TEL = "tel:";
    private static final String SMS = "sms:";

    private static PhoneFeature phoneFeature;
    private WeakReference<Context> contextReference;
    private String targetExtra;
    private Intent intent;

    public static PhoneFeature getInstance(Context context) {
        synchronized (PhoneFeature.class) {
            if (phoneFeature == null) {
                phoneFeature = new PhoneFeature(context);
            }
        }
        return phoneFeature;
    }

    public PhoneFeature(Context context) {
        this.contextReference = new WeakReference<>(context);
    }

    public PhoneFeature setTargetExtra(String targetExtra) {
        this.targetExtra = targetExtra;
        return this;
    }

    /**
     * call up the tel number
     */
    public void callUp() {
        try {
            intent = new Intent(Intent.ACTION_DIAL, Uri.parse(TEL + targetExtra));
            contextReference.get().startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "call up fail:" + e.getMessage());
        } finally {
            intent = null;
            contextReference.clear();
            phoneFeature = null;
        }
    }

    /**
     * send message to the tel number
     */
    public void sendMessage() {
        try {
            intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(SMS + targetExtra));
            contextReference.get().startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "send message fail:" + e.getMessage());
        } finally {
            intent = null;
            contextReference.clear();
            phoneFeature = null;
        }
    }

    /**
     * copy the tel number to clipboard
     */
    public void copyNumber() {
        try {
            ((ClipboardManager) contextReference.get().getSystemService(Context.CLIPBOARD_SERVICE))
                    .setPrimaryClip(ClipData.newPlainText("tel", targetExtra));
            Snackbar.make(((Activity) contextReference.get()).getWindow().getDecorView(),
                    contextReference.get().getResources().getString(R.string.menu_copy_number),
                    Snackbar.LENGTH_SHORT)
                    .show();
        } catch (Exception e) {
            Log.e(TAG, "copy number fail:" + e.getMessage());
        } finally {
            intent = null;
            contextReference.clear();
            phoneFeature = null;
        }
    }
    /**
     * share the tel
     */
    public void shareTo() {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, targetExtra);
            contextReference.get().startActivity(Intent.createChooser(intent, ""));
        } catch (Exception e) {
            Log.e(TAG, "share number fail:" + e.getMessage());
        } finally {
            intent = null;
            contextReference.clear();
            phoneFeature = null;
        }
    }
}
