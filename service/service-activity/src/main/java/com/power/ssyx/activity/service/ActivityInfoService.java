package com.power.ssyx.activity.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.model.activity.ActivityInfo;
import com.power.ssyx.model.activity.ActivityRule;
import com.power.ssyx.model.order.CartInfo;
import com.power.ssyx.vo.activity.ActivityRuleVo;
import com.power.ssyx.vo.order.CartInfoVo;
import com.power.ssyx.vo.order.OrderConfirmVo;

import java.util.List;
import java.util.Map;

/**
 * @author power
 * @description 针对表【activity_info(活动表)】的数据库操作Service
 * @createDate 2023-08-21 22:53:23
 */
public interface ActivityInfoService extends IService<ActivityInfo> {

    Result getPageList(Integer page, Integer limit);

    Result get(Integer id);

    Result saveActivityInfo(ActivityInfo activityInfo);

    Result updateActivityInfoById(ActivityInfo activityInfo);

    Result deleteActivityInfoById(Integer id);

    Result deleteActivityInfoByIds(List<Long> ids);

    Result findActivityRuleList(Long id);

    Result saveActivityRule(ActivityRuleVo activityRuleVo);

    // 根据关键字查询匹配sku信息
    Result findSkuInfoByKeyword(String keyword);

    // 根据skuId列表获取促销信息
    Map<Long, List<String>> findActivity(List<Long> skuIdList);

    // 根据SkuId获取营销数据和优惠卷
    Map<String, Object> findActivityAndCoupon(Long skuId, Long userId);

    // 根据skuId获取活动规则数据
    List<ActivityRule> findActivityRuleBySkuId(Long skuId);

    // 获取购物车里面满足条件优惠卷和活动的信息
    OrderConfirmVo findCartActivityAndCoupon(List<CartInfo> cartInfoList, Long userId);

    // 获取购物车对应规则数据
    List<CartInfoVo> findCartActivityList(List<CartInfo> cartInfoList);


}
