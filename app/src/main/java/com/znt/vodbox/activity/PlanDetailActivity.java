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
import android.widget.ImageView;
import android.widget.TextView;

import com.lvfq.pickerview.TimePickerView;
import com.znt.vodbox.R;
import com.znt.vodbox.adapter.OnMoreClickListener;
import com.znt.vodbox.adapter.PlanDetailAdapter;
import com.znt.vodbox.bean.CommonCallBackBean;
import com.znt.vodbox.bean.GroupInfo;
import com.znt.vodbox.bean.PlanInfo;
import com.znt.vodbox.bean.SubPlanInfor;
import com.znt.vodbox.dialog.TextInputBottomDialog;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.http.HttpCallback;
import com.znt.vodbox.http.HttpClient;
import com.znt.vodbox.utils.PickViewUtil;
import com.znt.vodbox.utils.StringUtils;
import com.znt.vodbox.utils.ViewUtils;
import com.znt.vodbox.utils.binding.Bind;
import com.znt.vodbox.view.ItemTextView;
import com.znt.vodbox.view.SwitchButton;
import com.znt.vodbox.view.xlistview.LJListView;

import java.util.ArrayList;
import java.util.List;

public class PlanDetailActivity extends BaseActivity  implements
        LJListView.IXListViewListener, AdapterView.OnItemClickListener,OnMoreClickListener
,View.OnClickListener,PlanDetailAdapter.OnScheDeleteListener {


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

    private TextView tvHint = null;

    private View viewAdd = null;

    private View viewApply = null;

    private SwitchButton switchButton = null;

    private SwitchButton switchButtonDate = null;

    private LJListView listView = null;
    private PlanDetailAdapter mPlanDetailAdapter = null;

    private List<SubPlanInfor> subPlanList = new ArrayList<SubPlanInfor>();

    private PlanInfo mPlanInfo = null;
    private boolean isEdit = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPlanInfo = (PlanInfo)getIntent().getSerializableExtra("PLAN_INFO");

        if(mPlanInfo != null)
        {
            isEdit = true;
            if(TextUtils.isEmpty(mPlanInfo.getId()))
                isEdit = false;
        }

        tvTopTitle.setText(getResources().getString(R.string.plan_detail));
        ivTopMore.setVisibility(View.GONE);
        tvConfirm.setVisibility(View.VISIBLE);
        tvConfirm.setText(getResources().getString(R.string.save));
        ivTopReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAndFeedBack();
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

        viewApply = headerView.findViewById(R.id.view_plan_detail_apply);
        switchButton = (SwitchButton)headerView.findViewById(R.id.sb_plan_detail);
        switchButtonDate = (SwitchButton)headerView.findViewById(R.id.sb_plan_detail_date);

        tvHint = (TextView)headerView.findViewById(R.id.tv_plan_detail_plan_time);
        itvName = (ItemTextView)headerView.findViewById(R.id.itv_plan_detail_name);
        itvShops = (ItemTextView)headerView.findViewById(R.id.itv_plan_detail_shops);
        itvDateStart = (ItemTextView)headerView.findViewById(R.id.itv_plan_detail_date_select_start);
        itvDateEnd = (ItemTextView)headerView.findViewById(R.id.itv_plan_detail_date_select_end);
        viewAdd = headerView.findViewById(R.id.view_plan_detail_add);

        tvHint.setOnClickListener(this);
        headerView.setOnClickListener(this);

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


        mPlanDetailAdapter = new PlanDetailAdapter(getActivity(),this);
        listView.setAdapter(mPlanDetailAdapter);

        initViews();

        if(mPlanInfo != null)
            updatePlanData();
        else
        {
            mPlanInfo = new PlanInfo();
            mPlanInfo.setPlanName(getResources().getString(R.string.plan_name_default));
            itvName.getSecondView().setText(mPlanInfo.getPlanName());

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

        if(mPlanInfo != null && mPlanInfo.isGroupPlan())
        {
            switchButton.setChecked(true);
            showShops(true);
        }
        else
        {
            switchButton.setChecked(false);
            showShops(false);
        }
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
    }

    private void updatePlanData()
    {
        itvName.getSecondView().setText(mPlanInfo.getPlanName());
        if(!TextUtils.isEmpty(mPlanInfo.getStartDate()))
        {
            itvDateStart.getSecondView().setText(getResources().getString(R.string.plan_detail_start_time) + ": " + mPlanInfo.getStartDate());
            itvDateEnd.getSecondView().setText(getResources().getString(R.string.plan_detail_end_time) + ": " + mPlanInfo.getEndDate());
            switchButtonDate.setChecked(true);
            showDateSelect(true);
        }
        else
        {
            switchButtonDate.setChecked(false);
            showDateSelect(false);
        }
        mPlanDetailAdapter.notifyDataSetChanged(mPlanInfo.getSubPlanList());
    }

    private void showShops(boolean isShow)
    {
        if(isShow)
        {
            itvShops.setVisibility(View.VISIBLE);
            itvShops.getSecondView().setText(mPlanInfo.getGroupName());
            //planInfor.setPlanFlag("1");
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

        }
        else
        {
            itvDateStart.setVisibility(View.GONE);
            itvDateEnd.setVisibility(View.GONE);

        }
    }

    private void createPlan()
    {

        try
        {
            String token = Constant.mUserInfo.getToken();
            String planName = mPlanInfo.getPlanName();
            String cycleTypes = mPlanInfo.getCycleTypes() + "";
            String startTimes = mPlanInfo.getStartTimes() + "";
            String endTimes = mPlanInfo.getEndTimes() + "";
            String categoryIds = mPlanInfo.getCategoryIds() + "";
            String startDate = mPlanInfo.getStartDate();
            String endDate = mPlanInfo.getEndDate();
            String merchId = Constant.mUserInfo.getMerchant().getId();
            String groupId = mPlanInfo.getGroupId();
            if(!switchButton.isChecked())
                groupId = "";

            cycleTypes = removeTags(cycleTypes);
            startTimes = removeTimeTags(startTimes);
            endTimes = removeTimeTags(endTimes);
            categoryIds = removeTags(categoryIds);

            // Simulate network access.
            HttpClient.addPlan(token, groupId, planName,cycleTypes,startTimes,endTimes,categoryIds,startDate,endDate
                   ,merchId , new HttpCallback<CommonCallBackBean>() {
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
            String id = mPlanInfo.getId();
            String planName = mPlanInfo.getPlanName();
            String cycleTypes = mPlanInfo.getCycleTypes() + "";
            String startTimes = mPlanInfo.getStartTimes() + "";
            String endTimes = mPlanInfo.getEndTimes() + "";
            String categoryIds = mPlanInfo.getCategoryIds() + "";
            String startDate = mPlanInfo.getStartDate();
            String endDate = mPlanInfo.getEndDate();
            String merchId = Constant.mUserInfo.getMerchant().getId();
            String groupId = mPlanInfo.getGroupId();
            if(!switchButton.isChecked())
                groupId = "";

            cycleTypes = removeTags(cycleTypes);
            startTimes = removeTimeTags(startTimes);
            endTimes = removeTimeTags(endTimes);
            categoryIds = removeTags(categoryIds);
            if(!switchButton.isChecked())
                groupId = "";
            // Simulate network access.
            HttpClient.updatePlanToServer(token, id, planName,cycleTypes,startTimes,endTimes,categoryIds,startDate,endDate
                    ,merchId , new HttpCallback<CommonCallBackBean>() {
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

        }
    }

    private void finishAndFeedBack()
    {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("PLAN_INFO", mPlanInfo);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();

    }

    private String removeTags(String src)
    {
        if(src.contains("["))
            src = src.replace("[","");
        if(src.contains("]"))
            src = src.replace("]","");
        return src;
    }

    private String removeTimeTags(String src)
    {
        if(src.contains("["))
            src = src.replace("[","");
        if(src.contains("]"))
            src = src.replace("]","");
        String[] srcArray = src.split(",");
        src = "";
        for(int i=0;i<srcArray.length;i++)
        {
            String s = srcArray[i];
            s = removeSecond(s);
            src += s + ",";
        }
        if(src.endsWith(","))
            src = src.substring(0,src.lastIndexOf(","));
        return src;
    }

    private String removeSecond(String time)
    {
        if(StringUtils.countStr(time,":") > 1)
        {
            int index = time.lastIndexOf(":");
            time = time.substring(0,index);
        }
        return time;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK)
            return;
        if(requestCode == 1)
        {
            mPlanInfo = (PlanInfo) data.getSerializableExtra("PLAN_INFO");
            //mPlanInfo.convertPlanToSubPlan();
            updatePlanData();
        }
        else if(requestCode == 2)
        {
            GroupInfo tempInfo = (GroupInfo) data.getSerializableExtra("GROUP_INFOR");
            mPlanInfo.setGroupName(tempInfo.getGroupName());
            mPlanInfo.setGroupId(tempInfo.getId());
            showShops(true);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if(position >= 2)
            position = position - 2;

        if(position >= mPlanInfo.getSubPlanList().size())
            return;

        Bundle bundle = new Bundle();
        bundle.putSerializable("PLAN_INFO", mPlanInfo);
        mPlanInfo.setSelectedSubPlanIndex(position);
        bundle.putBoolean("IS_EDIT", true);
        ViewUtils.startActivity(getActivity(), PlanCreateActivity.class, bundle, 1);
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
            bundle.putSerializable("PLAN_INFO", mPlanInfo);

            bundle.putBoolean("IS_EDIT", false);
            ViewUtils.startActivity(getActivity(), PlanCreateActivity.class, bundle, 1);
        }
        else if(v == itvDateStart.getBgView())
        {

            PickViewUtil.alertTimerPicker(PlanDetailActivity.this, TimePickerView.Type.YEAR_MONTH_DAY, "yyyy-MM-dd", new PickViewUtil.TimerPickerCallBack() {
                @Override
                public void onTimeSelect(String date) {

                    mPlanInfo.setStartDate(date);
                    itvDateStart.getSecondView().setText(getResources().getString(R.string.plan_detail_start_time) + ": " + date);
                }
            });
        }
        else if(v == itvDateEnd.getBgView())
        {

            PickViewUtil.alertTimerPicker(PlanDetailActivity.this, TimePickerView.Type.YEAR_MONTH_DAY, "yyyy-MM-dd", new PickViewUtil.TimerPickerCallBack() {
                @Override
                public void onTimeSelect(String date) {

                    mPlanInfo.setEndDate(date);
                    itvDateEnd.getSecondView().setText(getResources().getString(R.string.plan_detail_end_time) + ": "+ date);
                }
            });

        }
        else if(v == itvShops.getBgView())
        {
            Bundle bundle = new Bundle();
            bundle.putString("GROUP_ID",mPlanInfo.getGroupId());
            bundle.putBoolean("IS_EDIT",true);
            //bundle.putSerializable("PlanInfor", planInfor);
            ViewUtils.startActivity(getActivity(), GroupListActivity.class, bundle, 2);
        }
        else if(v == itvName.getBgView())
        {
            //showNameEditDialog(mPlanInfo.getPlanName());
            TextInputBottomDialog mTextInputBottomDialog = new TextInputBottomDialog(getActivity());
            mTextInputBottomDialog.show("请输入计划名称", mPlanInfo.getPlanName(), new TextInputBottomDialog.OnDismissResultListener() {
                @Override
                public void onConfirmDismiss(String content) {
                    mPlanInfo.setPlanName(content);
                    itvName.getSecondView().setText(content);
                }
            });
        }

    }


    @Override
    public void OnScheDelete(int index) {
        mPlanInfo.removeSubPlan(index);
        mPlanDetailAdapter.notifyDataSetChanged();
    }
}
