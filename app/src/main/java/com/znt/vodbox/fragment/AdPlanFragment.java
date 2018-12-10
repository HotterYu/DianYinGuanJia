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
import com.znt.vodbox.activity.AdPlanDetailActivity;
import com.znt.vodbox.adapter.AdPlanlistAdapter;
import com.znt.vodbox.adapter.OnMoreClickListener;
import com.znt.vodbox.bean.AdPlanInfo;
import com.znt.vodbox.bean.AdPlanListResultBean;
import com.znt.vodbox.bean.CommonCallBackBean;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.http.HttpCallback;
import com.znt.vodbox.http.HttpClient;
import com.znt.vodbox.utils.binding.Bind;
import com.znt.vodbox.view.xlistview.LJListView;

import java.util.ArrayList;
import java.util.List;


public class AdPlanFragment extends BaseFragment implements LJListView.IXListViewListener, AdapterView.OnItemClickListener, OnMoreClickListener {
    @Bind(R.id.ptrl_plan_list)
    private LJListView listView = null;


    private List<AdPlanInfo> dataList = new ArrayList<>();

    private AdPlanlistAdapter mPlanlistAdapter = null;
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

        mPlanlistAdapter = new AdPlanlistAdapter(dataList);

        mPlanlistAdapter.setOnMoreClickListener(this);

        listView.setAdapter(mPlanlistAdapter);

        listView.onFresh();

    }

    public void refreshData()
    {
        listView.onFresh();
    }

    public void getAdPlanList()
    {
        String token = Constant.mUserInfo.getToken();
        String pageNo = "1";
        String pageSize = "50";
        String id = "";//计划id
        String merchId = Constant.mUserInfo.getMerchant().getId();
        String groupId = "";
        //String merchId = mUserInfo.getMerchant().getId();
        String typeId = "";
        String planName = "";

        try
        {
            // Simulate network access.
            HttpClient.getAdPlanList(token,pageNo, pageSize,id,merchId,groupId,planName, new HttpCallback<AdPlanListResultBean>() {
                @Override
                public void onSuccess(AdPlanListResultBean resultBean) {
                    listView.stopRefresh();
                    if(resultBean != null)
                    {
                        dataList = resultBean.getData();

                        mPlanlistAdapter.notifyDataSetChanged(dataList);

                    }
                    else
                    {
                        //shopinfoList.clear();
                    }

                }

                @Override
                public void onFail(Exception e) {
                    listView.stopRefresh();
                }
            });
        }
        catch (Exception e)
        {

        }
    }

    public void deleteAdPlan(AdPlanInfo info)
    {
        String token = Constant.mUserInfo.getToken();

        try
        {
            // Simulate network access.
            HttpClient.deleteAdPlan(token,info.getId(),new HttpCallback<CommonCallBackBean>() {
                @Override
                public void onSuccess(CommonCallBackBean resultBean) {
                    listView.stopRefresh();
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
                    listView.stopRefresh();
                }
            });
        }
        catch (Exception e)
        {

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

        AdPlanInfo tempInfo = dataList.get(position);

        Intent intent = new Intent(getActivity(), AdPlanDetailActivity.class);
        Bundle b = new Bundle();
        b.putSerializable("AD_PLAN_INFO",tempInfo);
        intent.putExtras(b);
        startActivityForResult(intent, 2);

    }

    @Override
    public void onMoreClick(int position) {
        AdPlanInfo tempInfo = dataList.get(position);
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(tempInfo.getName());
        dialog.setItems(R.array.plan_dialog, (dialog1, which) -> {
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
                        mPlanlistAdapter.notifyDataSetChanged(dataList);
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2://
                    deleteAdPlan(tempInfo);
                    break;
            }
        });
        dialog.show();
    }

    @Override
    public void onRefresh() {
        getAdPlanList();
    }

    @Override
    public void onLoadMore() {
        getAdPlanList();
    }
}
