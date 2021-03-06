package com.znt.vodbox.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.vodbox.adapter.AlbumMusiclistAdapter;
import com.znt.vodbox.adapter.OnMoreClickListener;
import com.znt.vodbox.bean.CommonCallBackBean;
import com.znt.vodbox.bean.MediaInfo;
import com.znt.vodbox.bean.MusicListResultBean;
import com.znt.vodbox.entity.LocalDataEntity;
import com.znt.vodbox.enums.LoadStateEnum;
import com.znt.vodbox.executor.DownloadSearchedMusic;
import com.znt.vodbox.executor.ShareOnlineMusic;
import com.znt.vodbox.http.HttpCallback;
import com.znt.vodbox.http.HttpClient;
import com.znt.vodbox.model.SearchMusic;
import com.znt.vodbox.model.Shopinfo;
import com.znt.vodbox.utils.ToastUtils;
import com.znt.vodbox.utils.ViewUtils;
import com.znt.vodbox.utils.binding.Bind;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class SearchSystemMusicActivity_old extends BaseActivity implements SearchView.OnQueryTextListener
        , AdapterView.OnItemClickListener, OnMoreClickListener {
    @Bind(R.id.lv_search_music_list)
    private ListView lvSearchMusic;
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

        albumId = getIntent().getStringExtra("ALBUM_ID");
        mShopinfo = (Shopinfo)getIntent().getSerializableExtra("SHOP_INFO");

    }

    @Override
    protected void onServiceBound() {
        lvSearchMusic.setAdapter(mAlbumMusiclistAdapter);
        TextView tvLoadFail = llLoadFail.findViewById(R.id.tv_load_fail_text);
        tvLoadFail.setText(R.string.search_empty);

        lvSearchMusic.setOnItemClickListener(this);
        mAlbumMusiclistAdapter.setOnMoreClickListener(this);
    }

    @Override
    protected int getDarkTheme() {
        return R.style.AppThemeDark_Search;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_music, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.onActionViewExpanded();
        searchView.setQueryHint(getString(R.string.search_tips));
        searchView.setOnQueryTextListener(this);
        searchView.setSubmitButtonEnabled(true);
        try {
            Field field = searchView.getClass().getDeclaredField("mGoButton");
            field.setAccessible(true);
            ImageView mGoButton = (ImageView) field.get(searchView);
            mGoButton.setImageResource(R.drawable.ic_menu_search);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        ViewUtils.changeViewState(lvSearchMusic, llLoading, llLoadFail, LoadStateEnum.LOADING);
        searchMusic(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    private void searchMusic(String keyword) {
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
                        ViewUtils.changeViewState(lvSearchMusic, llLoading, llLoadFail, LoadStateEnum.LOAD_SUCCESS);

                        dataList = resultBean.getData();
                        mAlbumMusiclistAdapter.notifyDataSetChanged(dataList);
                        //tvTopTitle.setText(mAlbumInfo.getName() + "(" + resultBean.getMessage() + ")");
                        lvSearchMusic.requestFocus();
                    }
                    else
                    {
                        ViewUtils.changeViewState(lvSearchMusic, llLoading, llLoadFail, LoadStateEnum.LOAD_FAIL);
                    }
                    //listView.stopRefresh();
                }

                @Override
                public void onFail(Exception e) {
                    ViewUtils.changeViewState(lvSearchMusic, llLoading, llLoadFail, LoadStateEnum.LOAD_FAIL);
                    //vSearching.setVisibility(View.GONE);
                    //listView.stopRefresh();
                }
            });
        }
        catch (Exception e)
        {
            ViewUtils.changeViewState(lvSearchMusic, llLoading, llLoadFail, LoadStateEnum.LOAD_FAIL);
            //listView.stopRefresh();
        }
    }

    private void pushMedia(String terminId)
    {
        String type = "1";
        String dataId = "";
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

        MediaInfo tempInfor = dataList.get(position);

        if(mShopinfo == null)
        {
            showPlayDialog(tempInfor.getMusicName(), tempInfor.getMusicUrl(), tempInfor.getId());
        }
        else
        {
            showPlayDialog(tempInfor.getMusicName(), tempInfor.getMusicUrl(), tempInfor.getId(), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pushMedia(mShopinfo.getTmlRunStatus().get(0).getTerminalId());
                    dismissDialog();
                }
            });

        }
    }

    @Override
    public void onMoreClick(int position) {
        final MediaInfo tempInfo = dataList.get(position);
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(tempInfo.getMusicName());
        dialog.setItems(R.array.album_music_dialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog1, int which) {
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
                            pushMedia(mShopinfo.getTmlRunStatus().get(0).getTerminalId());
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
        });
        dialog.show();
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

    private void share(SearchMusic.Song song) {
        new ShareOnlineMusic(this, song.getSongname(), song.getSongid()) {
            @Override
            public void onPrepare() {
                showProgress();
            }

            @Override
            public void onExecuteSuccess(Void aVoid) {
                cancelProgress();
            }

            @Override
            public void onExecuteFail(Exception e) {
                cancelProgress();
            }
        }.execute();
    }

    private void download(final SearchMusic.Song song) {
        new DownloadSearchedMusic(this, song) {
            @Override
            public void onPrepare() {
                showProgress();
            }

            @Override
            public void onExecuteSuccess(Void aVoid) {
                cancelProgress();
                ToastUtils.show(getString(R.string.now_download, song.getSongname()));
            }

            @Override
            public void onExecuteFail(Exception e) {
                cancelProgress();
                ToastUtils.show(R.string.unable_to_download);
            }
        }.execute();
    }
}
