package com.znt.vodbox.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.znt.vodbox.R;
import com.znt.vodbox.application.MusicApplication;
import com.znt.vodbox.bean.UserCallBackBean;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.entity.LocalDataEntity;
import com.znt.vodbox.http.HttpCallback;
import com.znt.vodbox.http.HttpClient;
import com.znt.vodbox.model.UserInfo;
import com.znt.vodbox.utils.SharedPreferencesUtil;
import com.znt.vodbox.utils.binding.Bind;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class WelcomeActivity extends Activity {

    @Bind(R.id.iv_entry)
    ImageView mIVEntry;

    private static final int ANIM_TIME = 3000;

    private static final float SCALE_END = 1.15F;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 判断是否是第一次开启应用
        boolean isFirstOpen = SharedPreferencesUtil.getBoolean(this, SharedPreferencesUtil.FIRST_OPEN, true);
        // 如果是第一次启动，则先进入功能引导页
        /*if (isFirstOpen) {
            Intent intent = new Intent(this, WelcomeGuideActivity.class);
            startActivity(intent);
            finish();
            return;
        }*/

        // 如果不是第一次启动app，则正常显示启动屏
        setContentView(R.layout.activity_welcome);
        //ButterKnife.bind(this);
        startMainActivity();
    }
    private void startMainActivity(){
        Random random = new Random(SystemClock.elapsedRealtime());//SystemClock.elapsedRealtime() 从开机到现在的毫秒数（手机睡眠(sleep)的时间也包括在内）
        Observable.timer(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>()
                {
                    @Override
                    public void call(Long aLong)
                    {
                        startAnim();
                    }
                });
    }

    private void startAnim() {

        ObjectAnimator animatorX = ObjectAnimator.ofFloat(mIVEntry, "scaleX", 0.6f, SCALE_END);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(mIVEntry, "scaleY", 0.6f, SCALE_END);

        AnimatorSet set = new AnimatorSet();
        set.setDuration(ANIM_TIME).play(animatorX).with(animatorY);
        set.start();

        final boolean result = attemptLogin();

        set.addListener(new AnimatorListenerAdapter()
        {

            @Override
            public void onAnimationEnd(Animator animation)
            {
                /*if(MusicApplication.isLogin)
                {
                    startActivity(new Intent(WelcomeActivity.this, MusicActivity.class));
                }
                else*/
                if(!result)
                {
                    startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                    WelcomeActivity.this.finish();
                }

            }
        });
    }

    /**
     * 屏蔽物理返回按钮
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean attemptLogin() {

        UserInfo userInfo = LocalDataEntity.newInstance(getApplicationContext()).getUserInfor();

        // Store values at the time of the login attempt.
        String email = userInfo.getUsername();
        String password = userInfo.getPwd();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
            return false;

        try
        {
            // Simulate network access.
            HttpClient.userLogin(email, password, new HttpCallback<UserCallBackBean>() {
                @Override
                public void onSuccess(UserCallBackBean tempInfor) {

                    UserInfo userInfo = tempInfor.getData();
                    userInfo.setPwd(password);
                    Constant.mUserInfo = userInfo;

                    LocalDataEntity.newInstance(getApplicationContext()).setUserInfor(userInfo);

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

                    if(userInfo != null)
                    {
                        MusicApplication.isLogin = true;
                        Intent intent = new Intent(WelcomeActivity.this, MusicActivity.class);
                        Bundle b = new Bundle();
                        b.putSerializable("USER_INFO",userInfo);
                        intent.putExtras(b);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        //showProgress(false);
                        startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                    }
                }

                @Override
                public void onFail(Exception e) {
                    startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                }
            });
        }
        catch (Exception e)
        {
            startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
        }

        return true;
    }
}
