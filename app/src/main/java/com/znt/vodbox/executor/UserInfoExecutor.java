package com.znt.vodbox.executor;

import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.znt.vodbox.R;
import com.znt.vodbox.entity.LocalDataEntity;
import com.znt.vodbox.model.UserInfo;
import com.znt.vodbox.utils.binding.Bind;
import com.znt.vodbox.utils.binding.ViewBinder;


public class UserInfoExecutor{
        //implements IExecutor{
    private static final String TAG = "WeatherExecutor";
    private Context mContext;
    @Bind(R.id.ll_weather)
    private LinearLayout llWeather;
    @Bind(R.id.iv_weather_icon)
    private ImageView ivIcon;
    @Bind(R.id.tv_user_info_name)
    private TextView tvTemp;
    @Bind(R.id.tv_user_info_type)
    private TextView tvCity;
    @Bind(R.id.tv_user_info_code)
    private TextView tvWind;

    public UserInfoExecutor(Context context, View navigationHeader) {
        mContext = context.getApplicationContext();
        ViewBinder.bind(this, navigationHeader);

    }

    public void showUserInfo()
    {
        final UserInfo userInfo = LocalDataEntity.newInstance(mContext).getUserInfor();
        if(userInfo != null)
        {
            tvTemp.setText(userInfo.getNickName());
            tvCity.setText( userInfo.getTypeName());
            if(userInfo.getMerchant() != null)
                tvWind.setText("激活码：" + userInfo.getMerchant().getBindCode());
        }
        tvWind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                // 将文本内容放到系统剪贴板里。
                cm.setText(userInfo.getMerchant().getBindCode());
                Toast.makeText(mContext,"激活码复制成功",Toast.LENGTH_LONG).show();
            }
        });
    }


}
