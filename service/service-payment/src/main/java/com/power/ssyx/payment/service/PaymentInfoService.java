package com.power.ssyx.payment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.power.ssyx.model.order.PaymentInfo;
import org.springframework.stereotype.Service;

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
}
