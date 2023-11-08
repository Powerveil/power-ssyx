package com.power.ssyx.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.model.order.OrderInfo;
import com.power.ssyx.vo.order.OrderSubmitVo;

/**
 * @author power
 * @description 针对表【order_info(订单)】的数据库操作Service
 * @createDate 2023-10-28 20:25:38
 */
public interface OrderInfoService extends IService<OrderInfo> {

    // 确认订单
    Result confirmOrder();

    // 生成订单
    Result submitOrder(OrderSubmitVo orderParamVo);

    // 获取订单详情
    Result getOrderInfoById(Long orderId);

    // 根据orderNo查询订单信息
    OrderInfo getOrderInfoByOrderNo(String orderNo);
}
