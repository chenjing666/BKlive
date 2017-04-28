package com.biaoke.bklive.user.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.biaoke.bklive.R;
import com.biaoke.bklive.base.BaseActivity;
import com.biaoke.bklive.message.AppConsts;
import com.biaoke.bklive.user.fragment.AnchorLivedataFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyLiveActivity extends BaseActivity {

    @BindView(R.id.back_mylive)
    ImageView backMylive;
    @BindView(R.id.replace_mylive)
    RelativeLayout replaceMylive;
    private AnchorLivedataFragment anchorFragmentLive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_live);
        ButterKnife.bind(this);
        intiFragmentLive();
    }

    @Override
    protected String getPowerBarColors() {
        return AppConsts.POWER_BAR_BACKGROUND;
    }

    private void intiFragmentLive() {
        FragmentTransaction transaction_live = getSupportFragmentManager().beginTransaction();
        if (anchorFragmentLive == null) {
            anchorFragmentLive = new AnchorLivedataFragment();
            transaction_live.add(R.id.replace_mylive, anchorFragmentLive);
        }
        transaction_live.show(anchorFragmentLive);
        transaction_live.commit();
    }

    @OnClick(R.id.back_mylive)
    public void onClick() {
        finish();
    }
}
