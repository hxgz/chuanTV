package com.hxgz.chuantv.utils;

import android.util.Log;

/**
 * @author zhoujianwu
 * @date 2020/10/23
 * @description：
 */
public class LogUtil {
    public static int e(String msg) {
        final StackTraceElement[] stackTrace = new Exception().getStackTrace();
        String tag = stackTrace[1].getClassName() + ":" + stackTrace[1].getMethodName();
        return Log.e(tag, msg);
    }

    public static int d(String msg) {
        final StackTraceElement[] stackTrace = new Exception().getStackTrace();
        String tag = stackTrace[1].getClassName() + ":" + stackTrace[1].getMethodName();
        return Log.d(tag, msg);
    }
}

    