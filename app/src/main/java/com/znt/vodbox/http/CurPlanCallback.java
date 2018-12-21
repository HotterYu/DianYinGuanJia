package com.znt.vodbox.http;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;
import com.znt.vodbox.bean.MediaInfo;
import com.znt.vodbox.bean.PlanInfo;
import com.znt.vodbox.bean.PlanResultBean;
import com.znt.vodbox.constants.HttpApi;
import com.znt.vodbox.utils.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Response;

/**
 * Created by hzwangchenyan on 2017/2/8.
 */
public abstract class CurPlanCallback<T>  extends Callback<T>
{

    protected String RESULT_INFO = "data";
    protected String RESULT_OK = "resultcode";

    private Class<T> clazz;
    private Gson gson;

    public CurPlanCallback(Class<T> clazz)
    {
        this.clazz = clazz;
        gson = new Gson();
    }

    @Override
    public T parseNetworkResponse(Response response, int id) throws IOException
    {
        try
        {
            String jsonString = response.body().string();
            PlanResultBean curPlanResultBean = (PlanResultBean) gson.fromJson(jsonString, clazz);

            PlanInfo planInfo = curPlanResultBean.getData();
            List<MediaInfo> tempList = new ArrayList<>();
            int count = planInfo.getScheIds().size();
            for(int i=0;i<count;i++)
            {
                String tempScheId = planInfo.getScheIds().get(i);

                String tempCycleType = planInfo.getCycleTypes().get(i);
                String tempStartTime = planInfo.getStartTimes().get(i);
                String tempEndTime = planInfo.getEndTimes().get(i);

                int sLong = DateUtils.timeToInt(tempStartTime, ":");
                int eLong = DateUtils.timeToInt(tempEndTime, ":");

                String curTime = DateUtils.getStringTimeEnd(System.currentTimeMillis());
                int dLong = DateUtils.timeToInt(curTime, ":");

                if(isTimeOverlap(sLong,eLong, dLong))
                {
                    String tempCategoryIds = "";
                    int tempLen = planInfo.getCategoryIds().size();
                    for(int j=0;j<tempLen;j++)
                    {
                        tempCategoryIds += planInfo.getCategoryIds().get(j) + ",";
                    }
                    if(tempCategoryIds.endsWith(","))
                        tempCategoryIds = tempCategoryIds.substring(0, tempCategoryIds.length() - 1);
                    tempList = getScheduleMusics(planInfo.getId(),tempScheId,tempCategoryIds, tempStartTime,tempEndTime,tempCycleType);

                }

            }

            curPlanResultBean.setMediaList(tempList);

            return (T) curPlanResultBean;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private List<MediaInfo> getScheduleMusics(String planId, String scheId, String categoryIds,String startPlayTime, String endPlayTime, String week)
    {
        List<MediaInfo> tempList = new ArrayList<MediaInfo>();
        int scheduleMusicPageNum = 1;
        Map<String, String> params = new HashMap<String, String>();
        params.put("planId", planId);
        params.put("scheId", scheId);
        params.put("categoryIds", categoryIds);
        params.put("pageSize", 1000 + "");
        params.put("pageNo", scheduleMusicPageNum + "");
        try
        {
            Response response = OkHttpUtils.get().url(HttpApi.GET_SCHEDULE_MUSICS).id(10000).params(params).build().execute();
            if(response.isSuccessful())
            {
                String string = response.body().string();
                try
                {
                    JSONObject jsonObject = new JSONObject(string);
                    int result = jsonObject.getInt(RESULT_OK);
                    if(result == 1)
                    {
                        String info = getInforFromJason(jsonObject, RESULT_INFO);
                        JSONArray jsonArray = new JSONArray(info);
                        int count = jsonArray.length();
                        for(int i=0;i<count;i++)
                        {
                            JSONObject json = jsonArray.getJSONObject(i);
                            String musicId = getInforFromJason(json, "id");
                            String musicName = getInforFromJason(json, "musicName");
                            String musicSing = getInforFromJason(json, "musicSing");
                            String musicAlbum = getInforFromJason(json, "musicAlbum");
                            String musicDuration = getInforFromJason(json, "musicDuration");
                            String musicType = getInforFromJason(json, "musicType");
                            String sourceType = getInforFromJason(json, "sourceType");
                            String sourceId = getInforFromJason(json, "sourceId");
                            String singerid = getInforFromJason(json, "singerid");
                            String albumid = getInforFromJason(json, "albumid");
                            String fileSize = getInforFromJason(json, "fileSize");

                            String musicUrl = getInforFromJason(json, "musicUrl");
                            if(!TextUtils.isEmpty(musicUrl))
                                musicUrl = URLDecoder.decode(musicUrl);
                            MediaInfo tempInfor = new MediaInfo();
                            tempInfor.setId(musicId);
                            tempInfor.setMusicName(musicName);
                            tempInfor.setMusicUrl(musicUrl);
                            tempInfor.setMusicSing(musicSing);
                            tempInfor.setMusicAlbum(musicAlbum);

                            if(!TextUtils.isEmpty(musicDuration))
                            {
                                tempInfor.setMusicDuration(musicDuration);

                            }
                            if(!TextUtils.isEmpty(fileSize))
                            {
                                tempInfor.setFileSize(fileSize);
                            }

                            tempList.add(tempInfor);
                            //DBMediaHelper.getInstance().addMedia(tempInfor);

                        }
                    }
                }
                catch (Exception e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return tempList;
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

    protected String getInforFromJason(JSONObject json, String key)
    {
        if(json == null || key == null)
            return "";
        if(json.has(key))
        {
            try
            {
                String result = json.getString(key);
                if(result.equals("null"))
                    result = "";
                return result;
                //return StringUtils.decodeStr(result);
            }
            catch (JSONException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return "";
    }

}