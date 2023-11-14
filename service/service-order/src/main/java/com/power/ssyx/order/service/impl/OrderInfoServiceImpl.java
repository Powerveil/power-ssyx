package com.power.ssyx.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.power.ssyx.client.activity.ActivityFeignClient;
import com.power.ssyx.client.cart.CartFeignClient;
import com.power.ssyx.client.product.ProductFeignClient;
import com.power.ssyx.client.user.UserFeignClient;
import com.power.ssyx.common.auth.AuthContextHolder;
import com.power.ssyx.common.constant.RedisConst;
import com.power.ssyx.common.exception.SsyxException;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.common.result.ResultCodeEnum;
import com.power.ssyx.common.utils.DateUtil;
import com.power.ssyx.enums.*;
import com.power.ssyx.model.activity.ActivityRule;
import com.power.ssyx.model.activity.CouponInfo;
import com.power.ssyx.model.order.CartInfo;
import com.power.ssyx.model.order.OrderInfo;
import com.power.ssyx.model.order.OrderItem;
import com.power.ssyx.mq.constant.MqConst;
import com.power.ssyx.mq.service.RabbitService;
import com.power.ssyx.order.mapper.OrderInfoMapper;
import com.power.ssyx.order.mapper.OrderItemMapper;
import com.power.ssyx.order.service.OrderInfoService;
import com.power.ssyx.order.service.OrderItemService;
import com.power.ssyx.vo.order.CartInfoVo;
import com.power.ssyx.vo.order.OrderConfirmVo;
import com.power.ssyx.vo.order.OrderSubmitVo;
import com.power.ssyx.vo.product.SkuStockLockVo;
import com.power.ssyx.vo.user.LeaderAddressVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author power
 * @description 针对表【order_info(订单)】的数据库操作Service实现
 * @createDate 2023-10-28 20:25:38
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo>
        implements OrderInfoService {

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private CartFeignClient cartFeignClient;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ActivityFeignClient activityFeignClient;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private RabbitService rabbitService;

    @Autowired
    private OrderItemMapper orderItemMapper;

    // 需要添加一层结构，现在问题逐渐显露
    @Autowired
    private OrderItemService orderItemService;


    @Override
    public Result confirmOrder() {
        // 获取到用户Id
        Long userId = AuthContextHolder.getUserId();

        // 获取用户对应团长信息
        LeaderAddressVo leaderAddressVo = userFeignClient.getUserAddressByUserId(userId);

        // 获取购物车里面选中的商品
        List<CartInfo> cartInfoList = cartFeignClient.getCartCheckedList(userId);

        // 唯一标识订单
        String orderNo = System.currentTimeMillis() + "";
        redisTemplate.opsForValue()
                .set(RedisConst.ORDER_REPEAT + orderNo, orderNo, 24, TimeUnit.HOURS);


        // 获取购物车满足条件活动和优惠卷信息
        OrderConfirmVo orderConfirmVo = activityFeignClient.findCartActivityAndCoupon(cartInfoList, userId);
        // 封装其他值
        orderConfirmVo.setLeaderAddressVo(leaderAddressVo);
        orderConfirmVo.setOrderNo(orderNo);

        return Result.ok(orderConfirmVo);
    }

    @Override
    public Result submitOrder(OrderSubmitVo orderParamVo) {
        // 获取到用户Id
        Long userId = AuthContextHolder.getUserId();

        // 第二步 订单不能重复提交，重复提交验证
        // 通过redis + Lua脚本进行判断
        //// Lua脚本保证原子性操作
        // 1.获取传递过来的订单 orderNo
        String orderNo = orderParamVo.getOrderNo();
        if (!StringUtils.hasText(orderNo)) {
            throw new SsyxException(ResultCodeEnum.ILLEGAL_REQUEST);
        }
        // 2.拿着orderNo 到 redis 进行查询
        // 用这些key进行比较，有的话删除成功返回1，没有的话返回0
        String script = "if(redis.call('get', KEYS[1]) == ARGV[1]) then return redis.call('del', KEYS[1]) else return 0 end";
        // 3.如果redis有相同orderNo，表示正常提交订单，把redis的orderNo删除
        Boolean flag = (Boolean) redisTemplate.execute(new DefaultRedisScript(script, Boolean.class),
                Arrays.asList(RedisConst.ORDER_REPEAT + orderNo), orderNo);
        // 4.如果redis没有相同orderNo，表示重复提交了，不能再往后进行
        if (!flag) {
            throw new SsyxException(ResultCodeEnum.REPEAT_SUBMIT);
        }

        // 第三步 验证库存 并且 锁定库存
        // 比如仓库有10个西红柿，我想买2个西红柿
        // ** 验证库存，查询仓库里面是否有充足的西红柿
        // ** 库存充足，库存锁定 2锁定（目前没有真正减库存）
        // 1.远程调用service-cart模块，获取当前用户购物车商品（选中的购物项）
        List<CartInfo> cartInfoList = cartFeignClient.getCartCheckedList(userId);

        // 2.购物车有很多商品，商品不同类型，重点处理普通类型商品
        List<CartInfo> commonSkuList = cartInfoList.stream()
                .filter(cartInfo -> cartInfo.getSkuType().equals(SkuType.COMMON.getCode()))
                .collect(Collectors.toList());

        // 3.把获取购物车里面普通类型商品List集合，转换List<SkuStockLockVo>
        if (!CollectionUtils.isEmpty(commonSkuList)) {
            List<SkuStockLockVo> commonStockLockVoList = commonSkuList.stream().map(item -> {
                SkuStockLockVo skuStockLockVo = new SkuStockLockVo();
                skuStockLockVo.setSkuId(item.getSkuId());
                skuStockLockVo.setSkuNum(item.getSkuNum());
                return skuStockLockVo;
            }).collect(Collectors.toList());
            // 4.远程调用service-product模块实现锁定商品
            //// 验证库存并锁定库存，保证具备原子性
            Boolean isLockSuccess = productFeignClient.checkAndLock(commonStockLockVoList, orderNo);
            if (!isLockSuccess) { // 锁定失败
                throw new SsyxException(ResultCodeEnum.ORDER_STOCK_FALL);
            }
        }


        // 第四步 下单过程
        // order_info order_item order_log
        Long orderId = this.saveOrder(orderParamVo, cartInfoList);


        // 下单完成，删除购物车记录
        rabbitService.sendMessage(MqConst.EXCHANGE_ORDER_DIRECT,
                MqConst.ROUTING_DELETE_CART,
                userId);

        // 第五步 返回订单id
        return Result.ok(orderId);
    }


    //计算总金额
    private BigDecimal computeTotalAmount(List<CartInfo> cartInfoList) {
        BigDecimal total = new BigDecimal(0);
        for (CartInfo cartInfo : cartInfoList) {
            BigDecimal itemTotal = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
            total = total.add(itemTotal);
        }
        return total;
    }

    /**
     * 计算购物项分摊的优惠减少金额
     * 打折：按折扣分担
     * 现金：按比例分摊
     *
     * @param cartInfoParamList
     * @return
     */
    private Map<String, BigDecimal> computeActivitySplitAmount(List<CartInfo> cartInfoParamList) {
        Map<String, BigDecimal> activitySplitAmountMap = new HashMap<>();

        //促销活动相关信息
        List<CartInfoVo> cartInfoVoList = activityFeignClient.findCartActivityList(cartInfoParamList);

        //活动总金额
        BigDecimal activityReduceAmount = new BigDecimal(0);
        if (!CollectionUtils.isEmpty(cartInfoVoList)) {
            for (CartInfoVo cartInfoVo : cartInfoVoList) {
                ActivityRule activityRule = cartInfoVo.getActivityRule();
                List<CartInfo> cartInfoList = cartInfoVo.getCartInfoList();
                if (null != activityRule) {
                    //优惠金额， 按比例分摊
                    BigDecimal reduceAmount = activityRule.getReduceAmount();
                    activityReduceAmount = activityReduceAmount.add(reduceAmount);
                    if (cartInfoList.size() == 1) {
                        activitySplitAmountMap.put("activity:" + cartInfoList.get(0).getSkuId(), reduceAmount);
                    } else {
                        //总金额
                        BigDecimal originalTotalAmount = new BigDecimal(0);
                        for (CartInfo cartInfo : cartInfoList) {
                            BigDecimal skuTotalAmount = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                            originalTotalAmount = originalTotalAmount.add(skuTotalAmount);
                        }
                        //记录除最后一项是所有分摊金额， 最后一项=总的 - skuPartReduceAmount
                        BigDecimal skuPartReduceAmount = new BigDecimal(0);
                        if (activityRule.getActivityType() == ActivityType.FULL_REDUCTION) {
                            for (int i = 0, len = cartInfoList.size(); i < len; i++) {
                                CartInfo cartInfo = cartInfoList.get(i);
                                if (i < len - 1) {
                                    BigDecimal skuTotalAmount = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                                    //sku分摊金额
                                    BigDecimal skuReduceAmount = skuTotalAmount.divide(originalTotalAmount, 2, RoundingMode.HALF_UP).multiply(reduceAmount);
                                    activitySplitAmountMap.put("activity:" + cartInfo.getSkuId(), skuReduceAmount);

                                    skuPartReduceAmount = skuPartReduceAmount.add(skuReduceAmount);
                                } else {
                                    BigDecimal skuReduceAmount = reduceAmount.subtract(skuPartReduceAmount);
                                    activitySplitAmountMap.put("activity:" + cartInfo.getSkuId(), skuReduceAmount);
                                }
                            }
                        } else {
                            for (int i = 0, len = cartInfoList.size(); i < len; i++) {
                                CartInfo cartInfo = cartInfoList.get(i);
                                if (i < len - 1) {
                                    BigDecimal skuTotalAmount = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));

                                    //sku分摊金额
                                    BigDecimal skuDiscountTotalAmount = skuTotalAmount.multiply(activityRule.getBenefitDiscount().divide(new BigDecimal("10")));
                                    BigDecimal skuReduceAmount = skuTotalAmount.subtract(skuDiscountTotalAmount);
                                    activitySplitAmountMap.put("activity:" + cartInfo.getSkuId(), skuReduceAmount);

                                    skuPartReduceAmount = skuPartReduceAmount.add(skuReduceAmount);
                                } else {
                                    BigDecimal skuReduceAmount = reduceAmount.subtract(skuPartReduceAmount);
                                    activitySplitAmountMap.put("activity:" + cartInfo.getSkuId(), skuReduceAmount);
                                }
                            }
                        }
                    }
                }
            }
        }
        activitySplitAmountMap.put("activity:total", activityReduceAmount);
        return activitySplitAmountMap;
    }

    //优惠卷优惠金额
    private Map<String, BigDecimal> computeCouponInfoSplitAmount(List<CartInfo> cartInfoList, Long couponId) {
        Map<String, BigDecimal> couponInfoSplitAmountMap = new HashMap<>();

        if (null == couponId) return couponInfoSplitAmountMap;
        CouponInfo couponInfo = activityFeignClient.findRangeSkuIdList(cartInfoList, couponId);

        if (null != couponInfo) {
            //sku对应的订单明细
            Map<Long, CartInfo> skuIdToCartInfoMap = new HashMap<>();
            for (CartInfo cartInfo : cartInfoList) {
                skuIdToCartInfoMap.put(cartInfo.getSkuId(), cartInfo);
            }
            //优惠券对应的skuId列表
            List<Long> skuIdList = couponInfo.getSkuIdList();
            if (CollectionUtils.isEmpty(skuIdList)) {
                return couponInfoSplitAmountMap;
            }
            //优惠券优化总金额
            BigDecimal reduceAmount = couponInfo.getAmount();
            if (skuIdList.size() == 1) {
                //sku的优化金额
                couponInfoSplitAmountMap.put("coupon:" + skuIdToCartInfoMap.get(skuIdList.get(0)).getSkuId(), reduceAmount);
            } else {
                //总金额
                BigDecimal originalTotalAmount = new BigDecimal(0);
                for (Long skuId : skuIdList) {
                    CartInfo cartInfo = skuIdToCartInfoMap.get(skuId);
                    BigDecimal skuTotalAmount = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                    originalTotalAmount = originalTotalAmount.add(skuTotalAmount);
                }
                //记录除最后一项是所有分摊金额， 最后一项=总的 - skuPartReduceAmount
                BigDecimal skuPartReduceAmount = new BigDecimal(0);
                if (couponInfo.getCouponType() == CouponType.CASH || couponInfo.getCouponType() == CouponType.FULL_REDUCTION) {
                    for (int i = 0, len = skuIdList.size(); i < len; i++) {
                        CartInfo cartInfo = skuIdToCartInfoMap.get(skuIdList.get(i));
                        if (i < len - 1) {
                            BigDecimal skuTotalAmount = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                            //sku分摊金额
                            BigDecimal skuReduceAmount = skuTotalAmount.divide(originalTotalAmount, 2, RoundingMode.HALF_UP).multiply(reduceAmount);
                            couponInfoSplitAmountMap.put("coupon:" + cartInfo.getSkuId(), skuReduceAmount);

                            skuPartReduceAmount = skuPartReduceAmount.add(skuReduceAmount);
                        } else {
                            BigDecimal skuReduceAmount = reduceAmount.subtract(skuPartReduceAmount);
                            couponInfoSplitAmountMap.put("coupon:" + cartInfo.getSkuId(), skuReduceAmount);
                        }
                    }
                }
            }
            couponInfoSplitAmountMap.put("coupon:total", couponInfo.getAmount());
        }
        return couponInfoSplitAmountMap;
    }

    @Transactional(rollbackFor = {Exception.class})
    public Long saveOrder(OrderSubmitVo orderParamVo, List<CartInfo> cartInfoList) {
        if (CollectionUtils.isEmpty(cartInfoList)) {
            throw new SsyxException(ResultCodeEnum.DATA_ERROR);
        }
//        Long userId = orderParamVo.getUserId();
        Long userId = AuthContextHolder.getUserId();
        //TODO 这里的userId使用哪个更好 AuthContextHolder.getUserId() 还是 orderParamVo.getUserId()
        LeaderAddressVo leaderAddressVo = userFeignClient.getUserAddressByUserId(userId);
        if (Objects.isNull(leaderAddressVo)) {
            throw new SsyxException(ResultCodeEnum.DATA_ERROR);
        }

        // 计算金额
        // TODO 营销活动 和 优惠卷 金额
        Map<String, BigDecimal> activitySplitAmount = this.computeActivitySplitAmount(cartInfoList);
        // 优惠卷金额
        // orderParamVo是带有的couponId
        Map<String, BigDecimal> couponInfoSplitAmount =
                this.computeCouponInfoSplitAmount(cartInfoList, orderParamVo.getCouponId());

        // 封装订单项数据
        List<OrderItem> orderItemList = new ArrayList<>();
        for (CartInfo cartInfo : cartInfoList) {
            OrderItem orderItem = new OrderItem();

            orderItem.setId(null);
            orderItem.setCategoryId(cartInfo.getCategoryId());
            if (cartInfo.getSkuType() == SkuType.COMMON.getCode()) {
                orderItem.setSkuType(SkuType.COMMON);
            } else {
                orderItem.setSkuType(SkuType.SECKILL);
            }
            orderItem.setSkuId(cartInfo.getSkuId());
            orderItem.setSkuName(cartInfo.getSkuName());
            orderItem.setSkuPrice(cartInfo.getCartPrice());
            orderItem.setImgUrl(cartInfo.getImgUrl());
            orderItem.setSkuNum(cartInfo.getSkuNum());
            orderItem.setLeaderId(orderParamVo.getLeaderId());

            // 营销活动金额
            BigDecimal activityAmount = activitySplitAmount.get("activity:" + orderItem.getSkuId());
            if (Objects.isNull(activityAmount)) {
                activityAmount = new BigDecimal(0);
            }
            orderItem.setSplitActivityAmount(activityAmount);
            // 优惠卷金额
            BigDecimal couponAmount = couponInfoSplitAmount.get("coupon:" + orderItem.getSkuId());
            if (Objects.isNull(couponAmount)) {
                couponAmount = new BigDecimal(0);
            }
            orderItem.setSplitCouponAmount(couponAmount);
            // 总金额
            BigDecimal skuTotalAmount = orderItem.getSkuPrice().multiply(new BigDecimal(orderItem.getSkuNum()));

            // 优惠之后的金额
            BigDecimal splitTotalAmount = skuTotalAmount.subtract(activityAmount).subtract(couponAmount);

            orderItem.setSplitTotalAmount(splitTotalAmount);

            orderItemList.add(orderItem);
        }

        // 封装订单OrderInfo数据
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setUserId(userId); // 用户id
        orderInfo.setOrderNo(orderParamVo.getOrderNo()); // 订单号 唯一标识
        orderInfo.setOrderStatus(OrderStatus.UNPAID); // 订单状态，生成成功未支付
        orderInfo.setProcessStatus(ProcessStatus.UNPAID);
        orderInfo.setLeaderId(orderParamVo.getLeaderId()); // 团长id
        orderInfo.setLeaderName(leaderAddressVo.getLeaderName()); // 团长名称

        orderInfo.setLeaderPhone(leaderAddressVo.getLeaderPhone());
        orderInfo.setTakeName(leaderAddressVo.getTakeName());
        orderInfo.setReceiverName(orderParamVo.getReceiverName());
        orderInfo.setReceiverPhone(orderParamVo.getReceiverPhone());
        orderInfo.setReceiverProvince(leaderAddressVo.getProvince());
        orderInfo.setReceiverCity(leaderAddressVo.getCity());
        orderInfo.setReceiverDistrict(leaderAddressVo.getDistrict());
        orderInfo.setReceiverAddress(leaderAddressVo.getDetailAddress());
        orderInfo.setWareId(cartInfoList.get(0).getWareId());

        orderInfo.setCouponId(orderParamVo.getCouponId());


        //计算订单金额
        BigDecimal originalTotalAmount = this.computeTotalAmount(cartInfoList);
        BigDecimal activityAmount = activitySplitAmount.get("activity:total");
        if (null == activityAmount) activityAmount = new BigDecimal(0);
        BigDecimal couponAmount = couponInfoSplitAmount.get("coupon:total");
        if (null == couponAmount) couponAmount = new BigDecimal(0);
        BigDecimal totalAmount = originalTotalAmount.subtract(activityAmount).subtract(couponAmount);
        //计算订单金额
        orderInfo.setOriginalTotalAmount(originalTotalAmount);
        orderInfo.setActivityAmount(activityAmount);
        orderInfo.setCouponAmount(couponAmount);
        orderInfo.setTotalAmount(totalAmount);

        //计算团长佣金
        BigDecimal profitRate = new BigDecimal(0); //orderSetService.getProfitRate();
        BigDecimal commissionAmount = orderInfo.getTotalAmount().multiply(profitRate);
        orderInfo.setCommissionAmount(commissionAmount);

        // 添加数据到订单基本信息表
        // order_info order_item TODO order_log暂时没有添加
        baseMapper.insert(orderInfo);

        // 保存
        for (OrderItem orderItem : orderItemList) {
            orderItem.setOrderId(orderInfo.getId());
        }
        orderItemService.saveBatch(orderItemList);


        // 如果当前订单使用优惠卷，更新优惠卷状态
        if (!Objects.isNull(orderInfo.getCouponId())) {
            // 这个订单id是自增的(orderId)，不是那个orderNo
            activityFeignClient.updateCouponInfoUserStatus(orderInfo.getCouponId(), userId, orderInfo.getId());
        }

        // 下单成功，记录用户购物商品数量，redis
        String orderSkuKey = RedisConst.ORDER_SKU_MAP + orderParamVo.getUserId();
        BoundHashOperations<String, String, Integer> hashOperations = redisTemplate.boundHashOps(orderSkuKey);
        cartInfoList.forEach(cartInfo -> {
            if (hashOperations.hasKey(cartInfo.getSkuId().toString())) {
                Integer orderSkuNum = hashOperations.get(cartInfo.getSkuId().toString()) + cartInfo.getSkuNum();
                hashOperations.put(cartInfo.getSkuId().toString(), orderSkuNum);
            }
        });
        redisTemplate.expire(orderSkuKey, DateUtil.getCurrentExpireTimes(), TimeUnit.SECONDS);

        // 返回订单id

        return orderInfo.getId();
    }

    @Override
    public Result getOrderInfoById(Long orderId) {

        // 根据orderId查询订单基本信息
        OrderInfo orderInfo = baseMapper.selectById(orderId);

        // 根据orderId查询订单所有订单项list列表
        LambdaQueryWrapper<OrderItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderItem::getOrderId, orderId);
        List<OrderItem> orderItemList = orderItemMapper.selectList(queryWrapper);

        // 查询所有订单项封装到每个对象里面
        orderInfo.setOrderItemList(orderItemList);

        return Result.ok(orderInfo);
    }

    // 根据orderNo查询订单信息
    @Override
    public OrderInfo getOrderInfoByOrderNo(String orderNo) {
        OrderInfo orderInfo = this
                .getOne(new LambdaQueryWrapper<OrderInfo>().eq(OrderInfo::getOrderNo, orderNo));
        return orderInfo;
    }

    // 订单支付成功，更新订单状态，扣减库存
    @Override
    public void orderPay(String orderNo) {
        // 查询订单状态是否已经修改完成了支付状态
        OrderInfo orderInfo = this.getOrderInfoByOrderNo(orderNo);
        if (Objects.isNull(orderInfo) || orderInfo.getOrderStatus().equals(OrderStatus.UNPAID)) {
            return;
        }
        // 更新状态
        this.updateOrderStatus(orderInfo.getId());

        // 扣减库存
        rabbitService.sendMessage(MqConst.EXCHANGE_ORDER_DIRECT,
                MqConst.ROUTING_MINUS_STOCK,
                orderNo);
    }

    // 更新状态
    private void updateOrderStatus(Long id) {
        OrderInfo orderInfo = this.getOne(new LambdaQueryWrapper<OrderInfo>().eq(OrderInfo::getId, id));
        orderInfo.setOrderStatus(OrderStatus.WAITING_DELIVER);
        orderInfo.setProcessStatus(ProcessStatus.WAITING_DELIVER);
        this.updateById(orderInfo);
    }


}




