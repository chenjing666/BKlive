package com.biaoke.bklive.activity;

import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.biaoke.bklive.R;
import com.biaoke.bklive.base.BaseActivity;
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

import java.net.URISyntaxException;

import butterknife.ButterKnife;

public class SWCameraStreamingActivity extends BaseActivity implements StreamingStateChangedListener {
    private MediaStreamingManager mMediaStreamingManager;
    private String liveUrl = null;
    private StreamingProfile mProfile;
    private AspectFrameLayout afl;
    private GLSurfaceView glSurfaceView;
    private CameraStreamingSetting cameraStreamingSetting;
    private MicrophoneStreamingSetting microphoneStreamingSetting;
    private WatermarkSetting watermarksetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swcamera_streaming);
        ButterKnife.bind(this);
        liveUrl = getIntent().getStringExtra("liveUrl");
        Log.e("--------liveUrl--------", liveUrl + "");
        initLive();

    }

    //
    @Override
    protected String getPowerBarColors() {
        return AppConsts.POWER_BAR_WHITE;
    }

    //
    public void initLive() {
        //水印
        // 100 为 alpha 值
        watermarksetting = new WatermarkSetting(this, R.drawable.logo_72, WatermarkSetting.WATERMARK_LOCATION.SOUTH_WEST, WatermarkSetting.WATERMARK_SIZE.MEDIUM, 100);
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
        try {
            mProfile.setPublishUrl(liveUrl)
                    .setVideoQuality(StreamingProfile.VIDEO_QUALITY_HIGH1)
                    .setAudioQuality(StreamingProfile.AUDIO_QUALITY_MEDIUM2)
                    .setEncodingSizeLevel(StreamingProfile.VIDEO_ENCODING_HEIGHT_480)
                    .setEncoderRCMode(StreamingProfile.EncoderRCModes.QUALITY_PRIORITY)
//                    .setAdaptiveBitrateEnable(true)//自适应码率
                    .setAVProfile(avProfile);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        cameraStreamingSetting = new CameraStreamingSetting();
        cameraStreamingSetting.setCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT)//摄像头前置
                .setContinuousFocusModeEnabled(true)//自动对焦
                .setFaceBeautySetting(new CameraStreamingSetting.FaceBeautySetting(1.0f, 1.0f, 0.8f))// FaceBeautySetting 中的参数依次为：beautyLevel，whiten，redden，
                .setVideoFilter(CameraStreamingSetting.VIDEO_FILTER_TYPE.VIDEO_FILTER_BEAUTY)//即磨皮程度、美白程度以及红润程度，取值范围为[0.0f, 1.0f]
                .setCameraPrvSizeLevel(CameraStreamingSetting.PREVIEW_SIZE_LEVEL.MEDIUM)
                .setCameraPrvSizeRatio(CameraStreamingSetting.PREVIEW_SIZE_RATIO.RATIO_16_9);
//                .setCameraSourceImproved(true);//只需要进行一次美颜的滤镜处理，就可以实现本地预览端和播放端的滤镜效果
        mMediaStreamingManager = new MediaStreamingManager(this, afl, glSurfaceView, AVCodecType.SW_VIDEO_WITH_SW_AUDIO_CODEC);  // soft codec
        mMediaStreamingManager.prepare(cameraStreamingSetting, mProfile);// microphoneStreamingSetting, watermarksetting,
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

//    @OnClick(R.id.btn_start_live)
//    public void onClick() {
//    获取直播推流地址
//        okhttputils();
//    隐藏直播描述，进入直播间
//        liveDescription.setVisibility(View.INVISIBLE);
//        Toast.makeText(this, "进入直播", Toast.LENGTH_SHORT).show();
//    }

    //获取直播地址流
//    private void okhttputils() {
//        SharedPreferences sharedPreferences_user = getSharedPreferences("isLogin", Context.MODE_PRIVATE);
//        String userId = sharedPreferences_user.getString("userId", "");
//        Log.e("userIduserId", userId);
//        String title_live = etLiveTitle.getText().toString().trim();
//        JSONObject paramsObject = new JSONObject();
//        try {
//            paramsObject.put("Protocol", "Live");
//            paramsObject.put("UserId", userId);
//            paramsObject.put("Cmd", "1");
//            paramsObject.put("Title", title_live);
//            paramsObject.put("Tag", "Tag");
//            Log.e("cao---=====", paramsObject.toString());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        OkHttpUtils
//                .postString()
//                .url(Api.ENCRYPT64)
//                .content(paramsObject.toString())
//                .mediaType(MediaType.parse("application/json; charset=utf-8"))
//                .build()
//                .execute(new StringCallback() {
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//
//                    }
//
//                    @Override
//                    public void onResponse(String response, int id) {
//                        OkHttpUtils.postString()
//                                .url(Api.LIVEPUT)
//                                .content(response)
//                                .mediaType(MediaType.parse("application/json; charset=utf-8"))
//                                .build()
//                                .execute(new StringCallback() {
//                                    @Override
//                                    public void onError(Call call, Exception e, int id) {
//
//                                    }
//
//                                    @Override
//                                    public void onResponse(String response, int id) {
//                                        OkHttpUtils.postString()
//                                                .url(Api.UNENCRYPT64)
//                                                .mediaType(MediaType.parse("application/json; charset=utf-8"))
//                                                .content(response)
//                                                .build()
//                                                .execute(new Callback() {
//                                                    @Override
//                                                    public Object parseNetworkResponse(Response response, int id) throws Exception {
//                                                        JSONObject object = new JSONObject(response.body().string());
//                                                        liveUrl = object.getString("RTMPPublishURL");
//                                                        Log.d("liveUrl-----", liveUrl);
////                                                        Message msg = new Message();
////                                                        msg.what = 0;
////                                                        mHandler.sendMessage(msg);
//                                                        return null;
//                                                    }
//
//                                                    @Override
//                                                    public void onError(Call call, Exception e, int id) {
//                                                        Log.d("onError", e.getMessage());
//                                                    }
//
//                                                    @Override
//                                                    public void onResponse(Object response, int id) {
//                                                    }
//                                                });
//                                    }
//                                });
//                    }
//                });
//
//    }
}
