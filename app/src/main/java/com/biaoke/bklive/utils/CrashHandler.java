package com.biaoke.bklive.utils;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

/**
 * Created by howie on 2017/5/4.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static volatile CrashHandler instance;

    private Context mContext;

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        if (instance == null) {
            synchronized (CrashHandler.class) {
                if (instance == null) {
                    instance = new CrashHandler();
                }
            }
        }
        return instance;
    }

    public void setCustomCrashHanler(Context context) {
        mContext = context;
        //崩溃时将catch住异常
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        //使用Toast进行提示
        showToast(mContext, "很抱歉，程序异常即将退出！"+throwable);
        //延时退出
        try {
            thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //退出程序
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    //线程中展示Toast
    private void showToast(final Context context, final String msg) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }).start();
    }

}
