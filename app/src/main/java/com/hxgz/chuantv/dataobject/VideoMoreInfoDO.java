package com.hxgz.chuantv.dataobject;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author zhoujianwu
 * @date 2020/10/18
 * @description：
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class VideoMoreInfoDO extends VideoInfoDO {
    private static final long serialVersionUID = -9094794434759279268L;

    private String aliasTitle;

    // 描述
    private String desc;

    // 年份
    private String year;

    // 演员
    private List<String> actorList;

    //标签
    private List<String> tagList;
}

    