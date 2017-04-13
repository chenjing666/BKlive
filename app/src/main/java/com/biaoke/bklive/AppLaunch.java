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
        //参数初始化化。
        StreamingEnv.init(getApplicationContext());
        //开启debug模式，方便定位错误，具体错误检查方式可以查看http://dev.umeng.com/social/android/quick-integration的报错必看，正式发布，请关闭该模式
        Config.DEBUG = true;
        QueuedWork.isUseThreadPool = false;
        UMShareAPI.get(this);
        {
            PlatformConfig.setWeixin("wxdc1e388c3822c80b", "3baf1193c85774b3fd9d18447d76cab0");
            PlatformConfig.setSinaWeibo("3921700954", "04b48b094faeb16683c32669824ebdad","http://sns.whalecloud.com");
            PlatformConfig.setQQZone("1106024311", "FGnkSQttl6C4Wdd6");
        }
    }
}
