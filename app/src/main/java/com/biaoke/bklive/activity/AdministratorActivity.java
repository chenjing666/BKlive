package com.biaoke.bklive.activity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.biaoke.bklive.R;
import com.biaoke.bklive.base.BaseActivity;
import com.biaoke.bklive.message.AppConsts;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AdministratorActivity extends BaseActivity {

    @BindView(R.id.back_manage)
    ImageView backManage;
    @BindView(R.id.recyclerview_administrator)
    RecyclerView recyclerviewAdministrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrator);
        ButterKnife.bind(this);
    }

    @Override
    protected String getPowerBarColors() {
        return AppConsts.POWER_BAR_BACKGROUND;
    }

    @OnClick(R.id.back_manage)
    public void onClick() {
        finish();
    }
}
