package com.znt.vodbox.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.vodbox.adapter.AlbumMusiclistAdapter;
import com.znt.vodbox.adapter.ShoplistAdapter;
import com.znt.vodbox.bean.CommonCallBackBean;
import com.znt.vodbox.bean.MediaInfo;
import com.znt.vodbox.bean.MusicListResultBean;
import com.znt.vodbox.bean.ShopListResultBean;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.http.HttpCallback;
import com.znt.vodbox.http.HttpClient;
import com.znt.vodbox.model.Shopinfo;
import com.znt.vodbox.utils.ToastUtils;
import com.znt.vodbox.utils.binding.Bind;
import com.znt.vodbox.view.xlistview.LJListView;

import java.util.ArrayList;
import java.util.List;

public class ShopSelectActivity extends BaseActivity  implements
        LJListView.IXListViewListener, AdapterView.OnItemClickListener{

    @Bind(R.id.tv_common_title)
    private TextView tvTopTitle = null;
    @Bind(R.id.iv_common_back)
    private ImageView ivTopReturn = null;
    @Bind(R.id.iv_common_more)
    private ImageView ivTopMore = null;

    @Bind(R.id.ptrl_shop_select)
    private LJListView listView = null;

    private String mediaName = "";
    private String mediaUrl = "";
    private String mediaId = "";

    private List<Shopinfo> shopinfoList = new ArrayList<>();

    private ShoplistAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_select);

        tvTopTitle.setText(getResources().getString(R.string.shop_select));
        ivTopMore.setVisibility(View.GONE);
        ivTopReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mediaName = getIntent().getStringExtra("MEDIA_NAME");
        mediaUrl = getIntent().getStringExtra("MEDIA_URL");
        mediaId = getIntent().getStringExtra("MEDIA_ID");

        listView.getListView().setDivider(getResources().getDrawable(R.color.transparent));
        listView.getListView().setDividerHeight(1);
        listView.setPullLoadEnable(true,"");
        listView.setPullRefreshEnable(true);
        listView.setIsAnimation(true);
        listView.setXListViewListener(this);
        listView.showFootView(false);
        listView.setRefreshTime();
        listView.setOnItemClickListener(this);

        adapter = new ShoplistAdapter(shopinfoList);
        listView.setAdapter(adapter);

        listView.onFresh();

    }

    public void loadShops()
    {
        String token = Constant.mUserInfo.getToken();
        String pageNo = "1";
        String pageSize = "100";
        String merchId = "";
        //String merchId = mUserInfo.getMerchant().getId();
        String groupId = "";
        String memberId = "";
        String name = "";
        String shopCode = "";
        String userShopCode = "";

        try
        {
            // Simulate network access.
            HttpClient.getAllShops(token, pageNo, pageSize,merchId,groupId,memberId,name,shopCode,userShopCode
                    , new HttpCallback<ShopListResultBean>() {
                        @Override
                        public void onSuccess(ShopListResultBean resultBean) {
                            if(resultBean != null)
                            {
                                shopinfoList = resultBean.getData();

                                adapter.notifyDataSetChanged(shopinfoList);
                                tvTopTitle.setText(getResources().getString(R.string.shop_select) + "(" + resultBean.getMessage() +")");
                            }
                            else
                            {
                                showToast(resultBean.getMessage());
                            }
                            listView.stopRefresh();
                        }

                        @Override
                        public void onFail(Exception e) {
                            listView.stopRefresh();
                        }
                    });
        }
        catch (Exception e)
        {
            listView.stopRefresh();
        }

    }

    private void pushMedia(String terminId)
    {
        String type = "1";
        String dataId = mediaId;
        String userId = Constant.mUserInfo.getMerchant().getId();
        String pusherid = "";
        String pushername = "Hotter Test";

        try
        {
            // Simulate network access.
            HttpClient.pushMedia(terminId, type, dataId, userId,pusherid,pushername, new HttpCallback<CommonCallBackBean>() {
                @Override
                public void onSuccess(CommonCallBackBean resultBean) {

                    if(resultBean != null)
                    {
                        if(resultBean.getResultcode().equals("0"))
                        {
                            ToastUtils.show(getResources().getString(R.string.push_fail)+":"+resultBean.getMessage());
                        }
                        else
                            ToastUtils.show(getResources().getString(R.string.push_success));

                    }
                    else
                    {
                        ToastUtils.show(getResources().getString(R.string.push_fail));
                    }

                }

                @Override
                public void onFail(Exception e) {
                    ToastUtils.show(getResources().getString(R.string.push_fail));
                }
            });
        }
        catch (Exception e)
        {
            ToastUtils.show(getResources().getString(R.string.push_fail));
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(position > 0)
            position = position - 1;

        Shopinfo tempInfor = shopinfoList.get(position);
        pushMedia(tempInfor.getId());
        /*Intent intent = new Intent();
        Bundle b = new Bundle();
        b.putSerializable("SHOP_INFO", tempInfor);

        setIntent(intent);
        setResult(RESULT_OK);
        finish();*/

    }

    @Override
    public void onRefresh() {
        loadShops();
    }

    @Override
    public void onLoadMore() {
        loadShops();
    }
}
