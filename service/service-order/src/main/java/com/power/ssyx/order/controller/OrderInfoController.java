package com.power.ssyx.order.controller;

import com.power.ssyx.common.result.Result;
import com.power.ssyx.model.order.OrderInfo;
import com.power.ssyx.order.service.OrderInfoService;
import com.power.ssyx.vo.order.OrderSubmitVo;
import com.power.ssyx.vo.order.OrderUserQueryVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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

    // 根据orderNo查询订单信息
    @GetMapping("/inner/getOrderInfo/{orderNo}")
    public OrderInfo getOrderInfo(@PathVariable("orderNo") String orderNo) {
        return orderInfoService.getOrderInfoByOrderNo(orderNo);
    }

    // 订单查询
    @ApiOperation(value = "获取用户订单分页列表")
    @GetMapping("/auth/findUserOrderPage/{page}/{limit}")
    public Result findUserOrderPage(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,

            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit,

            @ApiParam(name = "orderVo", value = "查询对象", required = false)
            OrderUserQueryVo orderUserQueryVo) {
        return orderInfoService.findUserOrderPage(page, limit, orderUserQueryVo);
    }
}
