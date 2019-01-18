package com.znt.vodbox.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.znt.vodbox.R;
import com.znt.vodbox.adapter.MyAlbumlistAdapter;
import com.znt.vodbox.adapter.OnMoreClickListener;
import com.znt.vodbox.bean.AlbumInfo;
import com.znt.vodbox.bean.AlbumListResultBean;
import com.znt.vodbox.bean.CommonCallBackBean;
import com.znt.vodbox.bean.TypeInfo;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.http.HttpCallback;
import com.znt.vodbox.http.HttpClient;
import com.znt.vodbox.utils.ToastUtils;
import com.znt.vodbox.utils.binding.Bind;
import com.znt.vodbox.view.searchview.ICallBack;
import com.znt.vodbox.view.searchview.SearchView;
import com.znt.vodbox.view.xlistview.LJListView;

import java.util.ArrayList;
import java.util.List;

public class SearchSysAlbumActivity extends BaseActivity implements
        LJListView.IXListViewListener, AdapterView.OnItemClickListener,OnMoreClickListener
{

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
    private TextView tvTopConfirm = null;
    @Bind(R.id.fab_my_album)
    FloatingActionButton fab = null;
    @Bind(R.id.ptrl_music_album)
    private LJListView listView = null;
    @Bind(R.id.search_view)
    private SearchView mSearchView = null;

    private List<AlbumInfo> albumInfos = new ArrayList<>();

    private MyAlbumlistAdapter mMyAlbumlistAdapter = null;

    private int pageNo = 1;
    private int pageSize = 25;
    private int maxSize = 0;

    private String typeId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_album);

        tvTopTitle.setText(getResources().getString(R.string.sys_album_search));
        ivTopMore.setVisibility(View.GONE);
        tvTopConfirm.setVisibility(View.GONE);
        tvTopTitleSub.setVisibility(View.VISIBLE);
        tvTopTitleSub.setText("全部");
        ivTopReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvTopConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SystemAlbumActivity.class);
                Bundle b = new Bundle();
                intent.putExtras(b);
                startActivityForResult(intent,1);
            }
        });

        viewTopTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MediaCategoryActivity.class);
                startActivityForResult(intent,2);
            }
        });

        fab.setVisibility(View.GONE);

        listView.getListView().setDivider(getResources().getDrawable(R.color.transparent));
        listView.getListView().setDividerHeight(1);
        listView.setPullLoadEnable(true,"");
        listView.setPullRefreshEnable(true);
        listView.setIsAnimation(true);
        listView.setXListViewListener(this);
        listView.showFootView(false);
        listView.setRefreshTime();
        listView.setOnItemClickListener(this);

        mMyAlbumlistAdapter = new MyAlbumlistAdapter(albumInfos);
        listView.setAdapter(mMyAlbumlistAdapter);

        mMyAlbumlistAdapter.setOnMoreClickListener(this);

        mSearchView.init("album_search_record.db");
        mSearchView.showRecordView(false);

        //listView.onFresh();

        mSearchView.setOnClickSearch(new ICallBack() {
            @Override
            public void SearchAciton(String string) {
                loadMyAlbums();
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                mSearchView.showRecordView(false);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

    }

    public void loadMyAlbums()
    {

        String name = mSearchView.getText().toString();
        if(TextUtils.isEmpty(name))
        {
            ToastUtils.show("请输入查找内容");
            listView.stopRefresh();
            return;
        }
        String token = Constant.mUserInfo.getToken();

        String merchId = Constant.mUserInfo.getMerchant().getId();
        //String merchId = mUserInfo.getMerchant().getId();

        try
        {
            // Simulate network access.
            HttpClient.getSystemAlbums(token, pageNo+"", pageSize+"",typeId,name, new HttpCallback<AlbumListResultBean>() {
                @Override
                public void onSuccess(AlbumListResultBean resultBean) {

                    if(resultBean != null)
                    {
                        if(pageNo == 1)
                            albumInfos.clear();
                        List<AlbumInfo> tempList = resultBean.getData();

                        if(tempList.size() == pageSize)
                            pageNo ++;

                        albumInfos.addAll(tempList);

                        mMyAlbumlistAdapter.notifyDataSetChanged(albumInfos);

                        if(!TextUtils.isEmpty(resultBean.getMessage()))
                            maxSize = Integer.parseInt(resultBean.getMessage());

                        tvTopTitle.setText(getResources().getString(R.string.sys_album) + "("+ maxSize + ")");
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
            typeId = tempInfor.getId();
            if(typeId == null)
                typeId = "";
            pageNo = 1;
            loadMyAlbums();
        }
    }

    @Override
    public void onRefresh() {
        pageNo = 1;
        loadMyAlbums();
    }

    @Override
    public void onLoadMore() {
        if(maxSize > albumInfos.size())
            loadMyAlbums();
        else
            showToast("没有更多数据了");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {

        if(position > 0)
            position = position - 1;
        AlbumInfo tempInfor = albumInfos.get(position);

        Intent intent = new Intent(getActivity(), AlbumMusicActivity.class);
        Bundle b = new Bundle();
        b.putSerializable("ALBUM_INFO",tempInfor);
        intent.putExtras(b);
        startActivity(intent);

    }

    @Override
    public void onMoreClick(int position) {
        final AlbumInfo tempInfo = albumInfos.get(position);
        showMusicOperationDialog(tempInfo);
    }

    private void showMusicOperationDialog(final AlbumInfo tempInfo)
    {
        new AlertView(tempInfo.getName(),null, "取消", null,
                getResources().getStringArray(R.array.sys_album_dialog),
                getActivity(), AlertView.Style.ActionSheet, new OnItemClickListener(){
            public void onItemClick(Object o,int which){
                switch (which) {
                    case 0://
                        collectAlbum(tempInfo.getId());
                        break;
                    case 1://
                        Intent intent = new Intent(getActivity(), AlbumMusicActivity.class);
                        Bundle b = new Bundle();
                        b.putSerializable("ALBUM_INFO",tempInfo);
                        b.putBoolean("IS_SYSTEM_ALBUM",true);
                        intent.putExtras(b);
                        startActivity(intent);
                        break;
                }
            }
        }).show();
    }

    public void collectAlbum(String albumId)
    {
        try
        {
            String token = Constant.mUserInfo.getToken();
            String merchId = Constant.mUserInfo.getMerchant().getId();

            HttpClient.collectAlbum(token, albumId, merchId, new HttpCallback<CommonCallBackBean>() {
                @Override
                public void onSuccess(CommonCallBackBean resultBean) {
                    if(resultBean != null)
                    {
                        finish();
                        /*int deleteCount = updateDeleteMusicList(musicIds);
                        tvTopTitle.setText(mAlbumInfo.getName() + "(" + (curMusicSize - deleteCount) + ")");
                        dismissDialog();*/
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

        }
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
