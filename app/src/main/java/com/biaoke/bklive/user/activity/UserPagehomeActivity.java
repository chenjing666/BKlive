package com.biaoke.bklive.user.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.biaoke.bklive.R;
import com.biaoke.bklive.base.BaseActivity;
import com.biaoke.bklive.message.AppConsts;
import com.biaoke.bklive.user.fragment.AnchorHomepageFragment;
import com.biaoke.bklive.user.fragment.AnchorLivedataFragment;
import com.biaoke.bklive.user.fragment.AnchorVideodataFragment;
import com.pkmmte.view.CircularImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
    private Fragment anchorFragmentHomepage, anchorFragmentVideo, anchorFragmentLive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_pagehome);
        ButterKnife.bind(this);
        intiFragmentHome();
        anchorHomepage.setSelected(true);
    }

    @Override
    protected String getPowerBarColors() {
        return AppConsts.POWER_BAR_BACKGROUND;
    }

    @OnClick({R.id.back_userhome, R.id.anchor_homepage, R.id.anchor_video, R.id.anchor_live})
    public void onClick(View view) {
        switch (view.getId()) {
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
}
