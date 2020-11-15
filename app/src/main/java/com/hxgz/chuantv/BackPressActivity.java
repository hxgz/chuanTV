package com.hxgz.chuantv;

import android.app.Activity;
import android.view.View;
import android.widget.Toast;
import com.hxgz.chuantv.utils.LogUtil;
import com.hxgz.chuantv.utils.NoticeUtil;

/**
 * @author zhoujianwu
 * @date 2020/11/14
 * @description：
 */
public class BackPressActivity extends Activity {
    private static final int TIME_INTERVAL = 2000;
    private long mBackPressed;

    View beforeCloseFocusView;

    public void setBeforeCloseFocusView(View view) {
        beforeCloseFocusView = view;
    }

    @Override
    public void onBackPressed() {
        final View currentFocus = getCurrentFocus();
        if (beforeCloseFocusView != currentFocus) {
            beforeCloseFocusView.requestFocus();
            return;
        }

        if (mBackPressed + TIME_INTERVAL <= System.currentTimeMillis()) {
            mBackPressed = System.currentTimeMillis();
            NoticeUtil.show(getBaseContext(), "按[返回键]退出");
            return;
        }

        super.onBackPressed();
    }
}

    