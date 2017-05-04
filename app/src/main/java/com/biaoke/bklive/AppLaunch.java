package com.biaoke.bklive;

import android.app.Application;

import com.qiniu.pili.droid.streaming.StreamingEnv;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.common.QueuedWork;

/**
 * Created by hasee on 2017/3/30.
 */

public class AppLaunch extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //参数初始化化
        StreamingEnv.init(getApplicationContext());
        //开启debug模式，方便定位错误，具体错误检查方式可以查看http://dev.umeng.com/social/android/quick-integration的报错必看，正式发布，请关闭该模式
        Config.DEBUG = true;
        QueuedWork.isUseThreadPool = false;
        UMShareAPI.get(this);
        {
            PlatformConfig.setWeixin("wx393fb4ea2ce1243b", "6725feca1c9c58ad76c2a023ae5c6110");
            PlatformConfig.setSinaWeibo("53400529", "87045c1fed3f93ef5fda9429352a2333","http://sns.whalecloud.com");
            PlatformConfig.setQQZone("1106024311", "FGnkSQttl6C4Wdd6");
        }
        //异常捕获工具初始化
        //上线了可以用来捕获用户产生的异常，把错误信息已文件的形式上传到服务器
        //调试期用它就不打印log了，啊啊
//        CrashHandler crashHandler = CrashHandler.getInstance();
//        crashHandler.init(getApplicationContext());
    }
}
