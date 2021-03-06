package com.znt.vodbox.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.znt.vodbox.R;
import com.znt.vodbox.adapter.AlbumMusiclistAdapter;
import com.znt.vodbox.adapter.OnMoreClickListener;
import com.znt.vodbox.bean.CommonCallBackBean;
import com.znt.vodbox.bean.MediaInfo;
import com.znt.vodbox.bean.MusicListResultBean;
import com.znt.vodbox.bean.ShopInfoCallBackBean;
import com.znt.vodbox.dialog.VolumeSetBottomDialog;
import com.znt.vodbox.entity.LocalDataEntity;
import com.znt.vodbox.http.HttpCallback;
import com.znt.vodbox.http.HttpClient;
import com.znt.vodbox.model.Shopinfo;
import com.znt.vodbox.utils.ToastUtils;
import com.znt.vodbox.utils.ViewUtils;
import com.znt.vodbox.utils.binding.Bind;
import com.znt.vodbox.view.MyCycleView;
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
    private TextView tvPlayMode = null;
    private TextView tvPlayNext = null;
    private TextView tvPlayStatus = null;
    private TextView tvPlayVolume = null;
    private TextView tvPushList = null;
    private TextView tvPushRecord = null;
    private TextView tvCurPlayCount = null;

    private MyCycleView mMyCycleView = null;

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
        mMyCycleView = (MyCycleView)viewHeader.findViewById(R.id.mcv_play_icon);
        tvShopName = (TextView)viewHeader.findViewById(R.id.tv_shop_detail_header_shop_name);
        tvShopGroup = (TextView)viewHeader.findViewById(R.id.tv_shop_detail_header_shop_group);
        tvCurPlayName = (TextView)viewHeader.findViewById(R.id.tv_shop_detail_header_song);
        tvCurStatus = (TextView)viewHeader.findViewById(R.id.tv_shop_detail_header_status);
        tvPushMedia = (TextView)viewHeader.findViewById(R.id.tv_shop_detail_push);
        tvPlayMode = (TextView)viewHeader.findViewById(R.id.tv_shop_detail_play_mode);
        tvPlayNext = (TextView)viewHeader.findViewById(R.id.tv_shop_detail_play_next);
        tvPlayStatus = (TextView)viewHeader.findViewById(R.id.tv_shop_detail_play_status);
        tvPlayVolume = (TextView)viewHeader.findViewById(R.id.tv_shop_detail_volume);
        tvPushList = (TextView)viewHeader.findViewById(R.id.tv_shop_detail_push_list);
        tvPushRecord = (TextView)viewHeader.findViewById(R.id.tv_shop_detail_push_record);
        tvCurPlayCount = (TextView)viewHeader.findViewById(R.id.tv_shop_detail_header_song_count);

        mMyCycleView.initView();

        tvPushMedia.setOnClickListener(this);
        tvPlayMode.setOnClickListener(this);
        tvPlayNext.setOnClickListener(this);
        tvPlayStatus.setOnClickListener(this);
        tvPlayVolume.setOnClickListener(this);
        tvPushList.setOnClickListener(this);
        tvPushRecord.setOnClickListener(this);

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

        updateUi();

        getTerminalInfo();
        getCurPlayMusics();
    }

    private void updateUi()
    {
        if(mShopInfo.getTmlRunStatus() != null && mShopInfo.getTmlRunStatus().size() > 0)
        {
            tvShopName.setText(mShopInfo.getTmlRunStatus().get(0).getShopname());
            if(mShopInfo.getGroup() != null && !TextUtils.isEmpty(mShopInfo.getGroup().getGroupName()))
                tvShopGroup.setText(getResources().getString(R.string.dev_group_belong_hint) + mShopInfo.getGroup().getGroupName());
            else
                tvShopGroup.setText(getResources().getString(R.string.dev_group_belong_none));
            tvCurPlayName.setText(getResources().getString(R.string.dev_shop_play) + mShopInfo.getTmlRunStatus().get(0).getPlayingSong());
            if(mShopInfo.getTmlRunStatus().get(0).getOnlineStatus() != null
                    && mShopInfo.getTmlRunStatus().get(0).getOnlineStatus().equals("1"))
            {
                tvCurStatus.setText(getResources().getString(R.string.dev_status) + getResources().getString(R.string.dev_status_online));
                tvCurStatus.setTextColor(getResources().getColor(R.color.text_blue_on));
            }
            else
            {
                tvCurStatus.setText(getResources().getString(R.string.dev_status) + getResources().getString(R.string.dev_status_offline));
            }

            if(mShopInfo.getTmlRunStatus().get(0).getPlayModel() != null
                    && mShopInfo.getTmlRunStatus().get(0).getPlayModel().equals("0"))
                tvPlayMode.setText(getResources().getString(R.string.dev_shop_detail_play_mode_order));
            else
                tvPlayMode.setText(getResources().getString(R.string.dev_shop_detail_play_mode_radom));
        }
        else
        {
            tvTopTitle.setText(getResources().getString(R.string.dev_shop_none_device));
        }
    }

    public void getCurPlayMusics()
    {
        if(mShopInfo.getTmlRunStatus().size() <= 0)
        {
            listView.stopRefresh();
            return;
        }
        String token = LocalDataEntity.newInstance(getActivity()).getUserInfor().getToken();

        HttpClient.getCurPlayMusics(token, mShopInfo.getTmlRunStatus().get(0).getTerminalId(), new HttpCallback<MusicListResultBean>() {
            @Override
            public void onSuccess(MusicListResultBean musicListResultBean) {
                if(musicListResultBean.isSuccess())
                {
                    List<MediaInfo> mediaInfoList = musicListResultBean.getData();
                    if(mediaInfoList != null && mediaInfoList.size() > 0)
                    {
                        dataList.clear();
                        dataList.addAll(mediaInfoList);
                        mAlbumMusiclistAdapter.notifyDataSetChanged(dataList);
                        tvCurPlayCount.setText(getResources().getString(R.string.dev_shop_detail_cur_play_count) + "("+dataList.size()+")");
                    }
                }
                else
                    showToast("请求数据失败："+musicListResultBean.getMessage());
                listView.stopRefresh();
            }

            @Override
            public void onFail(Exception e) {
                showToast("请求数据失败");
                listView.stopRefresh();
            }
        });
    }

    public void playControll(String type, String value)
    {
        String token = LocalDataEntity.newInstance(getActivity()).getUserInfor().getToken();
        String terminalId = mShopInfo.getTmlRunStatus().get(0).getTerminalId();
        try
        {
            // Simulate network access.
            HttpClient.playControll(token, terminalId, value, type, new HttpCallback<CommonCallBackBean>() {
                @Override
                public void onSuccess(CommonCallBackBean resultBean) {

                    if(resultBean.isSuccess())
                    {

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
            showToast(e.getMessage());
            Log.e("",e.getMessage());
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRefresh() {
        getTerminalInfo();
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
        showMusicOperationDialog(tempInfo);
    }

    private void showMusicOperationDialog(final MediaInfo tempInfo)
    {
        new AlertView(tempInfo.getMusicName(),null, "取消", null,
                getResources().getStringArray(R.array.cur_play_music_dialog),
                getActivity(), AlertView.Style.ActionSheet, new OnItemClickListener(){
            public void onItemClick(Object o,int which){
                switch (which) {
                    case 0://
                        /*Intent intent = new Intent(getActivity(), ShopSelectActivity.class);
                        Bundle b = new Bundle();
                        b.putString("MEDIA_NAME",tempInfo.getMusicName());
                        b.putString("MEDIA_ID",tempInfo.getId());
                        b.putString("MEDIA_URL",tempInfo.getMusicUrl());
                        intent.putExtras(b);
                        startActivity(intent);*/
                        pushMedia(tempInfo.getId(),mShopInfo.getTmlRunStatus().get(0).getTerminalId(),"1");
                        //requestSetRingtone(music);
                        break;
                    case 1://
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        // 将文本内容放到系统剪贴板里。
                        cm.setText(tempInfo.getMusicUrl());
                        showToast("复制成功");
                        break;

                }
            }
        }).show();
    }

    private void showVolumeDialog(final Shopinfo devInfor)
    {

        VolumeSetBottomDialog mVolumeSetBottomDialog = new VolumeSetBottomDialog(getActivity());
        mVolumeSetBottomDialog.show("音量设置", devInfor, new VolumeSetBottomDialog.OnVolumeSetDismissResultListener() {
            @Override
            public void onConfirmDismiss(int volume) {
                mShopInfo.getTmlRunStatus().get(0).setVolume(volume + "");
                playControll("1",volume + "");
            }
        });

    }

    private void pushMedia(String dataId,String terminId, String mediaType)
    {
        //String type = "1";//1 歌曲， 2 广告
        //String dataId = mediaId;
        String userId = LocalDataEntity.newInstance(getActivity()).getUserInfor().getMerchant().getId();
        String pusherid = "";
        String pushername = LocalDataEntity.newInstance(getActivity()).getUserInfor().getNickName();

        try
        {
            // Simulate network access.
            HttpClient.pushMedia(terminId, mediaType, dataId, userId,pusherid,pushername, new HttpCallback<CommonCallBackBean>() {
                @Override
                public void onSuccess(CommonCallBackBean resultBean) {

                    if(resultBean != null)
                    {
                        if(resultBean.getResultcode().equals("0"))
                        {
                            ToastUtils.show(getResources().getString(R.string.push_fail)+":"+resultBean.getMessage());
                        }
                        else
                            ToastUtils.show(getResources().getString(R.string.push_success));

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

    public void getTerminalInfo()
    {
        try
        {
            String id = mShopInfo.getTmlRunStatus().get(0).getTerminalId();
            String token = LocalDataEntity.newInstance(getActivity()).getUserInfor().getToken();

            HttpClient.getShopInfo(token, id, new HttpCallback<ShopInfoCallBackBean>() {
                @Override
                public void onSuccess(ShopInfoCallBackBean resultBean) {
                    if(resultBean != null && resultBean.isSuccess())
                    {
                        mShopInfo = resultBean.getData();
                        updateUi();
                        dismissDialog();
                    }
                    else
                    {
                        showToast(resultBean.getMessage());
                    }
                }
                @Override
                public void onFail(Exception e) {
                    if(e != null)
                        showToast(e.getMessage());
                }
            });
        }
        catch (Exception e)
        {

        }
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
        else if(view == tvPlayMode)
        {
            if(mShopInfo.getTmlRunStatus().size() < 0)
                return;
            if(mShopInfo.getTmlRunStatus().get(0).getPlayModel().equals("0"))
                playControll("2","1");//0-顺序播放 1-随机播放
            else
                playControll("2","0");
        }
        else if(view == tvPlayNext)
        {
            playControll("2","2");
        }
        else if(view == tvPlayStatus)
        {
            playControll("2","");//0-播放 1-暂停
        }
        else if(view == tvPushList)
        {
            Bundle bundle = new Bundle();
            bundle.putSerializable("SHOP_INFO",mShopInfo);
            ViewUtils.startActivity(getActivity(),PushListActivity.class,bundle);
        }
        else if(view == tvPushRecord)
        {
            Bundle bundle = new Bundle();
            bundle.putSerializable("SHOP_INFO",mShopInfo);
            ViewUtils.startActivity(getActivity(),PushListActivity.class,bundle);
        }
        else if(view == tvPlayVolume)
        {
            showVolumeDialog(mShopInfo);
        }
    }
}
