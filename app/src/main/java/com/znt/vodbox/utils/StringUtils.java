
package com.znt.vodbox.utils;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/** 
 * @ClassName: StringUtils 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-2-11 涓婂崍11:54:03  
 */
public class StringUtils
{

	private static long TIMEMSUNIT = 1000;
	private static long TIMEUNIT = 60;
	
	private static long STOREUNIT = 1024;

	public static int countStr(String str1, String str2) {
		if (str1.indexOf(str2) == -1) {
			return 0;
		}
		else
		{
			String[] tempStr = str1.split(str2);
			return tempStr.length-1;
		}

	}

	public static boolean isNumeric(String str){
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if( !isNum.matches() ){
			return false;
		}
		return true;
	}


	public static boolean isEmpty(String value) 
	{
		return value == null || value.equals("");
	}


	public static String[] splitUrls(String url, String tag) 
	{
		String[] urls = url.split(tag);
		return urls;
	}


	public static String toDBC(String input) 
	{          
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) 
        {              
	        if (c[i] == 12288) 
	        {                 
		        c[i] = (char) 32;                  
		        continue;
	        }
	         if (c[i] > 65280 && c[i] < 65375)
	            c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }  
	

	/**
    * @Description: 瀛楃涓茶浆鏁村舰
    * @param @param str
    * @param @return   
    * @return int 
    * @throws
     */
    public static int stringToInt(String str)
    {
    	if(str == null)
    		return -1;
    	return Integer.parseInt(str);
    }
	
	public static String getHeadByTag(String tag, String body)
	{
		if(body == null || tag == null || tag.length() == 0 || body.length() == 0)
			return "";
		String tempStr = null;
		if(body.endsWith(tag))
			body = body.substring(0, body.lastIndexOf(tag));
		if(body.contains(tag))
		{
			tempStr = body.substring(0, body.lastIndexOf(tag));
		}
		if(tempStr == null)
			tempStr = "";
		return tempStr;
	}
	
	public static String getLastByTag(String tag, String body)
	{
		if(body == null || tag == null || tag.length() == 0 || body.length() == 0)
			return "";
		String tempStr = null;
		if(body.endsWith(tag))
			body = body.substring(0, body.lastIndexOf(tag));
		if(body.contains(tag))
		{
			tempStr = body.substring(body.lastIndexOf(tag) + 1);
		}
		if(tempStr == null)
			tempStr = "";
		return tempStr;
	}
	
	/**
	* @Description: 灏嗗瓧绗︿覆杞负浠ndStr缁撳熬
	* @param @param tag  
	* @param @param endStr
	* @param @param body
	* @param @return   
	* @return String 
	* @throws
	 */
	public static String getStringEndsWithStr(String tag, String endStr, String body)
	{
		String resulStr = body;
		if(!resulStr.endsWith(endStr))
		{
			resulStr = getHeadByTag(tag, body) + endStr;
		}
		return resulStr;
	}
	

	/**
    * @Description: 鑾峰彇鐧惧垎鐧�
    * @param @param progress
    * @param @param total
    * @param @return   
    * @return int 
    * @throws
     */
    public static int getProgress(long progress, long total)
    {
    	float f = (float)progress/total;
    	if(f > 0)
    	{
    		BigDecimal bd = new BigDecimal(f);
    		bd = bd.setScale(2,BigDecimal.ROUND_UP);//鍙�3.1415926灏忔暟鐐瑰悗闈簩浣�
    		float f1 = Float.parseFloat(bd+"");
    		float result = f1*100;
    		int l = (int)result;
    		return l;
    	}
		return 0;
    }
    public static int getProgress(int progress, int total)
    {
    	float f = (float)progress/total;
    	BigDecimal bd = new BigDecimal(f);
    	bd = bd.setScale(2,BigDecimal.ROUND_UP);//鍙�3.1415926灏忔暟鐐瑰悗闈簩浣�
    	float f1 = Float.parseFloat(bd+"");
    	float result = f1*100;
    	int l = (int)result;
    	return l;
    }
    
    /**
	* @Description: 璁剧疆瀛楃涓查鑹� 濡傦細
	* exam1:editText2.setText(Html.fromHtml(  "<font color=#E61A6B>绾㈣壊浠ｇ爜</font> "+ "<i><font color=#1111EE>钃濊壊鏂滀綋浠ｇ爜</font></i>"
銆�銆�銆�銆�銆�銆�銆�銆�銆�銆�銆�銆�銆�銆�銆�銆�銆�銆�銆�銆�銆�銆�銆�銆�+"<u><i><font color=#1111EE>钃濊壊鏂滀綋鍔犵矖浣撲笅鍒掔嚎浠ｇ爜</font></i></u>"));
	* exam2:String temp = "name:<br /><font color=\"teal\">hello<small>title<b>activeBalance</b></small></font>"; 
	* 璇存槑锛�<br />锛氳〃绀烘崲琛岋紝鍜屸�淺n鈥濅竴鏍枫��
			<small>content</small>锛氳〃绀哄皬瀛椾綋銆�
			<font color=\"teal\">content</font>锛氳缃鑹诧紝teal鏄潚鑹层��
			<b>content</b>锛氳〃绀虹矖浣�
			<u>content</u>锛氳〃绀轰笅妯嚎
	* @param @param content  鍐呭
	* @param @param color 棰滆壊
	* @param @param bold 鏄惁绮椾綋
	* @param @param italic 鏄惁鏂滀綋
	* @param @param underline 鏄惁涓嬪垝绾�
	* @param @param size 澶у皬  0:small 1: big  
	* @param @return   
	* @return String  瀛楃涓茬殑鏍煎紡  閫氳繃Html.fromHtml(inforStyle)瑙ｆ瀽鍚庡啀鏄剧ず
	* @throws
	 */
	public static  String setTextStyle(String content, String color, boolean bold, boolean italic, boolean underline, int sizeType)
	{
		String result = "<font color=" + color + ">" + content + "</font>";
		if(bold)
			result = packageStrings("<b>", result, "</b>");
		if(italic)
			result = packageStrings("<i>", result, "</i>");
		if(underline)
			result = packageStrings("<u>", result, "</u>");
		if(sizeType == 0)
			result = packageStrings("<small>", result, "</small>");
		else if(sizeType == 1)
			result = packageStrings("<big>", result, "</big>");
		return result;
	}
	
	/**
     * 璁剧疆瀛愬瓧绗︿覆涓虹孩鑹�
     * @param text
     * @param colorText
     * @return
     */
    public static SpannableString setColorText(String text, String colorText)
    {
        //鍒涘缓涓�涓� SpannableString瀵硅薄    
        SpannableString msp = new SpannableString(text);
        Pattern p=Pattern.compile(colorText);
        Matcher matcher=p.matcher(text);
        while(matcher.find())
        {
            msp.setSpan(new ForegroundColorSpan(Color.RED), 
                    matcher.start(), matcher.end(), 
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return msp;
    }
    
    /**
     * 璁剧疆瀛愬瓧绗︿覆涓虹孩鑹插拰瀛椾綋澶у皬
     * @param text
     * @param colorText
     * @param colorTextSize
     * @return
     */
    public static SpannableString setColorText(String text, String colorText, float colorTextSize)
    {
        //鍒涘缓涓�涓� SpannableString瀵硅薄    
        SpannableString msp = new SpannableString(text);
        Pattern p = Pattern.compile(colorText);
        Matcher matcher = p.matcher(text);
        while(matcher.find())
        {
            msp.setSpan(new ForegroundColorSpan(Color.RED), 
                    matcher.start(), matcher.end(), 
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            msp.setSpan(new RelativeSizeSpan((colorTextSize)), 
                    matcher.start(), matcher.end(), 
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return msp;
    }
    /**
    * @Description: 璁剧疆鎸囧畾瀛楃涓查鑹插拰澶у皬
    * @param @param text
    * @param @param colorText
    * @param @param colorTextSize
    * @param @param color
    * @param @return   
    * @return SpannableString 
    * @throws
     */
    public static SpannableString setColorText(String text, String colorText, float colorTextSize, int color)
    {
    	//鍒涘缓涓�涓� SpannableString瀵硅薄    
    	SpannableString msp = new SpannableString(text);
    	Pattern p = Pattern.compile(colorText);
    	Matcher matcher = p.matcher(text);
    	while(matcher.find())
    	{
    		msp.setSpan(new ForegroundColorSpan(color), 
    				matcher.start(), matcher.end(), 
    				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    		
    		msp.setSpan(new RelativeSizeSpan((colorTextSize)), 
    				matcher.start(), matcher.end(), 
    				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    	}
    	return msp;
    }
	
	public static String packageStrings(String str1, String str, String str2)
	{
		if(str1 == null && str2 != null)
			return str + str2;
		if(str1 != null && str2 == null)
			return str1 + str;
		if(str1 == null && str2 == null)
			return null;
		return str1 + str + str2;
	}
	
	/**
    * @Description: 鑾峰彇鏃堕棿鏍煎紡
    * @param @param time
    * @param @return   
    * @return String 
    * @throws
     */
    public static String getFormatTime(long time) 
    {
        double second = (double) time / TIMEMSUNIT;
        if (second < 1) 
        {
            return time + " MS";
        }

        double minute = second / TIMEUNIT;
        if (minute < 1) 
        {
            BigDecimal result = new BigDecimal(Double.toString(second));
            return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " SEC";
        }

        double hour = minute / TIMEUNIT;
        if (hour < 1) 
        {
            BigDecimal result = new BigDecimal(Double.toString(minute));
            return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " MIN";
        }

        BigDecimal result = new BigDecimal(Double.toString(hour));
        return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " H";
    }
    
    /**
	* @Description: 鑾峰彇纾佺洏绌洪棿澶у皬鏍煎紡
	* @param @param size
	* @param @return   
	* @return String 
	* @throws
	 */
	public static String getFormatSize(double size) 
	{
        double kiloByte = size / STOREUNIT;
        if (kiloByte < 1) 
        {
            return size + " Byte";
        }

        double megaByte = kiloByte / STOREUNIT;
        if (megaByte < 1)
        {
            BigDecimal result = new BigDecimal(Double.toString(kiloByte));
            return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " KB";
        }

        double gigaByte = megaByte / STOREUNIT;
        if (gigaByte < 1) 
        {
            BigDecimal result = new BigDecimal(Double.toString(megaByte));
            return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " MB";
        }

        double teraBytes = gigaByte / STOREUNIT;
        if (teraBytes < 1)
        {
            BigDecimal result = new BigDecimal(Double.toString(gigaByte));
            return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " GB";
        }
        BigDecimal result = new BigDecimal(teraBytes);
        return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " TB";
    }
    
    /**
	  * 鍒ゆ柇鏄惁鏄偖绠�
	  * @param param
	  * @return
	  */
	 public static boolean isEmail(String param)
	 {
		 if(param == null)
			 return false;
		 if(param.length() <= 0)
			 return false;
		 boolean flag=false;
		 Pattern p = Pattern.compile("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\\.([a-zA-Z0-9_-])+)+$");
		 Matcher m = p.matcher(param);
		 flag=m.matches();
		 return flag;
	 } 
	 
	 /**
	 * 楠岃瘉鎵嬫満鏍煎紡
	* @Description: TODO
	* @param @param mobiles
	* @param @return   
	* @return boolean 
	* @throws
	 */
	public static boolean isMobileNO(String mobiles) 
	{
		/*
		绉诲姩锛�134銆�135銆�136銆�137銆�138銆�139銆�150銆�151銆�157(TD)銆�158銆�159銆�187銆�188
		鑱旈�氾細130銆�131銆�132銆�152銆�155銆�156銆�185銆�186
		鐢典俊锛�133銆�153銆�180銆�189銆侊紙1349鍗�氾級
		鎬荤粨璧锋潵灏辨槸绗竴浣嶅繀瀹氫负1锛岀浜屼綅蹇呭畾涓�3鎴�5鎴�8锛屽叾浠栦綅缃殑鍙互涓�0-9
		*/
		if(mobiles.startsWith("400"))
			return true;
		String telRegex = "[1][358769]\\d{9}";//"[1]"浠ｈ〃绗�1浣嶄负鏁板瓧1锛�"[358]"浠ｈ〃绗簩浣嶅彲浠ヤ负3銆�5銆�8涓殑涓�涓紝"\\d{9}"浠ｈ〃鍚庨潰鏄彲浠ユ槸0锝�9鐨勬暟瀛楋紝鏈�9浣嶃��
		if (TextUtils.isEmpty(mobiles)) 
			return false;
		else 
			return mobiles.matches(telRegex);
    }
	 
	
	/** 
	   *  鑾峰彇鏃堕棿鎴�
	   * @return 
	   */ 
	public static String generateTimeStamp() 
	{ 
		return String.valueOf(System.currentTimeMillis() / 1000); 
	} 
	  /** 
	   * 鍗曟浜х敓鍊�
	   * @param is32 
	   *            32 
	   * @return 
	   */ 
	public static String generateNonce(boolean is32) 
	{ 
		Random random = new Random(); 
		// 1234009999999 
		String result = String.valueOf(random.nextInt(9876599) + 123400); 
		if (is32) 
		{ 
			// MD5 
			try 
			{ 
				MessageDigest md = MessageDigest.getInstance("MD5"); 
				md.update(result.getBytes()); 
		        byte b[] = md.digest(); 
		        int i; 
		        StringBuffer buf = new StringBuffer(""); 
		        for (int offset = 0; offset < b.length; offset++) 
		        { 
		        	i = b[offset]; 
		        	if (i < 0) 
		        		i += 256; 
		        	if (i < 16) 
		        		buf.append("0"); 
		        	buf.append(Integer.toHexString(i)); 
		        } 
		        result = buf.toString(); 
			} 
			catch (NoSuchAlgorithmException e) 
			{ 
				e.printStackTrace(); 
			} 
		} 
		return result; 
	} 
	
      
	private static String byteToHexString(byte ib)
	{  
	      char[] Digit={  '0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f' };  
	      char[] ob=new char[2];  
	      ob[0]=Digit[(ib>>>4)& 0X0f];  
	      ob[1]=Digit[ib & 0X0F];  
	      String s=new String(ob);  
	      return s;           
	}       
	
	public static int dip2px(Activity activity, float dipValue) 
	{  
		float scale = activity.getResources().getDisplayMetrics().density; 
		return (int) (dipValue * scale + 0.5f); 
	}   
	public static int px2dip(Activity activity, float pxValue) 
	{  
		float scale = activity.getResources().getDisplayMetrics().density; 
		return (int) (pxValue / scale + 0.5f); 
	}
	public static int dip2px(Context activity, float dipValue) 
	{  
		float scale = activity.getResources().getDisplayMetrics().density; 
		return (int) (dipValue * scale + 0.5f); 
	}   
	public static int px2dip(Context activity, float pxValue) 
	{  
		float scale = activity.getResources().getDisplayMetrics().density; 
		return (int) (pxValue / scale + 0.5f); 
	}
	
	/**
	 * 鎾斁鏃堕棿杞崲
	 */
	public static String durationToTime(int time) {

		time /= 1000;
		int minute = time / 60;
		int second = time % 60;
		minute %= 60;
		return String.format("%02d:%02d", minute, second);
	}
	
	public static int formatDurationString(String durationString){
		int duration = 0;
		if (durationString == null || durationString.length() == 0){
			return duration;
		}
		
		double a = 3.2;
		int b = (int) a;
		try {
			String sArray[] = durationString.split(":");
			double hour = Double.valueOf(sArray[0]);
			double minute = Double.valueOf(sArray[1]);
			double second = Double.valueOf(sArray[2]);		
			
			return (int) (((hour * 60 + minute) * 60 + second) * 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return duration;
	}
	
	public static String secToTime(int time) 
	{
		String timeStr = null;
		int hour = 0;
		int minute = 0;
		int second = 0;
		if (time <= 0)
			return "00:00:00";
		else 
		{
			minute = time / 60;
			if (minute < 60) 
			{
				second = time % 60;
				timeStr = "00:" + unitFormat(minute) + ":" + unitFormat(second);
			} 
			else
			{
				hour = minute / 60;
				if (hour > 99)
					return "99:59:59";
				minute = minute % 60;
				second = time - hour * 3600 - minute * 60;
				timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":"
						+ unitFormat(second);
			}
		}
		return timeStr;
	}
	public static String getStrTime(int time) 
	{
		String timeStr = null;
		int hour = 0;
		int minute = 0;
		int second = 0;
		if (time <= 0)
			return "00灏忔椂00鍒�00绉�";
		else 
		{
			minute = time / 60;
			if (minute < 60) 
			{
				second = time % 60;
				timeStr = "00灏忔椂" + unitFormat(minute) + "鍒�" + unitFormat(second)+"绉�";
			} 
			else
			{
				hour = minute / 60;
				if (hour > 99)
					return "99灏忔椂59鍒�59绉�";
				minute = minute % 60;
				second = time - hour * 3600 - minute * 60;
				timeStr = unitFormat(hour) + "灏忔椂" + unitFormat(minute) + "鍒�"
						+ unitFormat(second) +"绉�";
			}
		}
		return timeStr;
	}

	public static String unitFormat(int i)
	{
		String retStr = null;
		if (i >= 0 && i < 10)
			retStr = "0" + Integer.toString(i);
		else if (i >= 10 && i <= 60) 
		{
			retStr = "" + i;
		} 
		else 
		{
			retStr = "00";
		}
		return retStr;
	}
	/**
	 * Convert time from "00:00:00" to seconds.
	 * 
	 * @param length
	 *            00:00:00鎴栬��00:00
	 * @return The length in seconds.
	 */
	public static int getIntLength(String length) 
	{
		if (TextUtils.isEmpty(length))
		{
			return -1;
		}
		String[] split = length.split(":");
		int count = -1;
		try 
		{
			if (split.length == 3) 
			{
				count += (Integer.parseInt(split[0])) * 60 * 60;
				count += Integer.parseInt(split[1]) * 60;
				count += Integer.parseInt(split[2]);
			} 
			else if (split.length == 2) 
			{
				count += Integer.parseInt(split[0]) * 60;
				count += Integer.parseInt(split[1]);
			}
		} 
		catch (NumberFormatException e)
		{
			e.printStackTrace();
		}
		return count;
	}
	
	public static List<String> stringsToList(String[] strs)
	{
		List<String> list = new ArrayList<String>();
		int size = strs.length;
		for(int i=0;i<size;i++)
		{
			list.add(strs[i]);
		}
		
		return list;
	}
	
	public static String getInforFromJason(JSONObject json, String key)
	{
		if(json == null || key == null)
			return "";
		if(json.has(key))
		{
			try
			{
				String result = json.getString(key);
				if(result.equals("null"))
					result = "";
				return result;
				//return StringUtils.decodeStr(result);
			} 
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "";
	}
	
	public static String getPlanTimeShow(String endTime)
	{
		String[] ts = endTime.split(":");
		String hour = null;
		String min = null;
		String sec = null;
		if(ts.length > 0)
			hour = ts[0];
		if(ts.length > 1)
			min = ts[1];
		if(ts.length > 2)
			sec = ts[2];
		if(!TextUtils.isEmpty(sec) && !TextUtils.isEmpty(min))
		{
			int secInt = Integer.parseInt(sec);
			if(secInt > 0)
			{
				int minInt = Integer.parseInt(min);
				int hourInt = Integer.parseInt(hour);
				minInt += 1;
				if(minInt == 60)
				{
					min = "00";
					hourInt = hourInt + 1;
					if(hourInt < 10)
						hour = "0" + hourInt;
					else
						hour = "" + hourInt;
				}
				else if(minInt < 10)
					min = "0" + minInt;
				else
					min = "" + minInt;
			}
		}
		return hour + ":" + min;
	}
	
	/** 
	* 瀹炵幇鏂囨湰澶嶅埗鍔熻兘 
	* add by wangqianzhou 
	* @param content 
	*/  
	public static void copy(String content, Context context)  
	{  
		// 寰楀埌鍓创鏉跨鐞嗗櫒  
		ClipboardManager cmb = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);  
		cmb.setText(content.trim());  
	}  
	/** 
	* 瀹炵幇绮樿创鍔熻兘 
	* add by wangqianzhou 
	* @param context 
	* @return 
	*/  
	public static String paste(Context context)  
	{  
		// 寰楀埌鍓创鏉跨鐞嗗櫒  
		ClipboardManager cmb = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);  
		return cmb.getText().toString().trim();  
	}  
	
	public static String getStringById(Context context, int id)
	{
		return context.getResources().getString(id);
	}
}
 
