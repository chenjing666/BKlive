package com.biaoke.bklive;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.biaoke.bklive.base.BaseActivity;
import com.biaoke.bklive.common.CountDownProgressView;
import com.biaoke.bklive.message.AppConsts;
import com.biaoke.bklive.user.mylocation.Constants;
import com.biaoke.bklive.user.mylocation.LMLocationListener;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LogoActivity extends BaseActivity {

    @BindView(R.id.logo_welcome)
    ImageView logoWelcome;
    @BindView(R.id.logo_welcome2)
    ImageView logoWelcome2;
    @BindView(R.id.countdownProgressView)
    CountDownProgressView countdownProgressView;

    //地理信息定位
    private LocationManager locationManager;
    private LMLocationListener listener[] = {new LMLocationListener(), new LMLocationListener()};
    private Timer timer;
    private int count = 0;
    private String wd;
    private String jd;
    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
        ButterKnife.bind(this);
        random = new Random();
        int what = random.nextInt(3);
        Log.e("随机数",what+"");
        if (what == 0) {
            logoWelcome.setImageResource(R.drawable.logo_welcome_480);
        } else if (what == 1) {
            logoWelcome.setImageResource(R.drawable.logo_welcome_shop1);
        } else {
            logoWelcome.setImageResource(R.drawable.logo_welcome_shop2);
        }

        getLocation();
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.logo_alpha);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
//                logoWelcome.setVisibility(View.GONE);
//                logoWelcome2.setVisibility(View.VISIBLE);
                countdownProgressView.setVisibility(View.VISIBLE);
                CountProgress();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                countdownProgressView.stop();
                Intent intent = new Intent(LogoActivity.this, MainActivity.class);
                intent.putExtra("wd", wd);
                intent.putExtra("jd", jd);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        logoWelcome.setAnimation(animation);


    }


    private void CountProgress() {
        countdownProgressView.setTimeMillis(3000);
        countdownProgressView.start();
        countdownProgressView.setProgressListener(new CountDownProgressView.OnProgressListener() {
            @Override
            public void onProgress(int progress) {
//                if (progress == 99) {
//                    Intent intent = new Intent(LogoActivity.this, MainActivity.class);
//                    startActivity(intent);
//                    finish();
//                }
            }
        });
        countdownProgressView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countdownProgressView.stop();
                Intent intent = new Intent(LogoActivity.this, MainActivity.class);
                intent.putExtra("wd", wd);
                intent.putExtra("jd", jd);
                startActivity(intent);
                finish();
            }
        });
    }


    //获取地理信息
    private void getLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1f, listener[0]);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1F, listener[1]);
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for (int i = 0; i < 1; i++) {
                    final Location location = listener[i].current();
                    if (location != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                count++;
                                if (count == 1) {
                                    wd = location.getLatitude() + "";
                                    jd = location.getLongitude() + "";
                                    Log.e(wd, jd);
                                }
                                Log.e("我的位置", location.getLatitude() + " : " + location.getLongitude());//维度getLatitude
                                if (count == 1) {
                                    timer.cancel();
                                }
                            }
                        });
                    }
                }
                Log.d(Constants.TAG, "No location received yet.");
            }
        }, 1, 1000);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1001) {
            //默认给定全部的权限
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1f, listener[0]);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1f, listener[1]);
        }
    }

    @Override
    protected String getPowerBarColors() {
        return AppConsts.POWER_BAR_WHITE;
    }
}
