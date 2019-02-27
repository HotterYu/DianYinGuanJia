package com.znt.vodbox.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.znt.vodbox.R;
import com.znt.vodbox.adapter.ViewPagerAdapter;
import com.znt.vodbox.application.MusicApplication;
import com.znt.vodbox.entity.LocalDataEntity;
import com.znt.vodbox.executor.NaviMenuExecutor;
import com.znt.vodbox.fragment.SheetListFragment;
import com.znt.vodbox.fragment.first.DYMusicFragment;
import com.znt.vodbox.fragment.first.HomeFragment;
import com.znt.vodbox.permission.PermissionHelper;
import com.znt.vodbox.permission.PermissionInterface;
import com.znt.vodbox.utils.ActivityManager;
import com.znt.vodbox.utils.SystemUtils;
import com.znt.vodbox.utils.ViewUtils;
import com.znt.vodbox.view.SlideViewPager;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener
        , View.OnClickListener,PermissionInterface {

    private Toolbar mtoolbar;
    private HomeFragment homeFragment;
    private DYMusicFragment mDYMusicFragment;
    private SheetListFragment mSheetListFragment;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private CircleImageView imageView;
    private BottomNavigationBar bottomNavigationBar = null;
    private View headerlayout;

    private ViewPagerAdapter viewPagerAdapter;
    private SlideViewPager viewPager;

    private BottomSheetDialog dialog;
    private SharedPreferences.Editor editor;

    private PermissionHelper mPermissionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //navgationview
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setItemIconTintList(null);
        //绑定headerlayout
        get_info();

        //初始化
        initView();

        mPermissionHelper = new PermissionHelper(MainActivity.this, this);
        mPermissionHelper.requestPermissions();

        long lastCheckTime = LocalDataEntity.newInstance(getApplicationContext()).getCheckUpdateTime();
        if(System.currentTimeMillis() - lastCheckTime >= 24 * 60 * 60 * 1000)
            checkUpdate();

    }

    private void checkUpdate()
    {
        if(SystemUtils.isNetConnected(getApplicationContext()))
        {
            /*HttpFactory HttpFactory = new HttpFactory(getApplicationContext(), handler);
            HttpFactory.checkUpdate();*/
        }

    }

    //获取用户信息并且判断是否登录
    private void get_info() {

        /*if (!MyA)
        {
            navigationView.addHeaderView(layoutInflater.inflate(R.layout.navigation_header_before, navigationView, false));
            headerlayout = navigationView.getHeaderView(0);
            imageView = (CircleImageView) headerlayout.findViewById(R.id.profile_image_before);
            imageView.setOnClickListener(this);
        }
        else*/
        {
            navigationView.addHeaderView(LayoutInflater.from(getApplicationContext()).inflate(R.layout.navigation_header, navigationView, false));
            headerlayout = navigationView.getHeaderView(0);
            imageView = (CircleImageView) headerlayout.findViewById(R.id.profile_image);
            TextView tv_header = (TextView) headerlayout.findViewById(R.id.tv_header);
            TextView followers = (TextView) headerlayout.findViewById(R.id.followers);
            TextView following = (TextView) headerlayout.findViewById(R.id.following);
            TextView tvCode = (TextView) headerlayout.findViewById(R.id.tv_act_code);
            tv_header.setText(LocalDataEntity.newInstance(getApplicationContext()).getUserName());
            tvCode.setText("复制激活码:"+LocalDataEntity.newInstance(getApplicationContext()).getPcCode());
            /*followers.setText("");
            following.setText("");*/
            headerlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if( MusicApplication.isLogin)
                        ViewUtils.startActivity(getApplicationContext(), AccountActivity.class,null);
                    else
                        ViewUtils.startActivity(getApplicationContext(), LoginAct.class,null);
                }
            });
            tvCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String pcCode = LocalDataEntity.newInstance(getApplicationContext()).getPcCode();
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    // 将文本内容放到系统剪贴板里。
                    cm.setText(pcCode);
                    Toast.makeText(getApplicationContext(),"激活码复制成功",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    public void initView() {
        //显示toolbar
        mtoolbar = (Toolbar) findViewById(R.id.toolbar);
        mtoolbar.setTitle("我的店铺");
        setSupportActionBar(mtoolbar);
        mtoolbar.setBackgroundColor(Color.parseColor("#ec6400"));
        //绑定侧边栏
        viewPager = (SlideViewPager) findViewById(R.id.view_pager_home);
        viewPager.setSlide(false);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        List<Fragment> list = new ArrayList<>();
        homeFragment = new HomeFragment();
        mDYMusicFragment = new DYMusicFragment();
        mSheetListFragment = SheetListFragment.newInstance();

        list.add(homeFragment);
        list.add(mDYMusicFragment);
        list.add(mSheetListFragment);
        viewPagerAdapter.setList(list);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, mtoolbar, R.string.drawer_open, R.string.drawer_close);
        actionBarDrawerToggle.syncState();
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        //显示底部导航
        bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        bottomNavigationBar.setBarBackgroundColor("#FCFCFC");
        bottomNavigationBar.addItem(new BottomNavigationItem(R.drawable.icon_bottom_menu_dianpu, "店铺").setInActiveColor(R.color.colorbttonfont).setActiveColorResource(R.color.main_bg))
                .addItem(new BottomNavigationItem(R.drawable.icon_bottom_menu_jinxuan, "精选").setInActiveColor(R.color.colorbttonfont).setActiveColorResource(R.color.main_bg))
                .addItem(new BottomNavigationItem(R.drawable.icon_bottom_menu_faxian, "发现").setInActiveColor(R.color.colorbttonfont).setActiveColorResource(R.color.main_bg))
                .setFirstSelectedPosition(0)
                .initialise();

        //侧边栏NavgationView的监听
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(false);//设置选项是否选中
                item.setCheckable(false);//选项是否可选
                //drawerLayout.closeDrawers();
                NaviMenuExecutor naviMenuExecutor = new NaviMenuExecutor(MainActivity.this);
                return naviMenuExecutor.onNavigationItemSelected(item);
            }
        });

        //底部导航监听事件
        bottomNavigationBar.setTabSelectedListener(this);
    }

    //主题选择
    private void theme_choose(View view) {
        view.findViewById(R.id.theme_black).setOnClickListener(this);
        view.findViewById(R.id.theme_blue).setOnClickListener(this);
        view.findViewById(R.id.theme_pink).setOnClickListener(this);
        view.findViewById(R.id.theme_purple).setOnClickListener(this);
        view.findViewById(R.id.theme_yellow).setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //显示这个的搜索绑定
        MenuItem searchItem = menu.findItem(R.id.search);
        searchItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(bottomNavigationBar.getCurrentSelectedPosition() == 0)
                {
                    homeFragment.goSearchShopActivity(getApplicationContext());
                }
                else if(bottomNavigationBar.getCurrentSelectedPosition() == 1)
                {
                    showSearTypeDialog();
                }
                else if(bottomNavigationBar.getCurrentSelectedPosition() == 2)
                {
                    mSheetListFragment.goSearchMusicActivity(getApplicationContext());
                }
                return false;
            }
        });
        MenuItem addItem = menu.findItem(R.id.add);
        addItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(bottomNavigationBar.getCurrentSelectedPosition() == 0)
                {
                    homeFragment.goAddShopActivity(MainActivity.this);
                }

                return false;
            }
        });
        MenuItem planItem = menu.findItem(R.id.plan);
        planItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                homeFragment.goPlanActivity(MainActivity.this);
                return false;
            }
        });

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        /*MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                System.out.println("open");
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                System.out.println("close");
                return true;
            }
        });*/
        return super.onPrepareOptionsMenu(menu);
    }

    private void showSearTypeDialog()
    {
        new AlertView("请选择搜索类型",null, "取消", null,
                getResources().getStringArray(R.array.search_sys_album_dialog),
                this, AlertView.Style.ActionSheet, new OnItemClickListener(){
            public void onItemClick(Object o,int which){
                switch (which) {
                    case 0://
                        mDYMusicFragment.goSearchAlbumActivity(getApplicationContext());
                        break;
                    case 1://
                        mDYMusicFragment.goSearchMusicActivity(getApplicationContext());
                        break;
                }
            }
        }).show();
    }


    //底部导航监听事件
    @Override
    public void onTabSelected(int position) {
        viewPager.setCurrentItem(position);
        switch (position) {
            case 0:
                mtoolbar.setTitle("我的店铺");

                mtoolbar.getMenu().findItem(R.id.search).setVisible(true);
                mtoolbar.getMenu().findItem(R.id.add).setVisible(true);
                break;
            case 1:
                mtoolbar.setTitle("精选歌单");
                mtoolbar.getMenu().findItem(R.id.search).setVisible(true);
                mtoolbar.getMenu().findItem(R.id.add).setVisible(false);

                break;
            case 2:
                mtoolbar.setTitle("在线音乐");
                mtoolbar.getMenu().findItem(R.id.search).setVisible(true);
                mtoolbar.getMenu().findItem(R.id.add).setVisible(false);
                break;
        }
    }
    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

    }

    //头像登录监听
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_image_before:
                drawerLayout.closeDrawers();//关闭navigationview
                startActivity(new Intent(this, OnlineMusicActivity.class));//启动用户登录界面
                break;
            case R.id.theme_black:
                mtoolbar.setBackgroundColor(Color.parseColor("#000000"));
                editor.putString("theme", "#000000");
                editor.commit();
                break;
            case R.id.theme_blue:
                mtoolbar.setBackgroundColor(Color.parseColor("#3F51B5"));
                editor.putString("theme", "#3F51B5");
                editor.commit();
                break;
            case R.id.theme_pink:
                mtoolbar.setBackgroundColor(Color.parseColor("#d4237a"));
                editor.putString("theme", "#d4237a");
                editor.commit();
                break;
            case R.id.theme_purple:
                mtoolbar.setBackgroundColor(Color.parseColor("#6A5ACD"));
                editor.putString("theme", "#6A5ACD");
                editor.commit();
                break;
            case R.id.theme_yellow:
                mtoolbar.setBackgroundColor(Color.parseColor("#FF7F00"));
                editor.putString("theme", "#FF7F00");
                editor.commit();
                break;
        }
    }

    @Override
    public void onBackPressed() {

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
            Toast.makeText(this, getResources().getString(R.string.app_close_hint), Toast.LENGTH_SHORT).show();;
            touchTime = System.currentTimeMillis();
        }

        //super.onBackPressed();
    }

    private long touchTime = 0;
    private void closeApp()
    {
        ActivityManager.getInstance().exitApp();
    }

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
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION

        };
    }
    @Override
    public void requestPermissionsSuccess()
    {
        //权限请求用户已经全部允许

    }

    @Override
    public void requestPermissionsFail() {
        //权限请求不被用户允许。可以提示并退出或者提示权限的用途并重新发起权限申请。
        showPermissions();
        //mPermissionHelper.requestPermissions();
        //close();
    }

    private void showPermissions(){
        final Dialog dialog=new android.app.AlertDialog.Builder(this).create();
        View v=LayoutInflater.from(this).inflate(R.layout.dialog_permissions,null);
        dialog.show();
        dialog.setContentView(v);

        Button btn_add= (Button) v.findViewById(R.id.btn_add);
        Button btn_diss= (Button) v.findViewById(R.id.btn_diss);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPermissionHelper.toPermissionSetting(MainActivity.this);
                dialog.dismiss();
                close();
            }
        });

        btn_diss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                close();
            }
        });
    }

    private void close()
    {
        System.exit(0);
    }
}
