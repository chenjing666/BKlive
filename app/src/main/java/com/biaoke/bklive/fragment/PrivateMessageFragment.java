package com.biaoke.bklive.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.biaoke.bklive.R;
import com.biaoke.bklive.activity.PrivateMsgActivity;
import com.biaoke.bklive.adapter.PrivateMessageAdapter;
import com.biaoke.bklive.bean.PrivateMessageBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * Created by hasee on 2017/4/1.
 */

public class PrivateMessageFragment extends Fragment {
    @BindView(R.id.recyclerview_private_message)
    RecyclerView recyclerviewPrivateMessage;
    Unbinder unbinder;
    private List<PrivateMessageBean> mList = new ArrayList<>();
    private PrivateMessageBean privateMessageBean;
    private PrivateMessageAdapter privateMessageAdapter;
    private List<String> fromId = new ArrayList<>();
    private String msg;
    private String fromUserId;
    private String iconUrl;
    private String nickName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_privatemessage, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    Handler mhandler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                    linearLayoutManager.setAutoMeasureEnabled(false);
                    recyclerviewPrivateMessage.setLayoutManager(linearLayoutManager);
                    privateMessageAdapter = new PrivateMessageAdapter(getActivity(), mList);
                    privateMessageAdapter.bind(mList);
                    privateMessageAdapter.setOnItemClickListener(listen);
                    recyclerviewPrivateMessage.setAdapter(privateMessageAdapter);
                    break;
            }
        }
    };

    /**
     * 接受数据信息
     *
     * @param data
     */
    public  void setMag(String data) {
        Log.d("PrivateMessageFragment", data);
        //收到消息的保存
        SharedPreferences sharedPreferences_message = getActivity().getSharedPreferences("PriMsg", Context.MODE_APPEND);
        SharedPreferences.Editor editor_message = sharedPreferences_message.edit();
        editor_message.putString(fromUserId, data);
        editor_message.commit();
//        {"Protocol":"UserMsg","Cmd":"p2p","FromUserId":"1174","Level":"0","NickName":"游客MTE3NA==",
//                "IconUrl":"http:\/\/omy78z02r.bkt.clouddn.com\/icon-1174-up.jpg","ToUserId":"1183","Msg":"消息内容","Time":1493276389,"性别":"男"}
        try {
            JSONObject jsonObject = new JSONObject(data);
            iconUrl = jsonObject.getString("IconUrl");
            String Level = jsonObject.getString("Level");
            nickName = jsonObject.getString("NickName");
            msg = jsonObject.getString("Msg");
            int Time = jsonObject.getInt("Time");
            String Sex = jsonObject.getString("性别");

            fromUserId = jsonObject.getString("FromUserId");
            String ToUserId = jsonObject.getString("ToUserId");
//            for (int i = 0; i <fromId.size(); i++) {
//                if (!fromUserId.equals(fromId.get(i)))
//                    fromId.add(fromUserId);
//            }
//            for (int i = 0; i < fromId.size(); i++) {
                Log.d("PrivateMessageFragment", fromId.size() + "");
//                if (fromUserId.equals(fromId.get(i))) {
                privateMessageBean = new PrivateMessageBean(iconUrl, Level, nickName, msg, Time, "?", Sex);
                mList.add(privateMessageBean);
                Message message = new Message();
                message.what = 0;
                mhandler.sendMessage(message);
//                }
//            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private PrivateMessageAdapter.OnItemClickListener listen = new PrivateMessageAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int postion) {
            Intent intent = new Intent(getActivity(), PrivateMsgActivity.class);
            intent.putExtra("fromUserId", fromUserId);
            intent.putExtra("iconUrl", iconUrl);
            intent.putExtra("nickName", nickName);
            startActivity(intent);
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
