package com.znt.vodbox.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.znt.vodbox.R;
import com.znt.vodbox.activity.PlanDetailActivity;
import com.znt.vodbox.adapter.OnMoreClickListener;
import com.znt.vodbox.adapter.PlanlistAdapter;
import com.znt.vodbox.bean.CommonCallBackBean;
import com.znt.vodbox.bean.PlanInfo;
import com.znt.vodbox.bean.PlanListResultBean;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.http.HttpCallback;
import com.znt.vodbox.http.HttpClient;
import com.znt.vodbox.utils.binding.Bind;
import com.znt.vodbox.view.xlistview.LJListView;

import java.util.ArrayList;
import java.util.List;


public class MediaPlanFragment extends BaseFragment implements LJListView.IXListViewListener, AdapterView.OnItemClickListener, OnMoreClickListener {
    @Bind(R.id.ptrl_plan_list)
    private LJListView listView = null;

    private List<PlanInfo> dataList = new ArrayList<>();

    private PlanlistAdapter mPlanlistAdapter = null;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_plan_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listView.getListView().setDivider(getResources().getDrawable(R.color.transparent));
        listView.getListView().setDividerHeight(1);
        listView.setPullLoadEnable(true,"");
        listView.setPullRefreshEnable(true);
        listView.setIsAnimation(true);
        listView.setXListViewListener(this);
        listView.showFootView(false);
        listView.setRefreshTime();
        listView.setOnItemClickListener(this);

        mPlanlistAdapter = new PlanlistAdapter(dataList);

        mPlanlistAdapter.setOnMoreClickListener(this);

        listView.setAdapter(mPlanlistAdapter);

        listView.onFresh();

    }

    public void refreshData()
    {
        listView.onFresh();
    }

    public void getPlanList()
    {
        String token = Constant.mUserInfo.getToken();
        String pageNo = "1";
        String pageSize = "20";
        String id = "";//计划id
        String merchId = Constant.mUserInfo.getMerchant().getId();
        String groupId = "";
        //String merchId = mUserInfo.getMerchant().getId();
        String typeId = "";
        String planName = "";

        try
        {
            // Simulate network access.
            HttpClient.getPlanList(token,pageNo, pageSize,id,merchId,groupId,planName, new HttpCallback<PlanListResultBean>() {
                @Override
                public void onSuccess(PlanListResultBean resultBean) {
                    listView.stopRefresh();
                    if(resultBean != null)
                    {
                        dataList = resultBean.getData();

                        mPlanlistAdapter.notifyDataSetChanged(dataList);

                    }
                    else
                    {
                        showToast(resultBean.getMessage());
                        //shopinfoList.clear();
                    }

                }

                @Override
                public void onFail(Exception e) {
                    listView.stopRefresh();
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

    public void deletePlan(PlanInfo info)
    {
        String token = Constant.mUserInfo.getToken();

        try
        {
            // Simulate network access.
            HttpClient.deletePlan(token,info.getId(),new HttpCallback<CommonCallBackBean>() {
                @Override
                public void onSuccess(CommonCallBackBean resultBean) {
                    listView.stopRefresh();
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
                    listView.stopRefresh();
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


        if(position > 0)
            position = position - 1;

        PlanInfo tempInfo = dataList.get(position);

        Intent intent = new Intent(getActivity(), PlanDetailActivity.class);
        Bundle b = new Bundle();
        b.putSerializable("PLAN_INFO",tempInfo);
        intent.putExtras(b);
        startActivityForResult(intent, 1);

    }

    @Override
    public void onMoreClick(int position) {
        PlanInfo tempInfo = dataList.get(position);
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(tempInfo.getPlanName());
        dialog.setItems(R.array.plan_dialog, (dialog1, which) -> {
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
                        mPlanlistAdapter.notifyDataSetChanged(dataList);
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }

                    break;
                case 2://
                    deletePlan(tempInfo);
                    break;
            }
        });
        dialog.show();
    }

    @Override
    public void onRefresh() {
        getPlanList();
    }

    @Override
    public void onLoadMore() {
        getPlanList();
    }
}
