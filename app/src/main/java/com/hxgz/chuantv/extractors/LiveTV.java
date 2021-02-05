package com.hxgz.chuantv.extractors;

import com.hxgz.chuantv.R;
import com.hxgz.chuantv.dataobject.LiveTvDO;
import com.hxgz.chuantv.utils.FileUtil;

import java.util.List;

/**
 * @author zhoujianwu
 * @date 2021/01/31
 * @description：
 */
public class LiveTV {
    public static List<LiveTvDO> getChannelList() {
        return FileUtil.getList(R.raw.live_tv_list, LiveTvDO.class);
    }
}

    