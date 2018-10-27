package com.znt.vodbox.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.hwangjr.rxbus.RxBus;
import com.znt.vodbox.R;
import com.znt.vodbox.dialog.MusicPlayDialog;
import com.znt.vodbox.dialog.MyAlertDialog;
import com.znt.vodbox.dialog.MyProgressDialog;
import com.znt.vodbox.utils.PermissionReq;
import com.znt.vodbox.utils.binding.ViewBinder;
import com.znt.vodbox.view.MyToast;


/**
 * 基类<br>
 * Created by wcy on 2015/11/26.
 */
public abstract class BaseFragment extends Fragment {
    protected Handler handler;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        handler = new Handler(Looper.getMainLooper());
        ViewBinder.bind(this, getView());
        RxBus.get().register(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        setListener();
    }

    protected void setListener() {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionReq.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onDestroy() {
        RxBus.get().unregister(this);
        super.onDestroy();
    }

    private MyToast myToast = null;
    public void showToast(int res)
    {
        if(myToast == null)
            myToast = new MyToast(getActivity());
        myToast.show(res);
    }
    public void showToast(String res)
    {
        if(myToast == null)
            myToast = new MyToast(getActivity());
        myToast.show(res);
    }



    public void showPlayDialog(String mediaName, String mediaUrl,String mediaId)
    {
        final MusicPlayDialog playDialog = new MusicPlayDialog(getActivity(), R.style.CustomDialog);

        playDialog.setInfor(mediaName, mediaUrl, mediaId);
        //playDialog.updateProgress("00:02:18 / 00:05:12");
        if(playDialog.isShowing())
            playDialog.dismiss();
        playDialog.show();

        WindowManager windowManager = ((Activity) getActivity()).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = playDialog.getWindow().getAttributes();
        lp.width = (int)(display.getWidth()); //设置宽度
        lp.height = (int)(display.getHeight()); //设置高度
        playDialog.getWindow().setAttributes(lp);
    }


    private MyProgressDialog mProgressDialog;
    private MyAlertDialog myAlertDialog = null;
    public final void showProgressDialog(Activity activity,
                                         String message)
    {
        while (activity.getParent() != null)
        {
            activity = activity.getParent();
        }

        if (TextUtils.isEmpty(message))
        {
            message = "正在处理...";
        }
        if(mProgressDialog == null)
            mProgressDialog = new MyProgressDialog(activity, R.style.CustomDialog);
        mProgressDialog.setInfor(message);

        if(!mProgressDialog.isShowing())
        {
            mProgressDialog.show();
            WindowManager windowManager = (activity).getWindowManager();
            Display display = windowManager.getDefaultDisplay();
            WindowManager.LayoutParams lp = mProgressDialog.getWindow().getAttributes();
            lp.width = (int)(display.getWidth()); //璁剧疆瀹藉害
            lp.height = (int)(display.getHeight()); //璁剧疆楂樺害
            mProgressDialog.getWindow().setAttributes(lp);
        }
    }
    public final void showProgressDialog(Activity activity,
                                         String message, boolean isBackEnable)
    {
        while (activity.getParent() != null)
        {
            activity = activity.getParent();
        }

        if (TextUtils.isEmpty(message))
        {
            message = "正在处理...";
        }
        if(mProgressDialog == null)
            mProgressDialog = new MyProgressDialog(activity, R.style.CustomDialog);
        mProgressDialog.setInfor(message);
        mProgressDialog.setBackEnable(isBackEnable);
        if(!mProgressDialog.isShowing())
        {
            mProgressDialog.show();
            WindowManager windowManager = (activity).getWindowManager();
            Display display = windowManager.getDefaultDisplay();
            WindowManager.LayoutParams lp = mProgressDialog.getWindow().getAttributes();
            lp.width = (int)(display.getWidth()); //璁剧疆瀹藉害
            lp.height = (int)(display.getHeight()); //璁剧疆楂樺害
            mProgressDialog.getWindow().setAttributes(lp);
        }
    }

    public final void showAlertDialog(Activity activity, View.OnClickListener listener, String title,
                                      String message)
    {
        if (TextUtils.isEmpty(title))
        {
            title = "提示";
        }

        while (activity.getParent() != null)
        {
            activity = activity.getParent();
        }

        if(myAlertDialog == null || myAlertDialog.isDismissed())
            myAlertDialog = new MyAlertDialog(activity, R.style.CustomDialog);
        myAlertDialog.setInfor(title, message);
        if(myAlertDialog.isShowing())
            myAlertDialog.dismiss();
        myAlertDialog.show();
        myAlertDialog.setOnClickListener(listener);

        WindowManager windowManager = ((Activity) activity).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = myAlertDialog.getWindow().getAttributes();
        lp.width = (int)(display.getWidth()); //璁剧疆瀹藉害
        lp.height = (int)(display.getHeight()); //璁剧疆楂樺害
        myAlertDialog.getWindow().setAttributes(lp);
    }

    public final void showAlertDialog(Activity activity, String title,
                                      String message)
    {

        while (activity.getParent() != null)
        {
            activity = activity.getParent();
        }

        if (TextUtils.isEmpty(title))
        {
            title = "提示";
        }


        if(myAlertDialog == null || myAlertDialog.isDismissed())
            myAlertDialog = new MyAlertDialog(activity, R.style.CustomDialog);
        myAlertDialog.setInfor(title, message);
        if(myAlertDialog.isShowing())
            myAlertDialog.dismiss();
        myAlertDialog.show();
        WindowManager windowManager = ((Activity) activity).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = myAlertDialog.getWindow().getAttributes();
        lp.width = (int)(display.getWidth()); //璁剧疆瀹藉害
        lp.height = (int)(display.getHeight()); //璁剧疆楂樺害
        myAlertDialog.getWindow().setAttributes(lp);
    }

    public final void dismissDialog()
    {
        if(getActivity() == null || getActivity().isFinishing())
            return;
        if (mProgressDialog != null && mProgressDialog.isShowing())
        {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
        if (myAlertDialog != null && myAlertDialog.isShowing())
        {
            myAlertDialog.dismiss();
            myAlertDialog = null;
        }
    }
    public final void showProgressDialog(Context context,
                                         String message, DialogInterface.OnDismissListener listener)
    {
        if (TextUtils.isEmpty(message))
        {
            message = "正在处理...";
        }
        if(mProgressDialog == null)
        {
            mProgressDialog = new MyProgressDialog(context, R.style.CustomDialog);
            mProgressDialog.setOnDismissListener(listener);
        }
        mProgressDialog.setInfor(message);

        if(!mProgressDialog.isShowing())
        {
            mProgressDialog.show();
            WindowManager windowManager = ((Activity) context).getWindowManager();
            Display display = windowManager.getDefaultDisplay();
            WindowManager.LayoutParams lp = mProgressDialog.getWindow().getAttributes();
            lp.width = (int)(display.getWidth()); //璁剧疆瀹藉害
            lp.height = (int)(display.getHeight()); //璁剧疆楂樺害
            mProgressDialog.getWindow().setAttributes(lp);
        }
    }

}
