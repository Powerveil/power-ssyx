package com.power.ssyx.order.listener;

import com.power.ssyx.client.product.ProductFeignClient;
import com.power.ssyx.common.constant.RedisConst;
import com.power.ssyx.enums.OrderStatus;
import com.power.ssyx.order.mapper.OrderInfoMapper;
import com.power.ssyx.order.service.OrderInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @ClassName RedisKeyExpireListener
 * @Description TODO(一句话描述该类的功能)
 * @Author Powerveil
 * @Date 2024/5/31 16:37
 * @Version 1.0
 */
@Component
@Slf4j
public class OrderExpireListener extends KeyExpirationEventMessageListener {

    @Autowired
    private OrderInfoMapper orderInfoMapper;
    @Autowired
    private OrderInfoService orderInfoService;

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
        if (expireKey.contains(RedisConst.ORDER_TEMP_SKU_MAP)) {
            String orderNo = null;
            try {
                // 1.更新订单状态
                // 1.1取出订单号
                int index = expireKey.lastIndexOf(":") + 1;
                orderNo = expireKey.substring(index);
                log.info("正在处理超时订单,订单号为:{}", orderNo);
//            // 1.2更新订单状态
//            orderInfoMapper.delete(orderInfoLambdaQueryWrapper);
                orderInfoMapper.updateStatusByOrderNo(orderNo, OrderStatus.CANCEL.getCode());
                // 3.解锁取消库存
                // 3.1查询订单项的skuId和skuNum
                Map<Long, Integer> map = orderInfoService.getSkuIdToSkuNumMap(orderNo);
                productFeignClient.unlockStockAndCancel(map, orderNo);
                log.info("超时订单处理完成,订单号为:{}", orderNo);
            } catch (Exception e) {
                log.error("超时订单处理失败,订单号为:{}", orderNo);
                e.printStackTrace();
            }
        }
    }
}
