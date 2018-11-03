package com.znt.vodbox.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.vodbox.model.Shopinfo;
import com.znt.vodbox.utils.DateUtils;
import com.znt.vodbox.utils.binding.Bind;
import com.znt.vodbox.utils.binding.ViewBinder;

import java.util.List;



public class ShoplistAdapter extends BaseAdapter {

    private Activity mContext = null;
    private List<Shopinfo> shopList;
    private OnMoreClickListener listener;
    private boolean isPlaylist=true;

    public ShoplistAdapter(Activity mContext, List<Shopinfo> shopList) {
        this.mContext = mContext;
        this.shopList = shopList;
    }

    public void notifyDataSetChanged(List<Shopinfo> shopList)
    {
        this.shopList = shopList;
        notifyDataSetChanged();
    }


    public void setOnMoreClickListener(OnMoreClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return shopList.size();
    }

    @Override
    public Object getItem(int position) {
        return shopList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_shops, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //holder.vPlaying.setVisibility((isPlaylist && position == AudioPlayer.get().getPlayPosition()) ? View.VISIBLE : View.INVISIBLE);
        Shopinfo shopinfo = shopList.get(position);
        holder.ivCover.setImageResource(R.drawable.icon_shop);
        holder.tvShopName.setText(shopinfo.getName());
        holder.tvDevId.setText(shopinfo.getId());
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

        if(shopinfo.getTmlRunStatus() != null && shopinfo.getTmlRunStatus().size() >0)
        {
            if(shopinfo.getTmlRunStatus().get(0).getOnlineStatus().equals("1"))
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

        holder.ivMore.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMoreClick(position);
            }
        });
        holder.vDivider.setVisibility(isShowDivider(position) ? View.VISIBLE : View.GONE);
        return convertView;
    }

    private boolean isShowDivider(int position) {
        return position != shopList.size() - 1;
    }

    private static class ViewHolder {
        @Bind(R.id.v_all_shop_playing)
        private View vPlaying;
        @Bind(R.id.iv_all_shop_cover)
        private ImageView ivCover;
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
        @Bind(R.id.v_all_shop_divider)
        private View vDivider;

        public ViewHolder(View view) {
            ViewBinder.bind(this, view);
        }
    }
}
