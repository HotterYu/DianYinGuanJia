package com.znt.vodbox.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.znt.vodbox.bean.TypeInfo;
import com.znt.vodbox.fragment.DYMusicCategoryFragment;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by LaravelChen on 2017/6/8.
 */

public class DYMusicFragmentPagerAdapter extends FragmentPagerAdapter {
    private List<TypeInfo> typeList = new ArrayList<>();
    private Context context;

    public DYMusicFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {

        DYMusicCategoryFragment mDYMusicCategoryFragment = new DYMusicCategoryFragment();
        if(position == 0)
            mDYMusicCategoryFragment.setTypeId("");
        else
        {
            mDYMusicCategoryFragment.setTypeId(typeList.get(position).getId());
        }
        return mDYMusicCategoryFragment;
    }

    public void setTypeList(List<TypeInfo> typeList)
    {
        this.typeList = typeList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return typeList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return typeList.get(position).getName();
    }

}
