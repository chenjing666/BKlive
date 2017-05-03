package com.biaoke.bklive.message;

/**
 * Created by hasee on 2017/2/21.
 * 网络连接地址
 */
public class Api {
    public   final static String WSHOST = "ws://server-test.bk5977.com:8282";

    private static final String ADDRESS = "http:server-test.bk5977.com:8800";

    public final static String ENCRYPT64=ADDRESS+"/BK/api/en64.php";//加密接口

    public final static String UNENCRYPT64=ADDRESS+"/BK/api/un64.php";//解密接口

    public final static String LOGIN = ADDRESS + "/BK/Logging.php";//登陆接口

    public final static String LOGIN_UM = ADDRESS + "/BK/LoggingApi.php";//登陆接口

    public final static String REGISTER = ADDRESS + "/BK/Register.php";//注册接口

    public final static String LIVEPUT = ADDRESS + "/BK/Live.php";//直播接口

    public final static String USERINFO_USER = ADDRESS + "/BK/UserInfo.php";//用户信息接口

    public final static String LIVE_CAMERA = ADDRESS + "/BK/IsLive.php";//摄像头直播接口

    public final static String VIDEO_LIVE = ADDRESS + "/BK/Explore.php";//发现页面视频接口

    public final static String BANNER = ADDRESS + "/BK/BannerUp.php";//发现页面轮播图接口

    public final static String FOLLOW = ADDRESS + "/BK/Fans.php";//关注接口

    public final static String UPLOAD = ADDRESS + "/BK/Upload.php";//发现页面视频接口

    public final static String VIDEOLIST = ADDRESS + "/BK/Video.php";//视频列表接口

    public final static String GPS = ADDRESS + "/BK/UpGps.php";//上传当前坐标接口

    public final static String SEARCH = ADDRESS + "/BK/Search.php";//搜索接口

}
