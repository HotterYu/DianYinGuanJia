package com.znt.vodbox.activity;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.vodbox.adapter.AlbumMusiclistAdapter;
import com.znt.vodbox.adapter.OnMoreClickListener;
import com.znt.vodbox.bean.MediaInfo;
import com.znt.vodbox.dialog.VolumeSetDialog;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.model.Shopinfo;
import com.znt.vodbox.utils.ViewUtils;
import com.znt.vodbox.utils.binding.Bind;
import com.znt.vodbox.view.xlistview.LJListView;

import java.util.ArrayList;
import java.util.List;

public class ShopDetailActivity extends BaseActivity implements
        LJListView.IXListViewListener, AdapterView.OnItemClickListener
        ,OnMoreClickListener,View.OnClickListener
{

    @Bind(R.id.tv_common_title)
    private TextView tvTopTitle = null;
    @Bind(R.id.iv_common_back)
    private ImageView ivTopReturn = null;
    @Bind(R.id.iv_common_more)
    private ImageView ivTopMore = null;

    @Bind(R.id.ptrl_shop_deatil_medias)
    private LJListView listView = null;

    private View viewHeader = null;
    private TextView tvShopName = null;
    private TextView tvShopGroup = null;
    private TextView tvCurPlayName = null;
    private TextView tvCurStatus = null;
    private TextView tvPushMedia = null;
    private TextView tvPlayPre = null;
    private TextView tvPlayNext = null;
    private TextView tvPlayStatus = null;
    private TextView tvPlayVolume = null;
    private TextView tvCurPlayCount = null;

    private Shopinfo mShopInfo = null;
    private List<MediaInfo> dataList = new ArrayList<>();

    private AlbumMusiclistAdapter mAlbumMusiclistAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);

        ivTopReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ivTopMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("SHOP_INFO", mShopInfo);
                ViewUtils.startActivity(getActivity(), ShopSettingActivity.class, bundle);
            }
        });

        ivTopMore.setImageResource(R.drawable.icon_top_right_more);

        mShopInfo = (Shopinfo) getIntent().getSerializableExtra("SHOP_INFO");

        tvTopTitle.setText(getResources().getString(R.string.dev_shop_detail));

        viewHeader = LayoutInflater.from(getActivity()).inflate(R.layout.view_shop_detail_header, null);
        tvShopName = (TextView)viewHeader.findViewById(R.id.tv_shop_detail_header_shop_name);
        tvShopGroup = (TextView)viewHeader.findViewById(R.id.tv_shop_detail_header_shop_group);
        tvCurPlayName = (TextView)viewHeader.findViewById(R.id.tv_shop_detail_header_song);
        tvCurStatus = (TextView)viewHeader.findViewById(R.id.tv_shop_detail_header_status);
        tvPushMedia = (TextView)viewHeader.findViewById(R.id.tv_shop_detail_push);
        tvPlayPre = (TextView)viewHeader.findViewById(R.id.tv_shop_detail_play_pre);
        tvPlayNext = (TextView)viewHeader.findViewById(R.id.tv_shop_detail_play_next);
        tvPlayStatus = (TextView)viewHeader.findViewById(R.id.tv_shop_detail_play_status);
        tvPlayVolume = (TextView)viewHeader.findViewById(R.id.tv_shop_detail_volume);
        tvCurPlayCount = (TextView)viewHeader.findViewById(R.id.tv_shop_detail_header_song_count);

        tvPushMedia.setOnClickListener(this);
        tvPlayPre.setOnClickListener(this);
        tvPlayNext.setOnClickListener(this);
        tvPlayStatus.setOnClickListener(this);
        tvPlayVolume.setOnClickListener(this);

        listView.addHeader(viewHeader);

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

        tvCurPlayCount.setText(getResources().getString(R.string.dev_shop_detail_cur_play_count) + "(0)");

        if(mShopInfo.getTmlRunStatus().size() > 0)
        {
            tvShopName.setText(mShopInfo.getTmlRunStatus().get(0).getShopname());
            if(mShopInfo.getGroup() != null && !TextUtils.isEmpty(mShopInfo.getGroup().getGroupName()))
                tvShopGroup.setText(getResources().getString(R.string.dev_group_belong_hint) + mShopInfo.getGroup().getGroupName());
            else
                tvShopGroup.setText(getResources().getString(R.string.dev_group_belong_none));
            tvCurPlayName.setText(mShopInfo.getTmlRunStatus().get(0).getPlayingSong());
            if(mShopInfo.getTmlRunStatus().get(0).getOnlineStatus().equals("1"))
            {
                tvCurStatus.setText(getResources().getString(R.string.dev_status_online));
            }
            else
            {
                tvCurStatus.setText(getResources().getString(R.string.dev_status_offline));
            }
        }
        else
        {
            tvTopTitle.setText(getResources().getString(R.string.dev_shop_none_device));
        }

        listView.onFresh();

    }

    public void getCurPlayMusics()
    {

        String token = Constant.mUserInfo.getToken();
        String pageNo = "1";
        String pageSize = "100";
        String merchId = Constant.mUserInfo.getMerchant().getId();
        //String merchId = mUserInfo.getMerchant().getId();
        try
        {
            // Simulate network access.
            /*HttpClient.getAlbumMusics(token, pageNo, pageSize,mAlbumInfo.getId(),searchWord, new HttpCallback<MusicListResultBean>() {
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
            });*/
        }
        catch (Exception e)
        {
            listView.stopRefresh();
        }

    }

    @Override
    public void onRefresh() {
        getCurPlayMusics();
    }

    @Override
    public void onLoadMore() {
        getCurPlayMusics();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if(position > 1)
            position = position - 2;

        MediaInfo tempInfor = dataList.get(position);
        showPlayDialog(tempInfor.getMusicName(),tempInfor.getMusicUrl(),tempInfor.getId());
    }

    @Override
    public void onMoreClick(int position) {
        MediaInfo tempInfo = dataList.get(position);
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(tempInfo.getMusicName());
        dialog.setItems(R.array.cur_play_music_dialog, (dialog1, which) -> {
            switch (which) {
                case 0://
                    Intent intent = new Intent(getActivity(), ShopSelectActivity.class);
                    Bundle b = new Bundle();
                    b.putString("MEDIA_NAME",tempInfo.getMusicName());
                    b.putString("MEDIA_ID",tempInfo.getId());
                    b.putString("MEDIA_URL",tempInfo.getMusicUrl());
                    intent.putExtras(b);
                    startActivity(intent);
                    //requestSetRingtone(music);
                    break;
                case 1://
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    // 将文本内容放到系统剪贴板里。
                    cm.setText(tempInfo.getMusicUrl());
                    showToast("复制成功");
                    break;
                case 2://
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

    private void showVolumeDialog(final Shopinfo devInfor)
    {
        final VolumeSetDialog playDialog = new VolumeSetDialog(getActivity(), devInfor);

        //playDialog.updateProgress("00:02:18 / 00:05:12");
        if(playDialog.isShowing())
            playDialog.dismiss();
        playDialog.show();
        playDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface arg0)
            {
                // TODO Auto-generated method stub
                boolean isVolumeUpdated = playDialog.isVolumeUpdated();
                tvPlayVolume.setText(getResources().getString(R.string.dev_shop_detail_volume_hint) + "(" + playDialog.getCurVolume() + " / 15)");
                mShopInfo.getTmlRunStatus().get(0).setVolume(playDialog.getCurVolume() + "");
            }
        });
        WindowManager windowManager = ((Activity) getActivity()).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = playDialog.getWindow().getAttributes();
        lp.width = (int)(display.getWidth()); //
        lp.height = (int)(display.getHeight()); //
        playDialog.getWindow().setAttributes(lp);
    }

    @Override
    public void onClick(View view)
    {
        if(view == tvPushMedia)
        {
            Bundle bundle = new Bundle();
            bundle.putSerializable("SHOP_INFO",mShopInfo);
            ViewUtils.startActivity(getActivity(),SearchSystemMusicActivity.class,bundle);
        }
        else if(view == tvPlayPre)
        {

        }
        else if(view == tvPlayNext)
        {

        }
        else if(view == tvPlayStatus)
        {

        }
        else if(view == tvPlayVolume)
        {
            showVolumeDialog(mShopInfo);
        }
    }
}
