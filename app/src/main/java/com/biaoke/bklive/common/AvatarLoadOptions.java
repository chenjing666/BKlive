package com.biaoke.bklive.common;

import com.biaoke.bklive.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * Created by hasee on 2017/5/12.
 */

public class AvatarLoadOptions {
    private AvatarLoadOptions() {
    }

    private static DisplayImageOptions options_item;

    /**
     * 图片加载选项
     */
    public static DisplayImageOptions build_item() {
        if (options_item == null) {
            options_item = new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.drawable.image_error)/*如果空url显示什么*/
                    .showImageOnLoading(R.drawable.image_loding)/*加载中显示什么*/
                    .showImageOnFail(R.drawable.image_error)/*加载失败显示什么*/
                    .cacheOnDisk(true)/*开启硬盘缓存*/
                    .cacheInMemory(true)/*开启内存缓存*/
                    .resetViewBeforeLoading(true)/*加载前重置ImageView*/
                    .build();
        }
        return options_item;
    }
}
