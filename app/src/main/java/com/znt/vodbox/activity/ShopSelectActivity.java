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
import com.znt.vodbox.adapter.ShoplistAdapter;
import com.znt.vodbox.bean.CommonCallBackBean;
import com.znt.vodbox.bean.ShopListResultBean;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.http.HttpCallback;
import com.znt.vodbox.http.HttpClient;
import com.znt.vodbox.model.Shopinfo;
import com.znt.vodbox.utils.ToastUtils;
import com.znt.vodbox.utils.binding.Bind;
import com.znt.vodbox.view.searchview.ICallBack;
import com.znt.vodbox.view.searchview.SearchView;
import com.znt.vodbox.view.xlistview.LJListView;

import java.util.ArrayList;
import java.util.List;

public class ShopSelectActivity extends BaseActivity  implements
        LJListView.IXListViewListener, AdapterView.OnItemClickListener{

    @Bind(R.id.tv_common_title)
    private TextView tvTopTitle = null;
    @Bind(R.id.iv_common_back)
    private ImageView ivTopReturn = null;
    @Bind(R.id.iv_common_more)
    private ImageView ivTopMore = null;
    @Bind(R.id.tv_common_title_sub)
    private TextView tvTopTitleSub = null;

    @Bind(R.id.ptrl_shop_select)
    private LJListView listView = null;
    @Bind(R.id.search_view)
    private SearchView mSearchView = null;

    private String mediaName = "";
    private String mediaUrl = "";
    private String mediaId = "";
    private String mediaType = "";

    private int pageNo = 1;
    private int pageSize = 25;
    private int maxSize = 0;

    private List<Shopinfo> shopinfoList = new ArrayList<>();

    private ShoplistAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_select);

        tvTopTitle.setText(getResources().getString(R.string.shop_select));
        ivTopMore.setVisibility(View.GONE);
        ivTopReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvTopTitleSub.setVisibility(View.VISIBLE);
        tvTopTitleSub.setText("按照名称搜索");

        tvTopTitleSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSearchTypeDialog();
            }
        });

        mediaName = getIntent().getStringExtra("MEDIA_NAME");
        mediaUrl = getIntent().getStringExtra("MEDIA_URL");
        mediaId = getIntent().getStringExtra("MEDIA_ID");
        mediaType = getIntent().getStringExtra("MEDIA_TYPE");
        if(TextUtils.isEmpty(mediaType))
            mediaType = "1";

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
        String merchId = "";
        //String merchId = mUserInfo.getMerchant().getId();
        String groupId = "";
        String memberId = "";
        String shopCode = "";
        String userShopCode = "";
        String name = text;

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
            HttpClient.getAllShops(token, pageNo+"", pageSize+""
                    ,merchId,groupId,memberId,name,shopCode,userShopCode,terminalId,oldId,"", new HttpCallback<ShopListResultBean>() {
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

                                tvTopTitle.setText(getResources().getString(R.string.shop_select) + "(" + resultBean.getMessage() +")");
                                adapter.notifyDataSetChanged(shopinfoList);
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
    private int searchType = 0;
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
                searchType = which;
                loadShops();
            }
        });tempAlertView.show();
    }

    private void pushMedia(String terminId, String mediaType)
    {
        //String type = "1";//1 歌曲， 2 广告
        String dataId = mediaId;
        String userId = Constant.mUserInfo.getMerchant().getId();
        String pusherid = "";
        String pushername = Constant.mUserInfo.getNickName();

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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(position > 0)
            position = position - 1;

        Shopinfo tempInfor = shopinfoList.get(position);

        if(tempInfor.getTmlRunStatus() != null
                && tempInfor.getTmlRunStatus().size() >0
                && tempInfor.getTmlRunStatus().get(0).getOnlineStatus() != null)
            pushMedia(tempInfor.getTmlRunStatus().get(0).getTerminalId(),mediaType);
        else
            showToast(getResources().getString(R.string.no_device_hint));
        /*Intent intent = new Intent();
        Bundle b = new Bundle();
        b.putSerializable("SHOP_INFO", tempInfor);

        setIntent(intent);
        setResult(RESULT_OK);
        finish();*/

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
