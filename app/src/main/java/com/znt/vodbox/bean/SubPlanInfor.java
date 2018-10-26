/*  
* @Project: ZNTVodBox 
* @User: Administrator 
* @Description: 家庭音乐
* @Author： yan.yu
* @Company：http://www.zhunit.com/
* @Date 2016-6-17 上午12:32:55 
* @Version V1.1   
*/ 

package com.znt.vodbox.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** 
 * @ClassName: SubPlanInfor 
 * @Description: TODO
 * @author yan.yu 
 * @date 2016-6-17 上午12:32:55  
 */
public class SubPlanInfor implements Serializable 
{

	/** 
	* @Fields serialVersionUID : TODO
	*/ 
	private static final long serialVersionUID = 1L;
	
	private String startTime = "";
	private String endTime = "";
	private String id = "";
	private String loopAddNum = "";
	private String cycleType = "";
	private String loopMusicName = "";
	private String loopMusicInfoId = "";
	private String planAlbumName = "";
	private String planAlbumId = "";

    public String getPlanAlbumId() {
        return planAlbumId;
    }

    public void setPlanAlbumId(String planAlbumId) {
        this.planAlbumId = planAlbumId;
    }


	public void setPlanAlbumName(String planAlbumName) {
		this.planAlbumName = planAlbumName;
	}
	public String getPlanAlbumName()
    {
        return planAlbumName;
    }

	public void setId(String id)
	{
		this.id = id;
	}
	public String getId()
	{
		return id;
	}
	
	public void setLoopAddNum(String loopAddNum)
	{
		this.loopAddNum = loopAddNum;
	}
	public String getLoopAddNum()
	{
		return loopAddNum;
	}
	public void setCycleType(String cycleType)
	{
		this.cycleType = cycleType;
	}
	public String getCycleType()
	{
		return cycleType;
	}
	public void setLoopMusicName(String loopMusicName)
	{
		this.loopMusicName = loopMusicName;
	}
	public String getLoopMusicName()
	{
		return loopMusicName;
	}
	public void setLoopMusicInfoId(String loopMusicInfoId)
	{
		this.loopMusicInfoId = loopMusicInfoId;
	}
	public String getLoopMusicInfoId()
	{
		return loopMusicInfoId;
	}
	
	public void setStartTime(String startTime)
	{
		this.startTime = startTime;
	}
	public String getStartTime()
	{
		return removeSecond(startTime);
	}
	
	public void setEndTime(String endTime)
	{
		this.endTime = endTime;
	}
	public String getEndTime()
	{
		return removeSecond(endTime);
	}
	
	public String getPlanTime()
	{
		return getStartTime() + " ~ " + getEndTime();
	}

	private String removeSecond(String time)
	{
		if(time.length()>=8)
		{
			int index = time.lastIndexOf(":");
			time = time.substring(0,index);
		}
		return time;
	}

	private List<AlbumInfo> albumList = new ArrayList<AlbumInfo>();
	public void setAlbumList(List<AlbumInfo> albumList)
	{
		this.albumList = albumList;
		planAlbumName = getPlanAlbumFormat();
		planAlbumId = getPlanAlbumIds();
	}
	public List<AlbumInfo> getAlbumList()
	{
		String[] planAlbumNameStr = planAlbumName.split(";");
		String[] planAlbumIdStr = planAlbumId.split(";");
		List<String> names = Arrays.asList(planAlbumNameStr);
		List<String> ids = Arrays.asList(planAlbumIdStr);
		albumList.clear();

		for (int i=0;i<names.size();i++)
		{
			AlbumInfo tempInfo = new AlbumInfo();
			String name = names.get(i);
			String id = ids.get(i);
			tempInfo.setName(name);
			tempInfo.setId(id);
			albumList.add(tempInfo);
		}

		return albumList;
	}
	public void addAlbumInfor(AlbumInfo infor)
	{
		albumList.add(infor);
	}
	/*public String getPlanAlbumName()
	{
		String planAlbum = "";
		int size = albumList.size();
		String tag = " ,  ";
		for(int i=0;i<size;i++)
		{
			AlbumInfo tempInfor = albumList.get(i);
			if(i < size - 1)
				planAlbum += tempInfor.getName() + tag;
			else
				planAlbum += tempInfor.getName();
		}
		return planAlbum;
	}*/
	public String getPlanAlbumFormat()
	{
		String planAlbum = "";
		int size = albumList.size();
		String tag = ";";
		for(int i=0;i<size;i++)
		{
			AlbumInfo tempInfor = albumList.get(i);
			if(i < size - 1)
				planAlbum += tempInfor.getName() + tag;
			else
				planAlbum += tempInfor.getName();
		}
		return planAlbum;
	}
	public String getPlanAlbumIds()
	{
		String planAlbum = "";
		int size = albumList.size();
		String tag = ";";
		for(int i=0;i<size;i++)
		{
			AlbumInfo tempInfor = albumList.get(i);
			if(i < size - 1)
				planAlbum += tempInfor.getId() + tag;
			else
				planAlbum += tempInfor.getId();
		}
		return planAlbum;
	}
	
}
 
