package com.power.ssyx.order.listener;

import com.power.ssyx.client.product.ProductFeignClient;
import com.power.ssyx.common.constant.RedisConst;
import com.power.ssyx.enums.OrderStatus;
import com.power.ssyx.order.mapper.OrderInfoMapper;
import com.power.ssyx.order.service.OrderInfoService;
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
        System.out.println("过期的key：" + expireKey);
        if (expireKey.contains(RedisConst.ORDER_TEMP_SKU_MAP)) {
            // 1.更新订单状态
            // 1.1取出订单号
            int index = expireKey.lastIndexOf(":") + 1;
            String orderNo = expireKey.substring(index);
//            // 1.2更新订单状态
//            orderInfoMapper.delete(orderInfoLambdaQueryWrapper);
            orderInfoMapper.updateStatusByOrderNo(orderNo, OrderStatus.CANCEL.getCode());
            // 3.解锁取消库存
            // 3.1查询订单项的skuId和skuNum
            Map<Long, Integer> map = orderInfoService.getSkuIdToSkuNumMap(orderNo);
            productFeignClient.unlockStockAndCancel(map, orderNo);
        }
    }


}
