package com.biaoke.bklive.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
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
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
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
    @BindView(R.id.gift_wen_big)
    ImageView giftWenBig;
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
    private String userlistName;
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
    private boolean select_only;
    private ClipboardManager mClipboardManager;//剪切板管理工具类
    private ClipData mClipData;//剪切板Data对象
    private UMImage thumb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_plvideo_view);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);//注册
        mClipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        list.clear();//清空用户列表
        ChatroomId = getIntent().getStringExtra("chatroomId");
        JSONObject jsonObject_yuser = new JSONObject();
        try {
            jsonObject_yuser.put("Protocol", "UserInfo");
            jsonObject_yuser.put("Cmd", "GetAll");
            jsonObject_yuser.put("UserId", ChatroomId);//主播信息
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
                periscopeLayout.addHeart();//只显示点赞动画
//                giftLayout.showLeftGiftVeiw(PLVideoViewActivity.this, mNickName, IconUrl);

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
                    GetChatRomList();//读取聊天室用户列表  服务器给，不需要请求了

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
//                    Toast.makeText(PLVideoViewActivity.this, "AK认证失败，请重新登录", Toast.LENGTH_SHORT).show();
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
                                }
                                if (cmd.equals("GetChatRomList")) {//获取聊天室用户ID
                                    String Result = object_joinchat.getString("Result");
                                    if (Result.equals("1")) {
                                        JSONArray jsonArray_list = new JSONArray(object_joinchat.getString("Data"));
                                        for (int i = 0; i < jsonArray_list.length(); i++) {
                                            JSONObject object_list = jsonArray_list.getJSONObject(i);
                                            String allUser = object_list.getString("UserId");//获取聊天室用户ID
                                            String IconUrl = object_list.getString("IconUrl");
//                                            String userListNickname = object_list.getString("NickName");
                                            list.add(new HeadBean(IconUrl, allUser, ""));
                                            Log.d("GetChatRomList用户头像ID", allUser);
                                        }
                                        //下面显示用户头像列表操作
//                                        list.add(new HeadBean("http://wmtp.net/wp-content/uploads/2017/02/0224_dongman_9.jpeg"));
                                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PLVideoViewActivity.this);
                                        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                                        videoHeadimgXrv.setLayoutManager(linearLayoutManager);
                                        //设置适配器
                                        videoHeadImgAdapter = new VideoHeadImgAdapter(PLVideoViewActivity.this, list);
                                        videoHeadimgXrv.setAdapter(videoHeadImgAdapter);
                                        videoHeadImgAdapter.setOnItemClickListener(headimagelisten);

                                    }
                                }
                                if (cmd.equals("AddChatRom")) {
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
                }
                if (cmd.equals("sys")) {
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
            userlistName = list.get(postion).getUserlistnickName();
            JSONObject jsonObject_yuser = new JSONObject();
            try {
                jsonObject_yuser.put("Protocol", "UserInfo");
                jsonObject_yuser.put("Cmd", "GetAll");
                jsonObject_yuser.put("UserId", userlistId);
                Log.e("发送用户列表信息", jsonObject_yuser.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            UserInfoHttp(Api.ENCRYPT64, jsonObject_yuser.toString());
            CommonPopWindow();
            popupWindow_livingroom_common.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        }
    };

    //退出聊天室
    private void exitChatRoom() {
        JSONObject jsonObject_quit = new JSONObject();
        try {
            jsonObject_quit.put("Protocol", "ChatRom");
            jsonObject_quit.put("Cmd", "ExitChatRom");
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

    //管理员删除用户禁言
    private void DeBlockUserSpeaking() {
        JSONObject jsonObject_deblock = new JSONObject();
        try {
            jsonObject_deblock.put("Protocol", "ChatRom");
            jsonObject_deblock.put("Cmd", "AddBlockUser");
            jsonObject_deblock.put("BlockUserId", "id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtils.postString()
                .url(Api.ENCRYPT64)
                .content(jsonObject_deblock.toString())
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

    //管理员设置用户禁言
    private void BlockUserSpeaking() {
        JSONObject jsonObject_block = new JSONObject();
        try {
            jsonObject_block.put("Protocol", "ChatRom");
            jsonObject_block.put("Cmd", "AddBlockUser");
            jsonObject_block.put("BlockUserId", "id");
            jsonObject_block.put("Minute", "20");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtils.postString()
                .url(Api.ENCRYPT64)
                .content(jsonObject_block.toString())
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
        TextView textView_private_msg = (TextView) anchorView.findViewById(R.id.tv_user_private_msg);
        textView_private_msg.setOnClickListener(commonListen);
        ImageView imageView_defriend = (ImageView) anchorView.findViewById(R.id.livingroom_data_defriend);
        imageView_defriend.setOnClickListener(commonListen);
        ImageView imageView_tip = (ImageView) anchorView.findViewById(R.id.livingroom_data_tip);
        imageView_tip.setOnClickListener(commonListen);

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
        textView_bkID.setText(userlistId);
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
                case R.id.livingroom_data_defriend:

                    break;
                case R.id.livingroom_data_tip:

                    break;
                case R.id.tv_user_homepage:
                    Intent intent_userhomepage = new Intent(PLVideoViewActivity.this, UserPagehomeActivity.class);
                    SharedPreferences sharedPreferences_user = getSharedPreferences("isLogin", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences_user.edit();
                    editor.putString("ChatroomId", userlistId);
                    editor.commit();
                    intent_userhomepage.putExtra("ChatroomId", userlistId);
                    startActivity(intent_userhomepage);
                    popupWindow_livingroom_common.dismiss();
                    finish();
                    break;
                case R.id.ll_follow_people:
                    addFollow();
                    break;
                case R.id.tv_user_private_msg:
                    Intent intent = new Intent(PLVideoViewActivity.this, PrivateMsgActivity.class);
                    intent.putExtra("fromUserId", userlistId);
                    intent.putExtra("iconUrl", userlistHeadurl);
                    intent.putExtra("nickName", userlistName);
                    startActivity(intent);
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
        exitChatRoom();//退出聊天室
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
        TextView textView_pri_msg = (TextView) anchorView.findViewById(R.id.tv_anchor_private_msg);
        textView_pri_msg.setOnClickListener(anchorListen);
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
                    SharedPreferences sharedPreferences_user = getSharedPreferences("isLogin", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences_user.edit();
                    editor.putString("ChatroomId", ChatroomId);
                    editor.commit();
                    intent_userhomepage.putExtra("ChatroomId", ChatroomId);
                    startActivity(intent_userhomepage);
                    popupWindow_livingroom_anchor.dismiss();
                    finish();
                    break;
                case R.id.ll_add_follow:
                    addFollow();
                    break;
                case R.id.tv_anchor_private_msg:
                    Intent intent = new Intent(PLVideoViewActivity.this, PrivateMsgActivity.class);
                    intent.putExtra("fromUserId", ChatroomId);
                    intent.putExtra("iconUrl", yHeadimageUrl);
                    intent.putExtra("nickName", yNickName);
                    startActivity(intent);
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
        select_only = true;

        //全屏动画

        button_sendGift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                giftWenBig.setVisibility(View.VISIBLE);
//                giftWenBig.setBackgroundResource(R.drawable.gift_wen);
//                final AnimationDrawable anim_wen_big = (AnimationDrawable) giftWenBig.getBackground();
//                anim_wen_big.start();

            }
        });

        //666开始
        final ImageView imageView_gift_lll = (ImageView) gift_view1.findViewById(R.id.gift_png_lll);
        //5.6 只有设置动画背景不同，其他代码都是重复的，可以考虑之后替换，精简代码
        //最多可以同时5个动画，再多就oom了
        final ImageView imageView_gift_livingroom_lll = (ImageView) gift_view1.findViewById(R.id.gift_livingroom_lll);
        final LinearLayout linearLayout_lll = (LinearLayout) gift_view1.findViewById(R.id.ll_gift_lll);
        final TextView textView_lll = (TextView) gift_view1.findViewById(R.id.gift_bg_exp_lll);
        linearLayout_lll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView_gift_lll.setBackgroundResource(R.drawable.gift_666);
                final AnimationDrawable anim_lll = (AnimationDrawable) imageView_gift_lll.getBackground();
                if ((!linearLayout_lll.isSelected()) && select_only) {

                    linearLayout_lll.setSelected(true);
                    textView_lll.setSelected(true);
                    textView_lll.setTextColor(getResources().getColor(R.color.black));
                    imageView_gift_livingroom_lll.setVisibility(View.GONE);
                    imageView_gift_lll.setVisibility(View.VISIBLE);
                    anim_lll.start();
                    select_only = false;
                } else if (linearLayout_lll.isSelected()) {
                    imageView_gift_lll.setBackgroundColor(getResources().getColor(R.color.transparent));
                    linearLayout_lll.setSelected(false);
                    textView_lll.setSelected(false);
                    textView_lll.setTextColor(getResources().getColor(R.color.white));
                    imageView_gift_livingroom_lll.setVisibility(View.VISIBLE);
                    imageView_gift_lll.setVisibility(View.GONE);
                    anim_lll.stop();
                    select_only = true;
                }
            }
        });
        //666结束

        //棒棒糖开始
        final ImageView imageView_gift_bbt = (ImageView) gift_view1.findViewById(R.id.gift_png_bbt);

        final ImageView imageView_gift_livingroom_bbt = (ImageView) gift_view1.findViewById(R.id.gift_livingroom_bbt);
        final LinearLayout linearLayout_bbt = (LinearLayout) gift_view1.findViewById(R.id.ll_gift_bbt);
        final TextView textView_bbt = (TextView) gift_view1.findViewById(R.id.gift_bg_exp);
        linearLayout_bbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView_gift_bbt.setBackgroundResource(R.drawable.gift_bangbangtang);
                final AnimationDrawable anim_bbt = (AnimationDrawable) imageView_gift_bbt.getBackground();
                if ((!linearLayout_bbt.isSelected()) && select_only) {

                    linearLayout_bbt.setSelected(true);
                    textView_bbt.setSelected(true);
                    textView_bbt.setTextColor(getResources().getColor(R.color.black));
                    imageView_gift_livingroom_bbt.setVisibility(View.GONE);
                    imageView_gift_bbt.setVisibility(View.VISIBLE);
                    anim_bbt.start();
                    select_only = false;
                } else if (linearLayout_bbt.isSelected()) {
                    imageView_gift_bbt.setBackgroundColor(getResources().getColor(R.color.transparent));
                    linearLayout_bbt.setSelected(false);
                    textView_bbt.setSelected(false);
                    textView_bbt.setTextColor(getResources().getColor(R.color.white));
                    imageView_gift_livingroom_bbt.setVisibility(View.VISIBLE);
                    imageView_gift_bbt.setVisibility(View.GONE);
                    anim_bbt.stop();
                    select_only = true;
                }
            }
        });
        //棒棒糖结束
        //蓝瘦香菇开始
        final ImageView imageView_gift_lsxg = (ImageView) gift_view1.findViewById(R.id.gift_png_lsxg);

        final ImageView imageView_gift_livingroom_lsxg = (ImageView) gift_view1.findViewById(R.id.gift_livingroom_lsxg);
        final LinearLayout linearLayout_lsxg = (LinearLayout) gift_view1.findViewById(R.id.ll_gift_lsxg);
        final TextView textView_lsxg = (TextView) gift_view1.findViewById(R.id.gift_bg_exp_lsxg);
        linearLayout_lsxg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView_gift_lsxg.setBackgroundResource(R.drawable.gift_lanshouxianggu);
                final AnimationDrawable anim_lsxg = (AnimationDrawable) imageView_gift_lsxg.getBackground();
                if (!linearLayout_lsxg.isSelected() && select_only) {

                    linearLayout_lsxg.setSelected(true);
                    textView_lsxg.setSelected(true);
                    textView_lsxg.setTextColor(getResources().getColor(R.color.black));
                    imageView_gift_livingroom_lsxg.setVisibility(View.GONE);
                    imageView_gift_lsxg.setVisibility(View.VISIBLE);
                    anim_lsxg.start();
                    select_only = false;
                } else if (linearLayout_lsxg.isSelected()) {
                    imageView_gift_lsxg.setBackgroundColor(getResources().getColor(R.color.transparent));
                    linearLayout_lsxg.setSelected(false);
                    textView_lsxg.setSelected(false);
                    textView_lsxg.setTextColor(getResources().getColor(R.color.white));
                    imageView_gift_livingroom_lsxg.setVisibility(View.VISIBLE);
                    imageView_gift_lsxg.setVisibility(View.GONE);
                    anim_lsxg.stop();
                    select_only = true;
                }
            }
        });//蓝瘦香菇结束

        //小红花开始
        final ImageView imageView_gift_xhh = (ImageView) gift_view1.findViewById(R.id.gift_png_xhh);

        final ImageView imageView_gift_livingroom_xhh = (ImageView) gift_view1.findViewById(R.id.gift_livingroom_xhh);
        final LinearLayout linearLayout_xhh = (LinearLayout) gift_view1.findViewById(R.id.ll_gift_xhh);
        final TextView textView_xhh = (TextView) gift_view1.findViewById(R.id.gift_bg_exp_xhh);
        linearLayout_xhh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView_gift_xhh.setBackgroundResource(R.drawable.gift_xiaohonghua);
                final AnimationDrawable anim_xhh = (AnimationDrawable) imageView_gift_xhh.getBackground();
                if (!linearLayout_xhh.isSelected() && select_only) {

                    linearLayout_xhh.setSelected(true);
                    textView_xhh.setSelected(true);
                    textView_xhh.setTextColor(getResources().getColor(R.color.black));
                    imageView_gift_livingroom_xhh.setVisibility(View.GONE);
                    imageView_gift_xhh.setVisibility(View.VISIBLE);
                    anim_xhh.start();
                    select_only = false;
                } else if (linearLayout_xhh.isSelected()) {
                    imageView_gift_xhh.setBackgroundColor(getResources().getColor(R.color.transparent));
                    linearLayout_xhh.setSelected(false);
                    textView_xhh.setSelected(false);
                    textView_xhh.setTextColor(getResources().getColor(R.color.white));
                    imageView_gift_livingroom_xhh.setVisibility(View.VISIBLE);
                    imageView_gift_xhh.setVisibility(View.GONE);
                    anim_xhh.stop();
                    select_only = true;
                }
            }
        });//小红花结束

        //老司机开始
        final ImageView imageView_gift_lsj = (ImageView) gift_view1.findViewById(R.id.gift_png_lsj);

        final ImageView imageView_gift_livingroom_lsj = (ImageView) gift_view1.findViewById(R.id.gift_livingroom_lsj);
        final LinearLayout linearLayout_lsj = (LinearLayout) gift_view1.findViewById(R.id.ll_gift_lsj);
        final TextView textView_lsj = (TextView) gift_view1.findViewById(R.id.gift_bg_exp_lsj);
        linearLayout_lsj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView_gift_lsj.setBackgroundResource(R.drawable.gift_laosiji);
                final AnimationDrawable anim_lsj = (AnimationDrawable) imageView_gift_lsj.getBackground();
                if (!linearLayout_lsj.isSelected() && select_only) {

                    linearLayout_lsj.setSelected(true);
                    textView_lsj.setSelected(true);
                    textView_lsj.setTextColor(getResources().getColor(R.color.black));
                    imageView_gift_livingroom_lsj.setVisibility(View.GONE);
                    imageView_gift_lsj.setVisibility(View.VISIBLE);
                    anim_lsj.start();
                    select_only = false;
                } else if (linearLayout_lsj.isSelected()) {
                    imageView_gift_lsj.setBackgroundColor(getResources().getColor(R.color.transparent));
                    linearLayout_lsj.setSelected(false);
                    textView_lsj.setSelected(false);
                    textView_lsj.setTextColor(getResources().getColor(R.color.white));
                    imageView_gift_livingroom_lsj.setVisibility(View.VISIBLE);
                    imageView_gift_lsj.setVisibility(View.GONE);
                    anim_lsj.stop();
                    select_only = true;
                }
            }
        });//老司机结束
        //皮皮虾开始
        final ImageView imageView_gift_ppx = (ImageView) gift_view1.findViewById(R.id.gift_png_ppx);

        final ImageView imageView_gift_livingroom_ppx = (ImageView) gift_view1.findViewById(R.id.gift_livingroom_ppx);
        final LinearLayout linearLayout_ppx = (LinearLayout) gift_view1.findViewById(R.id.ll_gift_ppx);
        final TextView textView_ppx = (TextView) gift_view1.findViewById(R.id.gift_bg_exp_ppx);
        linearLayout_ppx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView_gift_ppx.setBackgroundResource(R.drawable.gift_pipixia);
                final AnimationDrawable anim_ppx = (AnimationDrawable) imageView_gift_ppx.getBackground();
                if (!linearLayout_ppx.isSelected() && select_only) {
                    select_only = false;
                    linearLayout_ppx.setSelected(true);
                    textView_ppx.setSelected(true);
                    textView_ppx.setTextColor(getResources().getColor(R.color.black));
                    imageView_gift_livingroom_ppx.setVisibility(View.GONE);
                    imageView_gift_ppx.setVisibility(View.VISIBLE);
                    anim_ppx.start();
                } else if (linearLayout_ppx.isSelected()) {
                    imageView_gift_ppx.setBackgroundColor(getResources().getColor(R.color.transparent));
                    linearLayout_ppx.setSelected(false);
                    textView_ppx.setSelected(false);
                    textView_ppx.setTextColor(getResources().getColor(R.color.white));
                    imageView_gift_livingroom_ppx.setVisibility(View.VISIBLE);
                    imageView_gift_ppx.setVisibility(View.GONE);
                    anim_ppx.stop();
                    select_only = true;
                }
            }
        });//皮皮虾结束
        //坑开始
        final ImageView imageView_gift_k = (ImageView) gift_view1.findViewById(R.id.gift_png_k);

        final ImageView imageView_gift_livingroom_k = (ImageView) gift_view1.findViewById(R.id.gift_livingroom_k);
        final LinearLayout linearLayout_k = (LinearLayout) gift_view1.findViewById(R.id.ll_gift_k);
        final TextView textView_k = (TextView) gift_view1.findViewById(R.id.gift_bg_exp_k);
        linearLayout_k.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView_gift_k.setBackgroundResource(R.drawable.gift_keng);
                final AnimationDrawable anim_k = (AnimationDrawable) imageView_gift_k.getBackground();
                if (!linearLayout_k.isSelected() && select_only) {
                    select_only = false;
                    linearLayout_k.setSelected(true);
                    textView_k.setSelected(true);
                    textView_k.setTextColor(getResources().getColor(R.color.black));
                    imageView_gift_livingroom_k.setVisibility(View.GONE);
                    imageView_gift_k.setVisibility(View.VISIBLE);
                    anim_k.start();
                } else if (linearLayout_k.isSelected()) {
                    imageView_gift_k.setBackgroundColor(getResources().getColor(R.color.transparent));
                    linearLayout_k.setSelected(false);
                    textView_k.setSelected(false);
                    textView_k.setTextColor(getResources().getColor(R.color.white));
                    imageView_gift_livingroom_k.setVisibility(View.VISIBLE);
                    imageView_gift_k.setVisibility(View.GONE);
                    anim_k.stop();
                    select_only = true;
                }
            }
        });//坑结束
        //吻开始
        final ImageView imageView_gift_w = (ImageView) gift_view1.findViewById(R.id.gift_png_w);

        final ImageView imageView_gift_livingroom_w = (ImageView) gift_view1.findViewById(R.id.gift_livingroom_w);
        final LinearLayout linearLayout_w = (LinearLayout) gift_view1.findViewById(R.id.ll_gift_w);
        final TextView textView_w = (TextView) gift_view1.findViewById(R.id.gift_bg_exp_w);
        linearLayout_w.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView_gift_w.setBackgroundResource(R.drawable.gift_wen);
                final AnimationDrawable anim_w = (AnimationDrawable) imageView_gift_w.getBackground();
                if (!linearLayout_w.isSelected() && select_only) {
                    select_only = false;
                    linearLayout_w.setSelected(true);
                    textView_w.setSelected(true);
                    textView_w.setTextColor(getResources().getColor(R.color.black));
                    imageView_gift_livingroom_w.setVisibility(View.GONE);
                    imageView_gift_w.setVisibility(View.VISIBLE);
                    anim_w.start();
                } else if (linearLayout_w.isSelected()) {
                    imageView_gift_w.setBackgroundColor(getResources().getColor(R.color.transparent));
                    linearLayout_w.setSelected(false);
                    textView_w.setSelected(false);
                    textView_w.setTextColor(getResources().getColor(R.color.white));
                    imageView_gift_livingroom_w.setVisibility(View.VISIBLE);
                    imageView_gift_w.setVisibility(View.GONE);
                    anim_w.stop();
                    select_only = true;
                }
            }
        });//吻结束
        //要抱抱开始
        final ImageView imageView_gift_ybb = (ImageView) gift_view1.findViewById(R.id.gift_png_ybb);

        final ImageView imageView_gift_livingroom_ybb = (ImageView) gift_view1.findViewById(R.id.gift_livingroom_ybb);
        final LinearLayout linearLayout_ybb = (LinearLayout) gift_view1.findViewById(R.id.ll_gift_ybb);
        final TextView textView_ybb = (TextView) gift_view1.findViewById(R.id.gift_bg_exp_ybb);
        linearLayout_ybb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView_gift_ybb.setBackgroundResource(R.drawable.gift_yaobaobao);
                final AnimationDrawable anim_ybb = (AnimationDrawable) imageView_gift_ybb.getBackground();
                if (!linearLayout_ybb.isSelected() && select_only) {
                    select_only = false;
                    linearLayout_ybb.setSelected(true);
                    textView_ybb.setSelected(true);
                    textView_ybb.setTextColor(getResources().getColor(R.color.black));
                    imageView_gift_livingroom_ybb.setVisibility(View.GONE);
                    imageView_gift_ybb.setVisibility(View.VISIBLE);
                    anim_ybb.start();
                } else if (linearLayout_ybb.isSelected()) {
                    imageView_gift_ybb.setBackgroundColor(getResources().getColor(R.color.transparent));
                    linearLayout_ybb.setSelected(false);
                    textView_ybb.setSelected(false);
                    textView_ybb.setTextColor(getResources().getColor(R.color.white));
                    imageView_gift_livingroom_ybb.setVisibility(View.VISIBLE);
                    imageView_gift_ybb.setVisibility(View.GONE);
                    anim_ybb.stop();
                    select_only = true;
                }
            }
        });//要抱抱结束
        //么么哒开始
        final ImageView imageView_gift_mmd = (ImageView) gift_view2.findViewById(R.id.gift_png_mmd);

        final ImageView imageView_gift_livingroom_mmd = (ImageView) gift_view2.findViewById(R.id.gift_livingroom_mmd);
        final LinearLayout linearLayout_mmd = (LinearLayout) gift_view2.findViewById(R.id.ll_gift_mmd);
        final TextView textView_mmd = (TextView) gift_view2.findViewById(R.id.gift_bg_exp_mmd);
        linearLayout_mmd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView_gift_mmd.setBackgroundResource(R.drawable.gift_memeda);
                final AnimationDrawable anim_mmd = (AnimationDrawable) imageView_gift_mmd.getBackground();
                if (!linearLayout_mmd.isSelected() && select_only) {
                    linearLayout_mmd.setSelected(true);
                    textView_mmd.setSelected(true);
                    textView_mmd.setTextColor(getResources().getColor(R.color.black));
                    imageView_gift_livingroom_mmd.setVisibility(View.GONE);
                    imageView_gift_mmd.setVisibility(View.VISIBLE);
                    anim_mmd.start();
                    select_only = false;
                } else if (linearLayout_mmd.isSelected()) {
                    imageView_gift_mmd.setBackgroundColor(getResources().getColor(R.color.transparent));
                    linearLayout_mmd.setSelected(false);
                    textView_mmd.setSelected(false);
                    textView_mmd.setTextColor(getResources().getColor(R.color.white));
                    imageView_gift_livingroom_mmd.setVisibility(View.VISIBLE);
                    imageView_gift_mmd.setVisibility(View.GONE);
                    anim_mmd.stop();
                    select_only = true;
                }
            }
        });//么么哒结束

        //爱的烟花开始
        final ImageView imageView_gift_adyh = (ImageView) gift_view2.findViewById(R.id.gift_png_adyh);

        final ImageView imageView_gift_livingroom_adyh = (ImageView) gift_view2.findViewById(R.id.gift_livingroom_adyh);
        final LinearLayout linearLayout_adyh = (LinearLayout) gift_view2.findViewById(R.id.ll_gift_adyh);
        final TextView textView_adyh = (TextView) gift_view2.findViewById(R.id.gift_bg_exp_adyh);
        linearLayout_adyh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView_gift_adyh.setBackgroundResource(R.drawable.gift_aideyanhua);
                final AnimationDrawable anim_adyh = (AnimationDrawable) imageView_gift_adyh.getBackground();
                if (!linearLayout_adyh.isSelected() && select_only) {
                    linearLayout_adyh.setSelected(true);
                    textView_adyh.setSelected(true);
                    textView_adyh.setTextColor(getResources().getColor(R.color.black));
                    imageView_gift_livingroom_adyh.setVisibility(View.GONE);
                    imageView_gift_adyh.setVisibility(View.VISIBLE);
                    anim_adyh.start();
                    select_only = false;
                } else if (linearLayout_adyh.isSelected()) {
                    imageView_gift_adyh.setBackgroundColor(getResources().getColor(R.color.transparent));
                    linearLayout_adyh.setSelected(false);
                    textView_adyh.setSelected(false);
                    textView_adyh.setTextColor(getResources().getColor(R.color.white));
                    imageView_gift_livingroom_adyh.setVisibility(View.VISIBLE);
                    imageView_gift_adyh.setVisibility(View.GONE);
                    anim_adyh.stop();
                    select_only = true;
                }
            }
        });//爱的烟花结束

        //玫瑰花丛开始
        final ImageView imageView_gift_mghc = (ImageView) gift_view2.findViewById(R.id.gift_png_mghc);

        final ImageView imageView_gift_livingroom_mghc = (ImageView) gift_view2.findViewById(R.id.gift_livingroom_mghc);
        final LinearLayout linearLayout_mghc = (LinearLayout) gift_view2.findViewById(R.id.ll_gift_mghc);
        final TextView textView_mghc = (TextView) gift_view2.findViewById(R.id.gift_bg_exp_mghc);
        linearLayout_mghc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView_gift_mghc.setBackgroundResource(R.drawable.gift_meiguihuacong);
                final AnimationDrawable anim_mghc = (AnimationDrawable) imageView_gift_mghc.getBackground();
                if (!linearLayout_mghc.isSelected() && select_only) {
                    linearLayout_mghc.setSelected(true);
                    textView_mghc.setSelected(true);
                    textView_mghc.setTextColor(getResources().getColor(R.color.black));
                    imageView_gift_livingroom_mghc.setVisibility(View.GONE);
                    imageView_gift_mghc.setVisibility(View.VISIBLE);
                    anim_mghc.start();
                    select_only = false;
                } else if (linearLayout_mghc.isSelected()) {
                    imageView_gift_mghc.setBackgroundColor(getResources().getColor(R.color.transparent));
                    linearLayout_mghc.setSelected(false);
                    textView_mghc.setSelected(false);
                    textView_mghc.setTextColor(getResources().getColor(R.color.white));
                    imageView_gift_livingroom_mghc.setVisibility(View.VISIBLE);
                    imageView_gift_mghc.setVisibility(View.GONE);
                    anim_mghc.stop();
                    select_only = true;
                }
            }
        });//玫瑰花丛结束

        //水晶鞋开始
        final ImageView imageView_gift_sjx = (ImageView) gift_view2.findViewById(R.id.gift_png_sjx);

        final ImageView imageView_gift_livingroom_sjx = (ImageView) gift_view2.findViewById(R.id.gift_livingroom_sjx);
        final LinearLayout linearLayout_sjx = (LinearLayout) gift_view2.findViewById(R.id.ll_gift_sjx);
        final TextView textView_sjx = (TextView) gift_view2.findViewById(R.id.gift_bg_exp_sjx);
        linearLayout_sjx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView_gift_sjx.setBackgroundResource(R.drawable.gift_shuijingxie);
                final AnimationDrawable anim_sjx = (AnimationDrawable) imageView_gift_sjx.getBackground();
                if (!linearLayout_sjx.isSelected() && select_only) {
                    linearLayout_sjx.setSelected(true);
                    textView_sjx.setSelected(true);
                    textView_sjx.setTextColor(getResources().getColor(R.color.black));
                    imageView_gift_livingroom_sjx.setVisibility(View.GONE);
                    imageView_gift_sjx.setVisibility(View.VISIBLE);
                    anim_sjx.start();
                    select_only = false;
                } else if (linearLayout_sjx.isSelected()) {
                    imageView_gift_sjx.setBackgroundColor(getResources().getColor(R.color.transparent));
                    linearLayout_sjx.setSelected(false);
                    textView_sjx.setSelected(false);
                    textView_sjx.setTextColor(getResources().getColor(R.color.white));
                    imageView_gift_livingroom_sjx.setVisibility(View.VISIBLE);
                    imageView_gift_sjx.setVisibility(View.GONE);
                    anim_sjx.stop();
                    select_only = true;
                }
            }
        });//水晶鞋结束

        //情定一钻开始
        final ImageView imageView_gift_qdyz = (ImageView) gift_view2.findViewById(R.id.gift_png_qdyz);

        final ImageView imageView_gift_livingroom_qdyz = (ImageView) gift_view2.findViewById(R.id.gift_livingroom_qdyz);
        final LinearLayout linearLayout_qdyz = (LinearLayout) gift_view2.findViewById(R.id.ll_gift_qdyz);
        final TextView textView_qdyz = (TextView) gift_view2.findViewById(R.id.gift_bg_exp_qdyz);
        linearLayout_qdyz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView_gift_mghc.setBackgroundResource(R.drawable.gift_qingdingyizuan);
                final AnimationDrawable anim_qdyz = (AnimationDrawable) imageView_gift_qdyz.getBackground();
                if (!linearLayout_qdyz.isSelected() && select_only) {
                    linearLayout_qdyz.setSelected(true);
                    textView_qdyz.setSelected(true);
                    textView_qdyz.setTextColor(getResources().getColor(R.color.black));
                    imageView_gift_livingroom_qdyz.setVisibility(View.GONE);
                    imageView_gift_qdyz.setVisibility(View.VISIBLE);
                    anim_qdyz.start();
                    select_only = false;
                } else if (linearLayout_qdyz.isSelected()) {
                    imageView_gift_qdyz.setBackgroundColor(getResources().getColor(R.color.transparent));
                    linearLayout_qdyz.setSelected(false);
                    textView_qdyz.setSelected(false);
                    textView_qdyz.setTextColor(getResources().getColor(R.color.white));
                    imageView_gift_livingroom_qdyz.setVisibility(View.VISIBLE);
                    imageView_gift_qdyz.setVisibility(View.GONE);
                    anim_qdyz.stop();
                    select_only = true;
                }
            }
        });//情定一钻结束

        //流星雨开始
        final ImageView imageView_gift_lxy = (ImageView) gift_view2.findViewById(R.id.gift_png_lxy);

        final ImageView imageView_gift_livingroom_lxy = (ImageView) gift_view2.findViewById(R.id.gift_livingroom_lxy);
        final LinearLayout linearLayout_lxy = (LinearLayout) gift_view2.findViewById(R.id.ll_gift_lxy);
        final TextView textView_lxy = (TextView) gift_view2.findViewById(R.id.gift_bg_exp_lxy);
        linearLayout_lxy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView_gift_lxy.setBackgroundResource(R.drawable.gift_liuxing);
                final AnimationDrawable anim_lxy = (AnimationDrawable) imageView_gift_lxy.getBackground();
                if (!linearLayout_lxy.isSelected() && select_only) {
                    linearLayout_lxy.setSelected(true);
                    textView_lxy.setSelected(true);
                    textView_lxy.setTextColor(getResources().getColor(R.color.black));
                    imageView_gift_livingroom_lxy.setVisibility(View.GONE);
                    imageView_gift_lxy.setVisibility(View.VISIBLE);
                    anim_lxy.start();
                    select_only = false;
                } else if (linearLayout_lxy.isSelected()) {
                    imageView_gift_lxy.setBackgroundColor(getResources().getColor(R.color.transparent));
                    linearLayout_lxy.setSelected(false);
                    textView_lxy.setSelected(false);
                    textView_lxy.setTextColor(getResources().getColor(R.color.white));
                    imageView_gift_livingroom_lxy.setVisibility(View.VISIBLE);
                    imageView_gift_lxy.setVisibility(View.GONE);
                    anim_lxy.stop();
                    select_only = true;
                }
            }
        });//流星雨结束

        //兰博基尼开始
        final ImageView imageView_gift_lbjn = (ImageView) gift_view2.findViewById(R.id.gift_png_lbjn);

        final ImageView imageView_gift_livingroom_lbjn = (ImageView) gift_view2.findViewById(R.id.gift_livingroom_lbjn);
        final LinearLayout linearLayout_lbjn = (LinearLayout) gift_view2.findViewById(R.id.ll_gift_lbjn);
        final TextView textView_lbjn = (TextView) gift_view2.findViewById(R.id.gift_bg_exp_lbjn);
        linearLayout_lbjn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView_gift_lbjn.setBackgroundResource(R.drawable.gift_lanbojini);
                final AnimationDrawable anim_lbjn = (AnimationDrawable) imageView_gift_lbjn.getBackground();
                if (!linearLayout_lbjn.isSelected() && select_only) {
                    linearLayout_lbjn.setSelected(true);
                    textView_lbjn.setSelected(true);
                    textView_lbjn.setTextColor(getResources().getColor(R.color.black));
                    imageView_gift_livingroom_lbjn.setVisibility(View.GONE);
                    imageView_gift_lbjn.setVisibility(View.VISIBLE);
                    anim_lbjn.start();
                    select_only = false;
                } else if (linearLayout_lbjn.isSelected()) {
                    imageView_gift_lbjn.setBackgroundColor(getResources().getColor(R.color.transparent));
                    linearLayout_lbjn.setSelected(false);
                    textView_lbjn.setSelected(false);
                    textView_lbjn.setTextColor(getResources().getColor(R.color.white));
                    imageView_gift_livingroom_lbjn.setVisibility(View.VISIBLE);
                    imageView_gift_lbjn.setVisibility(View.GONE);
                    anim_lbjn.stop();
                    select_only = true;
                }
            }
        });//兰博基尼结束
        //为爱起航开始
        final ImageView imageView_gift_waqh = (ImageView) gift_view2.findViewById(R.id.gift_png_waqh);

        final ImageView imageView_gift_livingroom_waqh = (ImageView) gift_view2.findViewById(R.id.gift_livingroom_waqh);
        final LinearLayout linearLayout_waqh = (LinearLayout) gift_view2.findViewById(R.id.ll_gift_waqh);
        final TextView textView_waqh = (TextView) gift_view2.findViewById(R.id.gift_bg_exp_waqh);
        linearLayout_waqh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView_gift_waqh.setBackgroundResource(R.drawable.gift_weiaiqihang);
                final AnimationDrawable anim_waqh = (AnimationDrawable) imageView_gift_waqh.getBackground();
                if (!linearLayout_waqh.isSelected() && select_only) {
                    linearLayout_waqh.setSelected(true);
                    textView_waqh.setSelected(true);
                    textView_waqh.setTextColor(getResources().getColor(R.color.black));
                    imageView_gift_livingroom_waqh.setVisibility(View.GONE);
                    imageView_gift_waqh.setVisibility(View.VISIBLE);
                    anim_waqh.start();
                    select_only = false;
                } else if (linearLayout_waqh.isSelected()) {
                    imageView_gift_waqh.setBackgroundColor(getResources().getColor(R.color.transparent));
                    linearLayout_waqh.setSelected(false);
                    textView_waqh.setSelected(false);
                    textView_waqh.setTextColor(getResources().getColor(R.color.white));
                    imageView_gift_livingroom_waqh.setVisibility(View.VISIBLE);
                    imageView_gift_waqh.setVisibility(View.GONE);
                    anim_waqh.stop();
                    select_only = true;
                }
            }
        });//为爱起航结束
        //浪漫城堡开始
        final ImageView imageView_gift_lmcb = (ImageView) gift_view2.findViewById(R.id.gift_png_lmcb);

        final ImageView imageView_gift_livingroom_lmcb = (ImageView) gift_view2.findViewById(R.id.gift_livingroom_lmcb);
        final LinearLayout linearLayout_lmcb = (LinearLayout) gift_view2.findViewById(R.id.ll_gift_lmcb);
        final TextView textView_lmcb = (TextView) gift_view2.findViewById(R.id.gift_bg_exp_lmcb);
        linearLayout_lmcb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView_gift_lmcb.setBackgroundResource(R.drawable.gift_langmanchengbao);
                final AnimationDrawable anim_lmcb = (AnimationDrawable) imageView_gift_lmcb.getBackground();
                if (!linearLayout_lmcb.isSelected() && select_only) {
                    linearLayout_lmcb.setSelected(true);
                    textView_lmcb.setSelected(true);
                    textView_lmcb.setTextColor(getResources().getColor(R.color.black));
                    imageView_gift_livingroom_lmcb.setVisibility(View.GONE);
                    imageView_gift_lmcb.setVisibility(View.VISIBLE);
                    anim_lmcb.start();
                    select_only = false;
                } else if (linearLayout_lmcb.isSelected()) {
                    imageView_gift_lmcb.setBackgroundColor(getResources().getColor(R.color.transparent));
                    linearLayout_lmcb.setSelected(false);
                    textView_lmcb.setSelected(false);
                    textView_lmcb.setTextColor(getResources().getColor(R.color.white));
                    imageView_gift_livingroom_lmcb.setVisibility(View.VISIBLE);
                    imageView_gift_lmcb.setVisibility(View.GONE);
                    anim_lmcb.stop();
                    select_only = true;
                }
            }
        });//浪漫城堡结束


//        圆点指示器
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

        ImageView imageView_wechat = (ImageView) livingshareView.findViewById(R.id.livingroom_share_wechat);
        imageView_wechat.setOnClickListener(shareListen);
        ImageView imageView_qq = (ImageView) livingshareView.findViewById(R.id.livingroom_share_qq);
        imageView_qq.setOnClickListener(shareListen);
        ImageView imageView_qqq = (ImageView) livingshareView.findViewById(R.id.livingroom_share_qqq);
        imageView_qqq.setOnClickListener(shareListen);
        ImageView imageView_sina = (ImageView) livingshareView.findViewById(R.id.livingroom_share_sina);
        imageView_sina.setOnClickListener(shareListen);
        ImageView imageView_friend = (ImageView) livingshareView.findViewById(R.id.livingroom_share_friend);
        imageView_friend.setOnClickListener(shareListen);
        ImageView imageView_copy = (ImageView) livingshareView.findViewById(R.id.livingroom_copyadress);
        imageView_copy.setOnClickListener(shareListen);

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

    //    UMImage image = new UMImage(PLVideoViewActivity.this, R.drawable.man);//资源文件
//    UMImage thumb =  new UMImage(this, R.drawable.female);
//    image.setThumb(thumb);
//    image.compressStyle = UMImage.CompressStyle.SCALE;//大小压缩，默认为大小压缩，适合普通很大的图
//    image.compressStyle = UMImage.CompressStyle.QUALITY;//质量压缩，适合长图的分享, 压缩格式设置：
//    image.compressFormat = Bitmap.CompressFormat.PNG;//用户分享透明背景的图片可以设置这种方式，但是qq好友，微信朋友圈，不支持透明背景图片，会变成黑色
    private View.OnClickListener shareListen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_livingshare_cancel:
                    popupWindow_living_share.dismiss();
                    break;
                case R.id.livingroom_share_wechat://暂缺分享链接
                    thumb = new UMImage(PLVideoViewActivity.this, R.drawable.logo_72);
                    UMWeb web_wechat = new UMWeb("http://www.bk5977.com/");
                    web_wechat.setTitle("骠客直播");//标题
                    web_wechat.setThumb(thumb);  //缩略图
                    web_wechat.setDescription("你丑你先睡，我美我直播");//描述
                    //分享到微信
                    new ShareAction(PLVideoViewActivity.this).setPlatform(SHARE_MEDIA.WEIXIN)
                            .withMedia(web_wechat)
                            .setCallback(umShareListener)
                            .share();
//                    ShareUtils.getInstance().showShareView(PLVideoViewActivity.this, new ShareVo("https://www.baidu.com/"), new ShareListener() {
//                        @Override
//                        public void onStart() {
//
//                        }
//
//                        @Override
//                        public void onSuccess() {
//
//                        }
//
//                        @Override
//                        public void onFailure() {
//
//                        }
//
//                        @Override
//                        public void onCancel() {
//
//                        }
//                    });
                    break;
                case R.id.livingroom_copyadress:
                    //创建一个新的文本clip对象
                    mClipData = ClipData.newPlainText("label", "http://www.bk5977.com/");
                    //把clip对象放在剪贴板中
                    mClipboardManager.setPrimaryClip(mClipData);
                    Toast.makeText(getApplicationContext(), "复制成功！",
                            Toast.LENGTH_SHORT).show();
                    break;
                case R.id.livingroom_share_qq:
                    thumb = new UMImage(PLVideoViewActivity.this, R.drawable.logo_72);
                    UMWeb web_qq = new UMWeb("http://www.bk5977.com/");
                    web_qq.setTitle("骠客直播");//标题
                    web_qq.setThumb(thumb);  //缩略图
                    web_qq.setDescription("你丑你先睡，我美我直播");//描述
                    new ShareAction(PLVideoViewActivity.this).setPlatform(SHARE_MEDIA.QQ)
                            .withMedia(web_qq)
                            .setCallback(umShareListener)
                            .share();

//                    ShareUtils.getInstance().showShareViewQQ(PLVideoViewActivity.this, new ShareVo(""), new ShareListener() {
//                        @Override
//                        public void onStart() {
//
//                        }
//
//                        @Override
//                        public void onSuccess() {
//
//                        }
//
//                        @Override
//                        public void onFailure() {
//
//                        }
//
//                        @Override
//                        public void onCancel() {
//
//                        }
//                    });
                    break;
                case R.id.livingroom_share_qqq:
                    thumb = new UMImage(PLVideoViewActivity.this, R.drawable.logo_72);
                    UMWeb web_qqq = new UMWeb("http://www.bk5977.com/");
                    web_qqq.setTitle("骠客直播");//标题
                    web_qqq.setThumb(thumb);  //缩略图
                    web_qqq.setDescription("你丑你先睡，我美我直播");//描述
                    new ShareAction(PLVideoViewActivity.this).setPlatform(SHARE_MEDIA.QZONE)
                            .withMedia(web_qqq)
                            .setCallback(umShareListener)
                            .share();

                    break;
                case R.id.livingroom_share_sina:
                    thumb = new UMImage(PLVideoViewActivity.this, R.drawable.logo_72);
                    UMWeb web_sina = new UMWeb("http://www.bk5977.com/");
                    web_sina.setTitle("骠客直播");//标题
                    web_sina.setThumb(thumb);  //缩略图
                    web_sina.setDescription("你丑你先睡，我美我直播");//描述
                    new ShareAction(PLVideoViewActivity.this).setPlatform(SHARE_MEDIA.SINA)
                            .withMedia(web_sina)
                            .setCallback(umShareListener)
                            .share();
//                    ShareUtils.getInstance().showShareViewSina(PLVideoViewActivity.this, new ShareVo(""), new ShareListener() {
//                        @Override
//                        public void onStart() {
//
//                        }
//
//                        @Override
//                        public void onSuccess() {
//
//                        }
//
//                        @Override
//                        public void onFailure() {
//
//                        }
//
//                        @Override
//                        public void onCancel() {
//
//                        }
//                    });
                    break;
                case R.id.livingroom_share_friend:
                    thumb = new UMImage(PLVideoViewActivity.this, R.drawable.logo_72);
                    UMWeb web_wc = new UMWeb("http://www.bk5977.com/");
                    web_wc.setTitle("骠客直播");//标题
                    web_wc.setThumb(thumb);  //缩略图
                    web_wc.setDescription("你丑你先睡，我美我直播");//描述
                    new ShareAction(PLVideoViewActivity.this).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                            .withMedia(web_wc)
                            .setCallback(umShareListener)
                            .share();
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);

    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            //分享开始的回调
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            Log.d("plat", "platform" + platform);

            Toast.makeText(PLVideoViewActivity.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(PLVideoViewActivity.this, platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
            if (t != null) {
                Log.d("throw", "throw:" + t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(PLVideoViewActivity.this, platform + " 分享取消了", Toast.LENGTH_SHORT).show();
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
