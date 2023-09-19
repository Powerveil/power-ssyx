package com.power.ssyx.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.power.ssyx.model.user.UserDelivery;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author power
 * @description 针对表【user_delivery(会员提货记录表)】的数据库操作Mapper
 * @createDate 2023-09-18 21:48:49
 * @Entity com.power.ssyx.domain.UserDelivery
 */
@Mapper
public interface UserDeliveryMapper extends BaseMapper<UserDelivery> {

}




