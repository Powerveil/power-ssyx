package com.power.ssyx.cart.controller;

import com.power.ssyx.cart.service.CartInfoService;
import com.power.ssyx.common.result.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Powerveil
 * @Date 2023/10/8 15:14
 */
@RestController
@RequestMapping("/api/cart")
public class CartApiController {

    @Autowired
    private CartInfoService cartInfoService;

    // 添加商品到购物车
    // 添加内容，当前登录用户id，skuId，商品数量
    @GetMapping("/addToCart/{skuId}/{skuNum}")
    public Result addToCart(@PathVariable("skuId") Long skuId,
                            @PathVariable("skuNum") Integer skuNum) {
        return cartInfoService.addToCart(skuId, skuNum);
    }

    // 根据skuId删除购物车
    @DeleteMapping("/deleteCart/{skuId}")
    public Result deleteCart(@PathVariable("skuId") Long skuId) {
        return cartInfoService.deleteCart(skuId);
    }

    // 清空购物车
    @ApiOperation(value = "清空购物车")
    @DeleteMapping("/deleteAllCart")
    public Result deleteAllCart() {
        return cartInfoService.deleteAllCart();
    }

    // 批量删除购物车 多个skuId
    @ApiOperation(value = "批量删除购物车")
    @PostMapping("/batchDeleteCart")
    public Result batchDeleteCart(@RequestBody List<Long> skuIds) {
        return cartInfoService.batchDeleteCart(skuIds);
    }

    // 购物车列表
    @GetMapping("/cartList")
    public Result cartList() {
        return cartInfoService.cartList();
    }


    // 查询带优惠卷的购物车
    @GetMapping("/activityCartList")
    public Result activityCartList() {
        return cartInfoService.activityCartList();
    }


}
