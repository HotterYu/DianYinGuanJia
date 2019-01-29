package com.znt.vodbox.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import com.znt.vodbox.R;
import com.znt.vodbox.application.MusicApplication;
import com.znt.vodbox.bean.UserCallBackBean;
import com.znt.vodbox.config.Config;
import com.znt.vodbox.db.DBManager;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.http.HttpCallback;
import com.znt.vodbox.http.HttpClient;
import com.znt.vodbox.model.UserInfo;
import com.znt.vodbox.utils.ActivityManager;
import com.znt.vodbox.utils.ViewUtils;
import com.znt.vodbox.view.DrawableTextView;
import com.znt.vodbox.view.KeyboardWatcher;

/**
 * Created by WZH on 2017/3/25.
 */

public class LoginAct extends BaseActivity implements View.OnClickListener, KeyboardWatcher.SoftKeyboardStateListener {
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

    }

    private void loginByRecord(UserInfo userInfo)
    {

        String account = userInfo.getUsername();
        String pwd = userInfo.getPwd();
        if(!TextUtils.isEmpty(account) && !TextUtils.isEmpty(pwd))
        {
            login(account, pwd);
        }
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
                        Constant.mUserInfo = userInfo;
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

}
