package com.znt.vodbox.activity;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.vodbox.adapter.OnMoreClickListener;
import com.znt.vodbox.adapter.ShoplistAdapter;
import com.znt.vodbox.bean.ShopListResultBean;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.http.HttpCallback;
import com.znt.vodbox.http.HttpClient;
import com.znt.vodbox.model.Shopinfo;
import com.znt.vodbox.utils.ViewUtils;
import com.znt.vodbox.utils.binding.Bind;
import com.znt.vodbox.view.searchview.ICallBack;
import com.znt.vodbox.view.searchview.SearchView;
import com.znt.vodbox.view.xlistview.LJListView;

import java.util.ArrayList;
import java.util.List;


public class SearchShopActivity extends BaseActivity  implements LJListView.IXListViewListener,AdapterView.OnItemClickListener, OnMoreClickListener
  {
    @Bind(R.id.lv_all_shops)
    private LJListView listView;
    @Bind(R.id.tv_all_shops_loading)
    private TextView vSearching;
    @Bind(R.id.search_view)
    private SearchView mSearchView = null;

      @Bind(R.id.tv_common_title)
      private TextView tvTopTitle = null;
      @Bind(R.id.iv_common_back)
      private ImageView ivTopReturn = null;
      @Bind(R.id.iv_common_more)
      private ImageView ivTopMore = null;
      @Bind(R.id.tv_common_confirm)
      private TextView tvConfirm = null;


    private List<Shopinfo> shopinfoList = new ArrayList<>();

    private ShoplistAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_shop);

        tvTopTitle.setText("搜索店铺");
        ivTopMore.setVisibility(View.GONE);
        tvConfirm.setVisibility(View.GONE);

        ivTopReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        adapter = new ShoplistAdapter(getActivity(),shopinfoList);
        adapter.setOnMoreClickListener(this);

        listView.getListView().setDivider(getResources().getDrawable(R.color.transparent));
        listView.getListView().setDividerHeight(1);
        listView.setPullLoadEnable(true,"");
        listView.setPullRefreshEnable(true);
        listView.setIsAnimation(true);
        listView.setXListViewListener(this);
        listView.showFootView(false);
        listView.setRefreshTime();
        listView.setOnItemClickListener(this);

        listView.setAdapter(adapter);

        mSearchView.init("shop_search_record.db");
        mSearchView.showRecordView(false);
        mSearchView.setOnClickSearch(new ICallBack() {
            @Override
            public void SearchAciton(String string) {
                loadShops();
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

    @Override
    protected int getDarkTheme() {
        return R.style.AppThemeDark_Search;
    }


    public void loadShops()
    {

        String name = mSearchView.getText();
        if(TextUtils.isEmpty(name))
        {
            showToast("请输入搜索内容");
            return;
        }

        vSearching.setVisibility(View.GONE);

        String token = Constant.mUserInfo.getToken();
        String pageNo = "1";
        String pageSize = "100";
        String merchId = "";
        //String merchId = mUserInfo.getMerchant().getId();
        String groupId = "";
        String memberId = "";

        String shopCode = "";
        String userShopCode = "";

        mSearchView.showRecordView(false);

        try
        {
            // Simulate network access.
            HttpClient.getAllShops(token, pageNo, pageSize,merchId,groupId,memberId,name,shopCode,userShopCode,""
                    , new HttpCallback<ShopListResultBean>() {
                        @Override
                        public void onSuccess(ShopListResultBean resultBean) {
                            vSearching.setVisibility(View.GONE);

                            listView.stopRefresh();
                            if(resultBean.getResultcode().equals("1"))
                            {
                                shopinfoList = resultBean.getData();
                                tvTopTitle.setText("搜索店铺("+resultBean.getMessage()+")");
                                adapter.notifyDataSetChanged(shopinfoList);
                                mSearchView.showRecordView(false);
                            }
                            else
                            {
                                showToast(resultBean.getMessage());
                            }
                        }

                        @Override
                        public void onFail(Exception e) {
                            vSearching.setVisibility(View.GONE);
                            listView.stopRefresh();
                            if(e != null)
                                showToast(e.getMessage());
                            else
                                showToast("加载数据失败");
                        }
                    });
        }
        catch (Exception e)
        {
            listView.stopRefresh();
            showToast(e.getMessage());
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if(position > 0)
            position = position - 1;
        Shopinfo tempShop = shopinfoList.get(position);
        Bundle bundle = new Bundle();
        bundle.putBoolean("IS_EDIT", true);
        if(tempShop.getGroup() != null)
            bundle.putString("GROUP_ID",tempShop.getGroup().getId());
        bundle.putString("SHOP_IDS",tempShop.getId());
        ViewUtils.startActivity(getActivity(),ShopDetailActivity.class,bundle);
    }

    @Override
    public void onMoreClick(int position) {
        Shopinfo tempShop = shopinfoList.get(position);
        AlertDialog.Builder dialog = new AlertDialog.Builder(getApplication());
        dialog.setTitle(tempShop.getName());
        dialog.setItems(R.array.shop_list_dialog, (dialog1, which) -> {
            switch (which) {
                case 0://
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("IS_EDIT", true);
                    if(tempShop.getGroup() != null)
                        bundle.putString("GROUP_ID",tempShop.getGroup().getId());
                    bundle.putString("SHOP_IDS",tempShop.getId());
                    ViewUtils.startActivity(getActivity(),GroupListActivity.class,bundle,1);
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
}
