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
import com.znt.vodbox.adapter.AlbumSelectAdapter;
import com.znt.vodbox.adapter.MyAlbumlistAdapter;
import com.znt.vodbox.bean.AlbumInfo;
import com.znt.vodbox.bean.AlbumListResultBean;
import com.znt.vodbox.bean.PlanInfo;
import com.znt.vodbox.bean.SubPlanInfor;
import com.znt.vodbox.dialog.CountEditDialog;
import com.znt.vodbox.dialog.WheelListDialog;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.http.HttpCallback;
import com.znt.vodbox.http.HttpClient;
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

public class PlanCreateActivity extends  BaseActivity implements OnClickListener
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

    private View headerView = null;
    private LJListView listView = null;
    private WheelView wvHourStart = null;
    private WheelView wvMinStart = null;
    private WheelView wvHourEnd = null;
    private WheelView wvMinEnd = null;
    private TextView tvTimeHint = null;
    private TextView tvPlanTime = null;
    private TextView tvHint = null;

    private ItemTextView itvInsertMusic = null;
    private ItemTextView itvWeekSelect = null;
    private PlanInfo mPlanInfo = null;

    private boolean wheelScrolled = false;
    private int selectedHourStart, selectedMinStart,selectedHourEnd, selectedMinEnd;
    private final String TAG_HOUR = "TYPE_HOUR";
    private final String TAG_MIN = "TYPE_MIN";

    private String startTimes = "";
    private String endTimes = "";
    private String cycleType = "0";
    private boolean isEdit = false;

    private AlbumSelectAdapter mAlbumSelectAdapter = null;
    private List<AlbumInfo> albumList = new ArrayList<AlbumInfo>();
    private List<AlbumInfo> selectAlbumList = new ArrayList<AlbumInfo>();

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



    private void finishAndFeedBack()
    {

        SubPlanInfor tempInfor = getSubPlanInfor();
        if(tempInfor == null)
            return;

        if(!isEdit)
            mPlanInfo.addSubPlanInfor(tempInfor);
        else
            mPlanInfo.updateSelect(tempInfor);


        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("PLAN_INFO", mPlanInfo);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     *callbacks
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_plan_create);
        tvTopTitle.setText(getResources().getString(R.string.plan_detail_add_time));
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

        mPlanInfo = (PlanInfo)getIntent().getSerializableExtra("PLAN_INFO");

        headerView = LayoutInflater.from(getActivity()).inflate(R.layout.view_plan_create_list_header, null);
        wvHourStart = (WheelView)findViewById(R.id.vh_time_select_hour);
        wvMinStart = (WheelView)findViewById(R.id.vh_time_select_min);
        wvHourEnd = (WheelView)findViewById(R.id.vh_time_select_hour_end);
        wvMinEnd = (WheelView)findViewById(R.id.vh_time_select_min_end);
        tvTimeHint = (TextView)findViewById(R.id.tv_plan_create_time_hint);
        tvPlanTime = (TextView)findViewById(R.id.tv_plan_create_plan_time);
        tvHint = (TextView)findViewById(R.id.tv_plan_create_hint);

        itvInsertMusic = (ItemTextView)headerView.findViewById(R.id.itv_plan_detail_broadcast_albums);
        itvInsertMusic.getFirstView().setText("选择歌单（必选）");
        itvInsertMusic.showMoreButton(true);
        itvInsertMusic.getBgView().setOnClickListener(this);

        itvWeekSelect = (ItemTextView)headerView.findViewById(R.id.itv_plan_detail_push_week);
        itvWeekSelect.getFirstView().setText(getResources().getString(R.string.week_select));
        itvWeekSelect.getSecondView().setText(getResources().getString(R.string.week_every));
        itvWeekSelect.showMoreButton(true);
        itvWeekSelect.getBgView().setOnClickListener(this);

        listView = (LJListView)findViewById(R.id.ptrl_plan_create);


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

        mAlbumSelectAdapter = new AlbumSelectAdapter(getActivity(),albumList);
        listView.setAdapter(mAlbumSelectAdapter);
        initWheelViews();

        isEdit = getIntent().getBooleanExtra("IS_EDIT", false);

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

        }
        else
        {
            showCurPlanInfor();
        }

        listView.onFresh();

    }

    public void loadMyAlbums()
    {


        String token = Constant.mUserInfo.getToken();
        String pageNo = "1";
        String pageSize = "20";
        String merchId = Constant.mUserInfo.getMerchant().getId();
        //String merchId = mUserInfo.getMerchant().getId();
        String typeId = "";
        String name = "";

        try
        {
            // Simulate network access.
            HttpClient.getMyAlbums(token, pageNo, pageSize,merchId,typeId,name, new HttpCallback<AlbumListResultBean>() {
                @Override
                public void onSuccess(AlbumListResultBean resultBean) {
                    listView.stopRefresh();
                    if(resultBean != null)
                    {
                        albumList = resultBean.getData();
                        if(isEdit)
                        {
                            SubPlanInfor subPlanInfor = mPlanInfo.getSelelctPlanInfor();
                            selectAlbumList.addAll(subPlanInfor.getAlbumList());
                            mAlbumSelectAdapter.notifyDataSetChanged(albumList);
                            mAlbumSelectAdapter.setSelectedList(selectAlbumList);
                        }
                        else
                            mAlbumSelectAdapter.notifyDataSetChanged(albumList);


                    }
                    else
                    {
                        //shopinfoList.clear();
                    }
                    listView.stopRefresh();
                }

                @Override
                public void onFail(Exception e) {
                    listView.stopRefresh();
                }
            });
        }
        catch (Exception e)
        {

        }

    }

    private void onLoad(int updateCount)
    {
        listView.setCount(updateCount);
        listView.stopLoadMore();
        listView.stopRefresh();
        listView.setRefreshTime();
    }

    private void showCurPlanInfor()
    {
        SubPlanInfor subPlanInfor = mPlanInfo.getSelelctPlanInfor();
        if(subPlanInfor != null)
        {
            String sTime = subPlanInfor.getStartTime();
            String eTime = subPlanInfor.getEndTime();
            cycleType = subPlanInfor.getCycleType();

            String[] sTimes = StringUtils.splitUrls(sTime, ":");
            String[] eTimes = StringUtils.splitUrls(eTime, ":");

            int sHour = Integer.parseInt(sTimes[0]);
            int sMin = Integer.parseInt(sTimes[1]);
            int eHour = Integer.parseInt(eTimes[0]);
            int eMin = Integer.parseInt(eTimes[1]);

            initStartData(sHour, sMin);
            initEndData(eHour, eMin);
            showCycleType(cycleType);

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

    private SubPlanInfor getSubPlanInfor()
    {
        SubPlanInfor tempInfor = new SubPlanInfor();
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

        if(!mPlanInfo.checkPlanTime(startTimes, endTimes))
        {
            showToast("当前时间与计划时间有重叠");
            return null;
        }

        if(mAlbumSelectAdapter.getSelectedList().size() == 0)
        {
            showToast("请至少添加一个歌单");
            return null;
        }
        tempInfor.setStartTime(startTimes);
        tempInfor.setEndTime(endTimes);
        tempInfor.setAlbumList(mAlbumSelectAdapter.getSelectedList());

        tempInfor.setCycleType(cycleType);
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
                        cycleType = realPos+"";
                        /*mPlanInfo.getSelelctPlanInfor().setCycleType(realPos+"");
                        mPlanInfo.updateSelect(mPlanInfo.getSelelctPlanInfor());*/
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
    protected void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data)
    {
        if(resultCode != RESULT_OK)
            return;

        if(requestCode == 1)
        {

        }
        else if(requestCode == 3)
        {


        }
    }

    /**
     *callbacks
     */
    @Override
    public void onRefresh()
    {
        // TODO Auto-generated method stub
        loadMyAlbums();
    }

    /**
     *callbacks
     */
    @Override
    public void onLoadMore() {
        // TODO Auto-generated method stub

        loadMyAlbums();
    }

    /**
     *callbacks
     */
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub
        if(arg2 > 1)
            arg2 = arg2 - 2;
        AlbumInfo infor = albumList.get(arg2);
        Bundle bundle = new Bundle();
        bundle.putSerializable("MusicAlbumInfor", infor);
        //bundle.putSerializable("MusicEditType", MusicEditType.DeleteAdd);

        bundle.putBoolean("IS_COLLECT", true);
        ViewUtils.startActivity(getActivity(), AlbumMusicActivity.class, bundle);
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
            ViewUtils.startActivity(getActivity(), MyAlbumActivity.class, bundle, 3);
        }
        else if(v == itvWeekSelect.getBgView())
        {
            showWeekSelectDialog();
        }
    };
}
