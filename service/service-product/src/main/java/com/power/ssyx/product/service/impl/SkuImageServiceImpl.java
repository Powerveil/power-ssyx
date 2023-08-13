package com.power.ssyx.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.power.ssyx.model.product.SkuImage;
import com.power.ssyx.product.mapper.SkuImageMapper;
import com.power.ssyx.product.service.SkuImageService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author power
 * @description 针对表【sku_image(商品图片)】的数据库操作Service实现
 * @createDate 2023-08-05 15:19:30
 */
@Service
public class SkuImageServiceImpl extends ServiceImpl<SkuImageMapper, SkuImage>
        implements SkuImageService {

    @Override
    public List<SkuImage> getSkuImageList(Long skuId) {
        LambdaQueryWrapper<SkuImage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SkuImage::getSkuId, skuId);
        queryWrapper.orderByAsc(SkuImage::getSort);
        return list(queryWrapper);
    }

    @Override
    public boolean deleteBySkuId(Long skuId) {
        LambdaQueryWrapper<SkuImage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SkuImage::getSkuId, skuId);
        return remove(queryWrapper);
    }

    @Override
    public boolean deleteBySkuIds(List<Long> skuIds) {
        LambdaQueryWrapper<SkuImage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SkuImage::getSkuId, skuIds);
        return remove(queryWrapper);
    }
}




