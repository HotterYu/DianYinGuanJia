package com.znt.vodbox.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.vodbox.bean.CommonCallBackBean;
import com.znt.vodbox.bean.ShopInfoCallBackBean;
import com.znt.vodbox.entity.LocalDataEntity;
import com.znt.vodbox.http.HttpCallback;
import com.znt.vodbox.http.HttpClient;
import com.znt.vodbox.model.Shopinfo;
import com.znt.vodbox.utils.ViewUtils;
import com.znt.vodbox.utils.binding.Bind;

public class WifiSetActivity  extends BaseActivity{

    @Bind(R.id.tv_common_title)
    private TextView tvTopTitle = null;
    @Bind(R.id.iv_common_back)
    private ImageView ivTopReturn = null;
    @Bind(R.id.iv_common_more)
    private ImageView ivTopMore = null;
    @Bind(R.id.tv_common_confirm)
    private TextView tvTopTopRight = null;

    // UI references.
    @Bind(R.id.et_modify_album_name)
    private AutoCompleteTextView etAlbumName;
    @Bind(R.id.et_modify_album_description)
    private EditText etAlbumDesc;


    private View mProgressView;
    private View mLoginFormView;

    private Shopinfo mShopinfo = null;

    private int checkWifiCount = 0;
    private final int CHECK_TIME = 5000;
    private final int MSG_WIFI_CHECK = 1;
    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == MSG_WIFI_CHECK)
            {
                removeMessages(MSG_WIFI_CHECK);
                if(checkWifiCount <= 36)
                {
                    getTerminalInfo();
                    sendEmptyMessageDelayed(MSG_WIFI_CHECK, CHECK_TIME);
                    checkWifiCount ++;
                }
                else
                {
                    checkWifiCount = 0;
                    showToast("配置失败，请检查要配置的wifi是否正常");
                    dismissDialog();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_setting);

        tvTopTitle.setText(getResources().getString(R.string.dev_wifi_set));
        ivTopMore.setVisibility(View.GONE);
        tvTopTopRight.setVisibility(View.VISIBLE);
        tvTopTopRight.setText("选择");
        ivTopReturn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvTopTopRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewUtils.startActivity(getActivity(),WifiListActivity.class,null,1);
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                updateShopInfo();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        mShopinfo = (Shopinfo) getIntent().getSerializableExtra("DEVICE_INFO");
        if(mShopinfo != null)
        {
            etAlbumName.setText(mShopinfo.getWifiName());
            etAlbumDesc.setText(mShopinfo.getWifiPassword());
        }
    }

    public void updateShopInfo()
    {
        String token = LocalDataEntity.newInstance(getActivity()).getUserInfor().getToken();
        String wifiName = etAlbumName.getText().toString().trim();

        if(TextUtils.isEmpty(wifiName))
        {
            showToast("请输入WIFI名称");
            return;
        }

        mShopinfo.setWifiName(wifiName);
        mShopinfo.setWifiPassword(etAlbumDesc.getText().toString());

        try
        {
            HttpClient.updateShopInfo(token, mShopinfo, new HttpCallback<CommonCallBackBean>() {
                @Override
                public void onSuccess(CommonCallBackBean resultBean) {

                    if(resultBean != null && resultBean.isSuccess())
                    {
                        showProgressDialog(getActivity(),"正在配置WIFI，请稍后...");
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ViewUtils.sendMessage(mHandler,MSG_WIFI_CHECK);
                            }
                        }, CHECK_TIME);
                    }
                    else
                    {
                        if(resultBean == null)
                            showToast("发送wifi信息失败");
                        else
                            showToast(resultBean.getMessage());
                    }
                }

                @Override
                public void onFail(Exception e) {
                    showToast(e.getMessage());
                }
            });
        }
        catch (Exception e)
        {
            showToast(e.getMessage());
            Log.e("",e.getMessage());
        }
    }

    private void configSuccess()
    {
        dismissDialog();
        showToast("配置成功");
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("DeviceInfor",mShopinfo);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void getTerminalInfo()
    {
        try
        {
            String id = mShopinfo.getTmlRunStatus().get(0).getTerminalId();
            String token = LocalDataEntity.newInstance(getActivity()).getUserInfor().getToken();

            HttpClient.getShopInfo(token, id, new HttpCallback<ShopInfoCallBackBean>() {
                @Override
                public void onSuccess(ShopInfoCallBackBean resultBean) {
                    if(resultBean != null && resultBean.isSuccess())
                    {
                        mShopinfo = resultBean.getData();
                        String wifiUpdateCode = mShopinfo.getTmlRunStatus().get(0).getWifiUpdateCode();
                        if(wifiUpdateCode != null && wifiUpdateCode.equals("1000"))
                        {
                            //wifi开始配置了
                            //dismissDialog();
                            showToast("终端收到WIFI配置请求,正在配置...");
                        }
                        else if(wifiUpdateCode != null && wifiUpdateCode.equals("1001"))
                        {
                            //wifi配置失败
                            dismissDialog();
                            showToast("wifi配置失败");
                        }
                        else if(wifiUpdateCode != null && wifiUpdateCode.equals("1002"))
                        {
                            //wifi配置成功
                            configSuccess();
                        }
                        else if(wifiUpdateCode != null && wifiUpdateCode.equals("1003"))
                        {
                            //wifi开关打开失败
                            dismissDialog();
                            showToast("WIFI开关打开失败");
                        }

                    }
                    else
                    {
                        showToast(resultBean.getMessage());
                    }
                }
                @Override
                public void onFail(Exception e) {
                    if(e != null)
                        showToast(e.getMessage());
                }
            });
        }
        catch (Exception e)
        {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK)
            return;
        if(requestCode == 1)
        {
            String name = data.getStringExtra("WIFI_NAME");
            if(name != null)
                etAlbumName.setText(name);
        }
    }
}
