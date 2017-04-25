package com.biaoke.bklive.websocket;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.biaoke.bklive.eventbus.Event_chatroom;
import com.biaoke.bklive.eventbus.Event_chatroom_errorMsg;
import com.biaoke.bklive.message.Api;

import de.greenrobot.event.EventBus;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;
import de.tavendo.autobahn.WebSocketOptions;

/**
 * Created by hasee on 2017/4/13.
 */

public class WebSocketService extends Service {
    private static final String TAG = WebSocketService.class.getSimpleName();
    private static boolean isClosed = true;
    public static final String WEBSOCKET_ACTION = "WEBSOCKET_ACTION";
    private BroadcastReceiver connectionReceiver;
    private static WebSocketConnection webSocketConnection;
    //    private static String websocketHost = "ws://server-test.bk5977.com:8282";
    private static WebSocketOptions options = new WebSocketOptions();
    private static boolean isExitApp = false;

    //发消息
    public static void sendMsg(String s) {
        Log.d(TAG, "sendMsg = " + s);
        if (!TextUtils.isEmpty(s))
            if (webSocketConnection != null) {
                webSocketConnection.sendTextMessage(s);
            }
    }

    //建立连接
    public static void webSocketConnect() {
        webSocketConnection = new WebSocketConnection();
        try {
            webSocketConnection.connect(Api.WSHOST, new WebSocketHandler() {
                //websocket启动时候的回调
                @Override
                public void onOpen() {
                    super.onOpen();
                    isClosed = false;
                    Log.d("webSocketConnection", "建立连接");
                }

                //websocket接收到消息后的回调
                @Override
                public void onTextMessage(String payload) {
                    Log.d(TAG, "收到消息 = " + payload);
                    EventBus.getDefault().post(new Event_chatroom(payload));
                }

                //websocket关闭时候的回调
                @Override
                public void onClose(int code, String reason) {
                    super.onClose(code, reason);
                    Log.d(TAG, "关闭code = " + code + "关闭reason = " + reason);
                    if (code == 2) {
                        EventBus.getDefault().post(new Event_chatroom_errorMsg("连接服务器失败"));
                    } else if (code == 3) {
                        //退出聊天室
                        EventBus.getDefault().post(new Event_chatroom_errorMsg("您已退出聊天室"));
                    }
                }
            }, options);
        } catch (WebSocketException e) {
            e.printStackTrace();
        }

    }

    //关闭连接
    public static void closeWebsocket(boolean exitApp) {
        isExitApp = exitApp;
        if (webSocketConnection != null && webSocketConnection.isConnected()) {
            webSocketConnection.disconnect();
            webSocketConnection = null;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (connectionReceiver == null) {
            connectionReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                    if (networkInfo == null || !networkInfo.isAvailable()) {
                        Toast.makeText(getApplicationContext(), "网络已断开，请重新连接", Toast.LENGTH_SHORT).show();
                    } else {
                        if (webSocketConnection != null) {
                            webSocketConnection.disconnect();
                        }
                        if (isClosed) {
                            webSocketConnect();
                        }
                    }

                }
            };

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(connectionReceiver, intentFilter);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (connectionReceiver != null) {
            unregisterReceiver(connectionReceiver);
        }
    }

}
