package com.znt.vodbox.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
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
        @Bind(R.id.search_view)
        private SearchView mSearchView = null;

      @Bind(R.id.tv_common_title)
      private TextView tvTopTitle = null;
      @Bind(R.id.tv_common_title_sub)
      private TextView tvTopTitleSub = null;
      @Bind(R.id.iv_common_back)
      private ImageView ivTopReturn = null;
      @Bind(R.id.iv_common_more)
      private ImageView ivTopMore = null;
      @Bind(R.id.tv_common_confirm)
      private TextView tvConfirm = null;

      private int searchType = 0;


    private List<Shopinfo> shopinfoList = new ArrayList<>();

    private ShoplistAdapter adapter;

      private int pageNo = 1;
      private int pageSize = 25;
      private int maxSize = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_shop);

        tvTopTitle.setText("搜索店铺");
        ivTopMore.setVisibility(View.GONE);
        tvConfirm.setVisibility(View.GONE);

        tvTopTitleSub.setVisibility(View.VISIBLE);
        tvTopTitleSub.setText("按照名称搜索");

        ivTopReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvTopTitleSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSearchTypeDialog();
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

        String token = Constant.mUserInfo.getToken();

        String merchId = "";
        //String merchId = mUserInfo.getMerchant().getId();
        String groupId = "";
        String memberId = "";

        String shopCode = "";
        String userShopCode = "";
        String terminalId = "";
        String oldId = "";
        if(searchType == 0)
        {

        }
        else if(searchType == 1)
        {
            terminalId = name;
            name = "";
            oldId = "";
        }
        else if(searchType == 2)
        {
            oldId = name;
            name = "";
            terminalId = "";
        }

        mSearchView.showRecordView(false);

        try
        {
            // Simulate network access.
            HttpClient.getAllShops(token, pageNo+"", pageSize+""
                    ,merchId,groupId,memberId,name,shopCode,userShopCode,terminalId,oldId,""
                    , new HttpCallback<ShopListResultBean>() {
                        @Override
                        public void onSuccess(ShopListResultBean resultBean) {

                            listView.stopRefresh();
                            if(resultBean.getResultcode().equals("1"))
                            {
                                List<Shopinfo> tempList = resultBean.getData();

                                if(pageNo == 1)
                                    shopinfoList.clear();

                                if(tempList.size() <= pageSize)
                                    pageNo ++;

                                shopinfoList.addAll(tempList);
                                if(maxSize == 0)
                                    maxSize = Integer.parseInt(resultBean.getMessage());

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

      private AlertView tempAlertView = null;
      private void showSearchTypeDialog()
      {
          tempAlertView = new AlertView("请选择搜索类型",null, "取消", null,
                  getResources().getStringArray(R.array.search_type_dialog),
                  getActivity(), AlertView.Style.ActionSheet, new OnItemClickListener(){
              public void onItemClick(Object o,int which){
                  if(which == 0)
                  {
                      tvTopTitleSub.setText("按照名称搜索");
                  }
                  else if(which == 1)
                  {
                      tvTopTitleSub.setText("按照编号搜索");
                  }
                  else if(which == 2)
                  {
                      tvTopTitleSub.setText("按照旧编号搜索");
                  }
                  searchType = which;
                  loadShops();
              }
          });tempAlertView.show();
      }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if(position > 0)
            position = position - 1;
        Shopinfo tempShop = shopinfoList.get(position);

        if(tempShop.getTmlRunStatus() == null || tempShop.getTmlRunStatus().size() == 0)
        {
            showToast("该店铺下没有安装播放器");
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putBoolean("IS_EDIT", true);
        if(tempShop.getGroup() != null)
            bundle.putString("GROUP_ID",tempShop.getGroup().getId());
        bundle.putString("SHOP_IDS",tempShop.getId());
        bundle.putSerializable("SHOP_INFO",tempShop);
        ViewUtils.startActivity(getActivity(),ShopDetailActivity.class,bundle);
    }

    @Override
    public void onMoreClick(int position) {
        final Shopinfo tempShop = shopinfoList.get(position);
        showShopOperationDialog(tempShop);

    }

      private void showShopOperationDialog(final Shopinfo tempShop)
      {
          if(tempShop.getTmlRunStatus() == null || tempShop.getTmlRunStatus().size() == 0)
          {
              showToast("该店铺下没有安装设备");
              return;
          }

          new AlertView(tempShop.getName(),tempShop.getAddress(), "取消", null,
                  new String[]{"添加到分区", "插播歌曲"},
                  getActivity(), AlertView.Style.ActionSheet, new OnItemClickListener(){
              public void onItemClick(Object o,int position){
                  switch (position) {
                      case 0://
                          Bundle bundle = new Bundle();
                          bundle.putBoolean("IS_EDIT", true);
                          if(tempShop.getGroup() != null)
                              bundle.putString("GROUP_ID",tempShop.getGroup().getId());
                          bundle.putString("SHOP_IDS",tempShop.getId());
                          ViewUtils.startActivity(getActivity(),GroupListActivity.class,bundle,1);
                          break;
                      case 1://
                          Bundle bundle2 = new Bundle();
                          bundle2.putSerializable("SHOP_INFO",tempShop);
                          ViewUtils.startActivity(getActivity(),SearchSystemMusicActivity.class,bundle2);
                          break;
                  }
              }
          }).show();
      }

      @Override
      public void onRefresh() {
          pageNo = 1;
          loadShops();
      }

      @Override
      public void onLoadMore() {
          if(maxSize > shopinfoList.size())
              loadShops();
          else
              showToast("没有更多数据了");
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
