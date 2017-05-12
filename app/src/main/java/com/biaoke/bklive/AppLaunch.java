package com.biaoke.bklive;

import android.app.Application;

import com.lkl.pay.app.application.ApplicationController;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
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
        //支付拉卡拉
        ApplicationController.initData(this);
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
        //  ############################  ImageLoader相关  ###############
        //初始化ImageLoader(加载选项相关设置)
        DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)/*开启硬盘缓存*/
                .cacheInMemory(true)/*开启内存缓存*/
                .resetViewBeforeLoading(true)/*加载前重置ImageView*/
                .build();

        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this)
                .memoryCacheSize(4 * 1024 * 1024)//设置内存缓存的大小（4M）
                .defaultDisplayImageOptions(displayImageOptions)//设置默认的加载选项
                .build();

        ImageLoader.getInstance().init(configuration);

        //异常捕获工具初始化
//        CrashHandler crashHandler = CrashHandler.getInstance();
//        crashHandler.setCustomCrashHanler(getApplicationContext());


        //上线了可以用来捕获用户产生的异常，把错误信息已文件的形式上传到服务器
        //调试期用它就不打印log了，啊啊
//        CrashHandlerHasee crashHandler = CrashHandlerHasee.getInstance();
//        crashHandler.init(getApplicationContext());
    }
}
