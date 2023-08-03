package com.power.ssyx.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.power.ssyx.common.exception.SsyxException;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.common.result.ResultCodeEnum;
import com.power.ssyx.model.sys.RegionWare;
import com.power.ssyx.sys.mapper.RegionWareMapper;
import com.power.ssyx.sys.service.RegionWareService;
import com.power.ssyx.vo.sys.RegionWareQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * @author power
 * @description 针对表【region_ware(城市仓库关联表)】的数据库操作Service实现
 * @createDate 2023-08-03 20:48:29
 */
@Service
public class RegionWareServiceImpl extends ServiceImpl<RegionWareMapper, RegionWare>
        implements RegionWareService {

    @Autowired
    private RegionWareMapper regionWareMapper;

    @Override
    public Result getPageList(Integer page, Integer limit, RegionWareQueryVo regionWareQueryVo) {
        Page<RegionWare> pageParam = new Page<>(page, limit);
        String keyword = regionWareQueryVo.getKeyword();
        LambdaQueryWrapper<RegionWare> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            // 根据区域名称 或者 仓库名称查询
            queryWrapper.like(RegionWare::getRegionName, keyword)
                    .or().like(RegionWare::getWareName, keyword);
        }
        Page<RegionWare> regionWarePage = baseMapper.selectPage(pageParam, queryWrapper);
        return Result.ok(regionWarePage);
    }

    @Override
    public Result saveRegionWare(RegionWare regionWare) {
        // 判断区域是否已经开通
        LambdaQueryWrapper<RegionWare> queryWrapper = new LambdaQueryWrapper<>();
        // 一个区域只有一个仓库
        queryWrapper.eq(RegionWare::getRegionId, regionWare.getRegionId())
                .eq(RegionWare::getWareId, regionWare.getWareId());
        if (count(queryWrapper) > 0) {
            // 已经存在
            throw new SsyxException(ResultCodeEnum.REGION_OPEN);
        }
        save(regionWare);
        return Result.ok(null);
    }

    @Override
    public Result removeRegionWareById(Long id) {
        removeById(id);
        return Result.ok(null);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public Result updateStatus(Long id, Integer status) {
//        // 先查询regionWare 保证其他值不变
//        RegionWare regionWare = getById(id);
//        regionWare.setStatus(status);
//        // 尽量不更新其他无盖你数据
//        updateById(regionWare);
        int update = regionWareMapper.updateStatus(id, status);
        return Result.ok(null);
    }
}




