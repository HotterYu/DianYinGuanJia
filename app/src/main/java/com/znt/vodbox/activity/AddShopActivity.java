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
import com.znt.vodbox.utils.binding.Bind;

/**
 * A login screen that offers login via email/password.
 */
public class AddShopActivity extends BaseActivity{

    @Bind(R.id.tv_common_title)
    private TextView tvTopTitle = null;
    @Bind(R.id.iv_common_back)
    private ImageView ivTopReturn = null;
    @Bind(R.id.iv_common_more)
    private ImageView ivTopMore = null;

    // UI references.
    @Bind(R.id.et_add_shop_id)
    private AutoCompleteTextView etShopId;
    @Bind(R.id.et_add_shop_name)
    private EditText etShopName;
    @Bind(R.id.et_add_shop_code)
    private EditText etShopCode;

    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shop);

        tvTopTitle.setText(getResources().getString(R.string.add_shop));
        ivTopMore.setVisibility(View.GONE);
        ivTopReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                addpShop();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    public void addpShop()
    {
        String token = Constant.mUserInfo.getToken();
        String terminalId = etShopId.getText().toString().trim();

        if(TextUtils.isEmpty(terminalId))
        {
            showToast("请输入设备编号");
            return;
        }

        String shopName = etShopName.getText().toString().trim();

        if(TextUtils.isEmpty(shopName))
        {
            showToast("请输入店铺名称");
            return;
        }

        String merchId = Constant.mUserInfo.getMerchant().getId();
        String shopCode = etShopCode.getText().toString().trim();


        try
        {
            // Simulate network access.
            HttpClient.addShop(token, terminalId, shopName, merchId, shopCode, new HttpCallback<CommonCallBackBean>() {
                @Override
                public void onSuccess(CommonCallBackBean resultBean) {

                    if(resultBean != null && resultBean.isSuccess())
                    {
                        showToast(resultBean.getMessage());

                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        intent.putExtras(bundle);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                    else
                    {
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




}

