package com.biaoke.bklive.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
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
import com.biaoke.bklive.eventbus.Event_chatroom_errorMsg;
import com.biaoke.bklive.message.Api;
import com.biaoke.bklive.message.AppConsts;
import com.biaoke.bklive.user.activity.ContributionActivity;
import com.biaoke.bklive.user.activity.UserPagehomeActivity;
import com.biaoke.bklive.utils.GlideUtis;
import com.biaoke.bklive.websocket.WebSocketService;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoView;
import com.pkmmte.view.CircularImageView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.ui.widget.DanmakuView;
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
    @BindView(R.id.myNickname)
    TextView myNickname;
    @BindView(R.id.online_people)
    TextView onlinePeople;
    @BindView(R.id.btn_follow)
    Button btnFollow;
    @BindView(R.id.livingroom_user_image)
    ImageView livingroomUserImage;
    @BindView(R.id.ll_charm_more)
    LinearLayout llCharmMore;
    @BindView(R.id.barrage_switch_close)
    ImageView barrageSwitchClose;
    @BindView(R.id.barrage_switch_open)
    ImageView barrageSwitchOpen;
    @BindView(R.id.rl_barage_switch)
    RelativeLayout rlBarageSwitch;
    private GlideUtis glideUtis;
    private PLVideoView mVideoView;
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
    private String ChatroomId;
    private List<LivingroomChatListBean> chatList = new ArrayList<>();
    private LivingroomChatListAdapter livingroomChatListAdapter;
    private LivingroomChatListBean livingroomChatListBean_chatmsg;
    //系统提示
    private LivingroomChatSysAdapter livingroomChatSysAdapter;
    private String mNickName;
    private PopupWindow popupWindow_living_share, popupWindow_living_gift, popupWindow_livingroom_message, popupWindow_livingroom_anchor, popupWindow_livingroom_common;
    private String IconUrl;
    private String Level;
    //view数组
    private List<View> viewList;
    private String userId;
    private String accessKey;
    private String msg_addFollow;
    private String Charm;
    //    private EditText inputEditor;
    private String userlistId;
    private String userlistHeadurl;
    //主播信息
    private String yNickName;
    private String yLevel;
    private String yExperience;
    private String yCharm;
    private String yDiamond;
    private String yLiveNum;
    private String yVideoNum;
    private String yHeadimageUrl;
    private String ySex;
    private String yAge;
    private String yEmotion;
    private String yHometown;
    private String yWork;
    private String yFollow;
    private String yFans;
    private String ySignture;
    private String ySendgift;
    //主播信息
    //弹幕相关
    private boolean showDanmaku;
    private DanmakuView danmakuView;
    private DanmakuContext danmakuContext;
    private BaseDanmakuParser parser = new BaseDanmakuParser() {
        @Override
        protected IDanmakus parse() {
            return new Danmakus();
        }
    };
    //弹幕结束

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_plvideo_view);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);//注册
        list.clear();//清空用户列表
        ChatroomId = getIntent().getStringExtra("chatroomId");
        JSONObject jsonObject_yuser = new JSONObject();
        try {
            jsonObject_yuser.put("Protocol", "UserInfo");
            jsonObject_yuser.put("Cmd", "GetAll");
            jsonObject_yuser.put("UserId", ChatroomId);
            Log.e("发送主播信息", jsonObject_yuser.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        UserInfoHttp(Api.ENCRYPT64, jsonObject_yuser.toString());
        SharedPreferences sharedPreferences_user = getSharedPreferences("isLogin", Context.MODE_PRIVATE);//首先获取用户ID，直播要取
        userId = sharedPreferences_user.getString("userId", "");
        accessKey = sharedPreferences_user.getString("AccessKey", "");
        mNickName = sharedPreferences_user.getString("mNickName", "");
        IconUrl = sharedPreferences_user.getString("mHeadimageUrl", "");
        Level = sharedPreferences_user.getString("mLevel", "");
        Charm = sharedPreferences_user.getString("mCharm", "");
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
        rootLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //根布局点赞
                periscopeLayout.addHeart();
                giftLayout.showLeftGiftVeiw(PLVideoViewActivity.this, "你的名字", "http://wmtp.net/wp-content/uploads/2017/02/0224_dongman_9.jpeg");

                return false;
            }
        });
        glideUtis = new GlideUtis(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    joinInWeb();//获取长连接
                    Thread.sleep(100);
                    queryFollow();//查询是否关注
                    joinChatRoom();//加入聊天室
                    Thread.sleep(300);
                    GetChatRomCount();//读取聊天室人数
                    GetChatRomList();//读取聊天室用户列表
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        danmakuView = (DanmakuView) findViewById(R.id.danmaku_view);
        danmakuView.enableDanmakuDrawingCache(true);
        danmakuView.setCallback(new DrawHandler.Callback() {
            @Override
            public void prepared() {
                showDanmaku = true;
//                danmakuView.start();
//                generateSomeDanmaku();//测试用随机弹幕
            }

            @Override
            public void updateTimer(DanmakuTimer timer) {

            }

            @Override
            public void danmakuShown(BaseDanmaku danmaku) {

            }

            @Override
            public void drawingFinished() {

            }
        });
        danmakuContext = DanmakuContext.create();
        danmakuView.prepare(parser, danmakuContext);

        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == View.SYSTEM_UI_FLAG_VISIBLE) {
                    onWindowFocusChanged(true);
                }
            }
        });

        danmakuView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputMessageLivingroom.getVisibility() == View.VISIBLE) {
                    inputMessageLivingroom.setVisibility(View.GONE);
                    BottomPanelSend.setVisibility(View.VISIBLE);
                }
            }
        });
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
                    Toast.makeText(PLVideoViewActivity.this, "AK认证失败，请重新登录", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    btnFollow.setVisibility(View.GONE);
                    break;
                case 3:
                    Toast.makeText(PLVideoViewActivity.this, msg_addFollow, Toast.LENGTH_SHORT).show();
                    btnFollow.setVisibility(View.GONE);
                    break;
                case 4:
                    glideUtis.glideCircle(yHeadimageUrl, livingroomUserImage, true);
                    myNickname.setText(yNickName);
                    tvCharmLivingShow.setText(yCharm);
                    tvBkId.setText(ChatroomId);
                    break;
            }
        }
    };

    //查询是否关注
//{"Protocol":"Fans","Cmd":"IsFans","MastId":"1001","SlaveId":"1002","AccessKey":"bk5977"}
    private void queryFollow() {
        JSONObject jsonobject_follow = new JSONObject();
        try {
            jsonobject_follow.put("Protocol", "Fans");
            jsonobject_follow.put("Cmd", "IsFans");
            jsonobject_follow.put("MastId", ChatroomId);
            jsonobject_follow.put("SlaveId", userId);
            jsonobject_follow.put("AccessKey", accessKey);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtils.postString()
                .url(Api.ENCRYPT64)
                .content(jsonobject_follow.toString())
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        OkHttpUtils.postString()
                                .url(Api.FOLLOW)
                                .content(response)
                                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                                .build()
                                .execute(new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {

                                    }

                                    @Override
                                    public void onResponse(String response, int id) {
                                        OkHttpUtils.postString()
                                                .url(Api.UNENCRYPT64)
                                                .content(response)
                                                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                                                .build()
                                                .execute(new StringCallback() {
                                                    @Override
                                                    public void onError(Call call, Exception e, int id) {

                                                    }

                                                    @Override
                                                    public void onResponse(String response, int id) {
                                                        try {
                                                            JSONObject object_follow = new JSONObject(response);
                                                            String result_follow = object_follow.getString("Result");
                                                            if (result_follow.equals("0")) {
                                                                String msg_follow = object_follow.getString("Msg");
                                                                Message message_follow = new Message();
                                                                message_follow.what = 1;
                                                                handler.sendMessage(message_follow);
                                                            } else if (result_follow.equals("1")) {
                                                                boolean isFollow = object_follow.getBoolean("Data");
                                                                if (isFollow) {
                                                                    Message message_isfollow = new Message();
                                                                    message_isfollow.what = 2;
                                                                    handler.sendMessage(message_isfollow);
                                                                }
//                                                                else {
//                                                                    Message message_isfollow = new Message();
//                                                                    message_isfollow.what = 3;
//                                                                    handler.sendMessage(message_isfollow);
//                                                                }
                                                            }
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });
                                    }
                                });
                    }
                });
    }

    //关注添加关注
//    {"Protocol":"Fans","Cmd":"Add","MastId":"1001","SlaveId":"1002","AccessKey":"bk5977"}
    private void addFollow() {
        JSONObject jsonobject_addfollow = new JSONObject();
        try {
            jsonobject_addfollow.put("Protocol", "Fans");
            jsonobject_addfollow.put("Cmd", "Add");
            jsonobject_addfollow.put("MastId", ChatroomId);
            jsonobject_addfollow.put("SlaveId", userId);
            jsonobject_addfollow.put("AccessKey", accessKey);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtils.postString()
                .url(Api.ENCRYPT64)
                .content(jsonobject_addfollow.toString())
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        OkHttpUtils.postString()
                                .url(Api.FOLLOW)
                                .content(response)
                                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                                .build()
                                .execute(new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {

                                    }

                                    @Override
                                    public void onResponse(String response, int id) {
                                        OkHttpUtils.postString()
                                                .url(Api.UNENCRYPT64)
                                                .content(response)
                                                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                                                .build()
                                                .execute(new StringCallback() {
                                                    @Override
                                                    public void onError(Call call, Exception e, int id) {

                                                    }

                                                    @Override
                                                    public void onResponse(String response, int id) {
                                                        try {
                                                            JSONObject object_follow = new JSONObject(response);
                                                            String result_follow = object_follow.getString("Result");
                                                            msg_addFollow = object_follow.getString("Msg");
                                                            if (result_follow.equals("1")) {
                                                                Message message_isfollow = new Message();
                                                                message_isfollow.what = 3;
                                                                handler.sendMessage(message_isfollow);
                                                            }

                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });
                                    }
                                });
                    }
                });
    }


    //websocket错误信息提示
    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEvent_chatroom_errorMsg(Event_chatroom_errorMsg errorMsg) {
        String errormsg = errorMsg.getMsg();
        Toast.makeText(this, errormsg, Toast.LENGTH_SHORT).show();
    }


    //收到的聊天室消息
    //如果以后时间充足，这个地方接收解密过的
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
                                String cmd = object_joinchat.getString("Cmd");
                                if (mNickName.isEmpty()) {
                                    mNickName = "游客";
                                }
                                //获取聊天室人数
                                if (cmd.equals("GetChatRomCount")) {
                                    int ChatRomCount = object_joinchat.getInt("ChatRomCount");
                                    onlinePeople.setText("观众：" + ChatRomCount);
                                } else if (cmd.equals("GetChatRomList")) {//获取聊天室用户ID
                                    String Result = object_joinchat.getString("Result");
                                    if (Result.equals("1")) {
                                        JSONArray jsonArray_list = new JSONArray(object_joinchat.getString("Data"));
                                        for (int i = 0; i < jsonArray_list.length(); i++) {
                                            JSONObject object_list = jsonArray_list.getJSONObject(i);
                                            String allUser = object_list.getString("UserId");//获取聊天室用户ID
                                            String IconUrl = object_list.getString("IconUrl");
                                            list.add(new HeadBean(IconUrl, allUser));
                                        }
                                        //下面显示用户头像列表操作
//                                        Log.d("用户头像ID", allUser);
//                                        list.add(new HeadBean("http://wmtp.net/wp-content/uploads/2017/02/0224_dongman_9.jpeg"));
                                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PLVideoViewActivity.this);
                                        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                                        videoHeadimgXrv.setLayoutManager(linearLayoutManager);
                                        //设置适配器
                                        videoHeadImgAdapter = new VideoHeadImgAdapter(PLVideoViewActivity.this, list);
                                        videoHeadimgXrv.setAdapter(videoHeadImgAdapter);
                                        videoHeadImgAdapter.setOnItemClickListener(headimagelisten);

                                    }
                                } else if (cmd.equals("AddChatRom")) {
                                    String joinchat = object_joinchat.getString("Msg");
                                    //提示某某加入聊天室
                                    livingroomChatListBean_chatmsg = new LivingroomChatListBean("", "", mNickName, "加入聊天室");
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
                if (cmd.equals("chat")) {
                    String NickName = object_chatMsg.getString("NickName");
                    String IconUrl = object_chatMsg.getString("IconUrl");
                    String Level = object_chatMsg.getString("Level");
                    String Msg_chat = object_chatMsg.getString("Msg");
                    livingroomChatListBean_chatmsg = new LivingroomChatListBean(IconUrl, Level, NickName, Msg_chat);
                    chatList.add(livingroomChatListBean_chatmsg);
                    addDanmaku(Msg_chat, true);//弹幕
                    Message msgg = new Message();
                    msgg.what = 0;
                    handler.sendMessage(msgg);
                } else if (cmd.equals("sys")) {
                    String Msg_sys = object_chatMsg.getString("Msg");
                    if (Msg_sys.equals("进入直播间")) {
                        livingroomChatListBean_chatmsg = new LivingroomChatListBean("", "", mNickName, Msg_sys);
                        chatList.add(livingroomChatListBean_chatmsg);
                    } else {
                        livingroomChatListBean_chatmsg = new LivingroomChatListBean("", "", "系统消息", Msg_sys);
                        chatList.add(livingroomChatListBean_chatmsg);
                    }
                    LinearLayoutManager layoutManager_chatmessage = new LinearLayoutManager(PLVideoViewActivity.this, LinearLayoutManager.VERTICAL, true);
                    layoutManager_chatmessage.setAutoMeasureEnabled(false);
                    chatRecyclerview.setLayoutManager(layoutManager_chatmessage);
                    livingroomChatSysAdapter = new LivingroomChatSysAdapter(PLVideoViewActivity.this, chatList);
                    livingroomChatSysAdapter.bind(chatList);
                    chatRecyclerview.setAdapter(livingroomChatSysAdapter);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private VideoHeadImgAdapter.OnItemClickListener headimagelisten = new VideoHeadImgAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int postion) {
            userlistId = list.get(postion).getUserId();
            userlistHeadurl = list.get(postion).getImgUrl();
            JSONObject jsonObject_yuser = new JSONObject();
            try {
                jsonObject_yuser.put("Protocol", "UserInfo");
                jsonObject_yuser.put("Cmd", "GetAll");
                jsonObject_yuser.put("UserId", userlistId);
                Log.e("发送用户列表信息", jsonObject_yuser.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            CommonPopWindow();
            popupWindow_livingroom_common.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        }
    };

    //退出聊天室
    private void exitChatRoom() {
        JSONObject jsonObject_quit = new JSONObject();
        try {
            jsonObject_quit.put("Protocol", "ChatRom");
            jsonObject_quit.put("UserId", "ExitChatRom");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtils.postString()
                .url(Api.ENCRYPT64)
                .content(jsonObject_quit.toString())
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

    //读聊天室用户列表
    private void GetChatRomList() {
        JSONObject jsonObject_roomlist = new JSONObject();
        try {
            jsonObject_roomlist.put("Protocol", "ChatRom");
            jsonObject_roomlist.put("Cmd", "GetChatRomList");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtils.postString()
                .url(Api.ENCRYPT64)
                .content(jsonObject_roomlist.toString())
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

    //读聊天室在线人数
    private void GetChatRomCount() {
        JSONObject jsonObject_roomcount = new JSONObject();
        try {
            jsonObject_roomcount.put("Protocol", "ChatRom");
            jsonObject_roomcount.put("Cmd", "GetChatRomCount");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtils.postString()
                .url(Api.ENCRYPT64)
                .content(jsonObject_roomcount.toString())
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

//    private void addHeadimg() {
//        //设置布局管理器
//        list.add(new HeadBean("http://wmtp.net/wp-content/uploads/2017/02/0224_dongman_9.jpeg"));
//        list.add(new HeadBean("http://wmtp.net/wp-content/uploads/2017/02/0224_dongman_9.jpeg"));
//        list.add(new HeadBean("http://wmtp.net/wp-content/uploads/2017/02/0224_dongman_9.jpeg"));
//        list.add(new HeadBean("http://wmtp.net/wp-content/uploads/2017/02/0224_dongman_9.jpeg"));
//        list.add(new HeadBean("http://wmtp.net/wp-content/uploads/2017/02/0224_dongman_9.jpeg"));
//        list.add(new HeadBean("http://wmtp.net/wp-content/uploads/2017/02/0224_dongman_9.jpeg"));
//        list.add(new HeadBean("http://wmtp.net/wp-content/uploads/2017/02/0224_dongman_9.jpeg"));
//        list.add(new HeadBean("http://wmtp.net/wp-content/uploads/2017/02/0224_dongman_9.jpeg"));
//        list.add(new HeadBean("http://wmtp.net/wp-content/uploads/2017/02/0224_dongman_9.jpeg"));
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        videoHeadimgXrv.setLayoutManager(linearLayoutManager);
//        //设置适配器
//        videoHeadImgAdapter = new VideoHeadImgAdapter(this, list);
//        videoHeadimgXrv.setAdapter(videoHeadImgAdapter);
//        videoHeadImgAdapter.setOnItemClickListener(headimagelisten);
//    }

    //用户列表信息
    private void CommonPopWindow() {
        final View anchorView = LayoutInflater.from(this).inflate(R.layout.popw_userinfo_common, null);
        popupWindow_livingroom_common = new PopupWindow(anchorView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        TextView textView_homepage = (TextView) anchorView.findViewById(R.id.tv_user_homepage);
        textView_homepage.setOnClickListener(commonListen);
        CircularImageView circularImageView = (CircularImageView) anchorView.findViewById(R.id.iv_user_head);
        glideUtis.glideCircle(userlistHeadurl, circularImageView, true);

        TextView textView_level = (TextView) anchorView.findViewById(R.id.textview_level);
        textView_level.setText("lv." + yLevel);
        TextView textView_nickname = (TextView) anchorView.findViewById(R.id.tv_userName);
        textView_nickname.setText(yNickName);
        ImageView imageView_sex = (ImageView) anchorView.findViewById(R.id.iv_sex_user);
        if (ySex.equals("男")) {
            imageView_sex.setImageResource(R.drawable.man);
        } else {
            imageView_sex.setImageResource(R.drawable.female);
        }
        TextView textView_bkID = (TextView) anchorView.findViewById(R.id.user_id);
        textView_bkID.setText(ChatroomId);
        TextView textView_signture = (TextView) anchorView.findViewById(R.id.tv_people_signture);
        textView_signture.setText(ySignture);

        TextView textView_follow = (TextView) anchorView.findViewById(R.id.tv_follow_people);
        textView_follow.setText(yFollow);
        TextView textView_fans = (TextView) anchorView.findViewById(R.id.tv_fans_people);
        textView_fans.setText(yFans);
        TextView textView_send = (TextView) anchorView.findViewById(R.id.tv_sendgift_people);
        textView_send.setText(ySendgift);
        TextView textView_charm = (TextView) anchorView.findViewById(R.id.tv_charm_people);
        textView_charm.setText(yCharm);

        LinearLayout linearLayout_addFollow = (LinearLayout) anchorView.findViewById(R.id.ll_follow_people);
        linearLayout_addFollow.setOnClickListener(commonListen);


        popupWindow_livingroom_common.setTouchable(true);
        popupWindow_livingroom_common.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub
                return false;
            }
        });
        popupWindow_livingroom_common.setBackgroundDrawable(new ColorDrawable(this.getResources().getColor(R.color.transparent)));
    }

    private View.OnClickListener commonListen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_user_homepage:
                    Intent intent_userhomepage = new Intent(PLVideoViewActivity.this, UserPagehomeActivity.class);
                    intent_userhomepage.putExtra("ChatroomId", userlistId);
                    startActivity(intent_userhomepage);
                    popupWindow_livingroom_common.dismiss();
                    finish();
                    break;
                case R.id.ll_follow_people:
                    addFollow();
                    break;
            }
        }
    };


    @Override
    protected void onPause() {
        super.onPause();
        mVideoView.pause();
        if (danmakuView != null && danmakuView.isPrepared()) {
            danmakuView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoView.start();
        if (danmakuView != null && danmakuView.isPrepared() && danmakuView.isPaused()) {
            danmakuView.resume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.stopPlayback();
        EventBus.getDefault().unregister(this);//释放
        showDanmaku = false;
        if (danmakuView != null) {
            danmakuView.release();
            danmakuView = null;
        }
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
        if (inputMessageLivingroom.getVisibility() == View.VISIBLE) {
            inputMessageLivingroom.setVisibility(View.GONE);
            BottomPanelSend.setVisibility(View.VISIBLE);
        } else {
            WebSocketService.closeWebsocket(true);
            finish();
        }
//        super.onBackPressed();
    }

    @OnClick({R.id.rl_barage_switch, R.id.input_send, R.id.ll_charm_more, R.id.livingroom_user_image, R.id.btn_follow, R.id.living_close, R.id.charm_more, R.id.tv_sendmessage, R.id.iv_livingroom_gift, R.id.iv_livingroom_share})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_barage_switch:
                if (!ivChatbarrage.isSelected()) {
                    ivChatbarrage.setSelected(true);
                    barrageSwitchClose.setVisibility(View.GONE);
                    barrageSwitchOpen.setVisibility(View.VISIBLE);
                    danmakuView.start();
                } else {
                    ivChatbarrage.setSelected(false);
                    barrageSwitchClose.setVisibility(View.VISIBLE);
                    barrageSwitchOpen.setVisibility(View.GONE);
                    danmakuView.stop();
                }
                break;
            case R.id.ll_charm_more:
                Intent intent_contribution = new Intent(PLVideoViewActivity.this, ContributionActivity.class);
                startActivity(intent_contribution);
                break;
            case R.id.livingroom_user_image:
                AnchorPopWindow();
                popupWindow_livingroom_anchor.showAtLocation(view, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.btn_follow:
                addFollow();
                break;
            case R.id.living_close:
                finish();
                break;
            case R.id.charm_more:

                break;
            case R.id.tv_sendmessage:
//                MessagePopWindow();//用popw弹出聊天框，位置没法随时改变
//                popupWindow_livingroom_message.showAtLocation(view, Gravity.BOTTOM, 0, 0);
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
                if (inputEditor.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, "消息为空", Toast.LENGTH_SHORT).show();
                } else {
                    chatRoomMessage();
                    inputEditor.getText().clear();
                }
                break;
        }
    }

    private void AnchorPopWindow() {
        final View anchorView = LayoutInflater.from(this).inflate(R.layout.popw_userinfo_anchor, null);
        popupWindow_livingroom_anchor = new PopupWindow(anchorView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        LinearLayout linearLayout_expression_more = (LinearLayout) anchorView.findViewById(R.id.ll_expression_more);
        linearLayout_expression_more.setOnClickListener(anchorListen);
        TextView textView_homepage = (TextView) anchorView.findViewById(R.id.user_homepage);
        textView_homepage.setOnClickListener(anchorListen);
        ImageView imageView_anchorHead = (ImageView) anchorView.findViewById(R.id.iv_user_head);
        glideUtis.glideCircle(yHeadimageUrl, imageView_anchorHead, true);
        TextView textView_level = (TextView) anchorView.findViewById(R.id.textView);
        textView_level.setText("lv." + yLevel);
        TextView textView_nickname = (TextView) anchorView.findViewById(R.id.tv_userName);
        textView_nickname.setText(yNickName);
        ImageView imageView_sex = (ImageView) anchorView.findViewById(R.id.iv_sex_user);
        if (ySex.equals("男")) {
            imageView_sex.setImageResource(R.drawable.man);
        } else {
            imageView_sex.setImageResource(R.drawable.female);
        }
        TextView textView_bkID = (TextView) anchorView.findViewById(R.id.user_id);
        textView_bkID.setText(ChatroomId);
        TextView textView_signture = (TextView) anchorView.findViewById(R.id.tv_anchor_signture);
        textView_signture.setText(ySignture);

        TextView textView_follow = (TextView) anchorView.findViewById(R.id.tv_follow);
        textView_follow.setText(yFollow);
        TextView textView_fans = (TextView) anchorView.findViewById(R.id.tv_fans);
        textView_fans.setText(yFans);
        TextView textView_send = (TextView) anchorView.findViewById(R.id.tv_sendgift);
        textView_send.setText(ySendgift);
        TextView textView_charm = (TextView) anchorView.findViewById(R.id.tv_charm);
        textView_charm.setText(yCharm);

        LinearLayout linearLayout_addFollow = (LinearLayout) anchorView.findViewById(R.id.ll_add_follow);
        linearLayout_addFollow.setOnClickListener(anchorListen);


        popupWindow_livingroom_anchor.setTouchable(true);
        popupWindow_livingroom_anchor.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub
                return false;
            }
        });
        popupWindow_livingroom_anchor.setBackgroundDrawable(new ColorDrawable(this.getResources().getColor(R.color.transparent)));
    }

    private View.OnClickListener anchorListen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ll_expression_more:
                    Intent intent_expression = new Intent(PLVideoViewActivity.this, ExpressionActivity.class);
                    startActivity(intent_expression);
//                    popupWindow_livingroom_anchor.dismiss();
                    break;
                case R.id.user_homepage:
//                    finish();
                    Intent intent_userhomepage = new Intent(PLVideoViewActivity.this, UserPagehomeActivity.class);
                    intent_userhomepage.putExtra("ChatroomId", ChatroomId);
                    startActivity(intent_userhomepage);
                    popupWindow_livingroom_anchor.dismiss();
                    finish();
                    break;
                case R.id.ll_add_follow:
                    addFollow();
                    break;
            }
        }
    };

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
        boolean select_only = true;
        //666开始
        final ImageView imageView_gift_lll = (ImageView) gift_view1.findViewById(R.id.gift_png_lll);
        imageView_gift_lll.setBackgroundResource(R.drawable.gift_666);
        final AnimationDrawable anim_lll = (AnimationDrawable) imageView_gift_lll.getBackground();
        final ImageView imageView_gift_livingroom_lll = (ImageView) gift_view1.findViewById(R.id.gift_livingroom_lll);
        final LinearLayout linearLayout_lll = (LinearLayout) gift_view1.findViewById(R.id.ll_gift_lll);
        final TextView textView_lll = (TextView) gift_view1.findViewById(R.id.gift_bg_exp_lll);
        linearLayout_lll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!linearLayout_lll.isSelected()) {
                    linearLayout_lll.setSelected(true);
                    textView_lll.setSelected(true);
                    textView_lll.setTextColor(getResources().getColor(R.color.black));
                    imageView_gift_livingroom_lll.setVisibility(View.GONE);
                    imageView_gift_lll.setVisibility(View.VISIBLE);
                    anim_lll.start();
                } else {
                    linearLayout_lll.setSelected(false);
                    textView_lll.setSelected(false);
                    textView_lll.setTextColor(getResources().getColor(R.color.white));
                    imageView_gift_livingroom_lll.setVisibility(View.VISIBLE);
                    imageView_gift_lll.setVisibility(View.GONE);
                    anim_lll.stop();
                }
            }
        });
        //666结束

        //棒棒糖开始
        final ImageView imageView_gift_bbt = (ImageView) gift_view1.findViewById(R.id.gift_png_bbt);
        imageView_gift_bbt.setBackgroundResource(R.drawable.gift_bangbangtang);
        final AnimationDrawable anim_bbt = (AnimationDrawable) imageView_gift_bbt.getBackground();
        final ImageView imageView_gift_livingroom_bbt = (ImageView) gift_view1.findViewById(R.id.gift_livingroom_bbt);
        final LinearLayout linearLayout_bbt = (LinearLayout) gift_view1.findViewById(R.id.ll_gift_bbt);
        final TextView textView_bbt = (TextView) gift_view1.findViewById(R.id.gift_bg_exp);
        linearLayout_bbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!linearLayout_bbt.isSelected()) {
                    linearLayout_bbt.setSelected(true);
                    textView_bbt.setSelected(true);
                    textView_bbt.setTextColor(getResources().getColor(R.color.black));
                    imageView_gift_livingroom_bbt.setVisibility(View.GONE);
                    imageView_gift_bbt.setVisibility(View.VISIBLE);
                    anim_bbt.start();
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
        //棒棒糖结束
        //蓝瘦香菇开始
        final ImageView imageView_gift_lsxg = (ImageView) gift_view1.findViewById(R.id.gift_png_lsxg);
        imageView_gift_lsxg.setBackgroundResource(R.drawable.gift_lanshouxianggu);
        final AnimationDrawable anim_lsxg = (AnimationDrawable) imageView_gift_lsxg.getBackground();
        final ImageView imageView_gift_livingroom_lsxg = (ImageView) gift_view1.findViewById(R.id.gift_livingroom_lsxg);
        final LinearLayout linearLayout_lsxg = (LinearLayout) gift_view1.findViewById(R.id.ll_gift_lsxg);
        final TextView textView_lsxg = (TextView) gift_view1.findViewById(R.id.gift_bg_exp_lsxg);
        linearLayout_lsxg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!linearLayout_lsxg.isSelected()) {
                    linearLayout_lsxg.setSelected(true);
                    textView_lsxg.setSelected(true);
                    textView_lsxg.setTextColor(getResources().getColor(R.color.black));
                    imageView_gift_livingroom_lsxg.setVisibility(View.GONE);
                    imageView_gift_lsxg.setVisibility(View.VISIBLE);
                    anim_lsxg.start();
                } else {
                    linearLayout_lsxg.setSelected(false);
                    textView_lsxg.setSelected(false);
                    textView_lsxg.setTextColor(getResources().getColor(R.color.white));
                    imageView_gift_livingroom_lsxg.setVisibility(View.VISIBLE);
                    imageView_gift_lsxg.setVisibility(View.GONE);
                    anim_lsxg.stop();
                }
            }
        });//蓝瘦香菇结束


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

    //发聊天室消息
    private void chatRoomMessage() {
        //            {"Protocol":"ChatRom","Cmd":"chat","ChatRomId":"10012","Cmd":"Msg","UserId":"1002","NickName":"","IconUrl":"","Level":"0","Msg":"消息内容"}
//        {"Protocol":"ChatRom","Cmd":"chat","NickName":"","IconUrl":"","Level":"0","Msg":"消息内容"}
        JSONObject object_chatroomMsg = new JSONObject();
        String mMsg = inputEditor.getText().toString().trim();
        if (Level.isEmpty()) {
            Level = "1";
        }
        try {
            object_chatroomMsg.put("Protocol", "ChatRom");
            object_chatroomMsg.put("Cmd", "chat");
//            object_chatroomMsg.put("ChatRomId", chatroomId);
//            object_chatroomMsg.put("UserId", "");
            object_chatroomMsg.put("NickName", mNickName);
            object_chatroomMsg.put("IconUrl", IconUrl);
            object_chatroomMsg.put("Level", Level);
            object_chatroomMsg.put("Msg", mMsg);
//            addDanmaku(mMsg, true);//测试发弹幕信息
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

    /**
     * 向弹幕View中添加一条弹幕
     *
     * @param content    弹幕的具体内容
     * @param withBorder 弹幕是否有边框
     */
    private void addDanmaku(String content, boolean withBorder) {
        BaseDanmaku danmaku = danmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        danmaku.text = content;
        danmaku.padding = 5;
        danmaku.textSize = sp2px(20);
        danmaku.textColor = Color.WHITE;
        danmaku.setTime(danmakuView.getCurrentTime());
        if (withBorder) {
            danmaku.borderColor = Color.GREEN;
        }
        danmakuView.addDanmaku(danmaku);
    }

    /**
     * sp转px的方法。
     */
    public int sp2px(float spValue) {
        final float fontScale = getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 随机生成一些弹幕内容以供测试
     */
    private void generateSomeDanmaku() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (showDanmaku) {
                    int time = new Random().nextInt(300);
                    String content = "" + time + time;
                    addDanmaku(content, false);
                    try {
                        Thread.sleep(time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    //页面加载之后立即执行的方法
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    //直播间观众视角分享
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


    //获取用户信息
    public void UserInfoHttp(String url, String path) {
        OkHttpUtils
                .postString()
                .url(url)
                .content(path)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d("失败的返回", e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        OkHttpUtils.postString()
                                .url(Api.USERINFO_USER)
                                .content(response)
                                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                                .build()
                                .execute(new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {
                                        Log.d("失败的返回", e.getMessage());
                                    }

                                    @Override
                                    public void onResponse(String response, int id) {
                                        OkHttpUtils.postString()
                                                .url(Api.UNENCRYPT64)
                                                .content(response)
                                                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                                                .build()
                                                .execute(new StringCallback() {
                                                    @Override
                                                    public void onError(Call call, Exception e, int id) {
                                                        Log.d("失败的返回", e.getMessage());
                                                    }

                                                    @Override
                                                    public void onResponse(String response, int id) {
                                                        Log.d("成功===成功的返回", response);
                                                        try {
                                                            JSONObject jsonobject = new JSONObject(response);
                                                            yNickName = jsonobject.getString("NickName");
                                                            yLevel = jsonobject.getString("Level");
                                                            yExperience = jsonobject.getString("经验");
                                                            yCharm = jsonobject.getString("魅力");
                                                            yDiamond = jsonobject.getString("钻石");
                                                            yLiveNum = jsonobject.getString("直播");
                                                            yVideoNum = jsonobject.getString("点播");
                                                            yHeadimageUrl = jsonobject.getString("IconUrl");
                                                            ySex = jsonobject.getString("性别");
                                                            yAge = jsonobject.getString("生日");
                                                            yEmotion = jsonobject.getString("情感");
                                                            yHometown = jsonobject.getString("家乡");
                                                            yWork = jsonobject.getString("职业");
                                                            yFollow = jsonobject.getString("关注" + "");
                                                            yFans = jsonobject.getString("粉丝" + "");
                                                            ySignture = jsonobject.getString("签名" + "");
                                                            ySendgift = jsonobject.getString("消费");
                                                            Message message_userinfo = new Message();
                                                            message_userinfo.what = 4;
                                                            handler.sendMessage(message_userinfo);
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });
                                    }
                                });
                    }
                });
    }
}
