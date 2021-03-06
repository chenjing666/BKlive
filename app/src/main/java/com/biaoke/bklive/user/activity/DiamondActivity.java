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

public class DiamondActivity extends AppCompatActivity {

    @BindView(R.id.back_diamond)
    ImageView backDiamond;
    @BindView(R.id.diamond_mine)
    TextView diamondMine;
    @BindView(R.id.charge_diamond1)
    TextView chargeDiamond1;
    @BindView(R.id.charge_diamond6)
    TextView chargeDiamond6;
    @BindView(R.id.charge_diamond30)
    TextView chargeDiamond30;
    @BindView(R.id.charge_diamond98)
    TextView chargeDiamond98;
    @BindView(R.id.charge_diamond128)
    TextView chargeDiamond128;
    @BindView(R.id.charge_diamond328)
    TextView chargeDiamond328;
    @BindView(R.id.charge_diamond648)
    TextView chargeDiamond648;
    private String userId;
    private String mDiamond;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_diamond);
        ButterKnife.bind(this);
        SharedPreferences sharedPreferences_user = getSharedPreferences("isLogin", MODE_PRIVATE);
        userId = sharedPreferences_user.getString("userId", "");
        mDiamond = sharedPreferences_user.getString("mDiamond", "");
        diamondMine.setText(mDiamond);
    }

    @OnClick({R.id.back_diamond, R.id.charge_diamond1, R.id.charge_diamond6, R.id.charge_diamond30, R.id.charge_diamond98, R.id.charge_diamond128, R.id.charge_diamond328, R.id.charge_diamond648})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_diamond:
                finish();
                break;
            //支付功能暂时不加入
//            case R.id.charge_diamond1:
//                Intent intentone = new Intent(this, PayActivity.class);
//                intentone.putExtra("money", chargeDiamond1.getText());
//                startActivity(intentone);
//                break;
//            case R.id.charge_diamond6:
//                Intent intenttwo = new Intent(this, PayActivity.class);
//                intenttwo.putExtra("money", chargeDiamond6.getText());
//                startActivity(intenttwo);
//                break;
//            case R.id.charge_diamond30:
//                Intent intentthree = new Intent(this, PayActivity.class);
//                intentthree.putExtra("money", chargeDiamond30.getText());
//                startActivity(intentthree);
//                break;
//            case R.id.charge_diamond98:
//                Intent intentfour = new Intent(this, PayActivity.class);
//                intentfour.putExtra("money", chargeDiamond98.getText());
//                startActivity(intentfour);
//                break;
//            case R.id.charge_diamond128:
//                Intent intentfive = new Intent(this, PayActivity.class);
//                intentfive.putExtra("money", chargeDiamond128.getText());
//                startActivity(intentfive);
//                break;
//            case R.id.charge_diamond328:
//                Intent intentsix = new Intent(this, PayActivity.class);
//                intentsix.putExtra("money", chargeDiamond328.getText());
//                startActivity(intentsix);
//                break;
//            case R.id.charge_diamond648:
//                Intent intentseven = new Intent(this, PayActivity.class);
//                intentseven.putExtra("money", chargeDiamond648.getText());
//                startActivity(intentseven);
//                break;
        }
    }
}
