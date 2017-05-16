package com.biaoke.bklive.user.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.biaoke.bklive.R;
import com.biaoke.bklive.base.BaseActivity;
import com.biaoke.bklive.common.ActivityUtils;
import com.biaoke.bklive.message.Api;
import com.biaoke.bklive.message.AppConsts;
import com.biaoke.bklive.user.adapter.ShortVideoAdapter;
import com.biaoke.bklive.user.bean.LiveVideo_list;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;
import okhttp3.Call;
import okhttp3.MediaType;

public class MyVedioActivity extends BaseActivity {

    @BindView(R.id.recyclerView_myvideo)
    RecyclerView recyclerViewMyvideo;
    @BindView(R.id.refreshLayout_myvideo)
    PtrClassicFrameLayout refreshLayoutMyvideo;

    private ActivityUtils activityUtils;
    private ShortVideoAdapter shortVideoAdapter;
    private List<LiveVideo_list> shortVideoList = new ArrayList<>();

    private String accessKey;
    private int page = 0;
    private String useId;
    private JSONObject jsonObject_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_vedio);
        ButterKnife.bind(this);
        activityUtils = new ActivityUtils(this);
        SharedPreferences sharedPreferences_chatroomId = getSharedPreferences("isLogin", Context.MODE_PRIVATE);
        accessKey = sharedPreferences_chatroomId.getString("AccessKey", "");
        useId = sharedPreferences_chatroomId.getString("userId", "");
        //获取视频列表
        jsonObject_content = new JSONObject();
        try {
            jsonObject_content.put("Protocol", "Video");
            jsonObject_content.put("Cmd", "GetList");
            jsonObject_content.put("UserId", useId);
            jsonObject_content.put("Page", page + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getVideo(jsonObject_content.toString());

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setAutoMeasureEnabled(false);
        recyclerViewMyvideo.setLayoutManager(gridLayoutManager);
        Log.e("hhhheeee22", shortVideoList.size() + "");
//        shortVideoAdapter = new ShortVideoAdapter(this, shortVideoList);
//        shortVideoAdapter.bindData(shortVideoList);
//        shortVideoAdapter.setOnItemClickListener(videoListen);
//        recyclerViewMyvideo.setAdapter(shortVideoAdapter);
        initRefreshLayout();
    }

    private Handler mhandler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
//                    GridLayoutManager gridLayoutManager = new GridLayoutManager(MyVedioActivity.this, 2);
//                    gridLayoutManager.setAutoMeasureEnabled(false);
//                    recyclerViewMyvideo.setLayoutManager(gridLayoutManager);
                    Log.e("hhhheeee22", shortVideoList.size() + "");
                    shortVideoAdapter = new ShortVideoAdapter(MyVedioActivity.this, shortVideoList);
                    shortVideoAdapter.bindData(shortVideoList);
                    shortVideoAdapter.setOnItemClickListener(videoListen);
                    recyclerViewMyvideo.setAdapter(shortVideoAdapter);

                    break;
            }
        }
    };

    private void initRefreshLayout() {
        //初始化RefreshLayout
        //使用本对象作为key，用来记录上一次刷新的事件，如果两次下拉刷新间隔太近，不会触发刷新方法
        refreshLayoutMyvideo.setLastUpdateTimeRelateObject(this);
        //设置刷新时显示的背景色
        refreshLayoutMyvideo.setBackgroundResource(R.color.black);
        //关闭header所耗时长
        refreshLayoutMyvideo.setDurationToCloseHeader(1500);

        //实现刷新，加载回调
        refreshLayoutMyvideo.setPtrHandler(new PtrDefaultHandler2() {
            //加载更多时触发
            @Override
            public void onLoadMoreBegin(PtrFrameLayout frame) {
                loadMoreData();
            }

            //刷新时触发
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                page = 0;
                refreshData();
            }
        });
    }

    private ShortVideoAdapter.OnItemClickListener videoListen = new ShortVideoAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int postion) {
            activityUtils.showToast(postion + "点击");
        }

        @Override
        public void onItemLongClick(View view, int postion) {
            activityUtils.showToast("长按了");
        }
    };

    @Override
    protected String getPowerBarColors() {
        return AppConsts.POWER_BAR_GRAY;
    }

    /**
     * refresh
     */
    private void refreshData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initRefreshData();
                refreshLayoutMyvideo.refreshComplete();
            }
        }, 1500);
    }

    private void initRefreshData() {
        shortVideoList.clear();
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
                refreshLayoutMyvideo.refreshComplete();
            }
        }, 1500);
    }

    private void initLoadMoreData() {
        page = page + 1;
        try {
            jsonObject_content.put("Protocol", "Video");
            jsonObject_content.put("Cmd", "GetList");
            jsonObject_content.put("UserId", useId);
            jsonObject_content.put("Page", page + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("pagepage----", page + "");
        getVideo(jsonObject_content.toString());
    }

    @Override
    public void onStart() {
        super.onStart();
        //当前页面没数据，刷新
//        if (shortVideoAdapter.getItemCount() == 0) {
//            refreshLayoutMyvideo.autoRefresh();
//        }
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
                                                                String videoid = jsonobject.getString("Id");//获取视频的编号
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
                                                                LiveVideo_list liveVideo_list = new LiveVideo_list(videoid, UserId_video, Pv, Exp, HV, type, Title, SnapshotUrl, videoUrl, Format, PubTime);
                                                                shortVideoList.add(liveVideo_list);
                                                                Message message = new Message();
                                                                message.what = 1;
                                                                mhandler.sendMessage(message);
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
}
