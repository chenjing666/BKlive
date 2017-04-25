package com.biaoke.bklive.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.biaoke.bklive.R;
import com.biaoke.bklive.adapter.LivingroomChatListAdapter;
import com.biaoke.bklive.adapter.LivingroomChatSysAdapter;
import com.biaoke.bklive.base.BaseActivity;
import com.biaoke.bklive.bean.LivingroomChatListBean;
import com.biaoke.bklive.eventbus.Event_chatroom;
import com.biaoke.bklive.message.Api;
import com.biaoke.bklive.message.AppConsts;
import com.biaoke.bklive.utils.GlideUtis;
import com.biaoke.bklive.websocket.WebSocketService;
import com.qiniu.pili.droid.streaming.AVCodecType;
import com.qiniu.pili.droid.streaming.CameraStreamingSetting;
import com.qiniu.pili.droid.streaming.MediaStreamingManager;
import com.qiniu.pili.droid.streaming.MicrophoneStreamingSetting;
import com.qiniu.pili.droid.streaming.StreamingProfile;
import com.qiniu.pili.droid.streaming.StreamingState;
import com.qiniu.pili.droid.streaming.StreamingStateChangedListener;
import com.qiniu.pili.droid.streaming.WatermarkSetting;
import com.qiniu.pili.droid.streaming.widget.AspectFrameLayout;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Response;

public class SWCameraStreamingActivity extends BaseActivity implements StreamingStateChangedListener {
    @BindView(R.id.et_live_title)
    EditText etLiveTitle;
    @BindView(R.id.iv_liveshare_sina)
    ImageView ivLiveshareSina;
    @BindView(R.id.iv_liveshare_wechat)
    ImageView ivLiveshareWechat;
    @BindView(R.id.iv_liveshare_wechatfriend)
    ImageView ivLiveshareWechatfriend;
    @BindView(R.id.iv_liveshare_qq)
    ImageView ivLiveshareQq;
    @BindView(R.id.iv_liveshare_qqq)
    ImageView ivLiveshareQqq;
    @BindView(R.id.btn_start_live)
    Button btnStartLive;
    @BindView(R.id.live_cancel)
    ImageView liveCancel;
    @BindView(R.id.liveDescription)
    RelativeLayout liveDescription;
    @BindView(R.id.iv_chatbarrage)
    ImageView ivChatbarrage;
    @BindView(R.id.input_editor_live)
    EditText inputEditorLive;
    @BindView(R.id.input_bar)
    RelativeLayout inputBar;
    @BindView(R.id.input_sendmsg)
    TextView inputSendmsg;
    @BindView(R.id.input_message_liveroom)
    LinearLayout inputMessageLiveroom;
    @BindView(R.id.cameraPreview_surfaceView)
    GLSurfaceView cameraPreviewSurfaceView;
    @BindView(R.id.cameraPreview_afl)
    AspectFrameLayout cameraPreviewAfl;
    @BindView(R.id.ll_live_top)
    RelativeLayout llLiveTop;
    @BindView(R.id.ll_live_title)
    LinearLayout llLiveTitle;
    @BindView(R.id.ll_live_share)
    LinearLayout llLiveShare;
    @BindView(R.id.living_livingroom_upvot)
    ImageView livingLivingroomUpvot;
    @BindView(R.id.bottom_bar_living)
    RelativeLayout bottomBarLiving;
    @BindView(R.id.livingroom_user_image)
    ImageView livingroomUserImage;
    @BindView(R.id.myNickname)
    TextView myNickname;
    @BindView(R.id.online_people)
    TextView onlinePeople;
    @BindView(R.id.video_headimg_xrv)
    RecyclerView videoHeadimgXrv;
    @BindView(R.id.living_close)
    ImageView livingClose;
    @BindView(R.id.tv_charm_living_show)
    TextView tvCharmLivingShow;
    @BindView(R.id.charm_more)
    ImageView charmMore;
    @BindView(R.id.tv_livingroom_manage)
    TextView tvLivingroomManage;
    @BindView(R.id.chat_recyclerview_living)
    RecyclerView chatRecyclerviewLiving;
    @BindView(R.id.ll_livingroom_header)
    LinearLayout llLivingroomHeader;
    @BindView(R.id.ll_livingroom_headtwo)
    LinearLayout llLivingroomHeadtwo;
    @BindView(R.id.rl_chat_recyclerview_living)
    RelativeLayout rlChatRecyclerviewLiving;

    private MediaStreamingManager mMediaStreamingManager;
    private String liveUrl = null;
    private StreamingProfile mProfile;
    private AspectFrameLayout afl;
    private GLSurfaceView glSurfaceView;
    private CameraStreamingSetting cameraStreamingSetting;
    private MicrophoneStreamingSetting microphoneStreamingSetting;
    private WatermarkSetting watermarksetting;
    private PopupWindow popupWindow_pickup, popupWindow_living_share, popupWindow_living_message, popupWindow_living_chat;
    @BindView(R.id.iv_livingroom_comments)
    ImageView ivLivingroomComments;
    @BindView(R.id.iv_livingroom_private_message)
    ImageView ivLivingroomPrivateMessage;
    @BindView(R.id.living_livingroom_share)
    ImageView livingLivingroomShare;
    @BindView(R.id.iv_livingroom_pickup)
    ImageView ivLivingroomPickup;
    @BindView(R.id.iv_livingroom_music)
    ImageView ivLivingroomMusic;
    private GlideUtis glideUtis;
    private String userId;
    private String accessKey;
    private String mNickName;
    private String IconUrl;
    private String Level;
    private String Charm;

    //websocket
    private Intent websocketServiceIntent;

    private List<LivingroomChatListBean> chatList = new ArrayList<>();
    private LivingroomChatListAdapter livingroomChatListAdapter;
    private LivingroomChatListBean livingroomChatListBean_chatmsg;
    //系统提示
    private LivingroomChatSysAdapter livingroomChatSysAdapter;

    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //使布局延伸到状态栏
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_swcamera_streaming);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);//注册
        liveDescription.setVisibility(View.VISIBLE);
        bottomBarLiving.setVisibility(View.GONE);
        llLivingroomHeader.setVisibility(View.GONE);
        llLivingroomHeadtwo.setVisibility(View.GONE);
        rlChatRecyclerviewLiving.setVisibility(View.GONE);
        websocketServiceIntent = new Intent(SWCameraStreamingActivity.this, WebSocketService.class);
        startService(websocketServiceIntent);
        WebSocketService.webSocketConnect();
        initLive();
        SharedPreferences sharedPreferences_user = getSharedPreferences("isLogin", Context.MODE_PRIVATE);//首先获取用户ID，直播要取
        userId = sharedPreferences_user.getString("userId", "");
        accessKey = sharedPreferences_user.getString("AccessKey", "");
        mNickName = sharedPreferences_user.getString("mNickName", "");
        IconUrl = sharedPreferences_user.getString("mHeadimageUrl", "");
        Level = sharedPreferences_user.getString("mLevel", "");
        Charm = sharedPreferences_user.getString("mCharm", "");

        glideUtis = new GlideUtis(this);
        glideUtis.glideCircle(IconUrl, livingroomUserImage, true);
        myNickname.setText(mNickName);
        tvCharmLivingShow.setText(Charm);


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    joinInWeb();//获取长连接
//                    Thread.sleep(100);
//                    GetChatRomCount();//读取聊天室人数
//                    GetChatRomList();//读取聊天室用户列表
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    try {
                        mProfile.setPublishUrl(liveUrl);
                        mMediaStreamingManager.setStreamingProfile(mProfile);
                        mMediaStreamingManager.startStreaming();
                        Toast.makeText(SWCameraStreamingActivity.this, "直播啦！", Toast.LENGTH_SHORT).show();
                        bottomBarLiving.setVisibility(View.VISIBLE);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    LinearLayoutManager layoutManager_chatmessage = new LinearLayoutManager(SWCameraStreamingActivity.this, LinearLayoutManager.VERTICAL, false);
                    layoutManager_chatmessage.setAutoMeasureEnabled(false);
                    chatRecyclerviewLiving.setLayoutManager(layoutManager_chatmessage);
                    livingroomChatListAdapter = new LivingroomChatListAdapter(SWCameraStreamingActivity.this, chatList);
                    livingroomChatListAdapter.bind(chatList);
                    chatRecyclerviewLiving.setAdapter(livingroomChatListAdapter);
                    break;
                case 1:
                    Toast.makeText(SWCameraStreamingActivity.this, "AK认证失败，请重新登录", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(100);
                                GetChatRomCount();//读取聊天室人数
                                GetChatRomList();//读取聊天室用户列表
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    break;
            }
        }
    };

    //获取socket长连接
    private void joinInWeb() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Protocol", "Logging");
            jsonObject.put("UserId", userId);
            jsonObject.put("AccessKey", accessKey);
            jsonObject.put("PwdModel", "3");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        WebSocketService.sendMsg(jsonObject.toString());
//        {"Protocol":"Logging","Result":"1","UsdrId":"1174","Msg":"认证成功"}
    }

    //读聊天室用户列表
    private void GetChatRomList() {
        JSONObject jsonObject_roomlist = new JSONObject();
        try {
            jsonObject_roomlist.put("Protocol", "ChatRom");
            jsonObject_roomlist.put("Cmd", "GetChatRomList");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtils.postString()
                .url(Api.ENCRYPT64)
                .content(jsonObject_roomlist.toString())
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d("失败的返回", e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        WebSocketService.sendMsg(response);
                    }
                });
    }

    //读聊天室在线人数
    private void GetChatRomCount() {
        JSONObject jsonObject_roomcount = new JSONObject();
        try {
            jsonObject_roomcount.put("Protocol", "ChatRom");
            jsonObject_roomcount.put("Cmd", "GetChatRomCount");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtils.postString()
                .url(Api.ENCRYPT64)
                .content(jsonObject_roomcount.toString())
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d("失败的返回", e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        WebSocketService.sendMsg(response);
                    }
                });
    }

    //发聊天室消息
    private void chatRoomMessage() {
        //            {"Protocol":"ChatRom","Cmd":"chat","ChatRomId":"10012","Cmd":"Msg","UserId":"1002","NickName":"","IconUrl":"","Level":"0","Msg":"消息内容"}
//        {"Protocol":"ChatRom","Cmd":"chat","NickName":"","IconUrl":"","Level":"0","Msg":"消息内容"}
        JSONObject object_chatroomMsg = new JSONObject();
        String mMsg = inputEditorLive.getText().toString().trim();
        if (Level.isEmpty()) {
            Level = "1";
        }
        try {
            object_chatroomMsg.put("Protocol", "ChatRom");
            object_chatroomMsg.put("Cmd", "chat");
//            object_chatroomMsg.put("ChatRomId", chatroomId);
//            object_chatroomMsg.put("UserId", "");
            object_chatroomMsg.put("NickName", mNickName);
            object_chatroomMsg.put("IconUrl", IconUrl);
            object_chatroomMsg.put("Level", Level);
            object_chatroomMsg.put("Msg", mMsg);
            Log.e("发消息内容", object_chatroomMsg.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtils.postString()
                .url(Api.ENCRYPT64)
                .content(object_chatroomMsg.toString())
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d("失败的返回", e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        WebSocketService.sendMsg(response);
                    }
                });

    }

    @Override
    protected String getPowerBarColors() {
        return AppConsts.POWER_BAR_WHITE;
    }

    //初始化直播参数
    public void initLive() {
        //水印
        // 100 为 alpha 值
        watermarksetting = new WatermarkSetting(this, R.drawable.shuiyin, WatermarkSetting.WATERMARK_LOCATION.SOUTH_EAST, WatermarkSetting.WATERMARK_SIZE.MEDIUM, 100);
        microphoneStreamingSetting = new MicrophoneStreamingSetting();
        microphoneStreamingSetting.setBluetoothSCOEnabled(true);//开启蓝牙麦克风支持
        afl = (AspectFrameLayout) findViewById(R.id.cameraPreview_afl);
        // Decide FULL screen or real size
        afl.setShowMode(AspectFrameLayout.SHOW_MODE.REAL);
        glSurfaceView = (GLSurfaceView) findViewById(R.id.cameraPreview_surfaceView);
        StreamingProfile.AudioProfile aProfile = new StreamingProfile.AudioProfile(44100, 66 * 1024);
        StreamingProfile.VideoProfile vProfile = new StreamingProfile.VideoProfile(20, 500 * 1024, 32);
        StreamingProfile.AVProfile avProfile = new StreamingProfile.AVProfile(vProfile, aProfile);
        mProfile = new StreamingProfile();
        mProfile
                .setVideoQuality(StreamingProfile.VIDEO_QUALITY_HIGH1)
                .setAudioQuality(StreamingProfile.AUDIO_QUALITY_MEDIUM2)
                .setEncodingSizeLevel(StreamingProfile.VIDEO_ENCODING_HEIGHT_480)
                .setEncoderRCMode(StreamingProfile.EncoderRCModes.QUALITY_PRIORITY)
//                    .setAdaptiveBitrateEnable(true)//自适应码率
                .setAVProfile(avProfile);
        cameraStreamingSetting = new CameraStreamingSetting();
        cameraStreamingSetting.setCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT)//摄像头前置
                .setContinuousFocusModeEnabled(true)//自动对焦
                .setFaceBeautySetting(new CameraStreamingSetting.FaceBeautySetting(1.0f, 1.0f, 0.8f))// FaceBeautySetting 中的参数依次为：beautyLevel，whiten，redden，
                .setVideoFilter(CameraStreamingSetting.VIDEO_FILTER_TYPE.VIDEO_FILTER_BEAUTY)//即磨皮程度、美白程度以及红润程度，取值范围为[0.0f, 1.0f]
                .setCameraPrvSizeLevel(CameraStreamingSetting.PREVIEW_SIZE_LEVEL.MEDIUM)
                .setCameraPrvSizeRatio(CameraStreamingSetting.PREVIEW_SIZE_RATIO.RATIO_16_9);
//                .setCameraSourceImproved(true);//只需要进行一次美颜的滤镜处理，就可以实现本地预览端和播放端的滤镜效果
        mMediaStreamingManager = new MediaStreamingManager(this, afl, glSurfaceView, AVCodecType.SW_VIDEO_WITH_SW_AUDIO_CODEC);  // soft codec
        mMediaStreamingManager.prepare(cameraStreamingSetting, microphoneStreamingSetting, watermarksetting, mProfile);// microphoneStreamingSetting, watermarksetting,
        mMediaStreamingManager.setStreamingStateListener(this);
    }

    @Override
    public void onStateChanged(StreamingState streamingState, Object o) {
        switch (streamingState) {
            case PREPARING:

                break;
            case READY:
                // start streaming when READY
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (mMediaStreamingManager != null) {
                            mMediaStreamingManager.startStreaming();
                            Log.d("startStreaming---------", "startStreaming-------");
                        }
                    }
                }).start();
                break;
            case CONNECTING:
                break;
            case STREAMING:
                // The av packet had been sent.
                break;
            case SHUTDOWN:
                // The streaming had been finished.
//                mMediaStreamingManager.stopStreaming();//停止推流
                break;
            case IOERROR:
                // Network connect error.
                Toast.makeText(this, "网络错误！", Toast.LENGTH_SHORT).show();
                break;
            case OPEN_CAMERA_FAIL:
                // Failed to open camera.
                Toast.makeText(this, "连接相机失败！", Toast.LENGTH_SHORT).show();
                break;
            case DISCONNECTED:
                // The socket is broken while streaming
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMediaStreamingManager != null) {
            mMediaStreamingManager.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // You must invoke pause here.
        if (mMediaStreamingManager != null) {
            mMediaStreamingManager.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//释放
        if (mMediaStreamingManager != null) {
            mMediaStreamingManager.destroy();
        }
    }

    @Override
    public void onBackPressed() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("确认取消直播？")
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mMediaStreamingManager != null) {
                            mMediaStreamingManager.stopStreaming();
                        }
                        WebSocketService.closeWebsocket(true);
                        Intent intent_livingEnd = new Intent(SWCameraStreamingActivity.this, LivingEndActivity.class);
                        startActivity(intent_livingEnd);
                        finish();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).show();
//        super.onBackPressed();
    }

    @OnClick({R.id.tv_livingroom_manage, R.id.living_close, R.id.charm_more, R.id.iv_chatbarrage, R.id.input_sendmsg, R.id.btn_start_live, R.id.live_cancel, R.id.iv_livingroom_comments, R.id.iv_livingroom_private_message, R.id.living_livingroom_share, R.id.iv_livingroom_pickup, R.id.iv_livingroom_music})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_livingroom_manage:
                Intent intent_livingManage = new Intent(SWCameraStreamingActivity.this, LivingroomManageActivity.class);
                startActivity(intent_livingManage);
                break;
            case R.id.living_close:
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setMessage("确认取消直播？")
                        .setCancelable(false)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent_livingEnd = new Intent(SWCameraStreamingActivity.this, LivingEndActivity.class);
                                startActivity(intent_livingEnd);
                                finish();
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
//                AlertDialog alert = builder.create();
                break;
            case R.id.charm_more:
                break;
            case R.id.iv_chatbarrage:
                break;
            case R.id.input_sendmsg:
                Toast.makeText(this, "点击了发信息", Toast.LENGTH_SHORT).show();
                if (inputEditorLive.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, "消息为空", Toast.LENGTH_SHORT).show();
                } else {
                    chatRoomMessage();
                    inputEditorLive.getText().clear();
                }
                break;
            case R.id.btn_start_live:
//              获取直播推流地址
                okhttputils();
                //主播创建聊天室
                createChatroom();
//              隐藏直播描述，进入直播间
                liveDescription.setVisibility(View.INVISIBLE);
                bottomBarLiving.setVisibility(View.VISIBLE);
                llLivingroomHeader.setVisibility(View.VISIBLE);
                llLivingroomHeadtwo.setVisibility(View.VISIBLE);
                rlChatRecyclerviewLiving.setVisibility(View.VISIBLE);
                Message msg_live_ok = new Message();
                msg_live_ok.what = 3;
                mHandler.sendMessage(msg_live_ok);
                break;
            case R.id.live_cancel:
                finish();
                break;
            case R.id.iv_livingroom_comments:
                bottomBarLiving.setVisibility(View.GONE);
                inputMessageLiveroom.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_livingroom_private_message:
                messagePop();
                popupWindow_living_message.showAtLocation(view, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.living_livingroom_share:
                sharePopw();
                popupWindow_living_share.showAtLocation(view, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.iv_livingroom_pickup:
                pickupPop();
                popupWindow_pickup.showAtLocation(this.getWindow().getDecorView(), Gravity.BOTTOM,
                        (int) this.getResources().getDimension(R.dimen.x172), 0);
                ivLivingroomPickup.setVisibility(View.GONE);
                break;
            case R.id.iv_livingroom_music:
                break;
        }
    }


    //收到的聊天室消息
    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEvent_chatroom(Event_chatroom msg) {
        String mmsg = msg.getMsg();
        String isMsg = mmsg.substring(0, 1);
        Log.e("聊天消息", isMsg);
        Log.d("聊天消息", mmsg);
        if (!isMsg.equals("{")) {
            OkHttpUtils.postString()
                    .url(Api.UNENCRYPT64)
                    .content(mmsg)
                    .mediaType(MediaType.parse("application/json; charset=utf-8"))
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            Log.d("失败的返回", e.getMessage());
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            Log.d("成功的返回", response);
//                            {"Protocol":"ChatRom","Result":"1","Msg":"加入聊天室成功","Cmd":"AddChatRom","ChatRomId":"1174"}
                            Log.e("播放端mNickName", mNickName);
                            try {
                                JSONObject object_joinchat = new JSONObject(response);
                                String cmd = object_joinchat.getString("Cmd");
                                if (mNickName.isEmpty()) {
                                    mNickName = "游客";
                                }
                                //获取聊天室人数
                                if (cmd.equals("GetChatRomCount")) {
                                    int ChatRomCount = object_joinchat.getInt("ChatRomCount");
                                    onlinePeople.setText("观众：" + ChatRomCount);
                                } else if (cmd.equals("GetChatRomList")) {//获取聊天室用户ID
                                    String Result = object_joinchat.getString("Result");
                                    if (Result.equals("1")) {
                                        JSONArray jsonArray_list = new JSONArray(object_joinchat.getString("Data"));
                                        for (int i = 0; i < jsonArray_list.length(); i++) {
                                            String allUser = jsonArray_list.get(i).toString();//获取聊天室用户ID
                                            //下面显示用户头像列表操作

                                        }
                                    }
                                } else if (cmd.equals("AddChatRom")) {
                                    String joinchat = object_joinchat.getString("Msg");
                                    //提示某某加入聊天室
                                    livingroomChatListBean_chatmsg = new LivingroomChatListBean("", "", mNickName, "加入聊天室");
                                    chatList.add(livingroomChatListBean_chatmsg);
                                }
                                LinearLayoutManager layoutManager_chatmessage = new LinearLayoutManager(SWCameraStreamingActivity.this, LinearLayoutManager.VERTICAL, true);
                                layoutManager_chatmessage.setAutoMeasureEnabled(false);
                                chatRecyclerviewLiving.setLayoutManager(layoutManager_chatmessage);
                                livingroomChatSysAdapter = new LivingroomChatSysAdapter(SWCameraStreamingActivity.this, chatList);
                                livingroomChatSysAdapter.bind(chatList);
                                chatRecyclerviewLiving.setAdapter(livingroomChatSysAdapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } else {
            try {
//            {"Protocol":"ChatRom","Cmd":"chat","ChatRomId":"10012","Cmd":"Msg","UserId":"1002","NickName":"","IconUrl":"","Level":"0","Msg":"消息内容"}
                JSONObject object_chatMsg = new JSONObject(mmsg);
                //处理接收到的聊天信息
                String cmd = object_chatMsg.getString("Cmd");
                //获取聊天室人数
//                if (cmd.equals("GetChatRomCount")) {
////                    {"Protocol":"ChatRom","Cmd":"GetChatRomCount","Result":"1","ChatRomId":"10012","ChatRomCount":2}
//                    int ChatRomCount = object_chatMsg.getInt("ChatRomCount");
//                    onlinePeople.setText("观众：" + ChatRomCount);
//                } else if (cmd.equals("GetChatRomList")) {//获取聊天室用户ID
////                    {"Protocol":"ChatRom","Cmd":"GetChatRomList","Result":"1","ChatRomId":"10012","Data":["1001","1001"]}
////                    Data:
////                    "10014",//UserId
////                            "guest"
//                    String Result = object_chatMsg.getString("Result");
//                    if (Result.equals("1")) {
//                        JSONArray jsonArray_list = new JSONArray(object_chatMsg.getString("Data"));
//                        for (int i = 0; i < jsonArray_list.length(); i++) {
//                            String allUser = jsonArray_list.get(i).toString();//获取聊天室用户ID
//                            //下面显示用户头像列表操作
//
//                        }
//                    }
//                }
                if (cmd.equals("chat")) {
                    String NickName = object_chatMsg.getString("NickName");
                    String IconUrl = object_chatMsg.getString("IconUrl");
                    String Level = object_chatMsg.getString("Level");
                    String Msg_chat = object_chatMsg.getString("Msg");
                    livingroomChatListBean_chatmsg = new LivingroomChatListBean(IconUrl, Level, NickName, Msg_chat);
                    chatList.add(livingroomChatListBean_chatmsg);
                    Message msgg = new Message();
                    msgg.what = 2;
                    mHandler.sendMessage(msgg);
                } else if (cmd.equals("sys")) {
                    String Msg_sys = object_chatMsg.getString("Msg");
                    livingroomChatListBean_chatmsg = new LivingroomChatListBean("", "", "系统消息", Msg_sys);
                    chatList.add(livingroomChatListBean_chatmsg);
                    LinearLayoutManager layoutManager_chatmessage = new LinearLayoutManager(SWCameraStreamingActivity.this, LinearLayoutManager.VERTICAL, true);
                    layoutManager_chatmessage.setAutoMeasureEnabled(false);
                    chatRecyclerviewLiving.setLayoutManager(layoutManager_chatmessage);
                    livingroomChatSysAdapter = new LivingroomChatSysAdapter(SWCameraStreamingActivity.this, chatList);
                    livingroomChatSysAdapter.bind(chatList);
                    chatRecyclerviewLiving.setAdapter(livingroomChatSysAdapter);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void messagePop() {
        final View livingmessageView = LayoutInflater.from(this).inflate(R.layout.message_livingroom, null);
        popupWindow_living_message = new PopupWindow(livingmessageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        ListView listview = (ListView) livingmessageView.findViewById(R.id.listview_message_livingroom);
        String[] data = {"One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine"};
        listview.setAdapter(new ArrayAdapter<String>(livingmessageView.getContext(), android.R.layout.simple_list_item_1, data));
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    Toast.makeText(livingmessageView.getContext(), "哈哈", Toast.LENGTH_SHORT).show();
//                    chatPop();
//                    popupWindow_living_chat.showAtLocation(view, Gravity.BOTTOM, 0, 0);
                }
            }
        });

        popupWindow_living_message.setTouchable(true);
        popupWindow_living_message.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub
                return false;
            }
        });
        popupWindow_living_message.setBackgroundDrawable(new ColorDrawable(this.getResources().getColor(R.color.transparent)));
    }

    private void chatPop() {
        final View livingshareView = LayoutInflater.from(this).inflate(R.layout.chat_livingroom, null);
        popupWindow_living_chat = new PopupWindow(livingshareView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow_living_chat.setTouchable(true);
        popupWindow_living_chat.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub
                return false;
            }
        });
        popupWindow_living_chat.setBackgroundDrawable(new ColorDrawable(this.getResources().getColor(R.color.transparent)));
    }

    private void sharePopw() {
        final View livingshareView = LayoutInflater.from(this).inflate(R.layout.share_livingroom, null);
        popupWindow_living_share = new PopupWindow(livingshareView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        Button button_cancel = (Button) livingshareView.findViewById(R.id.btn_livingshare_cancel);
        button_cancel.setOnClickListener(shareListen);
        popupWindow_living_share.setTouchable(true);
        popupWindow_living_share.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub
                return false;
            }
        });
        popupWindow_living_share.setBackgroundDrawable(new ColorDrawable(this.getResources().getColor(R.color.transparent)));
    }

    private View.OnClickListener shareListen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_livingshare_cancel:
                    popupWindow_living_share.dismiss();
                    break;
            }
        }
    };

    private void pickupPop() {
        final View contentView = LayoutInflater.from(this).inflate(R.layout.livingroom_popw_pickup, null);
        popupWindow_pickup = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        ImageView imageView_pick = (ImageView) contentView.findViewById(R.id.livingroom_pop_pickup);
        imageView_pick.setOnClickListener(pickupPop);
        popupWindow_pickup.setTouchable(true);
        popupWindow_pickup.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub
                ivLivingroomPickup.setVisibility(View.VISIBLE);
                return false;
            }
        });
//        popupWindow_pickup.setBackgroundDrawable(getResources().getDrawable(R.drawable.livingroom_edge));
        popupWindow_pickup.setBackgroundDrawable(new ColorDrawable(this.getResources().getColor(R.color.transparent)));
    }

    private View.OnClickListener pickupPop = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.livingroom_pop_pickup:
                    popupWindow_pickup.dismiss();
                    ivLivingroomPickup.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    //获取直播地址流
    private void okhttputils() {
        SharedPreferences sharedPreferences_user = getSharedPreferences("isLogin", Context.MODE_PRIVATE);
        String userId = sharedPreferences_user.getString("userId", "");
        Log.e("userIduserId", userId);
        String title_live = etLiveTitle.getText().toString().trim();
        JSONObject paramsObject = new JSONObject();
        try {
            paramsObject.put("Protocol", "Live");
            paramsObject.put("UserId", userId);
            paramsObject.put("Cmd", "1");
            paramsObject.put("Title", title_live);
            paramsObject.put("Tag", "Tag");
            Log.e("cao---=====", paramsObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtils
                .postString()
                .url(Api.ENCRYPT64)
                .content(paramsObject.toString())
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        OkHttpUtils.postString()
                                .url(Api.LIVEPUT)
                                .content(response)
                                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                                .build()
                                .execute(new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {

                                    }

                                    @Override
                                    public void onResponse(String response, int id) {
                                        OkHttpUtils.postString()
                                                .url(Api.UNENCRYPT64)
                                                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                                                .content(response)
                                                .build()
                                                .execute(new Callback() {
                                                    @Override
                                                    public Object parseNetworkResponse(Response response, int id) throws Exception {
                                                        JSONObject object = new JSONObject(response.body().string());
                                                        liveUrl = object.getString("RTMPPublishURL");
                                                        Log.d("liveUrl-----", liveUrl);
                                                        Message msg = new Message();
                                                        msg.what = 0;
                                                        mHandler.sendMessage(msg);
                                                        return null;
                                                    }

                                                    @Override
                                                    public void onError(Call call, Exception e, int id) {
                                                        Log.d("onError", e.getMessage());
                                                    }

                                                    @Override
                                                    public void onResponse(Object response, int id) {
                                                    }
                                                });
                                    }
                                });
                    }
                });

    }

    //创建聊天室
    private void createChatroom() {
        JSONObject jsonObject_quit = new JSONObject();
        try {
            jsonObject_quit.put("Protocol", "ChatRom");
            jsonObject_quit.put("Cmd", "CreateChatRom");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtils.postString()
                .url(Api.ENCRYPT64)
                .content(jsonObject_quit.toString())
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d("失败的返回", e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        WebSocketService.sendMsg(response);
                    }
                });
    }


    //退出聊天室
    private void exitChatroom() {
        JSONObject jsonObject_quit = new JSONObject();
        try {
            jsonObject_quit.put("Protocol", "ChatRom");
            jsonObject_quit.put("UserId", "ExitChatRom");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtils.postString()
                .url(Api.ENCRYPT64)
                .content(jsonObject_quit.toString())
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d("失败的返回", e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        WebSocketService.sendMsg(response);
                    }
                });
    }

    //管理员设置用户禁言
    private void GagChat() {

    }
}
