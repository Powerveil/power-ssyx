package com.power.ssyx.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.model.sys.RegionWare;
import com.power.ssyx.vo.sys.RegionWareQueryVo;

/**
 * @author power
 * @description 针对表【region_ware(城市仓库关联表)】的数据库操作Service
 * @createDate 2023-08-03 20:48:29
 */
public interface RegionWareService extends IService<RegionWare> {

    Result getPageList(Integer page, Integer limit, RegionWareQueryVo regionWareQueryVo);

    // 添加开通区域
    Result saveRegionWare(RegionWare regionWare);

    Result removeRegionWareById(Long id);

    Result updateStatus(Long id, Integer status);
}
