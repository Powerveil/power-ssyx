package com.power.ssyx.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.power.ssyx.model.product.SkuAttrValue;

import java.util.List;

/**
 * @author power
 * @description 针对表【sku_attr_value(spu属性值)】的数据库操作Service
 * @createDate 2023-08-05 15:19:30
 */
public interface SkuAttrValueService extends IService<SkuAttrValue> {

    // 根据skuId获取SkuAttrValue列表
    List<SkuAttrValue> getAttrValueList(Long skuId);

    // 根据skuId删除SkuAttrValue列表
    boolean deleteBySkuId(Long skuId);

    boolean deleteBySkuIds(List<Long> skuIds);
}
