package com.znt.vodbox.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.znt.vodbox.R;
import com.znt.vodbox.bean.AlbumInfo;
import com.znt.vodbox.utils.binding.Bind;
import com.znt.vodbox.utils.binding.ViewBinder;

import java.util.List;

/**
 * 上拉加载更多
 * Created by yangle on 2017/10/12.
 */

public class DYMusicAlbumAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext = null;

    private List<AlbumInfo> dataList;
    private OnMoreClickListener listener;
    public interface OnItemClickListener
    {
        void onItemClick(View view);
    }

    public void setOnMoreClickListener(OnMoreClickListener listener) {
        this.listener = listener;
    }

    private OnItemClickListener mOnItemClickListener = null;
    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener)
    {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public DYMusicAlbumAdapter(Context mContext, List<AlbumInfo> dataList)
    {
        this.mContext = mContext;
        this.dataList = dataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {

        if(viewType == 0)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_dy_albums_item_card_layout, parent, false);
            return new RecyclerViewHolder(view);
        }
        /*else if(viewType == 1)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_rank_item_type, parent, false);
            return new TypeViewHolder(view);
        }*/

        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return dataList.get(position).getType();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position)
    {

        if(vh instanceof RecyclerViewHolder)
        {
            final RecyclerViewHolder holder = (RecyclerViewHolder) vh;

            AlbumInfo tempInfo = dataList.get(position);

            holder.tvName.setText(tempInfo.getName());
            holder.tvCount.setText(tempInfo.getMusicNum());
            if(!TextUtils.isEmpty(tempInfo.getImageUrl()))
                Glide.with(mContext).load(tempInfo.getImageUrl()).placeholder(R.drawable.default_cover)
                .error(R.drawable.default_cover).into(holder.imageView);
            holder.ivMore.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMoreClick(position);
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(mOnItemClickListener != null)
                        mOnItemClickListener.onItemClick(view);
                }
            });

            holder.itemView.setTag(position);

        }


        /*else if(vh instanceof TypeViewHolder)
        {
            TypeViewHolder recyclerViewHolder = (TypeViewHolder) vh;
            *//*GameBean mGameBean = dataList.get(position);
            if(mGameBean.getTypeCategory().equals(GameBean.RENQI))
                recyclerViewHolder.ivType.setImageResource(R.mipmap.type_rank_renqi);
            else if(mGameBean.getTypeCategory().equals(GameBean.JINGDIAN))
                recyclerViewHolder.ivType.setImageResource(R.mipmap.type_rank_jingdian);
            else if(mGameBean.getTypeCategory().equals(GameBean.XINPIN))
                recyclerViewHolder.ivType.setImageResource(R.mipmap.type_rank_xinpin);*//*
        }*/

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    private class RecyclerViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.image_view)
        public ImageView imageView;
        @Bind(R.id.iv_more)
        public ImageView ivMore;
        @Bind(R.id.tv_home_item_name)
        public TextView tvName;
        @Bind(R.id.tv_home_item_count)
        public TextView tvCount;
        @Bind(R.id.view_home_item_desc_bg)
        public View viewItemDesc = null;
        @Bind(R.id.view_home_item_bg)
        public View viewItemBg = null;



        public RecyclerViewHolder(View itemView) {
            super(itemView);
            ViewBinder.bind(this, itemView);
        }
    }
}
