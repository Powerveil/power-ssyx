package com.power.ssyx.search.service;

import com.power.ssyx.common.result.Result;

/**
 * @author Powerveil
 * @Date 2023/8/19 17:30
 */
public interface SkuService {

    Result upperSku(Long skuId);

    Result lowerSku(Long skuId);
}
