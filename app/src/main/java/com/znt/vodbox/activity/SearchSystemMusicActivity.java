package com.znt.vodbox.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.znt.vodbox.R;
import com.znt.vodbox.adapter.AlbumMusiclistAdapter;
import com.znt.vodbox.adapter.OnMoreClickListener;
import com.znt.vodbox.bean.CommonCallBackBean;
import com.znt.vodbox.bean.MediaInfo;
import com.znt.vodbox.bean.MusicListResultBean;
import com.znt.vodbox.entity.LocalDataEntity;
import com.znt.vodbox.http.HttpCallback;
import com.znt.vodbox.http.HttpClient;
import com.znt.vodbox.model.Shopinfo;
import com.znt.vodbox.utils.ToastUtils;
import com.znt.vodbox.utils.binding.Bind;
import com.znt.vodbox.view.searchview.ICallBack;
import com.znt.vodbox.view.xlistview.LJListView;

import java.util.ArrayList;
import java.util.List;


public class SearchSystemMusicActivity extends BaseActivity implements
        LJListView.IXListViewListener, AdapterView.OnItemClickListener,OnMoreClickListener {

    @Bind(R.id.tv_common_title)
    private TextView tvTopTitle = null;
    @Bind(R.id.iv_common_back)
    private ImageView ivTopReturn = null;
    @Bind(R.id.iv_common_more)
    private ImageView ivTopMore = null;

    @Bind(R.id.lv_search_music_list)
    private LJListView listView = null;
    @Bind(R.id.search_view)
    private com.znt.vodbox.view.searchview.SearchView mSearchView = null;

    @Bind(R.id.ll_loading)
    private LinearLayout llLoading;
    @Bind(R.id.ll_load_fail)
    private LinearLayout llLoadFail;
    private List<MediaInfo> dataList = new ArrayList<>();
    private AlbumMusiclistAdapter mAlbumMusiclistAdapter = new AlbumMusiclistAdapter(dataList);

    private String albumId = "";
    private Shopinfo mShopinfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_system_music);

        tvTopTitle.setText("搜索歌曲");
        ivTopMore.setVisibility(View.GONE);

        listView.getListView().setDivider(getResources().getDrawable(R.color.transparent));
        listView.getListView().setDividerHeight(1);
        listView.setPullLoadEnable(true,"");
        listView.setPullRefreshEnable(true);
        listView.setIsAnimation(true);
        listView.setXListViewListener(this);
        listView.showFootView(false);
        listView.setRefreshTime();
        listView.setOnItemClickListener(this);

        listView.setAdapter(mAlbumMusiclistAdapter);
        mAlbumMusiclistAdapter.setOnMoreClickListener(this);

        ivTopReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSearchView.init("system_music_search_record.db");
        mSearchView.showRecordView(false);

        albumId = getIntent().getStringExtra("ALBUM_ID");
        mShopinfo = (Shopinfo)getIntent().getSerializableExtra("SHOP_INFO");

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                mSearchView.showRecordView(false);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        //listView.onFresh();

        mSearchView.setOnClickSearch(new ICallBack() {
            @Override
            public void SearchAciton(String string) {
                searchMusic();
            }
        });
    }

    @Override
    protected int getDarkTheme() {
        return R.style.AppThemeDark_Search;
    }


    private void searchMusic() {

        String keyword = mSearchView.getText().toString();
        if(TextUtils.isEmpty(keyword))
        {
            ToastUtils.show("请输入查找内容");
            listView.stopRefresh();
            return;
        }
        String token = LocalDataEntity.newInstance(getActivity()).getUserInfor().getToken();
        String pageNo = "1";
        String pageSize = "100";

        String merchId = LocalDataEntity.newInstance(getActivity()).getUserInfor().getMerchant().getId();
        //String merchId = mUserInfo.getMerchant().getId();

        try
        {
            // Simulate network access.
            HttpClient.getSystemMusics(token, pageNo, pageSize,keyword, new HttpCallback<MusicListResultBean>() {
                @Override
                public void onSuccess(MusicListResultBean resultBean) {

                    if(resultBean != null && resultBean.isSuccess())
                    {
                        dataList = resultBean.getData();
                        mAlbumMusiclistAdapter.notifyDataSetChanged(dataList);
                        tvTopTitle.setText("搜索歌曲("+resultBean.getMessage()+")");
                    }
                    else
                    {

                    }
                    mSearchView.showRecordView(false);
                    listView.stopRefresh();
                }

                @Override
                public void onFail(Exception e) {
                    listView.stopRefresh();
                    //vSearching.setVisibility(View.GONE);
                    //listView.stopRefresh();
                }
            });
        }
        catch (Exception e)
        {
            listView.stopRefresh();
            //listView.stopRefresh();
        }
    }

    private void pushMedia(String terminId, String dataId)
    {
        String type = "1";
        String userId = LocalDataEntity.newInstance(getActivity()).getUserInfor().getMerchant().getId();
        String pusherid = "";
        String pushername = LocalDataEntity.newInstance(getActivity()).getUserInfor().getNickName();
        try
        {
            // Simulate network access.
            HttpClient.pushMedia(terminId, type, dataId, userId,pusherid,pushername, new HttpCallback<CommonCallBackBean>() {
                @Override
                public void onSuccess(CommonCallBackBean resultBean) {

                    if(resultBean != null)
                    {
                        if(!resultBean.isSuccess())
                        {
                            ToastUtils.show(getResources().getString(R.string.push_fail)+":"+resultBean.getMessage());
                        }
                        else
                        {
                            ToastUtils.show(getResources().getString(R.string.push_success));
                            finish();
                        }
                    }
                    else
                    {
                        ToastUtils.show(getResources().getString(R.string.push_fail));
                    }
                }
                @Override
                public void onFail(Exception e) {
                    ToastUtils.show(getResources().getString(R.string.push_fail));
                }
            });
        }
        catch (Exception e)
        {
            ToastUtils.show(getResources().getString(R.string.push_fail));
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if(position > 0)
            position = position - 1;
        final MediaInfo tempInfor = dataList.get(position);

        if(mShopinfo == null)
        {
            showPlayDialog(tempInfor.getMusicName(), tempInfor.getMusicUrl(), tempInfor.getId());
        }
        else
        {
            showPlayDialog(tempInfor.getMusicName(), tempInfor.getMusicUrl(), tempInfor.getId(), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pushMedia(mShopinfo.getTmlRunStatus().get(0).getTerminalId(),tempInfor.getId());
                    dismissDialog();
                }
            });
        }
    }

    @Override
    public void onMoreClick(int position) {
        final MediaInfo tempInfo = dataList.get(position);
        showMusicOperationDialog(tempInfo);

    }

    private void showMusicOperationDialog(final MediaInfo tempInfo)
    {
        new AlertView(tempInfo.getMusicName(),null, "取消", null,
                getResources().getStringArray(R.array.album_music_dialog),
                getActivity(), AlertView.Style.ActionSheet, new OnItemClickListener(){
            public void onItemClick(Object o,int which){
                switch (which) {
                    case 0://
                        if(TextUtils.isEmpty(albumId))
                        {
                            Intent i = new Intent(getActivity(), MyAlbumActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("MUSIC_IDS",tempInfo.getId());
                            i.putExtras(bundle);
                            startActivity(i);
                        }
                        else
                        {
                            addMusicToAlbum(albumId,tempInfo.getId());
                        }
                        break;
                    case 1://
                        if(mShopinfo == null)
                        {
                            Intent intent = new Intent(getActivity(), ShopSelectActivity.class);
                            Bundle b = new Bundle();
                            b.putString("MEDIA_NAME",tempInfo.getMusicName());
                            b.putString("MEDIA_ID",tempInfo.getId());
                            b.putString("MEDIA_URL",tempInfo.getMusicUrl());
                            intent.putExtras(b);
                            startActivity(intent);
                        }
                        else
                        {
                            pushMedia(mShopinfo.getTmlRunStatus().get(0).getTerminalId(),tempInfo.getId());
                        }

                        //requestSetRingtone(music);
                        break;
                    case 2://
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        // 将文本内容放到系统剪贴板里。
                        cm.setText(tempInfo.getMusicUrl());
                        showToast("复制成功");
                        break;
                    case 3://
                        //deleteMusic(music);
                        break;
                }
            }
        }).show();
    }

    public void addMusicToAlbum(String id, String musicIds)
    {
        String token = LocalDataEntity.newInstance(getActivity()).getUserInfor().getToken();
        try
        {
            // Simulate network access.
            HttpClient.addMusicToAlbum(token, id,musicIds, new HttpCallback<CommonCallBackBean>() {
                @Override
                public void onSuccess(CommonCallBackBean resultBean) {

                    if(resultBean != null)
                    {
                        //finish();
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
            if(e != null)
                showToast(e.getMessage());
            Log.e("",e.getMessage());
        }
    }

    @Override
    public void onRefresh() {
        searchMusic();
    }

    @Override
    public void onLoadMore() {
        searchMusic();
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
