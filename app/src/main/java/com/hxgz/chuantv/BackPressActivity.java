package com.hxgz.chuantv;

import android.app.Activity;
import android.view.View;
import com.hxgz.chuantv.consts.TvConst;
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

    