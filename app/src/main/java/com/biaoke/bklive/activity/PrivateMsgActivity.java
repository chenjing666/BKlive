package com.biaoke.bklive.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.biaoke.bklive.R;
import com.biaoke.bklive.base.BaseActivity;
import com.biaoke.bklive.message.AppConsts;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PrivateMsgActivity extends BaseActivity {

    @BindView(R.id.back_message)
    ImageView backMessage;
    @BindView(R.id.msg_user)
    TextView msgUser;
    @BindView(R.id.recyclerview_msg)
    RecyclerView recyclerviewMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_msg);
        ButterKnife.bind(this);
        String fromUserId = getIntent().getStringExtra("fromUserId");
        String iconUrl = getIntent().getStringExtra("iconUrl");
        SharedPreferences sharedPreferences_user = getSharedPreferences("isLogin", MODE_PRIVATE);
        String mHeadImageUrl = sharedPreferences_user.getString("mHeadimageUrl", "");
    }

    @Override
    protected String getPowerBarColors() {
        return AppConsts.POWER_BAR_BACKGROUND;
    }

    @OnClick(R.id.back_message)
    public void onClick() {
        finish();
    }
}
