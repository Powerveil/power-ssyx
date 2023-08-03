package com.power.ssyx.sys.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.model.sys.RegionWare;
import com.power.ssyx.model.sys.Ware;
import com.power.ssyx.sys.mapper.WareMapper;
import com.power.ssyx.sys.service.WareService;
import com.power.ssyx.vo.product.WareQueryVo;
import org.springframework.stereotype.Service;

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
        Page<RegionWare> pageParam = new Page<>(page, limit);

//        LambdaQueryWrapper<RegionWare> queryWrapper = new LambdaQueryWrapper<>();
//        if (StringUtils.hasText(keyword)) {
//            // 根据区域名称 或者 仓库名称查询
//            queryWrapper.like(RegionWare::getRegionName, keyword)
//                    .or().like(RegionWare::getWareName, keyword);
//        }
//        Page<RegionWare> regionWarePage = baseMapper.selectPage(pageParam, queryWrapper);
//        return Result.ok(regionWarePage);
        return null;
    }

    @Override
    public Result findAllList() {
        return Result.ok(list());
    }
}




