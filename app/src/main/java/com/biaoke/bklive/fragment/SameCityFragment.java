package com.biaoke.bklive.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.biaoke.bklive.R;
import com.biaoke.bklive.activity.PLVideoViewActivity;
import com.biaoke.bklive.activity.ShortVideoActivity;
import com.biaoke.bklive.adapter.liveItemAdapter_samecity;
import com.biaoke.bklive.bean.live_item;
import com.biaoke.bklive.message.Api;
import com.biaoke.bklive.websocket.WebSocketService;
import com.xlibs.xrv.LayoutManager.XStaggeredGridLayoutManager;
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

public class SameCityFragment extends Fragment {


    @BindView(R.id.xrecyclerview_samecity)
    XRecyclerView xrecyclerviewSamecity;
    Unbinder unbinder;
    private JSONObject jsonObject_content;
    private String useId;
    private String accessKey;
    private int page = 0;
    private List<live_item> recyclerDataList = new ArrayList<>();
    private View mHeaderView;
    private View mFooterView;
    private liveItemAdapter_samecity liveItemAdapter;
    private ImageView imageView;

    //websocket
    private Intent websocketServiceIntent;
    private AnimationDrawable anim;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_samecity, container, false);
        unbinder = ButterKnife.bind(this, view);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("isLogin", Context.MODE_PRIVATE);
        useId = sharedPreferences.getString("userId", "");
        accessKey = sharedPreferences.getString("AccessKey", "");

        jsonObject_content = new JSONObject();
        try {
            jsonObject_content.put("Protocol", "Explore");
            jsonObject_content.put("Cmd", "TongCheng");
            jsonObject_content.put("UserId", useId);
            jsonObject_content.put("Page", page + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getVideo(jsonObject_content.toString());

        mHeaderView = LayoutInflater.from(getActivity()).inflate(R.layout.header, null);
        imageView = (ImageView) mHeaderView.findViewById(R.id.headiv_found);
        imageView.setBackgroundResource(R.drawable.header_down_load);
        anim = (AnimationDrawable) imageView.getBackground();

        mFooterView = LayoutInflater.from(getActivity()).inflate(R.layout.footer, null);
        xrecyclerviewSamecity.addHeaderView(mHeaderView, 80);
        xrecyclerviewSamecity.addFootView(mFooterView, 50);
        //设置布局管理器,可以根据图片大小自适应
        XStaggeredGridLayoutManager xGridLayoutManager = new XStaggeredGridLayoutManager(2, XStaggeredGridLayoutManager.VERTICAL);
        xGridLayoutManager.setAutoMeasureEnabled(false);
        xrecyclerviewSamecity.setLayoutManager(xGridLayoutManager);
        //设置适配器
        liveItemAdapter = new liveItemAdapter_samecity(getActivity());
        liveItemAdapter.bind(recyclerDataList);
        liveItemAdapter.setOnItemClickListener(listen);
        xrecyclerviewSamecity.setAdapter(liveItemAdapter);
//        liveItemAdapter.notifyDataSetChanged();
//        xrecyclerviewSamecity.getChildAt(6);

        xrecyclerviewSamecity.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadMoreData();
            }
        });
        xrecyclerviewSamecity.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 0;
                anim.start();
                refreshData();
            }
        });
        return view;
    }


    /**
     * refresh
     */
    private void refreshData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initRefreshData();
                xrecyclerviewSamecity.refreshComplate();
            }
        }, 1500);
    }

    private void initRefreshData() {
        recyclerDataList.clear();
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
                xrecyclerviewSamecity.loadMoreComplate();
            }
        }, 2000);
    }

    private void initLoadMoreData() {
        page = page + 1;
        try {
            jsonObject_content.put("Protocol", "Explore");
            jsonObject_content.put("Cmd", "TongCheng");
            jsonObject_content.put("UserId", useId);
            jsonObject_content.put("Page", page + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getVideo(jsonObject_content.toString());
    }


    private liveItemAdapter_samecity.OnsamecityItemClickListener listen = new liveItemAdapter_samecity.OnsamecityItemClickListener() {
        @Override
        public void onItemClick(View view, int postion) {
            String type = recyclerDataList.get(postion).getType();
            Log.e("----视频类型----", type);
            String chatroomId = recyclerDataList.get(postion).getUserId();
            SharedPreferences sharedPreferences_chatroomId = getActivity().getSharedPreferences("isLogin", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor_chatroomId = sharedPreferences_chatroomId.edit();
            editor_chatroomId.putString("chatroomId", chatroomId);
            editor_chatroomId.commit();

//            getActivity().finish();//找错专用
            if (type.equals("live")) {
                Intent intent_video = new Intent(getActivity(), PLVideoViewActivity.class);
                intent_video.putExtra("path", recyclerDataList.get(postion).getVideoUrl());
                intent_video.putExtra("chatroomId", chatroomId);
                startActivity(intent_video);
                websocketServiceIntent = new Intent(getActivity(), WebSocketService.class);
                getActivity().startService(websocketServiceIntent);
                WebSocketService.webSocketConnect();
            } else {
                //跳转短视频视频播放，暂未开通
                Intent intent_shortvideo = new Intent(getActivity(), ShortVideoActivity.class);
                intent_shortvideo.putExtra("path", recyclerDataList.get(postion).getVideoUrl());
                startActivity(intent_shortvideo);
                //添加视频点击
                setPv(recyclerDataList.get(postion).getId());
//                Toast.makeText(getActivity(), "暂未开通", Toast.LENGTH_SHORT).show();
            }
        }
    };


    //获取同城视频信息
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
                                                        Log.d("成功的返回", response);
                                                        try {
                                                            JSONObject object = new JSONObject(response);
                                                            JSONArray jsonArray = new JSONArray(object.getString("Data"));
                                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                                JSONObject jsonobject = jsonArray.getJSONObject(i);
//                                                                String videoid = jsonobject.getString("Id");//获取视频的编号
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
                                                                if (type.equals("live")) {
                                                                    live_item liveItem = new live_item(UserId_video, NickName, IconUrl, Exp, Title, SnapshotUrl, videoUrl, Format, HV, type);
                                                                    recyclerDataList.add(liveItem);
                                                                } else {
                                                                    String videoid = jsonobject.getString("Id");//获取视频的编号
                                                                    live_item liveItem = new live_item(videoid, UserId_video, NickName, IconUrl, Exp, Title, SnapshotUrl, videoUrl, Format, HV, type);
                                                                    recyclerDataList.add(liveItem);
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

    //    设置播放次数
//    {"Protocol":"Video","Cmd":"SetPv","UserId":"1174","Id":"0","AccessKey":"bk5977"}
    private void setPv(String videoId) {
        JSONObject object_identification = new JSONObject();
        try {
            object_identification.put("Protocol", "Video");
            object_identification.put("Cmd", "SetPv");
            object_identification.put("UserId", useId);
            object_identification.put("Id", videoId);//视频的编号
            object_identification.put("AccessKey", accessKey);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtils.postString()
                .url(Api.ENCRYPT64)
                .content(object_identification.toString())
                .mediaType(MediaType.parse("application/json charset=utf-8"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        OkHttpUtils.postString()
                                .url(Api.LIVE_CAMERA)
                                .content(response)
                                .mediaType(MediaType.parse("application/json charset=utf-8"))
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
                                                .mediaType(MediaType.parse("application/json charset=utf-8"))
                                                .build()
                                                .execute(new StringCallback() {
                                                    @Override
                                                    public void onError(Call call, Exception e, int id) {

                                                    }

                                                    @Override
                                                    public void onResponse(String response, int id) {
                                                        try {
                                                            JSONObject jsonObject = new JSONObject(response);
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
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(WebSocketService.WEBSOCKET_ACTION);
        getActivity().registerReceiver(imReceiver, filter);

//        Log.e("samecityfragment", liveItemAdapter.getItemCount() + "");
//        if (liveItemAdapter.getItemCount() == 0) {
//            refreshData();
//            Log.d("samecityfragment","走了samecityfragment");
//        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        getActivity().unregisterReceiver(imReceiver);
    }

    private BroadcastReceiver imReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WebSocketService.WEBSOCKET_ACTION.equals(action)) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    String msg = bundle.getString("message");
                    if (!TextUtils.isEmpty(msg))
                        getMessage(msg);
                }

            }
        }
    };

    protected void getMessage(String msg) {
//        messageTv.setText("");
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
}
