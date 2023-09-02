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
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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

    List<TextPickerViewList> childPickerViewLists;

    private onClickListen mOnClickListen;

    public TextPickerViewList(ViewGroup parent) {
        this(parent, -1);
    }

    public TextPickerViewList(ViewGroup parent, int viewPosition) {
        this.parent = parent;

        scrollViewList = ScrollViewList.newInstance(parent.getContext());
        scrollViewList.setItemResId(R.layout.widget_text_picker_view);

        scrollViewList.setMOnClickListen((view, data, repeatClick) -> {
            int position = scrollViewList.getItemIndex(view);
            TextPickerViewList.this.setSelectedPosition(position);

            if (TextPickerViewList.this.mOnClickListen != null) {
                TextPickerViewList.this.mOnClickListen.onclick(view, data);
            }
        });
        parent.addView(scrollViewList, viewPosition);
    }

    public void setTvPickerDO(TVPickerDO tvPickerDO) {
        this.tvPickerDO = tvPickerDO;
    }

    public void asView() {

        for (int index = 0; index < tvPickerDO.getItemList().size(); index++) {
            TVPickerDO.ItemDO item = tvPickerDO.getItemList().get(index);
            TextView textView = (TextView) scrollViewList.newItem(item);
            textView.setText(item.getShow());
            if (item.getValue().equals(tvPickerDO.getDefaultSelectItemValue())) {
                setSelectedPosition(index);
            }
        }
    }


    public void setSelectedPosition(int position) {
        scrollViewList.selectItem(position);
        if (CollectionUtils.isNotEmpty(childPickerViewLists)) {
            childPickerViewLists.forEach(childPickerViewList -> childPickerViewList.scrollViewList.removeAllViews());
            childPickerViewLists = null;
        }

        TVPickerDO.ItemDO itemDO = tvPickerDO.getItemList().get(position);
        if (!CollectionUtils.isEmpty(itemDO.getChildrenList())) {
            AtomicInteger parentPosition = new AtomicInteger(parent.indexOfChild(scrollViewList));
            if (CollectionUtils.isNotEmpty(itemDO.getChildrenList())) {
                childPickerViewLists = itemDO.getChildrenList().stream()
                        .map(childTVPickerDO -> {
                            TextPickerViewList childPickerViewList = new TextPickerViewList(parent, parentPosition.incrementAndGet());
                            childPickerViewList.setTvPickerDO(childTVPickerDO);
                            childPickerViewList.setMOnClickListen(mOnClickListen);
                            childPickerViewList.asView();
                            return childPickerViewList;
                        }).collect(Collectors.toList());
            }
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
        tvPickParam.setValueDepth(new ArrayList());

        if (CollectionUtils.isNotEmpty(childPickerViewLists)) {
            childPickerViewLists.forEach(childPickerViewList -> {
                if(null!= childPickerViewList.getPickerData()){
                    tvPickParam.getValueDepth().add(childPickerViewList.getPickerData());
                }
            });
        }
        return tvPickParam;
    }

    public interface onClickListen {
        public void onclick(View view, Object Data);
    }
}

