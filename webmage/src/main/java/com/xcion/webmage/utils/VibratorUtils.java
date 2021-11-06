package com.xcion.webmage.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.VibrationEffect;
import android.os.Vibrator;

/**
 * @Author: Kern Hu
 * @E-mail: sky580@126.com
 * @CreateDate: 2020/10/13 9:35
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/10/13 9:35
 * @Version: 1.0
 * @Description: java类作用描述
 * @UpdateRemark: 更新说明
 */
public class VibratorUtils {

    public enum Effect {
        CLICK,
        LONG_PRESS,
        DOUBLE_CLICK,
    }

    @SuppressLint("MissingPermission")
    public static void setVibrator(Context context, Effect effect) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator.hasVibrator())
            vibrator.cancel();

        switch (effect) {
            case CLICK:
                vibrator.vibrate(VibrationEffect.EFFECT_CLICK);
                break;
            case LONG_PRESS:
                vibrator.vibrate(VibrationEffect.EFFECT_HEAVY_CLICK);
                break;
            case DOUBLE_CLICK:
                vibrator.vibrate(VibrationEffect.EFFECT_DOUBLE_CLICK);
                break;
        }
    }
}
