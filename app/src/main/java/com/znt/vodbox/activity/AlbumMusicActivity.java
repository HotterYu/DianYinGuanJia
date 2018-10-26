package com.znt.vodbox.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.znt.vodbox.R;
import com.znt.vodbox.adapter.AdListAdapter;
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

    @Bind(R.id.ptrl_album_music)
    private LJListView listView = null;

    private AlbumInfo mAlbumInfo = null;

    private List<MediaInfo> dataList = new ArrayList<>();


    private AlbumMusiclistAdapter mAlbumMusiclistAdapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_music);

        mAlbumInfo = (AlbumInfo) getIntent().getSerializableExtra("ALBUM_INFO");

        tvTopTitle.setText(mAlbumInfo.getName());
        ivTopMore.setVisibility(View.VISIBLE);
        ivTopMore.setImageResource(R.drawable.ic_menu_search);
        ivTopReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ivTopMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ViewUtils.startActivity(getActivity(),SearchMusicActivity.class,null);

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



    }

    public void getAlbumMusics()
    {


        String token = Constant.mUserInfo.getToken();
        String pageNo = "1";
        String pageSize = "100";
        String merchId = Constant.mUserInfo.getMerchant().getId();
        //String merchId = mUserInfo.getMerchant().getId();
        String searchWord = "";

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
                            listView.stopRefresh();
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
        getAlbumMusics();
    }

    @Override
    public void onLoadMore() {
        getAlbumMusics();
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
                    //shareMusic(music);
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
                    //MusicInfoActivity.start(getContext(), music);
                    break;
                case 3://
                    //deleteMusic(music);
                    break;
            }
        });
        dialog.show();
    }
}
