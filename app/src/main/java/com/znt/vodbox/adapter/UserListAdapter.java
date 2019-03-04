package com.znt.vodbox.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.vodbox.model.UserInfo;
import com.znt.vodbox.utils.binding.Bind;
import com.znt.vodbox.utils.binding.ViewBinder;

import java.util.List;


public class UserListAdapter extends BaseAdapter {
    private List<UserInfo> dataList;
    private OnMoreClickListener listener;
    private boolean isPlaylist=true;

    public UserListAdapter(List<UserInfo> dataList) {
        this.dataList = dataList;
    }

    public void notifyDataSetChanged(List<UserInfo> dataList)
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_my_accounts, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        UserInfo tempInfo = dataList.get(position);
        holder.ivMore.setVisibility(View.GONE);
        holder.tvNickName.setText("名称：" + tempInfo.getNickName());
        holder.tvLoginName.setText("账户：" + tempInfo.getUsername());
        holder.tvType.setText("类型：" + tempInfo.getTypeName());
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
        @Bind(R.id.iv_my_account_cover)
        private ImageView ivCover;

        @Bind(R.id.tv_my_account_nick_name)
        private TextView tvNickName;

        @Bind(R.id.tv_my_account_login_name)
        private TextView tvLoginName;

        @Bind(R.id.tv_my_account_type)
        private TextView tvType;

        @Bind(R.id.iv_my_account_more)
        private ImageView ivMore;

        @Bind(R.id.v_my_account_divider)
        private View vDivider;


        public ViewHolder(View view) {
            ViewBinder.bind(this, view);
        }
    }
}
