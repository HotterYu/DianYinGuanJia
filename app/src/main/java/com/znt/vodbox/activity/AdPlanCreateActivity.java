package com.znt.vodbox.activity;

import android.app.TimePickerDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.znt.vodbox.R;
import com.znt.vodbox.adapter.AdSelectListAdapter;
import com.znt.vodbox.adapter.OnMoreClickListener;
import com.znt.vodbox.bean.AdMediaInfo;
import com.znt.vodbox.bean.AdPlanInfo;
import com.znt.vodbox.bean.SubAdPlanInfo;
import com.znt.vodbox.dialog.AdPushTypeBottomDialog;
import com.znt.vodbox.dialog.TimeSelectBottomDialog;
import com.znt.vodbox.dialog.WheelListDialog;
import com.znt.vodbox.utils.DateUtils;
import com.znt.vodbox.utils.ViewUtils;
import com.znt.vodbox.utils.binding.Bind;
import com.znt.vodbox.view.ItemTextView;
import com.znt.vodbox.view.SwitchButton;
import com.znt.vodbox.view.xlistview.LJListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AdPlanCreateActivity extends  BaseActivity implements OnClickListener
        , OnItemClickListener, LJListView.IXListViewListener,OnMoreClickListener
{


    @Bind(R.id.tv_common_title)
    private TextView tvTopTitle = null;
    @Bind(R.id.iv_common_back)
    private ImageView ivTopReturn = null;
    @Bind(R.id.iv_common_more)
    private ImageView ivTopMore = null;
    @Bind(R.id.tv_common_confirm)
    private TextView tvConfirm = null;

    private View headerView = null;
    private LJListView listView = null;

    private ItemTextView itvTimeSelect = null;
    private ItemTextView itvInsetCount = null;
    private ItemTextView itvInsertMusic = null;
    private ItemTextView itvWeekSelect = null;
    private ItemTextView itvPushTimeing = null;
    private SwitchButton switchButtonTimeing = null;

    private String startTimes = "";
    private String endTimes = "";
    private String loopAddNum = "1";
    private String cycleType = "0";
    private String pushTime = "";
    private String playModel = "1";//1间隔时间插播   2定时插播  3间隔时间插播
    private boolean isEdit = false;

    private AdPlanInfo mAdPlanInfo = null;

    private AdSelectListAdapter mAdSelectListAdapter = null;
    private List<AdMediaInfo> selectAdList = new ArrayList<AdMediaInfo>();

    /**
     *callbacks
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ad_plan_create);

        tvTopTitle.setText(getResources().getString(R.string.ad_plan_detail_add_time));
        ivTopMore.setVisibility(View.GONE);
        tvConfirm.setVisibility(View.VISIBLE);
        tvConfirm.setText(getResources().getString(R.string.save));
        ivTopReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAndFeedBack();
            }
        });

        headerView = LayoutInflater.from(getActivity()).inflate(R.layout.view_ad_plan_create_list_header, null);


        switchButtonTimeing = (SwitchButton)headerView.findViewById(R.id.sb_plan_detail_broadcast_timeing);
        itvTimeSelect = (ItemTextView)headerView.findViewById(R.id.itv_plan_detail_time_select);
        itvInsetCount = (ItemTextView)headerView.findViewById(R.id.itv_plan_detail_broadcast_count);
        itvInsertMusic = (ItemTextView)headerView.findViewById(R.id.itv_plan_detail_broadcast_music);
        itvWeekSelect = (ItemTextView)headerView.findViewById(R.id.itv_plan_detail_push_week);
        itvPushTimeing = (ItemTextView)headerView.findViewById(R.id.itv_plan_detail_broadcast_timeing);

        itvTimeSelect.getFirstView().setText(getResources().getString(R.string.play_time_space));
        itvInsetCount.getFirstView().setText(getResources().getString(R.string.push_mode_set));
        itvInsertMusic.getFirstView().setText(getResources().getString(R.string.select_push_ad));
        itvInsertMusic.getSecondView().setText("请选择要插播的文件");
        itvInsertMusic.getFirstView().setTextColor(getResources().getColor(R.color.text_blue_on));
        itvInsertMusic.getMoreView().setImageResource(R.drawable.icon_add_on);

        itvWeekSelect.getFirstView().setText(getResources().getString(R.string.week_select));
        itvWeekSelect.getSecondView().setText(getResources().getString(R.string.week_every));
        itvPushTimeing.getFirstView().setText(getResources().getString(R.string.select_timeing));
        itvTimeSelect.showMoreButton(true);
        itvInsetCount.showMoreButton(true);
        itvInsertMusic.showMoreButton(true);
        itvWeekSelect.showMoreButton(true);
        itvPushTimeing.showMoreButton(true);
        itvTimeSelect.getBgView().setOnClickListener(this);
        itvInsetCount.getBgView().setOnClickListener(this);
        itvInsertMusic.getBgView().setOnClickListener(this);
        itvWeekSelect.getBgView().setOnClickListener(this);
        itvPushTimeing.getBgView().setOnClickListener(this);

        listView = (LJListView)findViewById(R.id.ptrl_plan_create);

        switchButtonTimeing.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1)
            {
                // TODO Auto-generated method stub
                showTimeingPushSet(arg1);
            }
        });

		listView.getListView().setDivider(getResources().getDrawable(R.color.transparent));
		listView.getListView().setDividerHeight(1);
		listView.setPullLoadEnable(true,"共5条数据"); //如果不想让脚标显示数据可以mListView.setPullLoadEnable(false,null)或者mListView.setPullLoadEnable(false,"")
		listView.setPullRefreshEnable(true);
		listView.setIsAnimation(true);
		listView.setXListViewListener(this);
		listView.showFootView(false);
		listView.setRefreshTime();
        listView.addHeader(headerView);
        listView.setVisibility(View.VISIBLE);
        listView.setOnItemClickListener(this);

        mAdSelectListAdapter = new AdSelectListAdapter(getApplicationContext(),selectAdList);

        listView.setAdapter(mAdSelectListAdapter);

        isEdit = getIntent().getBooleanExtra("IS_EDIT", false);
        mAdPlanInfo = (AdPlanInfo)getIntent().getSerializableExtra("AD_PLAN_INFO");

        if(!isEdit)
        {
            showCountInternalPush("1");
            showCycleType("0");
        }
        else
        {
            showCurPlanInfor();
        }

        if(mAdPlanInfo != null && mAdPlanInfo.getPlanModel().equals("2"))//定时播放
        {
            switchButtonTimeing.setChecked(true);
            showTimeingPushSet(true);
        }
        else
        {
            switchButtonTimeing.setChecked(false);
            showTimeingPushSet(true);
        }

        //listView.onFresh();
    }

    private void onLoad(int updateCount)
    {
        listView.setCount(updateCount);
        listView.stopLoadMore();
        listView.stopRefresh();
        listView.setRefreshTime();
    }

    private void showCountInternalPush(String loopAddNum)
    {
        if(loopAddNum.startsWith("0"))
        {
            String num = loopAddNum.substring(1);
            itvInsetCount.getSecondView().setText("间隔 " + num + " 分钟插播");
        }
        else
        {
            itvInsetCount.getSecondView().setText("间隔 " + loopAddNum + " 首歌曲插播");
        }
    }

    private void showTimeingPushSet(boolean isShow)
    {
        if(isShow)
        {
            itvPushTimeing.setVisibility(View.VISIBLE);
            itvInsertMusic.getFirstView().setText(getResources().getString(R.string.select_push_ad));
        }
        else
        {
            itvPushTimeing.setVisibility(View.GONE);
            itvInsertMusic.getFirstView().setText(getResources().getString(R.string.select_push_ad_timeing));
        }
    }

    private void showCurPlanInfor()
    {
        SubAdPlanInfo tempInfor = mAdPlanInfo.getSelelctPlanInfor();
        if(tempInfor != null)
        {
            //tvAddAlbum.setText(subPlanInfor.getPlanAlbumName());
            selectAdList.clear();
            selectAdList.addAll(tempInfor.getAdMediaInfoList());

            loopAddNum = tempInfor.getMusicNum();
            showCountInternalPush(loopAddNum);

            startTimes = tempInfor.getStartTime();
            endTimes = tempInfor.getEndTime();

            itvTimeSelect.getSecondView().setText(startTimes + " 到 " + endTimes);

            cycleType = tempInfor.getCycleType();

            if(tempInfor.isTimePushModel())
            {
                switchButtonTimeing.setChecked(true);
                showTimeingPushSet(true);
                itvPushTimeing.getSecondView().setText(tempInfor.getStartTime());
            }
            else
            {
                switchButtonTimeing.setChecked(false);
                showTimeingPushSet(false);
            }

            showCycleType(cycleType);

            if(!TextUtils.isEmpty(loopAddNum) && !TextUtils.isEmpty(tempInfor.getMusicNum()))
            {
                showCountInternalPush(loopAddNum);
            }
        }
    }

    private void showCycleType(String  cycleType)
    {
        itvWeekSelect.getSecondView().setText(DateUtils.getWeekByCycleType(this,cycleType));
    }

    private void finishAndFeedBack()
    {

        SubAdPlanInfo tempInfor = getSubPlanInfor();
        if(tempInfor == null)
            return;

        if(!isEdit)
            mAdPlanInfo.addSubPlanInfor(tempInfor);
        else
            mAdPlanInfo.updateSelect(tempInfor);

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("AD_PLAN_INFO", mAdPlanInfo);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    private SubAdPlanInfo getSubPlanInfor()
    {
        SubAdPlanInfo tempInfor = mAdPlanInfo.getSelelctPlanInfor();
        if(tempInfor == null)
            tempInfor = new SubAdPlanInfo();

        if(selectAdList.size() == 0)
        {
            showToast(getResources().getString(R.string.push_ad_select_hint));
            return null;
        }

        if(switchButtonTimeing.isChecked())
        {
            //定时广播
            tempInfor.setPlayModel("2");
            tempInfor.setStartTime(pushTime);
            tempInfor.setEndTime(pushTime);

            AdMediaInfo tempMedia = selectAdList.get(0);
            selectAdList.clear();
            selectAdList.add(tempMedia);
        }
        else
        {

            tempInfor.setPlayModel(playModel);

            if(TextUtils.isEmpty(startTimes) || TextUtils.isEmpty(endTimes))
            {
                showToast("请选择起始播放时段");
                return null;
            }
            if(startTimes.equals(endTimes))
            {
                showToast("播放时段起始时间不能一样的哦");
                return null;
            }
            if(TextUtils.isEmpty(startTimes))
            {
                showToast("请设置起始时间");
                return null;
            }
            if(TextUtils.isEmpty(endTimes))
            {
                showToast("请设置结束时间");
                return null;
            }

            if(!mAdPlanInfo.checkPlanTime(startTimes, endTimes))
            {
                showToast("当前时间与计划时间有重叠");
                return null;
            }
            tempInfor.setStartTime(startTimes);
            tempInfor.setEndTime(endTimes);

            if(TextUtils.isEmpty(loopAddNum))
                loopAddNum = "1";
            tempInfor.setMusicNum(loopAddNum);
        }

        tempInfor.setSelectedAdList(selectAdList);

        if(TextUtils.isEmpty(cycleType))
            cycleType = "0";
        tempInfor.setCycleType(cycleType);

        return tempInfor;
    }

    /**
     *callbacks
     */
    @Override
    protected void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode != RESULT_OK)
            return;

        if(requestCode == 1)
        {

        }
        else if(requestCode == 3)
        {
            selectAdList = (List<AdMediaInfo>) data.getSerializableExtra("AD_SELECTED_LIST");
            mAdSelectListAdapter.notifyDataSetChanged(selectAdList);
        }
    }

    @Override
    public void onMoreClick(final int position) {
        final AdMediaInfo tempInfo = selectAdList.get(position);
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(tempInfo.getAdname());

        dialog.setItems(R.array.plan_ad_list_dialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog1, int which) {
                switch (which) {
                    case 0://
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        // 将文本内容放到系统剪贴板里。
                        cm.setText(tempInfo.getAdname() + "\n" + tempInfo.getUrl());
                        showToast("复制成功");
                        break;
                    case 1://
                        selectAdList.remove(position);
                        mAdSelectListAdapter.notifyDataSetChanged(selectAdList);
                        break;
                }
            }
        });
        dialog.show();
    }

    /**
     *callbacks
     */
    @Override
    public void onRefresh()
    {
        // TODO Auto-generated method stub
        listView.stopRefresh();
    }

    /**
     *callbacks
     */
    @Override
    public void onLoadMore() {
        // TODO Auto-generated method stub
        listView.stopRefresh();
    }

    /**
     *callbacks
     */
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub
        if(arg2 > 1)
            arg2 = arg2 - 2;

        if(selectAdList.size() <= 0)
            return;

        AdMediaInfo infor = selectAdList.get(arg2);
        showPlayDialog(infor.getAdname(),infor.getUrl(),infor.getId());
        /*Bundle bundle = new Bundle();
        bundle.putSerializable("AD_MEDIA_INFO", infor);
        //bundle.putSerializable("MusicEditType", MusicEditType.DeleteAdd);
        ViewUtils.startActivity(getActivity(), AdListActivity.class, bundle);*/
    }


    private void showWeekSelectDialog()
    {
        final List<String> data = new ArrayList<>();
        data.add(getResources().getString(R.string.week_every));
        data.add(getResources().getString(R.string.week_monday));
        data.add(getResources().getString(R.string.week_tuesday));
        data.add(getResources().getString(R.string.week_wednesday));
        data.add(getResources().getString(R.string.week_thursday));
        data.add(getResources().getString(R.string.week_friday));
        data.add(getResources().getString(R.string.week_saturday));
        data.add(getResources().getString(R.string.week_sunday));

        final WheelListDialog dialog = new WheelListDialog(getActivity(),data);
        dialog.setListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btOk:
                        String selectWeek = dialog.getPositionData();
                        int realPos = dialog.getRealDataPosition();
                        dialog.dismiss();
                        itvWeekSelect.getSecondView().setText(data.get(realPos));
                        //mSubAdPlanInfo.setCycleType(realPos+"");
                        cycleType = realPos+"";
                        break;
                    case R.id.btCancel:
                        dialog.dismiss();
                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();
    }

    /**
     *callbacks
     */
    @Override
    public void onClick(View v)
    {
        // TODO Auto-generated method stub
        if(v == itvInsertMusic.getBgView())
        {
            Bundle bundle = new Bundle();
            bundle.putBoolean("IS_SELECT", true);
            bundle.putSerializable("AD_SELECTED_LIST", (Serializable) selectAdList);
            ViewUtils.startActivity(getActivity(), AdListActivity.class, bundle, 3);

        }
        else if(v == itvTimeSelect.getBgView())
        {

            TimeSelectBottomDialog mAdPushTypeBottomDialog = new TimeSelectBottomDialog(getActivity());

            mAdPushTypeBottomDialog.show("播放时段设置", loopAddNum,mAdPlanInfo, new TimeSelectBottomDialog.OnDismissResultListener() {
                @Override
                public void onConfirmDismiss(String sTimes, String eTimes) {
                    startTimes = sTimes;
                    endTimes = eTimes;
                    itvTimeSelect.getSecondView().setText(startTimes + " 到 " + endTimes);
                    if(mAdPlanInfo != null && mAdPlanInfo.getSelelctPlanInfor() != null)
                    {
                        mAdPlanInfo.getSelelctPlanInfor().setStartTime(startTimes);
                        mAdPlanInfo.getSelelctPlanInfor().setEndTime(endTimes);
                    }
                }
            });

        }
        else if(v == itvInsetCount.getBgView())
        {
            AdPushTypeBottomDialog mAdPushTypeBottomDialog = new AdPushTypeBottomDialog(getActivity());

            mAdPushTypeBottomDialog.show("广告插播设置", loopAddNum,playModel, new AdPushTypeBottomDialog.OnAdPushDismissResultListener() {
                @Override
                public void onConfirmDismiss(String content, String type) {
                    //mode = type;
                    loopAddNum = content;
                    playModel = type;
                    showCountInternalPush(loopAddNum);
                }
            });
        }
        else if(v == itvWeekSelect.getBgView())
        {
            showWeekSelectDialog();
        }
        else if(v == itvPushTimeing.getBgView())
        {
            //获取当前系统时间
            Calendar c= Calendar.getInstance();
            int hour=c.get(Calendar.HOUR_OF_DAY);
            int minute=c.get(Calendar.MINUTE);

            //弹出时间对话框
            TimePickerDialog tpd=new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    //Log.i("text","时间："+hourOfDay+"："+minute);
                    itvPushTimeing.getSecondView().setText(hourOfDay + "点"+minute+"分");
                    pushTime = hourOfDay + ":" + minute;
                }
            },hour,minute,true);
            tpd.show();
        }
    };
}
