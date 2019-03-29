package com.znt.vodbox.entity;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.znt.vodbox.model.UserInfo;
import com.znt.vodbox.utils.SystemUtils;

public class LocalDataEntity 
{

	private Context context = null;
	private final String RENDER_DEVICE = "NEW_RENDER_DEVICE";
	private final String SERVER_DEVICE = "NEW_SERVER_DEVICE";
	private final String MUSIC_INDEX = "NEW_MUSIC_INDEX";
	private final String FIRST_INIT = "NEW_FIRST_INIT";
	private final String IS_SONG_HINT_SHOW = "NEW_IS_SONG_HINT_SHOW";
	
	private final String DEVICE_ID = "NEW_DEVICE_ID";
	private final String DEVICE_CODE = "NEW_DEVICE_CODE";
	private final String DEVICE_VERSION = "NEW_DEVICE_VERSION";
	private final String DEVICE_NAME = "NEW_DEVICE_NAME";
	private final String DEVICE_IP = "NEW_DEVICE_IP";
	private final String WIFI_SSID = "NEW_WIFI_SSID";
	private final String WIFI_PWD = "NEW_WIFI_PWD";
	private final String WIFI_MAC = "NEW_WIFI_MAC";
	private final String USER_ID = "NEW_USER_ID";
	private final String USER_MEM_ID = "NEW_USER_MEME_ID";
	private final String USER_TYPE = "NEW_USER_TYPE";
	private final String USER_TOKEN = "NEW_USER_TOKEN";
	private final String THIRD_ID = "NEW_THIRD_ID";
	private final String THIRD_TOKEN = "NEW_THIRD_TOKEN";
	private final String USER_NAME = "NEW_USER_NAME";
	private final String USER_NICK_NAME = "NEW_USER_NICK_NAME";

	private final String USER_INFO = "NEW_USER_INFO";

	private final String USER_PWD = "NEW_USER_PWD";
	private final String USER_HEAD = "NEW_USER_HEAD";
	private final String USER_ACCOUNT = "NEW_USER_ACCOUNT";
	private final String USER_DEVICES = "NEW_USER_DEVICES";
	private final String LOGIN_TYPE = "NEW_LOGIN_TYPE";
	private final String COIN = "NEW_COIN";
	private final String ADMIN = "NEW_ADMIN";
	private final String PC_CODE = "NEW_PC_CODE";
	private final String SHOW_SYS_MUSIC_FLAG = "NEW_SHOW_SYS_MUSIC_FLAG";
	private final String DEVICE_ADDR = "NEW_DEVICE_ADDR";
	private final String LAT = "NEW_LAT";
	private final String LON = "NEW_LON";
	private final String PLAY_PERMISSION = "NEW_PLAY_PERMISSION";
	private final String PLAY_RES = "NEW_PLAY_RES";
	private final String LAST_REFRESH_TIME = "NEW_LAST_REFRESH_TIME";
	private final String EXCEL_SEND_EMAIL = "NEW_EXCEL_SEND_EMAIL";
	private final String DB_VERSION = "NEW_DB_VERSION";
	
	private final String CHECK_UPDATE_TIME = "NEW_CHECK_UPDATE_TIME";
	
	private MySharedPreference sharedPre = null;

	private static LocalDataEntity INSTANCE = null;
	
	public LocalDataEntity(Context context)
	{
		this.context = context;
		sharedPre = MySharedPreference.newInstance(context);
	}
	public static LocalDataEntity newInstance(Context context)
	{
		if(INSTANCE == null)
			INSTANCE = new LocalDataEntity(context);
		return INSTANCE;
	}

	/*public void init(Context context)
	{
		this.context  = context;
		INSTANCE = new LocalDataEntity(context);
	}
	public static LocalDataEntity getInstance()
	{
		if(INSTANCE == null)
			INSTANCE = new LocalDataEntity(context);
		return INSTANCE;
	}*/
	
	public void setCheckUpdateTime(long time)
	{
		sharedPre.setData(CHECK_UPDATE_TIME, time);
	}
	public long getCheckUpdateTime()
	{
		return sharedPre.getDataLong(CHECK_UPDATE_TIME, 0);
	}
	
	public void setExcelEmail(String email)
	{
		sharedPre.setData(EXCEL_SEND_EMAIL, email);
	}
	public String getExcelEmail()
	{
		return sharedPre.getData(EXCEL_SEND_EMAIL, "");
	}
	
	public void setDBUpdate(String version)
	{
		sharedPre.setData(DB_VERSION, version);
	}
	public boolean isDBUpdate()
	{
		return sharedPre.getData(DB_VERSION, "0").equals("0");
	}
	public void setLoginType(String type)
	{
		sharedPre.setData(LOGIN_TYPE, type);
	}
	public String getLoginType()
	{
		return sharedPre.getData(LOGIN_TYPE, "");
	}
	public void setThirdId(String id)
	{
		sharedPre.setData(THIRD_ID, id);
	}
	public String getThirdId()
	{
		return sharedPre.getData(THIRD_ID, "");
	}
	public void setThirdToken(String token)
	{
		sharedPre.setData(THIRD_TOKEN, token);
	}
	public String getThirdToken()
	{
		return sharedPre.getData(THIRD_TOKEN, "");
	}
	
	public void setUserName(String name)
	{
		sharedPre.setData(USER_NAME, name);
	}
	public void setUserPwd(String pwd)
	{
		sharedPre.setData(USER_PWD, pwd);
	}
	public String getUserName()
	{
		return sharedPre.getData(USER_NAME, "DG-" + Build.MODEL);
	}
	public String getUserId()
	{
		return sharedPre.getData(USER_ID, SystemUtils.getDeviceId(context));
	}
	public String getUserHead()
	{
		return sharedPre.getData(USER_HEAD, "");
	}
	public String getPcCode()
	{
		return sharedPre.getData(PC_CODE, "");
	}
	public void addUserDevices(String devId)
	{
		String devices = sharedPre.getData(USER_DEVICES, "");
		devices += devId;
		sharedPre.setData(USER_DEVICES, devices);
	}


	public void setUserString(String jasonStr)
	{
		sharedPre.setData(USER_INFO, jasonStr);
	}

	public void setUserInfor(UserInfo userInfor)
	{
		sharedPre.setData(USER_ID, userInfor.getId());
		sharedPre.setData(USER_TYPE, userInfor.getType());
		sharedPre.setData(USER_NAME, userInfor.getMerchant().getName());
		sharedPre.setData(USER_MEM_ID, userInfor.getMerchant().getId());
		sharedPre.setData(USER_NICK_NAME, userInfor.getNickName());
		sharedPre.setData(USER_PWD, userInfor.getPwd());
		sharedPre.setData(USER_ACCOUNT, userInfor.getUsername());
		sharedPre.setData(USER_TOKEN, userInfor.getToken());
		sharedPre.setData(PC_CODE, userInfor.getMerchant().getBindCode());
	}
	public UserInfo getUserInfor()
	{
		UserInfo userInfor = new UserInfo();
		String userId = sharedPre.getData(USER_ID, "");
		String userMemId = sharedPre.getData(USER_MEM_ID, "");
		String userName = sharedPre.getData(USER_NAME, "DG-" + Build.MODEL);
		String userNickName = sharedPre.getData(USER_NICK_NAME, "DG-" + Build.MODEL);
		String userPwd = sharedPre.getData(USER_PWD, "");
		String head = sharedPre.getData(USER_HEAD, "");
		String userAccount = sharedPre.getData(USER_ACCOUNT, "");
		String userType = sharedPre.getData(USER_TYPE, "");
		String pcCode = sharedPre.getData(PC_CODE, "");
		String token = sharedPre.getData(USER_TOKEN, "");
		userInfor.setId(userId);
		userInfor.setNickName(userNickName);
		//userInfor.setHead(head);

		if(userInfor.getMerchant() != null)
		{
			userInfor.getMerchant().setBindCode(pcCode);
			userInfor.getMerchant().setName(userName);
			userInfor.getMerchant().setId(userMemId);
		}
		userInfor.setToken(token);
		userInfor.setPwd(userPwd);
		userInfor.setType(userType);
		userInfor.setUsername(userAccount);

		return userInfor;
	}
	public void clearUserInfor()
	{
		sharedPre.setData(USER_ID, "");
		sharedPre.setData(USER_TYPE, "");
		sharedPre.setData(USER_NAME, "");
		sharedPre.setData(USER_PWD, "");
		sharedPre.setData(USER_ACCOUNT, "");
		sharedPre.setData(THIRD_ID, "");
		sharedPre.setData(THIRD_TOKEN, "");
		sharedPre.setData(USER_DEVICES, "");
		setLoginType("");
		sharedPre.setData(ADMIN, false);
	}
	
	public boolean isAdminUser()
	{
		String userType = sharedPre.getData(USER_TYPE, "");
		return userType.equals("2");
	}
	public boolean isNormalUser()
	{
		String userType = sharedPre.getData(USER_TYPE, "");
		return userType.equals("0");
	}
	
	public void updateDeviceName(String deviceName)
	{
		sharedPre.setData(DEVICE_NAME, deviceName);
	}
	public void updateDeviceVersion(String version)
	{
		sharedPre.setData(DEVICE_VERSION, version);
	}
	

	public String getDeviceId()
	{
		return sharedPre.getData(DEVICE_ID, "");
	}
	public void setDeviceCode(String code)
	{
		if(!TextUtils.isEmpty(code))
			sharedPre.setData(DEVICE_CODE, code);
	}
	public String getDeviceCode()
	{
		return sharedPre.getData(DEVICE_CODE, "");
	}
	public String getDeviceVersion()
	{
		return sharedPre.getData(DEVICE_VERSION, "");
	}
	public String getDeviceName()
	{
		return sharedPre.getData(DEVICE_NAME, "");
	}
	public String getWifiName()
	{
		return sharedPre.getData(WIFI_SSID, "");
	}
	public String getWifiPwd()
	{
		return sharedPre.getData(WIFI_PWD, "");
	}
	public String getWifiMac()
	{
		return sharedPre.getData(WIFI_MAC, "");
	}
	public String getDeviceIp()
	{
		return sharedPre.getData(DEVICE_IP, "");
	}
	

	
	/*public void setWifiPwd(String wifiPwd)
	{
		sharedPre.setData(WIFI_PWD, wifiPwd);
	}*/
	
	public void setAdmin(boolean authority)
	{
		sharedPre.setData(ADMIN, authority);
	}
	public boolean isAdmin()
	{
		return sharedPre.getData(ADMIN, false);
	}
	
	public void setMusiIndex(int index)
	{
		sharedPre.setData(MUSIC_INDEX, index);
	}
	public int getMusiIndex()
	{
		return sharedPre.getData(MUSIC_INDEX, 0);
	}
	
	public void setRenderDevice(String name)
	{
		sharedPre.setData(RENDER_DEVICE, name);
	}
	public String getRenderDevice()
	{
		return sharedPre.getData(RENDER_DEVICE, "");
	}
	
	public void setServerDevice(String name)
	{
		sharedPre.setData(SERVER_DEVICE, name);
	}
	public String getServerDevice()
	{
		return sharedPre.getData(SERVER_DEVICE, "");
	}
	
	public void setFirstInit(boolean isFirst)
	{
		sharedPre.setData(FIRST_INIT, isFirst);
	}
	public boolean isFirstInit()
	{
		return sharedPre.getData(FIRST_INIT, true);
	}
	
	public void setCoin(int coin)
	{
		sharedPre.setData(COIN, coin);
	}
	public int getCoin()
	{
		return sharedPre.getData(COIN, 0);
	}
	public void removeCoin(int coin)
	{
		int total = getCoin();
		if(coin >= total)
			setCoin(0);
		else
			setCoin(total - coin);
	}
	
	public void setRefreshTime(long time)
	{
		sharedPre.setData(LAST_REFRESH_TIME, time);
	}
	public long getRefreshTime()
	{
		return sharedPre.getDataLong(LAST_REFRESH_TIME, System.currentTimeMillis());
	}
	
	public void setSongHintShow(boolean isShow)
	{
		sharedPre.setData(IS_SONG_HINT_SHOW, isShow);
	}
	public boolean getSongHintShow()
	{
		return sharedPre.getData(IS_SONG_HINT_SHOW, true);
	}
	
	public void setPlayPermission(String permission)
	{
		sharedPre.setData(PLAY_PERMISSION, permission);
	}
	public String getPlayPermission()
	{
		return sharedPre.getData(PLAY_PERMISSION, "");
	}
	
	public void setPlayRes(String playRes)
	{
		sharedPre.setData(PLAY_RES, playRes);
	}
	public String getPlayRes()
	{
		return sharedPre.getData(PLAY_RES, "");
	}
	
	public void setLat(String lat)
	{
		sharedPre.setData(LAT, lat);
	}
	public void setLon(String lon)
	{
		sharedPre.setData(LON, lon);
	}
	
	public void setDeviceAddr(String addr)
	{
		sharedPre.setData(DEVICE_ADDR, addr);
	}
	
	public String getDeviceAddr()
	{
		return sharedPre.getData(DEVICE_ADDR, "");
	}
	public String getLat()
	{
		return sharedPre.getData(LAT, "");
	}
	public String getLon()
	{
		return sharedPre.getData(LON, "");
	}
	

}
