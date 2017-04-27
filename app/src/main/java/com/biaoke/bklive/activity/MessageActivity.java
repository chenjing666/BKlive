package com.biaoke.bklive.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.biaoke.bklive.R;
import com.biaoke.bklive.base.BaseActivity;
import com.biaoke.bklive.eventbus.Event_privatemessage;
import com.biaoke.bklive.fragment.GossipFragment;
import com.biaoke.bklive.fragment.MessageFragment;
import com.biaoke.bklive.fragment.PrivateMessageFragment;
import com.biaoke.bklive.message.Api;
import com.biaoke.bklive.message.AppConsts;
import com.biaoke.bklive.websocket.WebSocketService;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import okhttp3.Call;
import okhttp3.MediaType;

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
    private String userId;
    private String accessKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);//注册
        SharedPreferences sharedPreferences_user = getSharedPreferences("isLogin", Context.MODE_PRIVATE);
        userId = sharedPreferences_user.getString("userId", "");
        accessKey = sharedPreferences_user.getString("AccessKey", "");
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    joinInWeb();//获取长连接
                    Thread.sleep(100);
                    getMsgBefore();//读取缓存信息
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    //    读私信缓存接口
//    发：{"Protocol":"UserMsg","Cmd":"cache","UserId":"10012"}
    private void getMsgBefore() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Protocol", "UserMsg");
            jsonObject.put("UserId", userId);
            jsonObject.put("Cmd", "cache");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtils.postString()
                .url(Api.ENCRYPT64)
                .content(jsonObject.toString())
                .mediaType(MediaType.parse("application/json charset=utf-8"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e("MessageActivity发读缓存信息",response);
                        WebSocketService.sendMsg(response);
                    }
                });

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

    //获取socket长连接
    private void joinInWeb() {
//        String useId = sharedPreferences_accesskey.getString("userId", "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Protocol", "Logging");
            jsonObject.put("UserId", userId);
            jsonObject.put("AccessKey", accessKey);
            jsonObject.put("PwdModel", "3");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        WebSocketService.sendMsg(jsonObject.toString());
//        {"Protocol":"Logging","Result":"1","UsdrId":"1174","Msg":"认证成功"}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
