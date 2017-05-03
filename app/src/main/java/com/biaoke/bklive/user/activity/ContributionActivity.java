package com.biaoke.bklive.user.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.biaoke.bklive.R;
import com.biaoke.bklive.message.Api;
import com.biaoke.bklive.user.adapter.CharmBoarbAdapter;
import com.biaoke.bklive.user.bean.Charm_boardBean;
import com.biaoke.bklive.utils.GlideUtis;
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

public class ContributionActivity extends AppCompatActivity {


    @BindView(R.id.back_contribution)
    ImageView backContribution;
    @BindView(R.id.tv_contribution_charm)
    TextView tvContributionCharm;
    @BindView(R.id.tv_list_day)
    TextView tvListDay;
    @BindView(R.id.tv_list_totall)
    TextView tvListTotall;
    @BindView(R.id.contribution_first)
    ImageView contributionFirst;
    @BindView(R.id.tv_contribution_nickname)
    TextView tvContributionNickname;
    @BindView(R.id.iv_contribution_sex)
    ImageView ivContributionSex;
    @BindView(R.id.xrecyclerview_day_or_all)
    XRecyclerView xrecyclerviewDayOrAll;
    @BindView(R.id.contribution_level)
    TextView contributionLevel;
    @BindView(R.id.tv_charm_first)
    TextView tvCharmFirst;
    private String userId;
    private String mCharm;
    private GlideUtis glideUtis;
    private CharmBoarbAdapter charmBoarbAdapter;
    private List<Charm_boardBean> mList = new ArrayList<>();
    private int page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_contribution);
        ButterKnife.bind(this);
        glideUtis = new GlideUtis(this);
        SharedPreferences sharedPreferences_user = getSharedPreferences("isLogin", MODE_PRIVATE);
        userId = sharedPreferences_user.getString("userId", "");
        mCharm = sharedPreferences_user.getString("mCharm", "");
        tvContributionCharm.setText(mCharm + "魅力值");
        //默认显示日榜
        tvListDay.setSelected(true);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Protocol", "Fans");
            jsonObject.put("Cmd", "GetTopDay");
            jsonObject.put("UserId", userId);
            jsonObject.put("Page", page + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getCharmBoard(jsonObject.toString());
        XLinearLayoutManager linearLayoutManager = new XLinearLayoutManager(ContributionActivity.this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setAutoMeasureEnabled(false);
        xrecyclerviewDayOrAll.setLayoutManager(linearLayoutManager);
        charmBoarbAdapter = new CharmBoarbAdapter(ContributionActivity.this, mList);
        charmBoarbAdapter.bindData(mList);
        xrecyclerviewDayOrAll.setAdapter(charmBoarbAdapter);
        xrecyclerviewDayOrAll.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 0;
                if (tvListDay.isSelected()) {
                    refreshData();
                } else if (tvListTotall.isSelected()) {
                    refreshDataAll();
                }
            }
        });
        xrecyclerviewDayOrAll.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (tvListDay.isSelected()) {
                    loadMoreData();
                } else if (tvListTotall.isSelected()) {
                    loadMoreDataAll();
                }
            }
        });
    }

    private Handler mhandler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    glideUtis.glideCircle(mList.get(0).getHeadUrl(), contributionFirst, true);
                    contributionLevel.setText(mList.get(0).getLevel());
                    tvContributionNickname.setText(mList.get(0).getNickName());
                    if (mList.get(0).getSex().equals("男")) {
                        ivContributionSex.setImageResource(R.drawable.man);
                    } else {
                        ivContributionSex.setImageResource(R.drawable.female);
                    }
                    tvCharmFirst.setText(mList.get(0).getCharm());


                    break;
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
                xrecyclerviewDayOrAll.loadMoreComplate();
            }
        }, 2000);
    }

    private void initLoadMoreData() {
        page = page + 1;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Protocol", "Fans");
            jsonObject.put("Cmd", "GetTopDay");
            jsonObject.put("UserId", userId);
            jsonObject.put("Page", page + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getCharmBoard(jsonObject.toString());
    }

    private void loadMoreDataAll() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initLoadMoreDataAll();
                xrecyclerviewDayOrAll.loadMoreComplate();
            }
        }, 2000);
    }

    private void initLoadMoreDataAll() {
        page = page + 1;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Protocol", "Fans");
            jsonObject.put("Cmd", "GetTopAll");
            jsonObject.put("UserId", userId);
            jsonObject.put("Page", page + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getCharmBoard(jsonObject.toString());
    }

    /**
     * refresh
     */
    private void refreshData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initRefreshData();
                xrecyclerviewDayOrAll.refreshComplate();
            }
        }, 2000);
    }

    private void initRefreshData() {
        mList.clear();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Protocol", "Fans");
            jsonObject.put("Cmd", "GetTopDay");
            jsonObject.put("UserId", userId);
            jsonObject.put("Page", page + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getCharmBoard(jsonObject.toString());
    }

    /**
     * refresh
     */
    private void refreshDataAll() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initRefreshDataAll();
                xrecyclerviewDayOrAll.refreshComplate();
            }
        }, 2000);
    }

    private void initRefreshDataAll() {
        mList.clear();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Protocol", "Fans");
            jsonObject.put("Cmd", "GetTopAll");
            jsonObject.put("UserId", userId);
            jsonObject.put("Page", page + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getCharmBoard(jsonObject.toString());
    }



    @OnClick({R.id.back_contribution, R.id.tv_list_day, R.id.tv_list_totall})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_contribution:
                finish();
                break;
            case R.id.tv_list_day:
                tvListTotall.setSelected(false);
                tvListDay.setSelected(true);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("Protocol", "Fans");
                    jsonObject.put("Cmd", "GetTopDay");
                    jsonObject.put("UserId", userId);
                    jsonObject.put("Page", page + "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                getCharmBoard(jsonObject.toString());
                break;
            case R.id.tv_list_totall:
                tvListTotall.setSelected(true);
                tvListDay.setSelected(false);
                JSONObject object = new JSONObject();
                try {
                    object.put("Protocol", "Fans");
                    object.put("Cmd", "GetTopAll");
                    object.put("UserId", userId);
                    object.put("Page", page + "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                getCharmBoard(object.toString());
                break;
        }
    }

    //    {"Protocol":"Fans","Cmd":"GetTopAll","UserId":"1174","Page":"0"}//-----------总排行
//    {"Protocol":"Fans","Cmd":"GetTopDay","UserId":"1174","Page":"0"}//-----------天排行
    private void getCharmBoard(String content) {
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
                                                        Log.d("成功===成功的返回", response);
                                                        try {
                                                            JSONObject object = new JSONObject(response);
                                                            JSONArray jsonArray = new JSONArray(object.getString("Data"));
                                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                                JSONObject jsonobject = jsonArray.getJSONObject(i);
                                                                String UserId = jsonobject.getString("UserId");
                                                                String NickName = jsonobject.getString("NickName");
                                                                String IconUrl = jsonobject.getString("IconUrl");//用户头像
                                                                String Sex = jsonobject.getString("性别");
                                                                String Level = jsonobject.getString("Level");
                                                                String Signture = jsonobject.getString("签名");
                                                                String Charm = jsonobject.getString("魅力");
                                                                Charm_boardBean charmBoardBean = new Charm_boardBean(UserId, NickName, IconUrl, Sex, Level, Signture, Charm);
                                                                mList.add(charmBoardBean);
                                                                Message message = new Message();
                                                                message.what = 0;
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
