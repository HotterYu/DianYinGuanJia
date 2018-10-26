package com.znt.vodbox.activity;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.vodbox.adapter.AdListAdapter;
import com.znt.vodbox.adapter.OnMoreClickListener;
import com.znt.vodbox.bean.AdMediaInfo;
import com.znt.vodbox.bean.AdMediaListResultBean;
import com.znt.vodbox.bean.SubAdPlanInfo;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.http.HttpCallback;
import com.znt.vodbox.http.HttpClient;
import com.znt.vodbox.utils.binding.Bind;
import com.znt.vodbox.view.xlistview.LJListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AdListActivity  extends BaseActivity implements
        LJListView.IXListViewListener, AdapterView.OnItemClickListener,OnMoreClickListener {

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

    private AdListAdapter mAdListAdapter = null;

    private List<AdMediaInfo> dataList = new ArrayList<>();

    private boolean isSelect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_list);

        tvTopTitle.setText("我的广告");
        ivTopMore.setVisibility(View.GONE);
        tvConfirm.setVisibility(View.VISIBLE);

        ivTopReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSelect)
                    finishAndFeedBack();
                else
                {
                    isSelect = true;
                    tvConfirm.setText("选择");
                    mAdListAdapter.setSelect(isSelect);
                    mAdListAdapter.notifyDataSetChanged();
                }
            }
        });

        isSelect = getIntent().getBooleanExtra("IS_SELECT", false);
        if(isSelect)
            tvConfirm.setText("完成");
        else
            tvConfirm.setText("选择");

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
        mAdListAdapter.setSelect(isSelect);
        listView.setAdapter(mAdListAdapter);

        mAdListAdapter.setOnMoreClickListener(this);

        listView.onFresh();

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

    public void getAdMedias()
    {
        String token = Constant.mUserInfo.getToken();
        String pageNo = "1";
        String pageSize = "100";
        String merchId = Constant.mUserInfo.getMerchant().getId();
        //String merchId = mUserInfo.getMerchant().getId();
        String adtypeId = "";
        String adname = "";

        try
        {
            // Simulate network access.
            HttpClient.getAdLists(token, pageNo, pageSize,merchId,adtypeId,adname, new HttpCallback<AdMediaListResultBean>() {
                @Override
                public void onSuccess(AdMediaListResultBean resultBean) {

                    if(resultBean != null)
                    {
                        dataList = resultBean.getData();
                        mAdListAdapter.notifyDataSetChanged(dataList);
                        tvTopTitle.setText("我的广告("+resultBean.getMessage()+")");
                    }
                    else
                    {
                        //shopinfoList.clear();
                    }
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(position > 0)
            position = position - 1;

        AdMediaInfo tempInfor = dataList.get(position);
        showPlayDialog(tempInfor.getAdname(),tempInfor.getUrl(),tempInfor.getId());
    }

    @Override
    public void onMoreClick(int position) {
        AdMediaInfo tempInfo = dataList.get(position);
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(tempInfo.getAdname());
        dialog.setItems(R.array.ad_list_dialog, (dialog1, which) -> {
            switch (which) {
                case 0://
                    Intent intent = new Intent(getActivity(), ShopSelectActivity.class);
                    Bundle b = new Bundle();
                    b.putString("MEDIA_NAME",tempInfo.getAdname());
                    b.putString("MEDIA_ID",tempInfo.getId());
                    b.putString("MEDIA_URL",tempInfo.getUrl());
                    intent.putExtras(b);
                    startActivity(intent);
                    break;
                case 1://

                    break;
                case 2://
                    //MusicInfoActivity.start(getContext(), music);
                    break;
                case 3://
                    //deleteMusic(music);
                    break;
            }
        });
        dialog.show();
    }

    @Override
    public void onRefresh() {
        getAdMedias();
    }

    @Override
    public void onLoadMore() {
        getAdMedias();
    }
}
