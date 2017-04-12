package com.biaoke.bklive.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.biaoke.bklive.R;
import com.biaoke.bklive.activity.room.GiftLayout;
import com.biaoke.bklive.activity.room.PeriscopeLayout;
import com.biaoke.bklive.adapter.VideoHeadImgAdapter;
import com.biaoke.bklive.base.BaseActivity;
import com.biaoke.bklive.bean.HeadBean;
import com.biaoke.bklive.bean.live_item;
import com.biaoke.bklive.message.AppConsts;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PLVideoViewActivity extends BaseActivity {
    @BindView(R.id.video_headimg_xrv)
    RecyclerView videoHeadimgXrv;
    private PLVideoView mVideoView;
    private List<live_item> recyclerDataList = new ArrayList<>();
    private Button btn_start, btn_pause, btn_stop;
    private String path = "http://pili-live-hls.bk5977.com/bk-test1/1174.m3u8";
    private String path2;
    private VideoHeadImgAdapter videoHeadImgAdapter;
    private List<HeadBean> list = new ArrayList<HeadBean>();
    @BindView(R.id.root_layout)
    RelativeLayout rootLayout;
    @BindView(R.id.periscope_layout)
    PeriscopeLayout periscopeLayout;
    @BindView(R.id.gift_layout)
    GiftLayout giftLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_plvideo_view);
        ButterKnife.bind(this);

        path2 = getIntent().getStringExtra("path");
        mVideoView = (PLVideoView) findViewById(R.id.PLVideoView);
        mVideoView.setKeepScreenOn(true);//设置屏幕常亮
        mVideoView.requestFocus();//拿到焦点
        mVideoView.setVideoPath(path2);
//        mVideoView.start();
        btn_start = (Button) findViewById(R.id.start_video);
        btn_start.setOnClickListener(listen);
        btn_pause = (Button) findViewById(R.id.pause);
        btn_pause.setOnClickListener(listen);
        btn_stop = (Button) findViewById(R.id.stop);
        btn_stop.setOnClickListener(listen);
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
        addHeadimg();
        rootLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //根布局点赞
                periscopeLayout.addHeart();
                giftLayout.showLeftGiftVeiw(PLVideoViewActivity.this, "你的名字", "http://wmtp.net/wp-content/uploads/2017/02/0224_dongman_9.jpeg");

                return false;
            }
        });
    }

    private void addHeadimg() {
        //设置布局管理器
        list.add(new HeadBean("http://wmtp.net/wp-content/uploads/2017/02/0224_dongman_9.jpeg"));
        list.add(new HeadBean("http://wmtp.net/wp-content/uploads/2017/02/0224_dongman_9.jpeg"));
        list.add(new HeadBean("http://wmtp.net/wp-content/uploads/2017/02/0224_dongman_9.jpeg"));
        list.add(new HeadBean("http://wmtp.net/wp-content/uploads/2017/02/0224_dongman_9.jpeg"));
        list.add(new HeadBean("http://wmtp.net/wp-content/uploads/2017/02/0224_dongman_9.jpeg"));
        list.add(new HeadBean("http://wmtp.net/wp-content/uploads/2017/02/0224_dongman_9.jpeg"));
        list.add(new HeadBean("http://wmtp.net/wp-content/uploads/2017/02/0224_dongman_9.jpeg"));
        list.add(new HeadBean("http://wmtp.net/wp-content/uploads/2017/02/0224_dongman_9.jpeg"));
        list.add(new HeadBean("http://wmtp.net/wp-content/uploads/2017/02/0224_dongman_9.jpeg"));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        videoHeadimgXrv.setLayoutManager(linearLayoutManager);
        //设置适配器
        videoHeadImgAdapter = new VideoHeadImgAdapter(this,list);
        videoHeadimgXrv.setAdapter(videoHeadImgAdapter);

    }

    private View.OnClickListener listen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.start_video:
                    mVideoView.start();
                    Toast.makeText(PLVideoViewActivity.this, "开始", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.pause:
                    mVideoView.pause();
                    Toast.makeText(PLVideoViewActivity.this, "暂停", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.stop:
                    mVideoView.stopPlayback();
                    finish();
                    Toast.makeText(PLVideoViewActivity.this, "结束", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

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

    @Override
    protected String getPowerBarColors() {
        return AppConsts.POWER_BAR_BACKGROUND;
    }
}
