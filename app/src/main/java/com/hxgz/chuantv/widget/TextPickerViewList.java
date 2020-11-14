package com.hxgz.chuantv.widget;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.hxgz.chuantv.R;
import com.hxgz.chuantv.dataobject.TVPickParam;
import com.hxgz.chuantv.dataobject.TVPickerDO;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;

/**
 * @author zhoujianwu
 * @date 2020/10/25
 * @description：
 */
@Data
public class TextPickerViewList {
    ViewGroup parent;

    // 标签行
    private TVPickerDO tvPickerDO;

    ScrollViewList scrollViewList;

    TextPickerViewList childPickerViewList;

    private onClickListen mOnClickListen;

    public TextPickerViewList(ViewGroup parent) {
        this(parent, -1);
    }

    public TextPickerViewList(ViewGroup parent, int viewPosition) {
        this.parent = parent;

        scrollViewList = ScrollViewList.newInstance(parent.getContext());
        scrollViewList.setItemResId(R.layout.widget_text_picker_view);

        scrollViewList.setMOnClickListen((view, data) -> {
            int position = scrollViewList.getItemIndex(view);
            if (TextPickerViewList.this.mOnClickListen != null) {
                TextPickerViewList.this.mOnClickListen.onclick(view, data);
            }
            TextPickerViewList.this.setSelectedPosition(position);
        });
        parent.addView(scrollViewList, viewPosition);
    }

    public void setTvPickerDO(TVPickerDO tvPickerDO) {
        this.tvPickerDO = tvPickerDO;
    }

    public void asView() {
        int index = 0;
        for (TVPickerDO.ItemDO item : tvPickerDO.getItemList()) {
            TextView textView = (TextView) scrollViewList.newItem(item);
            textView.setText(item.getShow());

            if (item.getValue().equals(tvPickerDO.getDefaultSelectItemValue())) {
                setSelectedPosition(index++);
            }
        }
    }


    public void setSelectedPosition(int position) {
        scrollViewList.selectItem(position);
        if (null != childPickerViewList) {
            childPickerViewList.scrollViewList.removeAllViews();
            childPickerViewList = null;
        }

        TVPickerDO.ItemDO itemDO = tvPickerDO.getItemList().get(position);
        if (!CollectionUtils.isEmpty(itemDO.getChildrenList())) {
            TVPickerDO childPickerDO = new TVPickerDO();
            childPickerDO.setTopic(tvPickerDO.getTopic());
            childPickerDO.setItemList(itemDO.getChildrenList());

            childPickerViewList = new TextPickerViewList(parent, parent.indexOfChild(scrollViewList) + 1);
            childPickerViewList.setTvPickerDO(childPickerDO);
            childPickerViewList.setMOnClickListen(mOnClickListen);
            childPickerViewList.asView();
        }

    }

    public TVPickParam getPickerData() {
        View view = scrollViewList.getSelectedItems().stream().findFirst().orElse(null);
        if (null == view) {
            return null;
        }
        int selectedPosition = scrollViewList.getItemIndex(view);
        TVPickerDO.ItemDO itemDO = (TVPickerDO.ItemDO) scrollViewList.getItemData(selectedPosition);

        TVPickParam tvPickParam = new TVPickParam();
        tvPickParam.setTopic(tvPickerDO.getTopic());
        tvPickParam.setValue(itemDO.getValue());
        tvPickParam.setValueDepth(new ArrayList() {{
            add(itemDO.getValue());
        }});

        if (null != childPickerViewList) {
            TVPickParam childPickerData = childPickerViewList.getPickerData();
            if (null != childPickerData && !CollectionUtils.isEmpty(childPickerData.getValueDepth())) {
                tvPickParam.getValueDepth().addAll(childPickerData.getValueDepth());
            }
        }
        return tvPickParam;
    }

    public interface onClickListen {
        public void onclick(View view, Object Data);
    }
}

