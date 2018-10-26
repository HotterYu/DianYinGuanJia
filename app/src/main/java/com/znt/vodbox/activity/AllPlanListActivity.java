package com.znt.vodbox.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.vodbox.adapter.FragmentAdapter;
import com.znt.vodbox.fragment.AdPlanFragment;
import com.znt.vodbox.fragment.MediaPlanFragment;
import com.znt.vodbox.utils.binding.Bind;

public class AllPlanListActivity extends BaseActivity implements View.OnClickListener,ViewPager.OnPageChangeListener {


    @Bind(R.id.iv_return)
    private ImageView ivBack;
    @Bind(R.id.iv_search)
    private ImageView ivSearch;
    @Bind(R.id.tv_plan_media_plan)
    private TextView tvMediaPlan;
    @Bind(R.id.tv_plan_ad_plan)
    private TextView tvAdPlan;
    @Bind(R.id.fab)
    FloatingActionButton fab = null;
    @Bind(R.id.plan_viewpager)
    private ViewPager mViewPager;

    private MediaPlanFragment mMediaPlanFragment = null;
    private AdPlanFragment mAdPlanFragment = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_plan_list);


        mMediaPlanFragment = new MediaPlanFragment();
        mAdPlanFragment = new AdPlanFragment();

        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = null;
                if(mViewPager.getCurrentItem() == 1)
                {
                    intent = new Intent(getActivity(), AdPlanDetailActivity.class);
                    Bundle b = new Bundle();
                    intent.putExtras(b);
                    startActivityForResult(intent, 2);
                }
                else
                {
                    intent = new Intent(getActivity(), PlanDetailActivity.class);
                    Bundle b = new Bundle();
                    intent.putExtras(b);
                    startActivityForResult(intent, 1);
                }



            }
        });

        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(mMediaPlanFragment);
        adapter.addFragment(mAdPlanFragment);

        mViewPager.setAdapter(adapter);

        mViewPager.setOffscreenPageLimit(1);

        tvMediaPlan.setSelected(true);

        ivBack.setOnClickListener(this);
        ivSearch.setOnClickListener(this);
        tvMediaPlan.setOnClickListener(this);
        tvAdPlan.setOnClickListener(this);


        mViewPager.addOnPageChangeListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK)
            return;
        if(requestCode == 1)
        {
            mMediaPlanFragment.refreshData();
        }
        else if(requestCode == 2)
        {
            mAdPlanFragment.refreshData();
        }

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            tvMediaPlan.setSelected(true);
            tvAdPlan.setSelected(false);
        }
        else if (position == 1){
            tvMediaPlan.setSelected(false);
            tvAdPlan.setSelected(true);

        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_return:
                finish();
                break;
            case R.id.iv_search:
                //startActivity(new Intent(this, SearchMusicActivity.class));
                break;
            case R.id.tv_plan_media_plan:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.tv_plan_ad_plan:
                mViewPager.setCurrentItem(1);
                break;

        }
    }
}
