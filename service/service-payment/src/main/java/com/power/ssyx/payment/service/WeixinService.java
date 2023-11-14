package com.power.ssyx.payment.service;

import java.util.Map;

/**
 * @author Powerveil
 * @Date 2023/11/8 21:05
 */
public interface WeixinService {
    // 调用微信支付系统生成预付单
    Map<String, String> createJsapi(String orderNo);

    // 查询订单支付状态
    Map<String, String> queryPayStatus(String orderNo);
}
