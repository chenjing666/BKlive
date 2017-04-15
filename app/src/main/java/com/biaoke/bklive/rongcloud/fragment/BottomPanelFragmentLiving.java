package com.biaoke.bklive.rongcloud.fragment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.biaoke.bklive.R;
import com.biaoke.bklive.rongcloud.widge.InputPanel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by hasee on 2017/4/14.
 */

public class BottomPanelFragmentLiving extends Fragment {
    @BindView(R.id.iv_livingroom_comments)
    ImageView ivLivingroomComments;
    @BindView(R.id.iv_livingroom_private_message)
    ImageView ivLivingroomPrivateMessage;
    @BindView(R.id.living_livingroom_share)
    ImageView livingLivingroomShare;
    @BindView(R.id.iv_livingroom_pickup)
    ImageView ivLivingroomPickup;
    @BindView(R.id.iv_livingroom_music)
    ImageView ivLivingroomMusic;
    @BindView(R.id.input_panel)
    InputPanel inputPanel;
    Unbinder unbinder;


    private PopupWindow popupWindow_pickup, popupWindow_living_share, popupWindow_living_message, popupWindow_living_chat;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottombar_living, container);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.iv_livingroom_comments, R.id.iv_livingroom_private_message, R.id.living_livingroom_share, R.id.iv_livingroom_pickup, R.id.iv_livingroom_music})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_livingroom_comments:
                break;
            case R.id.iv_livingroom_private_message:
                messagePop();
                popupWindow_living_message.showAtLocation(view, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.living_livingroom_share:
                sharePopw();
                popupWindow_living_share.showAtLocation(view, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.iv_livingroom_pickup:
                pickupPop();
                popupWindow_pickup.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.BOTTOM,
                        (int) getActivity().getResources().getDimension(R.dimen.x172), 0);
                ivLivingroomPickup.setVisibility(View.GONE);
                break;
            case R.id.iv_livingroom_music:
                break;
        }
    }

    private void messagePop() {
        final View livingmessageView = LayoutInflater.from(getActivity()).inflate(R.layout.message_livingroom, null);
        popupWindow_living_message = new PopupWindow(livingmessageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        ListView listview = (ListView) livingmessageView.findViewById(R.id.listview_message_livingroom);
        String[] data = {"One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine"};
        listview.setAdapter(new ArrayAdapter<String>(livingmessageView.getContext(), android.R.layout.simple_list_item_1, data));
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    Toast.makeText(livingmessageView.getContext(), "哈哈", Toast.LENGTH_SHORT).show();
//                    chatPop();
//                    popupWindow_living_chat.showAtLocation(view, Gravity.BOTTOM, 0, 0);
                }
            }
        });

        popupWindow_living_message.setTouchable(true);
        popupWindow_living_message.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub
                return false;
            }
        });
        popupWindow_living_message.setBackgroundDrawable(new ColorDrawable(getActivity().getResources().getColor(R.color.transparent)));
    }

    private void chatPop() {
        final View livingshareView = LayoutInflater.from(getActivity()).inflate(R.layout.chat_livingroom, null);
        popupWindow_living_chat = new PopupWindow(livingshareView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow_living_chat.setTouchable(true);
        popupWindow_living_chat.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub
                return false;
            }
        });
        popupWindow_living_chat.setBackgroundDrawable(new ColorDrawable(getActivity().getResources().getColor(R.color.transparent)));
    }

    private void sharePopw() {
        final View livingshareView = LayoutInflater.from(getActivity()).inflate(R.layout.share_livingroom, null);
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
        popupWindow_living_share.setBackgroundDrawable(new ColorDrawable(getActivity().getResources().getColor(R.color.transparent)));
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

    private void pickupPop() {
        final View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.livingroom_popw_pickup, null);
        popupWindow_pickup = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        ImageView imageView_pick = (ImageView) contentView.findViewById(R.id.livingroom_pop_pickup);
        imageView_pick.setOnClickListener(pickupPop);
        popupWindow_pickup.setTouchable(true);
        popupWindow_pickup.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub
                ivLivingroomPickup.setVisibility(View.VISIBLE);
                return false;
            }
        });
//        popupWindow_pickup.setBackgroundDrawable(getResources().getDrawable(R.drawable.livingroom_edge));
        popupWindow_pickup.setBackgroundDrawable(new ColorDrawable(getActivity().getResources().getColor(R.color.transparent)));
    }

    private View.OnClickListener pickupPop = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.livingroom_pop_pickup:
                    popupWindow_pickup.dismiss();
                    ivLivingroomPickup.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };
}
