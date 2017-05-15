package com.biaoke.bklive.user.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class SetActivity extends BaseActivity {

    @BindView(R.id.btn_loginout)
    Button btnLoginout;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.set_phone_bind)
    TextView setPhoneBind;
    @BindView(R.id.set_password_change)
    TextView setPasswordChange;
    @BindView(R.id.set_accept_primsg)
    TextView setAcceptPrimsg;
    @BindView(R.id.set_live_remind)
    TextView setLiveRemind;
    @BindView(R.id.set_defriend)
    TextView setDefriend;
    @BindView(R.id.set_about_us)
    TextView setAboutUs;
    @BindView(R.id.set_chatbarrage_primsg)
    ImageView setChatbarragePrimsg;
    @BindView(R.id.set_barrage_primsg_close)
    ImageView setBarragePrimsgClose;
    @BindView(R.id.set_barrage_primsg_open)
    ImageView setBarragePrimsgOpen;
    @BindView(R.id.set_cache)
    TextView setCache;
    @BindView(R.id.set_chatbarrage_cache)
    ImageView setChatbarrageCache;
    @BindView(R.id.set_barrage_cache_close)
    ImageView setBarrageCacheClose;
    @BindView(R.id.set_barrage_cache_open)
    ImageView setBarrageCacheOpen;
    @BindView(R.id.set_autoplay)
    TextView setAutoplay;
    @BindView(R.id.set_chatbarrage_autoplay)
    ImageView setChatbarrageAutoplay;
    @BindView(R.id.set_barrage_autoplay_close)
    ImageView setBarrageAutoplayClose;
    @BindView(R.id.set_barrage_autoplay_open)
    ImageView setBarrageAutoplayOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        ButterKnife.bind(this);
    }

    @Override
    protected String getPowerBarColors() {
        return AppConsts.POWER_BAR_BACKGROUND;
    }

    @OnClick({R.id.btn_loginout, R.id.back, R.id.set_phone_bind, R.id.set_password_change, R.id.set_accept_primsg, R.id.set_live_remind,
            R.id.set_defriend, R.id.set_about_us, R.id.set_cache, R.id.set_autoplay})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_phone_bind:
                break;
            case R.id.set_password_change:
                break;
            case R.id.set_accept_primsg:
                break;
            case R.id.set_live_remind:
                break;
            case R.id.set_defriend:
                break;
            case R.id.set_about_us:
                break;
            case R.id.set_cache:
                break;
            case R.id.set_autoplay:
                break;
            case R.id.btn_loginout:
                SharedPreferences sharedPreferences = getSharedPreferences("isLogin", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                finish();
                break;
            case R.id.back:
                finish();
                break;
        }
    }
}
