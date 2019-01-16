package com.znt.vodbox.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.vodbox.bean.ZoneInfo;
import com.znt.vodbox.utils.binding.Bind;
import com.znt.vodbox.utils.binding.ViewBinder;

import java.util.List;


public class ZoneListAdapter extends BaseAdapter {
    private List<ZoneInfo> dataList;
    private OnMoreClickListener listener;
    private boolean isPlaylist=true;

    public ZoneListAdapter(List<ZoneInfo> dataList) {
        this.dataList = dataList;
    }

    public void notifyDataSetChanged(List<ZoneInfo> dataList)
    {
        this.dataList = dataList;
        notifyDataSetChanged();
    }


    public void setOnMoreClickListener(OnMoreClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_my_zones, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ZoneInfo tempInfo = dataList.get(position);
        holder.ivCover.setImageResource(R.drawable.default_cover);
        holder.tvName.setText(tempInfo.getGroupName());
        if(!TextUtils.isEmpty(tempInfo.getAdminId()))
            holder.tvAccount.setText(tempInfo.getAdminId());
        else
            holder.tvAccount.setText("无");

        holder.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onMoreClick(position);
                }
            }
        });
        holder.vDivider.setVisibility(isShowDivider(position) ? View.VISIBLE : View.GONE);
        return convertView;
    }

    private boolean isShowDivider(int position) {
        return position != dataList.size() - 1;
    }

    private static class ViewHolder {
        @Bind(R.id.iv_my_zone_cover)
        private ImageView ivCover;

        @Bind(R.id.tv_my_zone_name)
        private TextView tvName;

        @Bind(R.id.tv_my_zone_account)
        private TextView tvAccount;

        @Bind(R.id.iv_my_zone_more)
        private ImageView ivMore;

        @Bind(R.id.v_my_zone_divider)
        private View vDivider;


        public ViewHolder(View view) {
            ViewBinder.bind(this, view);
        }
    }
}
