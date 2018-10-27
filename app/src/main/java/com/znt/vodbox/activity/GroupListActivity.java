package com.znt.vodbox.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.vodbox.adapter.AlbumMusiclistAdapter;
import com.znt.vodbox.adapter.GroupListAdapter;
import com.znt.vodbox.adapter.OnMoreClickListener;
import com.znt.vodbox.bean.AlbumInfo;
import com.znt.vodbox.bean.CommonCallBackBean;
import com.znt.vodbox.bean.GourpListResultBean;
import com.znt.vodbox.bean.GroupInfo;
import com.znt.vodbox.bean.MediaInfo;
import com.znt.vodbox.bean.MusicListResultBean;
import com.znt.vodbox.bean.ShopListResultBean;
import com.znt.vodbox.dialog.EditNameDialog;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.http.HttpCallback;
import com.znt.vodbox.http.HttpClient;
import com.znt.vodbox.utils.binding.Bind;
import com.znt.vodbox.view.xlistview.LJListView;

import java.util.ArrayList;
import java.util.List;

public class GroupListActivity extends BaseActivity implements
        LJListView.IXListViewListener, AdapterView.OnItemClickListener,OnMoreClickListener
{

    @Bind(R.id.tv_common_title)
    private TextView tvTopTitle = null;
    @Bind(R.id.iv_common_back)
    private ImageView ivTopReturn = null;
    @Bind(R.id.iv_common_more)
    private ImageView ivTopMore = null;
    @Bind(R.id.tv_common_confirm)
    private TextView ivTopConfirm = null;

    @Bind(R.id.ptrl_group_list)
    private LJListView listView = null;

    @Bind(R.id.fab_add_group)
    FloatingActionButton fab = null;

    private List<GroupInfo> dataList = new ArrayList<>();

    private String curGroupId = "";
    private String shopIds = "";
    private boolean isEdit = false;

    private GroupListAdapter mGroupListAdapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        tvTopTitle.setText("我的分区");
        ivTopMore.setVisibility(View.GONE);
        ivTopConfirm.setVisibility(View.VISIBLE);
        ivTopConfirm.setText("选择");
        ivTopReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                showNameEditDialog(null);
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

        mGroupListAdapter = new GroupListAdapter(dataList);
        listView.setAdapter(mGroupListAdapter);

        mGroupListAdapter.setOnMoreClickListener(this);

        curGroupId = getIntent().getStringExtra("GROUP_ID");
        shopIds = getIntent().getStringExtra("SHOP_IDS");
        isEdit = getIntent().getBooleanExtra("IS_EDIT", false);
        if(isEdit)
            tvTopTitle.setText("请选择分区");
        listView.onFresh();

    }

    private EditNameDialog dialog = null;
    private void showNameEditDialog(final GroupInfo groupInfo)
    {
        if(dialog == null || dialog.isDismissed())
            dialog = new EditNameDialog(getActivity(),"请输入分区名称");
        if(groupInfo != null)
            dialog.setInfor(groupInfo.getGroupName());
        //playDialog.updateProgress("00:02:18 / 00:05:12");
        if(dialog.isShowing())
            dialog.dismiss();
        dialog.show();
        dialog.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                // TODO Auto-generated method stub
                if(TextUtils.isEmpty(dialog.getContent()))
                {
                    showToast("请输入分区名称");
                    return;
                }

                if(groupInfo != null)
                {
                    if(dialog.getContent().equals(groupInfo.getGroupName()))
                    {
                        showToast("请输入分区名称");
                        return;
                    }
                    renameGroupName(dialog.getContent(), groupInfo.getId());
                }
                else
                    addGroup(dialog.getContent());
            }
        });

        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = (int)(display.getWidth()); //设置宽度
        lp.height = (int)(display.getHeight()); //设置高度
        dialog.getWindow().setAttributes(lp);
    }

    public void getGroupList()
    {
        String token = Constant.mUserInfo.getToken();
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
                                tvTopTitle.setText("我的分区" + "("+resultBean.getMessage() + ")");
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
        String token = Constant.mUserInfo.getToken();


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
        String token = Constant.mUserInfo.getToken();
        String adminId = Constant.mUserInfo.getId();

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
                    dialog.dismiss();
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
        String token = Constant.mUserInfo.getToken();
        String merchId = Constant.mUserInfo.getMerchant().getId();
        String adminId = Constant.mUserInfo.getId();

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
                    dialog.dismiss();
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

    public void addShopTopGroup(String id)
    {
        String token = Constant.mUserInfo.getToken();
        try
        {
            // Simulate network access.
            HttpClient.addShopTopGroup(token, id, shopIds, new HttpCallback<CommonCallBackBean>() {
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
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    //bundle.putSerializable("GROUP_INFOR", tempInfor);
                    intent.putExtras(bundle);
                    setResult(RESULT_OK, intent);
                    finish();
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
        if(isEdit)
        {
            addShopTopGroup(tempInfor.getId());

        }
        else
        {
            /*Intent intent = new Intent(getActivity(), AlbumMusicActivity.class);
            Bundle b = new Bundle();
            b.putSerializable("MEDIA_INFO",tempInfor);
            intent.putExtras(b);
            startActivity(intent);*/
        }

    }

    @Override
    public void onMoreClick(int position) {
        GroupInfo tempInfo = dataList.get(position);
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(tempInfo.getGroupName());
        dialog.setItems(R.array.group_list_dialog, (dialog1, which) -> {
            switch (which) {
                case 0://
                    showNameEditDialog(tempInfo);
                    break;
                case 1://
                    /*Intent intent = new Intent(getActivity(), ShopSelectActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable("MEDIA_INFO",tempInfo);
                    intent.putExtras(b);
                    startActivity(intent);*/
                    //requestSetRingtone(music);
                    break;
                case 2://
                    deleteGroup(tempInfo.getId());
                    break;

            }
        });
        dialog.show();
    }
}