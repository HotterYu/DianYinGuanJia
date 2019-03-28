package com.znt.vodbox.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.znt.vodbox.R;
import com.znt.vodbox.activity.AdPlanDetailActivity;
import com.znt.vodbox.adapter.AdPlanLoadMoreAdapter;
import com.znt.vodbox.adapter.LoadMoreWrapper;
import com.znt.vodbox.adapter.OnMoreClickListener;
import com.znt.vodbox.bean.AdPlanInfo;
import com.znt.vodbox.bean.AdPlanListResultBean;
import com.znt.vodbox.bean.CommonCallBackBean;
import com.znt.vodbox.entity.LocalDataEntity;
import com.znt.vodbox.http.HttpCallback;
import com.znt.vodbox.http.HttpClient;
import com.znt.vodbox.utils.binding.Bind;
import com.znt.vodbox.view.StaggeredGridRecyclerView;
import com.znt.vodbox.view.listener.EndlessRecyclerOnScrollListener;

import java.util.ArrayList;
import java.util.List;


public class AdPlanFragment extends BaseFragment implements  OnMoreClickListener {
    @Bind(R.id.rv)
    private StaggeredGridRecyclerView mRecyclerView;
    @Bind(R.id.refresh)
    private SwipeRefreshLayout swipeRefreshLayout;

    private LoadMoreWrapper loadMoreWrapper;

    private List<AdPlanInfo> dataList = new ArrayList<>();

    private int pageNo = 1;
    private int pageSize = 25;
    private int maxSize = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_plan_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        // 设置下拉进度的背景颜色，默认就是白色的
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        // 设置下拉进度的主题颜色
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.main_bg, R.color.colorPrimaryDark);

        AdPlanLoadMoreAdapter shopLoadMoreAdapter = new AdPlanLoadMoreAdapter(getActivity(), dataList);
        loadMoreWrapper = new LoadMoreWrapper(shopLoadMoreAdapter);
        shopLoadMoreAdapter.setOnMoreClickListener(this);
        shopLoadMoreAdapter.setOnItemClickListener(new AdPlanLoadMoreAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                int index = (int) view.getTag();
                AdPlanInfo tempInfo = dataList.get(index);

                Intent intent = new Intent(getActivity(), AdPlanDetailActivity.class);
                Bundle b = new Bundle();
                b.putSerializable("AD_PLAN_INFO",tempInfo);
                intent.putExtras(b);
                startActivityForResult(intent, 2);
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(loadMoreWrapper);

        // 设置下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadMoreWrapper.showFooterView(false);

                pageNo = 1;
                refreshData();
            }
        });

        // 设置加载更多监听
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                loadMoreWrapper.setLoadState(loadMoreWrapper.LOADING);

                if (dataList.size() < maxSize)
                {
                    refreshData();

                }
                else
                {
                    // 显示加载到底的提示
                    loadMoreWrapper.setLoadState(loadMoreWrapper.LOADING_END);
                }
            }
        });
        refreshData();

    }

    public void refreshData()
    {
        getAdPlanList();
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

    public void getAdPlanList()
    {
        String token = LocalDataEntity.newInstance(getActivity()).getUserInfor().getToken();

        String id = "";//计划id
        String merchId = LocalDataEntity.newInstance(getActivity()).getUserInfor().getMerchant().getId();
        String groupId = "";
        //String merchId = mUserInfo.getMerchant().getId();
        String typeId = "";
        String planName = "";

        try
        {
            // Simulate network access.
            HttpClient.getAdPlanList(token,pageNo+"", pageSize+"",id,merchId,groupId,planName, new HttpCallback<AdPlanListResultBean>() {
                @Override
                public void onSuccess(AdPlanListResultBean resultBean) {
                    refreshUi();
                    if(resultBean != null)
                    {
                        int lastSize = dataList.size();
                        if(pageNo == 1)
                            dataList.clear();
                        List<AdPlanInfo> tempList = resultBean.getData();
                        dataList.addAll(tempList);
                        maxSize = Integer.parseInt(resultBean.getMessage());
                        loadMoreWrapper.notifyItemChanged(lastSize,dataList.size());

                    }
                    else
                    {
                        //shopinfoList.clear();
                    }

                }

                @Override
                public void onFail(Exception e) {
                    refreshUi();
                }
            });
        }
        catch (Exception e)
        {
            refreshUi();
        }
    }

    public void deleteAdPlan(AdPlanInfo info)
    {
        String token = LocalDataEntity.newInstance(getActivity()).getUserInfor().getToken();

        try
        {
            // Simulate network access.
            HttpClient.deleteAdPlan(token,info.getId(),new HttpCallback<CommonCallBackBean>() {
                @Override
                public void onSuccess(CommonCallBackBean resultBean) {
                    if(resultBean != null)
                    {
                        getAdPlanList();
                    }
                    else
                    {
                        //shopinfoList.clear();
                    }
                    showToast(resultBean.getMessage());
                }

                @Override
                public void onFail(Exception e) {

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
        if(resultCode != getActivity().RESULT_OK)
            return;
        if(requestCode == 2)
        {
            refreshData();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMoreClick(int position) {
        final AdPlanInfo tempInfo = dataList.get(position);
        showPlanOperationDialog(tempInfo);
    }

    private AlertView tempAlertView = null;
    private void showPlanOperationDialog(final AdPlanInfo tempInfo)
    {
        tempAlertView = new AlertView(tempInfo.getName(),null, "取消", null,
                getResources().getStringArray(R.array.plan_dialog),
                getActivity(), AlertView.Style.ActionSheet, new OnItemClickListener(){
            public void onItemClick(Object o,int which){
                switch (which) {
                    case 0://
                        Intent intent = new Intent(getActivity(), AdPlanDetailActivity.class);
                        Bundle b = new Bundle();
                        b.putSerializable("AD_PLAN_INFO",tempInfo);
                        intent.putExtras(b);
                        startActivityForResult(intent, 2);
                        break;
                    case 1://
                        AdPlanInfo copyInfo = null;
                        try {
                            copyInfo = (AdPlanInfo) tempInfo.clone();
                            copyInfo.setId("");
                            copyInfo.setName(copyInfo.getName() + "_复制");
                            dataList.add(0,copyInfo);
                            loadMoreWrapper.notifyDataSetChanged();
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 2://

                        tempAlertView.dismissImmediately();
                        new AlertView("提示", "确定删除该计划吗？", "取消", new String[]{"确定"}, null, getActivity(), AlertView.Style.Alert, new OnItemClickListener() {
                            @Override
                            public void onItemClick(Object o, int position) {
                                if(position == 0)
                                    deleteAdPlan(tempInfo);
                            }
                        }).setCancelable(true).show();
                        break;
                }
            }
        });tempAlertView.show();
    }

}
