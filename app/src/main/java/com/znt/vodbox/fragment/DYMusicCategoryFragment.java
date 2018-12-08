package com.znt.vodbox.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.znt.vodbox.R;
import com.znt.vodbox.activity.AddShopActivity;
import com.znt.vodbox.activity.AlbumMusicActivity;
import com.znt.vodbox.activity.MusicActivity;
import com.znt.vodbox.adapter.DYMusicAlbumAdapter;
import com.znt.vodbox.adapter.LoadMoreWrapper;
import com.znt.vodbox.adapter.OnMoreClickListener;
import com.znt.vodbox.bean.AlbumInfo;
import com.znt.vodbox.bean.AlbumListResultBean;
import com.znt.vodbox.bean.CommonCallBackBean;
import com.znt.vodbox.constants.RequestCode;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.entity.LocalDataEntity;
import com.znt.vodbox.http.HttpCallback;
import com.znt.vodbox.http.HttpClient;
import com.znt.vodbox.model.UserInfo;
import com.znt.vodbox.utils.ToastUtils;
import com.znt.vodbox.utils.ViewUtils;
import com.znt.vodbox.utils.binding.Bind;
import com.znt.vodbox.view.StaggeredGridRecyclerView;
import com.znt.vodbox.view.listener.EndlessRecyclerOnScrollListener;

import java.util.ArrayList;
import java.util.List;


public class DYMusicCategoryFragment extends BaseFragment implements OnMoreClickListener {
    @Bind(R.id.rv)
    private StaggeredGridRecyclerView mRecyclerView;
    @Bind(R.id.refresh)
    private SwipeRefreshLayout swipeRefreshLayout;

    @Bind(R.id.fab)
    FloatingActionButton fab = null;

    private LoadMoreWrapper loadMoreWrapper;

    private List<AlbumInfo> dataList = new ArrayList<>();

    private UserInfo mUserInfo = null;

    private MusicActivity.OnCountGetCallBack mOnCountGetCallBack = null;

    private int pageNo = 1;
    private int pageSize = 25;
    private int maxSize = 0;
    private String typeId = "";

    /*private static ShopFragment INSTANCE = null;
    public static ShopFragment newInstance()
    {
        if(INSTANCE == null)
            INSTANCE = new ShopFragment();
        return INSTANCE;
    }*/

    public void setTypeId(String typeId)
    {
        this.typeId = typeId;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_shop, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 设置下拉进度的背景颜色，默认就是白色的
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        // 设置下拉进度的主题颜色
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.main_bg, R.color.colorPrimaryDark);

        DYMusicAlbumAdapter mDYMusicAlbumAdapter = new DYMusicAlbumAdapter(getContext(), dataList);
        loadMoreWrapper = new LoadMoreWrapper(mDYMusicAlbumAdapter);
        mDYMusicAlbumAdapter.setOnMoreClickListener(this);
        mDYMusicAlbumAdapter.setOnItemClickListener(new DYMusicAlbumAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                int index = (int) view.getTag();
                AlbumInfo tempInfor = dataList.get(index);

                Intent intent = new Intent(getActivity(), AlbumMusicActivity.class);
                Bundle b = new Bundle();
                b.putSerializable("ALBUM_INFO",tempInfor);
                b.putBoolean("IS_SYSTEM_ALBUM",true);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(loadMoreWrapper);

        fab.setVisibility(View.GONE);

        // 设置下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadMoreWrapper.showFooterView(false);
                // 刷新数据
                if(dataList != null && dataList.size() > 0)
                    dataList.clear();
                pageNo = 1;
                getData();
            }
        });

        // 设置加载更多监听
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                loadMoreWrapper.setLoadState(loadMoreWrapper.LOADING);

                if (dataList.size() < 52)
                {

                }
                else
                {
                    // 显示加载到底的提示
                    loadMoreWrapper.setLoadState(loadMoreWrapper.LOADING_END);
                }
            }
        });

    }

    @Override
    protected void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
        getData();

    }

    public void setOnCountGetCallBack(MusicActivity.OnCountGetCallBack mOnCountGetCallBack)
    {
        this.mOnCountGetCallBack = mOnCountGetCallBack;

    }
    public void getData()
    {
        if(swipeRefreshLayout == null)
            return;

        if(pageNo == 1)
        {
            swipeRefreshLayout.setRefreshing(true);
            loadMoreWrapper.setLoadState(loadMoreWrapper.LOADING_COMPLETE);
        }
        else
        {
            swipeRefreshLayout.setRefreshing(false);
            loadMoreWrapper.setLoadState(loadMoreWrapper.LOADING_END);
        }

        if(mUserInfo == null)
            mUserInfo = LocalDataEntity.newInstance(getContext()).getUserInfor();

        String token = mUserInfo.getToken();
        String name = "";

        try
        {
            // Simulate network access.
            HttpClient.getSystemAlbums(token, pageNo + "", pageSize + "",typeId,name
                    , new HttpCallback<AlbumListResultBean>() {
                @Override
                public void onSuccess(AlbumListResultBean resultBean) {

                    if(resultBean.getResultcode().equals("1"))
                    {
                        List<AlbumInfo> tempList = resultBean.getData();

                        if(pageNo == 1)
                            dataList.clear();

                        if(tempList.size() <= pageSize)
                            pageNo ++;

                        dataList.addAll(tempList);
                        if(maxSize == 0)
                            maxSize = Integer.parseInt(resultBean.getMessage());

                        if(mOnCountGetCallBack != null)
                            mOnCountGetCallBack.onCountGetBack(resultBean.getMessage());
                        loadMoreWrapper.notifyDataSetChanged();
                    }
                    else
                    {
                        showToast(resultBean.getMessage());
                    }
                    refreshUi();
                }

                @Override
                public void onFail(Exception e) {
                    refreshUi();
                    if(e != null)
                        showToast(e.getMessage());
                    else
                        showToast("加载数据失败");
                }
            });
        }
        catch (Exception e)
        {
            refreshUi();
            if(e != null)
                showToast(e.getMessage());
            else
                showToast("加载数据失败");
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
                        //finish();
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

    private void refreshUi()
    {
        if(loadMoreWrapper != null)
            loadMoreWrapper.notifyDataSetChanged();
        if(swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
        if(loadMoreWrapper != null)
            loadMoreWrapper.setLoadState(loadMoreWrapper.LOADING_END);
    }
    @Override
    public void onMoreClick(final int position) {
        AlbumInfo tempInfo = dataList.get(position);
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.REQUEST_WRITE_SETTINGS) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.System.canWrite(getContext())) {
                ToastUtils.show(R.string.grant_permission_setting);
            }
        }
    }

}
