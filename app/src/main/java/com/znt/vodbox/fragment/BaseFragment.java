package com.znt.vodbox.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.WindowManager;

import com.hwangjr.rxbus.RxBus;
import com.znt.vodbox.R;
import com.znt.vodbox.dialog.MusicPlayDialog;
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
}
