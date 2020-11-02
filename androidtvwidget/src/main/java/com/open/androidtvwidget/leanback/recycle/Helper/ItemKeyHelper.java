package com.open.androidtvwidget.leanback.recycle.Helper;

import android.view.KeyEvent;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.open.androidtvwidget.leanback.recycle.RecyclerViewTV;

/**
 * 按键拖动item.
 * Created by hailongqiu on 2016/9/6.
 */
public class ItemKeyHelper extends ItemTouchHelper {

    public ItemKeyHelper(Callback callback) {
        super(callback);
    }

    @Override
    public void attachToRecyclerView(RecyclerView recyclerView) {
        super.attachToRecyclerView(recyclerView);
        if (recyclerView instanceof RecyclerViewTV) {
            RecyclerViewTV recyclerViewTV = (RecyclerViewTV) recyclerView;
            recyclerViewTV.addOnItemKeyListener(mOnItemKeyListener);
        }
    }

    private final RecyclerViewTV.OnItemKeyListener mOnItemKeyListener
            = new RecyclerViewTV.OnItemKeyListener() {
        @Override
        public boolean dispatchKeyEvent(KeyEvent event) {
            return false;
        }
    };

}
