package com.znt.vodbox.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.design.widget.BottomSheetDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.vodbox.bean.AdPlanInfo;
import com.znt.vodbox.bean.SubAdPlanInfo;
import com.znt.vodbox.utils.DateUtils;
import com.znt.vodbox.utils.StringUtils;
import com.znt.vodbox.view.wheel.ArrayWheelAdapter;
import com.znt.vodbox.view.wheel.OnWheelChangedListener;
import com.znt.vodbox.view.wheel.OnWheelScrollListener;
import com.znt.vodbox.view.wheel.WheelView;

/**
 * Created by prize on 2018/11/14.
 */

public class TimeSelectBottomDialog
{
    private Activity mContext = null;

    private WheelView wvHourStart = null;
    private WheelView wvMinStart = null;
    private WheelView wvHourEnd = null;
    private WheelView wvMinEnd = null;
    private TextView tvTimeHint = null;
    private TextView tvPlanTime = null;
    private TextView tvHint = null;

    private boolean wheelScrolled = false;
    private int selectedHourStart, selectedMinStart,selectedHourEnd, selectedMinEnd;

    private AdPlanInfo adPlanInfo = null;
    private String startTimes = "";
    private String endTimes = "";

    public TimeSelectBottomDialog(Activity mContext)
    {
        this.mContext = mContext;
    }

    private boolean isConfirm = false;

    public interface OnDismissResultListener
    {
        public void onConfirmDismiss(String startTimes, String endTimes);
    }

    public void show(String titleName, String oldContent, AdPlanInfo adPlanInfo, final OnDismissResultListener mOnDismissResultListener)
    {

        this.adPlanInfo = adPlanInfo;

        SubAdPlanInfo tempInfor = adPlanInfo.getSelelctPlanInfor();

        View view = View.inflate(mContext, R.layout.dialog_bottom_time_select, null);

        wvHourStart = (WheelView)view.findViewById(R.id.vh_time_select_hour);
        wvMinStart = (WheelView)view.findViewById(R.id.vh_time_select_min);
        wvHourEnd = (WheelView)view.findViewById(R.id.vh_time_select_hour_end);
        wvMinEnd = (WheelView)view.findViewById(R.id.vh_time_select_min_end);
        tvTimeHint = (TextView)view.findViewById(R.id.tv_plan_create_time_hint);
        tvPlanTime = (TextView)view.findViewById(R.id.tv_plan_create_plan_time);
        tvHint = (TextView)view.findViewById(R.id.tv_plan_create_hint);

        initWheelViews();

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
        }
        else
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

        final BottomSheetDialog bottomInterPasswordDialog = new BottomSheetDialog(mContext);

        bottomInterPasswordDialog.setCancelable(false);

        bottomInterPasswordDialog.setCanceledOnTouchOutside(false);

        bottomInterPasswordDialog.setContentView(view);
        bottomInterPasswordDialog.show();

        TextView title = (TextView) view.findViewById(R.id.tibd_title);

        Button btnCancel = (Button) view.findViewById(R.id.tibd_cancel);
        Button btnConfirm = (Button) view.findViewById(R.id.tibd_confirm);

        if(titleName != null)
            title.setText(titleName);

        bottomInterPasswordDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if(isConfirm)
                {
                    if(mOnDismissResultListener != null)
                        mOnDismissResultListener.onConfirmDismiss(startTimes,endTimes);
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isConfirm = false;
                bottomInterPasswordDialog.dismiss();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*String newContent = content.getText().toString();
                if(TextUtils.isEmpty(newContent))
                {
                    Toast.makeText(mContext,"请输入内容",Toast.LENGTH_LONG).show();
                    return;
                }

                if(oldContent != null && oldContent.equals(newContent))
                {
                    Toast.makeText(mContext,"内容无变化",Toast.LENGTH_LONG).show();
                    return;
                }*/

                isConfirm = true;
                bottomInterPasswordDialog.dismiss();
            }
        });
    }
    private final String TAG_HOUR = "TYPE_HOUR";
    private final String TAG_MIN = "TYPE_MIN";
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
            String tag = wheel.getTag().toString();
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

    private void updateStartTime(int hour, int min)
    {
        if(hour > 0)
            startTimes = getStringTwo(hour) + ":" + getStringTwo(min);
        else if(min > 0)
            startTimes = "00:" + getStringTwo(min);
        if(hour == 0 && min == 0)
            startTimes = "00:00";

        if(!adPlanInfo.checkPlanTime(startTimes, endTimes))
            tvTimeHint.setVisibility(View.VISIBLE);
        else
            tvTimeHint.setVisibility(View.GONE);

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
        if(!adPlanInfo.checkPlanTime(startTimes, endTimes))
            tvTimeHint.setVisibility(View.VISIBLE);
        else
            tvTimeHint.setVisibility(View.GONE);

        tvPlanTime.setText(startTimes + " 到 " + endTimes);

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

}
