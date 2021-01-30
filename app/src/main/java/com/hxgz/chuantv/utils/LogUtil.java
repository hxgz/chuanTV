package com.hxgz.chuantv.utils;

import android.util.Log;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author zhoujianwu
 * @date 2020/10/23
 * @description：
 */
public class LogUtil {
    public static int e(String msg) {
        final StackTraceElement[] stackTrace = new Exception().getStackTrace();
        String tag = stackTrace[1].getClassName() + ":" + stackTrace[1].getMethodName();
        return Log.e(tag, _prefix() + msg);
    }

    public static int d(String msg) {
        final StackTraceElement[] stackTrace = new Exception().getStackTrace();
        String tag = stackTrace[1].getClassName() + ":" + stackTrace[1].getMethodName();
        return Log.d(tag, _prefix() + msg);
    }

    private static String _prefix() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " "
                + Thread.currentThread().getName() + " ";
    }
}

    