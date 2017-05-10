package com.biaoke.bklive.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.biaoke.bklive.R;
import com.biaoke.bklive.utils.GlideUtis;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

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
    private String mNickname;
    private UMImage thumb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_living_end);
        ButterKnife.bind(this);
        SharedPreferences sharedPreferences_user = getSharedPreferences("isLogin", Context.MODE_PRIVATE);
        mHeadimageUrl = sharedPreferences_user.getString("mHeadimageUrl", "");
        mNickname = sharedPreferences_user.getString("mNickName", "");
        glideUtis = new GlideUtis(this);
        glideUtis.glideCircle(mHeadimageUrl, ivHeaderLiveEnd, true);
        tvLiveEndNickname.setText(mNickname);
    }

    @OnClick({R.id.tv_live_end_secret, R.id.iv_liveshare_sina, R.id.iv_liveshare_wechat, R.id.iv_liveshare_wechatfriend, R.id.iv_liveshare_qq, R.id.iv_liveshare_qqq, R.id.ll_live_share, R.id.btn_backto_foundfragment, R.id.iv_liveend_deletevideo, R.id.tv_liveend_deletevideo})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_live_end_secret:
                break;
            case R.id.iv_liveshare_sina:
                thumb = new UMImage(LivingEndActivity.this, R.drawable.logo_72);
                UMWeb web_sina = new UMWeb("http://www.bk5977.com/");
                web_sina.setTitle("骠客直播");//标题
                web_sina.setThumb(thumb);  //缩略图
                web_sina.setDescription("你丑你先睡，我美我直播");//描述
                new ShareAction(LivingEndActivity.this).setPlatform(SHARE_MEDIA.SINA)
                        .withMedia(web_sina)
                        .setCallback(umShareListener)
                        .share();
                break;
            case R.id.iv_liveshare_wechat:
                thumb = new UMImage(LivingEndActivity.this, R.drawable.logo_72);
                UMWeb web_wechat = new UMWeb("http://www.bk5977.com/");
                web_wechat.setTitle("骠客直播");//标题
                web_wechat.setThumb(thumb);  //缩略图
                web_wechat.setDescription("你丑你先睡，我美我直播");//描述
                //分享到微信
                new ShareAction(LivingEndActivity.this).setPlatform(SHARE_MEDIA.WEIXIN)
                        .withMedia(web_wechat)
                        .setCallback(umShareListener)
                        .share();
                break;
            case R.id.iv_liveshare_wechatfriend:
                thumb = new UMImage(LivingEndActivity.this, R.drawable.logo_72);
                UMWeb web_wc = new UMWeb("http://www.bk5977.com/");
                web_wc.setTitle("骠客直播");//标题
                web_wc.setThumb(thumb);  //缩略图
                web_wc.setDescription("你丑你先睡，我美我直播");//描述
                new ShareAction(LivingEndActivity.this).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                        .withMedia(web_wc)
                        .setCallback(umShareListener)
                        .share();
                break;
            case R.id.iv_liveshare_qq:
                thumb = new UMImage(LivingEndActivity.this, R.drawable.logo_72);
                UMWeb web_qq = new UMWeb("http://www.bk5977.com/");
                web_qq.setTitle("骠客直播");//标题
                web_qq.setThumb(thumb);  //缩略图
                web_qq.setDescription("你丑你先睡，我美我直播");//描述
                new ShareAction(LivingEndActivity.this).setPlatform(SHARE_MEDIA.QQ)
                        .withMedia(web_qq)
                        .setCallback(umShareListener)
                        .share();
                break;
            case R.id.iv_liveshare_qqq:
                thumb = new UMImage(LivingEndActivity.this, R.drawable.logo_72);
                UMWeb web_qqq = new UMWeb("http://www.bk5977.com/");
                web_qqq.setTitle("骠客直播");//标题
                web_qqq.setThumb(thumb);  //缩略图
                web_qqq.setDescription("你丑你先睡，我美我直播");//描述
                new ShareAction(LivingEndActivity.this).setPlatform(SHARE_MEDIA.QZONE)
                        .withMedia(web_qqq)
                        .setCallback(umShareListener)
                        .share();
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

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            //分享开始的回调
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            Log.d("plat", "platform" + platform);

            Toast.makeText(LivingEndActivity.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(LivingEndActivity.this, platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
            if (t != null) {
                Log.d("throw", "throw:" + t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(LivingEndActivity.this, platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);

    }
}
