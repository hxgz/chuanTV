package com.hxgz.chuantv.dataobject;

import lombok.Data;

import java.util.List;

/**
 * @author zhoujianwu
 * @date 2020/10/29
 * @description：
 */
@Data
public class TVPickerDO {
    private static final long serialVersionUID = -1545551498492416166L;

    private String topic;

    // private Boolean multiSelect;

    private String defaultSelectItemValue;

    private List<ItemDO> itemList;

    @Data
    public static class ItemDO {

        private String value;

        private String show;

        List<ItemDO> childrenList;
    }
}