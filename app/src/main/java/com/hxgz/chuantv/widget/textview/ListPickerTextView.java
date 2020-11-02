package com.hxgz.chuantv.widget.textview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import com.hxgz.chuantv.R;
import com.hxgz.chuantv.dataobject.TVPickParam;
import com.hxgz.chuantv.dataobject.TVPickerDO;
import com.hxgz.chuantv.utils.LogUtil;
import com.hxgz.chuantv.utils.ViewUtil;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author zhoujianwu
 * @date 2020/10/25
 * @description：
 */
@Data
public class ListPickerTextView {
    ViewGroup parent;

    // 标签行
    private TVPickerDO tvPickerDO;

    // 对应textView 列表
    private List<ObjectTextView> childrenItems;
    private int selectedPosition;

    LinearLayout layout;

    ListPickerTextView childListPickerTextView;

    private onClickListen mOnClickListen;

    public ListPickerTextView(ViewGroup parent) {
        selectedPosition = -1;
        childrenItems = new ArrayList<>();
        this.parent = parent;
    }

    public void addToView() {
        addToView(-1);
    }

    public void addToView(int viewIndex) {
        Context context = parent.getContext();

        layout = new LinearLayout(context);
        layout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setFocusable(false);

        //
        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(context);
        LinearLayout layout2 = new LinearLayout(context);
        layout2.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        layout2.setOrientation(LinearLayout.HORIZONTAL);
        layout2.setScrollContainer(true);

        int index = 0;
        int select = -1;

        for (TVPickerDO.ItemDO item : tvPickerDO.getItemList()) {
            ObjectTextView textView = ViewUtil.getTextViewForPick(context);
            textView.setnData(item);
            textView.setText(item.getShow());
            if (item.getValue().equals(tvPickerDO.getDefaultSelectItemValue())) {
                textView.setTextColor(context.getResources().getColor(R.color.yellow_90));
                select = index;
            }
            textView.setnIndex(index++);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ObjectTextView oldTextView = ListPickerTextView.this.getSelectedTextView();
                    ObjectTextView otView = (ObjectTextView) v;
                    otView.setTextColor(otView.getContext().getResources().getColor(R.color.yellow_90));
                    if (oldTextView != null) {
                        oldTextView.setTextColor(oldTextView.getContext().getResources().getColor(R.color.white_70));
                    }
                    ListPickerTextView.this.setSelectedPosition(otView.getnIndex());

                    if (ListPickerTextView.this.mOnClickListen != null) {
                        ListPickerTextView.this.mOnClickListen.onclick(otView);
                    }
                }
            });

            childrenItems.add(textView);

            layout2.addView(textView);


        }
        horizontalScrollView.addView(layout2);
        layout.addView(horizontalScrollView);

        parent.addView(layout, viewIndex);
        if (select >= 0)
            ListPickerTextView.this.setSelectedPosition(select);
    }

    public void setSelectedPosition(int position) {
        ListPickerTextView.this.selectedPosition = position;

        if (null != childListPickerTextView) {
            childListPickerTextView.layout.removeAllViews();
            childListPickerTextView = null;
        }

        TVPickerDO.ItemDO itemDO = tvPickerDO.getItemList().get(selectedPosition);
        if (!CollectionUtils.isEmpty(itemDO.getChildrenList())) {
            childListPickerTextView = new ListPickerTextView(parent);
            childListPickerTextView.setMOnClickListen(mOnClickListen);

            TVPickerDO childPickerDO = new TVPickerDO();
            childPickerDO.setTopic(tvPickerDO.getTopic());
            childPickerDO.setItemList(itemDO.getChildrenList());
            childListPickerTextView.setTvPickerDO(childPickerDO);

            childListPickerTextView.addToView(parent.indexOfChild(layout) + 1);
        }

    }

    public ObjectTextView getSelectedTextView() {
        for (int i = 0; i < childrenItems.size(); i++) {
            if (i == selectedPosition) return childrenItems.get(i);
        }
        return null;
    }

    public TVPickParam getPickerData() {
        if (selectedPosition == -1) {
            return null;
        }
        TVPickerDO.ItemDO itemDO = tvPickerDO.getItemList().get(selectedPosition);
        ObjectTextView textView = getSelectedTextView();

        TVPickParam tvPickParam = new TVPickParam();
        tvPickParam.setTopic(tvPickerDO.getTopic());
        tvPickParam.setValue(itemDO.getValue());
        tvPickParam.setValueDepth(new ArrayList() {{
            add(itemDO.getValue());
        }});

        if (null != childListPickerTextView) {
            TVPickParam childPickerData = childListPickerTextView.getPickerData();
            if (null != childPickerData && !CollectionUtils.isEmpty(childPickerData.getValueDepth())) {
                tvPickParam.getValueDepth().addAll(childPickerData.getValueDepth());
            }
        }
        return tvPickParam;
    }

    public interface onClickListen {
        public void onclick(ObjectTextView view);
    }
}

