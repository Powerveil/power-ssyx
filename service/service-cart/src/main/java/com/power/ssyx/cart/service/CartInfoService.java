package com.power.ssyx.cart.service;

import com.power.ssyx.common.result.Result;

import java.util.List;

/**
 * @author Powerveil
 * @Date 2023/10/8 15:14
 */
public interface CartInfoService {
    Result addToCart(Long skuId, Integer skuNum);

    Result deleteCart(Long skuId);

    Result deleteAllCart();

    Result batchDeleteCart(List<Long> skuIds);

    Result cartList();

    Result activityCartList();

}
