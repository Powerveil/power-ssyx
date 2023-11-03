package com.power.ssyx.cart.service.impl;

import com.power.ssyx.cart.service.CartInfoService;
import com.power.ssyx.client.activity.ActivityFeignClient;
import com.power.ssyx.client.product.ProductFeignClient;
import com.power.ssyx.common.auth.AuthContextHolder;
import com.power.ssyx.common.constant.RedisConst;
import com.power.ssyx.common.exception.SsyxException;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.common.result.ResultCodeEnum;
import com.power.ssyx.common.utils.BeanCopyUtils;
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
public class CartInfoServiceImpl implements CartInfoService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private ActivityFeignClient activityFeignClient;

    // 返回购物车在redis的eky
    private String getCartKey(Long userId) {
        // user:userId:cart
        return RedisConst.USER_KEY_PREFIX + userId + RedisConst.USER_CART_KEY_SUFFIX;
    }


    private BoundHashOperations getCartBoundHashOperations() {
        Long userId = AuthContextHolder.getUserId();
        String cartKey = getCartKey(userId);
        return redisTemplate.boundHashOps(cartKey);
    }

    // 添加商品到购物车
    @Override
    public Result addToCart(Long skuId, Integer skuNum) {
        Long userId = AuthContextHolder.getUserId();
        // 1.因为购物车数据存储到redis里面
        //   从redis里面根据key获取数据，这个key包含userId
        String cartKey = getCartKey(skuId);
        //                    (key, (key, value))
        BoundHashOperations<String, String, CartInfo> hashOperations = getCartBoundHashOperations();
        // 2.根据第一步查询出来的结果，得到的是skuId + skuNum关系
        CartInfo cartInfo = null;
        //   目的：判断是否是第一次添加这个商品到购物车
        //   进行判断，判断结果里面，是否有skuId
        if (hashOperations.hasKey(skuId.toString())) {
            // 3.如果结果里面包含skuId，不是第一次添加
            // 3.1.根据skuId，获取对应数量，更新数量
            cartInfo = hashOperations.get(skuId.toString());
            // 把购物车存在商品之前数量获取数量，在进行数量更新操作
            Integer currentSkuNum = cartInfo.getSkuNum() + skuNum;
            if (currentSkuNum < 1) {
                hashOperations.delete(skuId.toString());
                return Result.ok(null);
            }
            // 判断商品数量不能大于限购数量
            Integer perLimit = cartInfo.getPerLimit();
            if (currentSkuNum > perLimit) {
                throw new SsyxException(ResultCodeEnum.SKU_LIMIT_ERROR);
            }

            // 更新cartInfo对象
            cartInfo.setSkuNum(currentSkuNum);
            cartInfo.setCurrentBuyNum(currentSkuNum);// 预留字段

            // 更新其他值
            cartInfo.setIsChecked(1);// 默认选中结算 （结算前的小对钩）
            cartInfo.setUpdateTime(new Date());
        } else {
            // 4.如果结果里面没有skuId，就是第一次添加
            // 4.1.直接添加
            // 远程调用根据skuId获取skuInfo
            SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
            if (Objects.isNull(skuInfo)) {
                throw new SsyxException(ResultCodeEnum.DATA_ERROR);
            }
            skuNum = 1;
            // 封装cartInfo对象
            cartInfo = BeanCopyUtils.copyBean(skuInfo, CartInfo.class);
            cartInfo.setSkuId(skuId);
//            cartInfo.setCategoryId(skuInfo.getCategoryId());
//            cartInfo.setSkuType(skuInfo.getSkuType());
//            cartInfo.setIsNewPerson(skuInfo.getIsNewPerson());
            cartInfo.setUserId(userId);
            cartInfo.setCartPrice(skuInfo.getPrice());
            cartInfo.setSkuNum(skuNum);
            cartInfo.setCurrentBuyNum(skuNum);
            cartInfo.setSkuType(SkuType.COMMON.getCode());
//            cartInfo.setPerLimit(skuInfo.getPerLimit());
//            cartInfo.setImgUrl(skuInfo.getImgUrl());
//            cartInfo.setSkuName(skuInfo.getSkuName());
//            cartInfo.setWareId(skuInfo.getWareId());
            cartInfo.setIsChecked(1);
            cartInfo.setStatus(1);
            cartInfo.setCreateTime(new Date());
            cartInfo.setUpdateTime(new Date());


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
        BoundHashOperations<String, String, CartInfo> boundHashOperations = getCartBoundHashOperations();
        if (boundHashOperations.hasKey(skuId.toString())) {
            boundHashOperations.delete(skuId.toString());
        }
        return Result.ok(null);
    }

    @Override
    public Result deleteAllCart() {
        BoundHashOperations<String, String, CartInfo> boundHashOperations = getCartBoundHashOperations();
        List<String> skuIds = boundHashOperations.values()
                .stream().map(item -> item.getSkuId().toString())
                .collect(Collectors.toList());

        for (String skuId : skuIds) {
            boundHashOperations.delete(skuId);
        }

        return Result.ok(null);
    }

    @Override
    public Result batchDeleteCart(List<Long> skuIds) {
        BoundHashOperations<String, String, CartInfo> boundHashOperations = getCartBoundHashOperations();
        for (Long skuId : skuIds) {
            boundHashOperations.delete(skuId.toString());
        }
        return Result.ok(null);
    }

    @Override
    public Result cartList() {
        List<CartInfo> cartInfoList = new ArrayList<>();
        // 判断userId TODO 如果没有登录，就应该跳转到登录页面，而不是让他看空的购物车
        Long userId = AuthContextHolder.getUserId();
        if (!Objects.isNull(userId)) {
            // 从redis获取购物车数据
            BoundHashOperations<String, String, CartInfo> boundHashOperations = getCartBoundHashOperations();
            cartInfoList = boundHashOperations.values();
            if (!CollectionUtils.isEmpty(cartInfoList)) {
                // 根据商品添加时间，降序
                Collections.sort(cartInfoList, new Comparator<CartInfo>() {
                    @Override
                    public int compare(CartInfo o1, CartInfo o2) {
                        return o2.getCreateTime().compareTo(o1.getCreateTime());
                    }
                });
            }
        }
        return Result.ok(cartInfoList);
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
        List<CartInfo> cartInfoList = (List<CartInfo>) this.cartList().getData();
        OrderConfirmVo orderConfirmVo = activityFeignClient.findCartActivityAndCoupon(cartInfoList, userId);
        return Result.ok(orderConfirmVo);
    }

    @Override
    public Result checkCart(Long skuId, Integer isChecked) {
        Long userId = AuthContextHolder.getUserId();
        String cartKey = getCartKey(userId);
        // 获取field-value
        BoundHashOperations<String, String, CartInfo> cartBoundHashOperations = getCartBoundHashOperations();

        // 根据field(skuId)获取value(CartInfo)
        CartInfo cartInfo = cartBoundHashOperations.get(skuId.toString());
        if (!Objects.isNull(cartInfo)) {
            cartInfo.setIsChecked(isChecked);

            cartBoundHashOperations.put(skuId.toString(), cartInfo);

            // 设置key过期时间
            this.setCartKeyExpire(cartKey);
        }
        return Result.ok(null);
    }

    @Override
    public Result checkAllCart(Integer isChecked) {
        Long userId = AuthContextHolder.getUserId();
        String cartKey = getCartKey(userId);
        BoundHashOperations<String, String, CartInfo> boundHashOperations = getCartBoundHashOperations();
        List<CartInfo> cartInfoList = boundHashOperations.values();
        for (CartInfo cartInfo : cartInfoList) {
            if (!Objects.isNull(cartInfo)) {
                cartInfo.setIsChecked(isChecked);
                boundHashOperations.put(cartInfo.getSkuId().toString(), cartInfo);
            }
        }
        // 设置过期时间
        this.setCartKeyExpire(cartKey);
        return Result.ok(null);
    }

    @Override
    public Result batchCheckCart(List<Long> skuIdList, Integer isChecked) {
        Long userId = AuthContextHolder.getUserId();
        String cartKey = getCartKey(userId);
        // 获取field-value
        BoundHashOperations<String, String, CartInfo> cartBoundHashOperations = getCartBoundHashOperations();

        for (Long skuId : skuIdList) {
            CartInfo cartInfo = cartBoundHashOperations.get(skuId.toString());
            if (!Objects.isNull(cartInfo)) {
                cartInfo.setIsChecked(isChecked);

                cartBoundHashOperations.put(skuId.toString(), cartInfo);
            }
        }
        // 设置key过期时间
        this.setCartKeyExpire(cartKey);
        return Result.ok(null);
    }

    // 获取当前用户购物车选中购物项
    @Override
    public List<CartInfo> getCartCheckedList(Long userId) {
        String cartKey = getCartKey(userId);
        // 获取field-value
        BoundHashOperations<String, String, CartInfo> cartBoundHashOperations = redisTemplate.boundHashOps(cartKey);

        List<CartInfo> cartInfoList = cartBoundHashOperations.values();
        cartInfoList = cartInfoList.stream()
                .filter(item -> item.getIsChecked().equals(1))
                .collect(Collectors.toList());
        return cartInfoList;
    }

    // 根据userId删除选中购物车记录
    @Override
    public Boolean deleteCartChecked(Long userId) {

        List<CartInfo> cartInfoList = this.getCartCheckedList(userId);

        String cartKey = getCartKey(userId);
        // 获取field-value
        BoundHashOperations<String, String, CartInfo> cartBoundHashOperations = redisTemplate.boundHashOps(cartKey);


        // 获取skuIdList
        List<Long> skuIdList = cartInfoList.stream()
                .filter(cartInfo -> cartInfo.getIsChecked().equals(1))
                .map(CartInfo::getSkuId)
                .collect(Collectors.toList());

        // 遍历删除
        for (Long skuId : skuIdList) {
            cartBoundHashOperations.delete(skuId.toString());
        }

        return true;
    }


    // 设置key 过期时间
    private void setCartKeyExpire(String key) {
        redisTemplate.expire(key, RedisConst.USER_CART_EXPIRE, TimeUnit.SECONDS);
    }

}
