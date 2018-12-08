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

    private boolean isFragmentVisible;
    private boolean isReuseView;
    private boolean isFirstVisible;
    private View rootView;

    //setUserVisibleHint()在Fragment创建时会先被调用一次，传入isVisibleToUser = false
    //如果当前Fragment可见，那么setUserVisibleHint()会再次被调用一次，传入isVisibleToUser = true
    //如果Fragment从可见->不可见，那么setUserVisibleHint()也会被调用，传入isVisibleToUser = false
    //总结：setUserVisibleHint()除了Fragment的可见状态发生变化时会被回调外，在new Fragment()时也会被回调
    //如果我们需要在 Fragment 可见与不可见时干点事，用这个的话就会有多余的回调了，那么就需要重新封装一个
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //setUserVisibleHint()有可能在fragment的生命周期外被调用
        if (rootView == null) {
            return;
        }
        if (isFirstVisible && isVisibleToUser) {
            onFragmentFirstVisible();
            isFirstVisible = false;
        }
        if (isVisibleToUser) {
            onFragmentVisibleChange(true);
            isFragmentVisible = true;
            return;
        }
        if (isFragmentVisible) {
            isFragmentVisible = false;
            onFragmentVisibleChange(false);
        }
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVariable();
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //如果setUserVisibleHint()在rootView创建前调用时，那么
        //就等到rootView创建完后才回调onFragmentVisibleChange(true)
        //保证onFragmentVisibleChange()的回调发生在rootView创建完成之后，以便支持ui操作
        if (rootView == null) {
            rootView = view;
            if (getUserVisibleHint()) {
                if (isFirstVisible) {
                    onFragmentFirstVisible();
                    isFirstVisible = false;
                }
                onFragmentVisibleChange(true);
                isFragmentVisible = true;
            }
        }
        super.onViewCreated(isReuseView ? rootView : view, savedInstanceState);
    }

    private void initVariable() {
        isFirstVisible = true;
        isFragmentVisible = false;
        rootView = null;
        isReuseView = true;
    }
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
        initVariable();
        super.onDestroy();
    }

    /**
     * 去除setUserVisibleHint()多余的回调场景，保证只有当fragment可见状态发生变化时才回调
     * 回调时机在view创建完后，所以支持ui操作，解决在setUserVisibleHint()里进行ui操作有可能报null异常的问题
     *
     * 可在该回调方法里进行一些ui显示与隐藏，比如加载框的显示和隐藏
     *
     * @param isVisible true  不可见 -> 可见
     *                  false 可见  -> 不可见
     */
    protected void onFragmentVisibleChange(boolean isVisible) {

    }

    /**
     * 在fragment首次可见时回调，可在这里进行加载数据，保证只在第一次打开Fragment时才会加载数据，
     * 这样就可以防止每次进入都重复加载数据
     * 该方法会在 onFragmentVisibleChange() 之前调用，所以第一次打开时，可以用一个全局变量表示数据下载状态，
     * 然后在该方法内将状态设置为下载状态，接着去执行下载的任务
     * 最后在 onFragmentVisibleChange() 里根据数据下载状态来控制下载进度ui控件的显示与隐藏
     */
    protected void onFragmentFirstVisible() {

    }

    protected boolean isFragmentVisible() {
        return isFragmentVisible;
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
