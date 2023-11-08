package com.power.ssyx.client.activity;

import com.power.ssyx.model.activity.CouponInfo;
import com.power.ssyx.model.order.CartInfo;
import com.power.ssyx.vo.order.CartInfoVo;
import com.power.ssyx.vo.order.OrderConfirmVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * @author Powerveil
 * @Date 2023/9/24 23:34
 */
@FeignClient(value = "service-activity")
public interface ActivityFeignClient {

    @ApiOperation(value = "根据skuId列表获取促销信息")
    @PostMapping("/api/activity/inner/findActivity")
    public Map<Long, List<String>> findActivity(@RequestBody List<Long> skuIdList);

    @ApiOperation(value = "根据skuId获取营销数据和优惠卷")
    @GetMapping("/api/activity/inner/findActivityAndCoupon/{skuId}/{userId}")
    public Map<String, Object> findActivityAndCoupon(@PathVariable("skuId") Long skuId,
                                                     @PathVariable("userId") Long userId);

    @ApiOperation(value = "获取购物车满足条件的促销与优惠券信息")
    @PostMapping("/api/activity/inner/findCartActivityAndCoupon/{userId}")
    OrderConfirmVo findCartActivityAndCoupon(@RequestBody List<CartInfo> cartInfoList,
                                             @PathVariable Long userId);

    @ApiOperation(value = "获取购物车对应规则数据")
    @PostMapping("/api/activity/inner/findCartActivityList")
    List<CartInfoVo> findCartActivityList(@RequestBody List<CartInfo> cartInfoList);


    @ApiOperation(value = "获取购物车对应优惠卷")
    @PostMapping("/api/activity/inner/findRangeSkuIdList/{couponId}")
    public CouponInfo findRangeSkuIdList(@RequestBody List<CartInfo> cartInfoList,
                                         @PathVariable("couponId") Long couponId);

    @ApiOperation(value = "更新优惠卷使用状态")
    @GetMapping("/api/activity/inner/updateCouponInfoUserStatus/{couponId}/{userId}/{orderId}")
    public Boolean updateCouponInfoUserStatus(@PathVariable("couponId") Long couponId,
                                              @PathVariable("userId") Long userId,
                                              @PathVariable("orderId") Long orderId);
}
