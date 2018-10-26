package com.znt.vodbox.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.vodbox.bean.PlanInfo;
import com.znt.vodbox.model.Shopinfo;
import com.znt.vodbox.service.AudioPlayer;
import com.znt.vodbox.utils.DateUtils;
import com.znt.vodbox.utils.binding.Bind;
import com.znt.vodbox.utils.binding.ViewBinder;

import java.util.List;


public class PlanlistAdapter extends BaseAdapter {
    private List<PlanInfo> dataList;
    private OnMoreClickListener listener;
    private boolean isPlaylist=true;

    public PlanlistAdapter(List<PlanInfo>  dataList) {
        this.dataList = dataList;
    }

    public void notifyDataSetChanged(List<PlanInfo>  dataList)
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_plan_list, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.vPlaying.setVisibility((isPlaylist && position == AudioPlayer.get().getPlayPosition()) ? View.VISIBLE : View.INVISIBLE);
        PlanInfo tempInfo = dataList.get(position);
        holder.ivCover.setImageResource(R.drawable.default_cover);
        holder.tvPlanName.setText(tempInfo.getPlanName());
        if(tempInfo.isGroupPlan())
            holder.tvForShop.setText("指定分区:"+tempInfo.getGroupName());
        else
            holder.tvForShop.setText("无分区");

        if(TextUtils.isEmpty(tempInfo.getStartDate()))
            holder.tvDate.setText("播放时间：每天");
        else
            holder.tvDate.setText("播放时间："+tempInfo.getStartDate() + "至" + tempInfo.getEndDate());
        if(!TextUtils.isEmpty(tempInfo.getAddTime()))
            holder.tvCreateTime.setText("创建时间:"+ DateUtils.getDateFromLong(Long.parseLong(tempInfo.getAddTime())));
        holder.ivMore.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMoreClick(position);
            }
        });
        holder.vDivider.setVisibility(isShowDivider(position) ? View.VISIBLE : View.GONE);
        return convertView;
    }

    private boolean isShowDivider(int position) {
        return position != dataList.size() - 1;
    }

    private static class ViewHolder {
        @Bind(R.id.v_plan_playing)
        private View vPlaying;
        @Bind(R.id.iv_plan_cover)
        private ImageView ivCover;
        @Bind(R.id.tv_plan_name)
        private TextView tvPlanName;
        @Bind(R.id.tv_plan_for_shop)
        private TextView tvForShop;
        @Bind(R.id.tv_plan_date)
        private TextView tvDate;
        @Bind(R.id.tv_plan_create_time)
        private TextView tvCreateTime;
        @Bind(R.id.iv_plan_more)
        private ImageView ivMore;
        @Bind(R.id.v_plan_divider)
        private View vDivider;

        public ViewHolder(View view) {
            ViewBinder.bind(this, view);
        }
    }
}
