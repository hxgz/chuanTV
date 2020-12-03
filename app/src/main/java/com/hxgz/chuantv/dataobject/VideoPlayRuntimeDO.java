package com.hxgz.chuantv.dataobject;

import lombok.Data;

import java.io.Serializable;

/**
 * 视频播放
 *
 * @author zhoujianwu
 * @date 2020/10/18
 * @description：
 */
@Data
public class VideoPlayRuntimeDO implements Serializable {
    private static final long serialVersionUID = 4551551710029307159L;
    /**
     * 平台位置
     */
    private int selectedPlatformPosition;

    /**
     * 平台对应的视频位置
     */
    int selectedVideoPosition;

    /**
     * 播放文件
     */
    private String url;

    /**
     * 播放起始点
     */
    private Long startTime = 0L;
}

    