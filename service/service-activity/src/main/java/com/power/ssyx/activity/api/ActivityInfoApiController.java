package com.power.ssyx.activity.api;

import com.power.ssyx.activity.service.ActivityInfoService;
import com.power.ssyx.activity.service.CouponInfoService;
import com.power.ssyx.model.activity.CouponInfo;
import com.power.ssyx.model.order.CartInfo;
import com.power.ssyx.vo.order.CartInfoVo;
import com.power.ssyx.vo.order.OrderConfirmVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author Powerveil
 * @Date 2023/9/24 22:51
 */
@RestController
@RequestMapping("/api/activity")
public class ActivityInfoApiController {

    @Autowired
    private ActivityInfoService activityInfoService;

    @Autowired
    private CouponInfoService couponInfoService;

    @ApiOperation(value = "根据skuId列表获取促销信息")
    @PostMapping("/inner/findActivity")
    public Map<Long, List<String>> findActivity(@RequestBody List<Long> skuIdList) {
        return activityInfoService.findActivity(skuIdList);
    }

    @ApiOperation(value = "根据skuId获取营销数据和优惠卷")
    @GetMapping("/inner/findActivityAndCoupon/{skuId}/{userId}")
    public Map<String, Object> findActivityAndCoupon(@PathVariable("skuId") Long skuId,
                                                     @PathVariable("userId") Long userId) {
        return activityInfoService.findActivityAndCoupon(skuId, userId);
    }

    // 获取购物车里面满足条件优惠卷和活动的信息
    @ApiOperation(value = "获取购物车满足条件的促销与优惠券信息")
    @PostMapping("/inner/findCartActivityAndCoupon/{userId}")
    public OrderConfirmVo findCartActivityAndCoupon(@RequestBody List<CartInfo> cartInfoList,
                                                    @PathVariable Long userId) {
        return activityInfoService.findCartActivityAndCoupon(cartInfoList, userId);
    }

    @ApiOperation(value = "获取购物车对应规则数据")
    @GetMapping("/inner/findCartActivityList")
    public List<CartInfoVo> findCartActivityList(@RequestBody List<CartInfo> cartInfoList) {
        return activityInfoService.findCartActivityList(cartInfoList);
    }


    @ApiOperation(value = "获取购物车对应优惠卷")
    @GetMapping("/inner/findRangeSkuIdList/{couponId}")
    public CouponInfo findRangeSkuIdList(@RequestBody List<CartInfo> cartInfoList,
                                         @PathVariable("couponId") Long couponId) {
        return couponInfoService.findRangeSkuIdList(cartInfoList, couponId);
    }

    @ApiOperation(value = "更新优惠卷使用状态")
    @GetMapping("/inner/updateCouponInfoUserStatus/{couponId}/{userId}/{orderId}")
    public Boolean updateCouponInfoUserStatus(@PathVariable("couponId") Long couponId,
                                              @PathVariable("userId") Long userId,
                                              @PathVariable("orderId") Long orderId) {
        return couponInfoService.updateCouponInfoUserStatus(couponId, userId, orderId);
    }

}
