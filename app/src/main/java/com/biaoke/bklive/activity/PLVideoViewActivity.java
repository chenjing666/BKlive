package com.biaoke.bklive.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.biaoke.bklive.R;
import com.biaoke.bklive.activity.room.GiftLayout;
import com.biaoke.bklive.activity.room.PeriscopeLayout;
import com.biaoke.bklive.adapter.LivingroomChatListAdapter;
import com.biaoke.bklive.adapter.LivingroomChatSysAdapter;
import com.biaoke.bklive.adapter.VideoHeadImgAdapter;
import com.biaoke.bklive.base.BaseActivity;
import com.biaoke.bklive.bean.HeadBean;
import com.biaoke.bklive.bean.LivingroomChatListBean;
import com.biaoke.bklive.eventbus.Event_chatroom;
import com.biaoke.bklive.message.Api;
import com.biaoke.bklive.message.AppConsts;
import com.biaoke.bklive.websocket.WebSocketService;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import okhttp3.Call;
import okhttp3.MediaType;

public class PLVideoViewActivity extends BaseActivity {
    @BindView(R.id.video_headimg_xrv)
    RecyclerView videoHeadimgXrv;
    @BindView(R.id.chat_recyclerview)
    RecyclerView chatRecyclerview;
    @BindView(R.id.living_close)
    ImageView livingClose;
    @BindView(R.id.PLVideoView)
    com.pili.pldroid.player.widget.PLVideoView PLVideoView;
    @BindView(R.id.tv_charm_living_show)
    TextView tvCharmLivingShow;
    @BindView(R.id.charm_more)
    ImageView charmMore;
    @BindView(R.id.tv_bk_id)
    TextView tvBkId;
    @BindView(R.id.tv_sendmessage)
    TextView tvSendmessage;
    @BindView(R.id.iv_livingroom_gift)
    ImageView ivLivingroomGift;
    @BindView(R.id.iv_livingroom_share)
    ImageView ivLivingroomShare;
    @BindView(R.id.BottomPanel_send)
    RelativeLayout BottomPanelSend;
    @BindView(R.id.iv_chatbarrage)
    ImageView ivChatbarrage;
    @BindView(R.id.input_editor)
    EditText inputEditor;
    @BindView(R.id.input_bar)
    RelativeLayout inputBar;
    @BindView(R.id.input_send)
    TextView inputSend;
    @BindView(R.id.input_message_livingroom)
    LinearLayout inputMessageLivingroom;
    @BindView(R.id.iv_livingroom_upvot)
    ImageView ivLivingroomUpvot;
    //    @BindView(R.id.bottom_bar)
//    BottomPanelFragment bottomBar;
    private PLVideoView mVideoView;
    //    private List<live_item> recyclerDataList = new ArrayList<>();
    private String path2;
    private VideoHeadImgAdapter videoHeadImgAdapter;
    private List<HeadBean> list = new ArrayList<HeadBean>();
    @BindView(R.id.root_layout)
    RelativeLayout rootLayout;
    @BindView(R.id.periscope_layout)
    PeriscopeLayout periscopeLayout;
    @BindView(R.id.gift_layout)
    GiftLayout giftLayout;
    //websocket
//    private BottomPanelFragment bottomBar;
    private String ChatroomId;
    private List<LivingroomChatListBean> chatList = new ArrayList<>();
    private LivingroomChatListAdapter livingroomChatListAdapter;
    private LivingroomChatListBean livingroomChatListBean_chatmsg;
    //系统提示
    private LivingroomChatSysAdapter livingroomChatSysAdapter;
    private String mNickName;
    private PopupWindow popupWindow_living_share, popupWindow_living_gift;
    private String IconUrl;
    private String Level;//view数组
    private List<View> viewList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_plvideo_view);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);//注册
        ChatroomId = getIntent().getStringExtra("chatroomId");
        SharedPreferences sharedPreferences_user = getSharedPreferences("isLogin", Context.MODE_PRIVATE);//首先获取用户ID，直播要取
//        String UserId = sharedPreferences_user.getString("userId", "");
        mNickName = sharedPreferences_user.getString("mNickName", "");
        IconUrl = sharedPreferences_user.getString("mHeadimageUrl", "");
        Level = sharedPreferences_user.getString("mLevel", "");
        path2 = getIntent().getStringExtra("path");
        mVideoView = (PLVideoView) findViewById(R.id.PLVideoView);
        mVideoView.setKeepScreenOn(true);//设置屏幕常亮
        mVideoView.requestFocus();//拿到焦点
        mVideoView.setVideoPath(path2);
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                joinInWeb();//获取长连接
                joinChatRoom();//加入聊天室
            }
        }).start();
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    LinearLayoutManager layoutManager_chatmessage = new LinearLayoutManager(PLVideoViewActivity.this, LinearLayoutManager.VERTICAL, false);
                    layoutManager_chatmessage.setAutoMeasureEnabled(false);
                    chatRecyclerview.setLayoutManager(layoutManager_chatmessage);
                    livingroomChatListAdapter = new LivingroomChatListAdapter(PLVideoViewActivity.this, chatList);
                    livingroomChatListAdapter.bind(chatList);
                    chatRecyclerview.setAdapter(livingroomChatListAdapter);
                    break;
                case 1:

                    break;
                case 2:
                    break;
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEvent_chatroom(Event_chatroom msg) {
        String mmsg = msg.getMsg();
        String isMsg = mmsg.substring(0, 1);
        Log.e("聊天消息", isMsg);
        Log.d("聊天消息", mmsg);
        if (!isMsg.equals("{")) {
            OkHttpUtils.postString()
                    .url(Api.UNENCRYPT64)
                    .content(mmsg)
                    .mediaType(MediaType.parse("application/json; charset=utf-8"))
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            Log.d("失败的返回", e.getMessage());
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            Log.d("成功的返回", response);
//                            {"Protocol":"ChatRom","Result":"1","Msg":"加入聊天室成功","Cmd":"AddChatRom","ChatRomId":"1174"}
                            Log.e("播放端mNickName", mNickName);
                            try {
                                JSONObject object_joinchat = new JSONObject(response);
                                String joinchat = object_joinchat.getString("Msg");

                                if (mNickName.isEmpty()) {
                                    mNickName = "游客";
                                }
                                if (joinchat.equals("加入聊天室成功")) {
                                    //提示某某加入聊天室
                                    livingroomChatListBean_chatmsg = new LivingroomChatListBean("", "", mNickName, "加入聊天室");
                                    chatList.add(livingroomChatListBean_chatmsg);
                                } else if (!joinchat.equals("认证成功")) {
                                    livingroomChatListBean_chatmsg = new LivingroomChatListBean("", "", "", joinchat);
                                    chatList.add(livingroomChatListBean_chatmsg);
                                }
                                LinearLayoutManager layoutManager_chatmessage = new LinearLayoutManager(PLVideoViewActivity.this, LinearLayoutManager.VERTICAL, true);
                                layoutManager_chatmessage.setAutoMeasureEnabled(false);
                                chatRecyclerview.setLayoutManager(layoutManager_chatmessage);
                                livingroomChatSysAdapter = new LivingroomChatSysAdapter(PLVideoViewActivity.this, chatList);
                                livingroomChatSysAdapter.bind(chatList);
                                chatRecyclerview.setAdapter(livingroomChatSysAdapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } else {
            try {
//            {"Protocol":"ChatRom","Cmd":"chat","ChatRomId":"10012","Cmd":"Msg","UserId":"1002","NickName":"","IconUrl":"","Level":"0","Msg":"消息内容"}
                JSONObject object_chatMsg = new JSONObject(mmsg);
                //处理接收到的聊天信息
                String cmd = object_chatMsg.getString("Cmd");
                String NickName = object_chatMsg.getString("NickName");
                String IconUrl = object_chatMsg.getString("IconUrl");
                String Level = object_chatMsg.getString("Level");
                String Msg_chat = object_chatMsg.getString("Msg");
                if (Msg_chat.equals("加入聊天室成功")) {
                    //提示某某加入聊天室
                    livingroomChatListBean_chatmsg = new LivingroomChatListBean("", "", mNickName, "加入聊天室");
                    chatList.add(livingroomChatListBean_chatmsg);
                } else if (cmd.equals("sys")) {
                    livingroomChatListBean_chatmsg = new LivingroomChatListBean("", "", "系统消息", Msg_chat);
                    chatList.add(livingroomChatListBean_chatmsg);
                } else if (!IconUrl.isEmpty()) {
                livingroomChatListBean_chatmsg = new LivingroomChatListBean(IconUrl, Level, NickName, Msg_chat);
                    chatList.add(livingroomChatListBean_chatmsg);
                }
                Message msgg = new Message();
                msgg.what = 0;
                handler.sendMessage(msgg);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    //获取socket长连接
    private void joinInWeb() {
        SharedPreferences sharedPreferences_accesskey = getSharedPreferences("isLogin", Context.MODE_PRIVATE);
        String AccessKey = sharedPreferences_accesskey.getString("AccessKey", "");
        String useId = sharedPreferences_accesskey.getString("userId", "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Protocol", "Logging");
            jsonObject.put("UserId", useId);
            jsonObject.put("AccessKey", AccessKey);
            jsonObject.put("PwdModel", "3");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        WebSocketService.sendMsg(jsonObject.toString());
//        {"Protocol":"Logging","Result":"1","UsdrId":"1174","Msg":"认证成功"}
    }

    //加入聊天室
    private void joinChatRoom() {
        JSONObject jsonObject_chatroom = new JSONObject();
        try {
            jsonObject_chatroom.put("Protocol", "ChatRom");
            jsonObject_chatroom.put("ChatRomId", ChatroomId);
            jsonObject_chatroom.put("Cmd", "AddChatRom");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtils.postString()
                .url(Api.ENCRYPT64)
                .content(jsonObject_chatroom.toString())
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d("失败的返回", e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        WebSocketService.sendMsg(response);
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
        videoHeadImgAdapter = new VideoHeadImgAdapter(this, list);
        videoHeadimgXrv.setAdapter(videoHeadImgAdapter);

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
        EventBus.getDefault().unregister(this);//释放
    }

    @Override
    protected String getPowerBarColors() {
        return AppConsts.POWER_BAR_BACKGROUND;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        WebSocketService.closeWebsocket(true);
        super.onBackPressed();
    }

    @OnClick({R.id.living_close, R.id.charm_more, R.id.tv_sendmessage, R.id.iv_livingroom_gift, R.id.iv_livingroom_share, R.id.input_send})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.living_close:
                finish();
                break;
            case R.id.charm_more:

                break;
            case R.id.tv_sendmessage:
                BottomPanelSend.setVisibility(View.GONE);
                inputMessageLivingroom.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_livingroom_gift:
                giftPop();
                popupWindow_living_gift.showAtLocation(view, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.iv_livingroom_share:
                sharePopw();
                popupWindow_living_share.showAtLocation(view, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.input_send:
                Toast.makeText(this, "点击了发信息", Toast.LENGTH_SHORT).show();
                chatRoomMessage();
                inputEditor.getText().clear();
                break;
        }
    }

    //礼物popwindow
    private void giftPop() {
        View gift_view1, gift_view2, gift_view3;
        LayoutInflater mlayout = getLayoutInflater();
        final View livinggiftView = LayoutInflater.from(this).inflate(R.layout.gift_livingroom, null);
        popupWindow_living_gift = new PopupWindow(livinggiftView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        final Button[] mPreSelectedBt = {null};
        final LinearLayout mNumLayout = (LinearLayout) livinggiftView.findViewById(R.id.ll_page_num);
        ViewPager viewPager_gift = (ViewPager) livinggiftView.findViewById(R.id.viewpager_gift);
        ImageView imageView_reCharge = (ImageView) livinggiftView.findViewById(R.id.diamong_recharge_gift);
        Button button_sendGift = (Button) livinggiftView.findViewById(R.id.gift_send);
        viewList = new ArrayList<View>();// 将要分页显示的View装入数组中
        gift_view1 = mlayout.inflate(R.layout.gift_view1, null);
        gift_view2 = mlayout.inflate(R.layout.gift_view2, null);
        gift_view3 = mlayout.inflate(R.layout.gift_view3, null);
        viewList.add(gift_view1);
        viewList.add(gift_view2);
        viewList.add(gift_view3);
        GiftPagerAdapter giftadapter = new GiftPagerAdapter();
        viewPager_gift.setAdapter(giftadapter);
        final ImageView imageView_gift_bbt = (ImageView) gift_view1.findViewById(R.id.gift_png);
        imageView_gift_bbt.setBackgroundResource(R.drawable.gift_bangbangtang);
        final AnimationDrawable anim_bbt = (AnimationDrawable) imageView_gift_bbt.getBackground();
//        anim_bbt.start();
        final ImageView imageView_gift_livingroom_bbt = (ImageView) gift_view1.findViewById(R.id.gift_livingroom_bbt);
        final LinearLayout linearLayout_bbt = (LinearLayout) gift_view1.findViewById(R.id.ll_gift_bbt);
        final TextView textView_bbt = (TextView) gift_view1.findViewById(R.id.gift_bg_exp);
        imageView_gift_livingroom_bbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//目前只能点击一次，还不知道why
                Toast.makeText(PLVideoViewActivity.this, "变色啊", Toast.LENGTH_SHORT).show();
                if (!imageView_gift_bbt.isSelected()) {
                    linearLayout_bbt.setSelected(true);
                    textView_bbt.setSelected(true);
                    textView_bbt.setTextColor(getResources().getColor(R.color.black));
                    imageView_gift_livingroom_bbt.setVisibility(View.GONE);
                    imageView_gift_bbt.setVisibility(View.VISIBLE);
                    anim_bbt.start();
//                    if (linearLayout_bbt.isSelected())
                } else {
                    linearLayout_bbt.setSelected(false);
                    textView_bbt.setSelected(false);
                    textView_bbt.setTextColor(getResources().getColor(R.color.white));
                    imageView_gift_livingroom_bbt.setVisibility(View.VISIBLE);
                    imageView_gift_bbt.setVisibility(View.GONE);
                    anim_bbt.stop();
                }
            }
        });

        //圆点指示器
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.liwu_dian2);
        for (int i = 0; i < viewList.size(); i++) {
            Button bt = new Button(this);
            bt.setLayoutParams(new ViewGroup.LayoutParams(bitmap.getWidth(), bitmap.getHeight()));
            bt.setBackgroundResource(R.drawable.liwu_dian1);
            mNumLayout.addView(bt);
        }

        //滑动监听
        viewPager_gift.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (mPreSelectedBt[0] != null) {
                    mPreSelectedBt[0].setBackgroundResource(R.drawable.liwu_dian1);
                }

                Button currentBt = (Button) mNumLayout.getChildAt(position);
                currentBt.setBackgroundResource(R.drawable.liwu_dian2);
                mPreSelectedBt[0] = currentBt;
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        popupWindow_living_gift.setTouchable(true);
        popupWindow_living_gift.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub
                return false;
            }
        });
        popupWindow_living_gift.setBackgroundDrawable(new ColorDrawable(this.getResources().getColor(R.color.transparent)));
    }

    //礼物滑动适配
    private class GiftPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return viewList.size();
        }

        @Override
        public Object instantiateItem(View container, int position) {
            Log.i("INFO", "instantiate item:" + position);
            ((ViewPager) container).addView(viewList.get(position), 0);
            return viewList.get(position);
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            Log.i("INFO", "destroy item:" + position);
            ((ViewPager) container).removeView(viewList.get(position));
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
    }

    private void chatRoomMessage() {
        //            {"Protocol":"ChatRom","Cmd":"chat","ChatRomId":"10012","Cmd":"Msg","UserId":"1002","NickName":"","IconUrl":"","Level":"0","Msg":"消息内容"}
//        {"Protocol":"ChatRom","Cmd":"chat","NickName":"","IconUrl":"","Level":"0","Msg":"消息内容"}
        JSONObject object_chatroomMsg = new JSONObject();
        String mMsg = inputEditor.getText().toString().trim();
        try {
            object_chatroomMsg.put("Protocol", "ChatRom");
            object_chatroomMsg.put("Cmd", "chat");
//            object_chatroomMsg.put("ChatRomId", chatroomId);
//            object_chatroomMsg.put("UserId", "");
            object_chatroomMsg.put("NickName", mNickName);
            object_chatroomMsg.put("IconUrl", IconUrl);
            object_chatroomMsg.put("Level", Level);
            object_chatroomMsg.put("Msg", mMsg);
            Log.e("发消息内容", object_chatroomMsg.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtils.postString()
                .url(Api.ENCRYPT64)
                .content(object_chatroomMsg.toString())
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d("失败的返回", e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        WebSocketService.sendMsg(response);
                    }
                });

    }

    private void sharePopw() {
        final View livingshareView = LayoutInflater.from(this).inflate(R.layout.share_livingroom, null);
        popupWindow_living_share = new PopupWindow(livingshareView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        Button button_cancel = (Button) livingshareView.findViewById(R.id.btn_livingshare_cancel);
        button_cancel.setOnClickListener(shareListen);
        popupWindow_living_share.setTouchable(true);
        popupWindow_living_share.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub
                return false;
            }
        });
        popupWindow_living_share.setBackgroundDrawable(new ColorDrawable(this.getResources().getColor(R.color.transparent)));
    }

    private View.OnClickListener shareListen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_livingshare_cancel:
                    popupWindow_living_share.dismiss();
                    break;
            }
        }
    };
}
