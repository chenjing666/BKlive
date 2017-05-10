package com.biaoke.bklive.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.biaoke.bklive.R;
import com.biaoke.bklive.adapter.PriMsgAdapter;
import com.biaoke.bklive.base.BaseActivity;
import com.biaoke.bklive.bean.PriMsg;
import com.biaoke.bklive.eventbus.Event_privatemessage;
import com.biaoke.bklive.message.Api;
import com.biaoke.bklive.message.AppConsts;
import com.biaoke.bklive.websocket.WebSocketService;
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

public class PrivateMsgActivity extends BaseActivity {

    @BindView(R.id.back_message)
    ImageView backMessage;
    @BindView(R.id.msg_user)
    TextView msgUser;
    @BindView(R.id.listview_priMsg)
    ListView listviewPriMsg;
    @BindView(R.id.input_editor_priMsg)
    EditText inputEditorPriMsg;
    @BindView(R.id.input_send_priMsg)
    TextView inputSendPriMsg;
    @BindView(R.id.add_follow_priMsg)
    Button addFollowPriMsg;
    @BindView(R.id.ll_add_follow_priMsg)
    RelativeLayout llAddFollowPriMsg;

    private PriMsgAdapter priMsgAdapter;
    private List<PriMsg> msgList = new ArrayList<PriMsg>();
    private String mHeadImageUrl;
    private String iconUrl;
    private String userId;
    private String mNickName;
    private String fromUserId;
    private String msg_addFollow;
    private String accessKey;
    private String privatemessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_msg);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        fromUserId = getIntent().getStringExtra("fromUserId");
        iconUrl = getIntent().getStringExtra("iconUrl");
        String nickName = getIntent().getStringExtra("nickName");
        SharedPreferences sharedPreferences_user = getSharedPreferences("isLogin", MODE_PRIVATE);
        mHeadImageUrl = sharedPreferences_user.getString("mHeadimageUrl", "");
        userId = sharedPreferences_user.getString("userId", "");
        accessKey = sharedPreferences_user.getString("AccessKey", "");
        mNickName = sharedPreferences_user.getString("mNickName", "");
        msgUser.setText(nickName);
//        initMsgs();//放几条测试数据
        queryFollow();//查询是否关注
        priMsgAdapter = new PriMsgAdapter(PrivateMsgActivity.this, R.layout.pri_msg_item, msgList);
        listviewPriMsg.setAdapter(priMsgAdapter);
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    Toast.makeText(PrivateMsgActivity.this, msg_addFollow, Toast.LENGTH_SHORT).show();
                    break;
                case 1:

                    break;
                case 2:
                    llAddFollowPriMsg.setVisibility(View.GONE);
                    break;

            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
//        SharedPreferences sharedPreferences_message = getSharedPreferences("PriMsg", Context.MODE_APPEND);
//        privatemessage = sharedPreferences_message.getString(fromUserId, "");

    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void Event_privateMessage(Event_privatemessage privatemessage) {
        String privateMsg = privatemessage.getMsg();//json格式的信息
        Log.e("PrivateMsgActivity",privateMsg);
        try {
            JSONObject object_priMsg = new JSONObject(privateMsg);
            String priMsg_receive = object_priMsg.getString("Msg");
            String iconUrl_receive = object_priMsg.getString("IconUrl");
            if (!"".equals(priMsg_receive)) {
                PriMsg msg = new PriMsg(priMsg_receive, iconUrl_receive, PriMsg.TYPE_RECEIVED);
                msgList.add(msg);
                priMsgAdapter.notifyDataSetChanged(); // 当有新消息时，刷新ListView中的显示
                listviewPriMsg.setSelection(msgList.size()); // 将ListView定位到最后一行
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String getPowerBarColors() {
        return AppConsts.POWER_BAR_BACKGROUND;
    }

    @OnClick({R.id.back_message, R.id.input_send_priMsg, R.id.add_follow_priMsg})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_message:
                finish();
                break;
            case R.id.input_send_priMsg:
                String content = inputEditorPriMsg.getText().toString();
                if (!"".equals(content)) {
                    PriMsg msg = new PriMsg(content, mHeadImageUrl, PriMsg.TYPE_SENT);
                    msgList.add(msg);
                    priMsgAdapter.notifyDataSetChanged(); // 当有新消息时，刷新ListView中的显示
                    listviewPriMsg.setSelection(msgList.size()); // 将ListView定位到最后一行
                    sendMsgToSomeone(content);
                    inputEditorPriMsg.setText(""); // 清空输入框中的内容
                }
                break;
            case R.id.add_follow_priMsg:
                addFollow();
                llAddFollowPriMsg.setVisibility(View.GONE);
                break;
        }

    }

    //查询是否关注
//{"Protocol":"Fans","Cmd":"IsFans","MastId":"1001","SlaveId":"1002","AccessKey":"bk5977"}
    private void queryFollow() {
        JSONObject jsonobject_follow = new JSONObject();
        try {
            jsonobject_follow.put("Protocol", "Fans");
            jsonobject_follow.put("Cmd", "IsFans");
            jsonobject_follow.put("MastId", fromUserId);
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
            jsonobject_addfollow.put("MastId", fromUserId);
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
                                                                message_isfollow.what = 0;
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

    //发私信消息
    private void sendMsgToSomeone(String msg) {
        JSONObject object_sendPriMsg = new JSONObject();
        long time = System.currentTimeMillis();
        try {
            object_sendPriMsg.put("Protocol", "UserMsg");
            object_sendPriMsg.put("Cmd", "p2p");
            object_sendPriMsg.put("Msg", msg);
            object_sendPriMsg.put("ToUserId", fromUserId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //发消息的保存
        SharedPreferences sharedPreferences_message = getSharedPreferences("PriMsg", Context.MODE_APPEND);
        SharedPreferences.Editor editor_message = sharedPreferences_message.edit();
        editor_message.putString(fromUserId + "to", object_sendPriMsg.toString());
        editor_message.commit();
        OkHttpUtils.postString()
                .url(Api.ENCRYPT64)
                .content(object_sendPriMsg.toString())
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


    private void initMsgs() {
        PriMsg msg1 = new PriMsg("Hello guy.", iconUrl, PriMsg.TYPE_RECEIVED);
        msgList.add(msg1);
        PriMsg msg2 = new PriMsg("Hello. Who is that?", mHeadImageUrl, PriMsg.TYPE_SENT);
        msgList.add(msg2);
        PriMsg msg3 = new PriMsg("This is Tom. Nice talking to you. ", iconUrl, PriMsg.TYPE_RECEIVED);
        msgList.add(msg3);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
