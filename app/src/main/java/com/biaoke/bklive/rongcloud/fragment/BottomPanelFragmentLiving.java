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
import android.widget.ImageView;
import android.widget.PopupWindow;

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
    private PopupWindow popupWindow_pickup;

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
                break;
            case R.id.living_livingroom_share:
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
