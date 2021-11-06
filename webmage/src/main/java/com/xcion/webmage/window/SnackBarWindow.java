package com.xcion.webmage.window;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.xcion.webmage.R;

/**
 * @Author: Kern Hu
 * @E-mail: sky580@126.com
 * @CreateDate: 2020/10/9 11:16
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/10/9 11:16
 * @Version: 1.0
 * @Description: java类作用描述
 * @UpdateRemark: 更新说明
 */
public class SnackBarWindow {

    private Activity activity;
    private String title;
    private String message;
    private String positiveBtn;
    private String negativeBtn;
    private Callback callback;
    private Result result;

    public enum Result {
        /**
         * success or allowable
         */
        POSITIVE,
        /**
         * fail or rejective
         */
        NEGATIVE,
    }

    public interface Callback {

        /**
         * success or allowable
         */
        void onPositive();

        /**
         * fail or rejective
         */
        void onNegative();

    }

    public static SnackBarWindow getInstance() {
        return new SnackBarWindow();
    }

    public Activity getActivity() {
        return activity;
    }

    public SnackBarWindow setActivity(Activity activity) {
        this.activity = activity;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public SnackBarWindow setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public SnackBarWindow setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getPositiveBtn() {
        return positiveBtn;
    }

    public SnackBarWindow setPositiveBtn(String positiveBtn) {
        this.positiveBtn = positiveBtn;
        return this;
    }

    public String getNegativeBtn() {
        return negativeBtn;
    }

    public SnackBarWindow setNegativeBtn(String negativeBtn) {
        this.negativeBtn = negativeBtn;
        return this;
    }

    public Callback getCallback() {
        return callback;
    }

    public SnackBarWindow setCallback(Callback callback) {
        this.callback = callback;
        return this;
    }

    public void show() {
        showIt(getActivity(), getTitle(), getMessage(), getPositiveBtn(), getNegativeBtn(), getCallback());
    }


    private void showIt(Activity activity, String title, String message, String positiveBtn,
                        String negativeBtn, Callback callback) {
        /**
         * background
         * */
        int[] attrs = new int[]{android.R.attr.selectableItemBackgroundBorderless};
        TypedArray ta = activity.obtainStyledAttributes(attrs);
        Drawable defaultFocusHighlightCache1 = ta.getDrawable(0);
        Drawable defaultFocusHighlightCache2 = ta.getDrawable(0);
        ta.recycle();

        /**
         * instance snack bar
         * */
        SpannableString spannableString = new SpannableString(title + message);
        spannableString.setSpan(new ForegroundColorSpan(Color.GRAY), 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new RelativeSizeSpan(1.5f), 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        Snackbar snackbar = Snackbar.make(activity.getCurrentFocus(), spannableString.toString(), Snackbar.LENGTH_INDEFINITE);
        /**
         *
         * */
        TextView msgView = ((TextView) snackbar.getView().findViewById(R.id.snackbar_text));
        msgView.setText(spannableString);
        /**
         *
         * */
        Button positiveButton = snackbar.getView().findViewById(R.id.snackbar_action);
        positiveButton.setBackground(defaultFocusHighlightCache1);
        LinearLayout.LayoutParams positiveLP = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
        positiveLP.gravity = Gravity.BOTTOM;
        positiveButton.setLayoutParams(positiveLP);
        /**
         * positive listener
         * */
        snackbar.setAction(positiveBtn, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result = Result.POSITIVE;
                snackbar.dismiss();
            }
        });
        /**
         * instance negative button
         * */
        Button negativeButton = new Button(activity, null, 0);
        negativeButton.setText(negativeBtn);
        negativeButton.setPadding(30, 20, 30, 20);
        negativeButton.setTextColor(Color.GRAY);
        negativeButton.setBackground(defaultFocusHighlightCache2);
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result = Result.NEGATIVE;
                snackbar.dismiss();
            }
        });
        /**
         * add negative button
         * */
        Snackbar.SnackbarLayout snackLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        p.gravity = Gravity.BOTTOM;
        p.leftMargin = 10;
        ((LinearLayout) snackLayout.getChildAt(0)).addView(negativeButton, 2, p);
        /**
         * add show and dismiss listener
         * */
        snackbar.addCallback(new Snackbar.Callback() {

            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                if (result == Result.POSITIVE) {
                    if (callback != null) {
                        callback.onPositive();
                    }
                } else if (result == Result.NEGATIVE) {
                    if (callback != null) {
                        callback.onNegative();
                    }
                }
            }

        });
        /**
         * show it
         * */
        snackbar.show();
    }
}
