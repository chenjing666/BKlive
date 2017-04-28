package com.biaoke.bklive.user.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.biaoke.bklive.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContributionActivity extends AppCompatActivity {


    @BindView(R.id.back_contribution)
    ImageView backContribution;
    @BindView(R.id.tv_contribution_charm)
    TextView tvContributionCharm;
    @BindView(R.id.tv_list_day)
    TextView tvListDay;
    @BindView(R.id.tv_list_totall)
    TextView tvListTotall;
    @BindView(R.id.contribution_first)
    ImageView contributionFirst;
    @BindView(R.id.tv_contribution_nickname)
    TextView tvContributionNickname;
    @BindView(R.id.iv_contribution_sex)
    ImageView ivContributionSex;
    @BindView(R.id.contribution_nickname)
    TextView contributionNickname;
    @BindView(R.id.contribution_sex)
    ImageView contributionSex;
    @BindView(R.id.contribution_charm)
    TextView contributionCharm;
    private String userId;
    private String mCharm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_contribution);
        ButterKnife.bind(this);
        SharedPreferences sharedPreferences_user = getSharedPreferences("isLogin", MODE_PRIVATE);
        userId = sharedPreferences_user.getString("userId", "");
        mCharm = sharedPreferences_user.getString("mCharm", "");
        tvContributionCharm.setText(mCharm + "魅力值");
    }


    @OnClick({R.id.back_contribution, R.id.tv_list_day, R.id.tv_list_totall})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_contribution:
                finish();
                break;
            case R.id.tv_list_day:
                break;
            case R.id.tv_list_totall:
                break;
        }
    }
}
