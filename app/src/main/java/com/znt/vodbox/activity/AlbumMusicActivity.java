package com.znt.vodbox.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.znt.vodbox.R;
import com.znt.vodbox.adapter.AlbumMusiclistAdapter;
import com.znt.vodbox.adapter.OnMoreClickListener;
import com.znt.vodbox.bean.AlbumInfo;
import com.znt.vodbox.bean.CommonCallBackBean;
import com.znt.vodbox.bean.MediaInfo;
import com.znt.vodbox.bean.MusicListResultBean;
import com.znt.vodbox.entity.Config;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.http.HttpCallback;
import com.znt.vodbox.http.HttpClient;
import com.znt.vodbox.utils.FileUtils;
import com.znt.vodbox.utils.ViewUtils;
import com.znt.vodbox.utils.binding.Bind;
import com.znt.vodbox.view.searchview.ICallBack;
import com.znt.vodbox.view.searchview.SearchView;
import com.znt.vodbox.view.xlistview.LJListView;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class AlbumMusicActivity extends BaseActivity implements
        LJListView.IXListViewListener, AdapterView.OnItemClickListener,OnMoreClickListener
{

    @Bind(R.id.tv_common_title)
    private TextView tvTopTitle = null;
    @Bind(R.id.iv_common_back)
    private ImageView ivTopReturn = null;
    @Bind(R.id.iv_common_more)
    private ImageView ivTopMore = null;
    @Bind(R.id.tv_common_confirm)
    private TextView tvConfirm = null;

    @Bind(R.id.ptrl_album_music)
    private LJListView listView = null;
    @Bind(R.id.search_view)
    private SearchView mSearchView = null;

    @Bind(R.id.tv_bottom_operation_add)
    private TextView tvAdd = null;
    @Bind(R.id.tv_bottom_operation_delete)
    private TextView tvDelete = null;

    @Bind(R.id.fab_my_album_music)
    FloatingActionButton fab = null;
    @Bind(R.id.view_bottom_operation)
    View viewBottomOperation = null;

    private AlbumInfo mAlbumInfo = null;
    private boolean isSystemAlbum = false;

    private List<MediaInfo> dataList = new ArrayList<>();
    private AlbumMusiclistAdapter mAlbumMusiclistAdapter = null;

    private int pageNo = 1;
    private int pageSize = 25;
    private int curMusicSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_music);

        mAlbumInfo = (AlbumInfo) getIntent().getSerializableExtra("ALBUM_INFO");
        isSystemAlbum = getIntent().getBooleanExtra("IS_SYSTEM_ALBUM", false);

        mSearchView.init("album_music_record.db");
        mSearchView.showRecordView(false);

        tvTopTitle.setText(mAlbumInfo.getName());
        ivTopMore.setVisibility(View.GONE);
        tvConfirm.setVisibility(View.VISIBLE);
        if(isSystemAlbum)
            tvConfirm.setText("收藏");
        else
            tvConfirm.setText("添加");

        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                viewBottomOperation.setVisibility(View.VISIBLE);
                fab.setVisibility(View.GONE);
                mAlbumMusiclistAdapter.setSelect(true);
            }
        });

        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String musicIds = mAlbumMusiclistAdapter.getSelectedMediaIds();
                if(TextUtils.isEmpty(musicIds))
                    Toast.makeText(getActivity(),"请选择文件",Toast.LENGTH_SHORT).show();
                else
                    addSelectMusicsToAlbum(musicIds);
            }
        });
        tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String musicIds = mAlbumMusiclistAdapter.getSelectedMediaIds();
                if(TextUtils.isEmpty(musicIds))
                    Toast.makeText(getActivity(),"请选择文件",Toast.LENGTH_SHORT).show();
                else
                    deleteMusics(musicIds);
            }
        });

        ivTopReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isSystemAlbum)
                {
                    collectAlbum();
                }
                else
                {
                    Bundle bundle = new Bundle();
                    bundle.putString("ALBUM_ID", mAlbumInfo.getId());
                    ViewUtils.startActivity(getActivity(),SearchSystemMusicActivity.class,bundle);
                }
            }
        });

        mSearchView.setOnClickSearch(new ICallBack() {
            @Override
            public void SearchAciton(String string) {
                getAlbumMusics();
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

        mAlbumMusiclistAdapter = new AlbumMusiclistAdapter(dataList);
        listView.setAdapter(mAlbumMusiclistAdapter);

        mAlbumMusiclistAdapter.setOnMoreClickListener(this);


        listView.onFresh();

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

    public void getAlbumMusics()
    {

        String searchWord = mSearchView.getText().toString();
        String token = Constant.mUserInfo.getToken();

        String merchId = Constant.mUserInfo.getMerchant().getId();
        //String merchId = mUserInfo.getMerchant().getId();
        try
        {
            // Simulate network access.
            HttpClient.getAlbumMusics(token, pageNo+"", pageSize+"",mAlbumInfo.getId(),searchWord, new HttpCallback<MusicListResultBean>() {
                        @Override
                        public void onSuccess(MusicListResultBean resultBean) {

                            if(resultBean != null)
                            {

                                List<MediaInfo> tempList = resultBean.getData();

                                if(pageNo == 1)
                                    dataList.clear();

                                if(tempList.size() == pageSize)
                                    pageNo ++;

                                dataList.addAll(tempList);

                                mAlbumMusiclistAdapter.notifyDataSetChanged(dataList);
                                if(!TextUtils.isEmpty(resultBean.getMessage()))
                                    curMusicSize = Integer.parseInt(resultBean.getMessage());
                                tvTopTitle.setText(mAlbumInfo.getName() + "(" + curMusicSize + ")");

                            }
                            else
                            {
                                showToast(resultBean.getMessage());
                                //shopinfoList.clear();
                            }
                            mSearchView.showRecordView(false);
                            listView.stopRefresh();
                        }

                        @Override
                        public void onFail(Exception e) {
                            //vSearching.setVisibility(View.GONE);
                            listView.stopRefresh();
                            showToast(e.getMessage());
                        }
                    });
        }
        catch (Exception e)
        {
            listView.stopRefresh();
        }

    }

    public void deleteAlbumMusic(final String musicIds)
    {
        try
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

        }
    }

    public void collectAlbum()
    {
        try
        {
            String token = Constant.mUserInfo.getToken();
            String merchId = Constant.mUserInfo.getMerchant().getId();
            String albumId = mAlbumInfo.getId();

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

    private int updateDeleteMusicList(String musicIds)
    {
        String[] ids = musicIds.split(",");
        int len = ids.length;
        for(int i=0;i<len;i++)
        {
            String deleteMusicId = ids[i];
            for(int j=0;j<dataList.size();j++)
            {
                String id = dataList.get(j).getId();
                if(deleteMusicId.equals(id))
                {
                    dataList.remove(j);
                }
            }
        }
        mAlbumMusiclistAdapter.notifyDataSetChanged();
        return ids.length;
    }

    @Override
    public void onRefresh()
    {
        pageNo = 1;
        getAlbumMusics();
    }

    @Override
    public void onLoadMore()
    {
        if(curMusicSize > dataList.size())
            getAlbumMusics();
        else
            showToast("没有更多数据了");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if(position > 0)
            position = position - 1;

        MediaInfo tempInfo = dataList.get(position);

        if(FileUtils.isMusic(tempInfo.getMusicUrl()))
        {
            showPlayDialog(tempInfo.getMusicName(),tempInfo.getMusicUrl(),tempInfo.getId());
        }
        else
        {
            Bundle bundle = new Bundle();
            bundle.putString(Config.VIDEO_NAME, tempInfo.getMusicName());
            bundle.putString(Config.VIDEO_URL, tempInfo.getMusicUrl());
            bundle.putString(Config.VIDEO_ID, tempInfo.getId());
            ViewUtils.startActivity(this,VideoPlayActivity.class,bundle);
        }

    }

    @Override
    public void onMoreClick(int position) {
        MediaInfo tempInfo = dataList.get(position);
         if(isSystemAlbum)
            showSysMusicOperationDialog(tempInfo);
        else
            showMusicOperationDialog(tempInfo);
    }

    private void showSysMusicOperationDialog(final MediaInfo tempInfo)
    {
        new AlertView(tempInfo.getMusicName(),null, "取消", null,
                getResources().getStringArray(R.array.sys_album_music_dialog),
                getActivity(), AlertView.Style.ActionSheet, new OnItemClickListener(){
            public void onItemClick(Object o,int which){
                switch (which) {
                    case 0://
                        Intent i = new Intent(getActivity(), MyAlbumActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("MUSIC_IDS",tempInfo.getId());
                        i.putExtras(bundle);
                        startActivity(i);
                        break;
                    case 1://
                        Intent intent = new Intent(getActivity(), ShopSelectActivity.class);
                        Bundle b = new Bundle();
                        b.putString("MEDIA_NAME",tempInfo.getMusicName());
                        b.putString("MEDIA_ID",tempInfo.getId());
                        b.putString("MEDIA_URL",tempInfo.getMusicUrl());
                        intent.putExtras(b);
                        startActivity(intent);
                        //requestSetRingtone(music);
                        break;
                    case 2://
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        // 将文本内容放到系统剪贴板里。
                        cm.setText(tempInfo.getMusicName() + "\n" + URLDecoder.decode(tempInfo.getMusicUrl()));
                        showToast("复制成功");
                        break;
                }
            }
        }).show();
    }

    private void addSelectMusicsToAlbum(String musicIds)
    {
        //String musicIds = mAlbumMusiclistAdapter.getSelectedMediaIds();
        Intent i = new Intent(getActivity(), MyAlbumActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("MUSIC_IDS",musicIds);
        i.putExtras(bundle);
        startActivity(i);
    }

    private void deleteMusics(final String musicIds)
    {
        if(mAlertView != null)
            mAlertView.dismissImmediately();
        new AlertView("提示", "确定删除该文件吗？", "取消", new String[]{"确定"}, null, getActivity(), AlertView.Style.Alert, new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {
                if(position == 0)
                    deleteAlbumMusic(musicIds);
            }
        }).setCancelable(true).show();
    }

    private AlertView mAlertView = null;
    private void showMusicOperationDialog(final MediaInfo tempInfo)
    {
        mAlertView = new AlertView(tempInfo.getMusicName(),null, "取消", null,
                getResources().getStringArray(R.array.album_music_dialog),
                getActivity(), AlertView.Style.ActionSheet, new OnItemClickListener(){
            public void onItemClick(Object o,int which){
                switch (which) {
                    case 0://
                        addSelectMusicsToAlbum(tempInfo.getId());
                        break;
                    case 1://
                        Intent intent = new Intent(getActivity(), ShopSelectActivity.class);
                        Bundle b = new Bundle();
                        b.putString("MEDIA_NAME",tempInfo.getMusicName());
                        b.putString("MEDIA_ID",tempInfo.getId());
                        b.putString("MEDIA_URL",tempInfo.getMusicUrl());
                        intent.putExtras(b);
                        startActivity(intent);
                        //requestSetRingtone(music);
                        break;
                    case 2://
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        // 将文本内容放到系统剪贴板里。
                        cm.setText(tempInfo.getMusicName() + "\n" + URLDecoder.decode(tempInfo.getMusicUrl()));
                        showToast("复制成功");
                        break;
                    case 3://
                        deleteMusics(tempInfo.getId());
                        break;
                }
            }
        });mAlertView.show();
    }

    @Override
    public void onBackPressed()
    {
        if(mSearchView.isRecordViewShow())
        {
            mSearchView.showRecordView(false);
            return;
        }
        if(viewBottomOperation.isShown())
        {
            fab.setVisibility(View.VISIBLE);
            viewBottomOperation.setVisibility(View.GONE);
            mAlbumMusiclistAdapter.setSelect(false);
        }
        else
            super.onBackPressed();
    }
}
