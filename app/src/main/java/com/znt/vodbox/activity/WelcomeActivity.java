package com.znt.vodbox.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.vodbox.application.MusicApplication;
import com.znt.vodbox.bean.UserCallBackBean;
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

    TextView tvVersion;
    View logoView = null;
    private static final int ANIM_TIME = 3000;

    private static final float SCALE_END = 1.15F;
    private float scale = 0.6f; //logo缩放比例

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

        logoView = findViewById(R.id.view_splash_logo);
        tvVersion = (TextView)findViewById(R.id.tv_splash_version);

        tvVersion.setText(getVerName(this));
        //ButterKnife.bind(this);
        startMainActivity();
    }
    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }
    private void startMainActivity(){

        Random random = new Random(SystemClock.elapsedRealtime());//SystemClock.elapsedRealtime() 从开机到现在的毫秒数（手机睡眠(sleep)的时间也包括在内）
        Observable.timer(3000, TimeUnit.MILLISECONDS)
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
                    goLoginPage();
                }

            }
        });
    }

    private void goLoginPage()
    {
        //startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
        startActivity(new Intent(WelcomeActivity.this, LoginAct.class));
        WelcomeActivity.this.finish();
    }

    /**
     * 缩小
     *
     * @param view
     */
    public void zoomIn(final View view, float dist) {
        view.setPivotY(view.getHeight());
        view.setPivotX(view.getWidth() / 2);
        AnimatorSet mAnimatorSet = new AnimatorSet();
        ObjectAnimator mAnimatorScaleX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, scale);
        ObjectAnimator mAnimatorScaleY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, scale);
        ObjectAnimator mAnimatorTranslateY = ObjectAnimator.ofFloat(view, "translationY", 0.0f, -dist);

        mAnimatorSet.play(mAnimatorTranslateY).with(mAnimatorScaleX).with(mAnimatorScaleY);

        mAnimatorSet.setDuration(300);
        mAnimatorSet.start();

    }

    /**
     * f放大
     *
     * @param view
     */
    public void zoomOut(final View view) {
        if (view.getTranslationY()==0){
            return;
        }
        view.setPivotY(view.getHeight());
        view.setPivotX(view.getWidth() / 2);
        AnimatorSet mAnimatorSet = new AnimatorSet();

        ObjectAnimator mAnimatorScaleX = ObjectAnimator.ofFloat(view, "scaleX", scale, 1.0f);
        ObjectAnimator mAnimatorScaleY = ObjectAnimator.ofFloat(view, "scaleY", scale, 1.0f);
        ObjectAnimator mAnimatorTranslateY = ObjectAnimator.ofFloat(view, "translationY", view.getTranslationY(), 0);

        mAnimatorSet.play(mAnimatorTranslateY).with(mAnimatorScaleX).with(mAnimatorScaleY);
        mAnimatorSet.setDuration(300);
        mAnimatorSet.start();

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
        final String password = userInfo.getPwd();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
            return false;

        try
        {
            // Simulate network access.
            HttpClient.userLogin(email, password, new HttpCallback<UserCallBackBean>() {
                @Override
                public void onSuccess(UserCallBackBean tempInfor) {

                    if(tempInfor.isSuccess())
                    {
                        UserInfo userInfo = tempInfor.getData();
                        userInfo.setPwd(password);

                        LocalDataEntity.newInstance(getApplicationContext()).setUserInfor(userInfo);

                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

                        if(userInfo != null)
                        {
                            MusicApplication.isLogin = true;
                            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                            Bundle b = new Bundle();
                            b.putSerializable("USER_INFO",userInfo);
                            intent.putExtras(b);
                            startActivity(intent);
                            finish();
                        }
                    }
                    else
                    {
                        goLoginPage();
                    }
                }

                @Override
                public void onFail(Exception e) {
                    goLoginPage();
                }
            });
        }
        catch (Exception e)
        {
            goLoginPage();
        }

        return true;
    }
}
