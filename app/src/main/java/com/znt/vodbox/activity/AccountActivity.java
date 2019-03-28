package com.znt.vodbox.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.znt.vodbox.R;
import com.znt.vodbox.application.MusicApplication;
import com.znt.vodbox.bean.CommonCallBackBean;
import com.znt.vodbox.entity.LocalDataEntity;
import com.znt.vodbox.http.HttpCallback;
import com.znt.vodbox.http.HttpClient;
import com.znt.vodbox.model.UserInfo;
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
				ViewUtils.startActivity(getActivity(),UserRecordActivity.class,null,1);
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
			itvName.getSecondView().setText(infor.getMerchant().getName());
			itvPwd.getSecondView().setText("******");
			
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
		ViewUtils.startActivity(getActivity(), LoginAct.class, bunlde);
		finish();
	}
	

	/**
	*callbacks
	*/
	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		if(v == tvLogout)//
		{
			//logout();
			logOutProcess();

		}
		else if(v == itvName.getBgView())
		{
			showNameEditDialog();
		}
		else if(v == itvPwd.getBgView())
		{
			showPwdEditDialog();
		}
		else if(v == accountLogin)
		{
			
		}
	}
	
	private void showNameEditDialog()
	{

		ViewGroup extView = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.alertext_form,null);

		final String oldName = LocalDataEntity.newInstance(getApplicationContext()).getUserName();

		final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

		final EditText etName = (EditText) extView.findViewById(R.id.et_alert_input);

		final AlertView mAlertViewExt = new AlertView("修改昵称", "请输入新的昵称！", "取消", null, new String[]{"完成"}, AccountActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
			@Override
			public void onItemClick(Object o, int position) {

				if(position == 0)
				{
					String newName = etName.getText().toString();
					if(TextUtils.isEmpty(newName))
					{
						Toast.makeText(getApplicationContext(),"请输入昵称",Toast.LENGTH_SHORT).show();
						return;
					}
					if(newName.equals(oldName))
					{
						Toast.makeText(getApplicationContext(),"信息未更改",Toast.LENGTH_SHORT).show();
						return;
					}
					updateUserInfo(newName);
				}
			}
		});
		etName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean focus) {
				//输入框出来则往上移动
				boolean isOpen= imm.isActive();
				mAlertViewExt.setMarginBottom(isOpen&&focus ? 120 :0);
			}
		});
		etName.setHint("请输入昵称");
		etName.setText(oldName);
		mAlertViewExt.addExtView(extView);
		mAlertViewExt.show();
	}

	private void updateUserInfo(final String name)
	{
		String token = LocalDataEntity.newInstance(getActivity()).getUserInfor().getToken();
		String merchId = LocalDataEntity.newInstance(getActivity()).getUserInfor().getMerchant().getId();
		String adminId = LocalDataEntity.newInstance(getActivity()).getUserInfor().getId();
		HttpClient.updateUserInfo(token, merchId, name, new HttpCallback<CommonCallBackBean>() {
			@Override
			public void onSuccess(CommonCallBackBean commonCallBackBean) {
				if(commonCallBackBean.isSuccess())
				{
					LocalDataEntity.newInstance(getActivity()).setUserName(name);
					itvName.getSecondView().setText(name);
					showToast("名称修改成功");
				}
				else
					showToast("修改失败："+commonCallBackBean.getMessage());
			}

			@Override
			public void onFail(Exception e) {
				showToast("修改失败");
			}
		});

	}


	private void showPwdEditDialog()
	{

		ViewGroup extView = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.alertext_form_pwd,null);

		final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

		final EditText etOldPwd = (EditText) extView.findViewById(R.id.et_alert_input1);
		final EditText etNew = (EditText) extView.findViewById(R.id.et_alert_input2);

		final AlertView mAlertViewExt = new AlertView("修改密码", "请输入要修改的密码！", "取消", null, new String[]{"完成"}, AccountActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
			@Override
			public void onItemClick(Object o, int position) {

				if(position == 0)
				{
					String oldPwd = etOldPwd.getText().toString();
					String newPwd = etNew.getText().toString();
					if(TextUtils.isEmpty(oldPwd))
					{
						Toast.makeText(getApplicationContext(),"请输入旧密码",Toast.LENGTH_SHORT).show();
						return;
					}
					if(TextUtils.isEmpty(newPwd))
					{
						Toast.makeText(getApplicationContext(),"请输入新密码",Toast.LENGTH_SHORT).show();
						return;
					}
					if(newPwd.equals(etOldPwd))
					{
						Toast.makeText(getApplicationContext(),"密码无变化",Toast.LENGTH_SHORT).show();
						return;
					}
					updatePwd(oldPwd,newPwd);
				}
			}
		});
		etOldPwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean focus) {
				//输入框出来则往上移动
				boolean isOpen= imm.isActive();
				mAlertViewExt.setMarginBottom(isOpen&&focus ? 120 :0);
			}
		});
		etOldPwd.setHint("请输入旧密码");

		etNew.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean focus) {
				//输入框出来则往上移动
				boolean isOpen= imm.isActive();
				mAlertViewExt.setMarginBottom(isOpen&&focus ? 120 :0);
			}
		});
		etNew.setHint("请输入新密码");

		mAlertViewExt.addExtView(extView);
		mAlertViewExt.show();
	}

	private void updatePwd(final String oldPwd, final String newPwd)
	{
		String token = LocalDataEntity.newInstance(getActivity()).getUserInfor().getToken();
		/*String merchId = Constant.mUserInfo.getMerchant().getId();
		String adminId = Constant.mUserInfo.getId();*/
		HttpClient.updateUserPwd(token, oldPwd, newPwd, new HttpCallback<CommonCallBackBean>() {
			@Override
			public void onSuccess(CommonCallBackBean commonCallBackBean) {
				if(commonCallBackBean.isSuccess())
				{
					LocalDataEntity.newInstance(getActivity()).setUserPwd(newPwd);
					showToast("密码修改成功");
				}
				else
					showToast("修改失败："+commonCallBackBean.getMessage());
			}

			@Override
			public void onFail(Exception e) {
				showToast("修改失败");
			}
		});

	}

}
