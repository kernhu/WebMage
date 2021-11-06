 package com.xcion.webmage.picture;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Author: Kern
 * E-mail: sky580@126.com
 * DateTime: 2021/10/12  01:10
 * Intro:
 */
public class ImagePreviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_TYPE_IMAGE = 0;
    private static final int ITEM_TYPE_ADVERTISING = 1;

    private Context context;
    private OnImageClickListener clickListener;
    private List<Object> dataList = new ArrayList<>();

    public ImagePreviewAdapter(Context context, OnImageClickListener clickListener) {
        this.context = context;
        this.clickListener = clickListener;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (dataList.get(position) instanceof String) {
            return ITEM_TYPE_IMAGE;
        } else if (dataList.get(position) instanceof View) {
            return ITEM_TYPE_ADVERTISING;
        }
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_ADVERTISING) {

        } else {

        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }


}
