package com.znt.vodbox.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.tencent.bugly.crashreport.CrashReport;
import com.znt.vodbox.exception.MyExceptionHandler;
import com.znt.vodbox.storage.db.DBManager;
import com.znt.vodbox.utils.ActivityManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


/**
 * 自定义Application
 * Created by wcy on 2015/11/27.
 */
public class MusicApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        MyExceptionHandler excetionHandler = MyExceptionHandler.getInstance();
        excetionHandler.init(getApplicationContext());

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

        this.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                ActivityManager.getInstance().addActivity(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                ActivityManager.getInstance().removeActivity(activity);
            }
        });

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
