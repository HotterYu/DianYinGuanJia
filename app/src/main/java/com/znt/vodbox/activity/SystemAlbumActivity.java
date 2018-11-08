package com.znt.vodbox.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.vodbox.adapter.MyAlbumlistAdapter;
import com.znt.vodbox.adapter.OnMoreClickListener;
import com.znt.vodbox.bean.AlbumInfo;
import com.znt.vodbox.bean.AlbumListResultBean;
import com.znt.vodbox.bean.CommonCallBackBean;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.http.HttpCallback;
import com.znt.vodbox.http.HttpClient;
import com.znt.vodbox.utils.binding.Bind;
import com.znt.vodbox.view.searchview.ICallBack;
import com.znt.vodbox.view.searchview.SearchView;
import com.znt.vodbox.view.xlistview.LJListView;

import java.util.ArrayList;
import java.util.List;

public class SystemAlbumActivity extends BaseActivity implements
        LJListView.IXListViewListener, AdapterView.OnItemClickListener,OnMoreClickListener
{

    @Bind(R.id.tv_common_title)
    private TextView tvTopTitle = null;
    @Bind(R.id.iv_common_back)
    private ImageView ivTopReturn = null;
    @Bind(R.id.iv_common_more)
    private ImageView ivTopMore = null;

    @Bind(R.id.ptrl_music_album)
    private LJListView listView = null;
    @Bind(R.id.search_view)
    private SearchView mSearchView = null;

    private List<AlbumInfo> albumInfos = new ArrayList<>();

    private MyAlbumlistAdapter mMyAlbumlistAdapter = null;

    private boolean isSystemAlbum = true;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_album);

        tvTopTitle.setText(getResources().getString(R.string.sys_album));
        ivTopMore.setVisibility(View.GONE);
        ivTopReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

        mMyAlbumlistAdapter = new MyAlbumlistAdapter(albumInfos);
        listView.setAdapter(mMyAlbumlistAdapter);

        mMyAlbumlistAdapter.setOnMoreClickListener(this);

        mSearchView.init("sys_album_record.db");
        mSearchView.showRecordView(false);

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


        listView.onFresh();

    }

    public void loadMyAlbums()
    {

        String name = mSearchView.getText().toString();
        String token = Constant.mUserInfo.getToken();
        String pageNo = "1";
        String pageSize = "20";
        String merchId = Constant.mUserInfo.getMerchant().getId();
        //String merchId = mUserInfo.getMerchant().getId();
        String typeId = "";

        try
        {
            // Simulate network access.
            HttpClient.getSystemAlbums(token, pageNo, pageSize,typeId,name, new HttpCallback<AlbumListResultBean>() {
                        @Override
                        public void onSuccess(AlbumListResultBean resultBean) {

                            if(resultBean != null)
                            {
                                albumInfos = resultBean.getData();
                                mMyAlbumlistAdapter.notifyDataSetChanged(albumInfos);
                                tvTopTitle.setText(getResources().getString(R.string.sys_album) + "("+resultBean.getMessage() + ")");
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK)
            return;
        if(requestCode == 1)
        {
            listView.onFresh();
        }
    }

    @Override
    public void onRefresh() {
        loadMyAlbums();
    }

    @Override
    public void onLoadMore() {
        loadMyAlbums();
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
        b.putBoolean("IS_SYSTEM_ALBUM",true);
        intent.putExtras(b);
        startActivity(intent);

    }

    @Override
    public void onMoreClick(int position) {
        AlbumInfo tempInfo = albumInfos.get(position);
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(tempInfo.getName());
        dialog.setItems(R.array.sys_album_dialog, (dialog1, which) -> {
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
        });
        dialog.show();
    }

    public void deleteAlbum(String id)
    {
        String token = Constant.mUserInfo.getToken();
        try
        {
            // Simulate network access.
            HttpClient.deleteAlbum(token, id, new HttpCallback<CommonCallBackBean>() {
                @Override
                public void onSuccess(CommonCallBackBean resultBean) {

                    if(resultBean != null)
                    {
                        loadMyAlbums();
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
