package com.znt.vodbox.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.znt.vodbox.model.UserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: DBManager
 * @Description: TODO
 * @author yan.yu
 * @date 2014-8-14 上午10:25:36  
 */
public class DBManager extends MyDbHelper
{

	private static DBManager INSTANCE = null;

	private Context context = null;

	/**
	 * <p>Title: </p>
	 * <p>Description: </p>
	 * @param c
	 */
	public DBManager(Context c)
	{
		super(c);
		this.context = c;
		// TODO Auto-generated constructor stub
	}

	public static DBManager newInstance(Context c)
	{
		if(INSTANCE == null)
		{
			synchronized (DBManager.class)
			{
				if(INSTANCE == null)
					INSTANCE = new DBManager(c);
			}
		}
		return INSTANCE;
	}

	public void deleteAllMusic()
	{
		Cursor cur = query(TBL_MUSIC);
		if(cur != null)
		{
			while(cur.moveToNext())
			{
				String music_id = cur.getString(cur.getColumnIndex("music_id"));
				delete("music_id", music_id, TBL_MUSIC);
			}
		}
		cur.close();
	}


	/************̷̑٨Ⱥ݇¼*************/
	public int getSearchRecordCount()
	{
		Cursor cur = query(TBL_SEARCH_RECORD);
		return cur.getCount();
	}
	public List<String> getSearchRecordList()
	{
		List<String> tempList = new ArrayList<String>();
		Cursor cur = query(TBL_SEARCH_RECORD);
		if(cur.getCount() == 0)
			return tempList;
		if(cur != null)
		{
			while(cur.moveToNext())
			{
				String name = cur.getString(cur.getColumnIndex("name"));

				tempList.add(name);
			}
		}
		cur.close();
		return tempList;
	}
	public long setSearchRecord(String key)
	{
		if(!isSearchRecordExist(key))
		{
			deleteEndSearchRecord();

			ContentValues values = new ContentValues();
			values.put("name", key);
			values.put("modify_time", System.currentTimeMillis());
			return insert(values, TBL_SEARCH_RECORD);
		}
		return -1;
	}
	private void deleteEndSearchRecord()
	{
		Cursor cur = query(TBL_SEARCH_RECORD);
		if(cur != null)
		{
			if(cur.getCount() >= 6 && cur.moveToLast())
			{
				String name = cur.getString(cur.getColumnIndex("name"));
				delete("name", name, TBL_SEARCH_RECORD);
			}
		}
		cur.close();
	}
	private boolean isSearchRecordExist(String key)
	{
		boolean  result = false;
		Cursor cur = query(TBL_SEARCH_RECORD);
		if(cur != null)
		{
			while(cur.moveToNext())
			{
				String name = cur.getString(cur.getColumnIndex("name"));
				if(name != null && name.equals(key))
				{
					result = true;
					break;
				}
			}
		}
		cur.close();
		return result;
	}
	public void clearSearchRecord()
	{
		Cursor cur = query(TBL_SEARCH_RECORD);
		if(cur != null)
		{
			while(cur.moveToNext())
			{
				String name = cur.getString(cur.getColumnIndex("name"));
				delete("name", name, TBL_SEARCH_RECORD);
			}
		}
		cur.close();
	}


	/************̷֪̑ǌ݇¼*************/
	public int getSearchShopRecordCount()
	{
		Cursor cur = query(TBL_SEARCH_SHOP_RECORD);
		return cur.getCount();
	}
	public List<String> getSearchShopRecordList()
	{
		List<String> tempList = new ArrayList<String>();
		Cursor cur = query(TBL_SEARCH_SHOP_RECORD);
		if(cur.getCount() == 0)
			return tempList;
		if(cur != null)
		{
			while(cur.moveToNext())
			{
				String name = cur.getString(cur.getColumnIndex("name"));

				tempList.add(name);
			}
		}
		cur.close();
		return tempList;
	}
	public long setSearchShopRecord(String key)
	{
		if(!isSearchShopRecordExist(key))
		{
			deleteEndSearchShopRecord();

			ContentValues values = new ContentValues();
			values.put("name", key);
			values.put("modify_time", System.currentTimeMillis());
			return insert(values, TBL_SEARCH_SHOP_RECORD);
		}
		return -1;
	}
	private void deleteEndSearchShopRecord()
	{
		Cursor cur = query(TBL_SEARCH_SHOP_RECORD);
		if(cur != null)
		{
			if(cur.getCount() >= 6 && cur.moveToLast())
			{
				String name = cur.getString(cur.getColumnIndex("name"));
				delete("name", name, TBL_SEARCH_SHOP_RECORD);
			}
		}
		cur.close();
	}
	private boolean isSearchShopRecordExist(String key)
	{
		boolean  result = false;
		Cursor cur = query(TBL_SEARCH_SHOP_RECORD);
		if(cur != null)
		{
			while(cur.moveToNext())
			{
				String name = cur.getString(cur.getColumnIndex("name"));
				if(name != null && name.equals(key))
				{
					result = true;
					break;
				}
			}
		}
		cur.close();
		return result;
	}
	public void clearSearchShopRecord()
	{
		Cursor cur = query(TBL_SEARCH_SHOP_RECORD);
		if(cur != null)
		{
			while(cur.moveToNext())
			{
				String name = cur.getString(cur.getColumnIndex("name"));
				delete("name", name, TBL_SEARCH_SHOP_RECORD);
			}
		}
		cur.close();
	}

	/**
	 * @Description:
	 * @param infor
	 * @return void
	 * @throws
	 */
	public synchronized long insertUser(UserInfo infor)
	{
		if(infor == null)
			return -1;

		if(isUserExist(infor))
		{
			updateUser(infor);
			return 1;
		}

		String login_account = infor.getUsername();
		String login_pwd = infor.getPwd();
		String user_name = infor.getNickName();
		String act_code = infor.getMerchant().getBindCode();
		String user_id = infor.getId();

		ContentValues values = new ContentValues();

		if(!TextUtils.isEmpty(login_account))
			values.put("login_account", login_account);
		if(!TextUtils.isEmpty(login_pwd))
			values.put("login_pwd", login_pwd);
		if(!TextUtils.isEmpty(user_name))
			values.put("user_name", user_name);
		if(!TextUtils.isEmpty(act_code))
			values.put("act_code", act_code);
		if(!TextUtils.isEmpty(user_id))
			values.put("user_id", user_id);

		return insert(values, TBL_USER_LIST);
	}

	/**
	 * @Description:
	 * @param infor
	 * @return void
	 * @throws
	 */
	public synchronized long updateUser(UserInfo infor)
	{
		if(infor == null)
			return -1;

		String login_account = infor.getUsername();
		String login_pwd = infor.getPwd();
		String user_name = infor.getNickName();
		String act_code = infor.getMerchant().getBindCode();
		String user_id = infor.getId();

		ContentValues values = new ContentValues();

		if(!TextUtils.isEmpty(login_account))
			values.put("login_account", login_account);
		if(!TextUtils.isEmpty(login_pwd))
			values.put("login_pwd", login_pwd);
		if(!TextUtils.isEmpty(user_name))
			values.put("user_name", user_name);
		if(!TextUtils.isEmpty(act_code))
			values.put("act_code", act_code);
		if(!TextUtils.isEmpty(user_id))
			values.put("user_id", user_id);

		return edit(TBL_USER_LIST, infor.getId(), "0", values);
	}

	public List<UserInfo> getUserList()
	{
		List<UserInfo> tempList = new ArrayList<UserInfo>();
		Cursor cur = query(TBL_USER_LIST);
		if(cur.getCount() == 0)
			return tempList;
		if(cur != null)
		{
			while(cur.moveToNext())
			{
				String login_account = cur.getString(cur.getColumnIndex("login_account"));
				String login_pwd = cur.getString(cur.getColumnIndex("login_pwd"));
				String user_name = cur.getString(cur.getColumnIndex("user_name"));
				String act_code = cur.getString(cur.getColumnIndex("act_code"));
				String user_id = cur.getString(cur.getColumnIndex("user_id"));

				UserInfo infor = new UserInfo();
				infor.setUsername(login_account);
				infor.setPwd(login_pwd);
				infor.setNickName(user_name);
				infor.getMerchant().setBindCode(act_code);
				infor.setId(user_id);

				tempList.add(infor);
			}
		}
		cur.close();
		return tempList;
	}

	public boolean isUserExist(UserInfo userInfor)
	{
		boolean  result = false;

		Cursor cur = query(TBL_USER_LIST);
		if(cur != null)
		{
			while(cur.moveToNext())
			{
				String user_id = cur.getString(cur.getColumnIndex("user_id"));
				//String wifi_name = cur.getString(cur.getColumnIndex("wifi_name"));
				if(user_id != null && user_id.equals(userInfor.getId()))
				{
					result = true;
					break;
				}
			}
		}
		cur.close();
		return result;
	}

	public synchronized int getUserCount()
	{
		Cursor cur = query(TBL_USER_LIST);
		return cur.getCount();
	}

	public void deleteUser(String userId)
	{
		Cursor cur = query(TBL_USER_LIST);
		if(cur != null)
		{
			while(cur.moveToNext())
			{
				String user_id = cur.getString(cur.getColumnIndex("user_id"));
				if(userId.equals(user_id))
				{
					delete("user_id", user_id, TBL_USER_LIST);
					break;
				}
			}
		}
		cur.close();
	}
}
 
