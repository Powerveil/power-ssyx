package com.power.ssyx.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.power.ssyx.model.activity.CouponUse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author power
 * @description 针对表【coupon_use(优惠券领用表)】的数据库操作Mapper
 * @createDate 2023-08-21 22:53:23
 * @Entity com.power.ssyx.activity.domain.CouponUse
 */
@Mapper
public interface CouponUseMapper extends BaseMapper<CouponUse> {

    int updateCouponInfoUserStatus(@Param("couponId") Long couponId,
                                   @Param("userId") Long userId,
                                   @Param("orderId") Long orderId);
}




