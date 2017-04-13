package com.biaoke.bklive.rongcloud.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.biaoke.bklive.R;
import com.biaoke.bklive.rongcloud.widge.InputPanel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by hasee on 2017/4/12.
 */

public class BottomPanelFragment extends Fragment {

    @BindView(R.id.tv_sendmessage)
    TextView tvSendmessage;
    @BindView(R.id.iv_livingroom_gift)
    ImageView ivLivingroomGift;
    @BindView(R.id.iv_livingroom_share)
    ImageView ivLivingroomShare;
    @BindView(R.id.input_panel)
    InputPanel inputPanel;
    Unbinder unbinder;
    private PopupWindow popupWindow_living_share;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottombar, container);

        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    // handle back button
//                    Toast.makeText(getActivity(),"哈哈",Toast.LENGTH_SHORT).show();
                    if (inputPanel.getVisibility() == View.VISIBLE) {
                        inputPanel.setVisibility(View.GONE);
                        tvSendmessage.setVisibility(View.VISIBLE);
                    } else {
                        getActivity().onBackPressed();
                    }
                    return true;

                }

                return false;
            }
        });
    }

    @OnClick({R.id.tv_sendmessage, R.id.iv_livingroom_gift, R.id.iv_livingroom_share, R.id.input_panel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_sendmessage:
                tvSendmessage.setVisibility(View.GONE);
                inputPanel.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_livingroom_gift:


                break;
            case R.id.iv_livingroom_share:
                sharePopw();
                popupWindow_living_share.showAtLocation(view, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.input_panel:
                break;
        }
    }

//    public static boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == event.KEYCODE_BACK) {
//            Log.d("GameFragmet事件", "OK");
//        }
//        return true;
//    }

    private void sharePopw() {
        final View livingsgareView = LayoutInflater.from(getActivity()).inflate(R.layout.share_livingroom, null);
        popupWindow_living_share = new PopupWindow(livingsgareView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        Button button_cancel = (Button) livingsgareView.findViewById(R.id.btn_livingshare_cancel);
        button_cancel.setOnClickListener(shareListen);
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
