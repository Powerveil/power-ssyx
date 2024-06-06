package com.power.ssyx.cart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.power.ssyx.cart.mapper.CartInfoMapper;
import com.power.ssyx.cart.service.CartInfoService;
import com.power.ssyx.client.activity.ActivityFeignClient;
import com.power.ssyx.client.product.ProductFeignClient;
import com.power.ssyx.common.auth.AuthContextHolder;
import com.power.ssyx.common.constant.RedisConst;
import com.power.ssyx.common.exception.SsyxException;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.common.result.ResultCodeEnum;
import com.power.ssyx.common.utils.BeanCopyUtils;
import com.power.ssyx.contants.SystemConstants;
import com.power.ssyx.enums.SkuType;
import com.power.ssyx.model.order.CartInfo;
import com.power.ssyx.model.product.SkuInfo;
import com.power.ssyx.vo.order.OrderConfirmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Powerveil
 * @Date 2023/10/8 15:15
 */
@Service
public class CartInfoServiceImpl extends ServiceImpl<CartInfoMapper, CartInfo> implements CartInfoService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private ActivityFeignClient activityFeignClient;

    @Autowired
    private CartInfoMapper cartInfoMapper;

    // 返回购物车在redis的eky
    private String getCartKey(Long userId) {
        // user:userId:cart
        return RedisConst.USER_KEY_PREFIX + userId + RedisConst.USER_CART_KEY_SUFFIX;
    }


    private BoundHashOperations getCartBoundHashOperations() {
        Long userId = AuthContextHolder.getUserId();
        String cartKey = this.getCartKey(userId);
        return redisTemplate.boundHashOps(cartKey);
    }

    // 添加商品到购物车
    @Override
    public Result addToCart(Long skuId, Integer skuNum) {
        Long userId = AuthContextHolder.getUserId();
        // 1.因为购物车数据存储到redis里面
        //   从redis里面根据key获取数据，这个key包含userId
        String cartKey = this.getCartKey(skuId);
        //                    (key, (key, value))
        BoundHashOperations<String, String, CartInfo> hashOperations = this.getCartBoundHashOperations();
        // 2.根据第一步查询出来的结果，得到的是skuId + skuNum关系
        CartInfo cartInfo = null;
        //   目的：判断是否是第一次添加这个商品到购物车
        //   进行判断，判断结果里面，是否有skuId
        // 保存到数据库
        LambdaQueryWrapper<CartInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CartInfo::getUserId, userId)
                .eq(CartInfo::getSkuId, skuId);
        CartInfo one = this.getOne(queryWrapper);
        if (Objects.isNull(one)) {
            // 3.如果结果里面包含skuId，不是第一次添加
            // 3.1.根据skuId，获取对应数量，更新数量
            // 把购物车存在商品之前数量获取数量，在进行数量更新操作
            Integer currentSkuNum = one.getSkuNum() + skuNum;
            if (currentSkuNum < 1) {
                this.removeById(one.getId());
                // 从有到无删除购物项
                hashOperations.delete(skuId.toString());
                return Result.ok(null);
            }
            // 判断商品数量不能大于限购数量
            Integer perLimit = one.getPerLimit();
            if (currentSkuNum > perLimit) {
                throw new SsyxException(ResultCodeEnum.SKU_LIMIT_ERROR);
            }

            // 更新cartInfo对象
            one.setSkuNum(currentSkuNum);
            one.setCurrentBuyNum(currentSkuNum);// 预留字段

            // 更新其他值
            // todo 默认应该不选中
            one.setIsChecked(SystemConstants.IS_SELECTED);// 默认选中结算 （结算前的小对钩）
            one.setUpdateTime(new Date());
        } else {
            if (skuNum <= 0) {
                // 从无到无抛异常
                throw new SsyxException(ResultCodeEnum.CART_ADD_FAIL);
            }

            // 4.如果结果里面没有skuId，就是第一次添加
            // 4.1.直接添加
            // 远程调用根据skuId获取skuInfo
            SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
            if (Objects.isNull(skuInfo)) {
                throw new SsyxException(ResultCodeEnum.DATA_ERROR);
            }
            skuNum = 1; // 默认第一次添加数量为1
            // 封装cartInfo对象
            cartInfo = BeanCopyUtils.copyBean(skuInfo, CartInfo.class);
            cartInfo.setSkuId(skuId);
            cartInfo.setUserId(userId);
            cartInfo.setCartPrice(skuInfo.getPrice());
            cartInfo.setSkuNum(skuNum);
            cartInfo.setCurrentBuyNum(skuNum); // 预留字段
            cartInfo.setSkuType(SkuType.COMMON.getCode());
            // 下面四个字段数据库表设计的时候有默认值
//            cartInfo.setIsChecked(1); // 默认设置选中
//            cartInfo.setStatus(1); // 默认正常
//            cartInfo.setCreateTime(new Date());
//            cartInfo.setUpdateTime(new Date());


//            cartInfo = new CartInfo();
//            cartInfo.setSkuId(skuId);
//            cartInfo.setCategoryId(skuInfo.getCategoryId());
//            cartInfo.setSkuType(skuInfo.getSkuType());
//            cartInfo.setIsNewPerson(skuInfo.getIsNewPerson());
//            cartInfo.setUserId(userId);
//            cartInfo.setCartPrice(skuInfo.getPrice());
//            cartInfo.setSkuNum(skuNum);
//            cartInfo.setCurrentBuyNum(skuNum);
//            cartInfo.setSkuType(SkuType.COMMON.getCode());
//            cartInfo.setPerLimit(skuInfo.getPerLimit());
//            cartInfo.setImgUrl(skuInfo.getImgUrl());
//            cartInfo.setSkuName(skuInfo.getSkuName());
//            cartInfo.setWareId(skuInfo.getWareId());
//            cartInfo.setIsChecked(1);
//            cartInfo.setStatus(1);
//            cartInfo.setCreateTime(new Date());
//            cartInfo.setUpdateTime(new Date());
            this.save(cartInfo);
        }
        // 5.更新redis缓存
        hashOperations.put(skuId.toString(), cartInfo);
        // 6.设置有效时间
        this.setCartKeyExpire(cartKey);
        return Result.ok(null);
    }

    // 根据skuId删除购物车
    @Override
    public Result deleteCart(Long skuId) {
        BoundHashOperations<String, String, CartInfo> boundHashOperations = this.getCartBoundHashOperations();
        if (Boolean.TRUE.equals(boundHashOperations.hasKey(skuId.toString()))) {
            boundHashOperations.delete(skuId.toString());
        }
        LambdaQueryWrapper<CartInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CartInfo::getUserId, AuthContextHolder.getUserId())
                .eq(CartInfo::getSkuId, skuId);
        this.remove(queryWrapper);
        return Result.ok(null);
    }

    @Override
    public Result deleteAllCart() {
        BoundHashOperations<String, String, CartInfo> boundHashOperations = this.getCartBoundHashOperations();
        List<String> skuIds = boundHashOperations.values()
                .stream().map(item -> item.getSkuId().toString())
                .collect(Collectors.toList());

        cartInfoMapper.deleteAllCartByUserId(AuthContextHolder.getUserId());
        this.batchDelete(skuIds, boundHashOperations);

        return Result.ok(null);
    }

    private void batchDelete(List<String> skuIds, BoundHashOperations<String, String, CartInfo> boundHashOperations) {
        for (String skuId : skuIds) { // 不能使用boundHashOperations.delete(skuIds)
            boundHashOperations.delete(skuId);
        }
    }

    @Override
    public Result batchDeleteCart(List<Long> skuIds) {
        BoundHashOperations<String, String, CartInfo> boundHashOperations = this.getCartBoundHashOperations();
        List<String> collect = skuIds.stream().map(Object::toString).collect(Collectors.toList());
        LambdaQueryWrapper<CartInfo> queryWrapper = new LambdaQueryWrapper<>();
        // 删除数据库
        queryWrapper.eq(CartInfo::getUserId, AuthContextHolder.getUserId())
                .in(CartInfo::getSkuId, skuIds);
        this.remove(queryWrapper);
        // 删除redis
        this.batchDelete(collect, boundHashOperations);
        return Result.ok(null);
    }

    @Override
    public Result cartList() {
        List<CartInfo> cartInfoList = this.getCartInfos();
        return Result.ok(cartInfoList);
    }

    private List<CartInfo> getCartInfos() {
        List<CartInfo> cartInfoList = new ArrayList<>();
        // 判断userId
        Long userId = AuthContextHolder.getUserId();
        if (!Objects.isNull(userId)) {
            // 从redis获取购物车数据
            BoundHashOperations<String, String, CartInfo> boundHashOperations = this.getCartBoundHashOperations();
            cartInfoList = boundHashOperations.values();
            // 检查购物车信息列表是否为空
            if (CollectionUtils.isEmpty(cartInfoList)) {
                // 从数据库中根据用户ID查询购物车信息列表
                cartInfoList = lambdaQuery().eq(CartInfo::getUserId, userId).list();
                // 如果查询到的购物车信息列表为空
                if (!CollectionUtils.isEmpty(cartInfoList)) {
                    // 将购物车信息列表转换为 Map 并存储到缓存中
                    Map<String, CartInfo> collect = cartInfoList.stream()
                            .collect(Collectors.toMap(item -> item.getSkuId().toString(), item -> item));
                    boundHashOperations.putAll(collect);
                }
            }
            // 如果购物车信息列表不为空
            if (!CollectionUtils.isEmpty(cartInfoList)) {
                // 根据商品添加时间降序排序
                cartInfoList.sort(Comparator.comparing(CartInfo::getCreateTime).reversed());
            }
        }
        return cartInfoList;
    }

    /**
     * 带优惠卷和优惠活动的购物车列表
     *
     * @return
     */
    @Override
    public Result activityCartList() {
        // 有点烂的代码
        Long userId = AuthContextHolder.getUserId();
        List<CartInfo> cartInfoList = this.getCartInfos();
        OrderConfirmVo orderConfirmVo = activityFeignClient.findCartActivityAndCoupon(cartInfoList, userId);
        return Result.ok(orderConfirmVo);
    }

    @Override
    public Result checkCart(Long skuId, Integer isChecked) {
        Long userId = AuthContextHolder.getUserId();
        String cartKey = this.getCartKey(userId);
        // 获取field-value
        BoundHashOperations<String, String, CartInfo> cartBoundHashOperations = this.getCartBoundHashOperations();

        // 根据field(skuId)获取value(CartInfo)
        CartInfo cartInfo = cartBoundHashOperations.get(skuId.toString());
        // 检查 cartInfo 对象是否为 null
        if (!Objects.isNull(cartInfo)) {
            // 更新数据库中的购物车商品勾选状态
            cartInfoMapper.updateCheckStatus(userId, Collections.singletonList(skuId), isChecked);
            // 更新内存中的购物车商品勾选状态
            cartInfo.setIsChecked(isChecked);
            // 将更新后的 cartInfo 对象存储到缓存中
            cartBoundHashOperations.put(skuId.toString(), cartInfo);
            // 设置缓存 key 的过期时间
            this.setCartKeyExpire(cartKey);
        }
        return Result.ok(null);
    }

    @Override
    public Result checkAllCart(Integer isChecked) {
        // 获取当前用户的 ID
        Long userId = AuthContextHolder.getUserId();
        // 根据用户 ID 获取购物车的 key
        String cartKey = this.getCartKey(userId);
        // 获取与购物车 key 相关的哈希操作对象
        BoundHashOperations<String, String, CartInfo> boundHashOperations = this.getCartBoundHashOperations();
        // 获取购物车中所有的商品信息列表
        List<CartInfo> cartInfoList = boundHashOperations.values();
        // 遍历购物车商品信息列表
        for (CartInfo cartInfo : cartInfoList) {
            // 检查 cartInfo 对象是否为 null
            if (!Objects.isNull(cartInfo)) {
                // 更新内存中的购物车商品勾选状态
                cartInfo.setIsChecked(isChecked);
                // 将更新后的 cartInfo 对象存储到缓存中
                boundHashOperations.put(cartInfo.getSkuId().toString(), cartInfo);
            }
        }
        // 更新数据库中所有商品的勾选状态
        cartInfoMapper.updateCheckStatus(userId, null, isChecked);
        // 设置缓存 key 的过期时间
        this.setCartKeyExpire(cartKey);
        // 返回操作结果
        return Result.ok(null);
    }

    @Override
    public Result batchCheckCart(List<Long> skuIdList, Integer isChecked) {
        // 获取当前用户的 ID
        Long userId = AuthContextHolder.getUserId();
        // 根据用户 ID 获取购物车的 key
        String cartKey = this.getCartKey(userId);
        // 获取与购物车 key 相关的哈希操作对象（field-value 对）
        BoundHashOperations<String, String, CartInfo> cartBoundHashOperations = this.getCartBoundHashOperations();
        // 遍历 skuIdList 中的每个 SKU ID
        for (Long skuId : skuIdList) {
            // 根据 SKU ID 从缓存中获取对应的购物车信息
            CartInfo cartInfo = cartBoundHashOperations.get(skuId.toString());
            // 检查 cartInfo 对象是否为 null
            if (!Objects.isNull(cartInfo)) {
                // 更新内存中的购物车商品勾选状态
                cartInfo.setIsChecked(isChecked);
                // 将更新后的 cartInfo 对象存储到缓存中
                cartBoundHashOperations.put(skuId.toString(), cartInfo);
            }
        }
        // 更新数据库中指定用户的指定 SKU ID 列表的勾选状态
        cartInfoMapper.updateCheckStatus(userId, skuIdList, isChecked);
        // 设置缓存 key 的过期时间
        this.setCartKeyExpire(cartKey);
        // 返回操作结果
        return Result.ok(null);
    }

    // 获取当前用户购物车选中购物项
    @Override
    public List<CartInfo> getCartCheckedList(Long userId) {
        // 根据用户 ID 获取购物车的 key
        String cartKey = this.getCartKey(userId);
        // 获取与购物车 key 相关的哈希操作对象（field-value 对）
        BoundHashOperations<String, String, CartInfo> cartBoundHashOperations = redisTemplate.boundHashOps(cartKey);
        // 获取缓存中购物车所有商品信息列表
        List<CartInfo> cartInfoList = cartBoundHashOperations.values();
        // 检查购物车信息列表是否为空
        if (CollectionUtils.isEmpty(cartInfoList)) {
            // 如果缓存中没有购物车信息，从数据库中查询用户的购物车信息列表
            cartInfoList = lambdaQuery()
                    .eq(CartInfo::getUserId, userId)
                    .eq(CartInfo::getIsChecked, SystemConstants.IS_SELECTED)
                    .list();
        }
        // 过滤出已勾选的商品
        cartInfoList = cartInfoList.stream()
                .filter(item -> SystemConstants.IS_SELECTED.equals(item.getIsChecked()))
                .collect(Collectors.toList());
        // 返回已勾选的购物车商品信息列表
        return cartInfoList;
    }

    // 根据userId删除选中购物车记录
    @Override
    public Boolean deleteCartChecked(Long userId) {
        List<CartInfo> cartInfoList = this.getCartCheckedList(userId);
        // 获取购物车key
        String cartKey = this.getCartKey(userId);
        // 获取key [field-value,...]
        BoundHashOperations<String, String, CartInfo> cartBoundHashOperations = redisTemplate.boundHashOps(cartKey);
        // 获取skuIdList
        List<Long> skuIdList = cartInfoList.stream()
                .filter(cartInfo -> SystemConstants.IS_SELECTED.equals(cartInfo.getIsChecked()))
                .map(CartInfo::getSkuId)
                .collect(Collectors.toList());
        // 遍历删除
        for (Long skuId : skuIdList) {
            cartBoundHashOperations.delete(skuId.toString());
        }
        // 从数据库中删除
        LambdaQueryWrapper<CartInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CartInfo::getUserId, userId)
                .eq(CartInfo::getIsChecked, SystemConstants.IS_SELECTED);
        this.remove(queryWrapper);
        return Boolean.TRUE;
    }


    // 设置key 过期时间
    private void setCartKeyExpire(String key) {
        redisTemplate.expire(key, RedisConst.USER_CART_EXPIRE, TimeUnit.SECONDS);
    }

}
