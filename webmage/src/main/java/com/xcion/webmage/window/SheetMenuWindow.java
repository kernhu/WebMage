package com.xcion.webmage.window;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import com.xcion.webmage.R;

/**
 * @Author: Kern Hu
 * @E-mail: sky580@126.com
 * @CreateDate: 2020/12/9 17:54
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/12/9 17:54
 * @Version: 1.0
 * @Description: java类作用描述
 * @UpdateRemark: 更新说明
 */
public class SheetMenuWindow implements View.OnClickListener, DialogInterface.OnDismissListener {

    private Context context;
    private String[] menuItem;
    private OnItemClickListener onItemClickListener;
    private LinearLayout mRootLayout;
    private GradientDrawable drawable;
    private BottomSheetDialog mBottomSheetDialog;
    private int padding;
    private int position = -1;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public SheetMenuWindow setMenuItem(String... menuItem) {
        this.menuItem = menuItem;
        return this;
    }

    public SheetMenuWindow setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.onItemClickListener = itemClickListener;
        return this;
    }

    public static SheetMenuWindow getInstance(Context context) {
        return new SheetMenuWindow(context);
    }

    public SheetMenuWindow(Context context) {
        this.context = context;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        mRootLayout = new LinearLayout(context);
        mRootLayout.setOrientation(LinearLayout.VERTICAL);
        mRootLayout.setLayoutParams(params);
        mRootLayout.setBackgroundColor(Color.TRANSPARENT);
        padding = (int) context.getResources().getDimension(R.dimen.sheet_menu_window_padding);
        mRootLayout.setPadding(padding, padding, padding, padding);

        mBottomSheetDialog = new BottomSheetDialog(context);
        mBottomSheetDialog.setContentView(mRootLayout);
        mBottomSheetDialog.setCanceledOnTouchOutside(false);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.setCanceledOnTouchOutside(true);
        mBottomSheetDialog.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
        mBottomSheetDialog.setOnDismissListener(this);

        drawable = new GradientDrawable();
        drawable.setCornerRadius(context.getResources().getDimension(R.dimen.sheet_menu_window_radius));
        drawable.setStroke((int) context.getResources().getDimension(R.dimen.sheet_menu_window_stroke), Color.parseColor("#cccccc"));
        drawable.setColor(Color.parseColor("#eeeeee"));
    }

    public void show() {
        if (menuItem == null)
            return;

        for (int i = 0; i < menuItem.length; i++) {
            String item = menuItem[i];
            TextView mItemView = new TextView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            mItemView.setText(item);
            params.gravity = Gravity.CENTER;
            params.topMargin = (int) context.getResources().getDimension(R.dimen.sheet_menu_window_margin);
            params.bottomMargin = (int) context.getResources().getDimension(R.dimen.sheet_menu_window_margin);
            mItemView.setGravity(Gravity.CENTER);
            mItemView.setBackgroundDrawable(drawable);
            mItemView.setTextSize(TypedValue.COMPLEX_UNIT_PX,context.getResources().getDimension(R.dimen.sheet_menu_window_text_size));
            mItemView.setPadding(0, padding, 0, padding);
            mItemView.setId(336699 + i);
            mItemView.setTag(i);
            mItemView.setOnClickListener(this);
            mRootLayout.addView(mItemView, params);
        }

        mBottomSheetDialog.show();
    }

    public void dismiss() {
        mBottomSheetDialog.dismiss();
    }


    @Override
    public void onClick(View v) {
        position = (int) v.getTag();
        dismiss();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(position);
        }
    }


}
