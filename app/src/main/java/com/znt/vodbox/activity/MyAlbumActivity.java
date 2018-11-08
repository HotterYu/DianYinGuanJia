package com.znt.vodbox.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
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
import com.znt.vodbox.bean.TypeInfo;
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

public class MyAlbumActivity extends BaseActivity implements
        LJListView.IXListViewListener, AdapterView.OnItemClickListener,OnMoreClickListener
{

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

    private String musicIds = "";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_album);

        //mUserInfo = (UserInfo) getIntent().getSerializableExtra("USER_INFO");
        musicIds = getIntent().getStringExtra("MUSIC_IDS");

        tvTopTitle.setText(getResources().getString(R.string.my_album));
        ivTopMore.setVisibility(View.GONE);
        tvTopConfirm.setVisibility(View.VISIBLE);
        tvTopConfirm.setText("添加");
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

        tvTopTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TypeActivity.class);
                Bundle b = new Bundle();
                b.putString("TYPE","0");
                intent.putExtras(b);
                startActivityForResult(intent,2);
            }
        });

        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getActivity(), ModifyAlbumActivity.class);
                Bundle b = new Bundle();
                intent.putExtras(b);
                startActivityForResult(intent,1);
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

        mSearchView.init("album_search_record.db");
        mSearchView.showRecordView(false);

        listView.onFresh();

        mSearchView.setOnClickSearch(new ICallBack() {
            @Override
            public void SearchAciton(String string) {
                loadMyAlbums("");
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

    public void loadMyAlbums(String typeId)
    {

        String name = mSearchView.getText().toString();
        String token = Constant.mUserInfo.getToken();
        String pageNo = "1";
        String pageSize = "20";
        String merchId = Constant.mUserInfo.getMerchant().getId();
        //String merchId = mUserInfo.getMerchant().getId();

        try
        {
            // Simulate network access.
            HttpClient.getMyAlbums(token, pageNo, pageSize,merchId,typeId,name, new HttpCallback<AlbumListResultBean>() {
                        @Override
                        public void onSuccess(AlbumListResultBean resultBean) {

                            if(resultBean != null)
                            {
                                albumInfos = resultBean.getData();
                                mMyAlbumlistAdapter.notifyDataSetChanged(albumInfos);
                                tvTopTitle.setText(getResources().getString(R.string.my_album) + "("+resultBean.getMessage() + ")");
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

            loadMyAlbums(tempInfor.getId());
        }
    }

    @Override
    public void onRefresh() {
        loadMyAlbums("");
    }

    @Override
    public void onLoadMore() {
        loadMyAlbums("");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {

        if(position > 0)
            position = position - 1;
        AlbumInfo tempInfor = albumInfos.get(position);

        if(TextUtils.isEmpty(musicIds))
        {
            Intent intent = new Intent(getActivity(), AlbumMusicActivity.class);
            Bundle b = new Bundle();
            b.putSerializable("ALBUM_INFO",tempInfor);
            intent.putExtras(b);
            startActivity(intent);
        }
        else
        {
            addMusicToAlbum(tempInfor.getId());
        }

    }

    @Override
    public void onMoreClick(int position) {
        AlbumInfo tempInfo = albumInfos.get(position);
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(tempInfo.getName());
        dialog.setItems(R.array.my_album_dialog, (dialog1, which) -> {
            switch (which) {
                case 0://
                    Intent intent = new Intent(getActivity(), ModifyAlbumActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable("ALBUM_INFO",tempInfo);
                    intent.putExtras(b);
                    startActivityForResult(intent,1);
                    break;
                case 1://
                    Bundle bundle = new Bundle();
                    bundle.putString("ALBUM_ID", tempInfo.getId());
                    ViewUtils.startActivity(getActivity(),SearchSystemMusicActivity.class,bundle);
                    break;
                case 2://
                    deleteAlbum(tempInfo.getId());
                    break;
                case 3://
                    //deleteMusic(music);
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
                        loadMyAlbums("");
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

    public void addMusicToAlbum(String id)
    {
        String token = Constant.mUserInfo.getToken();
        try
        {
            // Simulate network access.
            HttpClient.addMusicToAlbum(token, id,musicIds, new HttpCallback<CommonCallBackBean>() {
                @Override
                public void onSuccess(CommonCallBackBean resultBean) {

                    if(resultBean != null)
                    {
                        finish();
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
