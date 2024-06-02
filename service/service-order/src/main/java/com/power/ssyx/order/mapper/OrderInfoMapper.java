package com.power.ssyx.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.power.ssyx.model.order.OrderInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author power
 * @description 针对表【order_info(订单)】的数据库操作Mapper
 * @createDate 2023-10-28 20:25:38
 * @Entity com.power.ssyx.order.domain.OrderInfo
 */
@Mapper
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {

    Long queryUserIdByOrderNo(@Param("orderNo") String orderNo);

    Long queryOrderIdByOrderNo(@Param("orderNo") String orderNo);

    boolean updateStatusByOrderNo(@Param("orderNo") String orderNo,
                                  @Param("orderStatus") Integer orderStatus);
}




