package com.biaoke.bklive.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.biaoke.bklive.R;
import com.biaoke.bklive.utils.GlideUtis;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LivingEndActivity extends AppCompatActivity {

    @BindView(R.id.iv_header_live_end)
    ImageView ivHeaderLiveEnd;
    @BindView(R.id.tv_live_end_nickname)
    TextView tvLiveEndNickname;
    @BindView(R.id.tv_live_end)
    TextView tvLiveEnd;
    @BindView(R.id.tv_live_end_secret)
    TextView tvLiveEndSecret;
    @BindView(R.id.tv_live_end_reminder)
    TextView tvLiveEndReminder;
    @BindView(R.id.tv_livetitle_end)
    TextView tvLivetitleEnd;
    @BindView(R.id.tv_live_end_time)
    TextView tvLiveEndTime;
    @BindView(R.id.tv_live_end_looknum)
    TextView tvLiveEndLooknum;
    @BindView(R.id.tv_live_end_charm)
    TextView tvLiveEndCharm;
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
    @BindView(R.id.ll_live_share)
    LinearLayout llLiveShare;
    @BindView(R.id.btn_backto_foundfragment)
    Button btnBacktoFoundfragment;
    @BindView(R.id.iv_liveend_deletevideo)
    ImageView ivLiveendDeletevideo;
    @BindView(R.id.tv_liveend_deletevideo)
    TextView tvLiveendDeletevideo;
    private String mHeadimageUrl;
    private GlideUtis glideUtis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_living_end);
        ButterKnife.bind(this);
        SharedPreferences sharedPreferences_user = getSharedPreferences("isLogin", Context.MODE_PRIVATE);
        mHeadimageUrl = sharedPreferences_user.getString("mHeadimageUrl", "");
        glideUtis = new GlideUtis(this);
        glideUtis.glideCircle(mHeadimageUrl, ivHeaderLiveEnd, true);
    }

    @OnClick({R.id.tv_live_end_secret, R.id.iv_liveshare_sina, R.id.iv_liveshare_wechat, R.id.iv_liveshare_wechatfriend, R.id.iv_liveshare_qq, R.id.iv_liveshare_qqq, R.id.ll_live_share, R.id.btn_backto_foundfragment, R.id.iv_liveend_deletevideo, R.id.tv_liveend_deletevideo})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_live_end_secret:
                break;
            case R.id.iv_liveshare_sina:
                break;
            case R.id.iv_liveshare_wechat:
                break;
            case R.id.iv_liveshare_wechatfriend:
                break;
            case R.id.iv_liveshare_qq:
                break;
            case R.id.iv_liveshare_qqq:
                break;
            case R.id.ll_live_share:
                break;
            case R.id.btn_backto_foundfragment:
                finish();
                break;
            case R.id.iv_liveend_deletevideo:
            case R.id.tv_liveend_deletevideo:

                break;
        }
    }
}
