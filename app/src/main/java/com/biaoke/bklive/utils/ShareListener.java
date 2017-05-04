package com.biaoke.bklive.utils;

/**
 * Created by Howie on 2017/4/10.
 */

public interface ShareListener {
    void onStart();

    void onSuccess();

    void onFailure();

    void onCancel();
}
