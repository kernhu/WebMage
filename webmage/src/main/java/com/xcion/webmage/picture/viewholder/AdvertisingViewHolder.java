package com.xcion.webmage.picture.viewholder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Author: Kern
 * E-mail: sky580@126.com
 * DateTime: 2021/10/12  23:56
 * Intro:
 */
public class AdvertisingViewHolder extends RecyclerView.ViewHolder {

    public ViewGroup containerView;

    public AdvertisingViewHolder(@NonNull View itemView) {
        super(itemView);
        containerView = new FrameLayout(itemView.getContext());
    }
}
