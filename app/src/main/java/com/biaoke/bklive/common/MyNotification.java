package com.biaoke.bklive.common;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.biaoke.bklive.R;

/**
 * Created by hasee on 2017/5/19.
 */

public class MyNotification {

    private void sendNotification(Context context, String title, String content) {
        //获取NotificationManager实例
        NotificationManager notifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //实例化NotificationCompat.Builde并设置相关属性
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                //点击通知后自动清除
                .setAutoCancel(true)
                //调用系统默认响铃,设置此属性后setSound()会无效
                //.setDefaults(Notification.DEFAULT_SOUND)
                //调用系统多媒体裤内的铃声
                //.setSound(Uri.withAppendedPath(MediaStore.Audio.Media.INTERNAL_CONTENT_URI,"2"));
                //调用自己提供的铃声，位于 /res/values/raw 目录下
//                .setSound(Uri.parse("android.resource://com.littlejie.notification/" + R.raw.sound))
                //使用系统默认的震动参数,会与自定义的冲突
                //.setDefaults(Notification.DEFAULT_VIBRATE)
                //自定义震动效果
//                .setVibrate(vibrate)
                //ledARGB 表示灯光颜色、 ledOnMS 亮持续时间、ledOffMS 暗的时间
//                .setLights(0xFF0000, 3000, 3000)
                //设置小图标
                .setSmallIcon(R.drawable.logo_72)
                //设置通知标题
                .setContentTitle(title)
                //设置通知内容
                .setContentText(content);
//        Notification notify = builder.build();
        //只有在设置了标志符Flags为Notification.FLAG_SHOW_LIGHTS的时候，才支持呼吸灯提醒。
//        notify.flags = Notification.FLAG_SHOW_LIGHTS;
        //设置通知时间，默认为系统发出通知的时间，通常不用设置
        //.setWhen(System.currentTimeMillis());
        //通过builder.build()方法生成Notification对象,并发送通知,id=1
        notifyManager.notify(1, builder.build());
    }
}
