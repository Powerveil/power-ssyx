package com.power.ssyx.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.power.ssyx.common.exception.SsyxException;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.common.result.ResultCodeEnum;
import com.power.ssyx.common.utils.ParamCheckUtils;
import com.power.ssyx.model.sys.RegionWare;
import com.power.ssyx.sys.mapper.RegionWareMapper;
import com.power.ssyx.sys.service.RegionWareService;
import com.power.ssyx.vo.sys.RegionWareQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

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

    // 获取开通区域列表
    @Override
    public Result getPageList(Integer page, Integer limit, RegionWareQueryVo regionWareQueryVo) {
        if (!ParamCheckUtils.validateParams(page, limit)) {
            throw new SsyxException(ResultCodeEnum.PARAM_ERROR);
        }

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

    // 添加开通区域
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

    // 删除开通区域 removeById
    @Override
    public Result removeRegionWareById(Long id) {
        if (!ParamCheckUtils.validateParams(id)) {
            throw new SsyxException(ResultCodeEnum.PARAM_ERROR);
        }
        removeById(id);
        return Result.ok(null);
    }

    // 更新开通区域状态 updateStatus
    @Override
    public Result updateStatus(Long id, Integer status) {
        if (!ParamCheckUtils.validateParams(id, status)) {
            throw new SsyxException(ResultCodeEnum.PARAM_ERROR);
        }
        if (regionWareMapper.updateStatus(id, status) > 0) {
            Result.ok(null);
        }
        return Result.fail("更新开通区域状态失败");
    }

    // 批量删除开通区域
    @Override
    public Result deleteRegionWareByIds(List<Long> ids) {
        if (!ParamCheckUtils.validateParams(ids)) {
            throw new SsyxException(ResultCodeEnum.PARAM_ERROR);
        }
        if (this.removeByIds(ids)) {
            return Result.ok(null);
        }
        return Result.fail("批量删除开通区域失败");
    }

    // 根据id查询开通区域
    @Override
    public Result get(Long id) {
        return Result.ok(getById(id));
    }
}




