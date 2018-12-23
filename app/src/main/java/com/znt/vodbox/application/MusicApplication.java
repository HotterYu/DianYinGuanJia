package com.znt.vodbox.application;

import android.content.Context;
import android.text.TextUtils;

import com.qihoo360.replugin.RePluginApplication;
import com.tencent.bugly.crashreport.CrashReport;
import com.znt.vodbox.storage.db.DBManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


/**
 * 自定义Application
 * Created by wcy on 2015/11/27.
 */
public class MusicApplication extends RePluginApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        AppCache.get().init(this);
        ForegroundObserver.init(this);
        DBManager.get().init(this);


        /* Bugly SDK初始化
        * 参数1：上下文对象
        * 参数2：APPID，平台注册时得到,注意替换成你的appId
        * 参数3：是否开启调试模式，调试模式下会输出'CrashReport'tag的日志
        */
        CrashReport.initCrashReport(getApplicationContext(), "6207095b94", true);

        final String processName = getProcessName(android.os.Process.myPid());
        initBugly(processName);

    }

    public static  boolean isLogin = false;

    /**
     * Bugly初始化
     */
    private void initBugly(String processName) {

        Context context = getApplicationContext();
        // 获取当前包名
        String packageName = context.getPackageName();
        // 设置是否为上报进程
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setUploadProcess(processName == null || processName.equals(packageName));
        // 初始化Bugly；第三个参数为SDK调试模式开关,debug模式下打开，release模式下关闭
        CrashReport.initCrashReport(context, "faed430f12", true);
    }

    /**
     * 获取进程号对应的进程名;bugly判断使用，只有主进程中才上报bugly；避免多进程下多次上报消耗资源
     *
     * @param pid 进程号
     * @return 进程名
     */
    public String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }
}
