package com.hxgz.chuantv.widget.textview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import androidx.annotation.Nullable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zhoujianwu
 * @date 2020/10/25
 * @description：
 */

@SuppressLint("AppCompatCustomView")
@EqualsAndHashCode(callSuper = true)
public class ObjectTextView extends TextView {
    private Object nData;

    private int nIndex;

    public ObjectTextView(Context context) {
        super(context);
    }

    public ObjectTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ObjectTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ObjectTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setnData(Object data) {
        this.nData = data;
    }

    public Object getnData() {
        return this.nData;
    }

    public void setnIndex(int index) {
        this.nIndex = index;
    }

    public int getnIndex() {
        return this.nIndex;
    }
}

    