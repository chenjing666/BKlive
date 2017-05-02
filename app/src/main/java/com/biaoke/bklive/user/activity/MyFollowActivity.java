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
import com.biaoke.bklive.user.adapter.MyFollowAdapter;
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

public class MyFollowActivity extends BaseActivity {

    @BindView(R.id.back_follow)
    ImageView backFollow;
    @BindView(R.id.recyclerview_myfollow)
    XRecyclerView recyclerviewMyfollow;
    private String userId;
    private int page = 0;
    private View view_follow;
    private String msg_addFollow;
    private String addFollowId;
    private MyFollowAdapter myFollowAdapter;
    private List<MyFansBean> myFollowBeanList = new ArrayList<>();
    private String accessKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_follow);
        ButterKnife.bind(this);
        SharedPreferences sharedPreferences_user = getSharedPreferences("isLogin", MODE_PRIVATE);
        userId = sharedPreferences_user.getString("userId", "");
        accessKey = sharedPreferences_user.getString("AccessKey", "");
        getFollowList(page + "");
        XLinearLayoutManager linearLayoutManager = new XLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setAutoMeasureEnabled(false);
        recyclerviewMyfollow.setLayoutManager(linearLayoutManager);
        myFollowAdapter = new MyFollowAdapter(this, myFollowBeanList);
        myFollowAdapter.bindData(myFollowBeanList);
        myFollowAdapter.setOnItemSetFollowClickListener(SetFollowListen);
        recyclerviewMyfollow.setAdapter(myFollowAdapter);
        recyclerviewMyfollow.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 0;
                refreshData();
            }
        });
        recyclerviewMyfollow.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadMoreData();
            }
        });
    }


    private Handler handler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    Toast.makeText(MyFollowActivity.this, msg_addFollow, Toast.LENGTH_SHORT).show();
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

    private MyFollowAdapter.OnItemSetFollowClickListener SetFollowListen = new MyFollowAdapter.OnItemSetFollowClickListener() {
        @Override
        public void onItemSetFollowClick(View view, int postion) {
            view_follow = view;
            addFollowId = myFollowBeanList.get(postion).getUserId();
            if (!view_follow.isSelected()) {
                addFollow();
            } else {
                delFollow();
            }
        }
    };

    /**
     * load more
     */
    private void loadMoreData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initLoadMoreData();
                recyclerviewMyfollow.loadMoreComplate();
            }
        }, 2000);
    }

    private void initLoadMoreData() {
        page = page + 1;
        getFollowList(page + "");
    }

    /**
     * refresh
     */
    private void refreshData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initRefreshData();
                recyclerviewMyfollow.refreshComplate();
            }
        }, 2000);
    }

    private void initRefreshData() {
        myFollowBeanList.clear();
        getFollowList(page + "");
    }



    //    {"Protocol":"Fans","Cmd":"GetGuanZhuList","UserId":"1174","Page":"0"}
    private void getFollowList(String page) {
        JSONObject jsonObject_content = new JSONObject();
        try {
            jsonObject_content.put("Protocol", "Fans");
            jsonObject_content.put("Cmd", "GetGuanZhuList");
            jsonObject_content.put("UserId", userId);
            jsonObject_content.put("Page", page + "");
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
                                                        Log.d("成功的返回MyFollowActivity", response);
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
                                                                myFollowBeanList.add(myFansBean);
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

    @OnClick(R.id.back_follow)
    public void onClick() {
        finish();
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
}
