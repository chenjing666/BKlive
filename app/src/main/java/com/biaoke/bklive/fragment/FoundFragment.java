package com.biaoke.bklive.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.biaoke.bklive.R;
import com.biaoke.bklive.activity.BkShopActivity;
import com.biaoke.bklive.activity.PLVideoViewActivity;
import com.biaoke.bklive.activity.ShortVideoActivity;
import com.biaoke.bklive.adapter.liveItemAdapter;
import com.biaoke.bklive.bean.Banner;
import com.biaoke.bklive.bean.live_item;
import com.biaoke.bklive.imagecycleview.ImageCycleView;
import com.biaoke.bklive.message.Api;
import com.biaoke.bklive.websocket.WebSocketService;
import com.lidroid.xutils.BitmapUtils;
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
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.MediaType;


/**
 * Created by hasee on 2017/3/30.
 */

public class FoundFragment extends Fragment {
    @BindView(R.id.icv_topView)
    ImageCycleView mImageCycleView;
    Unbinder unbinder;
    @BindView(R.id.recyclerview_found)
    XRecyclerView recyclerviewFound;
    @BindView(R.id.bk_shoppingmarket)
    ImageView bkShoppingmarket;
    @BindView(R.id.bk_shoppingmarket2)
    ImageView bkShoppingmarket2;

    private List<live_item> recyclerDataList = new ArrayList<>();
    private View mHeaderView;
    private View mFooterView;
    private liveItemAdapter liveItemAdapter;
    private ImageView imageView, imageView_load;
    private String useId;
    private int page = 0;
    private String BannerUp;
    private List<Banner> bannerList;
    private JSONObject jsonObject_content;

    //websocket
    private Intent websocketServiceIntent;
    private String accessKey;
    //地理信息定位
    private String wd;
    private String jd;
    private List<ImageCycleView.ImageInfo> list;
    private int myPosition;
    private AnimationDrawable anim_load;
    private AnimationDrawable anim;

    @RequiresApi(api = Build.VERSION_CODES.M)//api23以上才能对xrecyclerview 进行滑动监听
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_found, container, false);
        unbinder = ButterKnife.bind(this, view);
//        imageView_load = (ImageView) getActivity().findViewById(R.id.load_found);
//        imageView_load.setBackgroundResource(R.drawable.load_found);
//        anim_load = (AnimationDrawable) imageView_load.getBackground();
//        anim_load.start();

        if (!recyclerDataList.isEmpty()) {
            recyclerDataList.clear();
        }
        wd = getActivity().getIntent().getStringExtra("wd");
        jd = getActivity().getIntent().getStringExtra("jd");
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("isLogin", Context.MODE_PRIVATE);
        useId = sharedPreferences.getString("userId", "");
        accessKey = sharedPreferences.getString("AccessKey", "");
        boolean isLogin = sharedPreferences.getBoolean("isLogin", false);

        jsonObject_content = new JSONObject();
        try {
            jsonObject_content.put("Protocol", "Explore");
            jsonObject_content.put("Cmd", "FaXian");
            jsonObject_content.put("UserId", useId);
            jsonObject_content.put("Page", page + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getVideo(jsonObject_content.toString());
        myImagecycleview();//轮播图，加载各种途径图片

        mHeaderView = LayoutInflater.from(getActivity()).inflate(R.layout.header, null);
        imageView = (ImageView) mHeaderView.findViewById(R.id.headiv_found);
        imageView.setBackgroundResource(R.drawable.header_down_load);
        anim = (AnimationDrawable) imageView.getBackground();

        mFooterView = LayoutInflater.from(getActivity()).inflate(R.layout.footer, null);
        recyclerviewFound.addHeaderView(mHeaderView, 80);
        recyclerviewFound.addFootView(mFooterView, 50);

        //设置布局管理器,可以根据图片大小自适应
        final XStaggeredGridLayoutManager xGridLayoutManager = new XStaggeredGridLayoutManager(2, XStaggeredGridLayoutManager.VERTICAL);
        xGridLayoutManager.setAutoMeasureEnabled(false);
        recyclerviewFound.setLayoutManager(xGridLayoutManager);
        //设置适配器
        liveItemAdapter = new liveItemAdapter(getActivity(), recyclerviewFound);
        liveItemAdapter.bind(recyclerDataList);
        liveItemAdapter.setOnItemClickListener(listen);
        recyclerviewFound.setAdapter(liveItemAdapter);
        recyclerviewFound.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadMoreData();
            }
        });
        recyclerviewFound.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 0;
                anim.start();
                refreshData();
            }
        });

        int position = 0;
        LinearLayoutManager layoutMgr = new LinearLayoutManager(getActivity());
        int firstPosition = layoutMgr.findFirstVisibleItemPosition();
        View v = layoutMgr.getChildAt(position - firstPosition);
        if (Build.VERSION.SDK_INT > 22) {
            recyclerviewFound.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    int visibleItemCount = xGridLayoutManager.getChildCount();//显示在页面内的item
                    int lineItemCount = xGridLayoutManager.getSpanCount();//每行的item
                    int totalItemCount = xGridLayoutManager.getItemCount();//总的item
                    recyclerviewFound.getFocusedChild();
//                    Log.e("-----", recyclerviewFound.getChildAt(totalItemCount-4) + "在集合中返回指定位置的视图。");
                    int item = totalItemCount - myPosition;
                    if (item == 0 || item == 1 || item == 2 || item == 3 || item == 4 || item == 5 || item == 6 || item == 7) {
                        loadMoreData();
                    }
                }
            });
        }

        sendMyLocation(wd, jd);
        return view;
    }

    private void sendMyLocation(String wd, String jd) {
        JSONObject object_location = new JSONObject();
        try {
            object_location.put("Protocol", "UpGps");
            object_location.put("UserId", useId);
            object_location.put("wd", wd);
            object_location.put("jd", jd);
            object_location.put("AccessKey", accessKey);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtils.postString()
                .url(Api.ENCRYPT64)
                .content(object_location.toString())
                .mediaType(MediaType.parse("application/json charset=utf-8"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        OkHttpUtils.postString()
                                .url(Api.GPS)
                                .content(response)
                                .mediaType(MediaType.parse("application/json charset=utf-8"))
                                .build()
                                .execute(new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {

                                    }

                                    @Override
                                    public void onResponse(String response, int id) {
                                        Log.d("上传位置信息成功的返回", response);
                                    }
                                });
                    }
                });
    }

    private void showBanner() {
        JSONObject jsonObject_banner = new JSONObject();
        try {
            jsonObject_banner.put("Protocol", "BannerUp");
            jsonObject_banner.put("UserId", useId);
            jsonObject_banner.put("Cmd", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getBanner(jsonObject_banner.toString());
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    mImageCycleView.loadData(list, new ImageCycleView.LoadImageCallBack() {
                        @Override
                        public ImageView loadAndDisplay(ImageCycleView.ImageInfo imageInfo) {
                            //使用BitmapUtils,只能使用网络图片
                            BitmapUtils bitmapUtils = new BitmapUtils(getActivity());
                            ImageView imageView = new ImageView(getActivity());
                            bitmapUtils.display(imageView, imageInfo.image.toString());
                            return imageView;
                        }
                    });
                    break;
                case 1:
                    break;
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(WebSocketService.WEBSOCKET_ACTION);
        getActivity().registerReceiver(imReceiver, filter);
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

    private void joinInWeb() {
        SharedPreferences sharedPreferences_accesskey = getActivity().getSharedPreferences("isLogin", Context.MODE_PRIVATE);
        String AccessKey = sharedPreferences_accesskey.getString("AccessKey", "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Protocol", "Logging");
            jsonObject.put("UserId", useId);
            jsonObject.put("AccessKey", AccessKey);
            jsonObject.put("PwdModel", "3");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        WebSocketService.sendMsg(jsonObject.toString());
    }

    /**
     * refresh
     */
    private void refreshData() {
        recyclerviewFound= (XRecyclerView) getActivity().findViewById(R.id.recyclerview_found);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initRefreshData();
                recyclerviewFound.refreshComplate();
            }
        }, 2000);
    }

    private void initRefreshData() {
        recyclerDataList.clear();
        getVideo(jsonObject_content.toString());
    }

    /**
     * load more
     */
    private void loadMoreData() {
        recyclerviewFound= (XRecyclerView) getActivity().findViewById(R.id.recyclerview_found);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initLoadMoreData();
                recyclerviewFound.loadMoreComplate();
            }
        }, 2000);
    }

    private void initLoadMoreData() {
        page = page + 1;
        try {
            jsonObject_content.put("Protocol", "Explore");
            jsonObject_content.put("Cmd", "FaXian");
            jsonObject_content.put("UserId", useId);
            jsonObject_content.put("Page", page + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("pagepage----", page + "");
        getVideo(jsonObject_content.toString());
    }

    private liveItemAdapter.OnItemClickListener listen = new liveItemAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int postion) {
            myPosition = postion;
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
                Intent intent_shortvideo = new Intent(getActivity(), ShortVideoActivity.class);
                intent_shortvideo.putExtra("path", recyclerDataList.get(postion).getVideoUrl());
                startActivity(intent_shortvideo);
                //添加视频点击
                setPv(recyclerDataList.get(postion).getId());
            }
        }
    };

    //    设置播放次数
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


    //轮播图
    private void myImagecycleview() {
        //		mImageCycleView.setAutoCycle(false); //关闭自动播放
        mImageCycleView.setCycleDelayed(2000);//设置自动轮播循环时间
//
        mImageCycleView.setIndicationStyle(ImageCycleView.IndicationStyle.COLOR,
                Color.LTGRAY, Color.BLUE, 1f);

//		mImageCycleView.setIndicationStyle(ImageCycleView.IndicationStyle.IMAGE,
//				R.drawable.dian_unfocus, R.drawable.dian_focus, 1f);

        list = new ArrayList<ImageCycleView.ImageInfo>();
        bannerList = new ArrayList<>();
        //res图片资源
//        list.add(new ImageCycleView.ImageInfo(R.drawable.a1, "111111111111", ""));
//        list.add(new ImageCycleView.ImageInfo(R.drawable.a2, "222222222222222", ""));
//        list.add(new ImageCycleView.ImageInfo(R.drawable.a3, "3333333333333", ""));

        //SD卡图片资源
//		list.add(new ImageCycleView.ImageInfo(new File(Environment.getExternalStorageDirectory(),"a1.jpg"),"11111",""));
//		list.add(new ImageCycleView.ImageInfo(new File(Environment.getExternalStorageDirectory(),"a2.jpg"),"22222",""));
//		list.add(new ImageCycleView.ImageInfo(new File(Environment.getExternalStorageDirectory(),"a3.jpg"),"33333",""));

        JSONObject jsonObject_banner = new JSONObject();
        try {
            jsonObject_banner.put("Protocol", "BannerUp");
            jsonObject_banner.put("UserId", useId);
            jsonObject_banner.put("Cmd", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtils.postString()
                .url(Api.ENCRYPT64)
                .content(jsonObject_banner.toString())
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("错误的回调", e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        OkHttpUtils.postString()
                                .url(Api.BANNER)
                                .content(response)
                                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                                .build()
                                .execute(new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {
                                        Log.e("错误的回调", e.getMessage());
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
                                                        Log.e("错误的回调", e.getMessage());
                                                    }

                                                    @Override
                                                    public void onResponse(String response, int id) {
                                                        Log.e("轮播图信息", response);
                                                        try {
                                                            JSONObject jsonObject_banner = new JSONObject(response);
                                                            if (jsonObject_banner.getString("Result").equals("1")) {
                                                                JSONArray jsonArray_banner = new JSONArray(jsonObject_banner.getString("Data"));
                                                                for (int i = 0; i < jsonArray_banner.length(); i++) {
                                                                    JSONObject jsonobject = jsonArray_banner.getJSONObject(i);
                                                                    String ImgeUrl = jsonobject.getString("ImgeUrl");//轮播图片地址
                                                                    Log.e("轮播图地址", ImgeUrl);
                                                                    list.add(new ImageCycleView.ImageInfo(ImgeUrl, "", ""));
                                                                    Message msg = new Message();
                                                                    msg.what = 0;
                                                                    handler.sendMessage(msg);
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
//        for (int i = 0; i < bannerList.size(); i++) {
//            String url = bannerList.get(i).getImageUrl();
//            list.add(new ImageCycleView.ImageInfo(url, i + "", ""));
//        }
        //使用网络加载图片
//        String url1 = "http://server-test.bk5977.com:8800/video/21.jpg";
//        String url2 = "http://server-test.bk5977.com:8800/video/22.jpg";
//        String url3 = "http://server-test.bk5977.com:8800/video/23.jpg";
//        String url4 = "http://server-test.bk5977.com:8800/video/22.jpg";
//        String url5 = "http://server-test.bk5977.com:8800/video/23.jpg";
//        list.add(new ImageCycleView.ImageInfo(url1, "11", "eeee"));
//        list.add(new ImageCycleView.ImageInfo(url2, "222", "rrrr"));
//        list.add(new ImageCycleView.ImageInfo(url3, "333", "tttt"));
//        list.add(new ImageCycleView.ImageInfo(url4, "222", "rrrr"));
//        list.add(new ImageCycleView.ImageInfo(url5, "333", "tttt"));


        mImageCycleView.setOnPageClickListener(new ImageCycleView.OnPageClickListener() {
            @Override
            public void onClick(View imageView, ImageCycleView.ImageInfo imageInfo) {
                Toast.makeText(getActivity(), "oo" + imageInfo.value.toString(), Toast.LENGTH_SHORT).show();
            }
        });

//        mImageCycleView.loadData(list, new ImageCycleView.LoadImageCallBack() {
//            @Override
//            public ImageView loadAndDisplay(ImageCycleView.ImageInfo imageInfo) {

        //本地图片
//                ImageView imageView = new ImageView(getActivity());
//                imageView.setImageResource(Integer.parseInt(imageInfo.image.toString()));
//                return imageView;


//				//使用SD卡图片
//				SmartImageView smartImageView=new SmartImageView(MainActivity.this);
//				smartImageView.setImageURI(Uri.fromFile((File)imageInfo.image));
//				return smartImageView;

//				//使用SmartImageView，既可以使用网络图片也可以使用本地资源
//                SmartImageView smartImageView = new SmartImageView(getActivity());
//                smartImageView.setImageResource(Integer.parseInt(imageInfo.image.toString()));
//                return smartImageView;

        //使用BitmapUtils,只能使用网络图片
//                BitmapUtils bitmapUtils = new BitmapUtils(getActivity());
//                ImageView imageView = new ImageView(getActivity());
//                bitmapUtils.display(imageView, imageInfo.image.toString());
//                return imageView;
//            }
//        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        getActivity().unregisterReceiver(imReceiver);
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
//                        Log.d("成功的返回", response);
//                        Okhttputils(Api.LOGIN,response);
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
//                                                            "Protocol":"Explore",
//                                                            "Result":"1",
//                                                                    "Data":[],	//数组对象
//                                                            "BannerUp": 1491612445,//轮播图更新标志，保存后对比，如果本次和上次值不一样，更新
                                                            JSONObject object = new JSONObject(response);

//                                                            SharedPreferences sharedPreferences_banner = getActivity().getSharedPreferences("isLogin", Context.MODE_PRIVATE);
//                                                            SharedPreferences.Editor editor = sharedPreferences_banner.edit();
//                                                            String Bannerup = object.getString("BannerUp");
//                                                            editor.putString("BannerUp", Bannerup);
//                                                            editor.commit();
//                                                            Log.e("Bannerup=====:", Bannerup);
//                                                            Log.e("Bannerup=====:", BannerUp);
//                                                            if (Bannerup.equals(BannerUp)) {
//                                                                editor.putBoolean("isFirstBanner", true);
//                                                                Message message_banner = new Message();
//                                                                message_banner.what = 0;
//                                                                handler.sendMessage(message_banner);
//                                                            }
//                                                            else {
//                                                                Message message_banner = new Message();
//                                                                message_banner.what = 2;
//                                                                handler.sendMessage(message_banner);
//                                                            }

                                                            JSONArray jsonArray = new JSONArray(object.getString("Data"));
//                                                                   "Protocol":"Explore",
//                                                                    "UserId":"0",		// 用户ＩＤ
//                                                                    "NickName":"test1",		//用户昵称
//                                                                    "IconUrl":"http://server-test.bk5977.com:8800/BK/icon.png",		//用户头像
//                                                                    "Exp":"0",		//热度 心形后面的数字
//                                                                    "Title":"0",		//视频标题
//                                                                    "SnapshotUrl":"0",		//封面URL
//                                                                    "VideoUrl":"0",		//视频URL
//                                                                    "Format":"mp4",		//视频格式，mp4 m3u8
//                                                                    "HV":"H"	//H 竖屏 V 横屏
//                                                                    "Type":"vod"	//live 直播 vod视频
                                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                                JSONObject jsonobject = jsonArray.getJSONObject(i);
//                                                                String videoid = jsonobject.getString("Id");//获取视频的编号
                                                                //直播没有
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

    //获取轮播图信息---------
    private void getBanner(String content) {
        OkHttpUtils.postString()
                .url(Api.ENCRYPT64)
                .content(content)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("错误的回调", e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        OkHttpUtils.postString()
                                .url(Api.BANNER)
                                .content(response)
                                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                                .build()
                                .execute(new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {
                                        Log.e("错误的回调", e.getMessage());
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
                                                        Log.e("错误的回调", e.getMessage());
                                                    }

                                                    @Override
                                                    public void onResponse(String response, int id) {
                                                        Log.e("轮播图信息", response);
                                                        try {
                                                            JSONObject jsonObject_banner = new JSONObject(response);
                                                            if (jsonObject_banner.getString("Result").equals("1")) {
                                                                JSONArray jsonArray_banner = new JSONArray(jsonObject_banner.getString("Data"));
                                                                for (int i = 0; i < jsonArray_banner.length(); i++) {
                                                                    JSONObject jsonobject = jsonArray_banner.getJSONObject(i);
                                                                    String ImgeUrl = jsonobject.getString("ImgeUrl");//轮播图片地址
                                                                    Banner banner = new Banner(ImgeUrl);
                                                                    bannerList.add(banner);
                                                                }
//                                                                Message msg = new Message();
//                                                                msg.what = 1;
//                                                                handler.sendMessage(msg);
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


    @OnClick({R.id.bk_shoppingmarket, R.id.bk_shoppingmarket2})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bk_shoppingmarket:
            case R.id.bk_shoppingmarket2:
                Intent intent_shop = new Intent(getActivity(), BkShopActivity.class);
                startActivity(intent_shop);
                break;
        }
    }
}
