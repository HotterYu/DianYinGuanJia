package com.znt.vodbox.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.tamic.novate.Novate;
import com.znt.vodbox.R;
import com.znt.vodbox.fragment.first.DYMusicFragment;
import com.znt.vodbox.fragment.first.HomeFragment;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener, View.OnClickListener {

    private Toolbar mtoolbar;
    private HomeFragment homeFragment;
    private DYMusicFragment mDYMusicFragment;
    private HomeFragment videoFragment;
    private HomeFragment weiTouTiao;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private CircleImageView imageView;
    private BottomNavigationBar bottomNavigationBar = null;
    private View headerlayout;
    private Novate novate;
    private String username;
    private int followers;
    private int following;
    private String api_token;
    private Boolean message = true;
    private SharedPreferences sp;

    private BottomSheetDialog dialog;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        homeFragment = new HomeFragment();
        mDYMusicFragment = new DYMusicFragment();
        videoFragment = new HomeFragment();
        weiTouTiao = new HomeFragment();

        //navgationview
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setItemIconTintList(null);
        //绑定headerlayout
        get_info();

        //初始化
        initView();
    }

    //获取用户信息并且判断是否登录
    private void get_info() {
        sp = getSharedPreferences("user_auth", Activity.MODE_PRIVATE);
        editor = sp.edit();
        message = sp.getBoolean("message", false);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        if (!message) {
            navigationView.addHeaderView(layoutInflater.inflate(R.layout.navigation_header_before, navigationView, false));
            headerlayout = navigationView.getHeaderView(0);
            imageView = (CircleImageView) headerlayout.findViewById(R.id.profile_image_before);
            imageView.setOnClickListener(this);
        } else {
            navigationView.addHeaderView(layoutInflater.inflate(R.layout.navigation_header, navigationView, false));
            headerlayout = navigationView.getHeaderView(0);
            TextView tv_header = (TextView) headerlayout.findViewById(R.id.tv_header);
            TextView followers = (TextView) headerlayout.findViewById(R.id.followers);
            TextView following = (TextView) headerlayout.findViewById(R.id.following);
            tv_header.setText(sp.getString("name", "Laravel"));
            followers.setText(sp.getInt("followers", 0) + "");
            following.setText(sp.getInt("following", 0) + "");
        }
    }


    public void initView() {
        //显示toolbar
        mtoolbar = (Toolbar) findViewById(R.id.toolbar);
        mtoolbar.setTitle("今日头条 - 新闻");
        setSupportActionBar(mtoolbar);
        mtoolbar.setBackgroundColor(Color.parseColor(sp.getString("theme", "#ec6400")));
        //绑定侧边栏
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, mtoolbar, R.string.drawer_open, R.string.drawer_close);
        actionBarDrawerToggle.syncState();
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        //显示底部导航
        bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        bottomNavigationBar.setBarBackgroundColor("#FCFCFC");
        bottomNavigationBar.addItem(new BottomNavigationItem(R.drawable.home2, "店铺").setInActiveColor(R.color.colorbttonfont).setActiveColorResource(R.color.main_bg))
                .addItem(new BottomNavigationItem(R.drawable.comment, "发现").setInActiveColor(R.color.colorbttonfont).setActiveColorResource(R.color.main_bg))
                .addItem(new BottomNavigationItem(R.drawable.photo, "图片").setInActiveColor(R.color.colorbttonfont).setActiveColorResource(R.color.main_bg))
                .addItem(new BottomNavigationItem(R.drawable.play, "我的").setInActiveColor(R.color.colorbttonfont).setActiveColorResource(R.color.main_bg))
                .setFirstSelectedPosition(0)
                .initialise();
        setDefaultFragment();

        //侧边栏NavgationView的监听
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(false);//设置选项是否选中
                item.setCheckable(false);//选项是否可选
                switch (item.getItemId()) {
                    case R.id.item_setting:
                        if (message) {
                            //startActivity(new Intent(MainActivity.this, Se.class));
                        } else {
                            alert_info();
                        }
                        break;
                    case R.id.item_theme:
                        if (message)
                        {
                            dialog = new BottomSheetDialog(MainActivity.this);
                            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.theme_choose, null);
                            dialog.setContentView(view);
                            dialog.show();
                            theme_choose(view);
                        } else {
                            alert_info();
                        }
                        break;
                    case R.id.item_love:
                        if (message) {
                        } else {
                            alert_info();
                        }
                        break;
                    case R.id.item_share:
                        /*if (message) {
                            dialog = new BottomSheetDialog(MainActivity.this);
                            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.user_share, null);
                            dialog.setContentView(view);
                            dialog.show();
                            user_share(view, dialog);
                        } else {
                            alert_info();
                        }*/
                        break;
                    case R.id.logout:
                        if (message) {
                            new AlertDialog.Builder(MainActivity.this).setTitle("退出登录").setMessage("确认退出登录吗?").setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    logout();
                                }
                            }).show();
                        } else {
                            alert_info();
                        }
                        break;
                }
                drawerLayout.closeDrawers();
                return true;
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


    //设置启动页
    private void setDefaultFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.maindfragment, homeFragment).commit();
    }

    //toolbar的监听


    //请先登录的提示
    public void alert_info() {
        new AlertDialog.Builder(MainActivity.this).setTitle("消息提示").setMessage("请您先登录?").setNegativeButton("确定", null).show();
    }

    //退出登录
    public void logout() {
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
        Intent i = getIntent();
        finish();
        startActivity(i);
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
                else if(bottomNavigationBar.getCurrentSelectedPosition() == 2)
                {
                    mDYMusicFragment.goSearchMusicActivity(getApplicationContext());
                }
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

    //底部导航监听事件
    @Override
    public void onTabSelected(int position) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        switch (position) {
            case 0:
                mtoolbar.setTitle("我们的店铺");
                ft.replace(R.id.maindfragment, homeFragment).commit();
                break;
            case 1:
                ft.replace(R.id.maindfragment, weiTouTiao).commit();
                mtoolbar.setTitle("在线音乐");
                break;
            case 2:
                ft.replace(R.id.maindfragment, mDYMusicFragment).commit();
                mtoolbar.setTitle("系统音乐");
                break;
            case 3:
                ft.replace(R.id.maindfragment, videoFragment).commit();
                mtoolbar.setTitle("我的");
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
                startActivity(new Intent(this, UserLoginAndRegisterActivity.class));//启动用户登录界面
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
        //closeAllActivity();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}
