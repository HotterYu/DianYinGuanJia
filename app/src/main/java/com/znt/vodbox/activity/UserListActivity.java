package com.znt.vodbox.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.vodbox.adapter.OnMoreClickListener;
import com.znt.vodbox.adapter.UserListAdapter;
import com.znt.vodbox.bean.UserListCallBackBean;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.http.HttpCallback;
import com.znt.vodbox.http.HttpClient;
import com.znt.vodbox.model.UserInfo;
import com.znt.vodbox.utils.ActivityManager;
import com.znt.vodbox.utils.ViewUtils;
import com.znt.vodbox.utils.binding.Bind;
import com.znt.vodbox.view.searchview.ICallBack;
import com.znt.vodbox.view.searchview.SearchView;
import com.znt.vodbox.view.xlistview.LJListView;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends BaseActivity implements
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
    @Bind(R.id.search_view)
    private SearchView mSearchView = null;

    private List<UserInfo> dataList = new ArrayList<>();

    private UserListAdapter mUserListAdapter = null;

    private int curMusicSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        tvTopTitle.setText("账户列表");
        ivTopMore.setVisibility(View.GONE);

        ivTopReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSearchView.setOnClickSearch(new ICallBack() {
            @Override
            public void SearchAciton(String string) {
                getUserList();
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

        mUserListAdapter = new UserListAdapter(dataList);
        listView.setAdapter(mUserListAdapter);

        mUserListAdapter.setOnMoreClickListener(this);

        mSearchView.init("user_list_record.db");
        mSearchView.showRecordView(false);

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

    public void getUserList()
    {

        String nickName = mSearchView.getText().toString();
        String userName = "";
        String orgzId = "";//所属客户id
        String token = Constant.mUserInfo.getToken();
        String pageNo = "1";
        String pageSize = "100";
        String merchId = Constant.mUserInfo.getMerchant().getId();
        //String merchId = mUserInfo.getMerchant().getId();
        try
        {
            // Simulate network access.
            HttpClient.getUserList(token, pageNo, pageSize,userName,nickName,orgzId, new HttpCallback<UserListCallBackBean>() {
                        @Override
                        public void onSuccess(UserListCallBackBean resultBean) {

                            if(resultBean != null)
                            {
                                dataList = resultBean.getData();
                                mUserListAdapter.notifyDataSetChanged(dataList);
                                if(!TextUtils.isEmpty(resultBean.getMessage()))
                                    curMusicSize = Integer.parseInt(resultBean.getMessage());
                                tvTopTitle.setText("用户列表" + "(" + curMusicSize + ")");
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
    private int updateDeleteMusicList(String musicIds)
    {
        String[] ids = musicIds.split(",");
        int len = ids.length;
        for(int i=0;i<len;i++)
        {
            String deleteMusicId = ids[i];
            for(int j=0;j<dataList.size();j++)
            {
                String id = dataList.get(j).getId();
                if(deleteMusicId.equals(id))
                {
                    dataList.remove(j);
                }
            }
        }
        mUserListAdapter.notifyDataSetChanged();
        return ids.length;
    }

    @Override
    public void onRefresh() {
        getUserList();
    }

    @Override
    public void onLoadMore() {
        getUserList();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if(position > 0)
            position = position - 1;

        UserInfo tempInfor = dataList.get(position);

        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setText(tempInfor.getUsername());

        if(isCurCanLogin(tempInfor))
        {
            Bundle b = new Bundle();
            b.putSerializable("USER_INFO",tempInfor);
            ViewUtils.startActivity(getActivity(),LoginActivity.class,b);
            ActivityManager.getInstance().finishOthersActivity(UserRecordActivity.class);
        }
        else
            showToast("该账户已登录");
    }

    private boolean isCurCanLogin(UserInfo infor)
    {
        String userId = getLocalData().getUserId();
        if(userId.equals(infor.getId()))
            return false;
        return true;
    }

    @Override
    public void onMoreClick(int position) {
        /*UserInfo tempInfo = dataList.get(position);
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
                    showAlertDialog(getActivity(), new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            deleteAlbumMusic(tempInfo.getId());
                        }
                    }, "", "确定删除该文件吗?");
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
