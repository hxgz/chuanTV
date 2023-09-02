package com.hxgz.chuantv.dataobject;

import lombok.Data;

import java.util.List;

/**
 * @author zhoujianwu
 * @date 2020/10/29
 * @description：
 */
@Data
public class TVPickParam {
    private static final long serialVersionUID = -1545551498492416166L;

    private String topic;

    private String value;

    private List<TVPickParam> valueDepth;

}