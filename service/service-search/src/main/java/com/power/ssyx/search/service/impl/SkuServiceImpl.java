package com.power.ssyx.search.service.impl;

import com.power.ssyx.client.product.ProductFeignClient;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.enums.SkuType;
import com.power.ssyx.model.product.Category;
import com.power.ssyx.model.product.SkuInfo;
import com.power.ssyx.model.search.SkuEs;
import com.power.ssyx.search.repository.SkuRepository;
import com.power.ssyx.search.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author Powerveil
 * @Date 2023/8/19 17:31
 */
@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    private SkuRepository skuRepository;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Override
    public Result upperSku(Long skuId) {
        // 1.通过远程调用，根据skuId获取相关信息
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        if (Objects.isNull(skuInfo)) {
            return Result.fail(null);
        }
        Category category = productFeignClient.getCategory(skuInfo.getCategoryId());
        // 2.获取数据封装SkuEs对象
        SkuEs skuEs = new SkuEs();

        if (category != null) {
            skuEs.setCategoryId(category.getId());
            skuEs.setCategoryName(category.getName());
        }
        skuEs.setId(skuInfo.getId());
        skuEs.setKeyword(skuInfo.getSkuName() + "," + skuEs.getCategoryName());
        skuEs.setWareId(skuInfo.getWareId());
        skuEs.setIsNewPerson(skuInfo.getIsNewPerson());
        skuEs.setImgUrl(skuInfo.getImgUrl());
        skuEs.setTitle(skuInfo.getSkuName());
        if (skuInfo.getSkuType() == SkuType.COMMON.getCode()) { // 普通商品
            skuEs.setSkuType(0);
            skuEs.setPrice(skuInfo.getPrice().doubleValue());
            skuEs.setStock(skuInfo.getStock());
            skuEs.setSale(skuInfo.getSale());
            skuEs.setPerLimit(skuInfo.getPerLimit());
        } else {
            //TODO 待完善-秒杀商品

        }
        // 3.调用方法添加ES
        SkuEs save = skuRepository.save(skuEs);

        return Result.ok(null);
    }

    @Override
    public Result lowerSku(Long skuId) {
        skuRepository.deleteById(skuId);
        return null;
    }
}
