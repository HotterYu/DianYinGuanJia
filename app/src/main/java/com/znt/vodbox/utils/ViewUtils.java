package com.znt.vodbox.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;

import com.znt.vodbox.enums.LoadStateEnum;


/**
 * 视图工具类
 * Created by hzwangchenyan on 2016/1/14.
 */
public class ViewUtils {
    public static void changeViewState(View success, View loading, View fail, LoadStateEnum state) {
        switch (state) {
            case LOADING:
                success.setVisibility(View.GONE);
                loading.setVisibility(View.VISIBLE);
                fail.setVisibility(View.GONE);
                break;
            case LOAD_SUCCESS:
                success.setVisibility(View.VISIBLE);
                loading.setVisibility(View.GONE);
                fail.setVisibility(View.GONE);
                break;
            case LOAD_FAIL:
                success.setVisibility(View.GONE);
                loading.setVisibility(View.GONE);
                fail.setVisibility(View.VISIBLE);
                break;
        }
    }

    public static void startActivity(Activity context, Class<?> cls, Bundle bundle)
    {
        Intent intent = new Intent();
        intent.setClass(context, cls);
        if(bundle != null)
            intent.putExtras(bundle);
        context.startActivity(intent);
    }
    public static void startActivity(Activity context, Class<?> cls, Bundle bundle, int requestCode)
    {
        Intent intent = new Intent();
        intent.setClass(context, cls);
        if(bundle != null)
            intent.putExtras(bundle);
        context.startActivityForResult(intent, requestCode);
    }
    public static void startActivity(Context context, Class<?> cls, Bundle bundle)
    {
        Intent intent = new Intent();
        intent.setClass(context, cls);
        if(bundle != null)
            intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static void startCall(Activity context, String telNum)
    {
        if(!TextUtils.isEmpty(telNum))
        {
            Uri uri = Uri.parse("tel:" + telNum);
            Intent intent = new Intent(Intent.ACTION_DIAL, uri);
            context.startActivity(intent);

        }
    }
    public static void startWebView(Activity context, String webUrl)
    {
        if(!TextUtils.isEmpty(webUrl))
        {
            Uri uri = Uri.parse(webUrl);
            Intent it = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(it);

        }
    }
    public static void startMessage(Activity context, String telNum)
    {
        if(!TextUtils.isEmpty(telNum))
        {
            Uri uri = Uri.parse("smsto:" + telNum);
            Intent intent = new Intent(Intent.ACTION_SENDTO,uri);
            context.startActivity(intent);
        }
    }
    public static void startNetWorkSet(Context context)
    {
        Intent intentToNetwork = new Intent(Settings.ACTION_WIFI_SETTINGS);
        //Intent intentToNetwork = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        /*
        intentToNetwork.setAction("android.intent.action.VIEW");  */
        context.startActivity(intentToNetwork);
    }

    /**
     * @Description: 璺宠浆鍒板叾浠朼pp鎸囧畾椤甸潰
     * @param @param context
     * @param @param appPkg
     * @param @param absActivityName
     * @return void
     * @throws
     */
    public static void startAppActivity(Context context, String appPkg, String absActivityName)
    {
        // 杩欎釜鏄彟澶栦竴涓簲鐢ㄧ▼搴忕殑鍖呭悕   杩欎釜鍙傛暟鏄鍚姩鐨凙ctivity
        ComponentName componetName = new ComponentName(appPkg, absActivityName);
        //ComponentName componetName = new ComponentName(appPkg, absActivityName);
        Intent intent = new Intent();
        intent.setComponent(componetName);
        context.startActivity(intent);

        //闇�瑕佸湪閰嶇疆鏂囦欢涓activity鍋氬涓嬮厤缃�
        /*<activity
        android:name="com.neldtv.activity.HelpActivity">
        <intent-filter>
            <action android:name="android.intent.action.VIEW" />
            <category android:name="android.intent.category.DEFAULT" />
            <category android:name="android.intent.category.BROWSABLE" />
        </intent-filter>*/
    }


    /**
     * @Description: activity璺宠浆
     * @param @param context
     * @param @param bundle
     * @return void
     * @throws
     */
    public static void startActivity(Activity context, Bundle bundle)
    {
        Intent intent = new Intent();
        intent.putExtras(bundle);
        context.setResult(Activity.RESULT_OK, intent);
        context.finish();
    }

    public static void sendMessage(Handler handler, int what, Object obj)
    {
        if(handler == null)
            return;
        Message msg = new Message();
        if(obj != null)
            msg.obj = obj;
        msg.what = what;
        if(handler != null)
            handler.sendMessage(msg);
    }
    public static void sendMessage(Handler handler, int what)
    {
        if(handler != null)
            handler.sendEmptyMessage(what);
    }
}
