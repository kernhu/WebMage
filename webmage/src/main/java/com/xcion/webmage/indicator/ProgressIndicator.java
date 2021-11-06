package com.xcion.webmage.indicator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Author: Kern
 * E-mail: sky580@126.com
 * DateTime: 2021/4/9  00:18
 * Intro:
 */
public class ProgressIndicator extends View {

    private Drawable drawable;
    private int progress;
    private int progressWidth;
    private FlingRunnable mFlingRunnable;

    public ProgressIndicator(Context context) {
        super(context);
        init();
    }

    public ProgressIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProgressIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        drawable = new ColorDrawable(0xff4fd922);
    }

    /**
     * set progress bar color
     *
     * @param color
     */
    public void setProgressColor(int color) {
        drawable = new ColorDrawable(color);
        invalidate();
    }

    /**
     * set progress
     *
     * @param progress this progress max value 100
     */
    public void setProgress(int progress) {
        if (progress < 0 || progress > 100) {
            return;
        }
        if (progress < this.progress) {
            return;
        }
        this.progress = progress;
        if (mFlingRunnable == null) {
            mFlingRunnable = new FlingRunnable();
        } else {
            removeCallbacks(mFlingRunnable);
        }
        mFlingRunnable.startFling();
        this.bringToFront();
    }

    /**
     * get progress value
     *
     * @return progress value
     */
    public int getProgress() {
        return progress;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (drawable == null) {
            return;
        }
        drawable.setBounds(0, 0, progressWidth, getHeight());
        drawable.draw(canvas);
    }

    class FlingRunnable implements Runnable {

        int speed = 1;
        int targetWidth = 0;

        public void startFling() {
            if (getVisibility() == GONE) {
                setVisibility(VISIBLE);
                progressWidth = 0;
            }
            targetWidth = (getWidth() * progress / 100);
            if (progressWidth > targetWidth) {
                progressWidth = targetWidth;
            }
            postOnAnimation(this);
        }

        public void endFling() {
            progressWidth = getWidth();
            setVisibility(GONE);
            removeCallbacks(this);
        }

        @Override
        public void run() {
            //计算速度
            if (progressWidth < targetWidth) {
                speed++;
            } else {
                speed = 1;
            }
            //计算进度条宽度
            progressWidth += speed;
            //计算当前进度
            progress = progressWidth / getWidth() * 100;
            if (progressWidth < getWidth()) {
                invalidate();
                postOnAnimationDelayed(this, 10);
            } else {
                endFling();
            }
        }
    }
}
