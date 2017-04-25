package com.biaoke.bklive.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.biaoke.bklive.R;
import com.biaoke.bklive.activity.PrivateMsgActivity;
import com.biaoke.bklive.adapter.PrivateMessageAdapter;
import com.biaoke.bklive.bean.PrivateMessageBean;
import com.biaoke.bklive.eventbus.Event_privatemessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;


/**
 * Created by hasee on 2017/4/1.
 */

public class MessageFragment extends Fragment {
    @BindView(R.id.recyclerview_private_message)
    RecyclerView recyclerviewPrivateMessage;
    Unbinder unbinder;
    private List<PrivateMessageBean> mList;
    private PrivateMessageBean privateMessageBean;
    private PrivateMessageAdapter privateMessageAdapter;
    private List<String> fromId = new ArrayList<>();
    private String msg;
    private String fromUserId;
    private String iconUrl;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_privatemessage, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Subscribe(threadMode = ThreadMode.MainThread)
    public void Event_privateMessage(Event_privatemessage privatemessage) {
        String privateMsg = privatemessage.getMsg();//json格式的信息
        try {
            JSONObject jsonObject = new JSONObject(privateMsg);
            iconUrl = jsonObject.getString("IconUrl");
            String Level = jsonObject.getString("等级");
            String NickName = jsonObject.getString("NickName");
            msg = jsonObject.getString("Msg");
            String Time = jsonObject.getString("Time");
            String Sex = jsonObject.getString("性别");

            fromUserId = jsonObject.getString("FromUserId");
            String ToUserId = jsonObject.getString("ToUserId");
            fromId.add(fromUserId);
            for (int i = 0; i < fromId.size(); i++) {
                if (!fromUserId.equals(fromId.get(i))) {
                    privateMessageBean = new PrivateMessageBean(iconUrl, Level, NickName, msg, Time, "?", Sex);
                    mList.add(privateMessageBean);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true);
                    linearLayoutManager.setAutoMeasureEnabled(false);
                    recyclerviewPrivateMessage.setLayoutManager(linearLayoutManager);
                    privateMessageAdapter = new PrivateMessageAdapter(getActivity(), mList);
                    privateMessageAdapter.bind(mList);
                    privateMessageAdapter.setOnItemClickListener(listen);
                    recyclerviewPrivateMessage.setAdapter(privateMessageAdapter);
                }
            }
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
            startActivity(intent);
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
