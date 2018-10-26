package com.znt.vodbox.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.vodbox.bean.AlbumInfo;
import com.znt.vodbox.bean.MediaInfo;
import com.znt.vodbox.utils.FileUtils;
import com.znt.vodbox.utils.binding.Bind;
import com.znt.vodbox.utils.binding.ViewBinder;

import java.util.List;


public class AlbumMusiclistAdapter extends BaseAdapter {
    private List<MediaInfo> dataList;
    private OnMoreClickListener listener;
    private boolean isPlaylist=true;

    public AlbumMusiclistAdapter(List<MediaInfo> dataList) {
        this.dataList = dataList;
    }

    public void notifyDataSetChanged(List<MediaInfo> dataList)
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_my_albums, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        MediaInfo tempInfo = dataList.get(position);
        holder.ivCover.setImageResource(R.drawable.default_cover);
        holder.tvAlbumName.setText(tempInfo.getMusicName());
        String artist = FileUtils.getArtistAndAlbum(tempInfo.getMusicSing(), tempInfo.getMusicAlbum());
        holder.tvDesc.setText(artist);
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


        public ViewHolder(View view) {
            ViewBinder.bind(this, view);
        }
    }
}
