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

import com.znt.vodbox.R;
import com.znt.vodbox.activity.SearchMusicActivity;
import com.znt.vodbox.activity.SearchShopActivity;
import com.znt.vodbox.activity.SearchSysAlbumActivity;
import com.znt.vodbox.activity.SearchSystemMusicActivity;
import com.znt.vodbox.adapter.DYMusicFragmentPagerAdapter;
import com.znt.vodbox.bean.TypeCallBackBean;
import com.znt.vodbox.bean.TypeInfo;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.http.HttpCallback;
import com.znt.vodbox.http.HttpClient;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class DYMusicFragment extends Fragment {
    private View rootView;
    private DYMusicFragmentPagerAdapter adapter;

    public DYMusicFragment() {

    }

    public void goSearchShopActivity(Context activity)
    {
        startActivity(new Intent(activity, SearchShopActivity.class));
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
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        initView(rootView);
        return rootView;
    }

    public void initView(View rootView) {
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

        getTypes();
    }

    private List<TypeInfo> typeList = new ArrayList<>();
    public void getTypes()
    {

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
