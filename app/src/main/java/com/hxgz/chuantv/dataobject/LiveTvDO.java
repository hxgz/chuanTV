package com.hxgz.chuantv.dataobject;

import lombok.Data;

import java.io.Serializable;

/**
 * 直播电视台
 *
 * @author zhoujianwu
 * @date 2021/01/31
 * @description：
 */
@Data
public class LiveTvDO implements Serializable {
    private static final long serialVersionUID = 2436617018587319681L;

    /**
     * 分类
     */
    private String category;

    /**
     * 频道
     */
    private String channel;

    /**
     * 平台
     */
    private String platform;

    /**
     * 画质
     */
    private String quality;


    /**
     * 地址
     */
    private String address;
}

    