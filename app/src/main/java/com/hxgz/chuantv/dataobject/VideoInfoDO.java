package com.hxgz.chuantv.dataobject;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhoujianwu
 * @date 2020/10/18
 * @description：
 */
@Data
public class VideoInfoDO implements Serializable {
    private static final long serialVersionUID = 821557501516415401L;

    private String title;

    private String imgUrl;

    private String videoId;

    // 动态
    private String status;
}

    