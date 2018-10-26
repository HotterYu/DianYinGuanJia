package com.znt.vodbox.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.vodbox.adapter.AlbumSelectAdapter;
import com.znt.vodbox.adapter.OnMoreClickListener;
import com.znt.vodbox.bean.AlbumInfo;
import com.znt.vodbox.bean.AlbumListResultBean;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.http.HttpCallback;
import com.znt.vodbox.http.HttpClient;
import com.znt.vodbox.utils.binding.Bind;
import com.znt.vodbox.view.xlistview.LJListView;

import java.util.ArrayList;
import java.util.List;

public class AlbumSelectActivity extends BaseActivity implements
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

    private List<AlbumInfo> albumInfos = new ArrayList<>();

    private AlbumSelectAdapter adapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_album);

        //mUserInfo = (UserInfo) getIntent().getSerializableExtra("USER_INFO");

        tvTopTitle.setText(getResources().getString(R.string.select_album));
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

        adapter = new AlbumSelectAdapter(getActivity(),albumInfos);
        listView.setAdapter(adapter);

        //adapter.setOnMoreClickListener(this);

        listView.onFresh();

    }

    public void loadMyAlbums()
    {


        String token = Constant.mUserInfo.getToken();
        String pageNo = "1";
        String pageSize = "20";
        String merchId = Constant.mUserInfo.getMerchant().getId();
        //String merchId = mUserInfo.getMerchant().getId();
        String typeId = "";
        String name = "";

        try
        {
            // Simulate network access.
            HttpClient.getMyAlbums(token, pageNo, pageSize,merchId,typeId,name, new HttpCallback<AlbumListResultBean>() {
                        @Override
                        public void onSuccess(AlbumListResultBean resultBean) {

                            if(resultBean != null)
                            {
                                albumInfos = resultBean.getData();
                                adapter.notifyDataSetChanged(albumInfos);

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
        intent.putExtras(b);
        startActivity(intent);
    }

    @Override
    public void onMoreClick(int position) {
        AlbumInfo tempInfo = albumInfos.get(position);
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(tempInfo.getName());
        dialog.setItems(R.array.my_album_dialog, (dialog1, which) -> {
            switch (which) {
                case 0://
                    //shareMusic(music);
                    break;
                case 1://
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
