package com.xcion.webmage.errorpage;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xcion.webmage.WebMageController;
import com.xcion.webmage.R;

import java.lang.ref.WeakReference;

/**
 * @Author: Kern Hu
 * @E-mail: sky580@126.com
 * @CreateDate: 2020/11/16 10:28
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/11/16 10:28
 * @Version: 1.0
 * @Description: WebView custom error pages
 */
public class ErrorPages {

    private static ErrorPages mErrorPages;
    private WeakReference<Context> contextReference;
    private int errorCode;
    private String description;
    private WebView webView;
    private ViewGroup viewParent;
    private WebMageController controller;

    private RelativeLayout mCanvasLayout;
    private TextView mTextView;
    private Button mNetworkBtn;
    private RelativeLayout.LayoutParams mRootLP = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    private LinearLayout.LayoutParams mChildLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    private boolean isShowing = false;

    public static ErrorPages getInstance(Context context) {
        synchronized (ErrorPages.class) {
            if (mErrorPages == null) {
                mErrorPages = new ErrorPages(context);
            }
            return mErrorPages;
        }
    }

    public ErrorPages(Context context) {
        this.contextReference = new WeakReference<>(context);

        mCanvasLayout = new RelativeLayout(context);
        mCanvasLayout.setBackgroundColor(context.getResources().getColor(android.R.color.white));
        mCanvasLayout.setFocusable(true);
        mCanvasLayout.setLongClickable(true);

        //error page container for image/description and button
        final LinearLayout mRootView = new LinearLayout(context);
        mRootView.setOrientation(LinearLayout.VERTICAL);
        mRootView.setGravity(Gravity.CENTER);
        mRootLP.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mCanvasLayout.post(new Runnable() {
            @Override
            public void run() {
                if (mCanvasLayout != null) {
                    mRootLP.topMargin = mCanvasLayout.getMeasuredHeight() / 8;
                    mCanvasLayout.addView(mRootView, mRootLP);
                }
            }
        });

        //error page description
        mTextView = new TextView(context);
        mTextView.setTextColor(context.getResources().getColor(R.color.error_page_text_color));
        mTextView.setText(context.getResources().getString(R.string.error_page_description));
        mTextView.setPadding(10, 20, 10, 20);
        mTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        mTextView.setLineSpacing(1, 1.2f);
        Drawable drawable = context.getResources().getDrawable(R.drawable.ic_page_error, null);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        mTextView.setCompoundDrawables(null, drawable, null, null);
        mChildLP.gravity = Gravity.CENTER;
        mRootView.addView(mTextView, mChildLP);

        //reload button
        Button mReloadBtn = new Button(context);
        mReloadBtn.setGravity(Gravity.CENTER);
        mReloadBtn.setText(context.getResources().getString(R.string.error_page_reload));
        mReloadBtn.setPadding(25, 5, 25, 5);
        mChildLP.topMargin = 40;
        mChildLP.gravity = Gravity.CENTER;
        mRootView.addView(mReloadBtn, mChildLP);
        mReloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (controller != null) {
                    controller.onErrorPagerReload(v);
                }
            }
        });

        //network button
        mNetworkBtn = new Button(context);
        mNetworkBtn.setGravity(Gravity.CENTER);
        mNetworkBtn.setText(context.getResources().getString(R.string.error_page_network));
        mNetworkBtn.setPadding(25, 5, 25, 5);
        mChildLP.topMargin = 10;
        mChildLP.gravity = Gravity.CENTER;
        mRootView.addView(mNetworkBtn, mChildLP);
        mNetworkBtn.setVisibility(View.GONE);
        mNetworkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (controller != null) {
                    controller.onErrorPagerCheckNetwork(v);
                }
            }
        });

    }

    public ErrorPages setErrorCode(int errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public ErrorPages setDescription(String description) {
        this.description = description;
        return this;
    }

    public ErrorPages setWebView(WebView webView) {
        this.webView = webView;
        return this;
    }

    public ErrorPages setViewParent(ViewGroup viewParent) {
        this.viewParent = viewParent;
        return this;
    }

    public ErrorPages setController(WebMageController controller) {
        this.controller = controller;
        return this;
    }

    public void show() {
        if (isShowing) {
            return;
        }
        isShowing = true;
        if (viewParent != null) {
            if (viewParent.indexOfChild(mCanvasLayout) != -1) {
                mCanvasLayout.bringToFront();
            } else {
                viewParent.addView(mCanvasLayout);
            }
        }
        String desc = String.format(contextReference.get().getResources().getString(R.string.error_page_description), String.valueOf(errorCode), description);
        SpannableString spannable = new SpannableString(desc);
        spannable.setSpan(new RelativeSizeSpan(1.2f), 0, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new RelativeSizeSpan(0.8f), 12, desc.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(contextReference.get().getResources().getColor(R.color.error_page_text_color)), 0, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(contextReference.get().getResources().getColor(R.color.error_page_text_hint_color)), 12, desc.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTextView.setText(spannable);

        mNetworkBtn.setVisibility(View.VISIBLE);
    }

    public void dismiss() {
        isShowing = false;
        if (viewParent != null) {
            if (viewParent.indexOfChild(webView) != -1) {
                webView.bringToFront();
            } else {
                viewParent.addView(webView);
            }
        }
        viewParent = null;
        webView = null;
        mCanvasLayout = null;
        mErrorPages = null;
        contextReference.clear();
        contextReference = null;
    }


    public void onDestroy() {
        isShowing = false;
        viewParent = null;
        webView = null;
        mCanvasLayout = null;
        mErrorPages = null;
        contextReference.clear();
        contextReference = null;
    }
}
