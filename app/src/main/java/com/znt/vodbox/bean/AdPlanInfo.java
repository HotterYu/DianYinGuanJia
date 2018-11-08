package com.znt.vodbox.bean;

import android.text.TextUtils;

import com.znt.vodbox.utils.DateUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AdPlanInfo implements Serializable
{
    private String id;
    private String name;
    private String planModel;
    private String startDate;
    private String endDate;
    private List<String> scheIds = new ArrayList<>();
    private List<String> cycleTypes = new ArrayList<>();
    private List<String> playModels = new ArrayList<>();
    private List<String> startTimes = new ArrayList<>();
    private List<String> endTimes = new ArrayList<>();
    private List<String> musicNums = new ArrayList<>();
    private List<String> adinfoIds = new ArrayList<>();
    private List<String> adinfoNames = new ArrayList<>();
    private List<String> adUrls = new ArrayList<>();
    private String merchId;
    private String merchName;
    private String groupName;
    private String groupId;
    private String addTime;
    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }



    private List<SubAdPlanInfo> subPlanList = new ArrayList<SubAdPlanInfo>();
    public List<SubAdPlanInfo> getSubPlanList()
    {
        convertPlanToSubPlan();
        return subPlanList;
    }
    public void setSubPlanList(List<SubAdPlanInfo> subPlanList)
    {
        this.subPlanList = subPlanList;
        convertSubPlanToPlan();
    }

    public boolean isGroupPlan()
    {
        return !TextUtils.isEmpty(groupName) && !TextUtils.isEmpty(groupId);
    }

    private int selectedPlanIndex = 0;
    public SubAdPlanInfo getSelelctPlanInfor()
    {
        return getSubPlanList().get(selectedPlanIndex);
    }
    public void setSelectedSubPlanIndex(int selectedPlanIndex)
    {
        this.selectedPlanIndex = selectedPlanIndex;
    }


    public void updateSelect(SubAdPlanInfo infor)
    {
        SubAdPlanInfo tempInfor = getSelelctPlanInfor();
        infor.setId(tempInfor.getId());
        subPlanList.set(selectedPlanIndex, infor);
        convertSubPlanToPlan();
    }
    public void addSubPlanInfor(SubAdPlanInfo infor)
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
            SubAdPlanInfo tempInfo = new SubAdPlanInfo();
            tempInfo.setId(scheIds.get(i));
            tempInfo.setStartTime(getStartTimes().get(i));
            tempInfo.setEndTime(getEndTimes().get(i));
            tempInfo.setMusicNum(getMusicNums().get(i));
            tempInfo.setCycleType(getCycleTypes().get(i));
            tempInfo.setAdinfoId(getAdinfoIds().get(i));
            tempInfo.setAdinfoName(getAdinfoNames().get(i));
            tempInfo.setAdinfoUrl(getAdUrls().get(i));
            subPlanList.add(tempInfo);
        }
    }

    public void convertSubPlanToPlan()
    {
        int count = subPlanList.size();

        startTimes.clear();
        endTimes.clear();
        scheIds.clear();
        adUrls.clear();
        adinfoIds.clear();
        adinfoNames.clear();
        cycleTypes.clear();
        musicNums.clear();

        for(int i=0;i<count;i++)
        {
            SubAdPlanInfo subPlanInfor = subPlanList.get(i);
            startTimes.add(subPlanInfor.getStartTime());
            endTimes.add(subPlanInfor.getEndTime());
            scheIds.add(subPlanInfor.getId());
            cycleTypes.add(subPlanInfor.getCycleType());
            musicNums.add(subPlanInfor.getMusicNum());
            adUrls.add(subPlanInfor.getAdinfoUrl());
            adinfoIds.add(subPlanInfor.getAdinfoId());
            adinfoNames.add(subPlanInfor.getAdinfoName());
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlanModel() {
        return planModel;
    }

    public void setPlanModel(String planModel) {
        this.planModel = planModel;
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

    public List<String> getPlayModels() {
        return playModels;
    }

    public void setPlayModels(List<String> playModels) {
        this.playModels = playModels;
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

    public List<String> getMusicNums() {
        return musicNums;
    }

    public void setMusicNums(List<String> musicNums) {
        this.musicNums = musicNums;
    }

    public List<String> getAdinfoIds() {
        return adinfoIds;
    }

    public void setAdinfoIds(List<String> adinfoIds) {
        this.adinfoIds = adinfoIds;
    }

    public List<String> getAdinfoNames() {
        return adinfoNames;
    }

    public void setAdinfoNames(List<String> adinfoNames) {
        this.adinfoNames = adinfoNames;
    }

    public List<String> getAdUrls() {
        return adUrls;
    }

    public void setAdUrls(List<String> adUrls) {
        this.adUrls = adUrls;
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
        if(groupId == null)
            groupId = "";
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
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
            if(i != selectedPlanIndex)
            {
                SubAdPlanInfo tempInfor = subPlanList.get(i);
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
