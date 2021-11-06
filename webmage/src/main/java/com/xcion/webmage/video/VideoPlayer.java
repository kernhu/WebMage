package com.xcion.webmage.video;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

/**
 * @Author: Kern Hu
 * @E-mail: sky580@126.com
 * @CreateDate: 2020/10/17 10:32
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/10/17 10:32
 * @Version: 1.0
 * @Description: java类作用描述
 * @UpdateRemark: 更新说明
 */
public class VideoPlayer {

    private static VideoPlayer mVideoPlayer;
    public static View targetView;
    private int backgroundColor = Color.parseColor("#FF2C2C2C");
    private Rotation rotation = Rotation.ROTATE_PORTRAIT;

    private WeakReference<Activity> activity;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams = new WindowManager.LayoutParams();
    private boolean isShowing = false;


    public enum Rotation {
        ROTATE_PORTRAIT(0),
        ROTATE_LANDSCAPE(1),
        AUTO_BY_SENSOR(2);

        private int rotate;

        Rotation(int rotate) {
            this.rotate = rotate;
        }

        public int getRotate() {
            return rotate;
        }
    }

    public static VideoPlayer getInstance(Activity activity) {
        if (mVideoPlayer == null) {
            mVideoPlayer = new VideoPlayer(activity);
        }
        return mVideoPlayer;
    }

    public VideoPlayer(Activity activity) {
        this.activity = new WeakReference<>(activity);
        mWindowManager = activity.getWindowManager();
    }

    public VideoPlayer setTargetView(View targetView) {
        this.targetView = targetView;
        return this;
    }

    public VideoPlayer setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public VideoPlayer setRotation(Rotation rotation) {
        this.rotation = rotation;
        return this;
    }

    public boolean isShowing() {
        return isShowing;
    }

    public void destroy() {
        isShowing = false;
        activity.get().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mWindowManager.removeView(targetView);
        activity = null;
        targetView = null;
        mVideoPlayer = null;
    }

    public void build() {

        if (rotation == Rotation.ROTATE_PORTRAIT) {
            activity.get().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if (rotation == Rotation.ROTATE_LANDSCAPE) {
            activity.get().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (rotation == Rotation.AUTO_BY_SENSOR) {
            activity.get().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
        if (!isShowing) {
            isShowing = true;
            mWindowManager.addView(targetView, mLayoutParams);
        }
    }
}
