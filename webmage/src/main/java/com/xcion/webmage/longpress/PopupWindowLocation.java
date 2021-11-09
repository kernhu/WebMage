package com.xcion.webmage.longpress;

import android.content.Context;
import android.view.View;

/**
 * @author abs
 */
public class PopupWindowLocation {

    /**
     * 计算popupWindow在长按view 的什么位置显示
     *
     * @param anchorView  长按锚点的view
     * @param contentView 弹出框的布局View
     * @param touchX      锚点距离屏幕左边的距离
     * @param touchY      锚点距离屏幕上方的距离
     * @return popupWindow在长按view中的xy轴的偏移量
     */
    public static int[] calculatePopWindowPos(final View anchorView, final View contentView,
                                              int touchX, int touchY) {
        final int windowLoc[] = new int[2];
        int offset = 144;
        // 获取屏幕的高宽
        final int screenHeight = getScreenHeight(anchorView.getContext());
        final int screenWidth = getScreenWidth(anchorView.getContext());
        // 测量弹出框View的宽高
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        final int popHeight = contentView.getMeasuredHeight();
        final int popWidth = contentView.getMeasuredWidth();
        // 判断需要向上弹出还是向下弹出显示
        // 屏幕高度-触点距离左上角的高度 < popupWindow的高度
        // 如果小于弹出框的高度那么说明下方空间不够显示 popupWindow，需要放在触点的上方显示
        final boolean isNeedShowTop = (popHeight + touchY > screenHeight);
        // 判断需要向右边弹出还是向左边弹出显示
        //判断触点右边的剩余空间是否够显示popupWindow 大于就说明够显示
        final boolean isNeedShowRight = (touchX < (screenWidth / 2));
        if (isNeedShowTop) {
            //如果在上方显示 则用 触点的距离上方的距离 - 弹框的高度
            windowLoc[1] = touchY - popHeight;
        } else {
            //如果在下方显示 则用 触点的距离上方的距离
            windowLoc[1] = touchY;
        }
        if (isNeedShowRight) {
            windowLoc[0] = touchX;
        } else {
            //显示在左边的话 那么弹出框的位置在触点左边出现，则是触点距离左边距离 - 弹出框的宽度
            windowLoc[0] = touchX - popWidth - offset;
        }
        return windowLoc;
    }

    /**
     * dp转px
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 获取屏幕高度(px)
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取屏幕宽度(px)
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }
}
