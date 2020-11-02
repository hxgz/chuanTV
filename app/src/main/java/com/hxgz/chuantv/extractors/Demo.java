package com.hxgz.chuantv.extractors;

import android.util.Log;
import com.hxgz.chuantv.R;
import com.hxgz.chuantv.dataobject.*;
import com.hxgz.chuantv.utils.FileUtil;

import java.util.List;

/**
 * @author zhoujianwu
 * @date 2020/10/29
 * @description：
 */
public class Demo implements TVExtractor {
    @Override
    public VideoSectionPageDO previewNav(String navId) {
        return FileUtil.getObject(R.raw.tv_nav_list, VideoSectionPageDO.class);
    }

    @Override
    public VideoDetailDO getVideo(String videoId) {
        return FileUtil.getObject(R.raw.tv_video_info, VideoDetailDO.class);
    }

    @Override
    public List<TVPickerDO> getPicker(String refer) {
        return FileUtil.getList(R.raw.tv_picker_attribute, TVPickerDO.class);
    }

    @Override
    public List<VideoInfoDO> pickTV(List<TVPickParam> pickParamList, int page) {
        Log.d("TV:SEARCH", "param: " + pickParamList.toString() + " page:" + page);
        //vod-list-id-13-pg-1-order--by-hits-class-0-year-0-letter--area--lang-.html
        return FileUtil.getList(R.raw.tv_video_list, VideoInfoDO.class);
    }

    @Override
    public List<VideoInfoDO> search(String word, int page) {
        return FileUtil.getList(R.raw.tv_video_list, VideoInfoDO.class);
    }
}

    