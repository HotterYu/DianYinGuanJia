package com.znt.vodbox.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.vodbox.adapter.AdListAdapter;
import com.znt.vodbox.adapter.OnMoreClickListener;
import com.znt.vodbox.bean.AdMediaInfo;
import com.znt.vodbox.bean.AdMediaListResultBean;
import com.znt.vodbox.bean.TypeInfo;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.http.HttpCallback;
import com.znt.vodbox.http.HttpClient;
import com.znt.vodbox.utils.binding.Bind;
import com.znt.vodbox.view.searchview.ICallBack;
import com.znt.vodbox.view.searchview.SearchView;
import com.znt.vodbox.view.xlistview.LJListView;

import java.io.Serializable;
import java.net.URLDecoder;
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
    @Bind(R.id.search_view)
    private SearchView mSearchView = null;

    private AdListAdapter mAdListAdapter = null;

    private List<AdMediaInfo> dataList = new ArrayList<>();
    private List<AdMediaInfo> selectedAds = new ArrayList<>();

    private boolean isSelect = false;

    private int pageNo = 1;
    private int pageSize = 25;
    private int maxSize = 0;

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

        tvTopTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TypeActivity.class);
                Bundle b = new Bundle();
                b.putString("TYPE","1");
                intent.putExtras(b);
                startActivityForResult(intent,2);
            }
        });

        isSelect = getIntent().getBooleanExtra("IS_SELECT", false);
        if(isSelect)
            tvConfirm.setText("完成");
        else
            tvConfirm.setText("选择");

        selectedAds = (List<AdMediaInfo>) getIntent().getSerializableExtra("AD_SELECTED_LIST");


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
                getAdMedias("");
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

    public void getAdMedias(String adtypeId)
    {
        String token = Constant.mUserInfo.getToken();

        String merchId = Constant.mUserInfo.getMerchant().getId();
        //String merchId = mUserInfo.getMerchant().getId();
        String adname = mSearchView.getText().toString();

        try
        {
            // Simulate network access.
            HttpClient.getAdLists(token, pageNo + "", pageSize + "",merchId,adtypeId,adname, new HttpCallback<AdMediaListResultBean>() {
                @Override
                public void onSuccess(AdMediaListResultBean resultBean) {

                    if(resultBean != null)
                    {
                        List<AdMediaInfo> tempList = resultBean.getData();

                        if(pageNo == 1)
                            dataList.clear();

                        if(tempList.size() <= pageSize)
                            pageNo ++;

                        dataList.addAll(tempList);

                        mAdListAdapter.notifyDataSetChanged(dataList);
                        mAdListAdapter.updateSelected(selectedAds);
                        tvTopTitle.setText("我的广告("+resultBean.getMessage()+")");
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

    public void deleteAlbumMusic(String musicIds)
    {
        /*try
        {
            String token = Constant.mUserInfo.getToken();
            String albumId = mAlbumInfo.getId();

            HttpClient.deleteAlbumMusics(token, albumId, musicIds, new HttpCallback<CommonCallBackBean>() {
                @Override
                public void onSuccess(CommonCallBackBean resultBean) {
                    if(resultBean != null)
                    {
                        int deleteCount = updateDeleteMusicList(musicIds);
                        tvTopTitle.setText(mAlbumInfo.getName() + "(" + (curMusicSize - deleteCount) + ")");
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

        }*/
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

            getAdMedias(tempInfor.getId());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(position > 0)
            position = position - 1;

        AdMediaInfo tempInfor = dataList.get(position);
        showPlayDialog(tempInfor.getAdname(),URLDecoder.decode(tempInfor.getUrl()),tempInfor.getId());
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
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    // 将文本内容放到系统剪贴板里。
                    cm.setText(tempInfo.getAdname() + "\n" + URLDecoder.decode(tempInfo.getUrl()));
                    showToast("复制成功");
                    break;
                case 2://
                    //MusicInfoActivity.start(getContext(), music);
                    break;
                case 3://
                    showAlertDialog(getActivity(), new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            deleteAlbumMusic(tempInfo.getId());
                        }
                    }, "", "确定删除该文件吗?");
                    break;
            }
        });
        dialog.show();
    }

    @Override
    public void onRefresh() {
        pageNo = 1;
        getAdMedias("");
    }

    @Override
    public void onLoadMore() {
        if(maxSize > dataList.size())
            getAdMedias("");
        else
            showToast("没有更多数据了");
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
