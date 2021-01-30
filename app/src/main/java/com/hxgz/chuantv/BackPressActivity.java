package com.hxgz.chuantv;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import com.hxgz.chuantv.consts.TvConst;
import com.hxgz.chuantv.utils.DebugUtil;
import com.hxgz.chuantv.utils.LogUtil;
import com.hxgz.chuantv.utils.NoticeUtil;

/**
 * @author zhoujianwu
 * @date 2020/11/14
 * @description：
 */
public class BackPressActivity extends Activity {
    private long mBackPressed;

    View beforeCloseFocusView;

    public void setBeforeCloseFocusView(View view) {
        beforeCloseFocusView = view;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler((crashThread, ex) -> {
            final String stackTrace = DebugUtil.getStackTrace(ex);

            LogUtil.e("Catch Activity Error: " + stackTrace);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    NoticeUtil.showLong(getBaseContext(), ex.getMessage());
                }
            });
        });
    }

    @Override
    public void onBackPressed() {
        final View currentFocus = getCurrentFocus();
        if (beforeCloseFocusView != null && beforeCloseFocusView != currentFocus) {
            beforeCloseFocusView.requestFocus();
            return;
        }

        if (mBackPressed <= System.currentTimeMillis()) {
            mBackPressed = TvConst.TIME_INTERVAL + System.currentTimeMillis();
            NoticeUtil.show(getBaseContext(), "按【返回键】退出");
            return;
        }

        super.onBackPressed();
    }
}

    