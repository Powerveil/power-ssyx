package com.power.ssyx.payment.controller;

import com.power.ssyx.common.result.Result;
import com.power.ssyx.payment.manager.WeixinManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Powerveil
 * @Date 2023/11/8 21:04
 * 微信支付 API
 */
@Api(tags = "微信支付接口")
@RestController
@RequestMapping("/api/payment/weixin")
@Slf4j
public class WeixinController {

    @Autowired
    private WeixinManager weixinManager;

    @ApiOperation(value = "下单 小程序支付")
    @GetMapping("/createJsapi/{orderNo}")
    public Result createJsapi(@PathVariable("orderNo") String orderNo) {
        return weixinManager.createJsapi(orderNo);
    }

    // 查询订单支付状态
    @GetMapping("/queryPayStatus/{orderNo}")
    public Result queryPayStatus(@PathVariable("orderNo") String orderNo) throws InterruptedException {
        return weixinManager.queryPayStatus(orderNo);
    }
}
