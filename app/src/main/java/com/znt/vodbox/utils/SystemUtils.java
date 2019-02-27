package com.znt.vodbox.utils;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.DateUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Created by hzwangchenyan on 2016/3/22.
 */
public class SystemUtils {

    /**
     * 妫�娴嬬綉缁滄槸鍚﹁繛鎺�
     *
     * @return
     */
    public static boolean isNetConnected(Context context)
    {
        if(context == null)
            return false;

        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null)
        {
            NetworkInfo[] infos = cm.getAllNetworkInfo();
            if (infos != null)
            {
                for (NetworkInfo ni : infos)
                {
                    if (ni.isConnected())
                    {
                        return true;
                    }
                }
            }
        }
        return false;
        //return WifiFactory.newInstance(context).getWifiAdmin().isWifiEnabled();
    }

    /**
     * 判断是否有Activity在运行
     */
    public static boolean isStackResumed(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
        ActivityManager.RunningTaskInfo runningTaskInfo = runningTaskInfos.get(0);
        return runningTaskInfo.numActivities > 1;
    }

    /**
     * 判断Service是否在运行
     */
    public static boolean isServiceRunning(Context context, Class<? extends Service> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isFlyme() {
        String flymeFlag = getSystemProperty("ro.build.display.id");
        return !TextUtils.isEmpty(flymeFlag) && flymeFlag.toLowerCase().contains("flyme");
    }

    private static String getSystemProperty(String key) {
        try {
            Class<?> classType = Class.forName("android.os.SystemProperties");
            Method getMethod = classType.getDeclaredMethod("get", String.class);
            return (String) getMethod.invoke(classType, key);
        } catch (Throwable th) {
            th.printStackTrace();
        }
        return null;
    }

    public static String formatTime(String pattern, long milli) {
        int m = (int) (milli / DateUtils.MINUTE_IN_MILLIS);
        int s = (int) ((milli / DateUtils.SECOND_IN_MILLIS) % 60);
        String mm = String.format(Locale.getDefault(), "%02d", m);
        String ss = String.format(Locale.getDefault(), "%02d", s);
        return pattern.replace("mm", mm).replace("ss", ss);
    }

    public static String getDeviceId(Context context)
    {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }


    /**
     * @Description: 鑾峰彇鎵�鏈夌殑瀛樺偍璁惧鍒楄〃
     * @param @return
     * @return ArrayList<String>
     * @throws
     */
    public static ArrayList<String> getStorageDirectoriesArrayList()
    {
        ArrayList<String> list = new ArrayList<String>();
        BufferedReader bufReader = null;
        try
        {
            bufReader = new BufferedReader(new FileReader("/proc/mounts"));
            list.add(Environment.getExternalStorageDirectory().getPath());
            String line;
            while((line = bufReader.readLine()) != null)
            {
                if(line.contains("vfat") || line.contains("exfat") ||
                        line.contains("/mnt") || line.contains("/Removable"))
                {
                    StringTokenizer tokens = new StringTokenizer(line, " ");
                    String s = tokens.nextToken();
                    s = tokens.nextToken(); // Take the second token, i.e. mount point

                    if (list.contains(s))
                        continue;

                    if (line.contains("/dev/block/vold"))
                    {
                        if (!line.startsWith("tmpfs") &&
                                !line.startsWith("/dev/mapper") &&
                                !s.startsWith("/mnt/secure") &&
                                !s.startsWith("/mnt/shell") &&
                                !s.startsWith("/mnt/asec") &&
                                !s.startsWith("/mnt/obb")
                                )
                        {
                            list.add(s);
                        }
                    }
                }
            }
        }
        catch (FileNotFoundException e) {}
        catch (IOException e) {}
        finally
        {
            if (bufReader != null)
            {
                try
                {
                    bufReader.close();
                }
                catch (IOException e) {}
            }
        }
        return list;
    }

    /**
     * @Description: 鍒ゆ柇褰撳墠鐩綍鏄惁鍙敤
     * @param @param file
     * @param @return
     * @return boolean
     * @throws
     */
    public static boolean isStorageAvailable(File file)
    {
        if(getSDspace(file)[1] > 0)
            return true;
        return false;
    }

    /**
     * @Description: 鑾峰彇鏈湴缂撳瓨鐩綍
     * @param @param uniqueName
     * @param @return
     * @return File
     * @throws
     */
    public static File getAvailableDir(Context context, String uniqueName)
    {
		/*鑾峰彇澶栭儴瀛樺偍璁惧鍒楄〃*/
        List<String> sdList = getStorageDirectoriesArrayList();

		/*閫夋嫨绗竴涓湁鏁堢殑瀛樺偍璁惧浣滀负鏈湴缂撳瓨*/
        for(int i=0;i<sdList.size();i++)
        {
            File file = new File(sdList.get(i));
            if(isStorageAvailable(file) && file.canWrite())
            {
                if(!TextUtils.isEmpty(uniqueName))
                    return new File(sdList.get(i) + File.separator + uniqueName);
                else
                    return new File(sdList.get(i) + File.separator);
            }
        }
		/*濡傛灉娌℃湁澶栬灏变娇鐢ㄥ唴閮ㄥ瓨鍌�*/
        return context.getCacheDir();
    }

    /**
     * @Description: 鑾峰彇鏈湴瀛樺偍璁惧瀛樺偍绌洪棿
     * @param @param file
     * @param @return
     * @return long[]
     * @throws
     */
    public static long[] getSDspace(File file)
    {
        StatFs statfs = new StatFs(file.getAbsolutePath());

        long[] result = new long[3];

        long blocSize = statfs.getBlockSize();
        //鑾峰彇BLOCK鏁伴噺
        long totalBlocks = statfs.getBlockCount();
        //宸变娇鐢ㄧ殑Block鐨勬暟閲�
        long availaBlock = statfs.getAvailableBlocks();

        String total = StringUtils.getFormatSize(totalBlocks*blocSize);
        String availale = StringUtils.getFormatSize(availaBlock*blocSize);

        result[0] = blocSize;
        result[1] = totalBlocks;
        result[2] = availaBlock;

        return result;
    }

}
