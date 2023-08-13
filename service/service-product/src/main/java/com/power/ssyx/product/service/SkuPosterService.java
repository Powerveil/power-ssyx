package com.power.ssyx.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.power.ssyx.model.product.SkuPoster;

import java.util.List;

/**
 * @author power
 * @description 针对表【sku_poster(商品海报表)】的数据库操作Service
 * @createDate 2023-08-05 15:19:30
 */
public interface SkuPosterService extends IService<SkuPoster> {

    List<SkuPoster> getSkuPosterList(Long skuId);

    boolean deleteBySkuId(Long skuId);

    boolean deleteBySkuIds(List<Long> skuIds);
}
