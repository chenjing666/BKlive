package com.biaoke.bklive.user.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.biaoke.bklive.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class IdentificationActivity extends AppCompatActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.replace_fragment)
    RelativeLayout replaceFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identification);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.back)
    public void onClick() {
        finish();
    }
}
