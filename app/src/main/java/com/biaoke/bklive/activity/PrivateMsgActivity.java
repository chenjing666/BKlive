package com.biaoke.bklive.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.biaoke.bklive.R;
import com.biaoke.bklive.adapter.PriMsgAdapter;
import com.biaoke.bklive.base.BaseActivity;
import com.biaoke.bklive.bean.PriMsg;
import com.biaoke.bklive.eventbus.Event_privatemessage;
import com.biaoke.bklive.message.AppConsts;

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
        mNickName = sharedPreferences_user.getString("mNickName", "");
        msgUser.setText(nickName);
        initMsgs();//放几条测试数据
        priMsgAdapter = new PriMsgAdapter(PrivateMsgActivity.this, R.layout.pri_msg_item, msgList);

    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void Event_privateMessage(Event_privatemessage privatemessage) {
        String privateMsg = privatemessage.getMsg();//json格式的信息
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

                break;
        }

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
