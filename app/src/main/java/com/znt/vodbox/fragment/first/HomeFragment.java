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
import com.znt.vodbox.activity.AddShopActivity;
import com.znt.vodbox.activity.SearchShopActivity;
import com.znt.vodbox.adapter.ShopFragmentPagerAdapter;
import com.znt.vodbox.utils.ViewUtils;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private View rootView;
    private ShopFragmentPagerAdapter adapter;

    public HomeFragment() {

    }

    public void goSearchShopActivity(Context activity)
    {
        startActivity(new Intent(activity, SearchShopActivity.class));
    }

    public void goAddShopActivity(Context activity)
    {
        ViewUtils.startActivity(getActivity(),AddShopActivity.class,null,1);
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
        adapter = new ShopFragmentPagerAdapter(getChildFragmentManager(), getContext());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(7);

        //TabLayout
        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tablayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        //显示当前那个标签页
//        viewPager.setCurrentItem(1);
        tabLayout.setupWithViewPager(viewPager);
    }
}
