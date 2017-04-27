package com.biaoke.bklive.user.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.biaoke.bklive.R;
import com.biaoke.bklive.base.BaseActivity;
import com.biaoke.bklive.message.Api;
import com.biaoke.bklive.message.AppConsts;
import com.biaoke.bklive.user.fragment.IdentificationFragment;
import com.biaoke.bklive.user.fragment.IdentificationedFragment;
import com.biaoke.bklive.user.fragment.IdentificationingFragment;
import com.biaoke.bklive.user.fragment.IdentificationunFragment;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.MediaType;

public class IdentificationActivity extends BaseActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.replace_fragment)
    RelativeLayout replaceFragment;
    private String userId;
    private String result;
    private String msg;
    private Fragment identificationFragment, identificationedFragment, identificationingFragment, identificationunFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identification);
        ButterKnife.bind(this);
        SharedPreferences sharedPreferences_user = getSharedPreferences("isLogin", MODE_PRIVATE);
        userId = sharedPreferences_user.getString("userId", "");

        getIdentification();

    }

    private Handler mhandler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    if (result.equals("0")) {
                        intiIdentificationunFragment();
                    } else if (result.equals("1")) {
                        intiIdentificationedFragment();
                    } else if (result.equals("2")) {
                        intiIdentificationingFragment();
                    } else if (result.equals("3")) {
                        intiIdentificationFragment();
                    }
                    break;

            }
        }
    };

    //    {"Protocol":"IsLive","Cmd":"1","UserId":"1174"}
    private void getIdentification() {
        JSONObject object_identification = new JSONObject();
        try {
            object_identification.put("Protocol", "IsLive");
            object_identification.put("Cmd", "1");
            object_identification.put("UserId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtils.postString()
                .url(Api.ENCRYPT64)
                .content(object_identification.toString())
                .mediaType(MediaType.parse("application/json charset=utf-8"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        OkHttpUtils.postString()
                                .url(Api.LIVE_CAMERA)
                                .content(response)
                                .mediaType(MediaType.parse("application/json charset=utf-8"))
                                .build()
                                .execute(new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {

                                    }

                                    @Override
                                    public void onResponse(String response, int id) {
                                        OkHttpUtils.postString()
                                                .url(Api.UNENCRYPT64)
                                                .content(response)
                                                .mediaType(MediaType.parse("application/json charset=utf-8"))
                                                .build()
                                                .execute(new StringCallback() {
                                                    @Override
                                                    public void onError(Call call, Exception e, int id) {

                                                    }

                                                    @Override
                                                    public void onResponse(String response, int id) {
                                                        try {
                                                            JSONObject jsonObject = new JSONObject(response);
                                                            result = jsonObject.getString("Result");
                                                            msg = jsonObject.getString("Msg");
                                                            Message message = new Message();
                                                            message.what = 0;
                                                            mhandler.sendMessage(message);
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

    @Override
    protected String getPowerBarColors() {
        return AppConsts.POWER_BAR_BACKGROUND;
    }

    @OnClick(R.id.back)
    public void onClick() {
        finish();
    }

    //填写资料申请页面
    private void intiIdentificationFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (identificationFragment == null) {
            identificationFragment = new IdentificationFragment();
            transaction.replace(R.id.replace_fragment, identificationFragment);
        }
        hideFragment(transaction);
        transaction.show(identificationFragment);
        transaction.commit();
    }

    //审核中
    private void intiIdentificationingFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (identificationingFragment == null) {
            identificationingFragment = new IdentificationingFragment();
            transaction.replace(R.id.replace_fragment, identificationingFragment);
        }
        hideFragment(transaction);
        transaction.show(identificationingFragment);
        transaction.commit();
    }

    //已经通过
    private void intiIdentificationedFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (identificationedFragment == null) {
            identificationedFragment = new IdentificationedFragment();
            transaction.replace(R.id.replace_fragment, identificationedFragment);
        }
        hideFragment(transaction);
        transaction.show(identificationedFragment);
        transaction.commit();
    }

    //还未申请页面
    private void intiIdentificationunFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (identificationunFragment == null) {
            identificationunFragment = new IdentificationunFragment();
            transaction.replace(R.id.replace_fragment, identificationunFragment);
        }
        hideFragment(transaction);
        transaction.show(identificationunFragment);
        transaction.commit();
    }


    //隐藏所有的fragment
    private void hideFragment(FragmentTransaction transaction) {
        if (identificationFragment != null) {
            transaction.hide(identificationFragment);
        }
        if (identificationedFragment != null) {
            transaction.hide(identificationedFragment);
        }
        if (identificationingFragment != null) {
            transaction.hide(identificationingFragment);
        }
        if (identificationunFragment != null) {
            transaction.hide(identificationunFragment);
        }
    }
}
