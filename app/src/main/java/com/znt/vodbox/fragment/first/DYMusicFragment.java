package com.znt.vodbox.fragment.first;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.vodbox.activity.SearchSysAlbumActivity;
import com.znt.vodbox.activity.SearchSystemMusicActivity;
import com.znt.vodbox.activity.TypeActivity;
import com.znt.vodbox.adapter.DYMusicFragmentPagerAdapter;
import com.znt.vodbox.bean.TypeCallBackBean;
import com.znt.vodbox.bean.TypeInfo;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.http.HttpCallback;
import com.znt.vodbox.http.HttpClient;
import com.znt.vodbox.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class DYMusicFragment extends Fragment {
    private View rootView;
    private TextView tvMore;
    private DYMusicFragmentPagerAdapter adapter;

    public DYMusicFragment() {

    }

    public void goSearchAlbumActivity(Context activity)
    {
        startActivity(new Intent(activity, SearchSysAlbumActivity.class));
    }
    public void goSearchMusicActivity(Context activity)
    {
        startActivity(new Intent(activity, SearchSystemMusicActivity.class));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_dianyin_music, container, false);
        initView(rootView);
        return rootView;
    }

    public void initView(View rootView) {
        tvMore = (TextView) rootView.findViewById(R.id.tv_dymusic_category_more);
        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
        //关键的一个知识点getChidFragmentManager
        adapter = new DYMusicFragmentPagerAdapter(getChildFragmentManager(), getContext());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(7);

        //TabLayout
        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tablayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        //显示当前那个标签页
//        viewPager.setCurrentItem(1);
        tabLayout.setupWithViewPager(viewPager);

        tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("TYPE","0");
                ViewUtils.startActivity(getActivity(), TypeActivity.class,bundle);
            }
        });
        getTypes();
    }

    private List<TypeInfo> typeList = new ArrayList<>();
    public void getTypes()
    {

        if(Constant.mUserInfo == null)
            return;
        //LocalDataEntity.newInstance(getActivity()).getUserInfor();
        String searchWord = "";
        String token = Constant.mUserInfo.getToken();
        String merchId = Constant.mUserInfo.getMerchant().getId();
        //String merchId = mUserInfo.getMerchant().getId();
        try
        {
            // Simulate network access.
            HttpClient.getAlbumTypes(token, searchWord,"0", new HttpCallback<TypeCallBackBean>() {
                @Override
                public void onSuccess(TypeCallBackBean resultBean) {

                    if(resultBean != null)
                    {
                        if(typeList != null)
                            typeList.clear();
                        typeList = resultBean.getData();
                        TypeInfo temp = new TypeInfo();
                        temp.setName("全部");
                        typeList.add(0,temp);
                        adapter.setTypeList(typeList);
                    }
                    else
                    {

                        //shopinfoList.clear();
                    }
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
}
