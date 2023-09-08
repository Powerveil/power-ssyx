package com.power.ssyx.activity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.power.ssyx.activity.mapper.CouponInfoMapper;
import com.power.ssyx.activity.service.CouponInfoService;
import com.power.ssyx.activity.service.CouponRangeService;
import com.power.ssyx.client.product.ProductFeignClient;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.common.result.ResultCodeEnum;
import com.power.ssyx.enums.CouponRangeType;
import com.power.ssyx.model.activity.CouponInfo;
import com.power.ssyx.model.activity.CouponRange;
import com.power.ssyx.model.product.Category;
import com.power.ssyx.model.product.SkuInfo;
import com.power.ssyx.vo.activity.CouponRuleVo;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author power
 * @description 针对表【coupon_info(优惠券信息)】的数据库操作Service实现
 * @createDate 2023-08-21 22:53:23
 */
@Service
public class CouponInfoServiceImpl extends ServiceImpl<CouponInfoMapper, CouponInfo>
        implements CouponInfoService {

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private CouponRangeService couponRangeService;

    private CouponInfoService couponInfoServiceProxy;


    @Override
    public Result getPageList(Integer page, Integer limit) {
        Page<CouponInfo> pageParam = new Page<>(page, limit);

        LambdaQueryWrapper<CouponInfo> queryWrapper = new LambdaQueryWrapper<>();

        IPage<CouponInfo> result = baseMapper.selectPage(pageParam, queryWrapper);

//        // 遍历couponInfoList集合，得到每个CouponInfo对象
        // 向CouponInfo对象封装优惠卷类型到activityTypeString属性里面
        List<CouponInfo> records = result.getRecords();
        records.stream().forEach(item -> {
            item.setCouponTypeString(item.getCouponType().getComment());
            if (Objects.isNull(item.getRangeType())) {
                item.setRangeTypeString(item.getRangeType().getComment());
            }
        });
        return Result.ok(result);
    }

    @Override
    public Result get(Integer id) {
        CouponInfo couponInfo = this.getById(id);
        couponInfo.setCouponTypeString(couponInfo.getCouponType().getComment());
        if (Objects.isNull(couponInfo.getRangeType())) {
            couponInfo.setRangeTypeString(couponInfo.getRangeType().getComment());
        }
        return Result.ok(couponInfo);
    }

    @Override
    public Result saveCouponInfo(CouponInfo couponInfo) {
        // TODO 一定要使用DTO接受数据！！！
        couponInfo.setId(null);// 不能这样做
        // 优惠卷名需要存在
        String couponInfoName = couponInfo.getCouponName();
        if (!StringUtils.hasText(couponInfoName)) {
            return Result.build(null, ResultCodeEnum.ATTR_GROUP_NAME_IS_BLANK);
        }
        // 数据库不能有相同的优惠卷名
        LambdaQueryWrapper<CouponInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CouponInfo::getCouponName, couponInfoName);
        if (count(queryWrapper) > 0) {
            return Result.build(null, ResultCodeEnum.ATTR_GROUP_IS_EXIST);
        }
        if (this.save(couponInfo)) {
            return Result.ok(null);
        }
        return Result.fail("添加属性分组失败");
    }

    @Override
    public Result updateCouponInfoById(CouponInfo couponInfo) {
        // Id不能为空
        if (Objects.isNull(couponInfo.getId())) {
            return Result.build(null, ResultCodeEnum.ID_IS_NULL);
        }
        // 优惠卷名需要存在
        String couponInfoName = couponInfo.getCouponName();
        if (!StringUtils.hasText(couponInfoName)) {
            return Result.build(null, ResultCodeEnum.ATTR_GROUP_NAME_IS_BLANK);
        }
        // 数据库不能有相同的属性分组名
        LambdaQueryWrapper<CouponInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CouponInfo::getCouponName, couponInfoName);
        CouponInfo one = getOne(queryWrapper);
        if (!Objects.isNull(one) && !one.getId().equals(couponInfo.getId())) {
            return Result.build(null, ResultCodeEnum.ATTR_GROUP_IS_EXIST);
        }
        if (this.updateById(couponInfo)) {
            return Result.ok(null);
        }
        return Result.fail("更新属性分组失败");
    }

    @Override
    public Result deleteCouponInfoById(Integer id) {
        if (this.removeById(id)) {
            return Result.ok(null);
        }
        return Result.fail("删除属性分组失败");
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public Result deleteCouponInfoByIds(List<Long> ids) {
        if (this.removeByIds(ids)) {
            return Result.ok(null);
        }
        return Result.fail("批量删除属性分组失败");
    }

    // 4.根据优惠卷id查询规则数据
    @Override
    public Result findCouponRuleList(Long id) {
        Map<String, Object> result = new HashMap<>();
        List<SkuInfo> skuInfoList = new ArrayList<>();
        List<Category> categoryList = new ArrayList<>();
        // 第一步 根据优惠卷id查询优惠卷基本信息 coupon_info表
        CouponInfo couponInfo = getById(id);
        Integer type = couponInfo.getCouponType().getCode();

        // TODO 这里会不会出现类型既有SKU又有CATEGORY
        // 第二步 根据优惠卷id查询coupon_range 查询里面对应的range_id
        //       如果规则类型 SKU             range_id就是skuId值
        //       如果规则类型 CATEGORY        range_id就是分类Id值

        LambdaQueryWrapper<CouponRange> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CouponRange::getCouponId, id);
        queryWrapper.eq(CouponRange::getRangeType, type);

        // 获取SKU Ids
        List<Long> Ids = couponRangeService.list(queryWrapper)
                .stream().map(CouponRange::getRangeId).collect(Collectors.toList());
        if (!Ids.isEmpty()) {
//            if (SystemConstants.RANGE_TYPE_IS_SKU.equals(type)) {
            // TODO 严重bug
            if (CouponRangeType.SKU.getCode().equals(type)) {
                // 如果规则类型是SKU 得到skuId，远程调用根据多个skuId值获取对应sku信息
                skuInfoList = productFeignClient.getSkuListByIds(Ids);
                result.put("skuInfoList", skuInfoList);
//            } else if (SystemConstants.RANGE_TYPE_IS_CATEGORY.equals(type)) {
            } else if (CouponRangeType.CATEGORY.getCode().equals(type)) {
                // 如果规则类型是分类，得到分类Id，远程调用根据多个分类Id值获取对应分类信息
                categoryList = productFeignClient.getCategoryListByIds(Ids);
                result.put("categoryList", categoryList);
            }
        }

//        queryWrapper.eq(CouponRange::getRangeType, SystemConstants.RANGE_TYPE_IS_SKU);
//        // 获取SKU Ids
//        List<Long> skuIds = couponRangeService.list(queryWrapper)
//                .stream().map(CouponRange::getRangeId).collect(Collectors.toList());
//
//        // 获取CATEGORY Ids
//        LambdaQueryWrapper<CouponRange> queryWrapper1 = new LambdaQueryWrapper<>();
//        queryWrapper.eq(CouponRange::getCouponId, id);
//        queryWrapper.eq(CouponRange::getRangeType, SystemConstants.RANGE_TYPE_IS_CATEGORY);
//
//        List<Long> categoryIds = couponRangeService.list(queryWrapper1)
//                .stream().map(CouponRange::getRangeId).collect(Collectors.toList());
//        // 第三步 分别判断封装不同数据
//        // 如果规则类型是SKU 得到skuId，远程调用根据多个skuId值获取对应sku信息
//        if (!skuIds.isEmpty()) {
//            skuInfoList = productFeignClient.getSkuListByIds(skuIds);
//            result.put("skuInfoList", skuInfoList);
//        }
//        // 如果规则类型是分类，得到分类Id，远程调用根据多个分类Id值获取对应分类信息
//        if (!categoryIds.isEmpty()) {
//            categoryList = productFeignClient.getCategoryListByIds(categoryIds);
//            result.put("categoryList", categoryList);
//        }


        return Result.ok(result);
    }

    @Override
    public Result saveCouponRule(CouponRuleVo couponRuleVo) {
        // TODO 添加还是更新？
        // 根据优惠卷id删除规则数据
        Long couponId = couponRuleVo.getCouponId();

        CouponInfo couponInfo = getById(couponId);
        couponInfo.setRangeType(couponRuleVo.getRangeType());
        couponInfo.setAmount(couponRuleVo.getAmount());
        couponInfo.setConditionAmount(couponRuleVo.getConditionAmount());
        couponInfo.setRangeDesc(couponRuleVo.getRangeDesc());
//        // TODO 事务
//        save(couponInfo);

        LambdaQueryWrapper<CouponRange> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CouponRange::getCouponId, couponId);

        List<CouponRange> couponRangeList = couponRuleVo.getCouponRangeList();
        if (!couponRangeList.isEmpty()) {
            couponRangeList.forEach(item -> item.setCouponId(couponId));
        }

        transactionTemplate.execute(status -> {
            couponRangeService.remove(queryWrapper);
            // 更新优惠卷基本信息
            // 防止调用this
            couponInfoServiceProxy = (CouponInfoService) AopContext.currentProxy();
            couponInfoServiceProxy.updateById(couponInfo);
            // 添加优惠卷新规则数据
            couponRangeService.saveBatch(couponRangeList);
            return Boolean.TRUE;
        });

        return Result.ok(null);
    }

    @Override
    public Result findCouponByKeyword2(String keyword, Long couponInfoId) {
        List<SkuInfo> skuInfoByKeyword = productFeignClient.findSkuInfoByKeyword(keyword);

        List<Long> ids = skuInfoByKeyword.stream().map(SkuInfo::getId).collect(Collectors.toList());

        if (!Objects.isNull(ids)) {
            List<Long> existIds = baseMapper.selectSkuIdListExist(ids, couponInfoId);

            // (2) 判断逻辑处理
            skuInfoByKeyword = skuInfoByKeyword.stream().filter(item -> {
                return !existIds.contains(item.getId());
            }).collect(Collectors.toList());
        }

        return Result.ok(skuInfoByKeyword);
    }


}




