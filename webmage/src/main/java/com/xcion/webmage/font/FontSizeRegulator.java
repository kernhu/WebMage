package com.xcion.webmage.font;

import android.content.Context;
import android.widget.LinearLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.xcion.webmage.view.FontSizeRegulateView;

/**
 * Author: Kern
 * E-mail: sky580@126.com
 * DateTime: 2021/2/26  23:30
 * Intro:
 */
public class FontSizeRegulator {

    private Context context;
    private FontSizeChangeListener fontSizeChangeListener;
    private BottomSheetDialog mBottomSheetDialog;
    private LinearLayout mRootLayout;



    public FontSizeRegulator setContext(Context context) {
        this.context = context;
        return this;
    }

    public FontSizeRegulator setFontSizeChangeListener(FontSizeChangeListener fontSizeChangeListener) {
        this.fontSizeChangeListener = fontSizeChangeListener;
        return this;
    }

    public void build() {

        showSheetDialog();
    }

    private void showSheetDialog() {

        FontSizeRegulateView mFontSizeRegulateView = new FontSizeRegulateView(context);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        mRootLayout = new LinearLayout(context);
        mRootLayout.setOrientation(LinearLayout.VERTICAL);
        mRootLayout.setLayoutParams(params);
        mRootLayout.addView(mFontSizeRegulateView);

        mBottomSheetDialog = new BottomSheetDialog(context);
        mBottomSheetDialog.setContentView(mRootLayout);
        mBottomSheetDialog.setCanceledOnTouchOutside(false);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.setCanceledOnTouchOutside(true);

    }
}
