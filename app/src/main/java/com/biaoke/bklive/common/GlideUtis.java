package com.biaoke.bklive.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;

import com.biaoke.bklive.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.io.File;

/**
 * Created by Holiday on 2017/2/23.
 * 图片操作工具类
 */

public class GlideUtis {
    private Context mContext;
    public GlideUtis(Context mContext) {
        this.mContext = mContext;
    }
    /**
     * 加载url图片
     * @param url
     * @param v
     * @param useMemory 是否使用缓存
     */
    public void glide(String url, ImageView v,boolean useMemory){
        Glide.with(mContext)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .skipMemoryCache(useMemory)
                .centerCrop()
                .placeholder(R.drawable.logo_72)//占位图
                .error(R.drawable.logo_72)//加载出错的图
                .into(v);
    }

    /**
     * 加载file图片
     * @param file
     * @param v
     * @param useMemory 是否使用缓存
     */
    public void glide(File file, ImageView v, boolean useMemory){
        Glide.with(mContext)
                .load(file)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .skipMemoryCache(useMemory)
                .centerCrop()
                .placeholder(R.drawable.logo_72)//占位图
                .error(R.drawable.logo_1024)//加载出错的图
                .into(v);
    }

 



/**
     * 加载圆形图片
     * @param url
     * @param v
     * @param useMemory 是否使用缓存
     */
    public void glideCircle(String url, final ImageView v, boolean useMemory){
        Glide.with(mContext)
                .load(url)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(useMemory)
                .centerCrop()
                .transform(new GlideCircleTransform(mContext))
                .placeholder(R.drawable.logo_72)//占位图
                .error(R.drawable.logo_1024)//加载出错的图
                .into(new BitmapImageViewTarget(v) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        v.setImageDrawable(circularBitmapDrawable);
                    }
                });

    }

    /**
     * 从资源文件加载圆形图片
     * @param url
     * @param v
     * @param useMemory
     */
    public void glideDrawCircle(int  url, final ImageView v, boolean useMemory){
        Glide.with(mContext)
                .load(url)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(useMemory)
                .centerCrop()
                .transform(new GlideCircleTransform(mContext))
                .placeholder(R.mipmap.ic_launcher)//占位图
                .error(R.mipmap.ic_launcher)//加载出错的图
                .into(new BitmapImageViewTarget(v) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        v.setImageDrawable(circularBitmapDrawable);
                    }
                });
    }
    /**
     * 从资源文件
     * @param url
     * @param v
     * @param useMemory
     */
    public void glideDraw(int  url, ImageView v,boolean useMemory){
        Glide.with(mContext)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .centerCrop()
                .skipMemoryCache(useMemory)
                .transform(new GlideRoundTransform(mContext,10))
                .placeholder(R.drawable.logo_72)//占位图
                .error(R.drawable.logo_1024)//加载出错的图
                .into(v);
    }
    /**
     * 加载圆角图片
     * 注意：要展示该效果，需要设置View为warp属性
     * @param url
     * @param v
     * @param useMemory 是否使用缓存
     */
    public void glideRound(String url, ImageView v,boolean useMemory){
        Glide.with(mContext)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .centerCrop()
                .skipMemoryCache(useMemory)
                .transform(new GlideRoundTransform(mContext,10))
                .placeholder(R.drawable.logo_72)//占位图
                .error(R.drawable.logo_1024)//加载出错的图
                .into(v);
    }

    /**
     * 加载圆角图片
     * 注意：要展示该效果，需要设置View为warp属性
     * @param url
     * @param v
     * @param round  圆角的程度
     * @param useMemory 是否使用缓存
     */
    public void glideRound(String url, ImageView v,int round,boolean useMemory){
        Glide.with(mContext)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .centerCrop()
                .skipMemoryCache(useMemory)
                .transform(new GlideRoundTransform(mContext,round))
                .placeholder(R.drawable.logo_72)//占位图
                .error(R.drawable.logo_1024)//加载出错的图
                .into(v);
    }
}
