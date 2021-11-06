package com.xcion.web.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.xcion.web.R;
import com.xcion.web.bean.ItemInfo;

/**
 * @Author: Kern Hu
 * @E-mail: sky580@126.com
 * @CreateDate: 2020/11/7 12:42
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/11/7 12:42
 * @Version: 1.0
 * @Description: java类作用描述
 * @UpdateRemark: 更新说明
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private Context mContext;
    private OnItemClickListener itemClickListener;
    private List<ItemInfo> mData = new ArrayList<>();

    public RecyclerAdapter(Context mContext, List<ItemInfo> mData) {
        this.mContext = mContext;
        this.mData.addAll(mData);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_card_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ItemInfo info = mData.get(position);
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(this);

        MyViewHolder viewHolder = (MyViewHolder) holder;
        viewHolder.mTitleText.setText(info.getTitle());
        viewHolder.mDescText.setText(info.getDesc());

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public void setUpdate(List<ItemInfo> data, boolean isRefresh) {
        if (data != null) {
            if (isRefresh) {
                mData.clear();
            }
            mData.addAll(data);
            notifyDataSetChanged();
        }
    }


    public ItemInfo getCurrentItem(int position) {
        if (position < mData.size()) {
            return mData.get(position);
        }
        return null;
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        if (itemClickListener != null)
            itemClickListener.onItemClick(v, position);
    }

    public interface OnItemClickListener {

        void onItemClick(View view, int position);
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        CardView mCardView;
        TextView mTitleText;
        TextView mDescText;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mCardView = itemView.findViewById(R.id.item_card_view);
            mTitleText = itemView.findViewById(R.id.item_title_text);
            mDescText = itemView.findViewById(R.id.item_desc_text);
        }
    }
}
