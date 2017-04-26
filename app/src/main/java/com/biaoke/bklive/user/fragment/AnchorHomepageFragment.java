package com.biaoke.bklive.user.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.biaoke.bklive.R;
import com.biaoke.bklive.activity.ExpressionActivity;
import com.biaoke.bklive.message.Api;
import com.biaoke.bklive.user.activity.ContributionActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.MediaType;

/**
 * Created by hasee on 2017/4/14.
 */

public class AnchorHomepageFragment extends Fragment {
    @BindView(R.id.bk_contribution)
    RelativeLayout bkContribution;
    @BindView(R.id.bk_expression)
    RelativeLayout bkExpression;
    @BindView(R.id.tv_age)
    TextView tvAge;
    @BindView(R.id.tv_zhankong)
    TextView tvZhankong;
    @BindView(R.id.tv_emotion)
    TextView tvEmotion;
    @BindView(R.id.tv_zhankong2)
    TextView tvZhankong2;
    @BindView(R.id.tv_hometown)
    TextView tvHometown;
    @BindView(R.id.tv_zhankong3)
    TextView tvZhankong3;
    @BindView(R.id.tv_work)
    TextView tvWork;
    @BindView(R.id.tv_zhankong4)
    TextView tvZhankong4;
    @BindView(R.id.tv_bkid)
    TextView tvBkid;
    @BindView(R.id.tv_zhankong5)
    TextView tvZhankong5;
    @BindView(R.id.tv_signture)
    TextView tvSignture;
    @BindView(R.id.tv_zhankong6)
    TextView tvZhankong6;
    Unbinder unbinder;
    private String chatroomId;
    //信息
    private String yNickName;
    private String yLevel;
    private String yExperience;
    private String yCharm;
    private String yDiamond;
    private String yLiveNum;
    private String yVideoNum;
    private String yHeadimageUrl;
    private String ySex;
    private String yAge;
    private String yEmotion;
    private String yHometown;
    private String yWork;
    private String yFollow;
    private String yFans;
    private String ySignture;
    private String ySendgift;
    //信息

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_anchor_homepage, container, false);
        unbinder = ButterKnife.bind(this, view);
        SharedPreferences sharedPreferences_chatroomId = getActivity().getSharedPreferences("isLogin", Context.MODE_PRIVATE);
        chatroomId = sharedPreferences_chatroomId.getString("ChatroomId", "");
        JSONObject jsonObject_yuser = new JSONObject();
        try {
            jsonObject_yuser.put("Protocol", "UserInfo");
            jsonObject_yuser.put("Cmd", "GetAll");
            jsonObject_yuser.put("UserId", chatroomId);
            Log.e("发送主播信息", jsonObject_yuser.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        UserInfoHttp(Api.ENCRYPT64, jsonObject_yuser.toString());
        return view;
    }

    private Handler mhandler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    tvAge.setText(yAge);
                    tvEmotion.setText(yEmotion);
                    tvHometown.setText(yHometown);
                    tvWork.setText(yWork);
                    tvBkid.setText(chatroomId);
                    tvSignture.setText(ySignture);
                    break;
                case 1:
                    break;
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.bk_contribution, R.id.bk_expression})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bk_contribution:
                Intent intent_contribution = new Intent(getActivity(), ContributionActivity.class);
                startActivity(intent_contribution);
                break;
            case R.id.bk_expression:
                Intent intent_expression = new Intent(getActivity(), ExpressionActivity.class);
                startActivity(intent_expression);
                break;
        }
    }

    //获取用户信息
    public void UserInfoHttp(String url, String path) {
        OkHttpUtils
                .postString()
                .url(url)
                .content(path)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d("失败的返回", e.getMessage());
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
                                        Log.d("失败的返回", e.getMessage());
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
                                                        Log.d("失败的返回", e.getMessage());
                                                    }

                                                    @Override
                                                    public void onResponse(String response, int id) {
                                                        Log.d("成功===成功的返回", response);
                                                        try {
                                                            JSONObject jsonobject = new JSONObject(response);
                                                            yNickName = jsonobject.getString("NickName");
                                                            yLevel = jsonobject.getString("Level");
                                                            yExperience = jsonobject.getString("经验");
                                                            yCharm = jsonobject.getString("魅力");
                                                            yDiamond = jsonobject.getString("钻石");
                                                            yLiveNum = jsonobject.getString("直播");
                                                            yVideoNum = jsonobject.getString("点播");
                                                            yHeadimageUrl = jsonobject.getString("IconUrl");
                                                            ySex = jsonobject.getString("性别");
                                                            yAge = jsonobject.getString("生日");
                                                            yEmotion = jsonobject.getString("情感");
                                                            yHometown = jsonobject.getString("家乡");
                                                            yWork = jsonobject.getString("职业");
                                                            yFollow = jsonobject.getString("关注" + "");
                                                            yFans = jsonobject.getString("粉丝" + "");
                                                            ySignture = jsonobject.getString("签名" + "");
                                                            ySendgift = jsonobject.getString("消费");
                                                            Message message_userinfo = new Message();
                                                            message_userinfo.what = 0;
                                                            mhandler.sendMessage(message_userinfo);
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
