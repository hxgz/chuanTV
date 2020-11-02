package com.hxgz.chuantv.dataobject;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 榜单页
 *
 * @author zhoujianwu
 * @date 2020/10/18
 * @description：
 */
@Data
public class VideoSectionPageDO implements Serializable {
    private static final long serialVersionUID = 821557501516415401L;

    private List<NavItemDO> navItemDOList;

    private List<SectionItemDO> sectionItemDOList;

}

    