package com.power.ssyx.cart.controller;

import com.power.ssyx.cart.service.CartInfoService;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.model.order.CartInfo;
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

    // 1.根据skuId选中

    @GetMapping("/checkCart/{skuId}/{isChecked}")
    public Result checkCart(@PathVariable("skuId") Long skuId,
                            @PathVariable("isChecked") Integer isChecked) {
        return cartInfoService.checkCart(skuId, isChecked);
    }

    // 2.全选
    @GetMapping("/checkAllCart/{isChecked}")
    public Result checkAllCart(@PathVariable("isChecked") Integer isChecked) {
        return cartInfoService.checkAllCart(isChecked);
    }

    // 3.批量选中
    @ApiOperation(value = "批量选择购物车")
    @PostMapping("/batchCheckCart/{isChecked}")
    public Result batchCheckCart(@RequestBody List<Long> skuIdList,
                                 @PathVariable("isChecked") Integer isChecked) {
        return cartInfoService.batchCheckCart(skuIdList, isChecked);
    }


    // 获取当前用户购物车选中购物项

    /**
     * 根据用户Id 查询购物车列表
     *
     * @param userId
     * @return
     */
    @GetMapping("/inner/getCartCheckedList/{userId}")
    public List<CartInfo> getCartCheckedList(@PathVariable("userId") Long userId) {
        return cartInfoService.getCartCheckedList(userId);
    }
}
