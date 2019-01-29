package com.znt.vodbox.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.znt.vodbox.R;
import com.znt.vodbox.dialog.MusicPlayDialog;
import com.znt.vodbox.dialog.MyAlertDialog;
import com.znt.vodbox.dialog.MyProgressDialog;
import com.znt.vodbox.entity.LocalDataEntity;
import com.znt.vodbox.service.PlayService;
import com.znt.vodbox.storage.preference.Preferences;
import com.znt.vodbox.utils.PermissionReq;
import com.znt.vodbox.utils.binding.ViewBinder;
import com.znt.vodbox.view.MyToast;


/**
 * 基类<br>
 * 如果继承本类，需要在 layout 中添加 {@link Toolbar} ，并将 AppTheme 继承 Theme.AppCompat.NoActionBar 。
 * Created by wcy on 2015/11/26.
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected Handler handler;
    protected PlayService playService;
    private ServiceConnection serviceConnection;
    private ProgressDialog progressDialog;
    private LocalDataEntity localData = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Preferences.isNightMode()) {
            setTheme(getDarkTheme());
        }

        super.onCreate(savedInstanceState);

        setSystemBarTransparent();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        handler = new Handler(Looper.getMainLooper());
        bindService();
        localData = new LocalDataEntity(getActivity());

    }

    public LocalDataEntity getLocalData()
    {
        return localData;
    }

    public void closeAllActivity()
    {
        MyActivityManager.getScreenManager().popAllActivityExceptionOne(null);
    }

    public Activity getActivity()
    {
        return this;
    }

    private MyToast myToast = null;
    public void showToast(int res)
    {
        if(myToast == null)
            myToast = new MyToast(getApplicationContext());
        myToast.show(res);
    }
    public void showToast(String res)
    {
        if(myToast == null)
            myToast = new MyToast(getApplicationContext());
        myToast.show(res);
    }
    @StyleRes
    protected int getDarkTheme() {
        return R.style.AppThemeDark;
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initView();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        initView();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        initView();
    }

    private void initView() {
        ViewBinder.bind(this);

        Toolbar mToolbar = findViewById(R.id.toolbar);
        if (mToolbar == null) {
            throw new IllegalStateException("Layout is required to include a Toolbar with id 'toolbar'");
        }
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void bindService() {
        Intent intent = new Intent();
        intent.setClass(this, PlayService.class);
        serviceConnection = new PlayServiceConnection();
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private class PlayServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            playService = ((PlayService.PlayBinder) service).getService();
            onServiceBound();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(getClass().getSimpleName(), "service disconnected");
        }
    }

    protected void onServiceBound() {
    }

    private void setSystemBarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // LOLLIPOP解决方案
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // KITKAT解决方案
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showProgress() {
        showProgress(getString(R.string.loading));
    }

    public void showProgress(String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
        }
        progressDialog.setMessage(message);
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    public void cancelProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.cancel();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionReq.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        if (serviceConnection != null) {
            unbindService(serviceConnection);
        }
        super.onDestroy();
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
    public void showPlayDialog(String mediaName, String mediaUrl, String mediaId, View.OnClickListener listener)
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
        playDialog.getRightButton().setOnClickListener(listener);
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
        if (progressDialog != null && progressDialog.isShowing())
        {
            progressDialog.dismiss();
            progressDialog = null;
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
