package com.power.ssyx.order.listener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.power.ssyx.client.product.ProductFeignClient;
import com.power.ssyx.common.constant.RedisConst;
import com.power.ssyx.model.order.OrderItem;
import com.power.ssyx.order.mapper.OrderInfoMapper;
import com.power.ssyx.order.mapper.OrderItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName RedisKeyExpireListener
 * @Description TODO(一句话描述该类的功能)
 * @Author Powerveil
 * @Date 2024/5/31 16:37
 * @Version 1.0
 */
@Component
public class OrderExpireListener extends KeyExpirationEventMessageListener {

    @Autowired
    private OrderInfoMapper orderInfoMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ProductFeignClient productFeignClient;

    public OrderExpireListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    /**
     * 重写 onMessage方法
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        // 过期的key
        String expireKey = message.toString();
        System.out.println("过期的key：" + expireKey);
        if (expireKey.contains(RedisConst.ORDER_TEMP_SKU_MAP)) {
            // 1.删除订单基本表中的数据
            // 1.1取出订单号
            int index = expireKey.lastIndexOf(":") + 1;
            String orderNo = expireKey.substring(index);
//            LambdaQueryWrapper<OrderInfo> orderInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
//            orderInfoLambdaQueryWrapper.eq(OrderInfo::getOrderNo, orderNo);
//            // 1.2删除订单基本表中的数据
//            orderInfoMapper.delete(orderInfoLambdaQueryWrapper);
            // 2.删除订单项表中的数据
            Long orderId = orderInfoMapper.queryOrderIdByOrderNo(orderNo);
            LambdaQueryWrapper<OrderItem> orderItemLambdaQueryWrapper = new LambdaQueryWrapper<>();
            orderItemLambdaQueryWrapper.eq(OrderItem::getOrderId, orderId);
            // 2.1取出订单项数据，为解锁取消库存做准备
            List<OrderItem> orderItems = orderItemMapper.selectList(orderItemLambdaQueryWrapper);
            // 将orderItems转为map，key为skuId，value为skuNum
            Map<Long, Integer> map = orderItems.stream()
                    .collect(Collectors.toMap(OrderItem::getSkuId, OrderItem::getSkuNum));
            // 2.2删除订单项的数据
//            orderItemMapper.delete(orderItemLambdaQueryWrapper);
            // 3.解锁取消库存
            productFeignClient.unlockStockAndCancel(map, orderNo);
        }
    }
}
