package com.biaoke.bklive.user.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.biaoke.bklive.R;
import com.biaoke.bklive.base.BaseActivity;
import com.biaoke.bklive.message.Api;
import com.biaoke.bklive.message.AppConsts;
import com.biaoke.bklive.user.eventbus.Event_signture;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import okhttp3.Call;
import okhttp3.MediaType;


public class SigntureActivity extends BaseActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.et_signture)
    EditText etSignture;
    @BindView(R.id.btn_save)
    Button btnSave;
    private String userId;
    private String accessKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signture);
        ButterKnife.bind(this);
        SharedPreferences sharedPreferences_user = getSharedPreferences("isLogin", Context.MODE_PRIVATE);//首先获取用户ID，直播要取
        userId = sharedPreferences_user.getString("userId", "");
        accessKey = sharedPreferences_user.getString("AccessKey", "");

    }

    @Override
    protected String getPowerBarColors() {
        return AppConsts.POWER_BAR_BACKGROUND;
    }

    @OnClick({R.id.back, R.id.btn_save})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.btn_save:
                //发送消息
                String singture = etSignture.getText().toString();
                EventBus.getDefault().post(new Event_signture(singture));
                sendSignture();
                finish();
                break;
        }
    }

    private void sendSignture() {
        JSONObject jsonObject_sendNickname = new JSONObject();
        try {
            jsonObject_sendNickname.put("Protocol", "UserInfo");
            jsonObject_sendNickname.put("Cmd", "Set");
            jsonObject_sendNickname.put("UserId", userId);
            jsonObject_sendNickname.put("AccessKey", accessKey);
            jsonObject_sendNickname.put("Name", "签名");
            jsonObject_sendNickname.put("Data", etSignture.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("修改昵称", jsonObject_sendNickname.toString());
        OkHttpUtils.postString()
                .url(Api.ENCRYPT64)
                .content(jsonObject_sendNickname.toString())
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("失败的返回", e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        OkHttpUtils.postString()
                                .url(Api.USERINFO_USER)
                                .content(response)
                                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                                .build()
                                .execute(new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {
                                        Log.e("失败的返回", e.getMessage());
                                    }

                                    @Override
                                    public void onResponse(String response, int id) {
                                        OkHttpUtils.postString()
                                                .url(Api.UNENCRYPT64)
                                                .content(response)
                                                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                                                .build()
                                                .execute(new StringCallback() {
                                                    @Override
                                                    public void onError(Call call, Exception e, int id) {
                                                        Log.e("失败的返回", e.getMessage());
                                                    }

                                                    @Override
                                                    public void onResponse(String response, int id) {
                                                        try {
                                                            JSONObject object_nickname = new JSONObject(response);
                                                            String msg = object_nickname.getString("Msg");
//                                                            Toast.makeText(NicknameActivity.this, msg, Toast.LENGTH_SHORT).show();
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });
                                    }
                                });
                    }
                });
    }
}
