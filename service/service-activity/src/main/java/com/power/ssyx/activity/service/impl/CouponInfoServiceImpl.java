package com.power.ssyx.activity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.power.ssyx.activity.mapper.CouponInfoMapper;
import com.power.ssyx.activity.mapper.CouponRangeMapper;
import com.power.ssyx.activity.mapper.CouponUseMapper;
import com.power.ssyx.activity.service.CouponInfoService;
import com.power.ssyx.activity.service.CouponRangeService;
import com.power.ssyx.client.product.ProductFeignClient;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.common.result.ResultCodeEnum;
import com.power.ssyx.contants.SystemConstants;
import com.power.ssyx.enums.CouponRangeType;
import com.power.ssyx.model.activity.CouponInfo;
import com.power.ssyx.model.activity.CouponRange;
import com.power.ssyx.model.order.CartInfo;
import com.power.ssyx.model.product.Category;
import com.power.ssyx.model.product.SkuInfo;
import com.power.ssyx.vo.activity.CouponRuleVo;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
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

    @Autowired
    private CouponRangeMapper couponRangeMapper;

    private CouponInfoService couponInfoServiceProxy;

    @Autowired
    private CouponUseMapper couponUseMapper;


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
        CouponInfo couponInfo = this.getById(id);
        Integer type = couponInfo.getCouponType().getCode();

        // TODO 这里会不会出现类型既有SKU又有CATEGORY
        // 第二步 根据优惠卷id查询coupon_range 查询里面对应的range_id
        //       如果规则类型 SKU             range_id就是skuId值
        //       如果规则类型 CATEGORY        range_id就是分类Id值

        LambdaQueryWrapper<CouponRange> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CouponRange::getCouponId, id);
        queryWrapper.eq(CouponRange::getRangeType, type);

        // 获取rangeIds
        List<Long> rangeIds = couponRangeService.list(queryWrapper)
                .stream().map(CouponRange::getRangeId).collect(Collectors.toList());
        if (!rangeIds.isEmpty()) {
//            if (SystemConstants.RANGE_TYPE_IS_SKU.equals(type)) {
            // TODO 严重bug
            if (CouponRangeType.SKU.getCode().equals(type)) {
                // 如果规则类型是SKU 得到skuId，远程调用根据多个skuId值获取对应sku信息
                skuInfoList = productFeignClient.getSkuListByIds(rangeIds);
                result.put("skuInfoList", skuInfoList);
//            } else if (SystemConstants.RANGE_TYPE_IS_CATEGORY.equals(type)) {
            } else if (CouponRangeType.CATEGORY.getCode().equals(type)) {
                // 如果规则类型是分类，得到分类Id，远程调用根据多个分类Id值获取对应分类信息
                categoryList = productFeignClient.getCategoryListByIds(rangeIds);
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

        CouponInfo couponInfo = this.getById(couponId);
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

    // 2.根据skuId + userId查询优惠卷信息
    @Override
    public List<CouponInfo> findCouponInfoList(Long skuId, Long userId) {
        // 1.远程调用，根据skuId获取skuInfo
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        // 2.根据条件查询：skuId + 分类id + userId
        List<CouponInfo> couponInfoList =
                baseMapper.selectCouponInfoList(skuId, skuInfo.getCategoryId(), userId);
        return couponInfoList;
    }

    /**
     * 获取购物车可以使用优惠卷列表
     *
     * @param cartInfoList
     * @param userId
     * @return 购物车可以使用优惠卷列表
     */
    @Override
    public List<CouponInfo> findCartCouponInfo(List<CartInfo> cartInfoList, Long userId) {
        // 1.根据userId获取用户 全部优惠卷
        // coupon_use coupon_info
        List<CouponInfo> userAllCouponList = baseMapper.selectCartCouponInfoList(userId);
        if (CollectionUtils.isEmpty(userAllCouponList)) {
            return new ArrayList<>();
        }
        // 2.从第一部返回list集合中，获取所有优惠卷id列表
        List<Long> couponIdList = userAllCouponList.stream()
                .map(CouponInfo::getId)
                .collect(Collectors.toList());
        // 3.查询优惠卷对应的范围，coupon_range
        // couponRangeList
        LambdaQueryWrapper<CouponRange> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(CouponRange::getCouponId, couponIdList);
        List<CouponRange> couponRangeList = couponRangeService.list(queryWrapper);
        // 4.获取优惠卷id 对应skuId列表
        // 优惠卷id进行分组，得到map集合
        //     Map<Long, List<Long>>
        // todo 这里可以优化 couponIdToSkuIdMap在这里只用了一次，而且这个方法里面的时间复杂度为O(n^3)
        Map<Long, List<Long>> couponIdToSkuIdMap = this.findCouponIdToSkuIdMap(cartInfoList, couponRangeList);
        // 5.遍历全部优惠卷集合，判断优惠卷类型
        // 全场通用 sku和分类
        BigDecimal reduceAmount = new BigDecimal(0);
        // 最优优惠卷
        CouponInfo optimalCouponInfo = null;
        // 计算购物车商品的总价
        BigDecimal totalAmount = this.computeTotalAmount(cartInfoList);
        for (CouponInfo couponInfo : userAllCouponList) {
            if (CouponRangeType.ALL.equals(couponInfo.getRangeType())) { // 全场通用
                // 判断是否满足优惠使用门槛
                if (totalAmount.subtract(couponInfo.getConditionAmount()).doubleValue() >= 0) {
                    // 选中优惠卷
                    couponInfo.setIsSelect(SystemConstants.IS_SELECTED);
                }
            } else {
                // 优惠卷id获取对应skuId列表
                List<Long> skuIdList = couponIdToSkuIdMap.get(couponInfo.getId());
                // 当前满足使用范围购物项
                List<CartInfo> currentCartInfoList = cartInfoList.stream()
                        .filter(cartInfo -> skuIdList.contains(cartInfo.getSkuId()))
                        .collect(Collectors.toList());
                BigDecimal currentCartTotalAmount = this.computeTotalAmount(currentCartInfoList);
                if (currentCartTotalAmount.subtract(couponInfo.getConditionAmount()).doubleValue() >= 0) {
                    couponInfo.setIsSelect(SystemConstants.IS_SELECTED);
                }
            }
            if (SystemConstants.IS_SELECTED.equals(couponInfo.getIsSelect())
                    && couponInfo.getAmount().subtract(reduceAmount).doubleValue() > 0) {
                reduceAmount = couponInfo.getAmount();
                optimalCouponInfo = couponInfo;
            }
        }
        // 6.返回List<CouponInfo>
        // 这个是最优的优惠卷
        if (!Objects.isNull(optimalCouponInfo)) {
            optimalCouponInfo.setIsOptimal(1);
        }
        return userAllCouponList;
    }

    // 获取购物车对应优惠卷
    @Override
    public CouponInfo findRangeSkuIdList(List<CartInfo> cartInfoList, Long couponId) {
        // 根据优惠卷id基本信息查询
        CouponInfo couponInfo = baseMapper.selectById(couponId);
        if (Objects.isNull(couponInfo)) {
            return null;
        }
        // 根据couponId查询对应CouponRange数据
        List<CouponRange> couponRangeList = couponRangeMapper.selectList(
                new LambdaQueryWrapper<CouponRange>().eq(CouponRange::getCouponId, couponId)
        );

        // 对应sku信息
        Map<Long, List<Long>> couponIdToSkuIdMap = this.findCouponIdToSkuIdMap(cartInfoList, couponRangeList);
        // 遍历map，得到value值，封装到couponInfo对象
        // 只有一个优惠卷
        List<Long> skuIsList = couponIdToSkuIdMap.entrySet().iterator().next().getValue();
        couponInfo.setSkuIdList(skuIsList);
        return couponInfo;
    }

    // 更新优惠卷使用状态
    @Override
    public Boolean updateCouponInfoUserStatus(Long couponId, Long userId, Long orderId) {
        int count = couponUseMapper.updateCouponInfoUserStatus(couponId, userId, orderId);
        return count > 0;
    }

    private BigDecimal computeTotalAmount(List<CartInfo> cartInfoList) {
        BigDecimal total = new BigDecimal("0");
        for (CartInfo cartInfo : cartInfoList) {
            // 是否选中
            if (SystemConstants.IS_SELECTED.equals(cartInfo.getIsChecked())) {
                BigDecimal itemTotal = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                total = total.add(itemTotal);
            }
        }
        return total;
    }

    // 4.获取优惠卷id 对应skuId列表

    /**
     * 优惠卷id进行分组，得到map集合
     * @param cartInfoList 购物车商品列表
     * @param couponRangeList 用户的全部可用优惠卷 可能出现的情况：<couponId, 空Set集合>
     * @return 优惠卷id进行分组，得到map集合
     */
    private Map<Long, List<Long>> findCouponIdToSkuIdMap(List<CartInfo> cartInfoList,
                                                         List<CouponRange> couponRangeList) {
        Map<Long, List<Long>> couponIdToSkuIdMap = new HashMap<>();
        // couponRangeList数据处理，根据优惠卷id分组
        // todo 为什么要用map进行遍历？可以直接从db中查询然后进行遍历吗？
        Map<Long, List<CouponRange>> couponRangeToRangeListMap = couponRangeList.stream()
                .collect(Collectors.groupingBy(CouponRange::getCouponId));

        // 遍历map集合
        for (Map.Entry<Long, List<CouponRange>> entry : couponRangeToRangeListMap.entrySet()) {
            Long couponId = entry.getKey();
            List<CouponRange> rangeList = entry.getValue();

            // 创建集合 set 存储skuId
            Set<Long> skuIdSet = new HashSet<>();

            for (CartInfo cartInfo : cartInfoList) {
                for (CouponRange couponRange : rangeList) {
                    if (this.isCoupon(cartInfo, couponRange)) {
                        skuIdSet.add(cartInfo.getSkuId());
                    }
//                    // 判断
//                    if (CouponRangeType.SKU.equals(couponRange.getRangeType())
//                            && couponRange.getRangeId().equals(cartInfo.getSkuId())) {
//                        skuIdSet.add(cartInfo.getSkuId());
//                    } else if (CouponRangeType.CATEGORY.equals(couponRange.getRangeType())
//                            && couponRange.getRangeId().equals(cartInfo.getCategoryId())) {
//                        skuIdSet.add(cartInfo.getSkuId());
//                    } else if (CouponRangeType.ALL.equals(couponRange.getRangeType())) {
//                        skuIdSet.add(cartInfo.getSkuId());
//                    }

//                    if (this.isCouponForCategory(cartInfo, couponRange)
//                     || this.isCouponForSku(cartInfo, couponRange)
//                    || (CouponRangeType.ALL.equals(couponRange.getRangeType()))) {
//                        skuIdSet.add(cartInfo.getSkuId());
//                    }
                }
            }
            // 将优惠券ID和对应的SKU ID列表放入Map中
            couponIdToSkuIdMap.put(couponId, new ArrayList<>(skuIdSet));
        }

        return couponIdToSkuIdMap;
    }

    private boolean isCoupon(CartInfo cartInfo, CouponRange couponRange) {
        return this.isCouponForSku(cartInfo, couponRange)
                || this.isCouponForCategory(cartInfo, couponRange)
                || this.isCouponForAll(couponRange);
    }

    private boolean isCouponForSku(CartInfo cartInfo, CouponRange couponRange) {
        return CouponRangeType.SKU.equals(couponRange.getRangeType())
                && couponRange.getRangeId().equals(cartInfo.getSkuId());
    }

    private boolean isCouponForCategory(CartInfo cartInfo, CouponRange couponRange) {
        return CouponRangeType.CATEGORY.equals(couponRange.getRangeType())
                && couponRange.getRangeId().equals(cartInfo.getCategoryId());
    }

    private boolean isCouponForAll(CouponRange couponRange) {
        return CouponRangeType.ALL.equals(couponRange.getRangeType());
    }



}




