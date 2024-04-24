package com.power.ssyx.activity.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.power.ssyx.activity.mapper.CouponUseMapper;
import com.power.ssyx.activity.service.CouponUseService;
import com.power.ssyx.common.auth.AuthContextHolder;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.enums.CouponStatus;
import com.power.ssyx.model.activity.CouponUse;
import org.springframework.stereotype.Service;

/**
 * @author power
 * @description 针对表【coupon_use(优惠券领用表)】的数据库操作Service实现
 * @createDate 2023-08-21 22:53:23
 */
@Service
public class CouponUseServiceImpl extends ServiceImpl<CouponUseMapper, CouponUse>
        implements CouponUseService {
    // 获取优惠卷
    @Override
    public Result receiveCoupon(Long id) {
        Long userId = AuthContextHolder.getUserId();
        CouponUse couponUse = new CouponUse();
        couponUse.setCouponId(id);
        couponUse.setUserId(userId);
        couponUse.setCouponStatus(CouponStatus.NOT_USED);
        // 类型默认为2：主动获取
        this.save(couponUse);
        return Result.ok(null);
    }
}




