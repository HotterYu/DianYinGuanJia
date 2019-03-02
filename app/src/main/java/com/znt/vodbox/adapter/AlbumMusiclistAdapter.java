package com.znt.vodbox.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.vodbox.bean.MediaInfo;
import com.znt.vodbox.utils.FileUtils;
import com.znt.vodbox.utils.binding.Bind;
import com.znt.vodbox.utils.binding.ViewBinder;

import java.util.ArrayList;
import java.util.List;


public class AlbumMusiclistAdapter extends BaseAdapter {
    private List<MediaInfo> dataList;
    private OnMoreClickListener listener;
    private boolean isPlaylist=true;
    private boolean isSelect = false;

    public AlbumMusiclistAdapter(List<MediaInfo> dataList) {
        this.dataList = dataList;
    }

    public void notifyDataSetChanged(List<MediaInfo> dataList)
    {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public void setSelect(boolean isSelect)
    {
        this.isSelect = isSelect;
        //notifyDataSetChanged();
    }

    public void remove(int index)
    {
        dataList.remove(index);
        notifyDataSetChanged();
    }
    public void remove(MediaInfo ietm)
    {
        dataList.remove(ietm);
        notifyDataSetChanged();
    }
    public void insert(MediaInfo ietm, int index)
    {
        dataList.add(index, ietm);
        notifyDataSetChanged();
    }

    private boolean isDrag = false;
    public void setDragView(boolean isDrag)
    {
        this.isDrag = isDrag;
        notifyDataSetChanged();
    }

    public List<MediaInfo> getSelectedMedias()
    {
        List<MediaInfo> tempList = new ArrayList<>();

        for(int i=0;i<dataList.size();i++)
        {
            MediaInfo tempInfo = dataList.get(i);
            if(tempInfo.isSelected())
                tempList.add(tempInfo);
        }

        return tempList;
    }
    public String getSelectedMediaIds()
    {
        String ids = "";

        for(int i=0;i<dataList.size();i++)
        {
            MediaInfo tempInfo = dataList.get(i);
            if(tempInfo.isSelected())
                ids += tempInfo.getId() + ",";
        }
        if(ids.endsWith(","))
            ids = ids.substring(0,ids.length()-1);
        return ids;
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

        MediaInfo tempInfo = dataList.get(position);

        return tempInfo;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_music, parent, false);
            holder = new ViewHolder(convertView);

            holder.ivItemSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = (int) v.getTag();
                    MediaInfo tempInfo = dataList.get(index);
                    tempInfo.setSelected(!tempInfo.isSelected());
                    dataList.set(index, tempInfo);
                    notifyDataSetChanged();
                }
            });

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.ivItemSelect.setTag(position);

        MediaInfo tempInfo = dataList.get(position);

        if(FileUtils.isPicture(tempInfo.getMusicUrl()))
        {
            holder.ivCover.setImageResource(R.drawable.icon_image);
        }
        else if(FileUtils.isVideo(tempInfo.getMusicUrl()))
        {
            holder.ivCover.setImageResource(R.drawable.icon_video);
        }
        else if(FileUtils.isMusic(tempInfo.getMusicUrl()))
        {
            holder.ivCover.setImageResource(R.drawable.icon_music);
        }

        holder.tvAlbumName.setText(tempInfo.getMusicName());
        String artist = FileUtils.getArtistAndAlbum(tempInfo.getMusicSing(), tempInfo.getMusicAlbum());
        holder.tvDesc.setText(artist);

        holder.tvNum.setText((position + 1) + "");

        if(isSelect)
        {
            holder.ivItemSelect.setVisibility(View.VISIBLE);
            holder.ivMore.setVisibility(View.GONE);

            if(tempInfo.isSelected())
                holder.ivItemSelect.setImageResource(R.drawable.icon_selected_on);
            else
                holder.ivItemSelect.setImageResource(R.drawable.icon_selected_off);
        }
        else
        {
            holder.ivMore.setVisibility(View.VISIBLE);
            holder.ivItemSelect.setVisibility(View.GONE);
        }

        if(isDrag)
        {
            holder.ivCover.setVisibility(View.GONE);
            holder.ivSortIcon.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.ivCover.setVisibility(View.VISIBLE);
            holder.ivSortIcon.setVisibility(View.GONE);
        }

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
        @Bind(R.id.iv_cover)
        private ImageView ivCover;

        @Bind(R.id.tv_music_item_num)
        private TextView tvNum;

        @Bind(R.id.tv_title)
        private TextView tvAlbumName;

        @Bind(R.id.tv_artist)
        private TextView tvDesc;

        @Bind(R.id.iv_more)
        private ImageView ivMore;

        @Bind(R.id.v_divider)
        private View vDivider;

        @Bind(R.id.iv_album_music_select)
        private ImageView ivItemSelect;

        @Bind(R.id.drag_handle)
        private ImageView ivSortIcon;


        public ViewHolder(View view) {
            ViewBinder.bind(this, view);
        }
    }
}
