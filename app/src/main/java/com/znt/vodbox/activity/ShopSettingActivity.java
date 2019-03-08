package com.znt.vodbox.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.vodbox.bean.CommonCallBackBean;
import com.znt.vodbox.dialog.TextInputBottomDialog;
import com.znt.vodbox.dialog.VideoDirectionDialog;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.http.HttpCallback;
import com.znt.vodbox.http.HttpClient;
import com.znt.vodbox.model.Shopinfo;
import com.znt.vodbox.utils.DateUtils;
import com.znt.vodbox.utils.ViewUtils;
import com.znt.vodbox.utils.binding.Bind;
import com.znt.vodbox.view.ItemTextView;

public class ShopSettingActivity extends BaseActivity implements OnClickListener
{

	@Bind(R.id.tv_common_title)
	private TextView tvTopTitle = null;
	@Bind(R.id.iv_common_back)
	private ImageView ivTopReturn = null;
	@Bind(R.id.iv_common_more)
	private ImageView ivTopMore = null;

	private ItemTextView itvName = null;
	private ItemTextView itvAddr = null;
	private ItemTextView itvOritation = null;
	private ItemTextView itvLastOnline = null;
	private ItemTextView itvEndTime = null;
	private ItemTextView itvVersion = null;
	private ItemTextView itvWifiName = null;
	private ItemTextView itvWifiPwd = null;
	private ItemTextView itvIp = null;
	private ItemTextView itvStorage = null;
	
	private Shopinfo deviceInfor = null;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.view_shop_setting);

		ivTopReturn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
		ivTopMore.setVisibility(View.GONE);
		tvTopTitle.setText(getResources().getString(R.string.shop_detail_setting));

		itvName = (ItemTextView)findViewById(R.id.itv_shop_setting_name);
		itvAddr = (ItemTextView)findViewById(R.id.itv_shop_setting_addr);
		itvOritation = (ItemTextView)findViewById(R.id.itv_shop_setting_oritation);
		itvLastOnline = (ItemTextView)findViewById(R.id.itv_shop_setting_last_online_time);
		itvEndTime = (ItemTextView)findViewById(R.id.itv_shop_setting_dead_time);
		itvVersion = (ItemTextView)findViewById(R.id.itv_shop_setting_version);
		itvWifiName = (ItemTextView)findViewById(R.id.itv_shop_setting_wifi_name);
		itvWifiPwd = (ItemTextView)findViewById(R.id.itv_shop_setting_wifi_pwd);
		itvIp = (ItemTextView)findViewById(R.id.itv_shop_setting_ip);
		itvStorage = (ItemTextView)findViewById(R.id.itv_shop_setting_storage);
		
		deviceInfor = (Shopinfo) getIntent().getSerializableExtra("SHOP_INFO");
		
		initViews();
		
	}
	
	private void initViews()
	{
		itvName.getFirstView().setText(getResources().getString(R.string.itv_shop_setting_name));
		itvAddr.getFirstView().setText(getResources().getString(R.string.itv_shop_setting_addr));
		itvOritation.getFirstView().setText(getResources().getString(R.string.itv_shop_setting_oritation));
		itvLastOnline.getFirstView().setText(getResources().getString(R.string.itv_shop_setting_last_online_time));
		itvEndTime.getFirstView().setText(getResources().getString(R.string.itv_shop_setting_dead_time));
		itvVersion.getFirstView().setText(getResources().getString(R.string.itv_shop_setting_version));
		itvWifiName.getFirstView().setText(getResources().getString(R.string.itv_shop_setting_wifi_name));
		itvWifiPwd.getFirstView().setText(getResources().getString(R.string.itv_shop_setting_wifi_pwd));
		itvIp.getFirstView().setText(getResources().getString(R.string.itv_shop_setting_ip));
		itvStorage.getFirstView().setText(getResources().getString(R.string.itv_shop_setting_storage));

		/*itvName.showBottomLine(true);
		itvAddr.showBottomLine(true);
		itvOritation.showBottomLine(true);

		itvLastOnline.showBottomLine(true);
		itvIp.showBottomLine(true);*/

		itvName.showMoreButton(true);
		itvAddr.showMoreButton(true);
		itvOritation.showMoreButton(true);
		itvWifiName.showMoreButton(true);
		
		
		if(deviceInfor.getTmlRunStatus().size() > 0 &&
				!TextUtils.isEmpty(deviceInfor.getTmlRunStatus().get(0).getExpiredTime()))
		{
			long endTime = Long.parseLong(deviceInfor.getTmlRunStatus().get(0).getExpiredTime());
			if(endTime > 0 && endTime < System.currentTimeMillis())
			{
				//itvEndTime.getSecondView().setText(DateUtils.getDateFromLong(Long.parseLong(deviceInfor.getEndTime())));
				itvEndTime.getSecondView().setText(getResources().getString(R.string.over_time_end));
				itvEndTime.getSecondView().setTextColor(getResources().getColor(R.color.red));
			}
			else
			{
				int leastYear = DateUtils.getYearFromLong(endTime) - DateUtils.getYearFromLong(System.currentTimeMillis());
				if(leastYear >= 10)
					itvEndTime.getSecondView().setText(getResources().getString(R.string.all_ever));
				else
					itvEndTime.getSecondView().setText(DateUtils.getDateFromLong(endTime));
				itvEndTime.getSecondView().setTextColor(getResources().getColor(R.color.text_black_mid));
			}
		}
		
		itvName.getSecondView().setText(deviceInfor.getName());
		itvAddr.getSecondView().setText(deviceInfor.getAddress());
		itvOritation.getSecondView().setText(getVideoWhirl(deviceInfor.getTmlRunStatus().get(0).getVideoWhirl()));
		itvLastOnline.getSecondView().setText(DateUtils.getDateFromLong(Long.parseLong(deviceInfor.getTmlRunStatus().get(0).getLastConnTime())));
		
		//itvVersion.getSecondView().setText(deviceInfor.getTmlRunStatus().get(0).get);
		itvWifiName.getSecondView().setText(deviceInfor.getWifiName() + "\n" + deviceInfor.getWifiPassword());
		itvWifiPwd.getSecondView().setText(deviceInfor.getWifiPassword());
		//itvIp.getSecondView().setText(deviceInfor.getTmlRunStatus().get(0).get);
		if(TextUtils.isEmpty(deviceInfor.getNetInfo()))
			itvStorage.getSecondView().setText("无");
		else
			itvStorage.getSecondView().setText(deviceInfor.getNetInfo());

		itvWifiName.setOnClick(this);
		itvName.setOnClick(this);
		itvAddr.setOnClick(this);
		itvOritation.setOnClick(this);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK)
		{
			if(requestCode == 2)
			{
				Shopinfo devInfor = (Shopinfo) data.getSerializableExtra("DeviceInfor");
				itvWifiName.getSecondView().setText(devInfor.getWifiName() + "\n" + devInfor.getWifiPassword());
			}
		}
	}

	private String getVideoWhirl(String videoWhirl)
	{
		if(TextUtils.isEmpty(videoWhirl))
			videoWhirl = "0";

		if(videoWhirl.equals("0"))
		{
			return getResources().getString(R.string.video_oritation_1);
		}
		else if(videoWhirl.equals("1"))
		{
			return getResources().getString(R.string.video_oritation_2);
		}
		else if(videoWhirl.equals("2"))
		{
			return getResources().getString(R.string.video_oritation_3);
		}
		else if(videoWhirl.equals("3"))
		{
			return getResources().getString(R.string.video_oritation_4);
		}
		else if(videoWhirl.equals("4"))
		{
			return getResources().getString(R.string.video_oritation_5);
		}
		else if(videoWhirl.equals("5"))
		{
			return getResources().getString(R.string.video_oritation_6);
		}
		else if(videoWhirl.equals("6"))
		{
			return getResources().getString(R.string.video_oritation_7);
		}
		else if(videoWhirl.equals("7"))
		{
			return getResources().getString(R.string.video_oritation_8);
		}
		else if(videoWhirl.equals("8"))
		{
			return getResources().getString(R.string.video_oritation_9);
		}
		else if(videoWhirl.equals("9"))
		{
			return getResources().getString(R.string.video_oritation_10);
		}
		else if(videoWhirl.equals("10"))
		{
			return getResources().getString(R.string.video_oritation_11);
		}
		else if(videoWhirl.equals("11"))
		{
			return getResources().getString(R.string.video_oritation_12);
		}
		else
			
		return getResources().getString(R.string.screen_oritiontion)+videoWhirl;
	}
	
	private void showVideoWhirlDialog(final Shopinfo devInfor)
	{
		final VideoDirectionDialog videoDirectionDialog = new VideoDirectionDialog(getActivity());
	
		//playDialog.updateProgress("00:02:18 / 00:05:12");
		if(videoDirectionDialog.isShowing())
			videoDirectionDialog.dismiss();
		videoDirectionDialog.showDialog(deviceInfor.getTmlRunStatus().get(0).getVideoWhirl(), deviceInfor.getId());
		videoDirectionDialog.setOnDismissListener(new OnDismissListener()
		{
			@Override
			public void onDismiss(DialogInterface arg0) 
			{
				// TODO Auto-generated method stub
				if(!TextUtils.isEmpty(videoDirectionDialog.getCurDerection()))
				{
					deviceInfor.getTmlRunStatus().get(0).setVideoWhirl(videoDirectionDialog.getCurDerection());
					itvOritation.getSecondView().setText(getVideoWhirl(deviceInfor.getTmlRunStatus().get(0).getVideoWhirl()));
				}
				//
			}
		});
		WindowManager windowManager = ((Activity) getActivity()).getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = videoDirectionDialog.getWindow().getAttributes();
		lp.width = (int)(display.getWidth()); //璁剧疆瀹藉害
		lp.height = (int)(display.getHeight()); //璁剧疆楂樺害
		videoDirectionDialog.getWindow().setAttributes(lp);
	}
	
	public void updateShopInfo(String name)
	{

		deviceInfor.setName(name);
		String token = Constant.mUserInfo.getToken();
		try
		{
			// Simulate network access.
			HttpClient.updateShopInfo(token, deviceInfor, new HttpCallback<CommonCallBackBean>() {
				@Override
				public void onSuccess(CommonCallBackBean resultBean) {

					if(resultBean.isSuccess())
					{

					}
					else
					{

					}
					showToast(resultBean.getMessage());
				}

				@Override
				public void onFail(Exception e) {
					showToast(e.getMessage());
				}
			});
		}
		catch (Exception e)
		{
			showToast(e.getMessage());
			Log.e("",e.getMessage());
		}

	}
	
	@Override
	public void onClick(View v) 
	{
		// TODO Auto-generated method stub
		if(v == itvName.getBgView())
		{
			TextInputBottomDialog mTextInputBottomDialog = new TextInputBottomDialog(getActivity());
			mTextInputBottomDialog.show("请输入店铺名称", deviceInfor.getName(), new TextInputBottomDialog.OnDismissResultListener() {
				@Override
				public void onConfirmDismiss(String content) {

					updateShopInfo(content);
					itvName.getSecondView().setText(content);

				}
			});
		}
		else if(v == itvAddr.getBgView())
		{
			//ViewUtils.startActivity(getActivity(), LocationActivity.class, null, 1);
		}
		else if(v == itvOritation.getBgView())
		{
			showVideoWhirlDialog(deviceInfor);
		}
		else if(v == itvWifiName.getBgView())
		{
			Bundle bundle = new Bundle();
			bundle.putSerializable("DEVICE_INFO", deviceInfor);
			ViewUtils.startActivity(getActivity(), WifiSetActivity.class, bundle, 2);
		}
	}

}
