package com.power.ssyx.client.order;

import com.power.ssyx.model.order.OrderInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author Powerveil
 * @Date 2023/11/8 21:51
 */
@FeignClient(value = "service-order")
public interface OrderFeignClient {

    // 根据orderNo查询订单信息
    @GetMapping("/api/order/inner/getOrderInfo/{orderNo}")
    public OrderInfo getOrderInfo(@PathVariable("orderNo") String orderNo);
}
