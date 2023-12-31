package com.power.ssyx.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.model.sys.RegionWare;
import com.power.ssyx.vo.sys.RegionWareQueryVo;

import java.util.List;

/**
 * @author power
 * @description 针对表【region_ware(城市仓库关联表)】的数据库操作Service
 * @createDate 2023-08-03 20:48:29
 */
public interface RegionWareService extends IService<RegionWare> {

    // 获取开通区域列表
    Result getPageList(Integer page, Integer limit, RegionWareQueryVo regionWareQueryVo);

    // 添加开通区域
    Result saveRegionWare(RegionWare regionWare);

    // 删除开通区域 removeById
    Result removeRegionWareById(Long id);

    // 更新开通区域状态 updateStatus
    Result updateStatus(Long id, Integer status);

    // 批量删除开通区域
    Result deleteRegionWareByIds(List<Long> ids);

    // 根据id查询开通区域
    Result get(Long id);
}
