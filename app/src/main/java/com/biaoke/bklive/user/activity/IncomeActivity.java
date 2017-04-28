package com.biaoke.bklive.user.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    @BindView(R.id.charm_income)
    TextView charmIncome;
    @BindView(R.id.income_money)
    TextView incomeMoney;
    private String userId;
    private String mCharm;
    private double money;
    private double enputMoney;

    //25魅力为1元
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //使布局延伸到状态栏
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_income);
        ButterKnife.bind(this);
        SharedPreferences sharedPreferences_user = getSharedPreferences("isLogin", MODE_PRIVATE);
        userId = sharedPreferences_user.getString("userId", "");
        mCharm = sharedPreferences_user.getString("mCharm", "");
        charmIncome.setText(mCharm);
        if (mCharm.isEmpty()) {
            money = 0;
        } else {
            money = Double.parseDouble(mCharm) / 25;
        }
        incomeMoney.setText(money + "");
    }

    @OnClick({R.id.back, R.id.btn_pick_money})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.btn_pick_money:
                String putMoney = etPickMoney.getText().toString().trim();
                if (putMoney.isEmpty()) {
                    enputMoney = 0.00;
                } else {
                    enputMoney = Double.parseDouble(putMoney);
                }
                if (money == 0) {
                    Toast.makeText(IncomeActivity.this, "没有可提现金额", Toast.LENGTH_SHORT).show();
                }
                if (enputMoney > money && money > 0) {
                    Toast.makeText(IncomeActivity.this, "输入金额有误", Toast.LENGTH_SHORT).show();
                }
                if (enputMoney <= money && money > 0) {
                    Toast.makeText(IncomeActivity.this, "提现成功", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
