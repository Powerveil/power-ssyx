package com.power.ssyx.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.power.ssyx.model.product.SkuPoster;
import com.power.ssyx.product.mapper.SkuPosterMapper;
import com.power.ssyx.product.service.SkuPosterService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author power
 * @description 针对表【sku_poster(商品海报表)】的数据库操作Service实现
 * @createDate 2023-08-05 15:19:30
 */
@Service
public class SkuPosterServiceImpl extends ServiceImpl<SkuPosterMapper, SkuPoster>
        implements SkuPosterService {

    @Override
    public List<SkuPoster> getSkuPosterList(Long skuId) {
        LambdaQueryWrapper<SkuPoster> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SkuPoster::getSkuId, skuId);
        return list(queryWrapper);
    }

    @Override
    public boolean deleteBySkuId(Long skuId) {
        LambdaQueryWrapper<SkuPoster> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SkuPoster::getSkuId, skuId);
        return remove(queryWrapper);
    }

    @Override
    public boolean deleteBySkuIds(List<Long> skuIds) {
        LambdaQueryWrapper<SkuPoster> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SkuPoster::getSkuId, skuIds);
        return remove(queryWrapper);
    }
}




