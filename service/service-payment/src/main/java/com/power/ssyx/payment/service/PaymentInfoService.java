package com.power.ssyx.payment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.power.ssyx.model.order.PaymentInfo;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author Powerveil
 * @Date 2023/11/8 21:06
 */
@Service
public interface PaymentInfoService extends IService<PaymentInfo> {
    // 根据orderNo查询支付记录
    PaymentInfo getPaymentInfoByOrderNo(String orderNo);

    // 添加支付记录
    PaymentInfo savePaymentInfo(String orderNo);

    // 3.1.支付成功，修改支付记录表状态：已经支付
    // 3.2.支付成功，修改订单记录已经支付，看库存扣减
    void paySuccess(String outTradeNo, Map<String, String> resultMap);
}
