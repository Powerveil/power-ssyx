package com.power.ssyx.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.power.ssyx.model.order.OrderItem;
import com.power.ssyx.order.mapper.OrderItemMapper;
import com.power.ssyx.order.service.OrderItemService;
import org.springframework.stereotype.Service;

/**
 * @author power
 * @description 针对表【order_item(订单项信息)】的数据库操作Service实现
 * @createDate 2023-10-28 20:25:38
 */
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem>
        implements OrderItemService {

}




