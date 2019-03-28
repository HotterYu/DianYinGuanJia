package com.znt.vodbox.activity;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.znt.vodbox.R;
import com.znt.vodbox.application.MusicApplication;
import com.znt.vodbox.bean.UserCallBackBean;
import com.znt.vodbox.config.Config;
import com.znt.vodbox.db.DBManager;
import com.znt.vodbox.entity.LocalDataEntity;
import com.znt.vodbox.http.HttpCallback;
import com.znt.vodbox.http.HttpClient;
import com.znt.vodbox.model.UserInfo;
import com.znt.vodbox.permission.PermissionHelper;
import com.znt.vodbox.permission.PermissionInterface;
import com.znt.vodbox.utils.ActivityManager;
import com.znt.vodbox.utils.FileUtils;
import com.znt.vodbox.utils.ViewUtils;
import com.znt.vodbox.view.DrawableTextView;
import com.znt.vodbox.view.KeyboardWatcher;
import com.znt.vodbox.view.SplashView;

import static com.znt.vodbox.activity.WelcomeActivity.getVerName;

/**
 * Created by WZH on 2017/3/25.
 */

public class LoginAct extends BaseActivity implements View.OnClickListener, KeyboardWatcher.SoftKeyboardStateListener,PermissionInterface {
    private DrawableTextView logo;
    private EditText et_mobile;
    private EditText et_password;
    private ImageView iv_clean_phone;
    private ImageView clean_password;
    private ImageView iv_show_pwd;
    private Button btn_login;
    private TextView forget_password;
    private TextView tvRegister;
    private TextView tvApply;
    private TextView tvContact;

    private String loginType = "0";//1, 不显示splash

    private PermissionHelper mPermissionHelper;

    private LinearLayout linearLayout = null;
    private SplashView mSplashView = null;
    private View viewSpBg = null;
    private final long finishTime = 2500;

    private int screenHeight = 0;//屏幕高度
    private float scale = 0.8f; //logo缩放比例
    private View service, body;
    private KeyboardWatcher keyboardWatcher;

    private View root;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
        initView();
        initListener();

        UserInfo userInfo = (UserInfo) getIntent().getSerializableExtra("USER_INFO");
        if(userInfo != null)
            loginByRecord(userInfo);
        else
            initDataFromLocal();

        keyboardWatcher = new KeyboardWatcher(findViewById(Window.ID_ANDROID_CONTENT));
        keyboardWatcher.addSoftKeyboardStateListener(this);

        mPermissionHelper = new PermissionHelper(this, this);
        mPermissionHelper.requestPermissions();

        loginType = getIntent().getStringExtra("LoginType");
        if(TextUtils.isEmpty(loginType))
            loginType = "0";
        if(loginType.equals("0"))
        {
            showSplashView();
        }
        else
        {
            viewSpBg.setVisibility(View.GONE);
        }

    }

    private void showSplashView()
    {
        mSplashView = new SplashView(this);
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                mSplashView.splashAndDisappear(new SplashView.ISplashListener()
                {
                    @Override
                    public void onStart()
                    {
                        // log the animation start event

                    }

                    @Override
                    public void onUpdate(float completionFraction)
                    {
                        // log animation update events

                    }

                    @Override
                    public void onEnd()
                    {
                        // log the animation end event

                        // free the view so that it turns into garbage
                        mSplashView = null;
                        hideSplash();
                    }
                });
            }
        }, finishTime);

        mSplashView.setRemoveFromParentOnEnd(true); // remove the SplashView from MainView once animation is completed
        mSplashView.setSplashBackgroundColor(getResources().getColor(R.color.white)); // the background color of the view
        mSplashView.setRotationRadius(getResources().getDimensionPixelOffset(R.dimen.splash_rotation_radius)); // radius of the big circle that the little circles will rotate on
        mSplashView.setCircleRadius(getResources().getDimensionPixelSize(R.dimen.splash_circle_radius)); // radius of each circle
        mSplashView.setRotationDuration(getResources().getInteger(R.integer.splash_rotation_duration)); // time for one rotation to be completed by the small circles
        mSplashView.setSplashDuration(getResources().getInteger(R.integer.splash_duration)); // total time taken for the circles to merge together and disappear
        mSplashView.setCircleColors(getResources().getIntArray(R.array.splash_circle_colors)); // the colors of each circle in order


        linearLayout.addView(mSplashView);
    }

    private void hideSplash()
    {
        linearLayout.setVisibility(View.GONE);
        viewSpBg.setVisibility(View.GONE);
    }

    private void loginByRecord(UserInfo userInfo)
    {

        String account = userInfo.getUsername();
        String pwd = userInfo.getPwd();
        if(TextUtils.isEmpty(account))
            return;
        if(TextUtils.isEmpty(pwd))
        {
            et_mobile.setText(account);
            return;
        }
        login(account, pwd);
    }
    private void initDataFromLocal()
    {
        UserInfo userInfo = getLocalData().getUserInfor();
        String account = userInfo.getUsername();
        String pwd = userInfo.getPwd();

        if(!TextUtils.isEmpty(account) && !TextUtils.isEmpty(pwd))
        {
            login(account, pwd);
        }
    }

    private void fillEditText()
    {
        UserInfo userInfo = getLocalData().getUserInfor();
        String account = userInfo.getUsername();
        String pwd = userInfo.getPwd();
        if(!TextUtils.isEmpty(account))
            et_mobile.setText(account);
        if(!TextUtils.isEmpty(pwd))
            et_password.setText(pwd);
    }

    private void initView() {
        logo = (DrawableTextView) findViewById(R.id.logo);
        et_mobile = (EditText) findViewById(R.id.et_mobile);
        et_password = (EditText) findViewById(R.id.et_password);
        iv_clean_phone = (ImageView) findViewById(R.id.iv_clean_phone);
        clean_password = (ImageView) findViewById(R.id.clean_password);
        iv_show_pwd = (ImageView) findViewById(R.id.iv_show_pwd);
        btn_login = (Button) findViewById(R.id.btn_login);
        forget_password = (TextView) findViewById(R.id.forget_password);
        tvRegister = (TextView) findViewById(R.id.regist);
        tvApply = (TextView) findViewById(R.id.service_apply);
        tvContact = (TextView) findViewById(R.id.contact_service);

        TextView tvVersion = (TextView)findViewById(R.id.tv_splash_version);

        tvVersion.setText(getVerName(this));

        linearLayout = (LinearLayout) findViewById(R.id.view_login_splash);
        viewSpBg = findViewById(R.id.view_login_splash_bg);
        service = findViewById(R.id.service);
        body = findViewById(R.id.body);
        screenHeight = this.getResources().getDisplayMetrics().heightPixels; //获取屏幕高度
        root = findViewById(R.id.root);
        findViewById(R.id.close).setOnClickListener(this);
    }

    private void initListener() {
        iv_clean_phone.setOnClickListener(this);
        clean_password.setOnClickListener(this);
        iv_show_pwd.setOnClickListener(this);
        btn_login.setOnClickListener(this);

        tvRegister.setOnClickListener(this);
        tvApply.setOnClickListener(this);
        tvContact.setOnClickListener(this);

        forget_password.setOnClickListener(this);

        et_mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s) && iv_clean_phone.getVisibility() == View.GONE) {
                    iv_clean_phone.setVisibility(View.VISIBLE);
                } else if (TextUtils.isEmpty(s)) {
                    iv_clean_phone.setVisibility(View.GONE);
                }
            }
        });
        et_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s) && clean_password.getVisibility() == View.GONE) {
                    clean_password.setVisibility(View.VISIBLE);
                } else if (TextUtils.isEmpty(s)) {
                    clean_password.setVisibility(View.GONE);
                }
                if (s.toString().isEmpty())
                    return;
                if (!s.toString().matches("[A-Za-z0-9]+")) {
                    String temp = s.toString();
                    Toast.makeText(LoginAct.this, R.string.please_input_limit_pwd, Toast.LENGTH_SHORT).show();
                    s.delete(temp.length() - 1, temp.length());
                    et_password.setSelection(s.length());
                }
            }
        });
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

    private void login(String account, final String password)
    {

        if (TextUtils.isEmpty(account)) {
            showToast(getString(R.string.error_field_required));
            return ;
        }
        else if (TextUtils.isEmpty(password)) {
            showToast(getString(R.string.error_field_required1));
            return ;
        }

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            showToast(getString(R.string.error_invalid_password));
            return ;
        }

        try
        {
            showProgress("正在登录，请稍后...");
            // Simulate network access.
            HttpClient.userLogin(account, password, new HttpCallback<UserCallBackBean>() {
                @Override
                public void onSuccess(UserCallBackBean tempInfor) {

                    dismissDialog();
                    if(tempInfor.isSuccess())
                    {
                        UserInfo userInfo = tempInfor.getData();
                        LocalDataEntity.newInstance(getActivity()).setUserInfor(userInfo);

                        /*InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);*/

                        if(userInfo != null)
                        {
                            /*LoginRecordInfo tempInfo = new LoginRecordInfo();
                            tempInfo.setAccount(userInfo.getUsername());
                            tempInfo.setNickName(userInfo.getNickName());
                            tempInfo.setPwd(mPasswordView.getText().toString());
                            DBManager.get().getMusicDao().insert(tempInfo);*/
                            ActivityManager.getInstance().finishOthersActivity(LoginAct.class);
                            userInfo.setPwd(password);
                            DBManager.newInstance(getApplicationContext()).insertUser(userInfo);
                            getLocalData().setUserInfor(userInfo);
                            MusicApplication.isLogin = true;
                            Intent intent = new Intent(LoginAct.this, MainActivity.class);
                            Bundle b = new Bundle();
                            b.putSerializable("USER_INFO",userInfo);
                            intent.putExtras(b);
                            startActivity(intent);
                            finish();
                        }
                    }
                    else
                    {
                        fillEditText();
                        showToast(getString(R.string.login_error)+":"+tempInfor.getMessage());
                    }
                }

                @Override
                public void onFail(Exception e) {
                    dismissDialog();
                    showToast(getString(R.string.login_error)+":"+e.getMessage());
                    fillEditText();
                }
            });
        }
        catch (Exception e)
        {
            fillEditText();
            dismissDialog();
            if(e == null)
                showToast(getString(R.string.login_error));
            else
                showToast(getString(R.string.login_error)+":"+e.getMessage());
        }
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 5;
    }

    private void goNewVersion()
    {
        if(isAppInstall(getApplicationContext(),"com.znt.vodbox"))
        {
            //软件已经安装了，直接打开
            openApp();
        }
        else//软件没有安装，读取文件安装
        {
            String apkPath = Environment.getExternalStorageDirectory() +"/StoreSound/";
            Uri mUri = FileUtils.copyAssetsFile(getApplicationContext(),"DianYinGuanJia.apk",apkPath);
            if(mUri != null)
                FileUtils.openApk(mUri,getApplicationContext());
            else
                Toast.makeText(getApplicationContext(),"没有读取到安装包",Toast.LENGTH_SHORT).show();
        }
    }

    private void openApp()
    {
        Intent intent = getPackageManager().getLaunchIntentForPackage("com.znt.vodbox");
        if (intent != null)
        {
            //intent.putExtra("type", "110");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    /**
     * App是否已安装
     * @param mContext
     * @param packageName 包名
     * @return
     */
    private boolean isAppInstall(Context mContext, String packageName){
        PackageInfo mInfo;
        try {
            mInfo = mContext.getPackageManager().getPackageInfo(packageName, 0 );
        } catch (Exception e) {
            // TODO: handle exception
            mInfo = null;
        }
        if(mInfo == null){
            return false;
        }else {
            return true;
        }
    }

    private boolean flag = false;
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_clean_phone:
                et_mobile.setText("");
                break;
            case R.id.clean_password:
                et_password.setText("");
                break;
            case R.id.close:
                finish();
                break;
            case R.id.btn_login:
                String account = et_mobile.getText().toString().trim();
                String password = et_password.getText().toString().trim();
                login(account,password);
                break;
            case R.id.regist:
                goRegisterPage();
                break;
            case R.id.contact_service:
                goContactPage();
                break;
            case R.id.service_apply:
                goApplyPage();
                break;
            case R.id.forget_password:

                goNewVersion();

                /*Uri uri = Uri.parse("http://zhunit-music.oss-cn-shenzhen.aliyuncs.com/apk/DianYinGuanJia.apk");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);*/
                break;
            case R.id.iv_show_pwd:
                if(flag == true){
                    et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    iv_show_pwd.setImageResource(R.drawable.pass_gone);
                    flag = false;
                }else{
                    et_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    iv_show_pwd.setImageResource(R.drawable.pass_visuable);
                    flag = true;
                }
                String pwd = et_password.getText().toString();
                if (!TextUtils.isEmpty(pwd))
                    et_password.setSelection(pwd.length());
                break;
        }
    }

    private void goRegisterPage()
    {
        Bundle b = new Bundle();
        b.putString(Config.Key.ACTIVITY_TITLE,"在线注册");
        b.putString(Config.Key.ACTIVITY_WEB_URL," http://www.zhunit.com/register.html");
        ViewUtils.startActivity(this, WebViewActivity.class,b);
    }

    private void goContactPage()
    {
        Bundle b = new Bundle();
        b.putString(Config.Key.ACTIVITY_TITLE,"联系我们");
        b.putString(Config.Key.ACTIVITY_WEB_URL," http://www.zhunit.com");
        ViewUtils.startActivity(this, WebViewActivity.class,b);
    }

    private void goApplyPage()
    {
        Bundle b = new Bundle();
        b.putString(Config.Key.ACTIVITY_TITLE,"申请试用");
        b.putString(Config.Key.ACTIVITY_WEB_URL,"http://www.zhunit.com/business/merch_buy_box.jsp");
        ViewUtils.startActivity(this, WebViewActivity.class,b);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        keyboardWatcher.removeSoftKeyboardStateListener(this);
    }

    @Override
    public void onSoftKeyboardOpened(int keyboardSize) {
        Log.e("wenzhihao", "----->show" + keyboardSize);
        int[] location = new int[2];
        body.getLocationOnScreen(location); //获取body在屏幕中的坐标,控件左上角
        int x = location[0];
        int y = location[1];
        Log.e("wenzhihao","y = "+y+",x="+x);
        int bottom = screenHeight - (y+body.getHeight()) ;
        Log.e("wenzhihao","bottom = "+bottom);
        Log.e("wenzhihao","con-h = "+body.getHeight());
        if (keyboardSize > bottom){
            ObjectAnimator mAnimatorTranslateY = ObjectAnimator.ofFloat(body, "translationY", 0.0f, -(keyboardSize - bottom));
            mAnimatorTranslateY.setDuration(300);
            mAnimatorTranslateY.setInterpolator(new AccelerateDecelerateInterpolator());
            mAnimatorTranslateY.start();
            zoomIn(logo, keyboardSize - bottom);

        }
    }

    @Override
    public void onSoftKeyboardClosed() {
        Log.e("wenzhihao", "----->hide");
        ObjectAnimator mAnimatorTranslateY = ObjectAnimator.ofFloat(body, "translationY", body.getTranslationY(), 0);
        mAnimatorTranslateY.setDuration(300);
        mAnimatorTranslateY.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimatorTranslateY.start();
        zoomOut(logo);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(mPermissionHelper.requestPermissionsResult(requestCode, permissions, grantResults)){
            //权限请求结果，并已经处理了该回调
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public int getPermissionsRequestCode() {
        return 10000;
    }

    @Override
    public String[] getPermissions() {
        return new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION

        };
    }
    @Override
    public void requestPermissionsSuccess()
    {
        //权限请求用户已经全部允许
        try
        {

        }
        catch (Exception e)
        {
            if(e == null)
                showToast("初始化失败");
            else
                showToast("初始化失败："+e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void requestPermissionsFail() {
        //权限请求不被用户允许。可以提示并退出或者提示权限的用途并重新发起权限申请。

        mPermissionHelper.requestPermissions();
        //
    }

}
