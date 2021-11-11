package com.xcion.webmage.feature.textual;

import android.content.Context;
import android.content.Intent;

import com.xcion.webmage.feature.phone.PhoneFeature;

import java.lang.ref.WeakReference;

/**
 * @author:: Kern
 * e-mail: sky580@126.com
 * dateTime: 2021/11/12  00:02
 * intro: This is ... all the items of long press menu about textual;
 */
public class TextualFeature {

    private static final String TAG = PhoneFeature.class.getCanonicalName();

    private static TextualFeature textualFeature;
    private WeakReference<Context> contextReference;
    private String targetExtra;
    private Intent intent;

    public static TextualFeature getInstance(Context context) {
        synchronized (PhoneFeature.class) {
            if (textualFeature == null) {
                textualFeature = new TextualFeature(context);
            }
        }
        return textualFeature;
    }

    public TextualFeature(Context context) {
        this.contextReference = new WeakReference<>(context);
    }


}
