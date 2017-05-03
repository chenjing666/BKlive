package com.biaoke.bklive.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.biaoke.bklive.R;
import com.biaoke.bklive.adapter.liveItemAdapter;
import com.biaoke.bklive.base.BaseActivity;
import com.biaoke.bklive.bean.live_item;
import com.biaoke.bklive.message.Api;
import com.biaoke.bklive.message.AppConsts;
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
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.MediaType;

public class SearchActivity extends BaseActivity {

    @BindView(R.id.tv_cancle)
    TextView tvCancle;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.recyclerview_search_record)
    RecyclerView recyclerviewSearchRecord;
    @BindView(R.id.rl_search_hot)
    RelativeLayout rlSearchHot;
    @BindView(R.id.search_clear)
    LinearLayout searchClear;
    @BindView(R.id.xrecyclerview_search)
    XRecyclerView xrecyclerviewSearch;
    @BindView(R.id.zhanwei)
    RelativeLayout zhanwei;
    private String useId;
    private String accessKey;
    private int page = 0;
    private List<live_item> recyclerDataList = new ArrayList<>();
    private View mHeaderView;
    private View mFooterView;
    private liveItemAdapter liveItemAdapter;
    private ImageView imageView;

    //websocket
    private Intent websocketServiceIntent;
    private JSONObject jsonObject;
    private String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        SharedPreferences sharedPreferences = getSharedPreferences("isLogin", Context.MODE_PRIVATE);
        useId = sharedPreferences.getString("userId", "");
        accessKey = sharedPreferences.getString("AccessKey", "");

        mHeaderView = LayoutInflater.from(SearchActivity.this).inflate(R.layout.header, null);
        imageView = (ImageView) mHeaderView.findViewById(R.id.headiv_found);
        imageView.setBackgroundResource(R.drawable.header_down_load);
        AnimationDrawable anim = (AnimationDrawable) imageView.getBackground();
        anim.start();
        mFooterView = LayoutInflater.from(SearchActivity.this).inflate(R.layout.footer, null);
        xrecyclerviewSearch.addHeaderView(mHeaderView, 80);
        xrecyclerviewSearch.addFootView(mFooterView, 50);

        //设置布局管理器,可以根据图片大小自适应
        XStaggeredGridLayoutManager xGridLayoutManager = new XStaggeredGridLayoutManager(2, XStaggeredGridLayoutManager.VERTICAL);
        xGridLayoutManager.setAutoMeasureEnabled(false);
        xrecyclerviewSearch.setLayoutManager(xGridLayoutManager);
        //设置适配器
        liveItemAdapter = new liveItemAdapter(SearchActivity.this, xrecyclerviewSearch);
        liveItemAdapter.bind(recyclerDataList);
        liveItemAdapter.setOnItemClickListener(listen);
        xrecyclerviewSearch.setAdapter(liveItemAdapter);

        xrecyclerviewSearch.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadMoreData();
            }
        });
        xrecyclerviewSearch.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 0;
                refreshData();
            }
        });

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    recyclerviewSearchRecord.setVisibility(View.GONE);
                    rlSearchHot.setVisibility(View.GONE);
                    searchClear.setVisibility(View.GONE);
                    zhanwei.setVisibility(View.GONE);
                    jsonObject = new JSONObject();
                    content = etSearch.getText().toString();
                    try {
                        jsonObject.put("Protocol", "Search");
                        jsonObject.put("UserId", useId);
                        jsonObject.put("FindData", content);
                        jsonObject.put("Page", page + "");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    getSearch(jsonObject.toString());

                    return true;
                }

                return false;
            }
        });
    }


    private Handler handler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case 0:
//                    liveItemAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };


    private liveItemAdapter.OnItemClickListener listen = new liveItemAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int postion) {
            String type = recyclerDataList.get(postion).getType();
            Log.e("----视频类型----", type);
            String chatroomId = recyclerDataList.get(postion).getUserId();
            SharedPreferences sharedPreferences_chatroomId = getSharedPreferences("isLogin", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor_chatroomId = sharedPreferences_chatroomId.edit();
            editor_chatroomId.putString("chatroomId", chatroomId);
            editor_chatroomId.commit();

            if (type.equals("live")) {
                Intent intent_video = new Intent(SearchActivity.this, PLVideoViewActivity.class);
                intent_video.putExtra("path", recyclerDataList.get(postion).getVideoUrl());
                intent_video.putExtra("chatroomId", chatroomId);
                startActivity(intent_video);
                websocketServiceIntent = new Intent(SearchActivity.this, WebSocketService.class);
                startService(websocketServiceIntent);
                WebSocketService.webSocketConnect();
            } else {
                Intent intent_shortvideo = new Intent(SearchActivity.this, ShortVideoActivity.class);
                intent_shortvideo.putExtra("path", recyclerDataList.get(postion).getVideoUrl());
                startActivity(intent_shortvideo);
                //添加视频点击次数
                setPv(recyclerDataList.get(postion).getId());
            }
        }
    };

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


    /**
     * refresh
     */
    private void refreshData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initRefreshData();
                xrecyclerviewSearch.refreshComplate();
            }
        }, 2000);
    }

    private void initRefreshData() {
        recyclerDataList.clear();
        getSearch(jsonObject.toString());
    }

    /**
     * load more
     */
    private void loadMoreData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initLoadMoreData();
                xrecyclerviewSearch.loadMoreComplate();
            }
        }, 2000);
    }

    private void initLoadMoreData() {
        page = page + 1;
        try {
            jsonObject.put("Protocol", "Search");
            jsonObject.put("UserId", useId);
            jsonObject.put("FindData", content);
            jsonObject.put("Page", page + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getSearch(jsonObject.toString());
    }

    //    {"Protocol":"Search","UserId":"1001","FindData":"标题","Page":"0"}
    private void getSearch(String content) {
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
                                .url(Api.SEARCH)
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
                                                        Log.d("搜索页面", response);
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
                                                                live_item liveItem = new live_item(videoid, UserId_video, NickName, IconUrl, Exp, Title, SnapshotUrl, videoUrl, Format, HV, type);
                                                                recyclerDataList.add(liveItem);
                                                                Message message = new Message();
                                                                message.what = 0;
                                                                handler.sendMessage(message);
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
    protected String getPowerBarColors() {
        return AppConsts.POWER_BAR_BACKGROUND;
    }

    @OnClick(R.id.tv_cancle)
    public void onClick() {
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(WebSocketService.WEBSOCKET_ACTION);
        registerReceiver(imReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(imReceiver);
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
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
