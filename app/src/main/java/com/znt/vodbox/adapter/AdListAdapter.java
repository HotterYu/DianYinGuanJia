package com.znt.vodbox.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.vodbox.bean.AdMediaInfo;
import com.znt.vodbox.utils.DateUtils;
import com.znt.vodbox.utils.FileSizeUtil;
import com.znt.vodbox.utils.binding.Bind;
import com.znt.vodbox.utils.binding.ViewBinder;

import java.util.ArrayList;
import java.util.List;


public class AdListAdapter extends BaseAdapter {

    private Context mContext = null;

    private List<AdMediaInfo> dataList;
    private OnMoreClickListener listener;
    private boolean isSelect = false;

    public AdListAdapter(Context mContext,List<AdMediaInfo> dataList) {
        this.dataList = dataList;
    }

    public void notifyDataSetChanged(List<AdMediaInfo> dataList)
    {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public void setSelect(boolean isSelect)
    {
        this.isSelect = isSelect;
        //notifyDataSetChanged();
    }

    public List<AdMediaInfo> getSelectedList()
    {
        List<AdMediaInfo> tempList = new ArrayList<>();

        for(int i=0;i<dataList.size();i++)
        {
            AdMediaInfo tempInfo = dataList.get(i);
            if(tempInfo.isSelected())
                tempList.add(tempInfo);
        }
        return tempList;
    }

    public void updateSelected(List<AdMediaInfo> selectedList)
    {
        if(selectedList == null)
            return;
        
        int size = selectedList.size();
        for(int i=0;i<size;i++)
        {
            AdMediaInfo infor = selectedList.get(i);
            int len = dataList.size();
            for(int j=0;j<len;j++)
            {
                AdMediaInfo tempInfor = dataList.get(j);
                if(infor.getId().equals(tempInfor.getId()))
                {
                    tempInfor.setSelected(true);
                    dataList.set(j, tempInfor);
                }
            }
        }
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_ad_list, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
            holder.ivMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onMoreClick(position);
                    }
                }
            });
            holder.ivItemSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = (int) v.getTag();
                    AdMediaInfo tempInfo = dataList.get(index);
                    tempInfo.setSelected(!tempInfo.isSelected());
                    dataList.set(index, tempInfo);
                    notifyDataSetChanged();
                }
            });

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        AdMediaInfo tempInfo = dataList.get(position);
        //holder.ivCover.setImageResource(R.drawable.default_cover);
        holder.tvAlbumName.setText(tempInfo.getAdname());
        holder.ivItemSelect.setTag(position);

        String addTime = tempInfo.getAddTime();
        String addTimeFormat = "";
        if(!TextUtils.isEmpty(addTime))
            addTimeFormat = DateUtils.getDateFromLong(Long.parseLong(addTime));
        long fileSize = 0;
        if(!TextUtils.isEmpty(tempInfo.getFileSize()))
            fileSize = Long.parseLong(tempInfo.getFileSize());
        String artist = FileSizeUtil.FormetFileSize(fileSize) + "  " + addTimeFormat;
        if(fileSize > 0)
        {
            holder.tvDesc.setVisibility(View.VISIBLE);
            holder.tvDesc.setText(artist);
        }
        else
            holder.tvDesc.setVisibility(View.GONE);

        if(isSelect)
        {
            holder.ivItemSelect.setVisibility(View.VISIBLE);
            //holder.ivMore.setVisibility(View.GONE);

            if(tempInfo.isSelected())
                holder.ivItemSelect.setImageResource(R.drawable.icon_selected_on);
            else
                holder.ivItemSelect.setImageResource(R.drawable.icon_selected_off);
        }
        else
        {
            //holder.ivMore.setVisibility(View.VISIBLE);
            holder.ivItemSelect.setVisibility(View.GONE);
        }


        holder.vDivider.setVisibility(isShowDivider(position) ? View.VISIBLE : View.GONE);
        return convertView;
    }

    private boolean isShowDivider(int position) {
        return position != dataList.size() - 1;
    }

    private static class ViewHolder {
        @Bind(R.id.iv_my_album_cover)
        private ImageView ivCover;

        @Bind(R.id.tv_my_album_name)
        private TextView tvAlbumName;

        @Bind(R.id.tv_my_album_desc)
        private TextView tvDesc;

        @Bind(R.id.tv_my_album_count)
        private TextView tvCount;

        @Bind(R.id.iv_my_album_more)
        private ImageView ivMore;

        @Bind(R.id.v_my_album_divider)
        private View vDivider;

        @Bind(R.id.iv_my_album_select)
        private ImageView ivItemSelect;

        public ViewHolder(View view) {
            ViewBinder.bind(this, view);
        }
    }
}
