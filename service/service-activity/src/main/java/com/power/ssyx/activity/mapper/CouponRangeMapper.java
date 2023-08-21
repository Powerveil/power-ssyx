package com.power.ssyx.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.power.ssyx.model.activity.CouponRange;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author power
 * @description 针对表【coupon_range(优惠券范围表)】的数据库操作Mapper
 * @createDate 2023-08-21 22:53:23
 * @Entity com.power.ssyx.activity.domain.CouponRange
 */
@Mapper
public interface CouponRangeMapper extends BaseMapper<CouponRange> {

}




