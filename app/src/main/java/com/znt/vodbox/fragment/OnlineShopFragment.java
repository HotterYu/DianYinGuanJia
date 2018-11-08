package com.znt.vodbox.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.znt.vodbox.R;
import com.znt.vodbox.activity.AddShopActivity;
import com.znt.vodbox.activity.GroupListActivity;
import com.znt.vodbox.activity.MusicActivity;
import com.znt.vodbox.activity.ShopDetailActivity;
import com.znt.vodbox.adapter.OnMoreClickListener;
import com.znt.vodbox.adapter.ShoplistAdapter;
import com.znt.vodbox.bean.ShopListResultBean;
import com.znt.vodbox.constants.RequestCode;
import com.znt.vodbox.http.HttpCallback;
import com.znt.vodbox.http.HttpClient;
import com.znt.vodbox.model.Shopinfo;
import com.znt.vodbox.model.UserInfo;
import com.znt.vodbox.utils.ToastUtils;
import com.znt.vodbox.utils.ViewUtils;
import com.znt.vodbox.utils.binding.Bind;
import com.znt.vodbox.view.xlistview.LJListView;

import java.util.ArrayList;
import java.util.List;


public class OnlineShopFragment extends BaseFragment implements LJListView.IXListViewListener,AdapterView.OnItemClickListener, OnMoreClickListener {
    @Bind(R.id.lv_all_shops)
    private LJListView listView;
    @Bind(R.id.fab)
    FloatingActionButton fab = null;
    private List<Shopinfo> shopinfoList = new ArrayList<>();

    private ShoplistAdapter adapter;

    private UserInfo mUserInfo = null;

    private MusicActivity.OnCountGetCallBack mOnCountGetCallBack = null;

    private int pageNo = 1;
    private int pageSize = 25;
    private int maxSize = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_shop, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new ShoplistAdapter(getActivity(),shopinfoList);
        adapter.setOnMoreClickListener(this);

        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ViewUtils.startActivity(getActivity(),AddShopActivity.class,null,1);
            }
        });

        listView.getListView().setDivider(getResources().getDrawable(R.color.transparent));
        listView.getListView().setDividerHeight(1);
        listView.setPullLoadEnable(true,"");
        listView.setPullRefreshEnable(true);
        listView.setIsAnimation(true);
        listView.setXListViewListener(this);
        listView.showFootView(false);
        listView.setRefreshTime();
        listView.setOnItemClickListener(this);

        listView.setAdapter(adapter);

    }

    public void setOnCountGetCallBack(MusicActivity.OnCountGetCallBack mOnCountGetCallBack)
    {
        this.mOnCountGetCallBack = mOnCountGetCallBack;

    }

    public void setUserInfo(UserInfo mUserInfo)
    {
        this.mUserInfo = mUserInfo;
        listView.onFresh();
    }

    public void loadShops()
    {

        String token = mUserInfo.getToken();

        String merchId = "";
        //String merchId = mUserInfo.getMerchant().getId();
        String groupId = "";
        String memberId = "";
        String name = "";
        String shopCode = "";
        String userShopCode = "";
        String onlinestatus = "1";

        try
        {
            // Simulate network access.
            HttpClient.getAllShops(token, pageNo+"", pageSize+"",merchId,groupId,memberId,name,shopCode,userShopCode,onlinestatus
                    , new HttpCallback<ShopListResultBean>() {
                @Override
                public void onSuccess(ShopListResultBean resultBean) {
                    listView.stopRefresh();
                    if(resultBean.getResultcode().equals("1"))
                    {
                        List<Shopinfo> tempList = resultBean.getData();

                        if(pageNo == 1)
                            shopinfoList.clear();

                        if(tempList.size() <= pageSize)
                            pageNo ++;

                        shopinfoList.addAll(tempList);
                        if(maxSize == 0)
                            maxSize = Integer.parseInt(resultBean.getMessage());

                        if(mOnCountGetCallBack != null)
                            mOnCountGetCallBack.onCountGetBack(resultBean.getMessage());
                        adapter.notifyDataSetChanged(shopinfoList);
                    }
                    else
                    {
                        showToast(resultBean.getMessage());
                    }
                }

                @Override
                public void onFail(Exception e) {
                    listView.stopRefresh();
                    if(e != null)
                        showToast(e.getMessage());
                    else
                        showToast("加载数据失败");
                }
            });
        }
        catch (Exception e)
        {
            listView.stopRefresh();
            showToast(e.getMessage());
        }

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if(position > 0)
            position = position - 1;
        Shopinfo tempShop = shopinfoList.get(position);
        Bundle bundle = new Bundle();
        /*bundle.putBoolean("IS_EDIT", true);
        if(tempShop.getGroup() != null)
            bundle.putString("GROUP_ID",tempShop.getGroup().getId());
        bundle.putString("SHOP_IDS",tempShop.getId());*/
        bundle.putSerializable("SHOP_INFO",tempShop);
        ViewUtils.startActivity(getActivity(),ShopDetailActivity.class,bundle);
    }

    @Override
    public void onMoreClick(final int position) {
        Shopinfo tempShop = shopinfoList.get(position);
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle(tempShop.getName());
        dialog.setItems(R.array.shop_list_dialog, (dialog1, which) -> {
            switch (which) {
                case 0://
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("IS_EDIT", true);
                    if(tempShop.getGroup() != null)
                        bundle.putString("GROUP_ID",tempShop.getGroup().getId());
                    bundle.putString("SHOP_IDS",tempShop.getId());
                    ViewUtils.startActivity(getActivity(),GroupListActivity.class,bundle,1);
                    break;
                case 1://
                    //requestSetRingtone(music);
                    break;
                case 2://
                    Bundle bundle1 = new Bundle();
                    bundle1.putSerializable("SHOP_INFO",tempShop);
                    ViewUtils.startActivity(getActivity(),ShopDetailActivity.class,bundle1);
                    break;
            }
        });
        dialog.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.REQUEST_WRITE_SETTINGS) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.System.canWrite(getContext())) {
                ToastUtils.show(R.string.grant_permission_setting);
            }
        }
    }



    @Override
    public void onRefresh() {
        pageNo = 1;
        loadShops();
    }

    @Override
    public void onLoadMore() {
        if(maxSize > shopinfoList.size())
            loadShops();
        else
            showToast("没有更多数据了");
    }
}
