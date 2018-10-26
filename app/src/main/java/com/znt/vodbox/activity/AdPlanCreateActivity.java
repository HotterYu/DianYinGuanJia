package com.znt.vodbox.activity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.znt.vodbox.R;
import com.znt.vodbox.adapter.AdListAdapter;
import com.znt.vodbox.adapter.MyAlbumlistAdapter;
import com.znt.vodbox.bean.AdMediaInfo;
import com.znt.vodbox.bean.AdPlanInfo;
import com.znt.vodbox.bean.AlbumInfo;
import com.znt.vodbox.bean.SubAdPlanInfo;
import com.znt.vodbox.bean.SubPlanInfor;
import com.znt.vodbox.dialog.CountEditDialog;
import com.znt.vodbox.dialog.WheelListDialog;
import com.znt.vodbox.utils.DateUtils;
import com.znt.vodbox.utils.StringUtils;
import com.znt.vodbox.utils.ViewUtils;
import com.znt.vodbox.utils.binding.Bind;
import com.znt.vodbox.view.ItemTextView;
import com.znt.vodbox.view.SwitchButton;
import com.znt.vodbox.view.wheel.ArrayWheelAdapter;
import com.znt.vodbox.view.wheel.OnWheelChangedListener;
import com.znt.vodbox.view.wheel.OnWheelScrollListener;
import com.znt.vodbox.view.wheel.WheelView;
import com.znt.vodbox.view.xlistview.LJListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AdPlanCreateActivity extends  BaseActivity implements OnClickListener
        , OnItemClickListener, LJListView.IXListViewListener
{


    @Bind(R.id.tv_common_title)
    private TextView tvTopTitle = null;
    @Bind(R.id.iv_common_back)
    private ImageView ivTopReturn = null;
    @Bind(R.id.iv_common_more)
    private ImageView ivTopMore = null;
    @Bind(R.id.tv_common_confirm)
    private TextView tvConfirm = null;

    @Bind(R.id.view_ad_create_time_select)
    private View viewTimeSelect = null;

    private View headerView = null;
    private LJListView listView = null;
    private WheelView wvHourStart = null;
    private WheelView wvMinStart = null;
    private WheelView wvHourEnd = null;
    private WheelView wvMinEnd = null;
    private TextView tvTimeHint = null;
    private TextView tvPlanTime = null;
    private TextView tvHint = null;
    private TextView tvPushTypeHint = null;
    private TextView tvPushTypeHintSub = null;

    private ItemTextView itvInsetCount = null;
    private ItemTextView itvInsertMusic = null;
    private ItemTextView itvWeekSelect = null;
    private ItemTextView itvPushTimeing = null;
    private SwitchButton switchButtonBroad = null;
    private SwitchButton switchButtonTimeing = null;



    private boolean wheelScrolled = false;
    private int selectedHourStart, selectedMinStart,selectedHourEnd, selectedMinEnd;
    private final String TAG_HOUR = "TYPE_HOUR";
    private final String TAG_MIN = "TYPE_MIN";

    private String startTimes = "";
    private String endTimes = "";
    private String loopAddNum = "";
    private String cycleType = "";
    private boolean isEdit = false;

    private AdPlanInfo mAdPlanInfo = null;

    private AdListAdapter mAdListAdapter = null;
    private List<AdMediaInfo> adList = new ArrayList<AdMediaInfo>();
    private List<AdMediaInfo> selectAdList = new ArrayList<AdMediaInfo>();

    public static final String[] DAY_STRING = { "00", "01", "02", "03", "04", "05",
            "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16",
            "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27",
            "28", "29", "30", "31" , "32" , "33" , "34" , "35" , "36" , "37" ,
            "38" , "39" , "40" , "41" , "42" , "43" , "44" , "45" , "46" , "47"
            , "48" , "49" , "50" , "51" , "52" , "53" , "54" , "55" , "56" , "57"
            , "58" , "59" };
    public static final String[] MONTH_STRING = { "00", "01", "02", "03", "04", "05",
            "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16",
            "17", "18", "19", "20", "21", "22", "23"};



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
        tvTopTitle.setVisibility(View.GONE);
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
        wvHourStart = (WheelView)findViewById(R.id.vh_time_select_hour);
        wvMinStart = (WheelView)findViewById(R.id.vh_time_select_min);
        wvHourEnd = (WheelView)findViewById(R.id.vh_time_select_hour_end);
        wvMinEnd = (WheelView)findViewById(R.id.vh_time_select_min_end);
        tvTimeHint = (TextView)findViewById(R.id.tv_plan_create_time_hint);
        tvPlanTime = (TextView)findViewById(R.id.tv_plan_create_plan_time);
        tvHint = (TextView)findViewById(R.id.tv_plan_create_hint);

        switchButtonBroad = (SwitchButton)headerView.findViewById(R.id.sb_plan_detail_broadcast);
        switchButtonTimeing = (SwitchButton)headerView.findViewById(R.id.sb_plan_detail_broadcast_timeing);
        itvInsetCount = (ItemTextView)headerView.findViewById(R.id.itv_plan_detail_broadcast_count);
        itvInsertMusic = (ItemTextView)headerView.findViewById(R.id.itv_plan_detail_broadcast_music);
        itvWeekSelect = (ItemTextView)headerView.findViewById(R.id.itv_plan_detail_push_week);
        itvPushTimeing = (ItemTextView)headerView.findViewById(R.id.itv_plan_detail_broadcast_timeing);
        tvPushTypeHint = (TextView)headerView.findViewById(R.id.sb_plan_detail_loop_hint);
        tvPushTypeHintSub = (TextView)headerView.findViewById(R.id.sb_plan_detail_loop_hint_2);

        itvInsetCount.getFirstView().setText("播放间隔");
        itvInsertMusic.getFirstView().setText("选择广告（必选）");
        itvWeekSelect.getFirstView().setText(getResources().getString(R.string.week_select));
        itvWeekSelect.getSecondView().setText(getResources().getString(R.string.week_every));
        itvPushTimeing.getFirstView().setText("选择定时时间");
        itvInsetCount.showMoreButton(true);
        itvInsertMusic.showMoreButton(true);
        itvWeekSelect.showMoreButton(true);
        itvPushTimeing.showMoreButton(true);
        itvInsetCount.getBgView().setOnClickListener(this);
        itvInsertMusic.getBgView().setOnClickListener(this);
        itvWeekSelect.getBgView().setOnClickListener(this);
        itvPushTimeing.getBgView().setOnClickListener(this);

        listView = (LJListView)findViewById(R.id.ptrl_plan_create);

        switchButtonBroad.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1)
            {
                // TODO Auto-generated method stub
                showCountInternalPush(arg1);
            }
        });
        switchButtonTimeing.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1)
            {
                // TODO Auto-generated method stub
                showTimeingPushSet(arg1);
            }
        });

        showCountInternalPush(switchButtonBroad.isChecked());

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

        mAdListAdapter = new AdListAdapter(getApplicationContext(),selectAdList);
        listView.setAdapter(mAdListAdapter);
        initWheelViews();

        isEdit = getIntent().getBooleanExtra("IS_EDIT", false);
        mAdPlanInfo = (AdPlanInfo)getIntent().getSerializableExtra("AD_PLAN_INFO");

        if(!isEdit)
        {
            String curHour = DateUtils.getHour();
            String curMin = DateUtils.getTime();
            int curHourInt = 0;
            int curMinInt = 0;
            if(!TextUtils.isEmpty(curHour))
                curHourInt = Integer.parseInt(curHour);
            if(!TextUtils.isEmpty(curMin))
                curMinInt = Integer.parseInt(curMin);
            initStartData(getHourFromTime(curHourInt), getMinFromTime(curMinInt));
            initEndData(getHourFromTime(curHourInt), getMinFromTime(curMinInt));

            itvInsetCount.getSecondView().setText("每隔 " + 1 + " 首插播一次");
            showCycleType("0");
        }
        else
        {
            showCurPlanInfor();
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

    private void showCountInternalPush(boolean isCountPush)
    {
        if(isCountPush)
        {
            itvInsetCount.getSecondView().setText("");
            itvInsetCount.getFirstView().setText("设置间隔数量");
            tvPushTypeHint.setText("间隔数量插播");
            tvPushTypeHintSub.setText("间隔几首歌曲插播一次广告");
        }
        else
        {
            itvInsetCount.getSecondView().setText("");
            itvInsetCount.getFirstView().setText("设置间隔时间");
            tvPushTypeHint.setText("间隔时间插播");
            tvPushTypeHintSub.setText("间隔一定时间插播一次广告");
        }
    }

    private void showTimeingPushSet(boolean isShow)
    {
        if(isShow)
        {
            itvPushTimeing.setVisibility(View.VISIBLE);
            //viewTimeSelect.setVisibility(View.GONE);

            /*if(planInfor != null && planInfor.getSelelctPlanInfor()!= null)
                planInfor.getSelelctPlanInfor().setCycleType("1");*/
        }
        else
        {
            itvPushTimeing.setVisibility(View.GONE);
            //viewTimeSelect.setVisibility(View.VISIBLE);
            /*if(planInfor != null && planInfor.getSelelctPlanInfor()!= null)
                planInfor.getSelelctPlanInfor().setCycleType("0");*/
        }
    }

    private void showCurPlanInfor()
    {
        SubAdPlanInfo tempInfor = mAdPlanInfo.getSelelctPlanInfor();
        if(tempInfor != null)
        {
            String sTime = tempInfor.getStartTime();
            String eTime = tempInfor.getEndTime();

            String[] sTimes = StringUtils.splitUrls(sTime, ":");
            String[] eTimes = StringUtils.splitUrls(eTime, ":");

            int sHour = Integer.parseInt(sTimes[0]);
            int sMin = Integer.parseInt(sTimes[1]);
            int eHour = Integer.parseInt(eTimes[0]);
            int eMin = Integer.parseInt(eTimes[1]);

            initStartData(sHour, sMin);
            initEndData(eHour, eMin);

            //tvAddAlbum.setText(subPlanInfor.getPlanAlbumName());
            selectAdList.addAll(tempInfor.getAdMediaInfoList());

            loopAddNum = tempInfor.getMusicNum();

            cycleType = tempInfor.getCycleType();

            switchButtonBroad.setChecked(cycleType.equals("1"));

            showCycleType(cycleType);

            if(!TextUtils.isEmpty(loopAddNum) && !TextUtils.isEmpty(tempInfor.getMusicNum()))
            {
                switchButtonBroad.setChecked(true);
                itvInsetCount.getSecondView().setText("每隔 " + loopAddNum + " 首插播一次");
                //itvInsertMusic.getSecondView().setText(subPlanInfor.getMusicNum());
            }
            else
                switchButtonBroad.setChecked(false);
        }
    }

    private void showCycleType(String  cycleType)
    {
        itvWeekSelect.getSecondView().setText(DateUtils.getWeekByCycleType(this,cycleType));
    }

    private void showLoadingView(String textView)
    {
        if(TextUtils.isEmpty(textView))
        {
        }
        else
        {
            tvHint.setText(textView);
        }
        tvHint.setVisibility(View.VISIBLE);

    }
    private void hideLoadingView()
    {
        tvHint.setVisibility(View.GONE);
    }

    private void initWheelViews()
    {
        wvHourStart.setTag(TAG_HOUR);
        wvHourStart.setAdapter(new ArrayWheelAdapter<String>(MONTH_STRING));
        //wheel_month.setCurrentItem(wheel_month.getCurrentVal("02"));
        wvHourStart.setCyclic(true);
        //wvHour.setLabel("��");
        wvHourStart.addChangingListener(startWheelChangeListener);
        wvHourStart.addScrollingListener(wheelScrolledListener, null);

        wvMinStart.setAdapter(new ArrayWheelAdapter<String>(DAY_STRING));
        //wvMin.setLabel("��");
        wvMinStart.setTag(TAG_MIN);
        wvMinStart.setCyclic(true);
        wvMinStart.addChangingListener(startWheelChangeListener);
        wvMinStart.addScrollingListener(wheelScrolledListener, null);

        wvHourEnd.setTag(TAG_HOUR);
        wvHourEnd.setAdapter(new ArrayWheelAdapter<String>(MONTH_STRING));
        //wheel_month.setCurrentItem(wheel_month.getCurrentVal("02"));
        wvHourEnd.setCyclic(true);
        //wvHour.setLabel("��");
        wvHourEnd.addChangingListener(endWheelChangeListener);
        wvHourEnd.addScrollingListener(wheelScrolledListener, null);

        wvMinEnd.setAdapter(new ArrayWheelAdapter<String>(DAY_STRING));
        //wvMin.setLabel("��");
        wvMinEnd.setTag(TAG_MIN);
        wvMinEnd.setCyclic(true);
        wvMinEnd.addChangingListener(endWheelChangeListener);
        wvMinEnd.addScrollingListener(wheelScrolledListener, null);
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
        SubAdPlanInfo tempInfor = new SubAdPlanInfo();
        if(startTimes.equals(endTimes))
        {
            showToast("起始时间不能一样的哦");
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

        if(selectAdList.size() == 0)
        {
            showToast("请至少添加一个广告");
            return null;
        }
        tempInfor.setStartTime(startTimes);
        tempInfor.setEndTime(endTimes);
        tempInfor.setSelectedAdList(selectAdList);

        if(switchButtonBroad.isChecked())
        {
            if(TextUtils.isEmpty(loopAddNum))
                loopAddNum = "1";
            tempInfor.setMusicNum(loopAddNum);
            if(TextUtils.isEmpty(cycleType))
                cycleType = "0";
            tempInfor.setCycleType(cycleType);
            if(selectAdList.size() == 0)
            {
                showToast("请选择要插播的歌曲");
                return null;
            }
        }
        else
        {
            tempInfor.setMusicNum("");
            tempInfor.setCycleType("");
            tempInfor.setAdinfoId("");
            tempInfor.setAdinfoName("");
        }


        return tempInfor;
    }


    private void initStartData(int hour, int min)
    {
        wvHourStart.setCurrentItem(hour);
        wvMinStart.setCurrentItem(min);

        updateStartTime(hour, min);

    }
    private void initEndData(int hour, int min)
    {
        wvHourEnd.setCurrentItem(hour);
        wvMinEnd.setCurrentItem(min);

        updateEndTime(hour, min);

    }

    private int getHourFromTime(int time)
    {
        if(time < 24)
            return time;
        else
            return time % 24;
    }
    private int getMinFromTime(int time)
    {
        if(time < 60)
            return time;
        else
            return time % 60;
    }

    private void updateStartTime(int hour, int min)
    {
        if(hour > 0)
            startTimes = getStringTwo(hour) + ":" + getStringTwo(min);
        else if(min > 0)
            startTimes = "00:" + getStringTwo(min);
        if(hour == 0 && min == 0)
            startTimes = "00:00";

        /*if(!planInfor.checkPlanTime(startTimes, endTimes))
            tvTimeHint.setVisibility(View.VISIBLE);
        else
            tvTimeHint.setVisibility(View.GONE);*/

        tvPlanTime.setText(startTimes + " 到 " + endTimes);

    }
    private void updateEndTime(int hour, int min)
    {
        if(hour > 0)
            endTimes = getStringTwo(hour) + ":" + getStringTwo(min);
        else if(min > 0)
            endTimes = "00:" + getStringTwo(min);
        if(hour == 0 && min == 0)
            endTimes = "00:00";
        /*if(!planInfor.checkPlanTime(startTimes, endTimes))
            tvTimeHint.setVisibility(View.VISIBLE);
        else
            tvTimeHint.setVisibility(View.GONE);*/

        tvPlanTime.setText(startTimes + " 到 " + endTimes);

    }
    private String getStringTwo(int orgNum)
    {
        if(orgNum >= 0 && orgNum < 10)
            return "0" + orgNum;
        return orgNum + "";
    }

    OnWheelScrollListener wheelScrolledListener = new OnWheelScrollListener()
    {
        public void onScrollingStarted(WheelView wheel)
        {
            wheelScrolled = true;
        }
        @Override
        public void onScrollingFinished(WheelView wheel)
        {
            // TODO Auto-generated method stub
            //String tag = wheel.getTag().toString();
            wheelScrolled = false;
        }
    };

    // Wheel changed listener
    private OnWheelChangedListener startWheelChangeListener = new OnWheelChangedListener()
    {
        @Override
        public void onLayChanged(WheelView wheel, int oldValue, int newValue,
                                 LinearLayout layout)
        {
            // TODO Auto-generated method stub

            if(wheel.getTag().toString().equals(TAG_HOUR))
                selectedHourStart = newValue;
            else if(wheel.getTag().toString().equals(TAG_MIN))
                selectedMinStart = newValue;

            updateStartTime(selectedHourStart, selectedMinStart);

        }
    };
    private OnWheelChangedListener endWheelChangeListener = new OnWheelChangedListener()
    {
        @Override
        public void onLayChanged(WheelView wheel, int oldValue, int newValue,
                                 LinearLayout layout)
        {
            // TODO Auto-generated method stub

            if(wheel.getTag().toString().equals(TAG_HOUR))
                selectedHourEnd = newValue;
            else if(wheel.getTag().toString().equals(TAG_MIN))
                selectedMinEnd = newValue;

            updateEndTime(selectedHourEnd, selectedMinEnd);

        }
    };

    private CountEditDialog countDialog = null;
    private void showCountEditDialog(final String count)
    {
        if(countDialog == null || countDialog.isDismissed())
            countDialog = new CountEditDialog(getActivity());
        countDialog.setInfor(count);
        //playDialog.updateProgress("00:02:18 / 00:05:12");
        if(countDialog.isShowing())
            countDialog.dismiss();
        countDialog.show();
        countDialog.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                // TODO Auto-generated method stub
                loopAddNum = countDialog.getContent();
                if(TextUtils.isEmpty(loopAddNum))
                {
                   // showToast("请输入插播间隔数");
                    return;
                }
                int looadNum = Integer.parseInt(loopAddNum);
                if(looadNum <= 0)
                {
                    //showToast("请输入大于0的数字");
                    return;
                }
                //planInfor.getSelelctPlanInfor().setLoopAddNum(countDialog.getContent());
                itvInsetCount.getSecondView().setText("每隔 " + loopAddNum + " 首插播一次");
                //mSubAdPlanInfo.setMusicNum(loopAddNum);
                countDialog.dismiss();
                //updatePlan();
            }
        });

        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = countDialog.getWindow().getAttributes();
        lp.width = (int)(display.getWidth()); //设置宽度
        lp.height = (int)(display.getHeight()); //设置高度
        countDialog.getWindow().setAttributes(lp);
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
            mAdListAdapter.notifyDataSetChanged(selectAdList);


        }
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
        AdMediaInfo infor = adList.get(arg2);
        Bundle bundle = new Bundle();
        bundle.putSerializable("AD_MEDIA_INFO", infor);
        //bundle.putSerializable("MusicEditType", MusicEditType.DeleteAdd);
        ViewUtils.startActivity(getActivity(), AdListActivity.class, bundle);
    }


    private void showWeekSelectDialog()
    {
        List<String> data = new ArrayList<>();
        data.add(getResources().getString(R.string.week_every));
        data.add(getResources().getString(R.string.week_monday));
        data.add(getResources().getString(R.string.week_tuesday));
        data.add(getResources().getString(R.string.week_wednesday));
        data.add(getResources().getString(R.string.week_thursday));
        data.add(getResources().getString(R.string.week_friday));
        data.add(getResources().getString(R.string.week_saturday));
        data.add(getResources().getString(R.string.week_sunday));

        WheelListDialog dialog = new WheelListDialog(getActivity(),data);
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

    private void showCountSelectDialog(boolean isCountInternal)
    {
        List<String> data = new ArrayList<>();
        if(isCountInternal)
        {
            for(int i=0;i<30;i++)
            {
                data.add((i+1) + "首歌曲");
            }
        }
        else
        {
            for(int i=0;i<30;i++)
            {
                data.add((i+1) + "分钟");
            }
        }


        WheelListDialog dialog = new WheelListDialog(getActivity(),data);
        dialog.setListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btOk:
                        String selectWeek = dialog.getPositionData();
                        int realPos = dialog.getRealDataPosition();
                        dialog.dismiss();
                        loopAddNum = realPos + "";
                        itvInsetCount.getSecondView().setText(data.get(realPos));
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
            /*if(planInfor.getSelelctPlanInfor() != null && planInfor.getSelelctPlanInfor().getLoopMusicInfoId() != null)
                bundle.putString("SELECT_MUSIC_ID", planInfor.getSelelctPlanInfor().getLoopMusicInfoId());*/
            ViewUtils.startActivity(getActivity(), AdListActivity.class, bundle, 3);


        }
        else if(v == itvInsetCount.getBgView())
        {
            showCountSelectDialog(switchButtonBroad.isChecked());
            //showCountEditDialog(loopAddNum);
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
                }
            },hour,minute,true);
            tpd.show();
        }
    };
}
