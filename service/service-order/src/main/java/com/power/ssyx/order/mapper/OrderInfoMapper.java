package com.power.ssyx.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.power.ssyx.model.order.OrderInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author power
 * @description 针对表【order_info(订单)】的数据库操作Mapper
 * @createDate 2023-10-28 20:25:38
 * @Entity com.power.ssyx.order.domain.OrderInfo
 */
@Mapper
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {

}




