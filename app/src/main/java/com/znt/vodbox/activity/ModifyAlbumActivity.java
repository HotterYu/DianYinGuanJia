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
import com.znt.vodbox.bean.AlbumInfo;
import com.znt.vodbox.bean.CommonCallBackBean;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.http.HttpCallback;
import com.znt.vodbox.http.HttpClient;
import com.znt.vodbox.utils.binding.Bind;

/**
 * A login screen that offers login via email/password.
 */
public class ModifyAlbumActivity extends BaseActivity{

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
    @Bind(R.id.et_modify_album_remark)
    private EditText etAlbumRemark;

    private View mProgressView;
    private View mLoginFormView;

    private AlbumInfo mAlbumInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_album);

        tvTopTitle.setText(getResources().getString(R.string.add_album));
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
                if(mAlbumInfo == null)
                    addpAlbum();
                else
                    mofifyAlbum(mAlbumInfo);
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        mAlbumInfo = (AlbumInfo) getIntent().getSerializableExtra("ALBUM_INFO");
        if(mAlbumInfo != null)
        {
            tvTopTitle.setText(getResources().getString(R.string.modify_album));
            etAlbumName.setText(mAlbumInfo.getName());
            etAlbumDesc.setText(mAlbumInfo.getDescription());
            etAlbumRemark.setText(mAlbumInfo.getRemark());
        }
    }

    public void addpAlbum()
    {
        String token = Constant.mUserInfo.getToken();
        String name = etAlbumName.getText().toString().trim();

        if(TextUtils.isEmpty(name))
        {
            showToast("请输入歌单名称");
            return;
        }

        String description = etAlbumDesc.getText().toString().trim();

        String merchId = Constant.mUserInfo.getMerchant().getId();
        String remark = etAlbumRemark.getText().toString().trim();


        try
        {
            // Simulate network access.
            HttpClient.addAlbum(token, name,merchId, description, remark, new HttpCallback<CommonCallBackBean>() {
                @Override
                public void onSuccess(CommonCallBackBean resultBean) {

                    if(resultBean != null)
                    {
                        //getGroupList();
                    }
                    else
                    {

                    }

                    showToast(resultBean.getMessage());

                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    intent.putExtras(bundle);
                    setResult(RESULT_OK, intent);
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

    public void mofifyAlbum(AlbumInfo albumInfo)
    {
        String token = Constant.mUserInfo.getToken();
        String name = etAlbumName.getText().toString().trim();

        if(TextUtils.isEmpty(name))
        {
            showToast("请输入歌单名称");
            return;
        }

        String description = etAlbumDesc.getText().toString().trim();

        String merchId = Constant.mUserInfo.getMerchant().getId();
        String remark = etAlbumRemark.getText().toString().trim();
        if(name.equals(albumInfo.getName()) && description.equals(albumInfo.getDescription()) && remark.equals(albumInfo.getRemark()))
        {
            showToast("信息无变化");
            return;
        }

        try
        {
            // Simulate network access.
            HttpClient.modifyAlbum(token, albumInfo.getId(),name,merchId, description, remark, new HttpCallback<CommonCallBackBean>() {
                @Override
                public void onSuccess(CommonCallBackBean resultBean) {

                    if(resultBean != null)
                    {
                        //getGroupList();
                    }
                    else
                    {

                    }

                    showToast(resultBean.getMessage());

                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    intent.putExtras(bundle);
                    setResult(RESULT_OK, intent);
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

