/*  
* @Project: ZNTVodBox 
* @User: Administrator 
* @Description: 家庭音乐
* @Author： yan.yu
* @Company：http://www.zhunit.com/
* @Date 2016-9-29 上午12:21:20
* @Version V1.1
*/

package com.znt.vodbox.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.vodbox.bean.UserCallBackBean;
import com.znt.vodbox.db.DBManager;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.http.HttpCallback;
import com.znt.vodbox.http.HttpClient;
import com.znt.vodbox.model.UserInfo;
import com.znt.vodbox.utils.ActivityManager;
import com.znt.vodbox.utils.ViewUtils;
import com.znt.vodbox.utils.binding.Bind;
import com.znt.vodbox.view.xlistview.LJListView;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: UserRecordActivity
 * @Description: TODO
 * @author yan.yu
 * @date 2016-9-29 上午12:21:20
 */
public class UserRecordActivity extends BaseActivity implements LJListView.IXListViewListener, OnItemClickListener
{

	@Bind(R.id.tv_common_title)
	private TextView tvTopTitle = null;
	@Bind(R.id.iv_common_back)
	private ImageView ivTopReturn = null;
	@Bind(R.id.iv_common_more)
	private ImageView ivTopMore = null;

	@Bind(R.id.ptrl_user_list)
	private LJListView mListView;

	private UserInfo userInfor = null;
	private List<UserInfo> userList = new ArrayList<UserInfo>();

	private UserAdapter userAdapter = null;


	/**
	 *callbacks
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);


		setContentView(R.layout.activity_user_record);

		tvTopTitle.setText("登录记录");
		ivTopMore.setVisibility(View.GONE);

		ivTopReturn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		mListView.getListView().setDivider(getResources().getDrawable(R.color.transparent));
		mListView.getListView().setDividerHeight(1);
		mListView.setPullLoadEnable(true,"共5条数据"); //如果不想让脚标显示数据可以mListView.setPullLoadEnable(false,null)或者mListView.setPullLoadEnable(false,"")
		mListView.setPullRefreshEnable(true);
		mListView.setIsAnimation(true);
		mListView.setXListViewListener(this);
		mListView.showFootView(false);
		mListView.setRefreshTime();
		mListView.setOnItemClickListener(this);

		userAdapter = new UserAdapter();
		mListView.setAdapter(userAdapter);

		getUserList();
	}

	private void onLoad(int updateCount)
	{
		mListView.setCount(updateCount);
		mListView.stopLoadMore();
		mListView.stopRefresh();
		mListView.setRefreshTime();
	}


	private void getUserList()
	{
		userList.clear();
		userList.addAll(DBManager.newInstance(getApplicationContext()).getUserList());
	}

	private void login()
	{
		HttpClient.userLogin(userInfor.getUsername(), userInfor.getPwd(), new HttpCallback<UserCallBackBean>() {
			@Override
			public void onSuccess(UserCallBackBean tempInfor) {
				if(tempInfor.isSuccess())
				{
					UserInfo tempInforData = tempInfor.getData();
					if(TextUtils.isEmpty(tempInforData.getPwd()))
						tempInforData.setPwd(userInfor.getPwd());
					Constant.mUserInfo = tempInforData;
					if(tempInforData != null)
					{
						getLocalData().setUserInfor(tempInforData);
						setResult(0);
						finish();
					}
				}
				else
				{

				}
			}

			@Override
			public void onFail(Exception e) {

			}
		});
	}

	private boolean isCurCanLogin(UserInfo infor)
	{
		String userId = getLocalData().getUserId();
		if(userId.equals(infor.getId()))
			return false;
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		// TODO Auto-generated method stub
		if(arg2 > 0)
			arg2 = arg2 - 1;
		userInfor = userList.get(arg2);
		if(isCurCanLogin(userInfor))
		{
			getLocalData().setUserInfor(userInfor);

			ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
			// 将文本内容放到系统剪贴板里。
			cm.setText(userInfor.getUsername());

			Bundle b = new Bundle();
			b.putSerializable("USER_INFO",userInfor);
			ViewUtils.startActivity(getActivity(),LoginAct.class,b);
			ActivityManager.getInstance().finishOthersActivity(UserRecordActivity.class);
		}
		else
			showToast("该账户已登录");
	}

	@Override
	public void onRefresh()
	{
		// TODO Auto-generated method stub
		getUserList();
		onLoad(0);
	}

	@Override
	public void onLoadMore()
	{
		// TODO Auto-generated method stub

	}

	class UserAdapter extends BaseAdapter
	{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return userList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return userList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder vh = null;
			if(convertView == null)
			{
				vh = new ViewHolder();
				convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.view_user_list_item, null);

				vh.tvName = (TextView)convertView.findViewById(R.id.tv_user_item_name);
				vh.tvAccount = (TextView)convertView.findViewById(R.id.tv_user_item_accout);
				vh.ivDelete = (ImageView)convertView.findViewById(R.id.iv_user_item_delete);

				vh.ivDelete.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						int  index = (int) v.getTag();
						UserInfo tempInfor = userList.get(index);
						DBManager.newInstance(getApplicationContext()).deleteUser(tempInfor.getId());
						userList.remove(index);
						userAdapter.notifyDataSetChanged();
					}
				});

				convertView.setTag(vh);
			}
			else
				vh = (ViewHolder) convertView.getTag();

			vh.ivDelete.setTag(position);

			UserInfo infor = userList.get(position);
			if(!TextUtils.isEmpty(infor.getMerchant().getName()))
				vh.tvName.setText(infor.getMerchant().getName());
			if(!TextUtils.isEmpty(infor.getUsername()))
				vh.tvAccount.setText(infor.getUsername());

			return convertView;
		}

		private class ViewHolder
		{
			public TextView tvName = null;
			public TextView tvAccount = null;
			public ImageView ivDelete = null;
		}

	}

}
 
