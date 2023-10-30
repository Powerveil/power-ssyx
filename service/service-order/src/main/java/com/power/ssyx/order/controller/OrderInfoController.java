package com.power.ssyx.order.controller;

import com.power.ssyx.common.result.Result;
import com.power.ssyx.order.service.OrderInfoService;
import com.power.ssyx.vo.order.OrderSubmitVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Powerveil
 * @Date 2023/10/28 20:27
 */
@RestController
@RequestMapping("/api/order")
public class OrderInfoController {

    @Autowired
    private OrderInfoService orderInfoService;


    @ApiOperation("确认订单")
    @GetMapping("/auth/confirmOrder")
    public Result confirm() {
        return orderInfoService.confirmOrder();
    }

    @ApiOperation("生成订单")
    @PostMapping("/auth/submitOrder")
    public Result submitOrder(@RequestBody OrderSubmitVo orderParamVo, HttpServletRequest request) {
        return orderInfoService.submitOrder(orderParamVo);
    }

    @ApiOperation("获取订单详情")
    @GetMapping("/auth/getOrderInfoById/{orderId}")
    public Result getOrderInfoById(@PathVariable("orderId") Long orderId) {
        return orderInfoService.getOrderInfoById(orderId);
    }
}
