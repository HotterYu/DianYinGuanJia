package com.znt.vodbox.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.znt.vodbox.R;
import com.znt.vodbox.activity.GrouShopActivity;
import com.znt.vodbox.adapter.GroupListAdapter;
import com.znt.vodbox.adapter.OnMoreClickListener;
import com.znt.vodbox.bean.CommonCallBackBean;
import com.znt.vodbox.bean.GourpListResultBean;
import com.znt.vodbox.bean.GroupInfo;
import com.znt.vodbox.dialog.TextInputBottomDialog;
import com.znt.vodbox.entity.LocalDataEntity;
import com.znt.vodbox.http.HttpCallback;
import com.znt.vodbox.http.HttpClient;
import com.znt.vodbox.utils.binding.Bind;
import com.znt.vodbox.view.xlistview.LJListView;

import java.util.ArrayList;
import java.util.List;


public class GroupFragment extends BaseFragment  implements
        LJListView.IXListViewListener, AdapterView.OnItemClickListener,OnMoreClickListener {
    @Bind(R.id.ptrl_group_list)
    private LJListView listView = null;

    private List<GroupInfo> dataList = new ArrayList<>();

    private GroupListAdapter mGroupListAdapter = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group_list, container, false);
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

        mGroupListAdapter = new GroupListAdapter(dataList);
        listView.setAdapter(mGroupListAdapter);

        mGroupListAdapter.setOnMoreClickListener(this);

        listView.onFresh();

    }

    @Override
    protected void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();

    }

    public void getGroupList()
    {
        String token = LocalDataEntity.newInstance(getActivity()).getUserInfor().getToken();
        String pageNo = "1";
        String pageSize = "100";
        //String merchId = Constant.mUserInfo.getMerchant().getId();
        //String adminId = Constant.mUserInfo.getId();
        String merchId = "";
        String adminId = "";
        String groupName = "";

        try
        {
            // Simulate network access.
            HttpClient.getGroupList(token, pageNo, pageSize,merchId,adminId,groupName, new HttpCallback<GourpListResultBean>() {
                @Override
                public void onSuccess(GourpListResultBean resultBean) {

                    if(resultBean != null)
                    {
                        //tvTopTitle.setText("我的分区" + "("+resultBean.getMessage() + ")");
                        dataList = resultBean.getData();
                        mGroupListAdapter.notifyDataSetChanged(dataList);

                    }
                    else
                    {
                        showToast(resultBean.getMessage());
                    }
                    listView.stopRefresh();
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
        }

    }

    public void deleteGroup(String id)
    {
        String token = LocalDataEntity.newInstance(getActivity()).getUserInfor().getToken();


        try
        {
            // Simulate network access.
            HttpClient.deleteGroup(token, id, new HttpCallback<CommonCallBackBean>() {
                @Override
                public void onSuccess(CommonCallBackBean resultBean) {

                    if(resultBean != null)
                    {
                        getGroupList();
                    }
                    else
                    {

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
            showToast(e.getMessage());
            Log.e("",e.getMessage());
        }

    }

    public void renameGroupName(String groupName, String id)
    {
        String token = LocalDataEntity.newInstance(getActivity()).getUserInfor().getToken();
        String adminId = LocalDataEntity.newInstance(getActivity()).getUserInfor().getId();

        try
        {
            // Simulate network access.
            HttpClient.renameGroupName(token, id, groupName, adminId, new HttpCallback<CommonCallBackBean>() {
                @Override
                public void onSuccess(CommonCallBackBean resultBean) {

                    if(resultBean != null)
                    {
                        getGroupList();
                    }
                    else
                    {

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
            showToast(e.getMessage());
            Log.e("",e.getMessage());
        }

    }

    public void addGroup(String groupName)
    {
        String token = LocalDataEntity.newInstance(getActivity()).getUserInfor().getToken();
        String merchId = LocalDataEntity.newInstance(getActivity()).getUserInfor().getMerchant().getId();
        String adminId = LocalDataEntity.newInstance(getActivity()).getUserInfor().getId();

        try
        {
            // Simulate network access.
            HttpClient.addGroup(token, merchId, groupName, adminId, new HttpCallback<CommonCallBackBean>() {
                @Override
                public void onSuccess(CommonCallBackBean resultBean) {

                    if(resultBean != null)
                    {
                        getGroupList();
                    }
                    else
                    {

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
            showToast(e.getMessage());
            Log.e("",e.getMessage());
        }

    }



    @Override
    public void onRefresh() {
        getGroupList();
    }

    @Override
    public void onLoadMore() {
        getGroupList();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if(position > 0)
            position = position - 1;

        GroupInfo tempInfor = dataList.get(position);
        Intent intent = new Intent(getActivity(), GrouShopActivity.class);
        Bundle b = new Bundle();
        b.putSerializable("GROUP_INFO",tempInfor);
        intent.putExtras(b);
        startActivity(intent);

    }

    @Override
    public void onMoreClick(int position) {
        final GroupInfo tempInfo = dataList.get(position);
        showGroupOperationDialog(tempInfo);
    }

    private AlertView tempAlertView = null;
    private void showGroupOperationDialog(final GroupInfo tempInfo)
    {
        tempAlertView = new AlertView(tempInfo.getGroupName(),null, "取消", null,
                getResources().getStringArray(R.array.group_list_dialog),
                getActivity(), AlertView.Style.ActionSheet, new OnItemClickListener(){
            public void onItemClick(Object o,int which){
                switch (which) {
                    case 0://
                        TextInputBottomDialog mTextInputBottomDialog = new TextInputBottomDialog(getActivity());
                        mTextInputBottomDialog.show("请输入分区名称", tempInfo.getGroupName(), new TextInputBottomDialog.OnDismissResultListener() {
                            @Override
                            public void onConfirmDismiss(String content) {
                                renameGroupName(content, tempInfo.getId());
                            }
                        });
                        break;
                    case 1://
                        tempAlertView.dismissImmediately();
                        new AlertView("提示", "确定删除该分区吗？", "取消", new String[]{"确定"}, null, getActivity(), AlertView.Style.Alert, new OnItemClickListener() {
                            @Override
                            public void onItemClick(Object o, int position) {
                                if(position == 0)
                                    deleteGroup(tempInfo.getId());
                            }
                        }).setCancelable(true).show();

                        break;
                }
            }
        });tempAlertView.show();
    }

}
