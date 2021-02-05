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

    private String channel;

    private String address;
}

    