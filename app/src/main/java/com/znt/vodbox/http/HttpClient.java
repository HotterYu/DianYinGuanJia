package com.znt.vodbox.http;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.znt.vodbox.bean.AdMediaListResultBean;
import com.znt.vodbox.bean.AdPlanListResultBean;
import com.znt.vodbox.bean.AlbumListResultBean;
import com.znt.vodbox.bean.CommonCallBackBean;
import com.znt.vodbox.bean.GourpListResultBean;
import com.znt.vodbox.bean.MusicListResultBean;
import com.znt.vodbox.bean.PlanListResultBean;
import com.znt.vodbox.bean.PlanResultBean;
import com.znt.vodbox.bean.ShopListResultBean;
import com.znt.vodbox.bean.TypeCallBackBean;
import com.znt.vodbox.bean.UserCallBackBean;
import com.znt.vodbox.bean.UserListCallBackBean;
import com.znt.vodbox.bean.ZoneListResultBean;
import com.znt.vodbox.constants.HttpApi;
import com.znt.vodbox.model.ArtistInfo;
import com.znt.vodbox.model.DownloadInfo;
import com.znt.vodbox.model.OnlineMusicList;
import com.znt.vodbox.model.SearchMusic;
import com.znt.vodbox.model.Shopinfo;
import com.znt.vodbox.model.Splash;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;

/**
 * Created by hzwangchenyan on 2017/2/8.
 */
public class HttpClient extends HttpApi{
    private static final String SPLASH_URL = "http://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1";
    private static final String BASE_URL = "http://tingapi.ting.baidu.com/v1/restserver/ting";
    private static final String METHOD_GET_MUSIC_LIST = "baidu.ting.billboard.billList";
    private static final String METHOD_DOWNLOAD_MUSIC = "baidu.ting.song.play";
    private static final String METHOD_ARTIST_INFO = "baidu.ting.artist.getInfo";
    private static final String METHOD_SEARCH_MUSIC = "baidu.ting.search.catalogSug";
    private static final String METHOD_LRC = "baidu.ting.song.lry";
    private static final String PARAM_METHOD = "method";
    private static final String PARAM_TYPE = "type";
    private static final String PARAM_SIZE = "size";
    private static final String PARAM_OFFSET = "offset";
    private static final String PARAM_SONG_ID = "songid";
    private static final String PARAM_TING_UID = "tinguid";
    private static final String PARAM_QUERY = "query";

    static {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(new HttpInterceptor())
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }

    public static void userLogin(String username, String password, @NonNull final HttpCallback<UserCallBackBean> callback) {
        OkHttpUtils.post().url(LOGIN)
                .addParams("clientType", "1")
                .addParams("username", username)
                .addParams("password", password)
                .build()
                .execute(new BaseHttpCallback<UserCallBackBean>(UserCallBackBean.class) {
                    @Override
                    public void onResponse(UserCallBackBean response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void addShop(String token,String terminalId, String shopName, String merchId,String shopCode,@NonNull final HttpCallback<CommonCallBackBean> callback) {
        OkHttpUtils.post().url(ADD_SHOP)
                .addHeader("token", token)
                .addParams("terminalId", terminalId)
                .addParams("shopName", shopName)
                .addParams("merchId", merchId)
                .addParams("shopCode", shopCode)
                .build()
                .execute(new BaseHttpCallback<CommonCallBackBean>(CommonCallBackBean.class) {
                    @Override
                    public void onResponse(CommonCallBackBean response, int id) {
                        if(response == null)
                            callback.onFail(null);
                        else
                            callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void updateShopInfo(String token, Shopinfo mShopinfo,@NonNull final HttpCallback<CommonCallBackBean> callback) {


        String id = mShopinfo.getId();
        String userShopCode = mShopinfo.getUserShopCode();
        String name = mShopinfo.getName();
        String tel = mShopinfo.getTel();
        String linkman = mShopinfo.getLinkman();
        String linkmanPhone = mShopinfo.getLinkmanPhone();
        String wifiName = mShopinfo.getWifiName();
        String wifiPassword = mShopinfo.getWifiPassword();
        String country = mShopinfo.getCountry();
        String province = mShopinfo.getProvince();
        String city = mShopinfo.getCity();
        String region = mShopinfo.getRegion();
        String address = mShopinfo.getAddress();
        String longitude = mShopinfo.getLongitude();
        String latitude = mShopinfo.getLatitude();

        String groupId = "";
        if(mShopinfo.getGroup() != null)
            groupId = mShopinfo.getGroup().getId();

        OkHttpUtils.post().url(UPDATE_SHOP_INFO)
                .addHeader("token", token)
                .addParams("id", id)
                .addParams("userShopCode", userShopCode)
                .addParams("name", name)
                .addParams("tel", tel)
                .addParams("linkman", linkman)
                .addParams("linkmanPhone", linkmanPhone)
                .addParams("wifiName", wifiName)
                .addParams("wifiPassword", wifiPassword)
                .addParams("country", country)
                .addParams("province", province)
                .addParams("city", city)
                .addParams("region", region)
                .addParams("address", address)
                .addParams("longitude", longitude)
                .addParams("latitude", latitude)
                .addParams("groupId", groupId)
                .build()
                .execute(new BaseHttpCallback<CommonCallBackBean>(CommonCallBackBean.class) {
                    @Override
                    public void onResponse(CommonCallBackBean response, int id) {
                        if(response == null)
                            callback.onFail(null);
                        else
                            callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void playControll(String token, String terminalId,String value,String type,@NonNull final HttpCallback<CommonCallBackBean> callback) {

        String url = SHOP_PLAY_CONTROLL + type;
        OkHttpUtils.post().url(url)
                .addHeader("token", token)
                .addParams("terminalId", terminalId)
                .addParams("value", value)
                .build()
                .execute(new BaseHttpCallback<CommonCallBackBean>(CommonCallBackBean.class) {
                    @Override
                    public void onResponse(CommonCallBackBean response, int id) {
                        if(response == null)
                            callback.onFail(null);
                        else
                            callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void addAlbum(String token,String name, String merchId, String description,String remark,@NonNull final HttpCallback<CommonCallBackBean> callback) {
        OkHttpUtils.post().url(ADD_ALBUM)
                .addHeader("token", token)
                .addParams("name", name)
                .addParams("merchId", merchId)
                .addParams("description", description)
                .addParams("remark", remark)
                .build()
                .execute(new BaseHttpCallback<CommonCallBackBean>(CommonCallBackBean.class) {
                    @Override
                    public void onResponse(CommonCallBackBean response, int id) {
                        if(response == null)
                            callback.onFail(null);
                        else
                            callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void modifyAlbum(String token,String id,String name, String merchId, String description,String remark,@NonNull final HttpCallback<CommonCallBackBean> callback) {
        OkHttpUtils.post().url(MODIFY_ALBUM)
                .addHeader("token", token)
                .addParams("id", id)
                .addParams("name", name)
                .addParams("merchId", merchId)
                .addParams("description", description)
                .addParams("remark", remark)
                .build()
                .execute(new BaseHttpCallback<CommonCallBackBean>(CommonCallBackBean.class) {
                    @Override
                    public void onResponse(CommonCallBackBean response, int id) {
                        if(response == null)
                            callback.onFail(null);
                        else
                            callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void deleteAlbum(String token,String id,@NonNull final HttpCallback<CommonCallBackBean> callback) {
        OkHttpUtils.post().url(DELETE_ALBUM)
                .addHeader("token", token)
                .addParams("id", id)
                .build()
                .execute(new BaseHttpCallback<CommonCallBackBean>(CommonCallBackBean.class) {
                    @Override
                    public void onResponse(CommonCallBackBean response, int id) {
                        if(response == null)
                            callback.onFail(null);
                        else
                            callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void addMusicToAlbum(String token,String id,String musicIds,@NonNull final HttpCallback<CommonCallBackBean> callback) {
        OkHttpUtils.post().url(ADD_MUSIC_TO_ALBUM)
                .addHeader("token", token)
                .addParams("id", id)
                .addParams("musicIds", musicIds)
                .build()
                .execute(new BaseHttpCallback<CommonCallBackBean>(CommonCallBackBean.class) {
                    @Override
                    public void onResponse(CommonCallBackBean response, int id) {
                        if(response == null)
                            callback.onFail(null);
                        else
                            callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void removeGroupShop(String token,String id,String shopIds,@NonNull final HttpCallback<CommonCallBackBean> callback) {
        OkHttpUtils.post().url(REMOVE_GROUP_SHOP)
                .addHeader("token", token)
                .addParams("id", id)
                .addParams("shopIds", shopIds)
                .build()
                .execute(new BaseHttpCallback<CommonCallBackBean>(CommonCallBackBean.class) {
                    @Override
                    public void onResponse(CommonCallBackBean response, int id) {
                        if(response == null)
                            callback.onFail(null);
                        else
                            callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void getAllShops(String token, String pageNo, String pageSize,String merchId, String groupId, String memberId,
                                   String name, String shopCode, String userShopCode, String terminalId, String oldId, String onlinestatus,
                                   @NonNull final HttpCallback<ShopListResultBean> callback) {
        OkHttpUtils.post().url(GET_SHOP_LIST)
                .addHeader("token", token)
                .addParams("clientType", "1")
                .addParams("merchId", merchId)
                .addParams("groupId", groupId)
                .addParams("memberId", memberId)
                .addParams("name", name)
                .addParams("shopCode", shopCode)
                .addParams("userShopCode", userShopCode)
                .addParams("terminalId", terminalId)
                .addParams("oldId", oldId)
                .addParams("onlineStatus", onlinestatus)
                .addParams("pageSize", pageSize)
                .addParams("pageNo", pageNo)
                .build()
                .execute(new BaseHttpCallback<ShopListResultBean>(ShopListResultBean.class) {
                    @Override
                    public void onResponse(ShopListResultBean response, int id) {
                        if(response == null)
                            callback.onFail(null);
                        else
                            callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void getPlan(String token, String planId, @NonNull final HttpCallback<PlanResultBean> callback) {
        OkHttpUtils.post().url(GET_PLAN)
                .addHeader("token", token)
                .addParams("planId", planId)
                .build()
                .execute(new CurPlanCallback<PlanResultBean>(PlanResultBean.class) {
                    @Override
                    public void onResponse(PlanResultBean response, int id) {
                        if(response == null)
                            callback.onFail(null);
                        else
                            callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void getMyAlbums(String token, String pageNo, String pageSize,String merchId, String typeId, String name,@NonNull final HttpCallback<AlbumListResultBean> callback) {
        OkHttpUtils.post().url(GET_MY_ALBUMS)
                .addHeader("token", token)
                .addParams("merchId", merchId)
                .addParams("typeId", typeId)
                .addParams("name", name)
                .addParams("pageSize", pageSize)
                .addParams("pageNo", pageNo)
                .build()
                .execute(new BaseHttpCallback<AlbumListResultBean>(AlbumListResultBean.class) {
                    @Override
                    public void onResponse(AlbumListResultBean response, int id) {
                        if(response == null)
                            callback.onFail(null);
                        else
                            callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void getSystemAlbums(String token, String pageNo, String pageSize,String typeId, String name,@NonNull final HttpCallback<AlbumListResultBean> callback) {
        OkHttpUtils.post().url(GET_SYS_ALBUMS)
                .addHeader("token", token)
                .addParams("typeId", typeId)
                .addParams("typeId", typeId)
                .addParams("name", name)
                .addParams("pageSize", pageSize)
                .addParams("pageNo", pageNo)
                .build()
                .execute(new BaseHttpCallback<AlbumListResultBean>(AlbumListResultBean.class) {
                    @Override
                    public void onResponse(AlbumListResultBean response, int id) {
                        if(response == null)
                            callback.onFail(null);
                        else
                            callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void getAlbumMusics(String token, String pageNo, String pageSize,String id, String searchWord
            ,@NonNull final HttpCallback<MusicListResultBean> callback) {
        OkHttpUtils.post().url(GET_ALBUM_MUSICS)
                .addHeader("token", token)
                .addParams("id", id)
                .addParams("searchWord", searchWord)
                .addParams("pageSize", pageSize)
                .addParams("pageNo", pageNo)
                .build()
                .execute(new BaseHttpCallback<MusicListResultBean>(MusicListResultBean.class) {
                    @Override
                    public void onResponse(MusicListResultBean response, int id) {
                        if(response == null)
                            callback.onFail(null);
                        else
                            callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void updateAlbumMusicSort(String token, String id, String musicIds, String orderNumbers
            ,@NonNull final HttpCallback<CommonCallBackBean> callback) {

        OkHttpUtils.post().url(UPDATE_ALBUM_MUSIC_SORT)
                .addHeader("token", token)
                .addParams("id", id)
                .addParams("musicIds", musicIds)
                .addParams("orderNumbers", orderNumbers)
                .build()
                .execute(new BaseHttpCallback<CommonCallBackBean>(CommonCallBackBean.class) {
                    @Override
                    public void onResponse(CommonCallBackBean response, int id) {
                        if(response == null)
                            callback.onFail(null);
                        else
                            callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }
    public static void getAlbumTypes(String token, String searchWord
            ,@NonNull final HttpCallback<TypeCallBackBean> callback) {

        OkHttpUtils.post().url(GET_ALBUM_TYPES)
                .addHeader("token", token)
                .addParams("searchWord", searchWord)
                .build()
                .execute(new BaseHttpCallback<TypeCallBackBean>(TypeCallBackBean.class) {
                    @Override
                    public void onResponse(TypeCallBackBean response, int id) {
                        if(response == null)
                            callback.onFail(null);
                        else
                            callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void getAdTypes(String token, String searchWord
            ,@NonNull final HttpCallback<TypeCallBackBean> callback) {

        OkHttpUtils.post().url(GET_AD_TYPES)
                .addHeader("token", token)
                .addParams("searchWord", searchWord)
                .build()
                .execute(new BaseHttpCallback<TypeCallBackBean>(TypeCallBackBean.class) {
                    @Override
                    public void onResponse(TypeCallBackBean response, int id) {
                        if(response == null)
                            callback.onFail(null);
                        else
                            callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void deleteAlbumMusics(String token,String id, String musicIds,@NonNull final HttpCallback<CommonCallBackBean> callback) {
        OkHttpUtils.post().url(DELETE_ALBUM_MUSICS)
                .addHeader("token", token)
                .addParams("id", id)
                .addParams("musicIds", musicIds)
                .build()
                .execute(new BaseHttpCallback<CommonCallBackBean>(CommonCallBackBean.class) {
                    @Override
                    public void onResponse(CommonCallBackBean response, int id) {
                        if(response == null)
                            callback.onFail(null);
                        else
                            callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void collectAlbum(String token,String categoryid, String merchId,@NonNull final HttpCallback<CommonCallBackBean> callback) {
        OkHttpUtils.post().url(COLLECT_ALBUM)
                .addHeader("token", token)
                .addParams("categoryid", categoryid)
                .addParams("merchId", merchId)
                .build()
                .execute(new BaseHttpCallback<CommonCallBackBean>(CommonCallBackBean.class) {
                    @Override
                    public void onResponse(CommonCallBackBean response, int id) {
                        if(response == null)
                            callback.onFail(null);
                        else
                            callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void getSystemMusics(String token, String pageNo, String pageSize,String searchWord
            ,@NonNull final HttpCallback<MusicListResultBean> callback) {
        OkHttpUtils.post().url(GET_SYS_MUSICS)
                .addHeader("token", token)
                .addParams("searchWord", searchWord)
                .addParams("pageSize", pageSize)
                .addParams("pageNo", pageNo)
                .build()
                .execute(new BaseHttpCallback<MusicListResultBean>(MusicListResultBean.class) {
                    @Override
                    public void onResponse(MusicListResultBean response, int id) {
                        if(response == null)
                            callback.onFail(null);
                        else
                            callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void getCurPlayMusics(String token, String terminalId
            ,@NonNull final HttpCallback<MusicListResultBean> callback) {
        OkHttpUtils.get().url(GET_CUR_PLAY_MUSICS)
                //.addHeader("token", token)
                .addParams("terminalId", terminalId)
                .build()
                .execute(new BaseHttpCallback<MusicListResultBean>(MusicListResultBean.class) {
                    @Override
                    public void onResponse(MusicListResultBean response, int id) {
                        if(response == null)
                            callback.onFail(null);
                        else
                            callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void getCurPushMusics(String terminalId
            ,@NonNull final HttpCallback<MusicListResultBean> callback) {
        OkHttpUtils.get().url(GET_CUR_PUSH_MUSICS)
                .addParams("terminalId", terminalId)
                .build()
                .execute(new BaseHttpCallback<MusicListResultBean>(MusicListResultBean.class) {
                    @Override
                    public void onResponse(MusicListResultBean response, int id) {
                        if(response == null)
                            callback.onFail(null);
                        else
                            callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }


    /**
     * terminalId	false	int	终端id
     pusherId	true	string	插播人，如果是登录用户填userid，如果是小程序非登录用户直接pusherid
     vodFlag	false	string	0-推荐 1-插播，如果不填默认为插播
     * @param terminalId
     * @param pusherId
     * @param vodFlag
     * @param callback
     */
    public static void getPushHistoryMusics(String terminalId, String pusherId,String vodFlag
            ,@NonNull final HttpCallback<MusicListResultBean> callback) {
        OkHttpUtils.get().url(GET_PUSH_HISTORY_MUSICS)
                .addParams("terminalId", terminalId)
                .build()
                .execute(new BaseHttpCallback<MusicListResultBean>(MusicListResultBean.class) {
                    @Override
                    public void onResponse(MusicListResultBean response, int id) {
                        if(response == null)
                            callback.onFail(null);
                        else
                            callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void getAdLists(String token, String pageNo, String pageSize,String merchId, String adtypeId, String adname
            ,@NonNull final HttpCallback<AdMediaListResultBean> callback) {
        OkHttpUtils.post().url(GET_AD_LISTS)
                .addHeader("token", token)
                .addParams("merchId", merchId)
                .addParams("adtypeId", adtypeId)
                .addParams("adname", adname)
                .addParams("pageSize", pageSize)
                .addParams("pageNo", pageNo)
                .build()
                .execute(new BaseHttpCallback<AdMediaListResultBean>(AdMediaListResultBean.class) {
                    @Override
                    public void onResponse(AdMediaListResultBean response, int id) {
                        if(response == null)
                            callback.onFail(null);
                        else
                            callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void updateAdInfo(String token, String id, String adname,String adtypeId, String adduration,@NonNull final HttpCallback<CommonCallBackBean> callback) {
        OkHttpUtils.post().url(UPDATE_AD_INFO)
                .addHeader("token", token)
                .addParams("id", id)
                .addParams("adname", adname)
                .addParams("adtypeId", adtypeId)
                .addParams("adduration", adduration)
                .build()
                .execute(new BaseHttpCallback<CommonCallBackBean>(CommonCallBackBean.class) {
                    @Override
                    public void onResponse(CommonCallBackBean response, int id) {
                        if(response == null)
                            callback.onFail(null);
                        else
                            callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void deleteAd(String token, String id,@NonNull final HttpCallback<CommonCallBackBean> callback) {
        OkHttpUtils.post().url(DELETE_AD)
                .addHeader("token", token)
                .addParams("id", id)
                .build()
                .execute(new BaseHttpCallback<CommonCallBackBean>(CommonCallBackBean.class) {
                    @Override
                    public void onResponse(CommonCallBackBean response, int id) {
                        if(response == null)
                            callback.onFail(null);
                        else
                            callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void getUserList(String token, String pageNo, String pageSize,String username, String nickName,String orgzId
            ,@NonNull final HttpCallback<UserListCallBackBean> callback) {
        OkHttpUtils.post().url(GET_USER_LIST)
                .addHeader("token", token)
                .addParams("username", username)
                .addParams("nickName", nickName)
                .addParams("orgzId", orgzId)
                .addParams("pageSize", pageSize)
                .addParams("pageNo", pageNo)
                .build()
                .execute(new BaseHttpCallback<UserListCallBackBean>(UserListCallBackBean.class) {
                    @Override
                    public void onResponse(UserListCallBackBean response, int id) {
                        if(response == null)
                            callback.onFail(null);
                        else
                            callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void getAllZoness(String token, String pageNo, String pageSize,String merchId, String adminId,String groupName
            ,@NonNull final HttpCallback<ZoneListResultBean> callback) {
        OkHttpUtils.post().url(GET_ALL_ZONES)
                .addHeader("token", token)
                .addParams("merchId", merchId)
                .addParams("adminId", adminId)
                .addParams("groupName", groupName)
                .addParams("pageSize", pageSize)
                .addParams("pageNo", pageNo)
                .build()
                .execute(new BaseHttpCallback<ZoneListResultBean>(ZoneListResultBean.class) {
                    @Override
                    public void onResponse(ZoneListResultBean response, int id) {
                        if(response == null)
                            callback.onFail(null);
                        else
                            callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void pushMedia(String terminalId, String type, String dataId,String userId
            , String pusherid, String pushername,@NonNull final HttpCallback<CommonCallBackBean> callback) {
        OkHttpUtils.post().url(PUSH_MEDIA)
                .addParams("terminalId", terminalId)
                .addParams("type", type)
                .addParams("dataId", dataId)
                .addParams("userId", userId)
                .addParams("clientType", "0")
                .addParams("pusherid", pusherid)
                .addParams("pushername", pushername)
                .build()
                .execute(new BaseHttpCallback<CommonCallBackBean>(CommonCallBackBean.class) {
                    @Override
                    public void onResponse(CommonCallBackBean response, int id) {
                        if(response == null)
                            callback.onFail(null);
                        else
                            callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void getPlanList(String token,String pageNo, String pageSize, String id,String merchId
            , String groupId, String planName,@NonNull final HttpCallback<PlanListResultBean> callback) {
        OkHttpUtils.post().url(GET_PLAN_LIST)
                .addHeader("token", token)
                .addParams("pageNo", pageNo)
                .addParams("pageSize", pageSize)
                .addParams("id", id)
                .addParams("merchId", merchId)
                .addParams("groupId", groupId)
                .addParams("planName", planName)
                .build()
                .execute(new BaseHttpCallback<PlanListResultBean>(PlanListResultBean.class) {
                    @Override
                    public void onResponse(PlanListResultBean response, int id) {
                        if(response == null)
                            callback.onFail(null);
                        else
                            callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void deletePlan(String token,String id,@NonNull final HttpCallback<CommonCallBackBean> callback) {
        OkHttpUtils.post().url(DELETE_PLAN)
                .addHeader("token", token)

                .addParams("id", id)
                .build()
                .execute(new BaseHttpCallback<CommonCallBackBean>(CommonCallBackBean.class) {
                    @Override
                    public void onResponse(CommonCallBackBean response, int id) {
                        if(response == null)
                            callback.onFail(null);
                        else
                            callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void deleteAdPlan(String token,String id,@NonNull final HttpCallback<CommonCallBackBean> callback) {
        OkHttpUtils.post().url(DELETE_AD_PLAN)
                .addHeader("token", token)

                .addParams("id", id)
                .build()
                .execute(new BaseHttpCallback<CommonCallBackBean>(CommonCallBackBean.class) {
                    @Override
                    public void onResponse(CommonCallBackBean response, int id) {
                        if(response == null)
                            callback.onFail(null);
                        else
                            callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void getAdPlanList(String token,String pageNo, String pageSize, String id,String merchId
            , String groupId, String name,@NonNull final HttpCallback<AdPlanListResultBean> callback) {
        OkHttpUtils.post().url(GET_AD_PLAN_LIST)
                .addHeader("token", token)
                .addParams("pageNo", pageNo)
                .addParams("pageSize", pageSize)
                .addParams("id", id)
                .addParams("merchId", merchId)
                .addParams("groupId", groupId)
                .addParams("name", name)
                .build()
                .execute(new BaseHttpCallback<AdPlanListResultBean>(AdPlanListResultBean.class) {
                    @Override
                    public void onResponse(AdPlanListResultBean response, int id) {
                        if(response == null)
                            callback.onFail(null);
                        else
                            callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void addPlan(String token,String groupId, String planName, String cycleTypes,String startTimes
            , String endTimes, String categoryIds,String startDate,String endDate,String merchId,@NonNull final HttpCallback<CommonCallBackBean> callback) {
        OkHttpUtils.post().url(ADD_PLAN)
                .addHeader("token", token)
                .addParams("groupId", groupId)
                .addParams("planName", planName)
                .addParams("cycleTypes", cycleTypes)
                .addParams("startTimes", startTimes)
                .addParams("endTimes", endTimes)
                .addParams("categoryIds", categoryIds)
                .addParams("startDate", startDate)
                .addParams("endDate", endDate)
                .addParams("merchId", merchId)
                .build()
                .execute(new BaseHttpCallback<CommonCallBackBean>(CommonCallBackBean.class) {
                    @Override
                    public void onResponse(CommonCallBackBean response, int id) {
                        if(response == null)
                            callback.onFail(null);
                        else
                            callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void updatePlanToServer(String token,String id, String planName, String cycleTypes,String startTimes
            , String endTimes, String categoryIds,String startDate,String endDate,String merchId,@NonNull final HttpCallback<CommonCallBackBean> callback) {
        OkHttpUtils.post().url(UPDATE_PLAN)
                .addHeader("token", token)
                .addParams("id", id)
                .addParams("planName", planName)
                .addParams("cycleTypes", cycleTypes)
                .addParams("startTimes", startTimes)
                .addParams("endTimes", endTimes)
                .addParams("categoryIds", categoryIds)
                .addParams("startDate", startDate)
                .addParams("endDate", endDate)
                .addParams("merchId", merchId)
                .build()
                .execute(new BaseHttpCallback<CommonCallBackBean>(CommonCallBackBean.class) {
                    @Override
                    public void onResponse(CommonCallBackBean response, int id) {
                        if(response == null)
                            callback.onFail(null);
                        else
                            callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void addAPlan(String token,String name, String cycleTypes, String startTimes,String endTimes
            , String musicNums, String adinfoIds,String startDate,String endDate,String groupId,String merchId, String playModels,@NonNull final HttpCallback<CommonCallBackBean> callback) {
        OkHttpUtils.post().url(ADD_AD_PLAN)
                .addHeader("token", token)
                .addParams("name", name)
                .addParams("cycleTypes", cycleTypes)
                .addParams("startTimes", startTimes)
                .addParams("endTimes", endTimes)
                .addParams("musicNums", musicNums)
                .addParams("adinfoIds", adinfoIds)
                .addParams("startDate", startDate)
                .addParams("endDate", endDate)
                .addParams("groupId", groupId)
                .addParams("merchId", merchId)
                .addParams("playModels", playModels)
                .build()
                .execute(new BaseHttpCallback<CommonCallBackBean>(CommonCallBackBean.class) {
                    @Override
                    public void onResponse(CommonCallBackBean response, int id) {
                        if(response == null)
                            callback.onFail(null);
                        else
                            callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void updateAPlan(String token,String id,String name, String cycleTypes, String startTimes,String endTimes
            , String musicNums, String adinfoIds,String startDate,String endDate,String merchId,String playModels,@NonNull final HttpCallback<CommonCallBackBean> callback) {
        OkHttpUtils.post().url(UPDATE_AD_PLAN)
                .addHeader("token", token)
                .addParams("id", id)
                .addParams("name", name)
                .addParams("cycleTypes", cycleTypes)
                .addParams("startTimes", startTimes)
                .addParams("endTimes", endTimes)
                .addParams("musicNums", musicNums)
                .addParams("adinfoIds", adinfoIds)
                .addParams("startDate", startDate)
                .addParams("endDate", endDate)
                .addParams("merchId", merchId)
                .addParams("playModels", playModels)
                .build()
                .execute(new BaseHttpCallback<CommonCallBackBean>(CommonCallBackBean.class) {
                    @Override
                    public void onResponse(CommonCallBackBean response, int id) {
                        if(response == null)
                            callback.onFail(null);
                        else
                            callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }


    public static void getSplash(@NonNull final HttpCallback<Splash> callback) {
        OkHttpUtils.get().url(SPLASH_URL).build()
                .execute(new JsonCallback<Splash>(Splash.class) {
                    @Override
                    public void onResponse(Splash response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void downloadFile(String url, String destFileDir, String destFileName, @Nullable final HttpCallback<File> callback) {
        OkHttpUtils.get().url(url).build()
                .execute(new FileCallBack(destFileDir, destFileName) {
                    @Override
                    public void inProgress(float progress, long total, int id) {
                    }

                    @Override
                    public void onResponse(File file, int id) {
                        if (callback != null) {
                            callback.onSuccess(file);
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (callback != null) {
                            callback.onFail(e);
                        }
                    }

                    @Override
                    public void onAfter(int id) {
                        if (callback != null) {
                            callback.onFinish();
                        }
                    }
                });
    }

    public static void getSongListInfo(String type, int size, int offset, @NonNull final HttpCallback<OnlineMusicList> callback) {
        OkHttpUtils.get().url(BASE_URL)
                .addParams(PARAM_METHOD, METHOD_GET_MUSIC_LIST)
                .addParams(PARAM_TYPE, type)
                .addParams(PARAM_SIZE, String.valueOf(size))
                .addParams(PARAM_OFFSET, String.valueOf(offset))
                .build()
                .execute(new JsonCallback<OnlineMusicList>(OnlineMusicList.class) {
                    @Override
                    public void onResponse(OnlineMusicList response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void getGroupList(String token, String pageNo, String pageSize,String merchId, String adminId, String groupName,@NonNull final HttpCallback<GourpListResultBean> callback) {
        OkHttpUtils.post().url(GET_GROUP_LIST)
                .addHeader("token", token)
                .addParams("merchId", merchId)
                .addParams("adminId", adminId)
                .addParams("groupName", groupName)
                .addParams("pageSize", pageSize)
                .addParams("pageNo", pageNo)
                .build()
                .execute(new BaseHttpCallback<GourpListResultBean>(GourpListResultBean.class) {
                    @Override
                    public void onResponse(GourpListResultBean response, int id) {
                        if(response == null)
                            callback.onFail(null);
                        else
                            callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void deleteGroup(String token, String id,@NonNull final HttpCallback<CommonCallBackBean> callback) {
        OkHttpUtils.post().url(DELETE_GROUP)
                .addHeader("token", token)
                .addParams("id", id)
                .build()
                .execute(new BaseHttpCallback<CommonCallBackBean>(CommonCallBackBean.class) {
                    @Override
                    public void onResponse(CommonCallBackBean response, int id) {
                        if(response == null)
                            callback.onFail(null);
                        else
                            callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void renameGroupName(String token, String id,String groupName,String adminId,@NonNull final HttpCallback<CommonCallBackBean> callback) {
        OkHttpUtils.post().url(UPDATE_GROUP)
                .addHeader("token", token)
                .addParams("id", id)
                .addParams("groupName", groupName)
                .addParams("adminId", adminId)
                .build()
                .execute(new BaseHttpCallback<CommonCallBackBean>(CommonCallBackBean.class) {
                    @Override
                    public void onResponse(CommonCallBackBean response, int id) {
                        if(response == null)
                            callback.onFail(null);
                        else
                            callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void addGroup(String token, String merchId,String groupName,String adminId,@NonNull final HttpCallback<CommonCallBackBean> callback) {
        OkHttpUtils.post().url(ADD_GROUP)
                .addHeader("token", token)
                .addParams("merchId", merchId)
                .addParams("groupName", groupName)
                .addParams("adminId", adminId)
                .build()
                .execute(new BaseHttpCallback<CommonCallBackBean>(CommonCallBackBean.class) {
                    @Override
                    public void onResponse(CommonCallBackBean response, int id) {
                        if(response == null)
                            callback.onFail(null);
                        else
                            callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void addShopTopGroup(String token, String id,String shopIds,@NonNull final HttpCallback<CommonCallBackBean> callback) {
        OkHttpUtils.post().url(ADD_SHOP_TO_GROUP)
                .addHeader("token", token)
                .addParams("id", id)
                .addParams("shopIds", shopIds)
                .build()
                .execute(new BaseHttpCallback<CommonCallBackBean>(CommonCallBackBean.class) {
                    @Override
                    public void onResponse(CommonCallBackBean response, int id) {
                        if(response == null)
                            callback.onFail(null);
                        else
                            callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void getMusicDownloadInfo(String songId, @NonNull final HttpCallback<DownloadInfo> callback) {
        OkHttpUtils.get().url(BASE_URL)
                .addParams(PARAM_METHOD, METHOD_DOWNLOAD_MUSIC)
                .addParams(PARAM_SONG_ID, songId)
                .build()
                .execute(new JsonCallback<DownloadInfo>(DownloadInfo.class) {
                    @Override
                    public void onResponse(DownloadInfo response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void getBitmap(String url, @NonNull final HttpCallback<Bitmap> callback) {
        OkHttpUtils.get().url(url).build()
                .execute(new BitmapCallback() {
                    @Override
                    public void onResponse(Bitmap bitmap, int id) {
                        callback.onSuccess(bitmap);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void updateUserInfo(String token, String id, String nickName,@NonNull final HttpCallback<CommonCallBackBean> callback) {
        OkHttpUtils.post().url(UPDATE_USER_INFO)
                .addHeader("token", token)
                .addParams("id", id)
                .addParams("nickName", nickName)
                .build()
                .execute(new BaseHttpCallback<CommonCallBackBean>(CommonCallBackBean.class) {
                    @Override
                    public void onResponse(CommonCallBackBean response, int id) {
                        if(response == null)
                            callback.onFail(null);
                        else
                            callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void updateUserPwd(String token, String oldpassword, String password,@NonNull final HttpCallback<CommonCallBackBean> callback) {
        OkHttpUtils.post().url(UPDATE_USER_PWD)
                .addHeader("token", token)
                .addParams("oldpassword", oldpassword)
                .addParams("password", password)
                .build()
                .execute(new BaseHttpCallback<CommonCallBackBean>(CommonCallBackBean.class) {
                    @Override
                    public void onResponse(CommonCallBackBean response, int id) {
                        if(response == null)
                            callback.onFail(null);
                        else
                            callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void searchMusic(String keyword, @NonNull final HttpCallback<SearchMusic> callback) {
        OkHttpUtils.get().url(BASE_URL)
                .addParams(PARAM_METHOD, METHOD_SEARCH_MUSIC)
                .addParams(PARAM_QUERY, keyword)
                .build()
                .execute(new JsonCallback<SearchMusic>(SearchMusic.class) {
                    @Override
                    public void onResponse(SearchMusic response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void getArtistInfo(String tingUid, @NonNull final HttpCallback<ArtistInfo> callback) {
        OkHttpUtils.get().url(BASE_URL)
                .addParams(PARAM_METHOD, METHOD_ARTIST_INFO)
                .addParams(PARAM_TING_UID, tingUid)
                .build()
                .execute(new JsonCallback<ArtistInfo>(ArtistInfo.class) {
                    @Override
                    public void onResponse(ArtistInfo response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }
}
