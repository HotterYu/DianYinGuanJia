package com.znt.vodbox.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.znt.vodbox.R;
import com.znt.vodbox.activity.AddShopActivity;
import com.znt.vodbox.activity.GroupListActivity;
import com.znt.vodbox.activity.MusicActivity;
import com.znt.vodbox.activity.ShopDetailActivity;
import com.znt.vodbox.adapter.LoadMoreWrapper;
import com.znt.vodbox.adapter.OnMoreClickListener;
import com.znt.vodbox.adapter.ShopLoadMoreAdapter;
import com.znt.vodbox.adapter.ShoplistAdapter;
import com.znt.vodbox.bean.ShopListResultBean;
import com.znt.vodbox.constants.RequestCode;
import com.znt.vodbox.entity.LocalDataEntity;
import com.znt.vodbox.http.HttpCallback;
import com.znt.vodbox.http.HttpClient;
import com.znt.vodbox.model.Shopinfo;
import com.znt.vodbox.model.UserInfo;
import com.znt.vodbox.utils.ToastUtils;
import com.znt.vodbox.utils.ViewUtils;
import com.znt.vodbox.utils.binding.Bind;
import com.znt.vodbox.view.StaggeredGridRecyclerView;
import com.znt.vodbox.view.listener.EndlessRecyclerOnScrollListener;
import com.znt.vodbox.view.xlistview.LJListView;

import java.util.ArrayList;
import java.util.List;


public class OnlineShopFragment extends BaseFragment implements OnMoreClickListener {
    @Bind(R.id.rv)
    private StaggeredGridRecyclerView mRecyclerView;
    @Bind(R.id.refresh)
    private SwipeRefreshLayout swipeRefreshLayout;

    @Bind(R.id.fab)
    FloatingActionButton fab = null;

    private LoadMoreWrapper loadMoreWrapper;

    private List<Shopinfo> dataList = new ArrayList<>();

    private UserInfo mUserInfo = null;

    private MusicActivity.OnCountGetCallBack mOnCountGetCallBack = null;

    private int pageNo = 1;
    private int pageSize = 25;
    private int maxSize = 0;

    private static OnlineShopFragment INSTANCE = null;
    public static OnlineShopFragment newInstance()
    {
        if(INSTANCE == null)
            INSTANCE = new OnlineShopFragment();
        return INSTANCE;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_shop, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 设置下拉进度的背景颜色，默认就是白色的
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        // 设置下拉进度的主题颜色
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);

        ShopLoadMoreAdapter shopLoadMoreAdapter = new ShopLoadMoreAdapter(getContext(), dataList);
        loadMoreWrapper = new LoadMoreWrapper(shopLoadMoreAdapter);
        shopLoadMoreAdapter.setOnMoreClickListener(this);
        shopLoadMoreAdapter.setOnItemClickListener(new ShopLoadMoreAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                int index = (int) view.getTag();
                Shopinfo tempShop = dataList.get(index);
                if(tempShop.getTmlRunStatus() != null
                        && tempShop.getTmlRunStatus().size() >0
                        && tempShop.getTmlRunStatus().get(0).getOnlineStatus() != null)
                {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("SHOP_INFO",tempShop);
                    ViewUtils.startActivity(getActivity(),ShopDetailActivity.class,bundle);
                }
                else
                    showToast(getResources().getString(R.string.no_device_hint));
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(loadMoreWrapper);

        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ViewUtils.startActivity(getActivity(),AddShopActivity.class,null,1);
            }
        });


        // 设置下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadMoreWrapper.showFooterView(false);
                // 刷新数据
                if(dataList != null && dataList.size() > 0)
                    dataList.clear();
                pageNo = 1;
                getData();
            }
        });

        // 设置加载更多监听
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                loadMoreWrapper.setLoadState(loadMoreWrapper.LOADING);

                if (dataList.size() < 52)
                {

                }
                else
                {
                    // 显示加载到底的提示
                    loadMoreWrapper.setLoadState(loadMoreWrapper.LOADING_END);
                }
            }
        });

        swipeRefreshLayout.setRefreshing(true);
        getData();

    }

    public void setOnCountGetCallBack(MusicActivity.OnCountGetCallBack mOnCountGetCallBack)
    {
        this.mOnCountGetCallBack = mOnCountGetCallBack;

    }

    public void setUserInfo(UserInfo mUserInfo)
    {
        this.mUserInfo = mUserInfo;

    }

    private void refreshUi()
    {
        if(loadMoreWrapper != null)
            loadMoreWrapper.notifyDataSetChanged();
        if(swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
        if(loadMoreWrapper != null)
            loadMoreWrapper.setLoadState(loadMoreWrapper.LOADING_END);
    }

    public void getData()
    {
        if(mUserInfo == null)
            mUserInfo = LocalDataEntity.newInstance(getContext()).getUserInfor();

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

                    if(resultBean.getResultcode().equals("1"))
                    {
                        List<Shopinfo> tempList = resultBean.getData();

                        if(pageNo == 1)
                            dataList.clear();

                        if(tempList.size() <= pageSize)
                            pageNo ++;

                        dataList.addAll(tempList);
                        if(maxSize == 0)
                            maxSize = Integer.parseInt(resultBean.getMessage());

                        if(mOnCountGetCallBack != null)
                            mOnCountGetCallBack.onCountGetBack(resultBean.getMessage());
                        loadMoreWrapper.notifyDataSetChanged();
                    }
                    else
                    {
                        showToast(resultBean.getMessage());
                    }
                    refreshUi();
                }

                @Override
                public void onFail(Exception e) {
                    refreshUi();
                    if(e != null)
                        showToast(e.getMessage());
                    else
                        showToast("加载数据失败");
                }
            });
        }
        catch (Exception e)
        {
            refreshUi();
            if(e != null)
                showToast(e.getMessage());
            else
                showToast("加载数据失败");
        }
    }

    @Override
    public void onMoreClick(final int position) {
        Shopinfo tempShop = dataList.get(position);
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
}
