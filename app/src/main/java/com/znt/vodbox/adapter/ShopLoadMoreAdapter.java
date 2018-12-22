package com.znt.vodbox.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.vodbox.model.Shopinfo;
import com.znt.vodbox.utils.DateUtils;
import com.znt.vodbox.utils.binding.Bind;
import com.znt.vodbox.utils.binding.ViewBinder;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 上拉加载更多
 * Created by yangle on 2017/10/12.
 */

public class ShopLoadMoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext = null;

    private List<Shopinfo> dataList;
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

    public ShopLoadMoreAdapter(Context mContext, List<Shopinfo> dataList)
    {
        this.mContext = mContext;
        this.dataList = dataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {

        if(viewType == 0)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_shops, parent, false);
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
    public void onBindViewHolder(RecyclerView.ViewHolder vh, final int position)
    {

        if(vh instanceof RecyclerViewHolder)
        {
            final RecyclerViewHolder holder = (RecyclerViewHolder) vh;

            Shopinfo shopinfo = dataList.get(position);

            holder.ivCover.setImageResource(R.drawable.icon_shop);
            holder.tvShopName.setText(shopinfo.getName());

            if(shopinfo.getGroup() != null && !TextUtils.isEmpty(shopinfo.getGroup().getGroupName()))
            {
                holder.tvGroup.setText(" - " + shopinfo.getGroup().getGroupName());
                holder.tvGroup.setTextColor(mContext.getResources().getColor(R.color.text_black_mid));
            }
            else
            {
                holder.tvGroup.setText(" - " + mContext.getResources().getString(R.string.dev_group_belong_none));
                holder.tvGroup.setTextColor(mContext.getResources().getColor(R.color.text_black_off));
            }

            if(shopinfo.getTmlRunStatus() != null
                    && shopinfo.getTmlRunStatus().size() >0)
            {

                holder.tvDevId.setText(shopinfo.getTmlRunStatus().get(0).getTerminalId());

                if(shopinfo.getTmlRunStatus().get(0).getOnlineStatus() != null &&
                        shopinfo.getTmlRunStatus().get(0).getOnlineStatus().equals("1"))
                {
                    holder.tvStatus.setText(mContext.getResources().getString(R.string.dev_status_online));
                    holder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.text_blue_on));
                    holder.tvCurSong.setTextColor(mContext.getResources().getColor(R.color.text_blue_on));
                    holder.tvShopName.setTextColor(mContext.getResources().getColor(R.color.text_blue_on));
                }
                else
                {
                    holder.tvStatus.setText(mContext.getResources().getString(R.string.dev_status_offline));
                    holder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.text_black_off));
                    holder.tvCurSong.setTextColor(mContext.getResources().getColor(R.color.text_black_off));
                    holder.tvShopName.setTextColor(mContext.getResources().getColor(R.color.text_black_on));
                }

                if(!TextUtils.isEmpty(shopinfo.getTmlRunStatus().get(0).getPlayingSong()))
                    holder.tvCurSong.setText(shopinfo.getTmlRunStatus().get(0).getPlayingSong());
                else
                    holder.tvCurSong.setText(mContext.getResources().getString(R.string.dev_shop_play_none));

                if(!TextUtils.isEmpty(shopinfo.getTmlRunStatus().get(0).getLastConnTime()))
                {
                    String time = DateUtils.getStringTime(Long.parseLong(shopinfo.getTmlRunStatus().get(0).getLastConnTime()));
                    holder.tvOnlineTime.setText(time);
                }
            }
            else
            {
                holder.tvCurSong.setText(mContext.getResources().getString(R.string.dev_shop_none_device));
                holder.tvOnlineTime.setVisibility(View.GONE);
            }

            if(!TextUtils.isEmpty(shopinfo.getAddress()))
                holder.tvAddr.setText(shopinfo.getAddress());
            else
                holder.tvAddr.setText(mContext.getResources().getString(R.string.dev_shop_addr_none));

            holder.ivMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onMoreClick(position);
                    }
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

        @Bind(R.id.v_all_shop_playing)
        private View vPlaying;
        @Bind(R.id.iv_all_shop_cover)
        private CircleImageView ivCover;
        @Bind(R.id.tv_all_shop_name)
        private TextView tvShopName;
        @Bind(R.id.tv_all_shop_curplay)
        private TextView tvCurSong;
        @Bind(R.id.tv_all_shop_addr)
        private TextView tvAddr;
        @Bind(R.id.tv_all_shop_online_time)
        private TextView tvOnlineTime;
        @Bind(R.id.iv_all_shop_more)
        private ImageView ivMore;
        @Bind(R.id.tv_all_shop_group)
        private TextView tvGroup;
        @Bind(R.id.tv_all_shop_status)
        private TextView tvStatus;
        @Bind(R.id.tv_all_shop_dev)
        private TextView tvDevId;


        public RecyclerViewHolder(View itemView) {
            super(itemView);
            ViewBinder.bind(this, itemView);
        }
    }
}
