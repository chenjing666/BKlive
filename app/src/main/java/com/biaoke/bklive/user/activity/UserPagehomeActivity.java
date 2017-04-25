package com.biaoke.bklive.user.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.biaoke.bklive.R;
import com.biaoke.bklive.base.BaseActivity;
import com.biaoke.bklive.message.Api;
import com.biaoke.bklive.message.AppConsts;
import com.biaoke.bklive.user.fragment.AnchorHomepageFragment;
import com.biaoke.bklive.user.fragment.AnchorLivedataFragment;
import com.biaoke.bklive.user.fragment.AnchorVideodataFragment;
import com.biaoke.bklive.utils.GlideUtis;
import com.pkmmte.view.CircularImageView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.MediaType;

public class UserPagehomeActivity extends BaseActivity {

    @BindView(R.id.back_userhome)
    ImageView backUserhome;
    @BindView(R.id.tv_diamond_send)
    TextView tvDiamondSend;
    @BindView(R.id.diamond_send)
    TextView diamondSend;
    @BindView(R.id.iv_user_head_homepage)
    CircularImageView ivUserHeadHomepage;
    @BindView(R.id.tv_userName)
    TextView tvUserName;
    @BindView(R.id.iv_sex_user)
    ImageView ivSexUser;
    @BindView(R.id.tv_user_signture)
    TextView tvUserSignture;
    @BindView(R.id.anchor_homepage)
    TextView anchorHomepage;
    @BindView(R.id.anchor_video)
    RelativeLayout anchorVideo;
    @BindView(R.id.anchor_live)
    TextView anchorLive;
    @BindView(R.id.rl_to_replace)
    RelativeLayout rlToReplace;
    @BindView(R.id.tv_anchor_video)
    TextView tvAnchorVideo;
    @BindView(R.id.ll_anchor_data)
    LinearLayout llAnchorData;
    @BindView(R.id.anchor_homepage_follow)
    TextView anchorHomepageFollow;
    @BindView(R.id.anchor_privatemessage)
    TextView anchorPrivatemessage;
    @BindView(R.id.anchor_defriend)
    TextView anchorDefriend;
    @BindView(R.id.homgpage_level)
    TextView homgpageLevel;
    private Fragment anchorFragmentHomepage, anchorFragmentVideo, anchorFragmentLive;
    private String ChatroomId;
    private GlideUtis glideUtis;
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
    private String userId;
    private String accessKey;
    private String msg_addFollow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_pagehome);
        ButterKnife.bind(this);
        intiFragmentHome();
        glideUtis = new GlideUtis(this);
        anchorHomepage.setSelected(true);
        ChatroomId = getIntent().getStringExtra("ChatroomId");
        SharedPreferences sharedPreferences_user = getSharedPreferences("isLogin", Context.MODE_PRIVATE);//首先获取用户ID，直播要取
        userId = sharedPreferences_user.getString("userId", "");
        accessKey = sharedPreferences_user.getString("AccessKey", "");
        JSONObject jsonObject_yuser = new JSONObject();
        try {
            jsonObject_yuser.put("Protocol", "UserInfo");
            jsonObject_yuser.put("Cmd", "GetAll");
            jsonObject_yuser.put("UserId", ChatroomId);
            Log.e("发送主播信息", jsonObject_yuser.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        UserInfoHttp(Api.ENCRYPT64, jsonObject_yuser.toString());
    }

    private Handler mhandler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    glideUtis.glideCircle(yHeadimageUrl, ivUserHeadHomepage, true);
                    diamondSend.setText(yDiamond);
                    homgpageLevel.setText(yLevel);
                    tvUserName.setText(yNickName);
                    if (ySex.equals("男")) {
                        ivSexUser.setImageResource(R.drawable.man);
                    } else {
                        ivSexUser.setImageResource(R.drawable.female);
                    }
                    tvUserSignture.setText(ySignture);
                    break;
                case 1:
                    Toast.makeText(UserPagehomeActivity.this, msg_addFollow, Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    break;
            }
        }
    };

    @Override
    protected String getPowerBarColors() {
        return AppConsts.POWER_BAR_BACKGROUND;
    }

    @OnClick({R.id.anchor_homepage_follow, R.id.anchor_privatemessage, R.id.anchor_defriend, R.id.back_userhome, R.id.anchor_homepage, R.id.anchor_video, R.id.anchor_live})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.anchor_homepage_follow:
                addFollow();
                break;
            case R.id.anchor_privatemessage:
                break;
            case R.id.anchor_defriend:
                break;
            case R.id.back_userhome:
                finish();
                break;
            case R.id.anchor_homepage:
                anchorHomepage.setSelected(true);
                tvAnchorVideo.setSelected(false);
                anchorLive.setSelected(false);
                intiFragmentHome();
                break;
            case R.id.anchor_video:
                anchorHomepage.setSelected(false);
                tvAnchorVideo.setSelected(true);
                anchorLive.setSelected(false);
                intiFragmentVideo();
                break;
            case R.id.anchor_live:
                anchorHomepage.setSelected(false);
                tvAnchorVideo.setSelected(false);
                anchorLive.setSelected(true);
                intiFragmentLive();
                break;
        }
    }

    private void intiFragmentHome() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (anchorFragmentHomepage == null) {
            anchorFragmentHomepage = new AnchorHomepageFragment();
            transaction.add(R.id.rl_to_replace, anchorFragmentHomepage);
        }
        hideFragment(transaction);
        transaction.show(anchorFragmentHomepage);
        transaction.commit();
    }

    private void intiFragmentVideo() {
        FragmentTransaction transaction_video = getSupportFragmentManager().beginTransaction();
        if (anchorFragmentVideo == null) {
            anchorFragmentVideo = new AnchorVideodataFragment();
            transaction_video.add(R.id.rl_to_replace, anchorFragmentVideo);
        }
        hideFragment(transaction_video);
        transaction_video.show(anchorFragmentVideo);
        transaction_video.commit();
    }

    private void intiFragmentLive() {
        FragmentTransaction transaction_live = getSupportFragmentManager().beginTransaction();
        if (anchorFragmentLive == null) {
            anchorFragmentLive = new AnchorLivedataFragment();
            transaction_live.add(R.id.rl_to_replace, anchorFragmentLive);
        }
        hideFragment(transaction_live);
        transaction_live.show(anchorFragmentLive);
        transaction_live.commit();
    }

    //隐藏所有的fragment
    private void hideFragment(FragmentTransaction transaction) {
        if (anchorFragmentHomepage != null) {
            transaction.hide(anchorFragmentHomepage);
        }
        if (anchorFragmentLive != null) {
            transaction.hide(anchorFragmentLive);
        }
        if (anchorFragmentVideo != null) {
            transaction.hide(anchorFragmentVideo);
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

    private void addFollow() {
        JSONObject jsonobject_addfollow = new JSONObject();
        try {
            jsonobject_addfollow.put("Protocol", "Fans");
            jsonobject_addfollow.put("Cmd", "Add");
            jsonobject_addfollow.put("MastId", ChatroomId);
            jsonobject_addfollow.put("SlaveId", userId);
            jsonobject_addfollow.put("AccessKey", accessKey);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtils.postString()
                .url(Api.ENCRYPT64)
                .content(jsonobject_addfollow.toString())
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        OkHttpUtils.postString()
                                .url(Api.FOLLOW)
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
                                                .content(response)
                                                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                                                .build()
                                                .execute(new StringCallback() {
                                                    @Override
                                                    public void onError(Call call, Exception e, int id) {

                                                    }

                                                    @Override
                                                    public void onResponse(String response, int id) {
                                                        try {
                                                            JSONObject object_follow = new JSONObject(response);
                                                            String result_follow = object_follow.getString("Result");
                                                            msg_addFollow = object_follow.getString("Msg");
                                                            if (result_follow.equals("1")) {
                                                                Message message_isfollow = new Message();
                                                                message_isfollow.what = 1;
                                                                mhandler.sendMessage(message_isfollow);
                                                            }

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
