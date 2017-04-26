package com.biaoke.bklive.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.biaoke.bklive.R;
import com.biaoke.bklive.base.BaseActivity;
import com.biaoke.bklive.eventbus.Event_privatemessage;
import com.biaoke.bklive.fragment.GossipFragment;
import com.biaoke.bklive.fragment.MessageFragment;
import com.biaoke.bklive.fragment.PrivateMessageFragment;
import com.biaoke.bklive.message.AppConsts;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

public class MessageActivity extends BaseActivity {

    @BindViews({R.id.tv_gossip, R.id.tv_privatemessage, R.id.tv_mymessage})
    TextView[] textViews;
    @BindView(R.id.viewpager_message)
    ViewPager viewpagerMessage;
    @BindView(R.id.back_message)
    ImageView backMessage;
    private GossipFragment mGossipFragment;
    private PrivateMessageFragment mPrivateMessageFragment;
    private MessageFragment mMessageFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);//注册
        initView();
        backMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if(mGossipFragment==null){
            mGossipFragment=new GossipFragment();
        }
        if(mPrivateMessageFragment==null){
            mPrivateMessageFragment=new PrivateMessageFragment();
        }
        if(mMessageFragment==null){
            mMessageFragment=new MessageFragment();
        }
    }




    //初始化视图
    private void initView() {
        viewpagerMessage.setAdapter(myMessageAdapter);
        //刚进来默认选择私信
        textViews[1].setSelected(true);
        viewpagerMessage.setCurrentItem(1);
        //viewPager添加滑动监听，用于控制TextView的展示
        viewpagerMessage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                //textView全部未选择
                for (TextView textView : textViews) {
                    textView.setSelected(false);
                }
                //设置选择效果
                textViews[position].setSelected(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private FragmentStatePagerAdapter myMessageAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return mGossipFragment;
                case 1:
                    return mPrivateMessageFragment;
                case 2:
                    return mMessageFragment;
            }
            return null;
        }
    };

    @Override
    protected String getPowerBarColors() {
        return AppConsts.POWER_BAR_BACKGROUND;
    }

    @OnClick({R.id.tv_gossip, R.id.tv_privatemessage, R.id.tv_mymessage})
    public void onClick(View view) {
        for (int i = 0; i < textViews.length; i++) {
            textViews[i].setSelected(false);
            textViews[i].setTag(i);
        }
        view.setSelected(true);
        viewpagerMessage.setCurrentItem((Integer) view.getTag(), false);
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void Event_privateMessage(Event_privatemessage privatemessage) {
        String privateMsg = privatemessage.getMsg();//json格式的信息
        //如果是私信发送通知到PrivateMessageFragment
        mPrivateMessageFragment.setMag(privateMsg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
