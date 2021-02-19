package com.hxgz.chuantv.extractors;

import com.hxgz.chuantv.R;
import com.hxgz.chuantv.dataobject.LiveTvDO;
import com.hxgz.chuantv.utils.FileUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zhoujianwu
 * @date 2021/01/31
 * @description：
 */
public class LiveTV {
    public static List<LiveTvDO> getChannelList() {
        final String content = FileUtil.getByRaw(R.raw.live_tv_list);

        List<LiveTvDO> liveTvDOList = new ArrayList<>();
        for (String line : content.split("\n", 0)) {
            List<String> lines = Stream.of(line.split(","))
                    .map(String::trim)
                    .filter(f -> !StringUtils.isEmpty(f) && !f.startsWith("#"))
                    .collect(Collectors.toList());
            if (lines.size() < 5) {
                continue;
            }
            LiveTvDO liveTvDO = new LiveTvDO();
            liveTvDO.setCategory(lines.get(0));
            liveTvDO.setChannel(lines.get(1));
            liveTvDO.setQuality(lines.get(2));
            liveTvDO.setPlatform(lines.get(3));
            liveTvDO.setAddress(lines.get(4));
            liveTvDOList.add(liveTvDO);
        }
        return liveTvDOList;
    }
}

    