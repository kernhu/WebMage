package com.xcion.webmage.popupwindow;

import android.content.Context;
import android.view.View;

/**
 * popup window
 */
public class MagePopupWindow extends AbstractPopupWindow<MagePopupWindow> {

    private OnViewListener viewListener;

    public static MagePopupWindow create() {
        return new MagePopupWindow();
    }

    public MagePopupWindow() {
    }

    public static MagePopupWindow create(Context context) {
        return new MagePopupWindow(context);
    }


    public MagePopupWindow(Context context) {
        setContext(context);
    }

    @Override
    protected void initAttributes() {

    }

    @Override
    protected void initViews(View view, MagePopupWindow popup) {
        if (viewListener != null) {
            viewListener.initViews(view, popup);
        }
    }

    public MagePopupWindow setOnViewListener(OnViewListener listener) {
        this.viewListener = listener;
        return this;
    }

    public interface OnViewListener {

        void initViews(View view, MagePopupWindow popup);
    }
}
