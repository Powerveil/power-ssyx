package com.power.ssyx.payment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.power.ssyx.client.order.OrderFeignClient;
import com.power.ssyx.common.exception.SsyxException;
import com.power.ssyx.common.result.ResultCodeEnum;
import com.power.ssyx.enums.PaymentStatus;
import com.power.ssyx.model.order.OrderInfo;
import com.power.ssyx.model.order.PaymentInfo;
import com.power.ssyx.mq.constant.MqConst;
import com.power.ssyx.mq.service.RabbitService;
import com.power.ssyx.payment.mapper.PaymentInfoMapper;
import com.power.ssyx.payment.service.PaymentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * @author Powerveil
 * @Date 2023/11/8 21:07
 */
@Service
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo> implements PaymentInfoService {

    @Autowired
    private OrderFeignClient orderFeignClient;

    @Autowired
    private RabbitService rabbitService;


    @Override
    public PaymentInfo getPaymentInfoByOrderNo(String orderNo) {
        PaymentInfo paymentInfo = baseMapper
                .selectOne(new LambdaQueryWrapper<PaymentInfo>().eq(PaymentInfo::getOrderNo, orderNo));
        return paymentInfo;
    }

    @Override
    public PaymentInfo savePaymentInfo(String orderNo) {
        // 远程调用，根据orderNo查询订单信息
        OrderInfo orderInfo = orderFeignClient.getOrderInfo(orderNo);
        if (Objects.isNull(orderInfo)) {
            throw new SsyxException(ResultCodeEnum.DATA_ERROR);
        }
        // 封装到PaymentInfo对象
//        PaymentInfo paymentInfo = BeanCopyUtils.copyBean(orderInfo, PaymentInfo.class);

        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setOrderId(orderInfo.getId());
//        paymentInfo.setPaymentType(paymentType); // 支付方式
        paymentInfo.setUserId(orderInfo.getUserId());
        paymentInfo.setOrderNo(orderInfo.getOrderNo());
        paymentInfo.setPaymentStatus(PaymentStatus.UNPAID);
        String subject = "userId:" + orderInfo.getUserId() + "下订单";
        paymentInfo.setSubject(subject);
        //paymentInfo.setTotalAmount(order.getTotalAmount());
        // TODO 为了测试
        paymentInfo.setTotalAmount(new BigDecimal("0.01"));

        // 调用方法实现添加
        this.save(paymentInfo);
        return paymentInfo;
    }

    /**
     * @param orderNo   createJsapi方法中存入的
     * @param resultMap 3.1.支付成功，修改支付记录表状态：已经支付
     *                  3.2.支付成功，修改订单记录已经支付，看库存扣减
     */
    @Override
    public void paySuccess(String orderNo, Map<String, String> resultMap) {
        // 1.查询当前订单支付记录表状态是否是已经支付
        PaymentInfo paymentInfo =
                getOne(new LambdaQueryWrapper<PaymentInfo>().eq(PaymentInfo::getOrderNo, orderNo));
        if (paymentInfo.getPaymentStatus().equals(PaymentStatus.UNPAID)) {
            return;
        }
        // 2.如果支付记录表支付状态没有支付，更新
        paymentInfo.setPaymentStatus(PaymentStatus.UNPAID);
        paymentInfo.setTradeNo(resultMap.get("ransaction_id"));
        paymentInfo.setCallbackContent(resultMap.toString());
        updateById(paymentInfo);
        // 3.整合RabbiMQ实现 修改订单记录已经支付，库存扣减
        rabbitService.sendMessage(MqConst.EXCHANGE_PAY_DIRECT, MqConst.ROUTING_PAY_SUCCESS, orderNo);
    }
}
