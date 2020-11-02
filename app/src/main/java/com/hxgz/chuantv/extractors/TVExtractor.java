package com.hxgz.chuantv.extractors;

import com.hxgz.chuantv.dataobject.*;
import com.hxgz.chuantv.exception.BizException;

import java.util.List;

/**
 * TV源
 */
public interface TVExtractor {
    /**
     * 导航页
     */
    VideoSectionPageDO previewNav(String navId) throws Exception;

    /**
     * 影片详情
     */
    VideoDetailDO getVideo(String videoId) throws Exception;

    /**
     * 获取筛选规则
     */
    List<TVPickerDO> getPicker(String refer) throws Exception;

    /**
     * 影片筛选
     */
    List<VideoInfoDO> pickTV(List<TVPickParam> pickParamList, int page) throws Exception;

    /**
     * 片名搜索
     */
    List<VideoInfoDO> search(String word, int page) throws Exception;
}
