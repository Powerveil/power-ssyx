package com.power.ssyx.cart.receiver;

import com.power.ssyx.cart.service.CartInfoService;
import com.power.ssyx.mq.constant.MqConst;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

/**
 * @author Powerveil
 * @Date 2023/11/3 22:56
 */
@Component
public class CartReceiver {
    @Autowired
    private CartInfoService cartInfoService;


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_DELETE_CART, durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_ORDER_DIRECT),
            key = (MqConst.ROUTING_DELETE_CART)
    ))
    public void deleteCart(Long userId, Message message, Channel channel) throws IOException {
        if (!Objects.isNull(userId)) {
            cartInfoService.deleteCartChecked(userId);
        }

        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
