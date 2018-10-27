package com.znt.vodbox.view.searchview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.znt.vodbox.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prize on 2018/10/27.
 */

public class SearchRecordAdapter extends BaseAdapter
{
    private Context context = null;

    List<String> names = new ArrayList<>();

    public SearchRecordAdapter(Context context, List<String> names)
    {
        this.context = context;
        this.names = names;
    }

    public List<String> getKeywords()
    {
        return names;
    }

    @Override
    public int getCount() {
        return names.size();
    }

    @Override
    public Object getItem(int i) {
        return names.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        ViewHolder vh = null;

        if(convertView == null)
        {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.view_holder_search_record,null);
            vh.tvName = convertView.findViewById(R.id.tv_record_name);
            vh.ivDelete =convertView.findViewById(R.id.iv_record_delete);

            vh.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            convertView.setTag(vh);
        }
        else
            vh = (ViewHolder)convertView.getTag();

        vh.tvName.setText(names.get(i));

        return convertView;
    }

    private class ViewHolder
    {
        TextView tvName = null;
        ImageView ivDelete = null;
    }
}
