package com.hxgz.chuantv.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.hxgz.chuantv.R;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ScrollViewList extends LinearLayout {
    LayoutInflater inflater;

    int showItemNum;
    int itemResId;

    TextView mHeader;
    ViewGroup mItems;

    List<Object> datas = new ArrayList<>();

    private onClickListen mOnClickListen;

    public ScrollViewList(Context context) {
        this(context, null);
    }

    public ScrollViewList(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollViewList(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ScrollViewList);
        showItemNum = ta.getInt(R.styleable.ScrollViewList_showItemNum, 0);
        itemResId = ta.getResourceId(R.styleable.ScrollViewList_itemResId, NO_ID);

        initView(context);
    }

    public void setItemResId(int itemResId) {
        this.itemResId = itemResId;
    }

    public static ScrollViewList newInstance(Context context) {
        ScrollViewList scrollViewList = new ScrollViewList(context);
        scrollViewList.setLayoutParams(
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return scrollViewList;
    }

    public void setShowItemNum(int showItemNum) {
        this.showItemNum = showItemNum;
    }

    private void initView(Context context) {
        inflater = LayoutInflater.from(context);

        inflater.inflate(R.layout.widget_scroll_view, this);
        mHeader = (TextView) findViewById(R.id.headTitle);
        mItems = (ViewGroup) findViewById(R.id.itemLayout);
    }

    public void setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            mHeader.setText(title);
            mHeader.setVisibility(VISIBLE);
        }
    }

    public void setMOnClickListen(onClickListen clickListen) {
        mOnClickListen = clickListen;
    }

    public void addItem(View view, Object o) {
//        if (showItemNum > 0) {
//            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) this.getLayoutParams();
//            int itemWidth = this.getWidth() - lp.leftMargin - lp.rightMargin;
//
//            ViewGroup.MarginLayoutParams vlp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
//
//            int width = itemWidth / showItemNum - vlp.leftMargin - vlp.rightMargin;
//            LogUtil.e(itemWidth + " " + width + " " + view.getWidth() + " " + view.getMeasuredWidth() + " " + this.getMeasuredWidth());
//            //view.getLayoutParams().width = width;
//        }

        view.setTag(this);
        view.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(300).start();
            } else {
                v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).start();
            }
        });

        view.setOnClickListener(v -> {
            final boolean selected = v.isSelected();
            ScrollViewList.this.clearItemSelected();
            v.setSelected(true);

            if (null != mOnClickListen) {
                int itemIndex = ScrollViewList.this.getItemIndex(v);
                Object data = ScrollViewList.this.getItemData(itemIndex);
                mOnClickListen.onClick(v, data, selected);
            }

        });

        mItems.addView(view);
        datas.add(o);
    }


    public View newItem(Object o) {
        if (itemResId == NO_ID || null == mItems) {
            return null;
        }

        View view = inflater.inflate(itemResId, this, false);
        addItem(view, o);
        return view;
    }

    public void clearItemSelected() {
        getSelectedItems().forEach(view -> view.setSelected(false));
    }


    public List<View> getSelectedItems() {
        return getAllItems().stream().filter(View::isSelected).collect(Collectors.toList());
    }

    public void selectItem(int index) {
        List<View> allItems = getAllItems();
        if (index >= 0 && index < allItems.size()) {
            allItems.get(index).setSelected(true);
        }
    }

    public int getItemIndex(View view) {
        return mItems.indexOfChild(view);
    }

    public Object getItemData(int index) {
        return datas.get(index);
    }

    public List<View> getAllItems() {
        List<View> result = new ArrayList<View>();
        for (int i = 0; i < mItems.getChildCount(); i++) {
            View child = mItems.getChildAt(i);
            result.add(child);
        }
        return result;
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }

    public interface onClickListen {

        /**
         * @param view        子view
         * @param data
         * @param repeatClick 是否重复点击
         */
        public void onClick(View view, Object data, boolean repeatClick);
    }
}
