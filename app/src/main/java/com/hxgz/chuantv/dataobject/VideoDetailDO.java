package com.hxgz.chuantv.dataobject;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhoujianwu
 * @date 2020/10/25
 * @description：
 */
@Data
public class VideoDetailDO implements Serializable {
    private static final long serialVersionUID = 5277487026039438228L;

    private VideoMoreInfoDO infoDO;

    private List<PlatformVideoFileDO> fileDOList;
}

    