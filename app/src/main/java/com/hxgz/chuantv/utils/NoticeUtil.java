package com.hxgz.chuantv.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * @author zhoujianwu
 * @date 2020/10/28
 * @description：
 */
public class NoticeUtil {
    private static void show(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }
}

    