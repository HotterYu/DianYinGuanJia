package com.znt.vodbox.http;

import android.content.Context;

import com.google.gson.Gson;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Response;

/**
 * Created by hzwangchenyan on 2017/2/8.
 */
public abstract class BaseHttpCallback<T>  extends Callback<T>
{
    private Class<T> clazz;
    private Gson gson;

    private Context mContext = null;

    public BaseHttpCallback(Class<T> clazz)
    {
        this.clazz = clazz;
        gson = new Gson();
    }

    public BaseHttpCallback(Class<T> clazz, Context mContext)
    {
        this.clazz = clazz;
        this.mContext = mContext;
        gson = new Gson();
    }

    @Override
    public T parseNetworkResponse(Response response, int id) throws IOException
    {
        try
        {
            String jsonString = response.body().string();
            return gson.fromJson(jsonString, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getInfoFromJson(JSONObject jsonObject, String key)
    {
        if(jsonObject == null)
            return null;
        if(jsonObject.has(key))
        {
            try {
                return jsonObject.getString(key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}