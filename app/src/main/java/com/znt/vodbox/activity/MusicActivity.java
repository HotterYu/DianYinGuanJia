package com.znt.vodbox.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.znt.vodbox.R;
import com.znt.vodbox.adapter.FragmentAdapter;
import com.znt.vodbox.constants.Extras;
import com.znt.vodbox.constants.Keys;
import com.znt.vodbox.entity.LocalDataEntity;
import com.znt.vodbox.executor.ControlPanel;
import com.znt.vodbox.executor.NaviMenuExecutor;
import com.znt.vodbox.executor.UserInfoExecutor;
import com.znt.vodbox.fragment.AllShopFragment;
import com.znt.vodbox.fragment.OfflineShopFragment;
import com.znt.vodbox.fragment.OnlineShopFragment;
import com.znt.vodbox.fragment.PlayFragment;
import com.znt.vodbox.location.LocationModel;
import com.znt.vodbox.model.UserInfo;
import com.znt.vodbox.permission.PermissionHelper;
import com.znt.vodbox.permission.PermissionInterface;
import com.znt.vodbox.service.AudioPlayer;
import com.znt.vodbox.service.QuitTimer;
import com.znt.vodbox.utils.SystemUtils;
import com.znt.vodbox.utils.ViewUtils;
import com.znt.vodbox.utils.binding.Bind;


public class MusicActivity extends BaseActivity implements View.OnClickListener, QuitTimer.OnTimerListener,
        NavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener,PermissionInterface {
    @Bind(R.id.drawer_layout)
    private DrawerLayout drawerLayout;
    @Bind(R.id.navigation_view)
    private NavigationView navigationView;
    @Bind(R.id.iv_menu)
    private ImageView ivMenu;
    @Bind(R.id.iv_search)
    private ImageView ivSearch;
    @Bind(R.id.tv_local_music)
    private TextView tvLocalMusic;
    @Bind(R.id.tv_all_shops)
    private TextView tvAllShops;
    @Bind(R.id.tv_online_music)
    private TextView tvOnlineMusic;
    @Bind(R.id.viewpager)
    private ViewPager mViewPager;
    @Bind(R.id.fl_play_bar)
    private FrameLayout flPlayBar;

    private View vNavigationHeader;
    private AllShopFragment mAllShopFragment;
    private OnlineShopFragment mOnlineShopFragment;
    private OfflineShopFragment mOfflineShopFragment;
    /*private MyAlbumFragment_ORG mMyAlbumFragment;
    private SheetListFragment mSheetListFragment;*/
    private PlayFragment mPlayFragment;
    private ControlPanel controlPanel;
    private NaviMenuExecutor naviMenuExecutor;
    private MenuItem timerItem;
    private boolean isPlayFragmentShow;

    private UserInfo mUserInfo = null;

    private LocationModel mLocationModel = null;

    private PermissionHelper mPermissionHelper;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(mPermissionHelper.requestPermissionsResult(requestCode, permissions, grantResults)){
            //权限请求结果，并已经处理了该回调
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public int getPermissionsRequestCode() {
        return 10000;
    }

    @Override
    public String[] getPermissions() {
        return new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                /*Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,*/
                Manifest.permission.ACCESS_FINE_LOCATION

        };
    }
    @Override
    public void requestPermissionsSuccess() {
        //权限请求用户已经全部允许
        initLocation();
    }

    @Override
    public void requestPermissionsFail() {
        //权限请求不被用户允许。可以提示并退出或者提示权限的用途并重新发起权限申请。
        mPermissionHelper.requestPermissions();
        //finish();
    }

    public interface OnCountGetCallBack
    {
        public void onCountGetBack(String count);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        mUserInfo = (UserInfo) getIntent().getSerializableExtra("USER_INFO");

        mPermissionHelper = new PermissionHelper(this, this);
        mPermissionHelper.requestPermissions();
    }

    private void initLocation()
    {
        mLocationModel = new LocationModel(getApplicationContext(), new LocationModel.OnLocationResultListener() {
            @Override
            public void onLocationSuccess(AMapLocation location) {

                String lat = location.getLatitude() + "";
                String lon = location.getLongitude() + "";
                String addr = location.getAddress();
                LocalDataEntity.newInstance(getApplicationContext()).setLat(lat);
                LocalDataEntity.newInstance(getApplicationContext()).setLon(lon);
                LocalDataEntity.newInstance(getApplicationContext()).setDeviceAddr(addr);
                //HttpClient.updateShopInfo(getApplicationContext());

                mLocationModel.stopLocation();
            }

            @Override
            public void onLocationFail(String error) {
                mLocationModel.stopLocation();
            }
        });
        mLocationModel.startLocation();
    }

    @Override
    protected void onServiceBound() {
        setupView();
        controlPanel = new ControlPanel(flPlayBar);
        naviMenuExecutor = new NaviMenuExecutor(this);
        AudioPlayer.get().addOnPlayEventListener(controlPanel);
        QuitTimer.get().setOnTimerListener(this);
        parseIntent();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        parseIntent();
    }

    private void setupView() {
        // add navigation header
        vNavigationHeader = LayoutInflater.from(this).inflate(R.layout.navigation_header, navigationView, false);
        navigationView.addHeaderView(vNavigationHeader);
        UserInfoExecutor mUserInfoExecutor = new UserInfoExecutor(getApplicationContext(),vNavigationHeader);
        mUserInfoExecutor.showUserInfo();
        vNavigationHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtils.startActivity(getActivity(),AccountActivity.class,null);
            }
        });

        // setup view pager
        mAllShopFragment = new AllShopFragment();
        mOnlineShopFragment = new OnlineShopFragment();
        mOfflineShopFragment = new OfflineShopFragment();
        /*mMyAlbumFragment = new MyAlbumFragment_ORG();
        mSheetListFragment = new SheetListFragment();*/
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(mAllShopFragment);
        adapter.addFragment(mOnlineShopFragment);
        adapter.addFragment(mOfflineShopFragment);
        //adapter.addFragment(mMyAlbumFragment);
        //adapter.addFragment(mSheetListFragment);
        mViewPager.setAdapter(adapter);

        mViewPager.setOffscreenPageLimit(2);

        mAllShopFragment.setOnCountGetCallBack(new OnCountGetCallBack() {
            @Override
            public void onCountGetBack(String count) {
                tvAllShops.setText(getResources().getString(R.string.all_shops) + "(" + count + ")");
            }
        });
        mOnlineShopFragment.setOnCountGetCallBack(new OnCountGetCallBack() {
            @Override
            public void onCountGetBack(String count) {
                tvLocalMusic.setText(getResources().getString(R.string.all_online_shops) + "(" + count + ")");
            }
        });
        mOfflineShopFragment.setOnCountGetCallBack(new OnCountGetCallBack() {
            @Override
            public void onCountGetBack(String count) {
                tvOnlineMusic.setText(getResources().getString(R.string.all_offline_shops) + "(" + count + ")");
            }
        });

        tvAllShops.setSelected(true);

        ivMenu.setOnClickListener(this);
        ivSearch.setOnClickListener(this);
        tvLocalMusic.setOnClickListener(this);
        tvAllShops.setOnClickListener(this);
        tvOnlineMusic.setOnClickListener(this);
        flPlayBar.setOnClickListener(this);
        mViewPager.addOnPageChangeListener(this);
        navigationView.setNavigationItemSelectedListener(this);

        mAllShopFragment.setUserInfo(mUserInfo);
        mOnlineShopFragment.setUserInfo(mUserInfo);
        mOfflineShopFragment.setUserInfo(mUserInfo);

    }

    private void parseIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra(Extras.EXTRA_NOTIFICATION)) {
            showPlayingFragment();
            setIntent(new Intent());
        }
    }

    @Override
    public void onTimer(long remain) {
        if (timerItem == null) {
            timerItem = navigationView.getMenu().findItem(R.id.action_timer);
        }
        String title = getString(R.string.menu_timer);
        timerItem.setTitle(remain == 0 ? title : SystemUtils.formatTime(title + "(mm:ss)", remain));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_menu:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.iv_search:

                if(mViewPager.getCurrentItem() == 0)
                {
                    startActivity(new Intent(this, SearchShopActivity.class));
                }
                else
                    startActivity(new Intent(this, SearchMusicActivity.class));
                break;
            case R.id.tv_all_shops:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.tv_local_music:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.tv_online_music:
                mViewPager.setCurrentItem(2);
                break;
            case R.id.fl_play_bar:
                showPlayingFragment();
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //drawerLayout.closeDrawers();
        handler.postDelayed(() -> item.setChecked(false), 500);
        return naviMenuExecutor.onNavigationItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK)
            return;
        if(requestCode == 1)
        {
            //GroupInfo tempInfo = (GroupInfo) data.getSerializableExtra("GROUP_INFOR");
            //mPlanInfo.convertPlanToSubPlan();
            mAllShopFragment.loadShops();

        }
        else if(requestCode == 2)
        {
            /*GroupInfo tempInfo = (GroupInfo) data.getSerializableExtra("GROUP_INFOR");
            mAdPlanInfo.setGroupName(tempInfo.getGroupName());
            mAdPlanInfo.setGroupId(tempInfo.getId());
            showShops(true);*/
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            tvAllShops.setSelected(true);
            tvLocalMusic.setSelected(false);
            tvOnlineMusic.setSelected(false);
        }
        else if (position == 1){
            tvAllShops.setSelected(false);
            tvLocalMusic.setSelected(true);
            tvOnlineMusic.setSelected(false);
        }
        else if (position == 2){
            tvAllShops.setSelected(false);
            tvLocalMusic.setSelected(false);
            tvOnlineMusic.setSelected(true);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    private void showPlayingFragment() {
        if (isPlayFragmentShow) {
            return;
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fragment_slide_up, 0);
        if (mPlayFragment == null) {
            mPlayFragment = new PlayFragment();
            ft.replace(android.R.id.content, mPlayFragment);
        } else {
            ft.show(mPlayFragment);
        }
        ft.commitAllowingStateLoss();
        isPlayFragmentShow = true;
    }

    private void hidePlayingFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(0, R.anim.fragment_slide_down);
        ft.hide(mPlayFragment);
        ft.commitAllowingStateLoss();
        isPlayFragmentShow = false;
    }

    @Override
    public void onBackPressed() {
        if (mPlayFragment != null && isPlayFragmentShow) {
            hidePlayingFragment();
            return;
        }
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
            return;
        }

        if((System.currentTimeMillis() - touchTime) < 2000)
        {
            closeApp();
            super.onBackPressed();
            // TODO Auto-generated method stub
        }
        else
        {
            showToast(getResources().getString(R.string.app_close_hint));
            touchTime = System.currentTimeMillis();
        }

        //super.onBackPressed();
    }

    private long touchTime = 0;
    private void closeApp()
    {
        closeAllActivity();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(Keys.VIEW_PAGER_INDEX, mViewPager.getCurrentItem());
        mAllShopFragment.onSaveInstanceState(outState);
        mOnlineShopFragment.onSaveInstanceState(outState);
        mOfflineShopFragment.onSaveInstanceState(outState);
        //mMyAlbumFragment.onSaveInstanceState(outState);
        //mSheetListFragment.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        mViewPager.post(() -> {
            mViewPager.setCurrentItem(savedInstanceState.getInt(Keys.VIEW_PAGER_INDEX), false);
            //mAllShopFragment.onRestoreInstanceState(savedInstanceState);
            //mMyAlbumFragment.onRestoreInstanceState(savedInstanceState);
            //mSheetListFragment.onRestoreInstanceState(savedInstanceState);
        });
    }

    @Override
    protected void onDestroy() {
        AudioPlayer.get().removeOnPlayEventListener(controlPanel);
        QuitTimer.get().setOnTimerListener(null);
        super.onDestroy();
    }
}
