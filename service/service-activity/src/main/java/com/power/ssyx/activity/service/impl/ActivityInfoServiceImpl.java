package com.power.ssyx.activity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.power.ssyx.activity.mapper.ActivityInfoMapper;
import com.power.ssyx.activity.mapper.ActivityRuleMapper;
import com.power.ssyx.activity.mapper.ActivitySkuMapper;
import com.power.ssyx.activity.service.ActivityInfoService;
import com.power.ssyx.client.product.ProductFeignClient;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.common.result.ResultCodeEnum;
import com.power.ssyx.model.activity.ActivityInfo;
import com.power.ssyx.model.activity.ActivityRule;
import com.power.ssyx.model.activity.ActivitySku;
import com.power.ssyx.model.product.SkuInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
}




