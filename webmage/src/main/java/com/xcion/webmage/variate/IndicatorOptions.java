package com.xcion.webmage.variate;

/**
 * @Author: Kern Hu
 * @E-mail: sky580@126.com
 * @CreateDate: 2020/12/25 11:24
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/12/25 11:24
 * @Version: 1.0
 * @Description: java类作用描述
 * @UpdateRemark: 更新说明
 */
public class IndicatorOptions {

    private static int height;
    private static int radius;
    private static int maxProgress;
    private static int backgroundColor;
    private static int progressColor;
    private static int animatorDuration;

    public IndicatorOptions() {
    }

    public static int getHeight() {
        return height;
    }

    public IndicatorOptions setHeight(int height) {
        this.height = height;
        return this;
    }

    public static int getRadius() {
        return radius;
    }

    public IndicatorOptions setRadius(int radius) {
        this.radius = radius;
        return this;
    }

    public static int getBackgroundColor() {
        return backgroundColor;
    }

    public IndicatorOptions setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public static int getProgressColor() {
        return progressColor;
    }

    public IndicatorOptions setProgressColor(int progressColor) {
        this.progressColor = progressColor;
        return this;
    }

    public static int getMaxProgress() {
        return maxProgress;
    }

    public IndicatorOptions setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
        return this;
    }

    public static int getAnimatorDuration() {
        return animatorDuration;
    }

    public IndicatorOptions setAnimatorDuration(int animatorDuration) {
        this.animatorDuration = animatorDuration;
        return this;
    }

    public IndicatorOptions build() {

        return this;
    }
}
