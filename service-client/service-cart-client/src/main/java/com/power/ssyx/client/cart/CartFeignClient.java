package com.power.ssyx.client.cart;

import com.power.ssyx.model.order.CartInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author Powerveil
 * @Date 2023/10/28 21:10
 */
@FeignClient(value = "service-cart")
public interface CartFeignClient {
    // 获取当前用户购物车选中购物项

    /**
     * 根据用户Id 查询购物车列表
     *
     * @param userId
     * @return
     */
    @GetMapping("/api/cart/inner/getCartCheckedList/{userId}")
    public List<CartInfo> getCartCheckedList(@PathVariable("userId") Long userId);
}
