package com.xcion.webmage.deeplink;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xcion.webmage.R;
import com.xcion.webmage.popupwindow.MagePopupWindow;
import com.xcion.webmage.popupwindow.XGravity;
import com.xcion.webmage.popupwindow.YGravity;

import java.lang.ref.WeakReference;


/**
 * window of custom for deeplink
 */
public class DeeplinkWindow {

    private WeakReference<Context> context;
    private LinearLayout rootLayout;
    private TextView titleView;
    private TextView messageView;
    private Button negativeButton;
    private Button positiveButton;
    private MagePopupWindow popupWindow;

    private String title;
    private String message;
    private int icon;
    private String positiveBtn;
    private String negativeBtn;
    private ViewGroup anchorView;
    private DeeplinkWindow.Callback callback;
    private DeeplinkWindow.Result result;

    public DeeplinkWindow(Context context) {
        this.context = new WeakReference<>(context);
        initView(context);
    }

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

    public String getTitle() {
        return title;
    }

    public DeeplinkWindow setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public DeeplinkWindow setMessage(String message) {
        this.message = message;
        return this;
    }

    public int getIcon() {
        return icon;
    }

    public DeeplinkWindow setIcon(int icon) {
        this.icon = icon;
        return this;
    }

    public String getPositiveBtn() {
        return positiveBtn;
    }

    public DeeplinkWindow setPositiveBtn(String positiveBtn) {
        this.positiveBtn = positiveBtn;
        return this;
    }

    public String getNegativeBtn() {
        return negativeBtn;
    }

    public DeeplinkWindow setNegativeBtn(String negativeBtn) {
        this.negativeBtn = negativeBtn;
        return this;
    }

    public ViewGroup getAnchorView() {
        return anchorView;
    }

    public DeeplinkWindow setAnchorView(ViewGroup anchorView) {
        this.anchorView = anchorView;
        return this;
    }

    public DeeplinkWindow.Callback getCallback() {
        return callback;
    }

    public DeeplinkWindow setCallback(DeeplinkWindow.Callback callback) {
        this.callback = callback;
        return this;
    }

    private void initView(Context context) {

        rootLayout = new LinearLayout(context);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setBackground(getBackground(context.getResources().getColor(R.color.theme_hint_background, null)));
        rootLayout.setPadding(50, 50, 50, 50);

        titleView = new TextView(context);
        titleView.setTextColor(context.getResources().getColor(R.color.theme_hint_title, null));
        titleView.setTypeface(Typeface.DEFAULT_BOLD);
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.dimen_hint));
        titleView.setCompoundDrawablePadding(10);
        titleView.setGravity(Gravity.CENTER_VERTICAL);
        rootLayout.addView(titleView);

        messageView = new TextView(context);
        messageView.setTextColor(context.getResources().getColor(R.color.theme_hint_message, null));
        messageView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.dimen_hint));
        messageView.setPadding(0, 30, 0, 30);
        rootLayout.addView(messageView);

        LinearLayout footerLayout = new LinearLayout(context);
        footerLayout.setOrientation(LinearLayout.HORIZONTAL);
        footerLayout.setPadding(0, 20, 0, 20);
        footerLayout.setGravity(Gravity.RIGHT);
        rootLayout.addView(footerLayout);

        /**
         * background
         * */
        int[] attrs = new int[]{android.R.attr.selectableItemBackgroundBorderless};
        TypedArray ta = context.obtainStyledAttributes(attrs);
        Drawable defaultFocusHighlightCache1 = ta.getDrawable(0);
        Drawable defaultFocusHighlightCache2 = ta.getDrawable(0);
        ta.recycle();

        positiveButton = new Button(context, null, 0);
        positiveButton.setPadding(40, 0, 40, 0);
        positiveButton.setTextColor(context.getResources().getColor(R.color.theme_hint_positive, null));
        positiveButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.dimen_hint));
        positiveButton.setBackground(defaultFocusHighlightCache1);

        negativeButton = new Button(context, null, 0);
        negativeButton.setPadding(40, 0, 40, 0);
        negativeButton.setTextColor(context.getResources().getColor(R.color.theme_hint_negative, null));
        negativeButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.dimen_hint));
        negativeButton.setBackground(defaultFocusHighlightCache2);

        footerLayout.addView(positiveButton);
        footerLayout.addView(negativeButton);

        positiveButton.setOnClickListener(positiveClickListener);
        negativeButton.setOnClickListener(negativeClickListener);

        popupWindow = MagePopupWindow.create(context)
                .setContentView(rootLayout, getScreenWidth(), LinearLayout.LayoutParams.WRAP_CONTENT)
                .setFocusAndOutsideEnable(true)
                .setBackgroundDimEnable(true)
                .setDimValue(0.4f)
                .setOnDismissListener(dismissListener)
                .setDimColor(context.getResources().getColor(R.color.theme_hint_shadow, null));

    }

    public void show() {
        Drawable drawable = context.get().getResources().getDrawable(icon, null);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        titleView.setCompoundDrawables(drawable, null, null, null);
        titleView.setText(getTitle());
        messageView.setText(getMessage());
        negativeButton.setText(getNegativeBtn());
        positiveButton.setText(getPositiveBtn());
        popupWindow.showAtAnchorView(anchorView, YGravity.ALIGN_BOTTOM, XGravity.CENTER, 0, -30);
    }

    /**
     * @param color
     * @return the background of PopupWindow
     */
    public Drawable getBackground(int color) {
        GradientDrawable gradient = new GradientDrawable();
        gradient.setCornerRadius(20);
        gradient.setColor(color);
        return gradient;
    }

    /**
     * @return the width of screen
     */
    public int getScreenWidth() {
        return (int) (context.get().getResources().getDisplayMetrics().widthPixels * 0.9);
    }

    /***************************************************************************/
    View.OnClickListener positiveClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            result = Result.POSITIVE;
            popupWindow.dismiss();
        }
    };

    View.OnClickListener negativeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            result = Result.NEGATIVE;
            popupWindow.dismiss();
        }
    };

    PopupWindow.OnDismissListener dismissListener = new PopupWindow.OnDismissListener() {

        @Override
        public void onDismiss() {
            if (callback != null) {
                if (result == Result.POSITIVE) {
                    callback.onPositive();
                } else if (result == Result.NEGATIVE) {
                    callback.onNegative();
                } else {
                }
            }
        }
    };
}
