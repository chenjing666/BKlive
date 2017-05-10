package com.biaoke.bklive.user.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.biaoke.bklive.R;
import com.biaoke.bklive.base.BaseActivity;
import com.biaoke.bklive.message.AppConsts;
import com.biaoke.bklive.utils.GlideUtis;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LevelActivity extends BaseActivity {

    @BindView(R.id.back_level)
    ImageView backLevel;
    @BindView(R.id.iv_user_head_level)
    ImageView ivUserHeadLevel;
    @BindView(R.id.tv_user_level)
    TextView tvUserLevel;
    @BindView(R.id.level_level1)
    TextView levelLevel1;
    @BindView(R.id.level_level2)
    TextView levelLevel2;
    private String useId;
    private String accessKey;
    private String mHeadUrl;
    private String mLevel;
    private GlideUtis glideUtis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
        SharedPreferences sharedPreferences = getSharedPreferences("isLogin", Context.MODE_PRIVATE);
        useId = sharedPreferences.getString("userId", "");
        accessKey = sharedPreferences.getString("AccessKey", "");
        mHeadUrl = sharedPreferences.getString("mHeadimageUrl", "");
        mLevel = sharedPreferences.getString("mLevel", "");
        ButterKnife.bind(this);
        glideUtis = new GlideUtis(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        glideUtis.glideCircle(mHeadUrl, ivUserHeadLevel, true);
        if (!mLevel.isEmpty()) {
            tvUserLevel.setText("lv." + mLevel);
            levelLevel1.setText("升lv." + mLevel);
        } else {
            tvUserLevel.setText("lv.0");
            levelLevel1.setText("lv." + mLevel);
        }
        levelLevel2.setText("lv." + (mLevel + 1));
    }

    @Override
    protected String getPowerBarColors() {
        return AppConsts.POWER_BAR_BACKGROUND;
    }

    @OnClick(R.id.back_level)
    public void onClick() {
        finish();
    }
}
