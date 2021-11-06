package com.xcion.webmage.picture.widget;

import android.graphics.PointF;
import android.graphics.RectF;
import android.widget.ImageView;


public class FromInfo {

    RectF mRect = new RectF();

    RectF mImgRect = new RectF();

    RectF mWidgetRect = new RectF();

    RectF mBaseRect = new RectF();

    PointF mScreenCenter = new PointF();

    float mScale;

    float mDegrees;

    ImageView.ScaleType mScaleType;

    public FromInfo(RectF rect, RectF img, RectF widget, RectF base, PointF screenCenter, float scale, float degrees, ImageView.ScaleType scaleType) {
        mRect.set(rect);
        mImgRect.set(img);
        mWidgetRect.set(widget);
        mScale = scale;
        mScaleType = scaleType;
        mDegrees = degrees;
        mBaseRect.set(base);
        mScreenCenter.set(screenCenter);
    }

    @Override
    public String toString() {
        return "FromInfo{" +
                "mRect=" + mRect +
                ", mImgRect=" + mImgRect +
                ", mWidgetRect=" + mWidgetRect +
                ", mBaseRect=" + mBaseRect +
                ", mScreenCenter=" + mScreenCenter +
                ", mScale=" + mScale +
                ", mDegrees=" + mDegrees +
                ", mScaleType=" + mScaleType +
                '}';
    }
}
