package com.hxgz.chuantv.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

/**
 * @author zhoujianwu
 * @date 2020/10/26
 * @description：
 */
public class DebugUtil {
    public static void whichViewFocusing(Activity activity) {
        new Thread(() -> {
            int oldId = -1;
            while (true) {
                View newView = activity.getCurrentFocus();
                if (newView != null && newView.getId() != oldId) {
                    oldId = newView.getId();
                    String idName = "";
                    try {
                        idName = activity.getResources().getResourceEntryName(newView.getId());
                    } catch (Resources.NotFoundException e) {
                        idName = String.valueOf(newView.getId());
                    }
                    Log.i("focusView", "Focused Id: " + idName + " Class: " + newView.getClass());
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

    