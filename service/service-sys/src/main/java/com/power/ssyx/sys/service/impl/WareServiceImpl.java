package com.power.ssyx.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.power.ssyx.common.exception.SsyxException;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.common.result.ResultCodeEnum;
import com.power.ssyx.common.utils.ParamCheckUtils;
import com.power.ssyx.model.sys.Ware;
import com.power.ssyx.sys.mapper.WareMapper;
import com.power.ssyx.sys.service.WareService;
import com.power.ssyx.vo.product.WareQueryVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author power
 * @description 针对表【ware(仓库表)】的数据库操作Service实现
 * @createDate 2023-08-03 20:48:29
 */
@Service
public class WareServiceImpl extends ServiceImpl<WareMapper, Ware>
        implements WareService {

    @Override
    public Result getPageList(Integer page, Integer limit, WareQueryVo wareQueryVo) {
        if (!ParamCheckUtils.validateParams(page, limit)) {
            throw new SsyxException(ResultCodeEnum.PARAM_ERROR);
        }

        Page<Ware> pageParam = new Page<>(page, limit);
        LambdaQueryWrapper<Ware> queryWrapper = new LambdaQueryWrapper<>();
        if (!Objects.isNull(wareQueryVo)) {
            queryWrapper.eq(Ware::getName, wareQueryVo.getName());
            queryWrapper.eq(Ware::getProvince, wareQueryVo.getProvince());
            queryWrapper.eq(Ware::getCity, wareQueryVo.getCity());
            queryWrapper.eq(Ware::getDistrict, wareQueryVo.getDistrict());
        }
        // 查询分页数据
        Page<Ware> warePage = baseMapper.selectPage(pageParam, queryWrapper);
        return Result.ok(warePage);
    }


    // 添加开通区域
    @Override
    public Result saveWare(Ware ware) {
        // 判断传入区域是否已经有仓库
        LambdaQueryWrapper<Ware> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Ware::getDistrict, ware.getDistrict());
        if (this.count(queryWrapper) > 0) {
            // 传入区域已经有仓库，不能向该区域添加仓库
            throw new SsyxException(ResultCodeEnum.REGION_OPEN);
        }
        this.save(ware);
        return Result.ok(null);
    }

    // 删除开通区域 removeById
    @Override
    public Result removeWareById(Long id) {
        if (!ParamCheckUtils.validateParams(id)) {
            throw new SsyxException(ResultCodeEnum.PARAM_ERROR);
        }
        this.removeById(id);
        return Result.ok(null);
    }

    // 批量删除开通区域
    @Override
    public Result deleteWareByIds(List<Long> ids) {
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
        return Result.ok(this.getById(id));
    }

    // 修改仓库
    @Override
    public Result updateWareById(Ware ware) {
        if (this.updateById(ware)) {
            return Result.ok(null);
        }
        return Result.fail("修改仓库失败");
    }

    @Override
    public Result findAllList() {
        return Result.ok(list());
    }
}




