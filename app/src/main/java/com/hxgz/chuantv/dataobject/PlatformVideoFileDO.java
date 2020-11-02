package com.hxgz.chuantv.dataobject;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 平台对应视频文件
 *
 * @author zhoujianwu
 * @date 2020/10/25
 * @description：
 */
@Data
public class PlatformVideoFileDO implements Serializable {
    private static final long serialVersionUID = -4929005028053912864L;

    private String platform;

    private String show;

    private List<VideoFileDO> videoFileDOList;

    @Data
    public static class VideoFileDO implements Serializable {
        private static final long serialVersionUID = -4929005028053912864L;

        private String title;

        private String url;

    }
}

    