package com.xcion.webmage.longpress;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;

import com.xcion.webmage.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Kern Hu
 * @E-mail: sky580@126.com
 * @CreateDate: 2020/9/30 17:27
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/9/30 17:27
 * @Version: 1.0
 * @Description: java类作用描述
 * @UpdateRemark: 更新说明
 */
class MenuItemAdapter<T> extends ArrayAdapter<T> {

    private static final int MAX_ITEM_HEIGHT = 45;

    public MenuItemAdapter(@NonNull Context context, int resource, @NonNull List<T> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(16);
        ViewGroup.LayoutParams params = tv.getLayoutParams();
        params.height = DensityUtil.dip2px(getContext(), MAX_ITEM_HEIGHT);
        tv.setLayoutParams(params);

        Object object = (Object) getItem(position);
        if (object instanceof MenuItem.TEXT) {
            MenuItem.TEXT text = (MenuItem.TEXT) object;
            tv.setText(getContext().getResources().getString(text.getResId()));
        } else if (object instanceof MenuItem.IMAGE) {
            MenuItem.IMAGE image = (MenuItem.IMAGE) object;
            tv.setText(getContext().getResources().getString(image.getResId()));
        } else if (object instanceof MenuItem.PHONE) {
            MenuItem.PHONE phone = (MenuItem.PHONE) object;
            tv.setText(getContext().getResources().getString(phone.getResId()));
        } else if (object instanceof MenuItem.EMAIL) {
            MenuItem.EMAIL email = (MenuItem.EMAIL) object;
            tv.setText(getContext().getResources().getString(email.getResId()));
        } else if (object instanceof MenuItem.GEO) {
            MenuItem.GEO geo = (MenuItem.GEO) object;
            tv.setText(getContext().getResources().getString(geo.getResId()));
        } else if (object instanceof MenuItem.SRC_ANCHOR) {
            MenuItem.SRC_ANCHOR src_anchor = (MenuItem.SRC_ANCHOR) object;
            tv.setText(getContext().getResources().getString(src_anchor.getResId()));
        } else if (object instanceof MenuItem.IMAGE_ANCHOR) {
            MenuItem.IMAGE_ANCHOR image_anchor = (MenuItem.IMAGE_ANCHOR) object;
            tv.setText(getContext().getResources().getString(image_anchor.getResId()));
        }
        return convertView;
    }
}
