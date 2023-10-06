package com.power.ssyx.activity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.power.ssyx.activity.mapper.ActivityInfoMapper;
import com.power.ssyx.activity.mapper.ActivityRuleMapper;
import com.power.ssyx.activity.mapper.ActivitySkuMapper;
import com.power.ssyx.activity.service.ActivityInfoService;
import com.power.ssyx.activity.service.ActivityRuleService;
import com.power.ssyx.activity.service.ActivitySkuService;
import com.power.ssyx.activity.service.CouponInfoService;
import com.power.ssyx.client.product.ProductFeignClient;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.common.result.ResultCodeEnum;
import com.power.ssyx.enums.ActivityType;
import com.power.ssyx.model.activity.ActivityInfo;
import com.power.ssyx.model.activity.ActivityRule;
import com.power.ssyx.model.activity.ActivitySku;
import com.power.ssyx.model.activity.CouponInfo;
import com.power.ssyx.model.product.SkuInfo;
import com.power.ssyx.vo.activity.ActivityRuleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author power
 * @description 针对表【activity_info(活动表)】的数据库操作Service实现
 * @createDate 2023-08-21 22:53:23
 */
@Service
public class ActivityInfoServiceImpl extends ServiceImpl<ActivityInfoMapper, ActivityInfo>
        implements ActivityInfoService {

    @Autowired
    private ActivityRuleMapper activityRuleMapper;

    @Autowired
    private ActivitySkuMapper activitySkuMapper;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private ActivityRuleService activityRuleService;

    @Autowired
    private ActivitySkuService activitySkuService;

    @Autowired
    private CouponInfoService couponInfoService;


    @Override
    public Result getPageList(Integer page, Integer limit) {
        Page<ActivityInfo> pageParam = new Page<>(page, limit);

        LambdaQueryWrapper<ActivityInfo> queryWrapper = new LambdaQueryWrapper<>();

        IPage<ActivityInfo> result = baseMapper.selectPage(pageParam, queryWrapper);

        // 遍历activityInfoList集合，得到每个ActivityInfo对象
        // 向ActivityInfo对象封装活动类型到activityTypeString属性里面
        List<ActivityInfo> records = result.getRecords();
        records.stream().forEach(item -> {
            item.setActivityTypeString(item.getActivityType().getComment());
        });
        return Result.ok(result);
    }

    @Override
    public Result get(Integer id) {
        ActivityInfo activityInfo = this.getById(id);
        activityInfo.setActivityTypeString(activityInfo.getActivityType().getComment());
        return Result.ok(activityInfo);
    }

    @Override
    public Result saveActivityInfo(ActivityInfo activityInfo) {
        // TODO 一定要使用DTO接受数据！！！
        activityInfo.setId(null);// 不能这样做
        // 活动名需要存在
        String activityInfoName = activityInfo.getActivityName();
        if (!StringUtils.hasText(activityInfoName)) {
            return Result.build(null, ResultCodeEnum.ATTR_GROUP_NAME_IS_BLANK);
        }
        // 数据库不能有相同的活动名
        LambdaQueryWrapper<ActivityInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActivityInfo::getActivityName, activityInfoName);
        if (count(queryWrapper) > 0) {
            return Result.build(null, ResultCodeEnum.ATTR_GROUP_IS_EXIST);
        }
        if (this.save(activityInfo)) {
            return Result.ok(null);
        }
        return Result.fail("添加属性分组失败");
    }

    @Override
    public Result updateActivityInfoById(ActivityInfo activityInfo) {
        // Id不能为空
        if (Objects.isNull(activityInfo.getId())) {
            return Result.build(null, ResultCodeEnum.ID_IS_NULL);
        }
        // 活动名需要存在
        String activityInfoName = activityInfo.getActivityName();
        if (!StringUtils.hasText(activityInfoName)) {
            return Result.build(null, ResultCodeEnum.ATTR_GROUP_NAME_IS_BLANK);
        }
        // 数据库不能有相同的属性分组名
        LambdaQueryWrapper<ActivityInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActivityInfo::getActivityName, activityInfoName);
        ActivityInfo one = getOne(queryWrapper);
        if (!Objects.isNull(one) && !one.getId().equals(activityInfo.getId())) {
            return Result.build(null, ResultCodeEnum.ATTR_GROUP_IS_EXIST);
        }
        if (this.updateById(activityInfo)) {
            return Result.ok(null);
        }
        return Result.fail("更新属性分组失败");
    }

    @Override
    public Result deleteActivityInfoById(Integer id) {
        if (this.removeById(id)) {
            return Result.ok(null);
        }
        return Result.fail("删除属性分组失败");
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public Result deleteActivityInfoByIds(List<Long> ids) {
        if (this.removeByIds(ids)) {
            return Result.ok(null);
        }
        return Result.fail("批量删除属性分组失败");
    }

    @Override
    public Result findActivityRuleList(Long id) {
        Map<String, Object> result = new HashMap<>();
        // 1.根据活动id查询，查询规则列表 activity_rule表
        LambdaQueryWrapper<ActivityRule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActivityRule::getActivityId, id);
        List<ActivityRule> activityRuleList = activityRuleMapper.selectList(queryWrapper);
        result.put("activityRuleList", activityRuleList);
        // 2.根据活动id查询，查询使用规则商品skuid列表 activity_sku表
        LambdaQueryWrapper<ActivitySku> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(ActivitySku::getActivityId, id);
        List<Long> skuIds =
                activitySkuMapper.selectList(queryWrapper1)
                        .stream().map(ActivitySku::getSkuId)
                        .collect(Collectors.toList());

        // 2.1.通过远程调用 service-product模块接口，根据skuid列表 得到商品信息
        // 远程调用得到skuInfoList
        List<SkuInfo> skuInfoList = productFeignClient.getSkuListByIds(skuIds);
        result.put("skuInfoList", skuInfoList);
        return Result.ok(result);
    }

    @Override
    public Result saveActivityRule(ActivityRuleVo activityRuleVo) {
        // 第一步 根据活动id删除之前规则数据
        // TODO 安全性校验
        Long activityId = activityRuleVo.getActivityId();
        ActivityInfo activityInfo = getById(activityId);
        ActivityType activityType = activityInfo.getActivityType();

        List<ActivityRule> activityRuleList = activityRuleVo.getActivityRuleList().stream().map(item -> {
            item.setActivityId(activityId);
            item.setActivityType(activityType);
            return item;
        }).collect(Collectors.toList());


        List<ActivitySku> activitySkuList = activityRuleVo.getActivitySkuList().stream().map(item -> {
            item.setActivityId(activityId);
            return item;
        }).collect(Collectors.toList());


        transactionTemplate.execute((status -> {
            //ActivityRule数据删除
            activityRuleMapper.
                    delete(new LambdaQueryWrapper<ActivityRule>().eq(ActivityRule::getActivityId, activityId));
            //ActivitySku数据删除
            activitySkuMapper.
                    delete(new LambdaQueryWrapper<ActivitySku>().eq(ActivitySku::getActivityId, activityId));
            activityRuleService.saveBatch(activityRuleList);
            activitySkuService.saveBatch(activitySkuList);

            return Boolean.TRUE;
        }));


        return Result.ok(null);
    }

    @Override
    public Result findSkuInfoByKeyword(String keyword) {
        // 第一步 根据关键字查询sku匹配内容列表
        // (1) service-product模块创建接口 根据关键字查询sku匹配内容列表
        // (2) service-activity远程调用得到sku内容列表
        List<SkuInfo> skuInfoByKeyword = productFeignClient.findSkuInfoByKeyword(keyword);

        List<Long> ids = skuInfoByKeyword.stream().map(SkuInfo::getId).collect(Collectors.toList());

        if (!Objects.isNull(ids)) {
            // 第二步 判断添加之前是否参加过活动，如果之前添加过，活动正在进行中，排除商品
            // (1) 查询两张表判断 activity_info 和 activity_sku，编写SQL语句实现
            //TODO 改为set
            List<Long> existIds = baseMapper.selectSkuIdListExist(ids);

            // (2) 判断逻辑处理
            skuInfoByKeyword = skuInfoByKeyword.stream().filter(item -> {
                return !existIds.contains(item.getId());
            }).collect(Collectors.toList());

        }
        return Result.ok(skuInfoByKeyword);
    }

    @Override
    public Map<Long, List<String>> findActivity(List<Long> skuIdList) {
        Map<Long, List<String>> result = new HashMap<>();
        skuIdList.forEach(skuId -> {
            List<ActivityRule> activityRuleList = baseMapper.findActivityRules(skuId);
            if (!CollectionUtils.isEmpty(activityRuleList)) {
                List<String> ruleList = new ArrayList<>();
                // 把规则名称处理
                activityRuleList.forEach(activityRule -> ruleList.add(this.getRuleDesc(activityRule)));
                result.put(skuId, ruleList);
            }
        });

        return result;
    }

    // 根据skuId获取营销数据和优惠卷
    @Override
    public Map<String, Object> findActivityAndCoupon(Long skuId, Long userId) {
        // 1.根据skuId获取sku营销活动，一个活动有多个规则
//        LambdaQueryWrapper<ActivitySku> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(ActivitySku::getSkuId, skuId);
//        ActivitySku activitySku = activitySkuService.getOne(queryWrapper);
//
//        Map<String, Object> activityRuleList = null;
//        if (!Objects.isNull(activitySku)) {
//            activityRuleList = (Map<String, Object>) this.findActivityRuleList(activitySku.getId());
//        }
        List<ActivityRule> activityRuleList = this.findActivityRuleBySkuId(skuId);

        // 2.根据skuId + userId查询优惠卷信息
        List<CouponInfo> couponInfoList = couponInfoService.findCouponInfoList(skuId, userId);

        // 3.封装到map集合，返回
        Map<String, Object> map = new HashMap<>();
        map.put("activityRuleList", activityRuleList);
        map.put("couponInfoList", couponInfoList);
        return map;
    }

    @Override
    public List<ActivityRule> findActivityRuleBySkuId(Long skuId) {
        List<ActivityRule> activityRuleList = baseMapper.findActivityRules(skuId);
        activityRuleList.forEach(item -> {
            String ruleDesc = this.getRuleDesc(item);
            item.setRuleDesc(ruleDesc);
        });
        return activityRuleList;
    }

    //构造规则名称的方法
    private String getRuleDesc(ActivityRule activityRule) {
        ActivityType activityType = activityRule.getActivityType();
        StringBuffer ruleDesc = new StringBuffer();
        if (activityType == ActivityType.FULL_REDUCTION) {
            ruleDesc
                    .append("满")
                    .append(activityRule.getConditionAmount())
                    .append("元减")
                    .append(activityRule.getBenefitAmount())
                    .append("元");
        } else {
            ruleDesc
                    .append("满")
                    .append(activityRule.getConditionNum())
                    .append("元打")
                    .append(activityRule.getBenefitDiscount())
                    .append("折");
        }
        return ruleDesc.toString();
    }
}




