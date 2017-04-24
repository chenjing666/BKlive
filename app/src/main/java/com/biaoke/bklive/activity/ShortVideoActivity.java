package com.biaoke.bklive.activity;

import android.os.Bundle;

import com.biaoke.bklive.R;
import com.biaoke.bklive.base.BaseActivity;
import com.biaoke.bklive.message.AppConsts;
import com.pili.pldroid.player.PLMediaPlayer;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShortVideoActivity extends BaseActivity {

    @BindView(R.id.PLVideoView)
    com.pili.pldroid.player.widget.PLVideoView mVideoView;
    private String shortvideopath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_short_video);
        ButterKnife.bind(this);
        shortvideopath = getIntent().getStringExtra("path");
        mVideoView.setKeepScreenOn(true);//设置屏幕常亮
        mVideoView.requestFocus();//拿到焦点
        mVideoView.setVideoPath(shortvideopath);
        mVideoView.setOnInfoListener(new PLMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(PLMediaPlayer plMediaPlayer, int i, int i1) {
                return false;
            }
        });
        mVideoView.setOnBufferingUpdateListener(new PLMediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(PLMediaPlayer plMediaPlayer, int i) {
                mVideoView.start();
            }
        });
    }

    @Override
    protected String getPowerBarColors() {
        return AppConsts.POWER_BAR_BLUE;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoView.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.stopPlayback();
    }
}
