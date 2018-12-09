package com.znt.vodbox.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.znt.vodbox.fragment.GroupFragment;
import com.znt.vodbox.fragment.ShopFragment;


/**
 * Created by LaravelChen on 2017/6/8.
 */

public class ShopFragmentPagerAdapter extends FragmentPagerAdapter {
    private String[] titles = new String[]{"全部店铺","在线店铺","离线店铺","分区查看"};
    public int COUNT = titles.length;
    private Context context;

    public ShopFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {

        if(position == 3)
        {
            GroupFragment mGroupFragment = new GroupFragment();
            return mGroupFragment;
        }

        ShopFragment mShopFragment = new ShopFragment();
        if(position == 0)
            mShopFragment.setOnlinestatus("");
        else if(position == 1)
            mShopFragment.setOnlinestatus("1");
        else if(position == 2)
            mShopFragment.setOnlinestatus("0");
        return mShopFragment;
    }

    @Override
    public int getCount() {
        return COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

}