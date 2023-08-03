package com.power.ssyx.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.model.sys.Region;
import com.power.ssyx.sys.mapper.RegionMapper;
import com.power.ssyx.sys.service.RegionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author power
 * @description 针对表【region(地区表)】的数据库操作Service实现
 * @createDate 2023-08-03 20:48:29
 */
@Service
public class RegionServiceImpl extends ServiceImpl<RegionMapper, Region>
        implements RegionService {

    @Override
    public Result findRegionByKeyword(String keyword) {
        if (Objects.isNull(keyword)) {

        }
        LambdaQueryWrapper<Region> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(Region::getName, keyword);
        List<Region> list = list(queryWrapper);
        return Result.ok(list);
    }

}




