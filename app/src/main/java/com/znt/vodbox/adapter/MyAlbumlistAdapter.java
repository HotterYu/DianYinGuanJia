package com.znt.vodbox.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.znt.vodbox.R;
import com.znt.vodbox.bean.AlbumInfo;
import com.znt.vodbox.utils.binding.Bind;
import com.znt.vodbox.utils.binding.ViewBinder;

import java.util.List;


public class MyAlbumlistAdapter extends BaseAdapter {
    private List<AlbumInfo> albumInfos;
    private OnMoreClickListener listener;
    private boolean isPlaylist=true;


    public MyAlbumlistAdapter(List<AlbumInfo> albumInfos) {
        this.albumInfos = albumInfos;
    }

    public void notifyDataSetChanged(List<AlbumInfo> albumInfos)
    {
        this.albumInfos = albumInfos;
        notifyDataSetChanged();
    }


    public void setOnMoreClickListener(OnMoreClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return albumInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return albumInfos.get(position);
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
        AlbumInfo albumInfo = albumInfos.get(position);
        holder.ivCover.setImageResource(R.drawable.default_cover);
        holder.tvAlbumName.setText(albumInfo.getName());
        holder.tvDesc.setText(albumInfo.getDescription());
        holder.tvCount.setText(albumInfo.getMusicNum());
        holder.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onMoreClick(position);
                }
            }
        });
        holder.vDivider.setVisibility(isShowDivider(position) ? View.VISIBLE : View.GONE);

        Glide.with(parent.getContext())
                .load(albumInfo.getImageUrl())
                .placeholder(R.drawable.default_cover)
                .error(R.drawable.default_cover)
                .into(holder.ivCover);

        return convertView;
    }

    private boolean isShowDivider(int position) {
        return position != albumInfos.size() - 1;
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
