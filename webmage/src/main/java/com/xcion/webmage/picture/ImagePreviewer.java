package com.xcion.webmage.picture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.xcion.webmage.R;
import com.xcion.webmage.view.ImageLoaderView.CircleProgress;

/**
 * @Author: Kern Hu
 * @E-mail: sky580@126.com
 * @CreateDate: 2020/10/9 17:36
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/10/9 17:36
 * @Version: 1.0
 * @Description: java类作用描述
 * @UpdateRemark: 更新说明
 */
public class  ImagePreviewer {

    private String url;
    private View parent;
    private PopupWindow.OnDismissListener dismissListener;

    private static ImagePreviewer mImagePreviewer;

    private PopupWindow mPopupWindow;
    private FrameLayout mFrameLayout;
    private ImageView mImageView;
    private ImageView mCloseBtn;
    private CircleProgress mCircleProgress;

    public static ImagePreviewer getInstance(Context context) {
        if (mImagePreviewer == null) {
            mImagePreviewer = new ImagePreviewer(context);
        }
        return mImagePreviewer;
    }

    public ImagePreviewer(Context context) {

        mFrameLayout = new FrameLayout(context);
        mFrameLayout.setBackgroundColor(Color.parseColor("#3A3A3A"));

        FrameLayout.LayoutParams mImageLP = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        mImageView = new ImageView(context);
        mImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mFrameLayout.addView(mImageView, mImageLP);
        mImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_tooltip_close, null));

        FrameLayout.LayoutParams mCloseLP = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        mCloseLP.bottomMargin = 10;
        mCloseLP.topMargin = 10;
        mCloseLP.leftMargin = 10;
        mCloseLP.rightMargin = 10;
        mCloseLP.gravity = Gravity.RIGHT;
        mCloseBtn = new ImageView(context);
        mCloseBtn.setPadding(10, 10, 10, 10);
        mFrameLayout.addView(mCloseBtn, mCloseLP);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(50);
        drawable.setStroke(1, Color.parseColor("#3A3A3A"));
        drawable.setColor(Color.parseColor("#4D3A3A3A"));
        mCloseBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_tooltip_close, null));
        mCloseBtn.setBackground(drawable);
        mCloseBtn.setOnClickListener(mClosedListener);

        mCircleProgress = new CircleProgress.Builder()
                .setTextColor(Color.RED)
                .build();
        mCircleProgress.inject(mImageView);

        mPopupWindow = new PopupWindow(mFrameLayout, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setOutsideTouchable(false);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }

    public String getUrl() {
        return url;
    }

    public ImagePreviewer setUrl(String url) {
        this.url = url;
        return this;
    }

    public View getParent() {
        return parent;
    }

    public ImagePreviewer setParent(View parent) {
        this.parent = parent;
        return this;
    }

    public PopupWindow.OnDismissListener getDismissListener() {
        return dismissListener;
    }

    public ImagePreviewer setDismissListener(PopupWindow.OnDismissListener dismissListener) {
        this.dismissListener = dismissListener;
        return this;
    }

    View.OnClickListener mClosedListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mPopupWindow.dismiss();
        }
    };

    public void show() {

        mPopupWindow.setOnDismissListener(getDismissListener());
        mPopupWindow.showAsDropDown(getParent());

        ImageLoader
                .getInstance()
                .setConnectTimeout(1000 * 12)
                .setReadTimeout(1000 * 12)
                .setUrl(url)
                .setCallback(new ImageLoader.Callback() {
                    @Override
                    public void onSuccess(Bitmap bitmap) {
                        mImageView.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onFailure(String error) {
                    }

                    @Override
                    public void onProgressChanged(int progress) {
                        mCircleProgress.setLevel(progress);
                        mCircleProgress.setMaxValue(100);
                    }
                })
                .load();
    }


    public static void onDestroy() {
        if (mImagePreviewer != null) {
            mImagePreviewer = null;
        }
    }
}
