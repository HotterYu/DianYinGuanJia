
package com.znt.vodbox.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.znt.vodbox.R;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.utils.FileUtils;
import com.znt.vodbox.utils.SystemUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/** 
 * @ClassName: MyDbHelper 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-2-18 涓嬪崍4:02:36  
 */
public class MyDbHelper extends SQLiteOpenHelper
{

	protected SQLiteDatabase db = null;
	
	private File dbFile = null;
	private final String DB_NAME = "znt_vod_box.db";
	private final String ROW_ID = "_id";
	protected final String ORDER_ASC = "modify_time asc";//鍗囧簭鎺掑簭
	protected final String ORDER_DESC = "modify_time desc";//闄嶅簭鎺掑簭
	protected final String TBL_MUSIC = "tbl_play_list";//闊充箰琛�
	protected final String TBL_DEVICE = "tbl_device_list";//璁惧琛�
	protected final String TBL_SONG_LIST = "song_list";//鐐规挱鍒楄〃
	protected final String TBL_SEARCH_RECORD = "search_record";//鎼滅储璁板綍
	protected final String TBL_SEARCH_SHOP_RECORD = "search_shop_record";//鎼滅储璁板綍
	protected final String TBL_USER_LIST = "user_list";//鎼滅储璁板綍
	private String dbDir = null;
	
	public MyDbHelper(Context c)
	{
		super(c, null, null, 2);
		
		dbDir = SystemUtils.getAvailableDir(c, Constant.WORK_DIR + "/db").getAbsolutePath();
		
		dbFile = new File(dbDir + "/" + DB_NAME);
		
		db = getWritableDatabase();
		
		//createDb(dbFile);
		openDatabase(c);
	}
	
	public void deleteDbFile()
	{
		if(dbFile != null && dbFile.exists())
		{
			close();
			dbFile.delete();
		}
	}
	protected int deleteAll(String tblName) 
	{  
		try {
			return db.delete(tblName, null, null);  
		} catch (Exception e) {
			// TODO: handle exception
		}
		return -1;
	}  
	
	/**
	*callbacks
	*/
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		// TODO Auto-generated method stub
		
	}

	/**
	*callbacks
	*/
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// TODO Auto-generated method stub
		/*String sql = "drop table " + TBL_NAME;
        db.execSQL(sql);
        onCreate(db);*/
	}

	/**
	* @Description: 鎵撳紑鏁版嵁搴擄紝濡傛灉娌℃湁灏卞垱寤�
	* @param @param file   
	* @return void 
	* @throws
	 */
	public int createDb(File file)
	{
		if(file == null)
			return 1;
		if(!file.exists())
		{
			int result = FileUtils.createFile(dbFile.getAbsolutePath());
			if(result != 0)
			{
				db = getWritableDatabase();
				return result;
			}
		}
		
		db = SQLiteDatabase.openOrCreateDatabase(file, null);
		
		if(db == null)
			return 1;
		return 0;
	}
	
	public int openDatabase(Context context) 
	{
        try 
        {
            // 鑾峰緱dictionary.db鏂囦欢鐨勭粷瀵硅矾寰�
            File dir = new File(dbDir);
            // 濡傛灉/sdcard/dictionary鐩綍涓瓨鍦紝鍒涘缓杩欎釜鐩綍
            if (!dir.exists())
                dir.mkdirs();
            // 濡傛灉鍦�/sdcard/dictionary鐩綍涓笉瀛樺湪
            // dictionary.db鏂囦欢锛屽垯浠巖es\raw鐩綍涓鍒惰繖涓枃浠跺埌
            // SD鍗＄殑鐩綍锛�/sdcard/dictionary锛�
            if (dbFile != null && !dbFile.exists()) 
            {
                // 鑾峰緱灏佽dictionary.db鏂囦欢鐨処nputStream瀵硅薄
                InputStream is = context.getResources().openRawResource(R.raw.dianyinguanjia_new);
                dbFile.createNewFile();
                FileOutputStream fos = new FileOutputStream(dbFile);
                byte[] buffer = new byte[8192];
                int count = 0;
                // 寮�濮嬪鍒禿ictionary.db鏂囦欢
                while ((count = is.read(buffer)) > 0) 
                {
                    fos.write(buffer, 0, count);
                }

                fos.close();
                is.close();
            }
           
            // 鎵撳紑/sdcard/dictionary鐩綍涓殑dictionary.db鏂囦欢
            db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
            if(db != null)
            	return 0;
        } 
        catch (Exception e) 
        {

        }
        return 1;
    }
	
	/**
	* @Description: 鍒涘缓琛ㄦ牸
	* @param @param tblName   
	* @return void 
	* @throws
	 */
	public int cretaeTbl(String tblName)
	{
		if(TextUtils.isEmpty(tblName))
			return 1;
		String CREATE_TBL = " create table "  
	            + tblName + " (" + ROW_ID + " integer primary key autoincrement, name text, url text, size long) "; 
		try
		{
			if (db == null)  
	            db = getWritableDatabase();  
			db.execSQL(CREATE_TBL);
		} 
		catch (Exception e)
		{
			// TODO: handle exception
			return 1;
		}
		return 0;
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onOpen(SQLiteDatabase db)
	{
		// TODO Auto-generated method stub
		super.onOpen(db);
	}
	
	/**
	* @Description: 鎻掑叆涓�鏉℃暟鎹�
	* @param @param values
	* @param @param tblName
	* @param @return   
	* @return long 
	* @throws
	 */
	public long insert(ContentValues values, String tblName)
	{
		if (db == null)  
            db = getWritableDatabase();
		return db.insert(tblName, null, values);
	}
	
	/**
	* @Description: 鏌ヨ
	* @param @param tblName
	* @param @return   
	* @return Cursor 
	* @throws
	 */
	public Cursor query(String tblName)
	{
		
		//Cursor cur = db.rawQuery("SELECT * FROM " + tblName, null);
		
		if (db == null || !db.isOpen())  
            db = getWritableDatabase();
		Cursor cursor = db.query(tblName, null, null, null, null, null, ORDER_DESC);
		return cursor;
	}
	
	public void edit(String tblName, int id, String key, String newValue)
	{
		if(TextUtils.isEmpty(newValue) || TextUtils.isEmpty(key))
			return ;
		if (db == null)  
            db = getWritableDatabase();  
		ContentValues values = new ContentValues();
		values.put(key, newValue);
		try
		{
			db.update(tblName, values, ROW_ID + "=" + Integer.toString(id), null);
		} 
		catch (Exception e)
		{
			// TODO: handle exception
		}
	}
	
	/**
	 *  鏇存柊澶氫釜瀛楁
	 * @param tblName
	 * @param id
	 * @param values
	 * @return
	 */
	protected int edit(String tblName, String id, ContentValues values)
	{
		if (db == null)  
            db = getWritableDatabase();  
		values.put("modify_time", System.currentTimeMillis());
		try
		{
			return db.update(tblName, values, "music_id=?", new String[]{id});
		} 
		catch (Exception e)
		{
		}
		return -1;
	}
	protected int edit(String tblName, String key, String id, ContentValues values)
	{
		if (db == null)  
			db = getWritableDatabase();  
		values.put("modify_time", System.currentTimeMillis());
		try
		{
			return db.update(tblName, values, key + "=?", new String[]{id});
		} 
		catch (Exception e)
		{
		}
		return -1;
	}
	
	/**
	* @Description: 鍒犻櫎涓�鏉℃暟鎹�
	* @param @param id
	* @param @param tblName
	* @param @return   
	* @return int 
	* @throws
	 */
	public int delete(int id, String tblName) 
	{  
        if (db == null)  
            db = getWritableDatabase();  
        return db.delete(tblName, ROW_ID + "=?", new String[] { String.valueOf(id) });  
    }  
	protected int delete(String key, String value, String tblName) 
	{  
		if (db == null)  
			db = getWritableDatabase();  
		return db.delete(tblName, key + "=?", new String[] { value });  
	}  
	
	/**
	* @Description: 鍒犻櫎鏁版嵁琛�
	* @param @param tblName
	* @param @return   
	* @return int 
	* @throws
	 */
	public int deleteTbl(String tblName)
	{
		if(TextUtils.isEmpty(tblName))
			return 1;
		String sql = "drop table " + tblName;
		if (db == null)  
            db = getWritableDatabase();  
        db.execSQL(sql);
        
        return 0;
	}
	
    public void close() 
    {  
        if (db != null)  
        {
        	db.close();
        	db = null;
        }
    }  
}
 
