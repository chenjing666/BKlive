package com.biaoke.bklive.utils;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.biaoke.bklive.bean.ShareVo;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;

/**
 * 分享工具类
 */

public class ShareUtils {
    private volatile static ShareUtils instance = null;
    private static final String TAG = "ShareUtils";
    private ShareListener mListener;
    private Activity mActivity;

    private ShareUtils() {
    }

    public static ShareUtils getInstance() {
        if (instance == null) {
            synchronized (ShareUtils.class) {
                if (instance == null) {
                    instance = new ShareUtils();
                }
            }
        }
        return instance;
    }

    /**
     * 分享方法
     *
     * @param activity
     * @param shareVo  分享的数据
     * @param listener 分享的监听
     */
    public void showShareView(final Activity activity, final ShareVo shareVo, ShareListener listener) {
        mListener = listener;
        mActivity = activity;
        new ShareAction(activity)
                //SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE,新浪，QQ
                .setDisplayList(SHARE_MEDIA.SINA, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                .setShareboardclickCallback(new ShareBoardlistener() {
                    @Override
                    public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
                        UMWeb web = new UMWeb(shareVo.getLinkUrl());
                        web.setTitle(shareVo.getTitle());
                        web.setDescription(shareVo.getContent());
                        web.setThumb(new UMImage(activity, shareVo.getImgUrl()));
                        new ShareAction(activity).withMedia(web)
                                .setPlatform(share_media)
                                .setCallback(umShareListener)
                                .share();
                    }
                }).open();

    }

    //4号添加
    public void showShareViewWeChat(final Activity activity, final ShareVo shareVo, ShareListener listener) {
        mListener = listener;
        mActivity = activity;
        new ShareAction(activity)
                .setDisplayList(SHARE_MEDIA.WEIXIN)
                .setShareboardclickCallback(new ShareBoardlistener() {
                    @Override
                    public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
                        UMWeb web = new UMWeb(shareVo.getLinkUrl());
                        web.setTitle(shareVo.getTitle());
                        web.setDescription(shareVo.getContent());
                        web.setThumb(new UMImage(activity, shareVo.getImgUrl()));
                        new ShareAction(activity).withMedia(web)
                                .setPlatform(share_media)
                                .setCallback(umShareListener)
                                .share();
                    }
                }).open();

    }

    //4号添加
    public void showShareViewQQ(final Activity activity, final ShareVo shareVo, ShareListener listener) {
        mListener = listener;
        mActivity = activity;
        new ShareAction(activity)
                .setDisplayList(SHARE_MEDIA.QQ)
                .setShareboardclickCallback(new ShareBoardlistener() {
                    @Override
                    public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
                        UMWeb web = new UMWeb(shareVo.getLinkUrl());
                        web.setTitle(shareVo.getTitle());
                        web.setDescription(shareVo.getContent());
                        web.setThumb(new UMImage(activity, shareVo.getImgUrl()));
                        new ShareAction(activity).withMedia(web)
                                .setPlatform(share_media)
                                .setCallback(umShareListener)
                                .share();
                    }
                }).open();

    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            if (mListener != null) {
                mListener.onStart();
            }
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            Log.d(TAG, "platform" + platform);
            if (mListener != null) {
                mListener.onSuccess();
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            if (mListener != null) {
                mListener.onFailure();
            }
            if (t != null) {
                Log.d(TAG, "throw:" + t.getMessage());
                String info = t.getMessage();
                if (info.contains("2008")) {
                    Toast.makeText(mActivity, "没有安装" + platform.toString() + "客户端,请下载后使用", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mActivity, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            if (mListener != null) {
                mListener.onCancel();
            }
        }
    };

}
