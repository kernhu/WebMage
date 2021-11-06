package com.xcion.webmage.picture.viewholder;

import android.view.View;

import com.xcion.webmage.picture.widget.MatrixImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Author: Kern
 * E-mail: sky580@126.com
 * DateTime: 2021/10/12  01:15
 * Intro:
 */
public class ImageViewHolder extends RecyclerView.ViewHolder {

    public MatrixImageView imageView;

    public ImageViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = new MatrixImageView(itemView.getContext());
        imageView.enable();
        imageView.enableRotate();
    }
}
