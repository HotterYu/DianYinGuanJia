package com.znt.vodbox.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.znt.vodbox.R;
import com.znt.vodbox.adapter.AdListAdapter;
import com.znt.vodbox.adapter.OnMoreClickListener;
import com.znt.vodbox.bean.AdMediaInfo;
import com.znt.vodbox.bean.CommonCallBackBean;
import com.znt.vodbox.bean.MusicListResultBean;
import com.znt.vodbox.bean.TypeInfo;
import com.znt.vodbox.entity.LocalDataEntity;
import com.znt.vodbox.http.HttpCallback;
import com.znt.vodbox.http.HttpClient;
import com.znt.vodbox.model.Shopinfo;
import com.znt.vodbox.utils.binding.Bind;
import com.znt.vodbox.view.searchview.ICallBack;
import com.znt.vodbox.view.searchview.SearchView;
import com.znt.vodbox.view.xlistview.LJListView;

import java.io.Serializable;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class PushListActivity extends BaseActivity implements
        LJListView.IXListViewListener, AdapterView.OnItemClickListener,OnMoreClickListener {

    @Bind(R.id.view_common_title)
    private View viewTopTitle = null;
    @Bind(R.id.tv_common_title_sub)
    private TextView tvTopTitleSub = null;
    @Bind(R.id.tv_common_title)
    private TextView tvTopTitle = null;
    @Bind(R.id.iv_common_back)
    private ImageView ivTopReturn = null;
    @Bind(R.id.iv_common_more)
    private ImageView ivTopMore = null;
    @Bind(R.id.tv_common_confirm)
    private TextView tvConfirm = null;
    @Bind(R.id.ptrl_ad_list)
    private LJListView listView = null;
    @Bind(R.id.search_view)
    private SearchView mSearchView = null;

    private AdListAdapter mAdListAdapter = null;

    private List<AdMediaInfo> dataList = new ArrayList<>();

    private Shopinfo mShopInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_list);

        mShopInfo = (Shopinfo) getIntent().getSerializableExtra("SHOP_INFO");

        tvTopTitle.setText("插播列表");
        ivTopMore.setVisibility(View.GONE);
        tvConfirm.setVisibility(View.VISIBLE);
        tvTopTitleSub.setVisibility(View.GONE);

        ivTopReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        viewTopTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AdCategoryActivity.class);
                startActivityForResult(intent,2);
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

        mAdListAdapter = new AdListAdapter(this,dataList);
        listView.setAdapter(mAdListAdapter);

        mAdListAdapter.setOnMoreClickListener(this);

        mSearchView.init("album_search_record.db");
        mSearchView.showRecordView(false);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                mSearchView.showRecordView(false);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        listView.onFresh();

        mSearchView.setOnClickSearch(new ICallBack() {
            @Override
            public void SearchAciton(String string) {
                getPushList();
            }
        });
    }

    private void finishAndFeedBack()
    {
        List<AdMediaInfo> selectList = mAdListAdapter.getSelectedList();


        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("AD_SELECTED_LIST", (Serializable) selectList);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void getPushList()
    {
        String terminalId = mShopInfo.getTmlRunStatus().get(0).getTerminalId();
        try
        {
            // Simulate network access.
            HttpClient.getCurPushMusics(terminalId, new HttpCallback<MusicListResultBean>() {
                @Override
                public void onSuccess(MusicListResultBean resultBean) {

                    if(resultBean != null)
                    {
                        /*List<AdMediaInfo> tempList = resultBean.getData();

                        if(pageNo == 1)
                            dataList.clear();

                        if(tempList.size() == pageSize)
                            pageNo ++;

                        dataList.addAll(tempList);

                        mAdListAdapter.notifyDataSetChanged(dataList);
                        if(!TextUtils.isEmpty(resultBean.getMessage()))
                            maxSize = Integer.parseInt(resultBean.getMessage());

                        tvTopTitle.setText("我的广告("+maxSize+")");*/
                    }
                    else
                    {
                        //shopinfoList.clear();
                    }

                    mSearchView.showRecordView(false);
                    listView.stopRefresh();
                }

                @Override
                public void onFail(Exception e) {
                    //vSearching.setVisibility(View.GONE);
                }
            });
        }
        catch (Exception e)
        {
            listView.stopRefresh();
        }
    }

    public void getPushHisttoryList()
    {
        String terminalId = mShopInfo.getTmlRunStatus().get(0).getTerminalId();
        String pusherId = "";
        String vodFlag = "";
        try
        {
            // Simulate network access.
            HttpClient.getPushHistoryMusics(terminalId,pusherId,vodFlag, new HttpCallback<MusicListResultBean>() {
                @Override
                public void onSuccess(MusicListResultBean resultBean) {

                    if(resultBean != null)
                    {
                        /*List<AdMediaInfo> tempList = resultBean.getData();

                        if(pageNo == 1)
                            dataList.clear();

                        if(tempList.size() == pageSize)
                            pageNo ++;

                        dataList.addAll(tempList);

                        mAdListAdapter.notifyDataSetChanged(dataList);
                        if(!TextUtils.isEmpty(resultBean.getMessage()))
                            maxSize = Integer.parseInt(resultBean.getMessage());

                        tvTopTitle.setText("我的广告("+maxSize+")");*/
                    }
                    else
                    {
                        //shopinfoList.clear();
                    }

                    mSearchView.showRecordView(false);
                    listView.stopRefresh();
                }

                @Override
                public void onFail(Exception e) {
                    //vSearching.setVisibility(View.GONE);
                }
            });
        }
        catch (Exception e)
        {
            listView.stopRefresh();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK)
            return;
        if(requestCode == 1)
        {
            listView.onFresh();
        }
        else if(requestCode == 2)
        {
            TypeInfo tempInfor = (TypeInfo)data.getSerializableExtra("TYPE_INFO");
            tvTopTitleSub.setText(tempInfor.getName());

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(position > 0)
            position = position - 1;

        AdMediaInfo tempInfor = dataList.get(position);
        tempInfor.setType("2");
        showPlayDialog(tempInfor.getAdname(),URLDecoder.decode(tempInfor.getUrl()),tempInfor.getId());
    }

    @Override
    public void onMoreClick(int position) {
        AdMediaInfo tempInfo = dataList.get(position);
        showMusicOperationDialog(position, tempInfo);
    }

    private AlertView tempAlertView = null;
    private void showMusicOperationDialog(final int adPosition, final AdMediaInfo tempInfo)
    {
        tempAlertView = new AlertView(tempInfo.getAdname(),null, "取消", null,
                getResources().getStringArray(R.array.ad_list_dialog),
                getActivity(), AlertView.Style.ActionSheet, new OnItemClickListener(){
            public void onItemClick(Object o,int which){
                switch (which) {
                    case 0://
                        Intent intent = new Intent(getActivity(), ShopSelectActivity.class);
                        Bundle b = new Bundle();
                        b.putString("MEDIA_NAME",tempInfo.getAdname());
                        b.putString("MEDIA_ID",tempInfo.getId());
                        b.putString("MEDIA_URL",tempInfo.getUrl());
                        b.putString("MEDIA_TYPE","2");
                        intent.putExtras(b);
                        startActivity(intent);
                        break;
                    case 1://
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        // 将文本内容放到系统剪贴板里。
                        cm.setText(tempInfo.getAdname() + "\n" + URLDecoder.decode(tempInfo.getUrl()));
                        showToast("复制成功");
                        break;
                    case 2://
                        tempAlertView.dismissImmediately();
                        showRenameDialog(adPosition, tempInfo);
                        break;
                    case 3://
                        tempAlertView.dismissImmediately();
                        new AlertView("提示", "确定删除该文件吗？", "取消", new String[]{"确定"}, null, getActivity(), AlertView.Style.Alert, new OnItemClickListener() {
                            @Override
                            public void onItemClick(Object o, int position) {
                                /*if(position == 0)
                                    deleteAd(adPosition, tempInfo.getId());*/
                            }
                        }).setCancelable(true).show();
                        break;
                }
            }
        });tempAlertView.show();
    }

    private void showRenameDialog(final int adPosition,final AdMediaInfo tempInfo)
    {
        ViewGroup extView = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.alertext_form,null);

        final String oldName = tempInfo.getAdname();

        final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        final EditText etName = (EditText) extView.findViewById(R.id.et_alert_input);

        final AlertView mAlertViewExt = new AlertView("修改名称", "请输入新的昵称！", "取消", null, new String[]{"完成"}, PushListActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {

                if(position == 0)
                {
                    String newName = etName.getText().toString();
                    if(TextUtils.isEmpty(newName))
                    {
                        Toast.makeText(getApplicationContext(),"请输入广告名称",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(newName.equals(oldName))
                    {
                        Toast.makeText(getApplicationContext(),"信息未更改",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    updateAdInfo(adPosition, tempInfo, newName);
                }
            }
        });
        etName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focus) {
                //输入框出来则往上移动
                boolean isOpen= imm.isActive();
                mAlertViewExt.setMarginBottom(isOpen&&focus ? 120 :0);
            }
        });
        etName.setHint("请输入昵称");
        etName.setText(oldName);
        mAlertViewExt.addExtView(extView);
        mAlertViewExt.show();
    }

    private void updateAdInfo(final int position, final AdMediaInfo tempInfo, final String newName)
    {
        String token = LocalDataEntity.newInstance(getActivity()).getUserInfor().getToken();
        /*String merchId = Constant.mUserInfo.getMerchant().getId();
        String adminId = Constant.mUserInfo.getId();*/
        HttpClient.updateAdInfo(token, tempInfo.getId(), newName,tempInfo.getAdtypeId(),tempInfo.getAdduration(), new HttpCallback<CommonCallBackBean>() {
            @Override
            public void onSuccess(CommonCallBackBean commonCallBackBean) {
                if(commonCallBackBean.isSuccess())
                {
                    dataList.get(position).setAdname(newName);
                    mAdListAdapter.notifyDataSetChanged();
                    showToast("名称修改成功");
                }
                else
                    showToast("修改失败："+commonCallBackBean.getMessage());
            }

            @Override
            public void onFail(Exception e) {
                showToast("修改失败");
            }
        });

    }

    @Override
    public void onRefresh() {
        getPushList();
    }

    @Override
    public void onLoadMore() {
        getPushList();
    }

    @Override
    public void onBackPressed()
    {
        if(mSearchView.isRecordViewShow())
        {
            mSearchView.showRecordView(false);
            return;
        }
        super.onBackPressed();
    }
}
