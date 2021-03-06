package com.biaoke.bklive.user.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.biaoke.bklive.R;

/**
 * Created by hasee on 2017/4/14.
 */

public class AnchorVideodataFragment extends Fragment {

    private String chatroomId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_anchor_videodata, container, false);
        SharedPreferences sharedPreferences_chatroomId = getActivity().getSharedPreferences("isLogin", Context.MODE_PRIVATE);
        chatroomId = sharedPreferences_chatroomId.getString("chatroomId", "");
        return view;
    }
}
