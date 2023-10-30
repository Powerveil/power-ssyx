package com.power.ssyx.order.service.impl;

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
import com.power.ssyx.enums.SkuType;
import com.power.ssyx.model.order.CartInfo;
import com.power.ssyx.model.order.OrderInfo;
import com.power.ssyx.order.mapper.OrderInfoMapper;
import com.power.ssyx.order.service.OrderInfoService;
import com.power.ssyx.vo.order.OrderConfirmVo;
import com.power.ssyx.vo.order.OrderSubmitVo;
import com.power.ssyx.vo.product.SkuStockLockVo;
import com.power.ssyx.vo.user.LeaderAddressVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
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

        // 返回订单id

        return null;
    }

    @Override
    public Result getOrderInfoById(Long orderId) {
        return null;
    }


}




