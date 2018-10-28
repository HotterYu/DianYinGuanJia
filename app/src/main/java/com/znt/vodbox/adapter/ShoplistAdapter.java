package com.znt.vodbox.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.vodbox.model.Shopinfo;
import com.znt.vodbox.service.AudioPlayer;
import com.znt.vodbox.utils.binding.Bind;
import com.znt.vodbox.utils.binding.ViewBinder;

import java.util.List;



public class ShoplistAdapter extends BaseAdapter {
    private List<Shopinfo> shopList;
    private OnMoreClickListener listener;
    private boolean isPlaylist=true;

    public ShoplistAdapter(List<Shopinfo> shopList) {
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
        holder.vPlaying.setVisibility((isPlaylist && position == AudioPlayer.get().getPlayPosition()) ? View.VISIBLE : View.INVISIBLE);
        Shopinfo shopinfo = shopList.get(position);
        holder.ivCover.setImageResource(R.drawable.icon_shop);
        holder.tvShopName.setText(shopinfo.getName());
        if(shopinfo.getTmlRunStatus() != null && shopinfo.getTmlRunStatus().size() >0)
        {
            holder.tvCurSong.setText(shopinfo.getTmlRunStatus().get(0).getPlayingSong());
            holder.tvOnlineTime.setText(shopinfo.getTmlRunStatus().get(0).getLastConnTime());
        }

        holder.tvAddr.setText(shopinfo.getAddress());
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
        @Bind(R.id.v_all_shop_divider)
        private View vDivider;

        public ViewHolder(View view) {
            ViewBinder.bind(this, view);
        }
    }
}
