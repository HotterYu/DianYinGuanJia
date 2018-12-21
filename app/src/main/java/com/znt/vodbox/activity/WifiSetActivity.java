package com.znt.vodbox.activity;

import android.content.Intent;
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
        String token = Constant.mUserInfo.getToken();
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
            // Simulate network access.
            HttpClient.updateShopInfo(token, mShopinfo, new HttpCallback<CommonCallBackBean>() {
                @Override
                public void onSuccess(CommonCallBackBean resultBean) {

                    if(resultBean.isSuccess())
                    {
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("DeviceInfor",mShopinfo);
                        intent.putExtras(bundle);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                    else
                    {

                    }
                    showToast(resultBean.getMessage());
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
