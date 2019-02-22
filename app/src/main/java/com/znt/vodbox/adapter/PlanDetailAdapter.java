package com.znt.vodbox.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.vodbox.bean.SubPlanInfor;
import com.znt.vodbox.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class PlanDetailAdapter  extends BaseAdapter
{
    private Activity activity = null;
    private List<SubPlanInfor> subPlanList = new ArrayList<SubPlanInfor>();
    private int selectIndex = 0;
    private int planType = 0;

    public OnScheDeleteListener mOnScheDeleteListener = null;

    public interface OnScheDeleteListener
    {
        public void OnScheDelete(int index);
    }

    public PlanDetailAdapter(Activity activity, OnScheDeleteListener mOnScheDeleteListener)
    {
        this.activity = activity;

        this.planType = planType;
        this.mOnScheDeleteListener = mOnScheDeleteListener;
    }

    public void notifyDataSetChanged(List<SubPlanInfor> subPlanList)
    {
        this.subPlanList = subPlanList;
        notifyDataSetChanged();
    }

    /**
     *callbacks
     */
    @Override
    public int getCount()
    {
        // TODO Auto-generated method stub
        return subPlanList.size();
    }

    /**
     *callbacks
     */
    @Override
    public Object getItem(int arg0)
    {
        // TODO Auto-generated method stub
        return subPlanList.get(arg0);
    }

    /**
     *callbacks
     */
    @Override
    public long getItemId(int arg0)
    {
        // TODO Auto-generated method stub
        return arg0;
    }

    /**
     *callbacks
     */
    @Override
    public View getView(int pos, View convertView, ViewGroup arg2)
    {
        // TODO Auto-generated method stub

        ViewHolder vh = null;
        if(convertView == null)
        {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(activity).inflate(R.layout.view_plan_detail_item, null);
            vh.tvTime = (TextView)convertView.findViewById(R.id.tv_plan_item_time);
            vh.tvAlbum = (TextView)convertView.findViewById(R.id.tv_plan_item_album);
            vh.tvWeek = (TextView)convertView.findViewById(R.id.tv_plan_item_week);
            vh.viewDelete = convertView.findViewById(R.id.view_plan_item_delete);

            vh.viewDelete.setVisibility(View.VISIBLE);
            vh.viewDelete.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    // TODO Auto-generated method stub
                    selectIndex = (Integer) v.getTag();
                    if(mOnScheDeleteListener != null)
                        mOnScheDeleteListener.OnScheDelete(selectIndex);

                }
            });
            convertView.setTag(vh);
        }
        else
            vh = (ViewHolder)convertView.getTag();

        vh.viewDelete.setTag(pos);

        SubPlanInfor infor = subPlanList.get(pos);
        vh.tvTime.setText(infor.getPlanTime());
        vh.tvAlbum.setText(infor.getPlanAlbumName().replace(";","\n"));
        vh.tvWeek.setText(DateUtils.getWeekByCycleType(activity,infor.getCycleType()));


        return convertView;
    }

    private class ViewHolder
    {
        TextView tvTime = null;
        TextView tvAlbum = null;
        TextView tvWeek = null;
        View viewDelete = null;
    }
}