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
import com.znt.vodbox.activity.PlanDetailActivity;
import com.znt.vodbox.adapter.LoadMoreWrapper;
import com.znt.vodbox.adapter.OnMoreClickListener;
import com.znt.vodbox.adapter.PlanLoadMoreAdapter;
import com.znt.vodbox.bean.CommonCallBackBean;
import com.znt.vodbox.bean.PlanInfo;
import com.znt.vodbox.bean.PlanListResultBean;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.http.HttpCallback;
import com.znt.vodbox.http.HttpClient;
import com.znt.vodbox.utils.binding.Bind;
import com.znt.vodbox.view.StaggeredGridRecyclerView;
import com.znt.vodbox.view.listener.EndlessRecyclerOnScrollListener;

import java.util.ArrayList;
import java.util.List;


public class MediaPlanFragment extends BaseFragment implements OnMoreClickListener {
    @Bind(R.id.rv)
    private StaggeredGridRecyclerView mRecyclerView;
    @Bind(R.id.refresh)
    private SwipeRefreshLayout swipeRefreshLayout;

    private LoadMoreWrapper loadMoreWrapper;

    private List<PlanInfo> dataList = new ArrayList<>();

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

        PlanLoadMoreAdapter shopLoadMoreAdapter = new PlanLoadMoreAdapter(getActivity(), dataList);
        loadMoreWrapper = new LoadMoreWrapper(shopLoadMoreAdapter);
        shopLoadMoreAdapter.setOnMoreClickListener(this);
        shopLoadMoreAdapter.setOnItemClickListener(new PlanLoadMoreAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                int index = (int) view.getTag();
                PlanInfo tempInfo = dataList.get(index);

                Intent intent = new Intent(getActivity(), PlanDetailActivity.class);
                Bundle b = new Bundle();
                b.putSerializable("PLAN_INFO",tempInfo);
                intent.putExtras(b);
                startActivityForResult(intent, 1);
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
        getPlanList();
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

    public void getPlanList()
    {

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

        String token = Constant.mUserInfo.getToken();
        String id = "";//计划id
        String merchId = Constant.mUserInfo.getMerchant().getId();
        String groupId = "";
        //String merchId = mUserInfo.getMerchant().getId();
        String typeId = "";
        String planName = "";

        try
        {
            // Simulate network access.
            HttpClient.getPlanList(token,pageNo+"", pageSize+"",id,merchId,groupId,planName, new HttpCallback<PlanListResultBean>() {
                @Override
                public void onSuccess(PlanListResultBean resultBean)
                {
                    if(resultBean != null)
                    {
                        int lastSize = dataList.size();

                        if(pageNo == 1)
                            dataList.clear();
                        List<PlanInfo> tempList = resultBean.getData();
                        dataList.addAll(tempList);
                        maxSize = Integer.parseInt(resultBean.getMessage());
                        loadMoreWrapper.notifyItemChanged(lastSize,dataList.size());
                    }
                    else
                    {
                        showToast(resultBean.getMessage());
                        //shopinfoList.clear();
                    }
                    refreshUi();
                }

                @Override
                public void onFail(Exception e) {
                    refreshUi();
                    showToast(e.getMessage());
                }
            });
        }
        catch (Exception e)
        {
            refreshUi();
            if(e!=null)
                showToast(e.getMessage());
            else
                showToast(getResources().getString(R.string.request_fail));
        }
    }

    public void deletePlan(PlanInfo info)
    {
        String token = Constant.mUserInfo.getToken();

        try
        {
            // Simulate network access.
            HttpClient.deletePlan(token,info.getId(),new HttpCallback<CommonCallBackBean>() {
                @Override
                public void onSuccess(CommonCallBackBean resultBean) {

                    if(resultBean != null)
                    {
                        getPlanList();
                    }
                    else
                    {
                        //shopinfoList.clear();
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
            if(e!=null)
                showToast(e.getMessage());
            else
                showToast(getResources().getString(R.string.request_fail));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != getActivity().RESULT_OK)
            return;
        if(requestCode == 1)
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
        final PlanInfo tempInfo = dataList.get(position);
        showPlanOperationDialog(tempInfo);
    }

    private AlertView tempAlertView = null;
    private void showPlanOperationDialog(final PlanInfo tempInfo)
    {
        tempAlertView = new AlertView(tempInfo.getPlanName(),null, "取消", null,
                getResources().getStringArray(R.array.plan_dialog),
                getActivity(), AlertView.Style.ActionSheet, new OnItemClickListener(){
            public void onItemClick(Object o,int which){
                switch (which) {
                    case 0://
                        Intent intent = new Intent(getActivity(), PlanDetailActivity.class);
                        Bundle b = new Bundle();
                        b.putSerializable("PLAN_INFO",tempInfo);
                        intent.putExtras(b);
                        startActivityForResult(intent, 1);
                        break;
                    case 1://

                        PlanInfo copyInfo = null;
                        try {
                            copyInfo = (PlanInfo) tempInfo.clone();
                            copyInfo.setId("");
                            copyInfo.setPlanName(copyInfo.getPlanName() + "_复制");
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
                                    deletePlan(tempInfo);
                            }
                        }).setCancelable(true).show();

                        break;
                }
            }
        });tempAlertView.show();
    }


}
