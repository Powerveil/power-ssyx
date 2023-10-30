package com.power.ssyx.cart.service;

import com.power.ssyx.common.result.Result;
import com.power.ssyx.model.order.CartInfo;

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

    /**
     * 带优惠卷和优惠活动的购物车列表
     *
     * @return
     */
    Result activityCartList();

    Result checkCart(Long skuId, Integer isChecked);

    Result checkAllCart(Integer isChecked);

    Result batchCheckCart(List<Long> skuIdList, Integer isChecked);

    // 获取当前用户购物车选中购物项
    List<CartInfo> getCartCheckedList(Long userId);
}
