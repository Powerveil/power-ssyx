package com.power.ssyx.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.power.ssyx.model.activity.CouponInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author power
 * @description 针对表【coupon_info(优惠券信息)】的数据库操作Mapper
 * @createDate 2023-08-21 22:53:23
 * @Entity com.power.ssyx.activity.domain.CouponInfo
 */
@Mapper
public interface CouponInfoMapper extends BaseMapper<CouponInfo> {

    List<Long> selectSkuIdListExist(@Param("ids") List<Long> ids, @Param("couponInfoId") Long couponInfoId);

    List<CouponInfo> selectCouponInfoList(@Param("skuId") Long skuId,
                                          @Param("categoryId") Long categoryId,
                                          @Param("userId") Long userId);
}




