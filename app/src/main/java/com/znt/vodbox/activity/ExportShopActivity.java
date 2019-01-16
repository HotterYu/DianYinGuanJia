package com.znt.vodbox.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.vodbox.bean.ShopListResultBean;
import com.znt.vodbox.email.EmailSenderManager;
import com.znt.vodbox.entity.LocalDataEntity;
import com.znt.vodbox.http.HttpCallback;
import com.znt.vodbox.http.HttpClient;
import com.znt.vodbox.model.Shopinfo;
import com.znt.vodbox.model.UserInfo;
import com.znt.vodbox.utils.DateUtils;
import com.znt.vodbox.utils.DeviceComparator;
import com.znt.vodbox.utils.ExcelUtils;
import com.znt.vodbox.utils.binding.Bind;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A login screen that offers login via email/password.
 */
public class ExportShopActivity extends BaseActivity{

    @Bind(R.id.tv_common_title)
    private TextView tvTopTitle = null;
    @Bind(R.id.tv_common_confirm)
    private TextView tvOpen = null;
    @Bind(R.id.iv_common_back)
    private ImageView ivTopReturn = null;
    @Bind(R.id.iv_common_more)
    private ImageView ivTopMore = null;

    private TextView tvExport = null;
    private TextView tvSend = null;
    private EditText etEmail = null;

    private List<Shopinfo> allShopList = new ArrayList<>();
    private List<Shopinfo> localDevices = new ArrayList<>();
    private String[] title = {"编号","门店ID",  "门店名称", "门店位置", "最后播放歌曲", "最后在线时间" , "在线状态"};

    private EmailSenderManager emailManager = null;

    private UserInfo mUserInfo = null;

    private File xlsFile = null;
    private File localXlsFile = null;
    private File localXlsFileExport = null;
    String adminName = null;

    private String dirPath = getSDPath() + "/DianYinGuanJia/export";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_shop);

        tvTopTitle.setText(getResources().getString(R.string.export_shop));
        ivTopMore.setVisibility(View.GONE);
        tvOpen.setVisibility(View.VISIBLE);
        tvOpen.setText("打开");
        ivTopReturn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvExport = (TextView)findViewById(R.id.tv_excel_export);
        tvSend = (TextView)findViewById(R.id.tv_excel_send);
        etEmail = (EditText)findViewById(R.id.et_excel_email);

        mUserInfo = LocalDataEntity.newInstance(getApplicationContext()).getUserInfor();

        etEmail.setText(getLocalData().getExcelEmail());

        emailManager = new EmailSenderManager();
        adminName = getExcelName();
        File file = new File(dirPath);
        makeDir(file);
        xlsFile = new File(file.getAbsolutePath() + "/" + adminName + ".xls");
        localXlsFile = new File(dirPath + "/" + "shops.xls");
        localXlsFileExport = new File(dirPath + "/" + "shops_filter.xls");

        tvExport.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
            if(xlsFile != null && xlsFile.exists())
                xlsFile.delete();
            pageNo = 1;
            getData();
            }
        });

        tvSend.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                String email = etEmail.getText().toString();
                if(TextUtils.isEmpty(email))
                {
                    showToast("请输入有效的邮箱地址");
                    return;
                }
                sendEmail(email);
                getLocalData().setExcelEmail(email);

            }
        });

        tvOpen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(xlsFile == null || !xlsFile.exists())
                {
                    showToast("本地报表不存在，请先导出报表");
                    return;
                }

                openFile(xlsFile);
            }
        });
    }

    private void sendEmail(String email)
    {
        try
        {
            if(xlsFile != null && xlsFile.exists())
            {
                emailManager.sendEmail("店音系统门店音乐播放报表", "您好，附件为贵公司店音系统门店音乐播放报表，请查收！"
                        + "\n"
                        + "如有任何疑问，请随时联系我们"
                        + "\n"
                        + "深圳助你科技有限公司（店音）\n https://www.zhunit.com", new String[]{email}, xlsFile);
                showToast("报表发送成功");
            }
            else
                showToast("文件不存在，请先导出报表");
        }
        catch (Exception e)
        {
            Log.e("", "an error occured while writing file...", e);
        }
    }

    private int pageNo = 1;
    private int pageSize = 100;
    private List<Shopinfo> dataList = new ArrayList<>();
    public void getData()
    {

        String token = mUserInfo.getToken();

        String merchId = "";
        //String merchId = mUserInfo.getMerchant().getId();
        String groupId = "";
        String memberId = "";
        String name = "";
        String shopCode = "";
        String userShopCode = "";

        try
        {
            HttpClient.getAllShops(token, pageNo + "", pageSize + "",merchId,groupId,memberId,name,shopCode,userShopCode,""
                    , new HttpCallback<ShopListResultBean>() {
                        @Override
                        public void onSuccess(ShopListResultBean resultBean) {

                            if(resultBean.getResultcode().equals("1"))
                            {

                                List<Shopinfo> tempList = resultBean.getData();

                                if(pageNo == 1)
                                    dataList.clear();

                                allShopList.addAll(tempList);

                                if(tempList.size() == pageSize)
                                {
                                    pageNo ++;
                                    getData();
                                }
                                else
                                {
                                    Collections.sort(allShopList, new DeviceComparator());//通过重写Comparator的实现类FileComparator来实现按文件创建时间排
                                    startAllShopExport() ;
                                    showToast("导出完成");
                                    dismissDialog();

                                    String email = etEmail.getText().toString();
                                    if(!TextUtils.isEmpty(email))
                                    {
                                        sendEmail(email);
                                    }
                                }
                            }
                            else
                            {
                                showToast(resultBean.getMessage());
                            }
                        }

                        @Override
                        public void onFail(Exception e) {
                            if(e != null)
                                showToast(e.getMessage());
                            else
                                showToast("加载数据失败");
                        }
                    });
        }
        catch (Exception e)
        {
            if(e != null)
                showToast(e.getMessage());
            else
                showToast("加载数据失败");
        }

    }

    @SuppressLint("SimpleDateFormat")
    public void startAllShopExport()
    {
        ExcelUtils.initExcel(xlsFile.getAbsolutePath(), title, adminName);
        ExcelUtils.writeObjListToExcel(initLoacalDevices(allShopList), dirPath + "/" + adminName + ".xls", ExportShopActivity.this);
    }

    private String getExcelName()
    {
        String adminName = getLocalData().getUserName();
        if(TextUtils.isEmpty(adminName))
            adminName = "门店音乐播放情况";
        else
            adminName = adminName + "的门店音乐情况";
        return adminName;
    }

    private ArrayList<ArrayList<String>> initLoacalDevices(List<Shopinfo> tempList)
    {
        ArrayList<ArrayList<String>> shopList = new ArrayList<>();
        //List<DeviceInfor> tempList = DBManager.newInstance(getApplicationContext()).getDeviceList();
        for(int i=0;i<tempList.size();i++)
        {
            Shopinfo tempInfor = tempList.get(i);
            ArrayList<String> beanList = new ArrayList<String>();
            beanList.add((i + (pageNo - 1)*pageSize) + "");
            beanList.add(tempInfor.getId());
            beanList.add(tempInfor.getName());
            if(TextUtils.isEmpty(tempInfor.getAddress()))
                beanList.add("未知");
            else
                beanList.add(tempInfor.getAddress());
            if(tempInfor.getTmlRunStatus() != null &&tempInfor.getTmlRunStatus().size() > 0)
            {
                beanList.add(tempInfor.getTmlRunStatus().get(0).getPlayingSong());
                long lastOnLine = 0;
                if(!TextUtils.isEmpty(tempInfor.getTmlRunStatus().get(0).getLastBootTime()))
                {
                    lastOnLine = Long.parseLong(tempInfor.getTmlRunStatus().get(0).getLastBootTime());
                    beanList.add(DateUtils.getStringTimeChinese(lastOnLine));
                }
                if(tempInfor.getTmlRunStatus().get(0).getOnlineStatus().equals("1"))
                    beanList.add("√ 在线");
                else if(isOnlineToday(lastOnLine))
                {
                    long offlineTime = System.currentTimeMillis() - lastOnLine;
                    beanList.add("× 当天掉线：" + DateUtils.getTimeFromLong(offlineTime));
                }
                else
                {
                    long offlineTime = System.currentTimeMillis() - lastOnLine;
                    beanList.add("× 长期掉线：" + DateUtils.getTimeFromLong(offlineTime));
                }
            }

            shopList.add(beanList);
        }
        //Toast.makeText(getApplicationContext(), tempList.size() + "   " + shopList.size(), 0).show();
        return shopList;
    }

    private boolean isOnlineToday(long lastOnLine)
    {
        long internalTime = System.currentTimeMillis() - lastOnLine;
        if(internalTime < 24 * 60 * 60 * 1000)
            return true;
        return false;
    }

    public void makeDir(File dir) {
        if (!dir.getParentFile().exists()) {
            makeDir(dir.getParentFile());
        }
        dir.mkdir();
    }

    public String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
        }
        String dir = sdDir.toString();
        return dir;

    }

    /**
     * 打开文件
     * @param file
     */
    private void openFile(File file)
    {

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //设置intent的Action属性
        intent.setAction(Intent.ACTION_VIEW);
        //获取文件file的MIME类型
        intent.setDataAndType (Uri.fromFile(file), "application/vnd.ms-excel");
        try
        {
            startActivity(intent); //这里最好try一下，有可能会报错。 //比如说你的MIME类型是打开邮箱，但是你手机里面没装邮箱客户端，就会报错。
        }
        catch (Exception e)
        {
            // TODO: handle exception
            showToast("打开失败，或者您的手机没有安装excel软件");
        }
    }

}

