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
import com.biaoke.bklive.eventbus.Event_chatroom_errorMsg;
import com.biaoke.bklive.message.Api;
import com.biaoke.bklive.message.AppConsts;
import com.biaoke.bklive.utils.GlideUtis;
import com.biaoke.bklive.websocket.WebSocketService;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
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
    @BindView(R.id.myNickname)
    TextView myNickname;
    @BindView(R.id.online_people)
    TextView onlinePeople;
    @BindView(R.id.btn_follow)
    Button btnFollow;
    @BindView(R.id.livingroom_user_image)
    ImageView livingroomUserImage;
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
    private PopupWindow popupWindow_living_share, popupWindow_living_gift;
    private String IconUrl;
    private String Level;
    //view数组
    private List<View> viewList;
    private String userId;
    private String accessKey;
    private String msg_addFollow;
    private String Charm;


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
        glideUtis = new GlideUtis(this);
        glideUtis.glideCircle(IconUrl, livingroomUserImage, true);
        myNickname.setText(mNickName);
        tvCharmLivingShow.setText(Charm);
        tvBkId.setText(userId);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    joinInWeb();//获取长连接
                    Thread.sleep(100);
                    queryFollow();//查询是否关注
                    joinChatRoom();//加入聊天室
                    Thread.sleep(1000);
                    GetChatRomCount();//读取聊天室人数
                    GetChatRomList();//读取聊天室用户列表
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
                    Toast.makeText(PLVideoViewActivity.this, "AK认证失败，请重新登录", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    btnFollow.setVisibility(View.GONE);
                    break;
                case 3:
                    Toast.makeText(PLVideoViewActivity.this, msg_addFollow, Toast.LENGTH_SHORT).show();
                    btnFollow.setVisibility(View.GONE);
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
                                            String allUser = jsonArray_list.get(i).toString();//获取聊天室用户ID
                                            //下面显示用户头像列表操作

                                        }
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
                //获取聊天室人数
//                if (cmd.equals("GetChatRomCount")) {
////                    {"Protocol":"ChatRom","Cmd":"GetChatRomCount","Result":"1","ChatRomId":"10012","ChatRomCount":2}
//                    int ChatRomCount = object_chatMsg.getInt("ChatRomCount");
//                    onlinePeople.setText("观众：" + ChatRomCount);
//                } else if (cmd.equals("GetChatRomList")) {//获取聊天室用户ID
////                    {"Protocol":"ChatRom","Cmd":"GetChatRomList","Result":"1","ChatRomId":"10012","Data":["1001","1001"]}
////                    Data:
////                    "10014",//UserId
////                            "guest"
//                    String Result = object_chatMsg.getString("Result");
//                    if (Result.equals("1")) {
//                        JSONArray jsonArray_list = new JSONArray(object_chatMsg.getString("Data"));
//                        for (int i = 0; i < jsonArray_list.length(); i++) {
//                            String allUser = jsonArray_list.get(i).toString();//获取聊天室用户ID
//                            //下面显示用户头像列表操作
//
//                        }
//                    }
//                }
                if (cmd.equals("chat")) {
                    String NickName = object_chatMsg.getString("NickName");
                    String IconUrl = object_chatMsg.getString("IconUrl");
                    String Level = object_chatMsg.getString("Level");
                    String Msg_chat = object_chatMsg.getString("Msg");
                    livingroomChatListBean_chatmsg = new LivingroomChatListBean(IconUrl, Level, NickName, Msg_chat);
                    chatList.add(livingroomChatListBean_chatmsg);
                    Message msgg = new Message();
                    msgg.what = 0;
                    handler.sendMessage(msgg);
                } else if (cmd.equals("sys")) {
                    String Msg_sys = object_chatMsg.getString("Msg");
                    livingroomChatListBean_chatmsg = new LivingroomChatListBean("", "", "系统消息", Msg_sys);
                    chatList.add(livingroomChatListBean_chatmsg);
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

    @OnClick({R.id.btn_follow, R.id.living_close, R.id.charm_more, R.id.tv_sendmessage, R.id.iv_livingroom_gift, R.id.iv_livingroom_share, R.id.input_send})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_follow:
                addFollow();
                break;
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
                if (inputEditor.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, "消息为空", Toast.LENGTH_SHORT).show();
                } else {
                    chatRoomMessage();
                    inputEditor.getText().clear();
                }
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
}
