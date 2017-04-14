package com.biaoke.bklive.user.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.biaoke.bklive.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class IncomeActivity extends AppCompatActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.et_pick_money)
    EditText etPickMoney;
    @BindView(R.id.btn_pick_money)
    Button btnPickMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //使布局延伸到状态栏
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_income);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.back, R.id.btn_pick_money})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.btn_pick_money:
                break;
        }
    }
}
