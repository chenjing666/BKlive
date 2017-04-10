package com.biaoke.bklive.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.biaoke.bklive.R;
import com.biaoke.bklive.base.BaseActivity;
import com.biaoke.bklive.message.Api;
import com.biaoke.bklive.message.AppConsts;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Response;

public class PrepareLiveActivity extends BaseActivity {

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
    private String liveUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepare_live);
        ButterKnife.bind(this);
    }


    private Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    Intent intent_liveprepare = new Intent(PrepareLiveActivity.this, SWCameraStreamingActivity.class);
                    intent_liveprepare.putExtra("liveUrl", liveUrl);
                    startActivity(intent_liveprepare);
                    finish();
                    break;
            }
        }
    };

    @Override
    protected String getPowerBarColors() {
        return AppConsts.POWER_BAR_BACKGROUND;
    }

    @OnClick({R.id.btn_start_live, R.id.live_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start_live:
                okhttputils();
//                Message msg = new Message();
//                msg.what = 0;
//                mHandler.sendMessage(msg);
                break;
            case R.id.live_cancel:
                finish();
                break;
        }
    }

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
            paramsObject.put("Tag", "tag");
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
                                                        String RongYunToken=object.getString("RongCloudToken");//获取融云token
                                                        JSONObject jsonObject_token=new JSONObject(RongYunToken);
                                                        String token=jsonObject_token.getString("token");
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
