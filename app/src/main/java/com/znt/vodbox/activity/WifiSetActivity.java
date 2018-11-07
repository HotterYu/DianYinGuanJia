package com.znt.vodbox.activity;

import android.os.Bundle;
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
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.http.HttpCallback;
import com.znt.vodbox.http.HttpClient;
import com.znt.vodbox.model.Shopinfo;
import com.znt.vodbox.utils.binding.Bind;

public class WifiSetActivity  extends BaseActivity{

    @Bind(R.id.tv_common_title)
    private TextView tvTopTitle = null;
    @Bind(R.id.iv_common_back)
    private ImageView ivTopReturn = null;
    @Bind(R.id.iv_common_more)
    private ImageView ivTopMore = null;

    // UI references.
    @Bind(R.id.et_modify_album_name)
    private AutoCompleteTextView etAlbumName;
    @Bind(R.id.et_modify_album_description)
    private EditText etAlbumDesc;


    private View mProgressView;
    private View mLoginFormView;

    private Shopinfo mShopinfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_setting);

        tvTopTitle.setText(getResources().getString(R.string.dev_wifi_set));
        ivTopMore.setVisibility(View.GONE);
        ivTopReturn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
        String token = Constant.mUserInfo.getToken();
        String wifiName = etAlbumName.getText().toString().trim();

        if(TextUtils.isEmpty(wifiName))
        {
            showToast("请输入WIFI名称");
            return;
        }

        String id = "";
        String userShopCode = "";
        String name = "";
        String tel = "";
        String linkman = "";
        String linkmanPhone = "";
        String wifiPassword = "";
        String country = "";
        String province = "";
        String city = "";
        String region = "";
        String address = "";
        String longitude = "";
        String latitude = "";
        String groupId = "";
        try
        {
            // Simulate network access.
            HttpClient.updateShopInfo(token, id,userShopCode, name, tel
                    , linkman, linkmanPhone,wifiName, wifiPassword, country, province, city, region, address, longitude
                    , latitude, groupId, new HttpCallback<CommonCallBackBean>() {
                @Override
                public void onSuccess(CommonCallBackBean resultBean) {

                    if(resultBean.isSuccess())
                    {
                        //getGroupList();
                    }
                    else
                    {

                    }

                    showToast(resultBean.getMessage());

                    /*Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    intent.putExtras(bundle);
                    setResult(RESULT_OK, intent);*/
                    finish();

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
}
