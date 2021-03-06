package com.biaoke.bklive;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.biaoke.bklive.activity.MessageActivity;
import com.biaoke.bklive.activity.SWCameraStreamingActivity;
import com.biaoke.bklive.activity.SearchActivity;
import com.biaoke.bklive.activity.ShortVedioActivity;
import com.biaoke.bklive.base.BaseActivity;
import com.biaoke.bklive.bottombar.BottomBar;
import com.biaoke.bklive.fragment.FollowFragment;
import com.biaoke.bklive.fragment.FoundFragment;
import com.biaoke.bklive.fragment.GameFragment;
import com.biaoke.bklive.fragment.SameCityFragment;
import com.biaoke.bklive.message.Api;
import com.biaoke.bklive.message.AppConsts;
import com.biaoke.bklive.user.activity.ContributionActivity;
import com.biaoke.bklive.user.activity.DiamondActivity;
import com.biaoke.bklive.user.activity.EditUserActivity;
import com.biaoke.bklive.user.activity.IdentificationActivity;
import com.biaoke.bklive.user.activity.IncomeActivity;
import com.biaoke.bklive.user.activity.LevelActivity;
import com.biaoke.bklive.user.activity.LoginActivity;
import com.biaoke.bklive.user.activity.MyFansActivity;
import com.biaoke.bklive.user.activity.MyFollowActivity;
import com.biaoke.bklive.user.activity.MyLiveActivity;
import com.biaoke.bklive.user.activity.MyVedioActivity;
import com.biaoke.bklive.user.activity.SetActivity;
import com.biaoke.bklive.user.bean.User;
import com.biaoke.bklive.utils.GlideUtis;
import com.biaoke.bklive.websocket.WebSocketService;
import com.pkmmte.view.CircularImageView;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.MediaType;

public class MainActivity extends BaseActivity {
    @BindViews({R.id.tv_follow, R.id.tv_game, R.id.tv_found, R.id.tv_samecity})
    TextView[] textViews;
    @BindView(R.id.iv_message)
    ImageView ivMessage;
    @BindView(R.id.main_head)
    LinearLayout mainHead;
    @BindView(R.id.mine)
    TextView mine;
    @BindView(R.id.iv_search)
    ImageView ivSearch;
    @BindView(R.id.viewpager_main)
    ViewPager viewpagerMain;
    @BindView(R.id.ll_main)
    LinearLayout llMain;
    @BindView(R.id.tv_diamond_send)
    TextView tvDiamondSend;
    @BindView(R.id.diamond_send)
    TextView diamondSend;
    @BindView(R.id.iv_user_head)
    CircularImageView ivUserHead;
    @BindView(R.id.btn_edit)
    ImageView btnEdit;
    @BindView(R.id.tv_userName)
    TextView tvUserName;
    @BindView(R.id.user_id)
    TextView userId;
    @BindView(R.id.tv_live_num)
    TextView tvLiveNum;
    @BindView(R.id.tv_follow_num)
    TextView tvFollowNum;
    @BindView(R.id.tv_fan_num)
    TextView tvFanNum;
    @BindView(R.id.bk_contribution)
    LinearLayout bkContribution;
    @BindView(R.id.bk_income)
    LinearLayout bkIncome;
    @BindView(R.id.bk_mydiamond)
    LinearLayout bkMydiamond;
    @BindView(R.id.bk_level)
    LinearLayout bkLevel;
    @BindView(R.id.bk_vedio)
    LinearLayout bkVedio;
    @BindView(R.id.bk_identification)
    LinearLayout bkIdentification;
    @BindView(R.id.bk_set)
    LinearLayout bkSet;
    @BindView(R.id.ll_user)
    LinearLayout llUser;
    @BindView(R.id.ll_bottom_bar)
    BottomBar llBottomBar;
    @BindView(R.id.live_putvideo)
    ImageView livePutvideo;
    @BindView(R.id.sl_user)
    ScrollView slUser;
    @BindView(R.id.tv_follow)
    TextView tvFollow;
    @BindView(R.id.tv_game)
    TextView tvGame;
    @BindView(R.id.tv_found)
    TextView tvFound;
    @BindView(R.id.tv_samecity)
    TextView tvSamecity;
    @BindView(R.id.ll_main1)
    LinearLayout llMain1;
    @BindView(R.id.iv_sex_user)
    ImageView ivSexUser;
    @BindView(R.id.tv_user_signture)
    TextView tvUserSignture;
    @BindView(R.id.live_mine)
    LinearLayout liveMine;
    @BindView(R.id.follow_mine)
    LinearLayout followMine;
    @BindView(R.id.fans_mine)
    LinearLayout fansMine;
    private PopupWindow popupWindow_vedio, popupWindow_login;
    private ImageView imageView_qq;
    private String APPID = "1106047080";
    private String openID = null;
    //点击2次返回，退出程序
    private boolean isExit = false;
    User user = new User();
    private String UserId;
    private String mNickName;
    private String mLevel;
    private String mExperience;
    private String mCharm;
    private String mDiamond;
    private String mLiveNum;
    private String mVideoNum;
    private String mHeadimageUrl;
    private String mSex;
    private String mAge;
    private String mEmotion;
    private String mHometown;
    private String mWork;
    private String mFollow;
    private String mFans;
    private String mSignture;
    private String Msg;
    private SharedPreferences sharedPreferences_user;
    private GlideUtis glideUtis_header_user;
    private String currentTime = System.currentTimeMillis() + "";//用于更换头像地址
    private String msg_um;

    //websocket
    private Intent websocketServiceIntent;
    private String openid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        glideUtis_header_user = new GlideUtis(this);
        //6.0之后开启相机的权限
//        ||ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_SETTINGS) != PackageManager.PERMISSION_GRANTED
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            String[] mPermissionList = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_LOGS, Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.SET_DEBUG_APP, Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.GET_ACCOUNTS, Manifest.permission.WRITE_APN_SETTINGS,
                    Manifest.permission.WRITE_SETTINGS};
//            android.permission.WRITE_SETTINGS
            ActivityCompat.requestPermissions(this, mPermissionList, 123);
        }
        //首先获取用户ID，直播要取
        sharedPreferences_user = getSharedPreferences("isLogin", Context.MODE_PRIVATE);
        UserId = sharedPreferences_user.getString("userId", "");//如果取不到值就取后面的""
//        Log.e(UserId + "主页面获取用户名:", UserId);
        BottomBar bottomBar = (BottomBar) findViewById(R.id.ll_bottom_bar);
        bottomBar.setOnItemChangedListener(new BottomBar.OnItemChangedListener() {

            @Override
            public void onItemChanged(int index) {
                if (index == 0) {
                    mainHead.setVisibility(View.VISIBLE);
                    llMain.setVisibility(View.VISIBLE);
                    mine.setVisibility(View.GONE);
                    slUser.setVisibility(View.GONE);
                } else if (index == 1) {
                    SharedPreferences sharedPreferences = getSharedPreferences("isLogin", Context.MODE_PRIVATE);
                    boolean isLogin = sharedPreferences.getBoolean("isLogin", false);
                    if (!isLogin) {
                        loginPopupWindow();
                        popupWindow_login.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, (int) getResources().getDimension(R.dimen.y100), (int) getResources().getDimension(R.dimen.y100));
                    } else {
                        mainHead.setVisibility(View.GONE);
                        mine.setVisibility(View.VISIBLE);
                        slUser.setVisibility(View.VISIBLE);
                        llMain.setVisibility(View.GONE);

                        JSONObject jsonObject_user = new JSONObject();
                        try {
                            jsonObject_user.put("Protocol", "UserInfo");
                            jsonObject_user.put("Cmd", "GetAll");
                            jsonObject_user.put("UserId", UserId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.e("主页面获取用户ID", jsonObject_user.toString());
                        UserInfoHttp(Api.ENCRYPT64, jsonObject_user.toString());
                    }
                }

            }
        });
        bottomBar.setSelectedState(0);
        init();//主页面
    }

    private void setUserInfo() {
        diamondSend.setText(mDiamond);
        glideUtis_header_user.glideCircle(mHeadimageUrl, ivUserHead, true);
        tvUserName.setText(mNickName);
        if (mSex.equals("男")) {
            ivSexUser.setImageResource(R.drawable.man);
        } else {
            ivSexUser.setImageResource(R.drawable.female);
        }
        userId.setText(UserId);
        if (mSignture.isEmpty()) {
            tvUserSignture.setText("还没有设置签名");
        } else {
            tvUserSignture.setText(mSignture);
        }
        tvLiveNum.setText(mLiveNum);
        tvFollowNum.setText(mFollow);
        tvFanNum.setText(mFans);
    }


    Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    Toast.makeText(MainActivity.this, msg_um, Toast.LENGTH_SHORT).show();
                    SharedPreferences sharedPreferences = getSharedPreferences("isLogin", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isLogin", true);
                    user.setuId(UserId);
                    editor.putString("userId",UserId);
                    editor.commit();
                    break;
                case 1:
                    Intent intent_prepare = new Intent(MainActivity.this, SWCameraStreamingActivity.class);
                    startActivity(intent_prepare);
                    break;
                case 2:
                    Toast.makeText(MainActivity.this, Msg, Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    SharedPreferences.Editor editor_userinfo = sharedPreferences_user.edit();
                    editor_userinfo.putString("mNickName", mNickName);
                    editor_userinfo.putString("mLevel", mLevel);
                    editor_userinfo.putString("mCharm", mCharm);
                    editor_userinfo.putString("mHeadimageUrl", mHeadimageUrl);
                    editor_userinfo.putString("mExperience", mExperience);
                    editor_userinfo.putString("mDiamond", mDiamond);
                    editor_userinfo.putString("mLiveNum", mLiveNum);
                    editor_userinfo.putString("mVideoNum", mVideoNum);
                    editor_userinfo.putString("mSex", mSex);
                    editor_userinfo.putString("mAge", mAge);
                    editor_userinfo.putString("mEmotion", mEmotion);
                    editor_userinfo.putString("mHometown", mHometown);
                    editor_userinfo.putString("mWork", mWork);
                    editor_userinfo.putString("mFollow", mFollow);
                    editor_userinfo.putString("mFans", mFans);
                    editor_userinfo.putString("mSignture", mSignture);
                    editor_userinfo.commit();
                    setUserInfo();
                    break;
                case 4:
                    Toast.makeText(MainActivity.this, msg_um, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    //设置电量条颜色
    @Override
    protected String getPowerBarColors() {
        return AppConsts.POWER_BAR_BACKGROUND;
    }

    @OnClick({R.id.live_mine, R.id.follow_mine, R.id.fans_mine,R.id.live_putvideo, R.id.iv_message, R.id.iv_search, R.id.btn_edit, R.id.bk_contribution, R.id.bk_income, R.id.bk_mydiamond, R.id.bk_level, R.id.bk_vedio, R.id.bk_identification, R.id.bk_set})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.live_mine:
                SharedPreferences sharedPreferences_user = getSharedPreferences("isLogin", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences_user.edit();
                editor.putString("ChatroomId", UserId);
                editor.commit();
                Intent intent_mylive = new Intent(this, MyLiveActivity.class);
                startActivity(intent_mylive);
                break;
            case R.id.follow_mine:
                startActivity(new Intent(MainActivity.this, MyFollowActivity.class));
                break;
            case R.id.fans_mine:
                startActivity(new Intent(MainActivity.this, MyFansActivity.class));
                break;
            case R.id.live_putvideo:
                llBottomBar.setVisibility(View.GONE);
                showPopWindow();
                setBackgroundAlpha(0.6f, MainActivity.this);
                popupWindow_vedio.showAtLocation(view, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.iv_message:
                //进入信息页面
                Intent intent_message = new Intent(this, MessageActivity.class);
                startActivity(intent_message);
                websocketServiceIntent = new Intent(this, WebSocketService.class);
                startService(websocketServiceIntent);
                WebSocketService.webSocketConnect();
                break;
            case R.id.iv_search:
                Intent intent_search = new Intent(this, SearchActivity.class);
                startActivity(intent_search);
                break;
            case R.id.btn_edit:
                Intent intent_edit = new Intent(this, EditUserActivity.class);
                startActivity(intent_edit);
                break;
            case R.id.bk_contribution:
                Intent intent_contribution = new Intent(this, ContributionActivity.class);
                startActivity(intent_contribution);
                break;
            case R.id.bk_income:
                Intent intent_income = new Intent(this, IncomeActivity.class);
                startActivity(intent_income);
                break;
            case R.id.bk_mydiamond:
                Intent intent_mydiamond = new Intent(this, DiamondActivity.class);
                startActivity(intent_mydiamond);
                break;
            case R.id.bk_level:
                Intent intent_level = new Intent(this, LevelActivity.class);
                startActivity(intent_level);
                break;
            case R.id.bk_vedio:
                Intent intent_myvedio = new Intent(this, MyVedioActivity.class);
                startActivity(intent_myvedio);
                break;
            case R.id.bk_identification:
                Intent intent_identification = new Intent(this, IdentificationActivity.class);
                startActivity(intent_identification);
                break;
            case R.id.bk_set:
                Intent intent_set = new Intent(this, SetActivity.class);
                startActivity(intent_set);
                break;
        }
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

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(WebSocketService.WEBSOCKET_ACTION);
        registerReceiver(imReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(imReceiver);
    }

    //登录弹窗，第三方登录
    private void loginPopupWindow() {
        final View contentView = LayoutInflater.from(this).inflate(R.layout.login_style, null);
        imageView_qq = (ImageView) contentView.findViewById(R.id.qq_login);
        imageView_qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow_login.dismiss();
                UMShareAPI.get(MainActivity.this).doOauthVerify(MainActivity.this, SHARE_MEDIA.QQ, umAuthListener);
            }
        });
        ImageView imageView_weibo = (ImageView) contentView.findViewById(R.id.weibo_login);
        imageView_weibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow_login.dismiss();
                UMShareAPI.get(MainActivity.this).doOauthVerify(MainActivity.this, SHARE_MEDIA.SINA, umAuthListener);
            }
        });
        ImageView imageView_wechat = (ImageView) contentView.findViewById(R.id.wechat_login);
        imageView_wechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow_login.dismiss();
                UMShareAPI.get(MainActivity.this).doOauthVerify(MainActivity.this, SHARE_MEDIA.WEIXIN, umAuthListener);

            }
        });
        ImageView imageView_phone = (ImageView) contentView.findViewById(R.id.phone_login);
        imageView_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_phone = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent_phone);
                finish();
                popupWindow_login.dismiss();
            }
        });

        popupWindow_login = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
//        ColorDrawable cd = new ColorDrawable(this.getResources().getColor(R.color.white));
//        popupWindow.setBackgroundDrawable(cd);
        //点击popwindow以外的布局让pop消失
        popupWindow_login.setTouchable(true);
        popupWindow_login.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub
                return false;
            }
        });
        popupWindow_login.setBackgroundDrawable(new ColorDrawable(this.getResources().getColor(R.color.transparent)));
    }

    private void showPopWindow() {
        final View contentView = LayoutInflater.from(this).inflate(R.layout.live_style, null);
        popupWindow_vedio = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        ImageView imageView_unput = (ImageView) contentView.findViewById(R.id.live_unputvideo);
        imageView_unput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackgroundAlpha(1.0f, MainActivity.this);
                popupWindow_vedio.dismiss();
                llBottomBar.setVisibility(View.VISIBLE);
            }
        });
        ImageView imageView_livescreen = (ImageView) contentView.findViewById(R.id.live_screeen);
        imageView_livescreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "敬请期待", Toast.LENGTH_SHORT).show();
            }
        });
        ImageView imageView_vedioshort = (ImageView) contentView.findViewById(R.id.vedio_short);
        imageView_vedioshort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackgroundAlpha(1.0f, MainActivity.this);
                startActivity(new Intent(MainActivity.this, ShortVedioActivity.class));
                popupWindow_vedio.dismiss();
                llBottomBar.setVisibility(View.VISIBLE);
            }
        });
        ImageView imageView_camera = (ImageView) contentView.findViewById(R.id.live_camera);
        imageView_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                okhttpliveCamera();
                setBackgroundAlpha(1.0f, MainActivity.this);
                popupWindow_vedio.dismiss();
                llBottomBar.setVisibility(View.VISIBLE);
//                finish();
            }
        });
        popupWindow_vedio.setTouchable(true);
        popupWindow_vedio.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(1.0f, MainActivity.this);
                llBottomBar.setVisibility(View.VISIBLE);
            }
        });
        popupWindow_vedio.setBackgroundDrawable(new ColorDrawable(this.getResources().getColor(R.color.transparent)));
    }

    //通过ID判断是否可以发布直播，不符合要实名认证
    private void okhttpliveCamera() {
        JSONObject jsonObject_camera = new JSONObject();
        try {
            jsonObject_camera.put("Protocol", "IsLive");
            jsonObject_camera.put("Cmd", "1");
            jsonObject_camera.put("UserId", UserId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtils
                .postString()
                .url(Api.ENCRYPT64)
                .content(jsonObject_camera.toString())
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
                                .url(Api.LIVE_CAMERA)
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
                                                            JSONObject jsonobject = new JSONObject(response);
                                                            Msg = jsonobject.getString("Msg");
                                                            String Result = jsonobject.getString("Result");
                                                            Message msg = new Message();
                                                            if (Result.equals("1")) {
                                                                msg.what = 1;
                                                                mHandler.sendMessage(msg);
                                                            } else if (Result.equals("0")) {
                                                                msg.what = 2;
                                                                mHandler.sendMessage(msg);
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


    //初始化视图
    private void init() {
        viewpagerMain.setAdapter(unLoginAdapter);
        viewpagerMain.setCurrentItem(2);
        //刚进来默认选择游戏
        textViews[2].setSelected(true);
        //viewPager添加滑动监听，用于控制TextView的展示
        viewpagerMain.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                //textView全部未选择
                for (TextView textView : textViews) {
                    textView.setSelected(false);
                }
                //设置选择效果
                textViews[position].setSelected(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private FragmentStatePagerAdapter unLoginAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                //市场
                case 0:
                    return new FollowFragment();
                //消息
                case 1:
                    return new GameFragment();
                //通讯录
                case 2:
                    return new FoundFragment();
                //我的
                case 3:
                    return new SameCityFragment();
            }
            return null;
        }
    };

    //textview点击事件
    @OnClick({R.id.tv_follow, R.id.tv_game, R.id.tv_found, R.id.tv_samecity})
    public void onClick(TextView view) {
        for (int i = 0; i < textViews.length; i++) {
            textViews[i].setSelected(false);
            textViews[i].setTag(i);
        }
        //设置选择效果
        view.setSelected(true);
        //参数false代表瞬间切换，而不是平滑过渡
        viewpagerMain.setCurrentItem((Integer) view.getTag(), false);
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
                                                            mNickName = jsonobject.getString("NickName");
                                                            mLevel = jsonobject.getString("Level");
                                                            mExperience = jsonobject.getString("经验");
                                                            mCharm = jsonobject.getString("魅力");
                                                            mDiamond = jsonobject.getString("钻石");
                                                            mLiveNum = jsonobject.getString("直播");
                                                            mVideoNum = jsonobject.getString("点播");
                                                            mHeadimageUrl = jsonobject.getString("IconUrl") + "?" + currentTime;
                                                            mSex = jsonobject.getString("性别");
                                                            mAge = jsonobject.getString("生日");
                                                            mEmotion = jsonobject.getString("情感");
                                                            mHometown = jsonobject.getString("家乡");
                                                            mWork = jsonobject.getString("职业");
                                                            mFollow = jsonobject.getString("关注" + "");
                                                            mFans = jsonobject.getString("粉丝" + "");
                                                            mSignture = jsonobject.getString("签名" + "");
                                                            Message message_userinfo = new Message();
                                                            message_userinfo.what = 3;
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


    private String plat;
    //*************************第三方登录*************************//
    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            //授权开始的回调
        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
//            Toast.makeText(getApplicationContext(), "Authorize succeed", Toast.LENGTH_SHORT).show();
            if (platform == SHARE_MEDIA.WEIXIN) {
                plat = "weixin";
                openid = data.get("openid");
                sendWeChat(plat, openid);
                //转换为set
                Set<String> keySet = data.keySet();
//                Log.d("微信的信息openid————", openid);
                //遍历循环，得到里面的key值----用户名，头像....
                for (String string : keySet) {
                    //打印下
                    Log.d("微信的信息————", string);
                    //得到openid后执行该方法 sendWeChat(openid);
                    Log.d("微信的信息————", data.get(string));
                }
            } else if (platform == SHARE_MEDIA.QQ) {
                plat = "qq";
                openid = data.get("uid");
                sendWeChat(plat, openid);
            } else if (platform == SHARE_MEDIA.SINA) {
                plat = "sina";
                openid = data.get("uid");
                sendWeChat(plat, openid);
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
//            Toast.makeText(getApplicationContext(), "Authorize fail", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
//            Toast.makeText(getApplicationContext(), "Authorize cancel", Toast.LENGTH_SHORT).show();
        }
    };

    //微信登录上传服务器{"Protocol":"LoggingApi","Cmd":"weixin","OpenID":"123"}
    private void sendWeChat(String platform, String openid) {
        JSONObject jsonObject_wechat = new JSONObject();
        try {
            jsonObject_wechat.put("Protocol", "LoggingApi");
            jsonObject_wechat.put("Cmd", platform);
            jsonObject_wechat.put("OpenID", openid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtils
                .postString()
                .url(Api.ENCRYPT64)
                .content(jsonObject_wechat.toString())
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
                                .url(Api.LOGIN_UM)
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
                                                            msg_um = jsonobject.getString("Msg");
                                                            String Result = jsonobject.getString("Result");
                                                            UserId = jsonobject.getString("UserId");
                                                            String AccessKey=jsonobject.getString("AccessKey");
                                                            SharedPreferences sharedPreferences_accesskey=getSharedPreferences("isLogin", Context.MODE_PRIVATE);
                                                            SharedPreferences.Editor editor = sharedPreferences_accesskey.edit();
                                                            editor.putString("AccessKey",AccessKey);
                                                            editor.commit();
                                                            if (Result.equals("1")) {
                                                                Message message_youmeng = new Message();
                                                                message_youmeng.what = 0;
                                                                mHandler.sendMessage(message_youmeng);
                                                            } else {
                                                                Message message_unyoumeng = new Message();
                                                                message_unyoumeng.what = 4;
                                                                mHandler.sendMessage(message_unyoumeng);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);

    }

    //点击两次返回退出程序
    @Override
    public void onBackPressed() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(this, "再摁一次退出程序", Toast.LENGTH_SHORT).show();
            //两秒内再次点击返回则退出
            //如果两秒内，用户没有再次点击，则把isExit设置为false
            viewpagerMain.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);
        } else {
            finish();
        }
    }

    //设置透明度
    public void setBackgroundAlpha(float bgAlpha, Context context) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha;
        getWindow().setAttributes(lp);
    }
}
