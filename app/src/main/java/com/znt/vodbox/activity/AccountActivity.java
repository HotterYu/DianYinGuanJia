package com.znt.vodbox.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.qihoo360.replugin.RePlugin;
import com.znt.vodbox.R;
import com.znt.vodbox.application.MusicApplication;
import com.znt.vodbox.model.UserInfo;
import com.znt.vodbox.utils.ActivityManager;
import com.znt.vodbox.utils.ViewUtils;
import com.znt.vodbox.utils.binding.Bind;
import com.znt.vodbox.view.CircleImageView;
import com.znt.vodbox.view.ItemTextView;

public class AccountActivity extends BaseActivity implements OnClickListener
{

	@Bind(R.id.tv_common_title)
	private TextView tvTopTitle = null;
	@Bind(R.id.iv_common_back)
	private ImageView ivTopReturn = null;
	@Bind(R.id.iv_common_more)
	private ImageView ivTopMore = null;
	@Bind(R.id.tv_common_confirm)
	private TextView tvConfirm = null;

	private CircleImageView ivHead = null;
	
	private View accountLogin = null;
	private ItemTextView itvName = null;
	private ItemTextView itvUser = null;
	private ItemTextView itvPwd = null;
	private TextView tvLogout = null;
	private View thirdLoginView = null;
	private ImageView ivQQLogin = null;
	private ImageView ivSinaLogin = null;
	private ImageView ivWeiXinLogin = null;
	private Button btnOldVersion = null;

	private UserInfo userInfor = null;
	private boolean isInit = true;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_account);

		tvTopTitle.setText("个人中心");
		ivTopMore.setVisibility(View.GONE);
		tvConfirm.setVisibility(View.VISIBLE);
		tvConfirm.setText("切换");

		ivTopReturn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		tvConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showChangeAccountDialog();
				//ViewUtils.startActivity(getActivity(),LoginRecordActivity.class,null,1);

			}
		});

		userInfor = getLocalData().getUserInfor();
		
		isInit = getIntent().getBooleanExtra("INIT", true);
		getViews();
		initViews();
	}
	private void showUserInfor()
	{
		if(MusicApplication.isLogin)
		{
			UserInfo infor = getLocalData().getUserInfor();
			itvUser.getSecondView().setText(infor.getUsername());
			itvName.getSecondView().setText(infor.getNickName());
			//itvPwd.getSecondView().setText(infor.getMerchant().get);
			
			if(getLocalData().getLoginType().equals("0"))
			{
				itvUser.setVisibility(View.VISIBLE);
				itvPwd.setVisibility(View.VISIBLE);
			}
			else if(getLocalData().getLoginType().equals("1"))
			{
				itvUser.setVisibility(View.INVISIBLE);
				itvPwd.setVisibility(View.INVISIBLE);
			}
		}
	}

	private void showChangeAccountDialog()
	{
		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
		dialog.setTitle("切换账户");

		dialog.setItems(R.array.change_account, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog1, int which) {
				switch (which) {
					case 0://
						ViewUtils.startActivity(getActivity(),UserListActivity.class,null,1);
						break;
					case 1://
						ViewUtils.startActivity(getActivity(),UserRecordActivity.class,null,1);
						break;
				}
			}
		});
		dialog.show();
	}
	
	/**
	*callbacks
	*/
	@Override
	protected void onResume()
	{
		showUserInfor();
		super.onResume();
	}
	
	/**
	*callbacks
	*/
	@Override
	protected void onDestroy()
	{

		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	private void getViews()
	{
	
		ivHead = (CircleImageView)findViewById(R.id.civ_account_head);

		accountLogin = findViewById(R.id.view_account_login);
		itvName = (ItemTextView)findViewById(R.id.itv_account_login_name);
		itvUser = (ItemTextView)findViewById(R.id.itv_account_login_user);
		itvPwd = (ItemTextView)findViewById(R.id.itv_account_login_pwd);
		tvLogout = (TextView)findViewById(R.id.tv_account_loginout);

		thirdLoginView = findViewById(R.id.view_account_third_login);
		ivQQLogin = (ImageView)findViewById(R.id.iv_login_qq);
		ivWeiXinLogin = (ImageView)findViewById(R.id.iv_login_weixin);
		ivSinaLogin = (ImageView)findViewById(R.id.iv_login_sina);

		btnOldVersion = (Button) findViewById(R.id.btn_login_old);

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		
	}
	private void initViews()
	{
		if(MusicApplication.isLogin)
			showLoginView();

		tvLogout.setOnClickListener(this);

		itvName.showMoreButton(true);
		itvPwd.showMoreButton(true);
		itvUser.getMoreView().setVisibility(View.INVISIBLE);
		
		itvName.getBgView().setOnClickListener(this);
		itvPwd.getBgView().setOnClickListener(this);
		
		itvName.hideIocn();
		itvPwd.hideIocn();
		itvPwd.showBottomLine(false);
		itvUser.showBottomLine(false);
		itvName.showBottomLine(false);

		ivQQLogin.setOnClickListener(this);
		ivWeiXinLogin.setOnClickListener(this);
		ivSinaLogin.setOnClickListener(this);


		btnOldVersion.setOnClickListener(this);

	}
	public int getAppVersionName(Context context) {
	    int versioncode = 0;  
	    try
		{
	        // ---get the package info---  
	        PackageManager pm = context.getPackageManager();  
	        PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);  
	        versioncode = pi.versionCode;
	    } catch (Exception e) {  
	        Log.e("VersionInfo", "Exception", e);  
	    }  
	    return versioncode;  
	}  
	
	private void showLoginView()
	{
		accountLogin.setVisibility(View.VISIBLE);
		accountLogin.setOnClickListener(this);
		
		itvPwd.getSecondView().setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
		
		itvName.getSecondView().setTextColor(getResources().getColor(R.color.text_black_on));
		itvUser.getSecondView().setTextColor(getResources().getColor(R.color.text_black_on));
		itvPwd.getSecondView().setTextColor(getResources().getColor(R.color.text_black_on));
	}
	
	/**
	*callbacks
	*/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		
		if (resultCode != RESULT_OK) 
		{
			return;
		} 
		
		if(requestCode == 5)
		{
			String phone = data.getStringExtra("phone");
			String pwd = data.getStringExtra("pwd");

		}
		else if(requestCode == 4)
		{
			String tempName = data.getStringExtra("NICK_NAME");
			userInfor.setUsername(tempName);
			getLocalData().setUserName(tempName);
			itvName.getSecondView().setText(tempName);
			//getLocalData().setUserName(tempName);
		}
		else if(requestCode == 1)
		{

		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void logOutProcess()
	{
		getLocalData().clearUserInfor();
		//showUnLoginView();
		MusicApplication.isLogin = false;
		//ivHead.setImageResource(R.drawable.logo);
		
		Bundle bunlde = new Bundle();
		bunlde.putString("LoginType", "1");
		ViewUtils.startActivity(getActivity(), LoginActivity.class, bunlde);
		finish();
	}
	

	/**
	*callbacks
	*/
	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		if(v == tvLogout)//ע��
		{
			//logout();
			logOutProcess();
			
			//PushManager.getInstance().turnOffPush(getActivity());
		}
		else if(v == itvName.getBgView())//�༭�ǳ�
		{

		}
		else if(v == itvPwd.getBgView())//�༭����
		{

		}
		else if(v == accountLogin)
		{
			
		}
		else if(v == btnOldVersion)
		{
			String pluginName = "DianYinGuanJiaOld";
			RePlugin.startActivity(AccountActivity.this, RePlugin.createIntent(pluginName,
					"com.znt.vodbox.activity.LoginActivity"));
			ActivityManager.getInstance().finishAllActivity();
		}
	}
	

}
