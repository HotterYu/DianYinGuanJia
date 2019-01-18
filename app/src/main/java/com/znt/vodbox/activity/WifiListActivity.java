package com.znt.vodbox.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.znt.vodbox.R;
import com.znt.vodbox.adapter.WifiListAdapter;
import com.znt.vodbox.bean.WifiBean;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.permission.PermissionHelper;
import com.znt.vodbox.permission.PermissionInterface;
import com.znt.vodbox.utils.WifiSupport;
import com.znt.vodbox.utils.binding.Bind;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by prize on 2018/11/9.
 */

public class WifiListActivity extends BaseActivity implements PermissionInterface
{

    @Bind(R.id.tv_common_title)
    private TextView tvTopTitle = null;
    @Bind(R.id.iv_common_back)
    private ImageView ivTopReturn = null;
    @Bind(R.id.iv_common_more)
    private ImageView ivTopMore = null;

    private static final String TAG = "MainActivity";

    private PermissionHelper mPermissionHelper;

    ProgressBar pbWifiLoading;

    List<WifiBean> realWifiList = new ArrayList<>();

    private WifiListAdapter adapter;

    private RecyclerView recyWifiList;

    private WifiBroadcastReceiver wifiReceiver;

    private int connectType = 0;//1：连接成功？ 2 正在连接（如果wifi热点列表发生变需要该字段）

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_list);

        tvTopTitle.setText(getResources().getString(R.string.dev_wifi_select));
        ivTopMore.setVisibility(View.GONE);

        ivTopReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initRecycler();

        pbWifiLoading = (ProgressBar) this.findViewById(R.id.pb_wifi_loading);

        hidingProgressBar();

        mPermissionHelper = new PermissionHelper(WifiListActivity.this, this);
        mPermissionHelper.requestPermissions();


    }

    private void initRecycler() {
        recyWifiList = (RecyclerView) this.findViewById(R.id.recy_list_wifi);
        adapter = new WifiListAdapter(this,realWifiList);
        recyWifiList.setLayoutManager(new LinearLayoutManager(this));
        recyWifiList.setAdapter(adapter);

        adapter.setOnItemClickListener(new WifiListAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int postion, Object o)
            {

                WifiBean mWifiBean = realWifiList.get(postion);

                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("WIFI_NAME",mWifiBean.getWifiName());
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void noConfigurationWifi(int position) {//之前没配置过该网络， 弹出输入密码界面
        /*WifiLinkDialog linkDialog = new WifiLinkDialog(this,R.style.dialog_download,realWifiList.get(position).getWifiName(), realWifiList.get(position).getCapabilities());
        if(!linkDialog.isShowing()){
            linkDialog.show();
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        //注册广播
        wifiReceiver = new WifiBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);//监听wifi是开关变化的状态
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);//监听wifi连接状态广播,是否连接了一个有效路由
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);//监听wifi列表变化（开启一个热点或者关闭一个热点）
        this.registerReceiver(wifiReceiver, filter);

        if(WifiSupport.isOpenWifi(this)){
            wifiListChange();
        }else{
            Toast.makeText(this,"WIFI处于关闭状态或权限获取失败22222",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(wifiReceiver);
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

        if(WifiSupport.isOpenWifi(this)){
            wifiListChange();
        }else{
            Toast.makeText(this,"WIFI处于关闭状态或权限获取失败22222",Toast.LENGTH_SHORT).show();
        }
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
        View v= LayoutInflater.from(this).inflate(R.layout.dialog_permissions,null);
        dialog.show();
        dialog.setContentView(v);

        Button btn_add= (Button) v.findViewById(R.id.btn_add);
        Button btn_diss= (Button) v.findViewById(R.id.btn_diss);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPermissionHelper.toPermissionSetting(WifiListActivity.this);
                dialog.dismiss();

            }
        });

        btn_diss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });
    }

    //监听wifi状态
    public class WifiBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())){
                Log.d(TAG,"网络列表变化了");
                wifiListChange();
            }
        }
    }

    /**
     * //网络状态发生改变 调用此方法！
     */
    public void wifiListChange(){
        sortScaResult();
        WifiInfo connectedWifiInfo = WifiSupport.getConnectedWifiInfo(this);
        if(connectedWifiInfo != null){
            wifiListSet(connectedWifiInfo.getSSID(),connectType);
        }
    }

    /**
     * 将"已连接"或者"正在连接"的wifi热点放置在第一个位置
     * @param wifiName
     * @param type
     */
    public void wifiListSet(String wifiName , int type){
        int index = -1;
        WifiBean wifiInfo = new WifiBean();
        if(isNullOrEmpty(realWifiList)){
            return;
        }
        for(int i = 0;i < realWifiList.size();i++){
            realWifiList.get(i).setState(Constant.WIFI_STATE_UNCONNECT);
        }
        Collections.sort(realWifiList);//根据信号强度排序
        for(int i = 0;i < realWifiList.size();i++){
            WifiBean wifiBean = realWifiList.get(i);
            if(index == -1 && ("\"" + wifiBean.getWifiName() + "\"").equals(wifiName)){
                index = i;
                wifiInfo.setLevel(wifiBean.getLevel());
                wifiInfo.setWifiName(wifiBean.getWifiName());
                wifiInfo.setCapabilities(wifiBean.getCapabilities());
                if(type == 1){
                    wifiInfo.setState(Constant.WIFI_STATE_CONNECT);
                }else{
                    wifiInfo.setState(Constant.WIFI_STATE_ON_CONNECTING);
                }
            }
        }
        if(index != -1){
            realWifiList.remove(index);
            realWifiList.add(0, wifiInfo);
            adapter.notifyDataSetChanged();
        }
    }

    public boolean isNullOrEmpty(Collection c) {
        if (null == c || c.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * 获取wifi列表然后将bean转成自己定义的WifiBean
     */
    public void sortScaResult(){
        List<ScanResult> scanResults = WifiSupport.noSameName(WifiSupport.getWifiScanResult(this));
        realWifiList.clear();
        if(!isNullOrEmpty(scanResults)){
            for(int i = 0;i < scanResults.size();i++){
                WifiBean wifiBean = new WifiBean();
                wifiBean.setWifiName(scanResults.get(i).SSID);
                wifiBean.setState(Constant.WIFI_STATE_UNCONNECT);   //只要获取都假设设置成未连接，真正的状态都通过广播来确定
                wifiBean.setCapabilities(scanResults.get(i).capabilities);
                wifiBean.setLevel(WifiSupport.getLevel(scanResults.get(i).level)+"");
                realWifiList.add(wifiBean);

                //排序
                Collections.sort(realWifiList);
                adapter.notifyDataSetChanged();
            }
        }
    }
/*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasAllPermission = true;
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int i : grantResults) {
                if (i != PackageManager.PERMISSION_GRANTED) {
                    hasAllPermission = false;   //判断用户是否同意获取权限
                    break;
                }
            }

            //如果同意权限
            if (hasAllPermission) {
                mHasPermission = true;
                if(WifiSupport.isOpenWifi(this) && mHasPermission){  //如果wifi开关是开 并且 已经获取权限
                    initRecycler();
                }else{
                    Toast.makeText(this,"WIFI处于关闭状态或权限获取失败1111",Toast.LENGTH_SHORT).show();
                }

            } else {  //用户不同意权限
                mHasPermission = false;
                Toast.makeText(this,"获取权限失败",Toast.LENGTH_SHORT).show();
            }
        }
    }*/


    public void showProgressBar() {
        pbWifiLoading.setVisibility(View.VISIBLE);
    }

    public void hidingProgressBar() {
        pbWifiLoading.setVisibility(View.GONE);
    }

}
