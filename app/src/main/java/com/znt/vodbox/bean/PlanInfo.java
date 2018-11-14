package com.znt.vodbox.bean;

import android.text.TextUtils;

import com.znt.vodbox.utils.DateUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PlanInfo implements Serializable, Cloneable
{
    private String id = "";
    private String planName = "";
    private String planType = "";
    private String merchId = "";
    private String merchName = "";
    private String groupName = "";
    private String groupId = "";
    private String startDate = "";
    private String endDate = "";
    private List<String> scheIds = new ArrayList<>();
    private List<String> cycleTypes = new ArrayList<>();
    private List<String>  startTimes = new ArrayList<>();
    private List<String>  endTimes = new ArrayList<>();
    private List<String>  categoryIds = new ArrayList<>();
    private List<String>  categoryNames = new ArrayList<>();
    private String addTime;


    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public boolean isGroupPlan()
    {
        return !TextUtils.isEmpty(groupName) && !TextUtils.isEmpty(groupId);
    }

    private List<SubPlanInfor> subPlanList = new ArrayList<SubPlanInfor>();

    public List<SubPlanInfor> getSubPlanList()
    {
        convertPlanToSubPlan();
        return subPlanList;
    }
    public void setSubPlanList(List<SubPlanInfor> subPlanList)
    {
        this.subPlanList = subPlanList;
        convertSubPlanToPlan();
    }

    private int selectedPlanIndex = 0;
    public SubPlanInfor getSelelctPlanInfor()
    {
        if(getSubPlanList().size() > 0)
            return getSubPlanList().get(selectedPlanIndex);
        else
            return null;
    }
    public void setSelectedSubPlanIndex(int selectedPlanIndex)
    {
        this.selectedPlanIndex = selectedPlanIndex;
    }


    public void updateSelect(SubPlanInfor infor)
    {
        SubPlanInfor tempInfor = getSelelctPlanInfor();
        infor.setId(tempInfor.getId());
        subPlanList.set(selectedPlanIndex, infor);
        convertSubPlanToPlan();
    }
    public void addSubPlanInfor(SubPlanInfor infor)
    {
        subPlanList.add(infor);
        convertSubPlanToPlan();
    }

    public void removeSubPlan(int index)
    {
        subPlanList.remove(index);
        convertSubPlanToPlan();
    }

    public void convertPlanToSubPlan()
    {
        subPlanList.clear();
        for(int i=0;i<scheIds.size();i++)
        {
            SubPlanInfor tempInfo = new SubPlanInfor();
            tempInfo.setId(scheIds.get(i));
            tempInfo.setStartTime(getStartTimes().get(i));
            tempInfo.setEndTime(getEndTimes().get(i));
            tempInfo.setPlanAlbumName(getCategoryNames().get(i));
            tempInfo.setCycleType(getCycleTypes().get(i));
            tempInfo.setPlanAlbumId(getCategoryIds().get(i));
            subPlanList.add(tempInfo);
        }
    }

    public void convertSubPlanToPlan()
    {
        int count = subPlanList.size();

        startTimes.clear();
        endTimes.clear();
        scheIds.clear();
        categoryNames.clear();
        categoryIds.clear();
        cycleTypes.clear();

        for(int i=0;i<count;i++)
        {
            SubPlanInfor subPlanInfor = subPlanList.get(i);
            startTimes.add(subPlanInfor.getStartTime());
            endTimes.add(subPlanInfor.getEndTime());
            scheIds.add(subPlanInfor.getId());
            categoryNames.add(subPlanInfor.getPlanAlbumName());
            categoryIds.add(subPlanInfor.getPlanAlbumId());
            cycleTypes.add(subPlanInfor.getCycleType());
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public String getMerchId() {
        return merchId;
    }

    public void setMerchId(String merchId) {
        this.merchId = merchId;
    }

    public String getMerchName() {
        return merchName;
    }

    public void setMerchName(String merchName) {
        this.merchName = merchName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getStartDate() {
        if(startDate == null)
            startDate = "";
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        if(endDate == null)
            endDate = "";
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public List<String> getScheIds() {
        return scheIds;
    }

    public void setScheIds(List<String> scheIds) {
        this.scheIds = scheIds;
    }

    public List<String> getCycleTypes() {
        return cycleTypes;
    }


    public void setCycleTypes(List<String> cycleTypes) {
        this.cycleTypes = cycleTypes;
    }

    public List<String> getStartTimes() {

        return startTimes;
    }

    public void setStartTimes(List<String> startTimes) {
        this.startTimes = startTimes;
    }

    public List<String> getEndTimes() {
        return endTimes;
    }

    public void setEndTimes(List<String> endTimes) {
        this.endTimes = endTimes;
    }

    public List<String> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<String> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public List<String> getCategoryNames() {
        return categoryNames;
    }

    public void setCategoryNames(List<String> categoryNames) {
        this.categoryNames = categoryNames;
    }


    public boolean checkPlanTime(String startTime, String endTime)
    {
        boolean isValid = true;
        int start = DateUtils.timeToInt(startTime, ":");
        int end = DateUtils.timeToInt(endTime, ":");
        if(start == end)
            return false;
        int size = subPlanList.size();
        for(int i=0;i<size;i++)
        {
            if(i !=selectedPlanIndex)
            {
                SubPlanInfor tempInfor = subPlanList.get(i);
                int tempS = DateUtils.timeToInt(tempInfor.getStartTime(), ":");
                int tempE = DateUtils.timeToInt(tempInfor.getEndTime(), ":");
                if((tempS > tempE))
                {
                    if(start > end)
                    {
                        isValid = false;
                        break;
                    }
                }
                if(isTimeOverlap(tempS, tempE, start))
                {
                    isValid = false;
                    break;
                }
                if(isTimeOverlap(tempS, tempE, end))
                {
                    isValid = false;
                    break;
                }
                if(isTimeOverlap(start, end, tempS))
                {
                    isValid = false;
                    break;
                }
                if(isTimeOverlap(start, end, tempE))
                {
                    isValid = false;
                    break;
                }
            }
        }


		/*boolean isValid = true;
		int start = DateUtils.timeToInt(startTime, ":");
		int end = DateUtils.timeToInt(endTime, ":");
		if(start > end)
			end += 24 * 60;

		int size = subPlanList.size();
		for(int i=0;i<size;i++)
		{
			if(i !=selectIndex)
			{
				SubPlanInfor tempInfor = subPlanList.get(i);
				int tempS = DateUtils.timeToInt(tempInfor.getStartTime(), ":");
				int tempE = DateUtils.timeToInt(tempInfor.getEndTime(), ":");
				if(tempS > tempE)
				{
					if(start < tempE)
						start += 24 * 60;
					tempE += 24 * 60;
				}
				if(isTimeOverlap(tempS, tempE, start))
				{
					isValid = false;
					break;
				}
				if(isTimeOverlap(tempS, tempE, end))
				{
					isValid = false;
					break;
				}
				if(isTimeOverlap(start, end, tempS))
				{
					isValid = false;
					break;
				}
				if(isTimeOverlap(start, end, tempE))
				{
					isValid = false;
					break;
				}
			}
		}*/

        return isValid;
    }
    private boolean isTimeOverlap(int start, int end, int dest)
    {
        if(start > end)
        {
            if(dest > start && dest < end + 24 * 60)
                return true;
        }
        else
        {
            if(dest > start && dest < end)
                return true;
        }

        return false;
    }

}
