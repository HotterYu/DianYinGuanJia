package com.znt.vodbox.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.vodbox.adapter.AdPlanDetailAdapter;
import com.znt.vodbox.adapter.OnMoreClickListener;
import com.znt.vodbox.bean.AdPlanInfo;
import com.znt.vodbox.bean.CommonCallBackBean;
import com.znt.vodbox.bean.GroupInfo;
import com.znt.vodbox.bean.SubAdPlanInfo;
import com.znt.vodbox.dialog.DoubleDatePickerDialog;
import com.znt.vodbox.dialog.TextInputBottomDialog;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.http.HttpCallback;
import com.znt.vodbox.http.HttpClient;
import com.znt.vodbox.utils.DateUtils;
import com.znt.vodbox.utils.ViewUtils;
import com.znt.vodbox.utils.binding.Bind;
import com.znt.vodbox.view.ItemTextView;
import com.znt.vodbox.view.SwitchButton;
import com.znt.vodbox.view.xlistview.LJListView;

import java.util.ArrayList;
import java.util.List;

public class AdPlanDetailActivity extends BaseActivity  implements
        LJListView.IXListViewListener, AdapterView.OnItemClickListener,OnMoreClickListener
,View.OnClickListener,AdPlanDetailAdapter.OnScheDeleteListener {


    @Bind(R.id.tv_common_title)
    private TextView tvTopTitle = null;
    @Bind(R.id.iv_common_back)
    private ImageView ivTopReturn = null;
    @Bind(R.id.iv_common_more)
    private ImageView ivTopMore = null;
    @Bind(R.id.tv_common_confirm)
    private TextView tvConfirm = null;

    private View headerView = null;

    private ItemTextView itvName = null;

    private ItemTextView itvShops = null;

    private ItemTextView itvDateStart = null;

    private ItemTextView itvDateEnd = null;

    private View viewAdd = null;

    private View viewApply = null;

    private SwitchButton switchButton = null;

    private SwitchButton switchButtonDate = null;

    private LJListView listView = null;

    private DoubleDatePickerDialog startTimeDialog = null;
    private DoubleDatePickerDialog endTimeDialog = null;

    private AdPlanDetailAdapter mAdPlanDetailAdapter = null;

    private List<SubAdPlanInfo> subPlanList = new ArrayList<SubAdPlanInfo>();

    private AdPlanInfo mAdPlanInfo = null;
    private boolean isEdit = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_plan_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAdPlanInfo = (AdPlanInfo)getIntent().getSerializableExtra("AD_PLAN_INFO");

        isEdit = (mAdPlanInfo != null);

        tvTopTitle.setText(getResources().getString(R.string.ad_plan_detail));
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
                if(isEdit)
                    updatePlanToServer();
                else
                    createPlan();
            }
        });

        headerView = LayoutInflater.from(getActivity()).inflate(R.layout.view_plan_detail_header, null);

        headerView.setOnClickListener(this);

        viewApply = headerView.findViewById(R.id.view_plan_detail_apply);
        switchButton = (SwitchButton)headerView.findViewById(R.id.sb_plan_detail);
        switchButtonDate = (SwitchButton)headerView.findViewById(R.id.sb_plan_detail_date);

        itvName = (ItemTextView)headerView.findViewById(R.id.itv_plan_detail_name);
        itvShops = (ItemTextView)headerView.findViewById(R.id.itv_plan_detail_shops);
        itvDateStart = (ItemTextView)headerView.findViewById(R.id.itv_plan_detail_date_select_start);
        itvDateEnd = (ItemTextView)headerView.findViewById(R.id.itv_plan_detail_date_select_end);
        viewAdd = headerView.findViewById(R.id.view_plan_detail_add);

        if(isEdit)
            switchButton.setEnable(false);
        else
            switchButton.setEnable(true);

        listView = (LJListView)findViewById(R.id.ptrl_plan_datail);
        listView.getListView().setDivider(getResources().getDrawable(R.color.transparent));
        listView.getListView().setDividerHeight(1);
        listView.setPullLoadEnable(true,"");
        listView.setPullRefreshEnable(true);
        listView.setIsAnimation(true);
        listView.setXListViewListener(this);
        listView.showFootView(false);
        listView.setRefreshTime();
        listView.setOnItemClickListener(this);

        listView.addHeader(headerView);

        mAdPlanDetailAdapter = new AdPlanDetailAdapter(getActivity(),this);
        listView.setAdapter(mAdPlanDetailAdapter);

        initViews();

        if(mAdPlanInfo != null)
            updatePlanData();
        else
        {
            mAdPlanInfo = new AdPlanInfo();
            String defaultName = DateUtils.getStringTimeChinese(System.currentTimeMillis()) + "计划";
            mAdPlanInfo.setName(defaultName);
            itvName.getSecondView().setText(mAdPlanInfo.getName());

            switchButton.setChecked(false);
            showShops(false);
            switchButtonDate.setChecked(false);
            showDateSelect(false);
        }

        /*listView.onFresh();
        listView.stopRefresh();*/

    }

    private void initViews()
    {
        itvName.getFirstView().setText(getResources().getString(R.string.plan_detail_name));
        itvShops.getFirstView().setText(getResources().getString(R.string.plan_detail_groups));
        itvDateStart.getFirstView().setText(getResources().getString(R.string.plan_detail_start_date));
        itvDateEnd.getFirstView().setText(getResources().getString(R.string.plan_detail_end_date));
        itvName.showMoreButton(true);
        itvShops.showMoreButton(true);
        itvDateStart.showMoreButton(true);
        itvDateEnd.showMoreButton(true);

        itvDateEnd.showBottomLine(false);

        viewAdd.setOnClickListener(this);
        itvName.getBgView().setOnClickListener(this);
        itvShops.getBgView().setOnClickListener(this);
        itvDateStart.getBgView().setOnClickListener(this);
        itvDateEnd.getBgView().setOnClickListener(this);

        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1)
            {
                // TODO Auto-generated method stub
                showShops(arg1);
            }
        });
        switchButtonDate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1)
            {
                // TODO Auto-generated method stub
                showDateSelect(arg1);
            }
        });

        startTimeDialog = new DoubleDatePickerDialog(getActivity(), 0, new DoubleDatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker startDatePicker, int startYear, int startMonthOfYear,
                                  int startDayOfMonth) {
                String textString = String.format("%d-%d-%d", startYear,
                        startMonthOfYear + 1, startDayOfMonth);
				String start = String.format("%d-%d-%d", startYear,
						startMonthOfYear + 1, startDayOfMonth);
				//String end = String.format("%d-%d-%d", endYear, endMonthOfYear + 1, endDayOfMonth);
                mAdPlanInfo.setStartDate(textString);
                itvDateStart.getSecondView().setText(getResources().getString(R.string.plan_detail_start_time) + ": " + textString);
            }
        },  null,getResources().getString(R.string.plan_detail_start_time));
        endTimeDialog = new DoubleDatePickerDialog(getActivity(), 0, new DoubleDatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker startDatePicker, int startYear, int startMonthOfYear,
                                  int startDayOfMonth) {
                String textString = String.format("%d-%d-%d", startYear,
                        startMonthOfYear + 1, startDayOfMonth);
				String start = String.format("%d-%d-%d", startYear,
						startMonthOfYear + 1, startDayOfMonth);
				//String end = String.format("%d-%d-%d", endYear, endMonthOfYear + 1, endDayOfMonth);*/
                mAdPlanInfo.setEndDate(textString);
                itvDateEnd.getSecondView().setText(getResources().getString(R.string.plan_detail_end_time) + ": "+ textString);
            }
        }, null, getResources().getString(R.string.plan_detail_end_time));
    }

    private void updatePlanData()
    {
        itvName.getSecondView().setText(mAdPlanInfo.getName());
        if(!TextUtils.isEmpty(mAdPlanInfo.getStartDate()))
        {
            itvDateStart.getSecondView().setText(getResources().getString(R.string.plan_detail_start_time) + ": " + mAdPlanInfo.getStartDate());
            itvDateEnd.getSecondView().setText(getResources().getString(R.string.plan_detail_end_time) + ": " + mAdPlanInfo.getEndDate());
            showDateSelect(true);
        }
        else
        {
            switchButtonDate.setChecked(false);
            showDateSelect(false);
        }

        if(mAdPlanInfo.isGroupPlan())
        {
            switchButton.setChecked(true);
            showShops(true);
            itvShops.getSecondView().setText(mAdPlanInfo.getGroupName());
        }
        else
        {
            switchButton.setChecked(false);
            showShops(false);
        }
        mAdPlanDetailAdapter.notifyDataSetChanged(mAdPlanInfo.getSubPlanList());
    }

    private void showShops(boolean isShow)
    {
        if(isShow)
        {
            itvShops.setVisibility(View.VISIBLE);
            itvShops.getSecondView().setText(mAdPlanInfo.getGroupName());
        }
        else
        {
            itvShops.setVisibility(View.GONE);
            //planInfor.setPlanFlag("0");
        }
    }
    private void showDateSelect(boolean isShow)
    {
        if(isShow)
        {
            itvDateStart.setVisibility(View.VISIBLE);
            itvDateEnd.setVisibility(View.VISIBLE);
            /*planInfor.setPlanType(PlanInfor.PLAN_TYPE_YEAR);
            itvDateStart.getSecondView().setText("开始时间：" + planInfor.getStartDate());
            itvDateEnd.getSecondView().setText("结束时间：" + planInfor.getEndDate());*/
            //planInfor.setPlanFlag("1");
        }
        else
        {
            itvDateStart.setVisibility(View.GONE);
            itvDateEnd.setVisibility(View.GONE);
            //planInfor.setPlanType(PlanInfor.PLAN_TYPE_EVERYDAY);
            //planInfor.setPlanFlag("0");
        }
    }

    private void createPlan()
    {

        try
        {
            String token = Constant.mUserInfo.getToken();
            String name = mAdPlanInfo.getName();
            String cycleTypes = mAdPlanInfo.getCycleTypes() + "";
            String playModels = mAdPlanInfo.getPlayModels() + "";
            String startTimes = mAdPlanInfo.getStartTimes() + "";
            String endTimes = mAdPlanInfo.getEndTimes() + "";
            String adinfoIds = mAdPlanInfo.getAdinfoIds() + "";
            String musicNums = mAdPlanInfo.getMusicNums() + "";
            String startDate = mAdPlanInfo.getStartDate();
            String endDate = mAdPlanInfo.getEndDate();
            String merchId = Constant.mUserInfo.getMerchant().getId();
            String groupId = mAdPlanInfo.getGroupId();
            if(!switchButton.isChecked())
                groupId = "";

            cycleTypes = removeTags(cycleTypes);
            playModels = removeTags(playModels);
            startTimes = removeTags(startTimes);
            endTimes = removeTags(endTimes);
            musicNums = removeTags(musicNums);
            adinfoIds = removeTags(adinfoIds);

            if(adinfoIds.startsWith(";"))
                adinfoIds = adinfoIds.substring(1);
            // Simulate network access.
            HttpClient.addAPlan(token,name,  cycleTypes,  startTimes, endTimes
                    ,  musicNums,  adinfoIds, startDate, endDate, groupId, merchId,playModels , new HttpCallback<CommonCallBackBean>() {
                        @Override
                        public void onSuccess(CommonCallBackBean resultBean)
                        {
                            if(resultBean.isSuccess())
                            {
                                finishAndFeedBack();
                            }
                            showToast(resultBean.getMessage());
                            Log.d("","");

                        }

                        @Override
                        public void onFail(Exception e) {
                            Log.d("","");
                            showToast(e.getMessage());
                        }
                    });
        }
        catch (Exception e)
        {
            Log.d("","");
        }
    }

    private void updatePlanToServer()
    {

        try
        {
            String token = Constant.mUserInfo.getToken();
            String id = mAdPlanInfo.getId();
            String name = mAdPlanInfo.getName();
            String cycleTypes = mAdPlanInfo.getCycleTypes() + "";
            String playModels = mAdPlanInfo.getPlayModels() + "";
            String startTimes = mAdPlanInfo.getStartTimes() + "";
            String endTimes = mAdPlanInfo.getEndTimes() + "";
            String adinfoIds = mAdPlanInfo.getAdinfoIds() + "";
            String musicNums = mAdPlanInfo.getMusicNums() + "";
            String startDate = mAdPlanInfo.getStartDate();
            String endDate = mAdPlanInfo.getEndDate();
            String merchId = Constant.mUserInfo.getMerchant().getId();
            String groupId = mAdPlanInfo.getGroupId();
            if(!switchButton.isChecked())
                groupId = "";

            cycleTypes = removeTags(cycleTypes);
            playModels = removeTags(playModels);
            startTimes = removeTags(startTimes);
            endTimes = removeTags(endTimes);
            musicNums = removeTags(musicNums);
            adinfoIds = removeTags(adinfoIds);

            // Simulate network access.
            HttpClient.updateAPlan(token,  id, name,  cycleTypes,  startTimes, endTimes
                    ,  musicNums,  adinfoIds, startDate, endDate, merchId, playModels , new HttpCallback<CommonCallBackBean>() {
                        @Override
                        public void onSuccess(CommonCallBackBean resultBean)
                        {
                            if(resultBean.isSuccess())
                            {
                                finishAndFeedBack();
                            }
                            showToast(resultBean.getMessage());
                            Log.d("","");

                        }

                        @Override
                        public void onFail(Exception e) {
                            Log.d("","");
                            showToast(e.getMessage());
                        }
                    });
        }
        catch (Exception e)
        {
            Log.e("",e.getMessage());
        }
    }

    private String removeTags(String src)
    {
        if(src.contains("["))
            src = src.replace("[","");
        if(src.contains("]"))
            src = src.replace("]","");
        return src;
    }

    private void finishAndFeedBack()
    {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("AD_PLAN_INFO", mAdPlanInfo);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK)
            return;
        if(requestCode == 1)
        {
            mAdPlanInfo = (AdPlanInfo) data.getSerializableExtra("AD_PLAN_INFO");
            //mPlanInfo.convertPlanToSubPlan();
            updatePlanData();
        }
        else if(requestCode == 2)
        {
            GroupInfo tempInfo = (GroupInfo) data.getSerializableExtra("GROUP_INFOR");
            mAdPlanInfo.setGroupName(tempInfo.getGroupName());
            mAdPlanInfo.setGroupId(tempInfo.getId());
            showShops(true);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if(position >= 2)
            position = position - 2;

        if(position >= mAdPlanInfo.getSubPlanList().size())
            return;

        Bundle bundle = new Bundle();
        bundle.putSerializable("AD_PLAN_INFO", mAdPlanInfo);
        mAdPlanInfo.setSelectedSubPlanIndex(position);
        bundle.putBoolean("IS_EDIT", true);
        ViewUtils.startActivity(getActivity(), AdPlanCreateActivity.class, bundle, 1);
    }

    @Override
    public void onMoreClick(int position) {

    }

    @Override
    public void onRefresh() {
        listView.stopRefresh();
    }

    @Override
    public void onLoadMore() {
        listView.stopRefresh();
    }

    @Override
    public void onClick(View v)
    {

        if(v == viewAdd)
        {
            Bundle bundle = new Bundle();
            bundle.putSerializable("AD_PLAN_INFO", mAdPlanInfo);

            bundle.putBoolean("IS_EDIT", false);
            ViewUtils.startActivity(getActivity(), AdPlanCreateActivity.class, bundle, 1);
        }
        else if(v == itvDateStart.getBgView())
        {

            startTimeDialog.showTimeDialog(null);
        }
        else if(v == itvDateEnd.getBgView())
        {
            endTimeDialog.showTimeDialog(null);

        }
        else if(v == itvShops.getBgView())
        {
            Bundle bundle = new Bundle();
            bundle.putString("GROUP_ID",mAdPlanInfo.getGroupId());
            bundle.putBoolean("IS_EDIT",true);
            //bundle.putSerializable("PlanInfor", planInfor);
            ViewUtils.startActivity(getActivity(), GroupListActivity.class, bundle, 2);
        }
        else if(v == itvName.getBgView())
        {
            TextInputBottomDialog mTextInputBottomDialog = new TextInputBottomDialog(getActivity());
            mTextInputBottomDialog.show("请输入计划名称", mAdPlanInfo.getName(), new TextInputBottomDialog.OnDismissResultListener() {
                @Override
                public void onConfirmDismiss(String content) {
                    mAdPlanInfo.setName(content);
                    itvName.getSecondView().setText(content);

                }
            });
        }

    }

    @Override
    public void OnScheDelete(int index) {
        mAdPlanInfo.removeSubPlan(index);
        mAdPlanDetailAdapter.notifyDataSetChanged();
    }
}
