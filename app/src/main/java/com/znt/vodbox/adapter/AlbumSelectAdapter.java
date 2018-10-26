/*  
* @Project: ZNTVodBox 
* @User: Administrator 
* @Description: 瀹跺涵闊充箰
* @Author锛� yan.yu
* @Company锛歨ttp://www.zhunit.com/
* @Date 2016-6-23 涓嬪崍10:47:40 
* @Version V1.1   
*/ 

package com.znt.vodbox.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.vodbox.bean.AlbumInfo;

import java.util.ArrayList;
import java.util.List;

/** 
 * @ClassName: AlbumSelectDialog 
 * @Description: TODO
 * @author yan.yu 
 */
public class AlbumSelectAdapter extends BaseAdapter
{

	private Activity baseActivity = null;
	private List<AlbumInfo> list = new ArrayList<AlbumInfo>();
	private List<AlbumInfo> selectedList = new ArrayList<AlbumInfo>();

	public AlbumSelectAdapter(Activity activity, List<AlbumInfo> list)
	{
		this.baseActivity = activity;
		this.list = list;
	}

	public void notifyDataSetChanged(List<AlbumInfo> list)
	{
		this.list.addAll(list);
		notifyDataSetChanged();

	}
	
	public void setSelectedList(List<AlbumInfo> selectedList)
	{
		this.selectedList = selectedList;

		int size = selectedList.size();
		for(int i=0;i<size;i++)
		{
			AlbumInfo infor = selectedList.get(i);
			int len = list.size();
			for(int j=0;j<len;j++)
			{
				AlbumInfo tempInfor = list.get(j);
				if(infor.getId().equals(tempInfor.getId()))
				{
					tempInfor.setSelected(true);
					list.set(j, tempInfor);
				}
			}
		}
		notifyDataSetChanged();
	}
	public List<AlbumInfo> getSelectedList()
	{
		return selectedList;
	}
	
	/**
	*callbacks
	*/
	@Override
	public int getCount() 
	{
		// TODO Auto-generated method stub
		return list.size();
	}

	/**
	*callbacks
	*/
	@Override
	public Object getItem(int arg0) 
	{
		// TODO Auto-generated method stub
		return list.get(arg0);
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

	private ViewHolder vh = null;
	/**
	*callbacks
	*/
	@Override
	public View getView(int pos, View convertView, ViewGroup arg2) 
	{
		// TODO Auto-generated method stub
		if(convertView == null)
		{
			vh = new ViewHolder();
			convertView = LayoutInflater.from(baseActivity).inflate(R.layout.view_album_select_item, null);
			
			vh.tvName = (TextView)convertView.findViewById(R.id.tv_album_sellect_name);
			vh.ivCover = (ImageView)convertView.findViewById(R.id.iv_album_select_cover);
			vh.viewOperation = convertView.findViewById(R.id.view_album_select_item_operation);
			vh.viewBg = convertView.findViewById(R.id.view_album_select_item_bg);
			vh.ivEdit = (ImageView)convertView.findViewById(R.id.iv_album_select_item_operation);

            vh.viewOperation.setVisibility(View.VISIBLE);
			
			vh.viewOperation.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					// TODO Auto-generated method stub
					int index = (Integer) v.getTag();
					AlbumInfo infor = list.get(index);
					infor.setSelected(!infor.isSelected());
					if(infor.isSelected())
					{
						selectedList.add(infor);
					}
					else
					{
						for(int i=0;i<selectedList.size();i++)
						{
							AlbumInfo tempInfor = selectedList.get(i);
							if(infor.getId().equals(tempInfor.getId()))
							{
								selectedList.remove(i);
							}
						}
						if(selectedList.size() > 0)
							selectedList.remove(infor);
					}
					list.set(index, infor);
					notifyDataSetChanged();
				}
			});
			
			convertView.setTag(vh);
		}
		else
			vh = (ViewHolder)convertView.getTag();
		
		vh.viewOperation.setTag(pos);

		AlbumInfo infor = list.get(pos);

        if(infor.isSelected())
            vh.ivEdit.setImageResource(R.drawable.icon_selected_on);
        else
            vh.ivEdit.setImageResource(R.drawable.icon_selected_off);
		
		vh.tvName.setText(infor.getName() + "(" + infor.getMusicNum() + ")");
		/*if(!TextUtils.isEmpty(infor.getCover()))
			Picasso.with(baseActivity).load(infor.getCover()).into(vh.ivCover);
		else
			Picasso.with(baseActivity).load("http://img5.imgtn.bdimg.com/it/u=698076066,2006876975&fm=21&gp=0.jpg").into(vh.ivCover);*/
		
		// TODO Auto-generated method stub
		return convertView;
	}
	

	private class ViewHolder
	{
		ImageView ivCover = null;
		TextView tvName = null;
		View viewOperation = null;
		View viewBg = null;
		ImageView ivEdit = null;
	}

}
 
