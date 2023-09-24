package com.power.ssyx.home.controller;

import com.power.ssyx.client.product.ProductFeignClient;
import com.power.ssyx.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Powerveil
 * @Date 2023/9/22 18:43
 */
@Api(tags = "商品分类")
@RestController
@RequestMapping("/api/home")
public class CategoryApiController {
    @Autowired
    private ProductFeignClient productFeignClient;

    @ApiOperation(value = "获取分类信息")
    @GetMapping("/category")
    public Result index() {
        return Result.ok(productFeignClient.findAllCategoryList());
    }

//    // 查询分类商品
//    @GetMapping("/{page}/{limit}")
//    public Result listSku(@PathVariable("page") Long page,
//                          @PathVariable("limit") Long limit,
//                          SkuEsQueryVo skuEsQueryVo) {
//
//    }


}
