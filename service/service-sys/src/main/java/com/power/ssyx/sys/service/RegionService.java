package com.power.ssyx.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.model.sys.Region;

/**
 * @author power
 * @description 针对表【region(地区表)】的数据库操作Service
 * @createDate 2023-08-03 20:48:29
 */
public interface RegionService extends IService<Region> {

    Result findRegionByKeyword(String keyword);

}
