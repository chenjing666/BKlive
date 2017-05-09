package com.biaoke.bklive.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.biaoke.bklive.R;
import com.biaoke.bklive.base.BaseActivity;
import com.biaoke.bklive.message.AppConsts;
import com.biaoke.bklive.paylkl.PaymentRequest;
import com.biaoke.bklive.paylkl.PaymentTask;
import com.swwx.paymax.PayResult;
import com.swwx.paymax.PaymaxCallback;
import com.swwx.paymax.PaymaxSDK;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PayActivity extends BaseActivity implements PaymaxCallback {

    @BindView(R.id.et_money)
    EditText etMoney;
    @BindView(R.id.ibWechat)
    ImageButton ibWechat;
    @BindView(R.id.ibAlipay)
    ImageButton ibAlipay;
    @BindView(R.id.ibLKL)
    ImageButton ibLKL;
    @BindView(R.id.bt_pay_ok)
    Button btPayOk;
    private String money;
    protected long time_expire;
    protected double amount = 0.0;
    private int channel = PaymaxSDK.CHANNEL_ALIPAY;

    /**
     * 支付宝支付渠道
     */
    private static final String CHANNEL_ALIPAY = "alipay_app";

    /**
     * 微信支付渠道
     */
    private static final String CHANNEL_WECHAT = "wechat_app";

    /**
     * 微信支付渠道
     */
    protected static final String CHANNEL_LKL = "lakala_app";
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        ButterKnife.bind(this);
        money = getIntent().getStringExtra("money");
        SharedPreferences sharedPreferences_user = getSharedPreferences("isLogin", MODE_PRIVATE);
        userId = sharedPreferences_user.getString("userId", "");
        getMoney();
        selectChannal();
        ;
    }

    private void selectChannal() {
        //默认选择支付宝支付
        channel = PaymaxSDK.CHANNEL_ALIPAY;
        ibAlipay.setBackgroundResource(R.drawable.selected);
        ibWechat.setBackgroundResource(R.drawable.unselected);
        ibLKL.setBackgroundResource(R.drawable.unselected);
    }

    private void getMoney() {
        if (!money.isEmpty()) {
            etMoney.setText(money);
        } else {
            etMoney.setText(98 + "");
        }
    }

    @Override
    protected String getPowerBarColors() {
        return AppConsts.POWER_BAR_GRAYLIGHT2;
    }

    @OnClick({R.id.ibWechat, R.id.ibAlipay, R.id.ibLKL, R.id.bt_pay_ok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibWechat:
                channel = PaymaxSDK.CHANNEL_WX;
                ibAlipay.setBackgroundResource(R.drawable.unselected);
                ibWechat.setBackgroundResource(R.drawable.selected);
                ibLKL.setBackgroundResource(R.drawable.unselected);
                break;
            case R.id.ibAlipay:
                channel = PaymaxSDK.CHANNEL_ALIPAY;
                ibAlipay.setBackgroundResource(R.drawable.selected);
                ibWechat.setBackgroundResource(R.drawable.unselected);
                ibLKL.setBackgroundResource(R.drawable.unselected);
                break;
            case R.id.ibLKL:
                channel = PaymaxSDK.CHANNEL_LKL;
                ibAlipay.setBackgroundResource(R.drawable.unselected);
                ibWechat.setBackgroundResource(R.drawable.unselected);
                ibLKL.setBackgroundResource(R.drawable.selected);
                break;
            case R.id.bt_pay_ok:
                String amountText = etMoney.getText().toString();
                time_expire = Long.parseLong("3600") * 1000 + System.currentTimeMillis();
                Log.d("FaceRecoSDK", "time_expire=" + time_expire);

                if (checkInputValid(amountText)) {
                    amount = parseInputTxt(amountText);
                    amount /= 100;

                    switch (channel) {
                        case PaymaxSDK.CHANNEL_WX:
                            new PaymentTask(PayActivity.this, PayActivity.this).execute(new PaymentRequest(CHANNEL_WECHAT, amount, "测试商品007", "测试商品Body", userId, time_expire));
                            break;

                        case PaymaxSDK.CHANNEL_ALIPAY:
                            new PaymentTask(PayActivity.this, PayActivity.this).execute(new PaymentRequest(CHANNEL_ALIPAY, amount, "测试商品007", "测试商品Body", userId, time_expire));
                            break;

                        case PaymaxSDK.CHANNEL_LKL: {
//                            new FaceTask().execute(new FaceRequest("123", "123", userId));
                        }
                        break;
                    }

                }
                break;
        }
    }

    private boolean checkInputValid(String amountText) {
        return !(null == amountText || amountText.length() == 0);
    }

    private double parseInputTxt(String amountText) {
        String replaceable = String.format("[%s, \\s.]", NumberFormat.getCurrencyInstance(Locale.CHINA).getCurrency().getSymbol(Locale.CHINA));
        String cleanString = amountText.replaceAll(replaceable, "");
        return Double.valueOf(new BigDecimal(cleanString).toString());
    }

    @Override
    public void onPayFinished(PayResult payResult) {
        String msg = "Unknow";
        switch (payResult.getCode()) {
            case PaymaxSDK.CODE_SUCCESS:
                msg = "Complete, Success!.";
                break;

            case PaymaxSDK.CODE_ERROR_CHARGE_JSON:
                msg = "Json error.";
                break;

            case PaymaxSDK.CODE_FAIL_CANCEL:
                msg = "cancel pay.";
                break;

            case PaymaxSDK.CODE_ERROR_CHARGE_PARAMETER:
                msg = "appid error.";
                break;

            case PaymaxSDK.CODE_ERROR_WX_NOT_INSTALL:
                msg = "wx not install.";
                break;

            case PaymaxSDK.CODE_ERROR_WX_NOT_SUPPORT_PAY:
                msg = "ex not support pay.";
                break;

            case PaymaxSDK.CODE_ERROR_WX_UNKNOW:
                msg = "wechat failed.";
                break;

            case PaymaxSDK.CODE_ERROR_ALI_DEAL:
                msg = "alipay dealing.";
                break;

            case PaymaxSDK.CODE_ERROR_ALI_CONNECT:
                msg = "alipay network connection failed.";
                break;

            case PaymaxSDK.CODE_ERROR_CHANNEL:
                msg = "channel error.";
                break;

            case PaymaxSDK.CODE_ERROR_LAK_USER_NO_NULL:
                msg = "lklpay user no is null.";
                break;

        }
        Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG)
                .setAction("Close", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();
    }
}
