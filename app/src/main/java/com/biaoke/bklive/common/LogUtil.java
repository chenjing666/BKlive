package com.biaoke.bklive.common;

import android.util.Log;

public class LogUtil {
	public static final String TAG = "骠客";

	public static boolean isDebug = true;

	public static void d(String tag, String msg) {
		if (isDebug)
			Log.d(tag, msg);
	}

	public static void d(String msg) {
		if (isDebug)
			Log.d(LogUtil.TAG, msg);
	}
}
