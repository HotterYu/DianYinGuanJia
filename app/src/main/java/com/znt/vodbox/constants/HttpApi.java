package com.znt.vodbox.constants;

public class HttpApi
{
    //private static final String API = "http://47.104.209.249:8080/api";
    public static final String API = "http://api.zhunit.com/api";

    public static final String LOGIN = API + "/user/login";

    public static final String ADD_SHOP = API + "/shop/add";
    public static final String GET_SHOP_LIST = API + "/shop/list";
    public static final String REMOVE_GROUP_SHOP = API + "/group/deltml";

    public static final String GET_USER_LIST= API + "/user/list";

    public static final String UPDATE_SHOP_INFO = API + "/shop/update";
    public static final String SHOP_PLAY_CONTROLL = API + "/tml/cmd/";

    //获取时段的歌曲列表
    public static final String GET_SCHEDULE_MUSICS = API + "/terminal/planschemusic";

    public static final String ADD_ALBUM = API + "/category/add";
    public static final String MODIFY_ALBUM = API + "/category/update";
    public static final String GET_MY_ALBUMS = API + "/category/list";
    public static final String GET_SYS_ALBUMS = API + "/query/category";
    public static final String COLLECT_ALBUM = API + "/category/copy";
    public static final String DELETE_ALBUM = API + "/category/del";
    public static final String GET_ALBUM_MUSICS = API + "/category/music";
    public static final String GET_ALBUM_TYPES = API + "/query/musictype";
    public static final String GET_AD_TYPES = API + "/query/adtype";
    public static final String DELETE_ALBUM_MUSICS = API + "/category/delMusic";
    public static final String ADD_MUSIC_TO_ALBUM = API + "/category/saveMusic";
    public static final String GET_SYS_MUSICS = API + "/query/search";
    public static final String GET_AD_LISTS = API + "/adinfo/list";
    public static final String PUSH_MEDIA = API + "/tml/push";
    public static final String GET_PLAN_LIST = API + "/plan/list";
    public static final String GET_AD_PLAN_LIST = API + "/adplan/list";
    public static final String GET_ALL_ZONES = API + "/group/list";
    public static final String DELETE_PLAN = API + "/plan/del";
    public static final String DELETE_AD_PLAN = API + "/adplan/del";
    public static final String UPDATE_PLAN = API + "/plan/update";
    public static final String GET_PLAN = API + "/terminal/plan";
    public static final String ADD_PLAN = API + "/plan/add";
    public static final String ADD_AD_PLAN = API + "/adplan/add";
    public static final String UPDATE_AD_PLAN = API + "/adplan/update";

    public static final String GET_GROUP_LIST = API + "/group/list";
    public static final String DELETE_GROUP = API + "/group/del";
    public static final String UPDATE_GROUP = API + "/group/update";
    public static final String ADD_GROUP = API + "/group/add";
    public static final String ADD_SHOP_TO_GROUP = API + "/group/addtml";

}
