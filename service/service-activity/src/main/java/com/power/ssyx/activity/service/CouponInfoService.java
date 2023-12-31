package com.power.ssyx.activity.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.model.activity.CouponInfo;
import com.power.ssyx.model.order.CartInfo;
import com.power.ssyx.vo.activity.CouponRuleVo;

import java.util.List;

/**
 * @author power
 * @description 针对表【coupon_info(优惠券信息)】的数据库操作Service
 * @createDate 2023-08-21 22:53:23
 */
public interface CouponInfoService extends IService<CouponInfo> {

    Result getPageList(Integer page, Integer limit);

    Result get(Integer id);

    Result saveCouponInfo(CouponInfo couponInfo);

    Result updateCouponInfoById(CouponInfo couponInfo);

    Result deleteCouponInfoById(Integer id);

    Result deleteCouponInfoByIds(List<Long> ids);

    Result findCouponRuleList(Long id);

    Result saveCouponRule(CouponRuleVo couponRuleVo);

    Result findCouponByKeyword2(String keyword, Long couponInfoId);

    List<CouponInfo> findCouponInfoList(Long skuId, Long userId);

    List<CouponInfo> findCartCouponInfo(List<CartInfo> cartInfoList, Long userId);

    // 获取购物车对应优惠卷
    CouponInfo findRangeSkuIdList(List<CartInfo> cartInfoList, Long couponId);

    // 更新优惠卷使用状态
    Boolean updateCouponInfoUserStatus(Long couponId, Long userId, Long orderId);
}
