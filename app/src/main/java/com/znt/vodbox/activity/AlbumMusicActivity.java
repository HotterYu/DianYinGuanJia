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
import com.znt.vodbox.adapter.AlbumMusiclistAdapter;
import com.znt.vodbox.adapter.OnMoreClickListener;
import com.znt.vodbox.bean.AlbumInfo;
import com.znt.vodbox.bean.MediaInfo;
import com.znt.vodbox.bean.MusicListResultBean;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.http.HttpCallback;
import com.znt.vodbox.http.HttpClient;
import com.znt.vodbox.utils.ViewUtils;
import com.znt.vodbox.utils.binding.Bind;
import com.znt.vodbox.view.searchview.ICallBack;
import com.znt.vodbox.view.searchview.SearchView;
import com.znt.vodbox.view.xlistview.LJListView;

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


    private AlbumInfo mAlbumInfo = null;

    private List<MediaInfo> dataList = new ArrayList<>();


    private AlbumMusiclistAdapter mAlbumMusiclistAdapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_music);

        mAlbumInfo = (AlbumInfo) getIntent().getSerializableExtra("ALBUM_INFO");

        mSearchView.init("album_music_record.db");
        mSearchView.showRecordView(false);

        tvTopTitle.setText(mAlbumInfo.getName());
        ivTopMore.setVisibility(View.GONE);
        tvConfirm.setVisibility(View.VISIBLE);
        tvConfirm.setText("添加");

        ivTopReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("ALBUM_ID", mAlbumInfo.getId());
                ViewUtils.startActivity(getActivity(),SearchSystemMusicActivity.class,bundle);

            }
        });

        mSearchView.setOnClickSearch(new ICallBack() {
            @Override
            public void SearchAciton(String string) {
                getAlbumMusics(string);
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

    public void getAlbumMusics(String searchWord)
    {


        String token = Constant.mUserInfo.getToken();
        String pageNo = "1";
        String pageSize = "100";
        String merchId = Constant.mUserInfo.getMerchant().getId();
        //String merchId = mUserInfo.getMerchant().getId();
        try
        {
            // Simulate network access.
            HttpClient.getAlbumMusics(token, pageNo, pageSize,mAlbumInfo.getId(),searchWord, new HttpCallback<MusicListResultBean>() {
                        @Override
                        public void onSuccess(MusicListResultBean resultBean) {

                            if(resultBean != null)
                            {
                                dataList = resultBean.getData();
                                mAlbumMusiclistAdapter.notifyDataSetChanged(dataList);
                                tvTopTitle.setText(mAlbumInfo.getName() + "(" + resultBean.getMessage() + ")");
                                mSearchView.showRecordView(false);
                            }
                            else
                            {
                                showToast(resultBean.getMessage());
                                //shopinfoList.clear();
                            }
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

    @Override
    public void onRefresh() {
        getAlbumMusics("");
    }

    @Override
    public void onLoadMore() {
        getAlbumMusics("");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if(position > 0)
            position = position - 1;

        MediaInfo tempInfor = dataList.get(position);
        showPlayDialog(tempInfor.getMusicName(),tempInfor.getMusicUrl(),tempInfor.getId());
    }

    @Override
    public void onMoreClick(int position) {
        MediaInfo tempInfo = dataList.get(position);
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(tempInfo.getMusicName());
        dialog.setItems(R.array.album_music_dialog, (dialog1, which) -> {
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
                    cm.setText(tempInfo.getMusicUrl());
                    showToast("复制成功");
                    break;
                case 3://
                    showAlertDialog(getActivity(), new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            //deleteMusic(music);
                        }
                    }, "", "确定删除该文件吗?");
                    break;
            }
        });
        dialog.show();
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
