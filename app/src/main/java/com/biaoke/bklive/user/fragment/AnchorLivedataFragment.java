package com.biaoke.bklive.user.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.biaoke.bklive.R;
import com.biaoke.bklive.activity.ShortVideoActivity;
import com.biaoke.bklive.message.Api;
import com.biaoke.bklive.user.adapter.LiveNumAdapter;
import com.biaoke.bklive.user.bean.LiveVideo_list;
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
 * Created by hasee on 2017/4/14.
 */

public class AnchorLivedataFragment extends Fragment {
    @BindView(R.id.livenum)
    TextView livenum;
    Unbinder unbinder;
    @BindView(R.id.recyclerview_livenum)
    XRecyclerView recyclerviewLivenum;

    private LiveNumAdapter liveNumAdapter;
    private List<LiveVideo_list> livenumList = new ArrayList<>();
    private String chatroomId;
    private int page = 0;
    private View mFooterView;
    private JSONObject jsonObject_content;
    private String mLiveNum;
    private String mVideoNum;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_anchor_livedata, container, false);
        unbinder = ButterKnife.bind(this, view);
        SharedPreferences sharedPreferences_chatroomId = getActivity().getSharedPreferences("isLogin", Context.MODE_PRIVATE);
        chatroomId = sharedPreferences_chatroomId.getString("ChatroomId", "");
//获取用户点播信息
        JSONObject jsonObject_user = new JSONObject();
        try {
            jsonObject_user.put("Protocol", "UserInfo");
            jsonObject_user.put("Cmd", "GetAll");
            jsonObject_user.put("UserId", chatroomId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        UserInfoHttp(Api.ENCRYPT64, jsonObject_user.toString());
//获取视频列表
        jsonObject_content = new JSONObject();
        try {
            jsonObject_content.put("Protocol", "Video");
            jsonObject_content.put("Cmd", "GetList");
            jsonObject_content.put("UserId", chatroomId);
            jsonObject_content.put("Page", page + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getVideo(jsonObject_content.toString());
        XLinearLayoutManager linearLayoutManager = new XLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setAutoMeasureEnabled(false);
        mFooterView = LayoutInflater.from(getActivity()).inflate(R.layout.footer, null);
        recyclerviewLivenum.addFootView(mFooterView, 50);
        recyclerviewLivenum.setLayoutManager(linearLayoutManager);
        liveNumAdapter = new LiveNumAdapter(getActivity(), livenumList);
        liveNumAdapter.bindData(livenumList);
        liveNumAdapter.setOnItemClickListener(listen);
        recyclerviewLivenum.setAdapter(liveNumAdapter);
        recyclerviewLivenum.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 0;
                refreshData();
            }
        });
        recyclerviewLivenum.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadMoreData();
            }
        });

        return view;
    }


    Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    livenum.setText(mVideoNum);//视频点播次数
                    break;

            }
        }
    };

    private LiveNumAdapter.OnItemClickListener listen = new LiveNumAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int postion) {
            Intent intent_shortvideo = new Intent(getActivity(), ShortVideoActivity.class);
            intent_shortvideo.putExtra("path", livenumList.get(postion).getVideoUrl());
            startActivity(intent_shortvideo);
        }
    };


    /**
     * refresh
     */
    private void refreshData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initRefreshData();
                recyclerviewLivenum.refreshComplate();
            }
        }, 2000);
    }

    private void initRefreshData() {
        livenumList.clear();
        getVideo(jsonObject_content.toString());
    }


    /**
     * load more
     */
    private void loadMoreData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initLoadMoreData();
                recyclerviewLivenum.loadMoreComplate();
            }
        }, 2000);
    }

    private void initLoadMoreData() {
        page = page + 1;
        try {
            jsonObject_content.put("Protocol", "Video");
            jsonObject_content.put("Cmd", "GetList");
            jsonObject_content.put("UserId", chatroomId);
            jsonObject_content.put("Page", page + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("pagepage----", page + "");
        getVideo(jsonObject_content.toString());
    }

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
                                .url(Api.VIDEOLIST)
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
                                                        Log.d("成功的返回", response);
                                                        try {
                                                            JSONObject object = new JSONObject(response);
                                                            JSONArray jsonArray = new JSONArray(object.getString("Data"));
                                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                                JSONObject jsonobject = jsonArray.getJSONObject(i);
                                                                String UserId_video = jsonobject.getString("UserId");
                                                                String Tag = jsonobject.getString("Tag");//搜索用的标签
                                                                String Pv = jsonobject.getString("Pv");//观看次数
                                                                String Exp = jsonobject.getString("Exp");//喜欢数
                                                                String HV = jsonobject.getString("HV");
                                                                String type = jsonobject.getString("Type");
                                                                String Title = jsonobject.getString("Title");
                                                                String SnapshotUrl = jsonobject.getString("SnapshotUrl");
                                                                String videoUrl = jsonobject.getString("VideoUrl");
                                                                String Format = jsonobject.getString("Format");
                                                                String PubTime = jsonobject.getString("PubTime");
                                                                LiveVideo_list liveVideo_list = new LiveVideo_list(UserId_video, Pv, Exp, HV, type, Title, SnapshotUrl, videoUrl, Format, PubTime);
                                                                livenumList.add(liveVideo_list);
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

    //获取用户信息
    public void UserInfoHttp(String url, String path) {
        OkHttpUtils
                .postString()
                .url(url)
                .content(path)
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
                                .url(Api.USERINFO_USER)
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
                                                        Log.d("成功===成功的返回", response);
                                                        try {
                                                            JSONObject jsonobject = new JSONObject(response);
                                                            mLiveNum = jsonobject.getString("直播");//直播次数
                                                            mVideoNum = jsonobject.getString("点播");//点播次数
                                                            Message message_userinfo = new Message();
                                                            message_userinfo.what = 0;
                                                            mHandler.sendMessage(message_userinfo);
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
}
