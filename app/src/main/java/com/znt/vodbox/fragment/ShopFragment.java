package com.znt.vodbox.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.znt.vodbox.R;
import com.znt.vodbox.activity.GroupListActivity;
import com.znt.vodbox.activity.MusicActivity;
import com.znt.vodbox.activity.SearchSystemMusicActivity;
import com.znt.vodbox.activity.ShopDetailActivity;
import com.znt.vodbox.adapter.LoadMoreWrapper;
import com.znt.vodbox.adapter.OnMoreClickListener;
import com.znt.vodbox.adapter.ShopLoadMoreAdapter;
import com.znt.vodbox.bean.CommonCallBackBean;
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

import java.util.ArrayList;
import java.util.List;



public class ShopFragment extends BaseFragment implements OnMoreClickListener {
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
    private int onlinesize = 0;
    private int expiredsize = 0;
    private String onlinestatus = "";

    private OnShopCounUpdateListener mOnShopCounUpdateListener = null;
    public interface OnShopCounUpdateListener
    {
        void onShopCounUpdate(int all, int online, int offline,int expire);
    }

    public void setOnShopCounUpdateListener(OnShopCounUpdateListener mOnShopCounUpdateListener)
    {
        this.mOnShopCounUpdateListener = mOnShopCounUpdateListener;
    }

    public void setOnlinestatus(String onlinestatus)
    {
        this.onlinestatus = onlinestatus;
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
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.main_bg, R.color.colorPrimaryDark);

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
        fab.setVisibility(View.GONE);

        // 设置下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadMoreWrapper.showFooterView(false);
                // 刷新数据
                /*if(dataList != null && dataList.size() > 0)
                    dataList.clear();*/
                pageNo = 1;
                getData();
            }
        });

        // 设置加载更多监听
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                loadMoreWrapper.setLoadState(loadMoreWrapper.LOADING);

                if (dataList.size() < maxSize)
                {
                    getData();
                }
                else
                {
                    // 显示加载到底的提示
                    loadMoreWrapper.setLoadState(loadMoreWrapper.LOADING_END);
                }
            }
        });

        getData();

    }

    @Override
    protected void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();

    }

    public void setOnCountGetCallBack(MusicActivity.OnCountGetCallBack mOnCountGetCallBack)
    {
        this.mOnCountGetCallBack = mOnCountGetCallBack;

    }
    public void getData()
    {
        if(swipeRefreshLayout == null)
            return;

        if(pageNo == 1)
        {
            swipeRefreshLayout.setRefreshing(true);
            loadMoreWrapper.setLoadState(loadMoreWrapper.LOADING_COMPLETE);
        }
        else
        {
            swipeRefreshLayout.setRefreshing(false);
            loadMoreWrapper.setLoadState(loadMoreWrapper.LOADING_END);
        }

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

        try
        {
            // Simulate network access.
            HttpClient.getAllShops(token, pageNo + "", pageSize
                            + "",merchId,groupId,memberId,name,shopCode,userShopCode,"","",onlinestatus
                    , new HttpCallback<ShopListResultBean>() {
                @Override
                public void onSuccess(ShopListResultBean resultBean) {

                    if(resultBean.getResultcode().equals("1"))
                    {

                        int lastSize = dataList.size();

                        List<Shopinfo> tempList = resultBean.getData();

                        if(pageNo == 1)
                            dataList.clear();

                        if(tempList.size() <= pageSize)
                            pageNo ++;

                        dataList.addAll(tempList);

                        loadMoreWrapper.notifyItemChanged(lastSize,dataList.size());

                        maxSize = Integer.parseInt(resultBean.getMessage());

                        if(resultBean.getExtendData() != null )
                        {
                            if(!TextUtils.isEmpty(resultBean.getExtendData().getOnlinesize()))
                                onlinesize = Integer.parseInt(resultBean.getExtendData().getOnlinesize());
                            if(!TextUtils.isEmpty(resultBean.getExtendData().getExpiredsize()))
                                expiredsize = Integer.parseInt(resultBean.getExtendData().getExpiredsize());
                            if(mOnShopCounUpdateListener != null)
                            {
                                mOnShopCounUpdateListener.onShopCounUpdate(maxSize,onlinesize,maxSize-onlinesize,expiredsize);
                            }
                        }

                        if(mOnCountGetCallBack != null)
                            mOnCountGetCallBack.onCountGetBack(resultBean.getMessage());

                        maxSize = Integer.parseInt(resultBean.getMessage());


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

    private void refreshUi()
    {
        if(loadMoreWrapper != null)
            loadMoreWrapper.notifyDataSetChanged();
        if(swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
        if(loadMoreWrapper != null)
            loadMoreWrapper.setLoadState(loadMoreWrapper.LOADING_END);
    }
    @Override
    public void onMoreClick(final int position) {
        Shopinfo tempShop = dataList.get(position);
        showShopOperationDialog(position,tempShop);
    }

    private void showShopOperationDialog(final int pos, final Shopinfo tempShop)
    {
        mAlertView = new AlertView(tempShop.getName(),tempShop.getAddress(), "取消", null,
                new String[]{"添加到分区", "插播歌曲", "删除店铺"},
                getActivity(), AlertView.Style.ActionSheet, new OnItemClickListener(){
            public void onItemClick(Object o,int position){
                switch (position) {
                    case 0://
                        if(tempShop.getTmlRunStatus() == null || tempShop.getTmlRunStatus().size() == 0)
                        {
                            showToast("该店铺下没有安装设备");
                            return;
                        }
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("IS_EDIT", true);
                        if(tempShop.getGroup() != null)
                            bundle.putString("GROUP_ID",tempShop.getGroup().getId());
                        bundle.putString("SHOP_IDS",tempShop.getId());
                        ViewUtils.startActivity(getActivity(),GroupListActivity.class,bundle,1);
                        break;
                    case 1://
                        if(tempShop.getTmlRunStatus() == null || tempShop.getTmlRunStatus().size() == 0)
                        {
                            showToast("该店铺下没有安装设备");
                            return;
                        }
                        Bundle bundle2 = new Bundle();
                        bundle2.putSerializable("SHOP_INFO",tempShop);
                        ViewUtils.startActivity(getActivity(),SearchSystemMusicActivity.class,bundle2);
                        break;
                    case 2://
                        deleteShop(pos,tempShop.getId());
                        break;
                }
            }
        });
        mAlertView.show();
    }
    private AlertView mAlertView = null;
    private void deleteShop(final int pos,final String id)
    {
        if(mAlertView != null)
            mAlertView.dismissImmediately();
        new AlertView("提示", "确定删除该店铺吗?", "取消", new String[]{"确定"}, null, getActivity(), AlertView.Style.Alert, new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {
                if(position == 0)
                    deleteShopProces(pos, id);
            }
        }).setCancelable(true).show();
    }
    public void deleteShopProces(final int pos, final String id)
    {
        try
        {
            String token = LocalDataEntity.newInstance(getActivity()).getUserInfor().getToken();

            HttpClient.deleteShop(token, id, new HttpCallback<CommonCallBackBean>() {
                @Override
                public void onSuccess(CommonCallBackBean resultBean) {
                    if(resultBean != null && resultBean.isSuccess())
                    {
                        dataList.remove(pos);
                        loadMoreWrapper.notifyDataSetChanged();
                        dismissDialog();
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

        }
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
