package com.znt.vodbox.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.vodbox.adapter.OnMoreClickListener;
import com.znt.vodbox.adapter.TypeListAdapter;
import com.znt.vodbox.bean.TypeCallBackBean;
import com.znt.vodbox.bean.TypeInfo;
import com.znt.vodbox.entity.LocalDataEntity;
import com.znt.vodbox.http.HttpCallback;
import com.znt.vodbox.http.HttpClient;
import com.znt.vodbox.utils.binding.Bind;
import com.znt.vodbox.view.searchview.ICallBack;
import com.znt.vodbox.view.searchview.SearchView;
import com.znt.vodbox.view.xlistview.LJListView;

import java.util.ArrayList;
import java.util.List;

public class MediaCategoryActivity extends BaseActivity implements
        LJListView.IXListViewListener, AdapterView.OnItemClickListener,OnMoreClickListener
{

    @Bind(R.id.tv_common_title)
    private TextView tvTopTitle = null;
    @Bind(R.id.iv_common_back)
    private ImageView ivTopReturn = null;
    @Bind(R.id.iv_common_more)
    private ImageView ivTopMore = null;
    @Bind(R.id.tv_common_confirm)
    private TextView tvConfirm = null;

    @Bind(R.id.ptrl_album_music)
    private LJListView listView = null;
    @Bind(R.id.search_view)
    private SearchView mSearchView = null;
    private List<TypeInfo> dataList = new ArrayList<>();

    private TypeListAdapter mAdapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_type);

        mSearchView.init("type_record.db");
        mSearchView.showRecordView(false);

        tvTopTitle.setText("歌单分类");

        ivTopMore.setVisibility(View.GONE);
        //tvConfirm.setVisibility(View.VISIBLE);

        ivTopReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        mSearchView.setOnClickSearch(new ICallBack() {
            @Override
            public void SearchAciton(String string) {
                getTypes();
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

        mAdapter = new TypeListAdapter(dataList);
        listView.setAdapter(mAdapter);

        mAdapter.setOnMoreClickListener(this);


        listView.onFresh();

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

    public void getTypes()
    {

        String searchWord = mSearchView.getText().toString();
        String token = LocalDataEntity.newInstance(getActivity()).getUserInfor().getToken();
        String merchId = LocalDataEntity.newInstance(getActivity()).getUserInfor().getMerchant().getId();
        //String merchId = mUserInfo.getMerchant().getId();
        try
        {
            // Simulate network access.
            HttpClient.getAlbumTypes(token, searchWord,new HttpCallback<TypeCallBackBean>() {
                        @Override
                        public void onSuccess(TypeCallBackBean resultBean) {

                            if(resultBean != null)
                            {
                                dataList = resultBean.getData();
                                TypeInfo tempTypeInfo = new TypeInfo();
                                tempTypeInfo.setName("全部");
                                dataList.add(0,tempTypeInfo);
                                mAdapter.notifyDataSetChanged(dataList);
                                /*if(!TextUtils.isEmpty(resultBean.getMessage()))
                                    curMusicSize = Integer.parseInt(resultBean.getMessage());
                                tvTopTitle.setText("歌单分类(" + curMusicSize + ")");*/

                            }
                            else
                            {
                                showToast(resultBean.getMessage());
                                //shopinfoList.clear();
                            }
                            mSearchView.showRecordView(false);
                            listView.stopRefresh();
                        }

                        @Override
                        public void onFail(Exception e) {
                            //vSearching.setVisibility(View.GONE);
                            listView.stopRefresh();
                            showToast(e.getMessage());
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
        getTypes();
    }

    @Override
    public void onLoadMore() {
        getTypes();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if(position > 0)
            position = position - 1;

        TypeInfo tempInfor = dataList.get(position);

        finishAndFeedBack(tempInfor);

        //showPlayDialog(tempInfor.getMusicName(),tempInfor.getMusicUrl(),tempInfor.getId());
    }

    private void finishAndFeedBack(TypeInfo tempInfor)
    {
        if(tempInfor == null)
            return;
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("TYPE_INFO", tempInfor);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onMoreClick(int position) {
        /*MediaInfo tempInfo = dataList.get(position);
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(tempInfo.getMusicName());
        dialog.setItems(R.array.album_music_dialog, (dialog1, which) -> {
            switch (which) {
                case 0://
                    Intent i = new Intent(getActivity(), MyAlbumActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("MUSIC_IDS",tempInfo.getId());
                    i.putExtras(bundle);
                    startActivity(i);
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
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    // 将文本内容放到系统剪贴板里。
                    cm.setText(tempInfo.getMusicUrl());
                    showToast("复制成功");
                    break;
                case 3://

                    break;
            }
        });
        dialog.show();*/
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
