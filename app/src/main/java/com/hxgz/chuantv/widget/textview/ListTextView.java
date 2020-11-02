package com.hxgz.chuantv.widget.textview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.hxgz.chuantv.R;
import com.hxgz.chuantv.VideoDetailActivity;
import com.hxgz.chuantv.dataobject.PlatformVideoFileDO;
import com.hxgz.chuantv.utils.LogUtil;
import com.hxgz.chuantv.utils.ViewUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhoujianwu
 * @date 2020/10/25
 * @description：
 */
@Data
public class ListTextView {

    //一个屏幕显示数量
    private int mNum;

    // 标签行
    private String tag;

    // 视频列表
    private List<PlatformVideoFileDO.VideoFileDO> mItems;

    // 对应textView 列表
    private List<ObjectTextView> childrenItems;

    private int selectedPosition;

    private onClickListen mOnClickListen;

    public ListTextView() {
        mNum = 10;
        selectedPosition = -1;
        childrenItems = new ArrayList<>();
    }

    public void addToView(ViewGroup parent) {
        Context context = parent.getContext();

        LinearLayout layout = new LinearLayout(context);
        layout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setFocusable(false);

        // 平台标签
        TextView textViewForShow = ViewUtil.getTextViewForShow(context);
        textViewForShow.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        textViewForShow.setText(tag);
        layout.addView(textViewForShow);

        // 剧集
        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(context);
        LinearLayout layout2 = new LinearLayout(context);
        layout2.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        layout2.setOrientation(LinearLayout.HORIZONTAL);
        layout2.setScrollContainer(true);

        int index = 0;
        for (PlatformVideoFileDO.VideoFileDO item : mItems) {
            ObjectTextView textView = ViewUtil.getTextViewForAction(context);
            textView.setnData(item);
            textView.setnIndex(index++);
            textView.setText(item.getTitle());

            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) parent.getLayoutParams();
            int paddingWidth = lp.leftMargin + lp.rightMargin; // =0

            int width = parent.getWidth() / mNum - paddingWidth;
            textView.setWidth(width);
            textView.setHeight(width / 2);


            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ObjectTextView otView = (ObjectTextView) v;

                    LogUtil.e(otView.toString());
                    if (ListTextView.this.mOnClickListen != null) {
                        ListTextView.this.mOnClickListen.onclick(otView);
                    }
                    //ListTextView.this.setSelectedPosition(otView.getnIndex());
                }

            });

            childrenItems.add(textView);

            layout2.addView(textView);
        }
        horizontalScrollView.addView(layout2);
        layout.addView(horizontalScrollView);

        parent.addView(layout);
    }

    public ObjectTextView getSelectedTextView() {
        for (int i = 0; i < childrenItems.size(); i++) {
            if (i == selectedPosition) return childrenItems.get(i);
        }
        return null;
    }

    public void setSelectedPosition(int position) {
        this.selectedPosition = position;

        ObjectTextView textView = getSelectedTextView();
        if (textView != null)
            textView.setTextColor(textView.getContext().getResources().getColor(R.color.yellow_90));
    }

    public void unSelectedPosition() {
        ObjectTextView textView = getSelectedTextView();
        if (textView != null)
            textView.setTextColor(textView.getContext().getResources().getColor(R.color.white_70));
        this.selectedPosition = -1;
    }

    public interface onClickListen {
        public void onclick(ObjectTextView view);
    }
}

