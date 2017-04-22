package com.biaoke.bklive.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.biaoke.bklive.R;
import com.biaoke.bklive.base.BaseActivity;
import com.biaoke.bklive.message.AppConsts;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LivingroomManageActivity extends BaseActivity {

    @BindView(R.id.back_manage)
    ImageView backManage;
    @BindView(R.id.recyclerview_liveManage)
    RecyclerView recyclerviewLiveManage;
    @BindView(R.id.btn_outof_Livingroom)
    Button btnOutofLivingroom;
    @BindView(R.id.tv_list_administrator)
    TextView tvListAdministrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livingroom_manage);
        ButterKnife.bind(this);
    }

    @Override
    protected String getPowerBarColors() {
        return AppConsts.POWER_BAR_BACKGROUND;
    }

    @OnClick({R.id.tv_list_administrator, R.id.back_manage, R.id.btn_outof_Livingroom})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_list_administrator:
                Intent intent_livingManage = new Intent(LivingroomManageActivity.this, AdministratorActivity.class);
                startActivity(intent_livingManage);
                break;
            case R.id.back_manage:
                finish();
                break;
            case R.id.btn_outof_Livingroom:

                break;
        }
    }

}
