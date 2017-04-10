package com.biaoke.bklive.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.biaoke.bklive.R;
import com.biaoke.bklive.activity.PLVideoViewActivity;
import com.biaoke.bklive.adapter.liveItemAdapter;
import com.biaoke.bklive.bean.Banner;
import com.biaoke.bklive.bean.live_item;
import com.biaoke.bklive.imagecycleview.ImageCycleView;
import com.biaoke.bklive.message.Api;
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
    private List<live_item> recyclerDataList = new ArrayList<>();
    private View mHeaderView;
    private View mFooterView;
    private liveItemAdapter liveItemAdapter;
    private ImageView imageView;
    private String useId;
    private String page = "0";
    private String BannerUp;
    private List<Banner> bannerList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_un_login, container, false);
        unbinder = ButterKnife.bind(this, view);
        if (!recyclerDataList.isEmpty()) {
            recyclerDataList.clear();
        }

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("isLogin", Context.MODE_PRIVATE);
        useId = sharedPreferences.getString("userId", "");
        JSONObject jsonObject_content = new JSONObject();
        try {
            jsonObject_content.put("Protocol", "Explore");
            jsonObject_content.put("UserId", useId);
            jsonObject_content.put("Page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getVideo(jsonObject_content.toString());
        myImagecycleview();//轮播图，加载各种途径图片

        mHeaderView = LayoutInflater.from(getActivity()).inflate(R.layout.header, null);
        imageView = (ImageView) mHeaderView.findViewById(R.id.headiv_found);
        imageView.setBackgroundResource(R.drawable.header_found);
        AnimationDrawable anim = (AnimationDrawable) imageView.getBackground();
        anim.start();
        mFooterView = LayoutInflater.from(getActivity()).inflate(R.layout.footer, null);
        recyclerviewFound.addHeaderView(mHeaderView, 80);
        recyclerviewFound.addFootView(mFooterView, 50);

        //设置布局管理器,可以根据图片大小自适应
        XStaggeredGridLayoutManager xGridLayoutManager = new XStaggeredGridLayoutManager(2, XStaggeredGridLayoutManager.VERTICAL);
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
                refreshData();
            }
        });
        return view;
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
//                    JSONObject jsonObject_banner = new JSONObject();
//                    try {
//                        jsonObject_banner.put("Protocol", "BannerUp");
//                        jsonObject_banner.put("UserId", useId);
//                        jsonObject_banner.put("Cmd", "1");
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    getBanner(jsonObject_banner.toString());
                    break;
                case 1:
                    Log.e("lllll", bannerList.size() + "");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
//                            myImagecycleview();
                        }
                    });

                    break;
                case 2:
                    break;
            }
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
                recyclerviewFound.refreshComplate();
            }
        }, 2000);
    }

    private void initRefreshData() {
        for (int i = 0; i < 20; i++) {
            live_item liveItem = new live_item("", "", "", "", i + i + "", "http://img.25pp.com/uploadfile/bizhi/iphone4/2012/1003/20121003113200683_3g.jpg", "", "", "", "");
            recyclerDataList.add(liveItem);
        }
    }

    /**
     * load more
     */
    private void loadMoreData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initLoadMoreData();
                recyclerviewFound.loadMoreComplate();
            }
        }, 2000);
    }

    private void initLoadMoreData() {
        for (int i = 0; i < 3; i++) {
            live_item liveItem = new live_item("", "", "", "", i + i + "", "http://img.25pp.com/uploadfile/bizhi/iphone4/2012/1003/20121003113200683_3g.jpg", "", "", "", "");
            recyclerDataList.add(liveItem);
        }
    }

    private liveItemAdapter.OnItemClickListener listen = new liveItemAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int postion) {
//            Toast.makeText(getActivity(), "发现页面" + postion, Toast.LENGTH_SHORT).show();
            Intent intent_video = new Intent(getActivity(), PLVideoViewActivity.class);
            intent_video.putExtra("path", recyclerDataList.get(postion).getVideoUrl());
            startActivity(intent_video);
        }
    };


    //轮播图
    private void myImagecycleview() {
        //		mImageCycleView.setAutoCycle(false); //关闭自动播放
//        mImageCycleView.setCycleDelayed(2000);//设置自动轮播循环时间
//
//		mImageCycleView.setIndicationStyle(ImageCycleView.IndicationStyle.COLOR,
//				Color.BLUE, Color.RED, 1f);

//		mImageCycleView.setIndicationStyle(ImageCycleView.IndicationStyle.IMAGE,
//				R.drawable.dian_unfocus, R.drawable.dian_focus, 1f);

        final List<ImageCycleView.ImageInfo> list = new ArrayList<ImageCycleView.ImageInfo>();
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
//        getBanner(jsonObject_banner.toString());
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
//                                                                    Banner banner = new Banner(ImgeUrl);
//                                                                    bannerList.add(banner);
                                                                    Log.e("轮播图地址", ImgeUrl);
                                                                    list.add(new ImageCycleView.ImageInfo(ImgeUrl, i + "", ""));
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
//        for (int i = 0; i < bannerList.size(); i++) {
//            String url = bannerList.get(i).getImageUrl();
//            list.add(new ImageCycleView.ImageInfo(url, i + "", ""));
//        }
        //使用网络加载图片
        String url1 = "http://server-test.bk5977.com:8800/video/21.jpg";
        String url2 = "http://server-test.bk5977.com:8800/video/22.jpg";
        String url3 = "http://server-test.bk5977.com:8800/video/23.jpg";
        list.add(new ImageCycleView.ImageInfo(url1, "11", "eeee"));
        list.add(new ImageCycleView.ImageInfo(url2, "222", "rrrr"));
        list.add(new ImageCycleView.ImageInfo(url3, "333", "tttt"));


        mImageCycleView.setOnPageClickListener(new ImageCycleView.OnPageClickListener() {
            @Override
            public void onClick(View imageView, ImageCycleView.ImageInfo imageInfo) {
                Toast.makeText(getActivity(), "你点击了" + imageInfo.value.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        mImageCycleView.loadData(list, new ImageCycleView.LoadImageCallBack() {
            @Override
            public ImageView loadAndDisplay(ImageCycleView.ImageInfo imageInfo) {

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
                BitmapUtils bitmapUtils = new BitmapUtils(getActivity());
                ImageView imageView = new ImageView(getActivity());
                bitmapUtils.display(imageView, imageInfo.image.toString());
                return imageView;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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
                                                                String UserId = jsonobject.getString("UserId");
                                                                String NickName = jsonobject.getString("NickName");
                                                                String IconUrl = jsonobject.getString("IconUrl");//用户头像
                                                                String Exp = jsonobject.getString("Exp");//热度 心形后面的数字
                                                                String Title = jsonobject.getString("Title");
                                                                String SnapshotUrl = jsonobject.getString("SnapshotUrl");//封面URL
                                                                String videoUrl = jsonobject.getString("VideoUrl");
                                                                String Format = jsonobject.getString("Format");
                                                                String HV = jsonobject.getString("HV");
                                                                String Type = jsonobject.getString("Type");
                                                                live_item liveItem = new live_item(UserId, NickName, IconUrl, Exp, Title, SnapshotUrl, videoUrl, Format, HV, Type);
                                                                recyclerDataList.add(liveItem);
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
}
