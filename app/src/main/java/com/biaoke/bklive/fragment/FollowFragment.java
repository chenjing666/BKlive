package com.biaoke.bklive.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.biaoke.bklive.R;
import com.biaoke.bklive.adapter.FollowLiveAdapter;
import com.biaoke.bklive.bean.FollowLiveBean;
import com.biaoke.bklive.message.Api;
import com.xlibs.xrv.LayoutManager.XLinearLayoutManager;
import com.xlibs.xrv.listener.OnLoadMoreListener;
import com.xlibs.xrv.listener.OnRefreshListener;
import com.xlibs.xrv.view.XRecyclerView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.MediaType;


/**
 * Created by hasee on 2017/3/10.
 */

public class FollowFragment extends Fragment {
    @BindView(R.id.recyclerview_follow)
    XRecyclerView recyclerviewFollow;
    Unbinder unbinder;
    private JSONObject jsonObject_content;
    private String useId;
    private String accessKey;
    private int page = 0;
    private View mHeaderView;
    private View mFooterView;
    private ImageView imageView;
    private FollowLiveAdapter followLiveAdapter;
    private List<FollowLiveBean> mList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_follow, container, false);
        unbinder = ButterKnife.bind(this, view);


        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("isLogin", Context.MODE_PRIVATE);
        useId = sharedPreferences.getString("userId", "");
        accessKey = sharedPreferences.getString("AccessKey", "");
        jsonObject_content = new JSONObject();
        try {
            jsonObject_content.put("Protocol", "Explore");
            jsonObject_content.put("Cmd", "GuangZhu");
            jsonObject_content.put("UserId", useId);
            jsonObject_content.put("Page", page + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getVideo(jsonObject_content.toString());
        mHeaderView = LayoutInflater.from(getActivity()).inflate(R.layout.header, null);
        imageView = (ImageView) mHeaderView.findViewById(R.id.headiv_found);
        imageView.setBackgroundResource(R.drawable.header_down_load);
        AnimationDrawable anim = (AnimationDrawable) imageView.getBackground();
        anim.start();
        mFooterView = LayoutInflater.from(getActivity()).inflate(R.layout.footer, null);
        recyclerviewFollow.addHeaderView(mHeaderView, 80);
        recyclerviewFollow.addFootView(mFooterView, 50);
        XLinearLayoutManager xLinearLayoutManager = new XLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true);
        xLinearLayoutManager.setAutoMeasureEnabled(false);
        recyclerviewFollow.setLayoutManager(xLinearLayoutManager);
        followLiveAdapter = new FollowLiveAdapter(getActivity(), mList);
        followLiveAdapter.bind(mList);
        followLiveAdapter.setOnItemClickListener(liveListen);
        followLiveAdapter.setOnItemFollowClickListener(addfollowListen);
        recyclerviewFollow.setAdapter(followLiveAdapter);
        recyclerviewFollow.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });
        recyclerviewFollow.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

            }
        });
        return view;
    }

    //item点击事件
    private FollowLiveAdapter.OnItemClickListener liveListen = new FollowLiveAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int postion) {

        }
    };
    //添加喜欢点击事件
    private FollowLiveAdapter.OnItemFollowClickListener addfollowListen = new FollowLiveAdapter.OnItemFollowClickListener() {
        @Override
        public void onItemSetFollowClick(View view, int postion) {

        }
    };


    //获取视频用户信息
    private void getVideo(String content) {
        OkHttpUtils
                .postString()
                .url(Api.ENCRYPT64)
                .content(content)
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
                                .url(Api.VIDEO_LIVE)
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
//                                        Log.d("成功的返回", response);
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
                                                        Log.d("关注页面", response);
                                                        try {
                                                            JSONObject object = new JSONObject(response);
                                                            JSONArray jsonArray = new JSONArray(object.getString("Data"));
                                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                                JSONObject jsonobject = jsonArray.getJSONObject(i);
                                                                String videoid = jsonobject.getString("Id");//获取视频的编号
                                                                String UserId_video = jsonobject.getString("UserId");
                                                                String NickName = jsonobject.getString("NickName");
                                                                String IconUrl = jsonobject.getString("IconUrl");//用户头像
                                                                String Exp = jsonobject.getString("Exp");//热度 心形后面的数字
                                                                String Title = jsonobject.getString("Title");
                                                                String SnapshotUrl = jsonobject.getString("SnapshotUrl");//封面URL
                                                                String videoUrl = jsonobject.getString("VideoUrl");
                                                                String Format = jsonobject.getString("Format");
                                                                String HV = jsonobject.getString("HV");
                                                                String type = jsonobject.getString("Type");
//                                                                live_item liveItem = new live_item(videoid, UserId_video, NickName, IconUrl, Exp, Title, SnapshotUrl, videoUrl, Format, HV, type);
//                                                                recyclerDataList.add(liveItem);
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
