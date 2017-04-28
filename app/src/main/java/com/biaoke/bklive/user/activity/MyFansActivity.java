package com.biaoke.bklive.user.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.biaoke.bklive.R;
import com.biaoke.bklive.base.BaseActivity;
import com.biaoke.bklive.message.Api;
import com.biaoke.bklive.message.AppConsts;
import com.biaoke.bklive.user.adapter.MyFansAdapter;
import com.biaoke.bklive.user.bean.MyFansBean;
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
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.MediaType;

public class MyFansActivity extends BaseActivity {

    @BindView(R.id.back_fans)
    ImageView backFans;
    @BindView(R.id.recyclerview_myfans)
    XRecyclerView recyclerviewMyfans;
    private String userId;
    private int page = 0;
    private MyFansAdapter myFansAdapter;
    private List<MyFansBean> myFansBeanList = new ArrayList<>();
    private String addFollowId;
    private String accessKey;
    private String msg_addFollow;
    private View view_follow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_fans);
        ButterKnife.bind(this);
        SharedPreferences sharedPreferences_user = getSharedPreferences("isLogin", MODE_PRIVATE);
        userId = sharedPreferences_user.getString("userId", "");
        accessKey = sharedPreferences_user.getString("AccessKey", "");
        getFanList(page + "");
        XLinearLayoutManager linearLayoutManager = new XLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setAutoMeasureEnabled(false);
        recyclerviewMyfans.setLayoutManager(linearLayoutManager);
        myFansAdapter = new MyFansAdapter(this, myFansBeanList);
        myFansAdapter.bindData(myFansBeanList);
        myFansAdapter.setOnItemSetFollowClickListener(SetFollowListen);
        recyclerviewMyfans.setAdapter(myFansAdapter);
        recyclerviewMyfans.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 0;
                refreshData();
            }
        });
        recyclerviewMyfans.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadMoreData();
            }
        });
        queryFollow();
    }


    private Handler handler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    Toast.makeText(MyFansActivity.this, msg_addFollow, Toast.LENGTH_SHORT).show();
                    view_follow.setSelected(true);
                    break;
                case 1:
                    view_follow.setSelected(false);
                    break;
                case 2:
                    view_follow.setSelected(true);
                    break;
            }
        }
    };

    private MyFansAdapter.OnItemSetFollowClickListener SetFollowListen = new MyFansAdapter.OnItemSetFollowClickListener() {
        @Override
        public void onItemSetFollowClick(View view, int postion) {
            view_follow = view;
            addFollowId = myFansBeanList.get(postion).getUserId();
            if (!view_follow.isSelected()) {
                addFollow();
            } else {
                delFollow();
            }
        }
    };

    //查询是否关注
//{"Protocol":"Fans","Cmd":"IsFans","MastId":"1001","SlaveId":"1002","AccessKey":"bk5977"}
    private void queryFollow() {
        JSONObject jsonobject_follow = new JSONObject();
        try {
            jsonobject_follow.put("Protocol", "Fans");
            jsonobject_follow.put("Cmd", "IsFans");
            jsonobject_follow.put("MastId", addFollowId);
            jsonobject_follow.put("SlaveId", userId);
            jsonobject_follow.put("AccessKey", accessKey);
            Log.e("查询是否关注", jsonobject_follow.toString());//后放的id放不进去，艹
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtils.postString()
                .url(Api.ENCRYPT64)
                .content(jsonobject_follow.toString())
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        OkHttpUtils.postString()
                                .url(Api.FOLLOW)
                                .content(response)
                                .mediaType(MediaType.parse("application/json; charset=utf-8"))
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
                                                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                                                .build()
                                                .execute(new StringCallback() {
                                                    @Override
                                                    public void onError(Call call, Exception e, int id) {

                                                    }

                                                    @Override
                                                    public void onResponse(String response, int id) {
                                                        try {
                                                            JSONObject object_follow = new JSONObject(response);
                                                            String result_follow = object_follow.getString("Result");
                                                            if (result_follow.equals("0")) {
                                                                String msg_follow = object_follow.getString("Msg");
                                                                Message message_follow = new Message();
                                                                message_follow.what = 1;
                                                                handler.sendMessage(message_follow);
                                                            } else if (result_follow.equals("1")) {
                                                                boolean isFollow = object_follow.getBoolean("Data");
                                                                if (isFollow) {
                                                                    Message message_isfollow = new Message();
                                                                    message_isfollow.what = 2;
                                                                    handler.sendMessage(message_isfollow);
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


    //    取消关注
//    {"Protocol":"Fans","Cmd":"Del","MastId":"1001","SlaveId":"1002","AccessKey":"bk5977"}
    private void delFollow() {
        JSONObject jsonobject_addfollow = new JSONObject();
        try {
            jsonobject_addfollow.put("Protocol", "Fans");
            jsonobject_addfollow.put("Cmd", "Del");
            jsonobject_addfollow.put("MastId", addFollowId);
            jsonobject_addfollow.put("SlaveId", userId);
            jsonobject_addfollow.put("AccessKey", accessKey);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtils.postString()
                .url(Api.ENCRYPT64)
                .content(jsonobject_addfollow.toString())
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        OkHttpUtils.postString()
                                .url(Api.FOLLOW)
                                .content(response)
                                .mediaType(MediaType.parse("application/json; charset=utf-8"))
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
                                                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                                                .build()
                                                .execute(new StringCallback() {
                                                    @Override
                                                    public void onError(Call call, Exception e, int id) {

                                                    }

                                                    @Override
                                                    public void onResponse(String response, int id) {
                                                        try {
                                                            JSONObject object_follow = new JSONObject(response);
                                                            String result_follow = object_follow.getString("Result");
                                                            msg_addFollow = object_follow.getString("Msg");
                                                            if (result_follow.equals("1")) {
                                                                Message message_isfollow = new Message();
                                                                message_isfollow.what = 1;
                                                                handler.sendMessage(message_isfollow);
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

    //关注添加关注
//    {"Protocol":"Fans","Cmd":"Add","MastId":"1001","SlaveId":"1002","AccessKey":"bk5977"}
    private void addFollow() {
        JSONObject jsonobject_addfollow = new JSONObject();
        try {
            jsonobject_addfollow.put("Protocol", "Fans");
            jsonobject_addfollow.put("Cmd", "Add");
            jsonobject_addfollow.put("MastId", addFollowId);
            jsonobject_addfollow.put("SlaveId", userId);
            jsonobject_addfollow.put("AccessKey", accessKey);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtils.postString()
                .url(Api.ENCRYPT64)
                .content(jsonobject_addfollow.toString())
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        OkHttpUtils.postString()
                                .url(Api.FOLLOW)
                                .content(response)
                                .mediaType(MediaType.parse("application/json; charset=utf-8"))
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
                                                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                                                .build()
                                                .execute(new StringCallback() {
                                                    @Override
                                                    public void onError(Call call, Exception e, int id) {

                                                    }

                                                    @Override
                                                    public void onResponse(String response, int id) {
                                                        try {
                                                            JSONObject object_follow = new JSONObject(response);
                                                            String result_follow = object_follow.getString("Result");
                                                            msg_addFollow = object_follow.getString("Msg");
                                                            if (result_follow.equals("1")) {
                                                                Message message_isfollow = new Message();
                                                                message_isfollow.what = 0;
                                                                handler.sendMessage(message_isfollow);
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


    //    {"Protocol":"Fans","Cmd":"GetFansList","UserId":"1174","Page":"0"}
    private void getFanList(String page) {
        JSONObject jsonObject_content = new JSONObject();
        try {
            jsonObject_content.put("Protocol", "Fans");
            jsonObject_content.put("Cmd", "GetFansList");
            jsonObject_content.put("UserId", userId);
            jsonObject_content.put("Page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtils
                .postString()
                .url(Api.ENCRYPT64)
                .content(jsonObject_content.toString())
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d("失败的返回", e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
//                        Log.d("成功的返回", response);
//                        Okhttputils(Api.LOGIN,response);
                        OkHttpUtils.postString()
                                .url(Api.FOLLOW)
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
                                                        Log.d("成功的返回MyFansActivity", response);
                                                        try {
                                                            JSONObject object = new JSONObject(response);
                                                            JSONArray jsonArray = new JSONArray(object.getString("Data"));
                                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                                JSONObject jsonobject = jsonArray.getJSONObject(i);
                                                                String UserId = jsonobject.getString("UserId");
                                                                String NickName = jsonobject.getString("NickName");
                                                                String IconUrl = jsonobject.getString("IconUrl");
                                                                String Sex = jsonobject.getString("性别");
                                                                String Level = jsonobject.getString("Level");
                                                                String Signturw = jsonobject.getString("签名");
                                                                MyFansBean myFansBean = new MyFansBean(UserId, NickName, IconUrl, Sex, Level, Signturw);
                                                                myFansBeanList.add(myFansBean);
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

    /**
     * load more
     */
    private void loadMoreData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initLoadMoreData();
                recyclerviewMyfans.loadMoreComplate();
            }
        }, 2000);
    }

    private void initLoadMoreData() {
        page = page + 1;
        getFanList(page + "");
    }

    /**
     * refresh
     */
    private void refreshData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initRefreshData();
                recyclerviewMyfans.refreshComplate();
            }
        }, 2000);
    }

    private void initRefreshData() {
        myFansBeanList.clear();
        getFanList(page + "");
    }

    @Override
    protected String getPowerBarColors() {
        return AppConsts.POWER_BAR_BACKGROUND;
    }

    @OnClick(R.id.back_fans)
    public void onClick() {
        finish();
    }
}
