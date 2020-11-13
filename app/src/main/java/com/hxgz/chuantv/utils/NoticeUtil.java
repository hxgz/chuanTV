package com.hxgz.chuantv.utils;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

/**
 * @author zhoujianwu
 * @date 2020/10/28
 * @description：
 */
public class NoticeUtil {
    public static void show(Context context, String msg) {
        Looper.prepare();
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        Looper.loop();
    }
}

    