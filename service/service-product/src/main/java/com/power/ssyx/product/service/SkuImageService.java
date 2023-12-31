package com.power.ssyx.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.power.ssyx.model.product.SkuImage;

import java.util.List;

/**
 * @author power
 * @description 针对表【sku_image(商品图片)】的数据库操作Service
 * @createDate 2023-08-05 15:19:30
 */
public interface SkuImageService extends IService<SkuImage> {

    // 根据skuId获取SkuImage列表
    List<SkuImage> getSkuImageList(Long skuId);

    boolean deleteBySkuId(Long skuId);

    boolean deleteBySkuIds(List<Long> skuIds);
}
