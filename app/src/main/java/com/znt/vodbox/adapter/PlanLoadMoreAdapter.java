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
import com.znt.vodbox.bean.PlanInfo;
import com.znt.vodbox.utils.DateUtils;
import com.znt.vodbox.utils.binding.Bind;
import com.znt.vodbox.utils.binding.ViewBinder;

import java.util.List;

/**
 * 上拉加载更多
 * Created by yangle on 2017/10/12.
 */

public class PlanLoadMoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext = null;

    private List<PlanInfo> dataList;
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

    public PlanLoadMoreAdapter(Context mContext, List<PlanInfo> dataList)
    {
        this.mContext = mContext;
        this.dataList = dataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {

        if(viewType == 0)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_plan_list, parent, false);
            return new RecyclerViewHolder(view);
        }
        else if(viewType == 1)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_plan_list_category, parent, false);
            return new TypeViewHolder(view);
        }

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
        else if(vh instanceof TypeViewHolder)
        {
            /*TypeViewHolder recyclerViewHolder = (TypeViewHolder) vh;
            recyclerViewHolder.tvType.setText("指定分区计划");
            recyclerViewHolder.tvType.setText("指定日期计划");*/
        }

    }

    /*public void notifyDataSetChanged()
    {
        dataList.clear();
        dataList.addAll();
        notifyItemChanged();
    }*/

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    private class RecyclerViewHolder extends RecyclerView.ViewHolder {

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


        public RecyclerViewHolder(View itemView) {
            super(itemView);
            ViewBinder.bind(this, itemView);
        }
    }
    private class TypeViewHolder extends RecyclerView.ViewHolder {

        /*@Bind(R.id.tv_plan_type)
        private TextView tvType;*/

        public TypeViewHolder(View itemView) {
            super(itemView);
            ViewBinder.bind(this, itemView);
        }
    }
}
