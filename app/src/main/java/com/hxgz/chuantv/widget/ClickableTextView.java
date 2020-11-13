package com.hxgz.chuantv.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;

@SuppressLint("AppCompatCustomView")
public class ClickableTextView extends TextView implements View.OnFocusChangeListener {
    public ClickableTextView(Context context) {
        this(context, null);
    }

    public ClickableTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClickableTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        defaultClickListener = v -> setSelected(true);

        setOnClickListener(defaultClickListener);

        setOnFocusChangeListener(this);
    }

    View.OnClickListener defaultClickListener;

    public void setOnClickListener(@Nullable OnClickListener l) {
        super.setOnClickListener(v -> {
            l.onClick(v);

            if (hasOnClickListeners()) {
                defaultClickListener.onClick(v);
            }
        });
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            v.animate().scaleX(1.2f).scaleY(1.2f).setDuration(300).start();
        } else {
            v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).start();
        }
    }
}