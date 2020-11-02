package com.hxgz.chuantv.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.hxgz.chuantv.R;
import com.hxgz.chuantv.VideoDetailActivity;
import com.hxgz.chuantv.widget.textview.ObjectTextView;

/**
 * @author zhoujianwu
 * @date 2020/10/25
 * @description：
 */
public class ViewUtil {
    public static TextView getTextViewForShow(Context context) {
        TextView textView = new TextView(context);
        textView.setTextColor(context.getResources().getColor(R.color.white_90));
        textView.setPadding(0, 0, 10, 0);
        textView.setFocusable(false);

        return textView;
    }

    public static ObjectTextView getTextViewForAction(Context context) {
        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        ObjectTextView textView = new ObjectTextView(context);
        textView.setTextColor(context.getResources().getColor(R.color.white_70));
        textView.setBackgroundColor(context.getResources().getColor(R.color.white_20));
//        textView.setPadding(10, 0, 10, 0);
        textView.setGravity(Gravity.CENTER);
        textView.setWidth(context.getResources().getDimensionPixelSize(R.dimen.h_160));
        textView.setHeight(context.getResources().getDimensionPixelSize(R.dimen.h_80));
        textView.setFocusable(true);
        textView.setClickable(true);
//        textView.setContextClickable(true);

        lp.setMargins(10, 10, 10, 10);
        textView.setLayoutParams(lp);

        textView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //textView.setTextColor(context.getResources().getColor(R.color.yellow_90));
                    v.animate().scaleX(1.2f).scaleY(1.2f).setDuration(300).start();
                } else {
                    //textView.setTextColor(context.getResources().getColor(R.color.white_70));
                    v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).start();
                }
            }
        });

        return textView;
    }

    public static ObjectTextView getTextViewForPick(Context context) {
        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        ObjectTextView textView = new ObjectTextView(context);
        textView.setTextColor(context.getResources().getColor(R.color.white_70));

        textView.setGravity(Gravity.CENTER);
        textView.setWidth(context.getResources().getDimensionPixelSize(R.dimen.h_160));
        textView.setHeight(context.getResources().getDimensionPixelSize(R.dimen.h_80));
        textView.setFocusable(true);
        textView.setClickable(true);

        lp.setMargins(10, 10, 10, 10);
        textView.setLayoutParams(lp);

        textView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    textView.setBackgroundColor(context.getResources().getColor(R.color.blue));
                    //textView.setTextColor(context.getResources().getColor(R.color.yellow_90));
                } else {
                    //textView.setTextColor(context.getResources().getColor(R.color.white_70));
                    textView.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        });

        return textView;
    }
}

    