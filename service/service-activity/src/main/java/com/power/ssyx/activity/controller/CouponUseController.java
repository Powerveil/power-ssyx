package com.power.ssyx.activity.controller;

import com.power.ssyx.activity.service.CouponUseService;
import com.power.ssyx.common.result.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName UserCouponInfoController
 * @Description 优惠券领用表相关操作
 * @Author Powerveil
 * @Date 2024/4/24 23:30
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/activity/auth")
public class CouponUseController {
    @Autowired
    private CouponUseService couponUseService;

    // 4.领取优惠卷
    @ApiOperation("领取优惠卷")
    @GetMapping("/getCouponInfo/{id}")
    public Result receiveCoupon(@PathVariable("id") Long id) {
        return couponUseService.receiveCoupon(id);
    }
}
