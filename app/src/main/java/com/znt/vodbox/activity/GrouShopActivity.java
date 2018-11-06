package com.znt.vodbox.activity;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.vodbox.adapter.OnMoreClickListener;
import com.znt.vodbox.adapter.ShoplistAdapter;
import com.znt.vodbox.bean.CommonCallBackBean;
import com.znt.vodbox.bean.GroupInfo;
import com.znt.vodbox.bean.ShopListResultBean;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.http.HttpCallback;
import com.znt.vodbox.http.HttpClient;
import com.znt.vodbox.model.Shopinfo;
import com.znt.vodbox.utils.ToastUtils;
import com.znt.vodbox.utils.ViewUtils;
import com.znt.vodbox.utils.binding.Bind;
import com.znt.vodbox.view.searchview.ICallBack;
import com.znt.vodbox.view.searchview.SearchView;
import com.znt.vodbox.view.xlistview.LJListView;

import java.util.ArrayList;
import java.util.List;

public class GrouShopActivity extends BaseActivity  implements
        LJListView.IXListViewListener, AdapterView.OnItemClickListener, OnMoreClickListener{

    @Bind(R.id.tv_common_title)
    private TextView tvTopTitle = null;
    @Bind(R.id.iv_common_back)
    private ImageView ivTopReturn = null;
    @Bind(R.id.iv_common_more)
    private ImageView ivTopMore = null;

    @Bind(R.id.ptrl_shop_select)
    private LJListView listView = null;
    @Bind(R.id.search_view)
    private SearchView mSearchView = null;

    private GroupInfo mGroupInfo;

    private List<Shopinfo> shopinfoList = new ArrayList<>();

    private ShoplistAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_select);


        ivTopMore.setVisibility(View.GONE);
        ivTopReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mGroupInfo = (GroupInfo)getIntent().getSerializableExtra("GROUP_INFO");
        tvTopTitle.setText(mGroupInfo.getGroupName());

        mSearchView.init("shop_record.db");
        mSearchView.showRecordView(false);

        mSearchView.setOnClickSearch(new ICallBack() {
            @Override
            public void SearchAciton(String string) {
                loadShops();
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

        adapter = new ShoplistAdapter(getActivity(),shopinfoList);
        listView.setAdapter(adapter);
        adapter.setOnMoreClickListener(this);

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

    public void loadShops()
    {
        String text = mSearchView.getText();

        String token = Constant.mUserInfo.getToken();
        String pageNo = "1";
        String pageSize = "100";
        String merchId = "";
        //String merchId = mUserInfo.getMerchant().getId();
        String groupId = mGroupInfo.getId();
        String memberId = "";
        String shopCode = "";
        String userShopCode = "";
        String name = text;

        /*if(StringUtils.isNumeric(text))
        {
            userShopCode = text;
        }
        else
            name = text;*/
        try
        {
            // Simulate network access.
            HttpClient.getAllShops(token, pageNo, pageSize,merchId,groupId,memberId,name,shopCode,userShopCode,""
                    , new HttpCallback<ShopListResultBean>() {
                        @Override
                        public void onSuccess(ShopListResultBean resultBean) {
                            if(resultBean != null)
                            {
                                shopinfoList = resultBean.getData();

                                adapter.notifyDataSetChanged(shopinfoList);
                                tvTopTitle.setText(mGroupInfo.getGroupName() + "(" + resultBean.getMessage() +")");
                                mSearchView.showRecordView(false);
                            }
                            else
                            {
                                showToast(resultBean.getMessage());
                            }
                            listView.stopRefresh();
                        }

                        @Override
                        public void onFail(Exception e) {
                            listView.stopRefresh();
                        }
                    });
        }
        catch (Exception e)
        {
            listView.stopRefresh();
        }

    }

    public void removeGroupShop(String shopIds)
    {
        try
        {
            String token = Constant.mUserInfo.getToken();
            String id = mGroupInfo.getId();


            HttpClient.removeGroupShop(token, id, shopIds, new HttpCallback<CommonCallBackBean>() {
                @Override
                public void onSuccess(CommonCallBackBean resultBean) {
                    if(resultBean != null)
                    {
                        updateDeleteMusicList(shopIds);
                        //dismissDialog();
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
            for(int j=0;j<shopinfoList.size();j++)
            {
                String id = shopinfoList.get(j).getId();
                if(deleteMusicId.equals(id))
                {
                    shopinfoList.remove(j);
                }
            }
        }
        adapter.notifyDataSetChanged();
        return ids.length;
    }


    private void pushMedia(String terminId)
    {
        String type = "1";
        String dataId = "";
        String userId = Constant.mUserInfo.getMerchant().getId();
        String pusherid = "";
        String pushername = Constant.mUserInfo.getNickName();

        try
        {
            // Simulate network access.
            HttpClient.pushMedia(terminId, type, dataId, userId,pusherid,pushername, new HttpCallback<CommonCallBackBean>() {
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


        if(position > 0)
            position = position - 1;
        Shopinfo tempShop = shopinfoList.get(position);
        Bundle bundle = new Bundle();
        /*bundle.putBoolean("IS_EDIT", true);
        if(tempShop.getGroup() != null)
            bundle.putString("GROUP_ID",tempShop.getGroup().getId());
        bundle.putString("SHOP_IDS",tempShop.getId());*/
        bundle.putSerializable("SHOP_INFO",tempShop);
        ViewUtils.startActivity(getActivity(),ShopDetailActivity.class,bundle);

    }

    @Override
    public void onRefresh() {
        loadShops();
    }

    @Override
    public void onLoadMore() {
        loadShops();
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

    @Override
    public void onMoreClick(int position) {
        Shopinfo tempShop = shopinfoList.get(position);
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(tempShop.getName());
        dialog.setItems(R.array.group_shop_dialog, (dialog1, which) -> {
            switch (which) {
                case 0://
                    removeGroupShop(tempShop.getId());
                    break;
                case 1://
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("SHOP_INFO",tempShop);
                    ViewUtils.startActivity(getActivity(),SearchSystemMusicActivity.class,bundle);
                    break;
                case 2://
                    Bundle bundle1 = new Bundle();
                    bundle1.putSerializable("SHOP_INFO",tempShop);
                    ViewUtils.startActivity(getActivity(),ShopDetailActivity.class,bundle1);
                    break;
            }
        });
        dialog.show();
    }
}
