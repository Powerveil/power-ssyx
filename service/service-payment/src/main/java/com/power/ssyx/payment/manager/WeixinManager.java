package com.power.ssyx.payment.manager;

import com.power.ssyx.common.result.Result;
import com.power.ssyx.common.result.ResultCodeEnum;
import com.power.ssyx.contants.SystemConstants;
import com.power.ssyx.payment.service.PaymentInfoService;
import com.power.ssyx.payment.service.WeixinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;
import java.util.Objects;

/**
 * @author Powerveil
 * @Date 2023/11/10 20:18
 */
@Component
public class WeixinManager {

    @Autowired
    private WeixinService weixinService;

    @Autowired
    private PaymentInfoService paymentInfoService;

    public Result createJsapi(@PathVariable("orderNo") String orderNo) {
        Map<String, String> map = weixinService.createJsapi(orderNo);
        return Result.ok(map);
    }

    // 查询订单支付状态
    public Result queryPayStatus(@PathVariable("orderNo") String orderNo) throws InterruptedException {
        Map<String, String> resultMap = null;
        // 这里知道前端会循环调用
        for (int i = 0; i < 3; i++) {
            // 1.调用微信支付系统接口查询订单支付状态
            resultMap = weixinService.queryPayStatus(orderNo);
            // 2.微信支付系统返回值为null，支付失败
            if (!Objects.isNull(resultMap)) break;
            Thread.sleep(10);
        }
        if (Objects.isNull(resultMap)) {
            // 3.支付中，等待
            return Result.build(null, ResultCodeEnum.PAYMENT_WAITING);
        }
        // 4.如果微信支付系统返回值，判断支付成功
        if (SystemConstants.PAYMENT_SUCCESS.equals(resultMap.get("trade_state"))) {
            //更改订单状态，处理支付结果
            String out_trade_no = resultMap.get("out_trade_no");
            paymentInfoService.paySuccess(out_trade_no, resultMap);
            return Result.ok(null);
        }
        // 5.支付中，等待
        return Result.build(null, ResultCodeEnum.PAYMENT_FAIL);
    }
}
