package com.biaoke.bklive.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.biaoke.bklive.base.BaseActivity;
import com.biaoke.bklive.message.Api;
import com.biaoke.bklive.message.AppConsts;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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

    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //使布局延伸到状态栏
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_swcamera_streaming);
        ButterKnife.bind(this);
//        TextView textView = (TextView) findViewById(R.id.input_sendmsg);
//        textView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(SWCameraStreamingActivity.this, "点击了发信息", Toast.LENGTH_SHORT).show();
//            }
//        });
        liveDescription.setVisibility(View.VISIBLE);
        bottomBarLiving.setVisibility(View.GONE);
        initLive();

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
            }
        }
    };

    //
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
        if (mMediaStreamingManager != null) {
            mMediaStreamingManager.destroy();
        }
    }

    @Override
    public void onBackPressed() {
        if (mMediaStreamingManager != null) {
            mMediaStreamingManager.stopStreaming();
        }
        super.onBackPressed();
    }

    @OnClick({R.id.iv_chatbarrage, R.id.input_sendmsg, R.id.btn_start_live, R.id.live_cancel, R.id.iv_livingroom_comments, R.id.iv_livingroom_private_message, R.id.living_livingroom_share, R.id.iv_livingroom_pickup, R.id.iv_livingroom_music})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_chatbarrage:
                break;
            case R.id.input_sendmsg:
                Toast.makeText(this, "点击了发信息", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_start_live:
//              获取直播推流地址
                okhttputils();
//              隐藏直播描述，进入直播间
                liveDescription.setVisibility(View.INVISIBLE);
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
//                                                        "code": 200,
//                                                                "userId": "1001",
//                                                                "token": "IrBvMOtboxb/CtHNf3dhvS9Cqig9AKecy6C72GS0ol6EO+vM8atpLok2WaW+YaQnTOoJEc6tHbJ7uQS1OithRQ=="
                                                        String RongYunToken = object.getString("RongCloudToken");//获取融云token
                                                        JSONObject jsonObject_token = new JSONObject(RongYunToken);
                                                        String token = jsonObject_token.getString("token");
                                                        Log.d("liveUrl-----", liveUrl);
                                                        Log.d("token-----", token);
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

}
