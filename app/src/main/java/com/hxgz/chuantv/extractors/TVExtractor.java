package com.hxgz.chuantv.extractors;

import com.hxgz.chuantv.dataobject.*;

import java.util.List;

/**
 * TV源
 */
public interface TVExtractor {
    /**
     * 默认导航条
     */
    List<NavItemDO> defaultNav();

    /**
     * 导航页
     */
    VideoSectionPageDO previewNav(String navId);

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

    default String getVideoAddress(String url) {
        return url;
    }
}
