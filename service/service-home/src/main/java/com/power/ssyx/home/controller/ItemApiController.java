package com.power.ssyx.home.controller;

import com.power.ssyx.common.result.Result;
import com.power.ssyx.home.service.ItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Powerveil
 * @Date 2023/10/6 11:32
 */
@Api(tags = "商品详情")
@RestController
@RequestMapping("/api/home")
public class ItemApiController {

    @Autowired
    private ItemService itemService;

    @ApiOperation(value = "获取sku详细信息")
    @GetMapping("/item/{id}")
    public Result index(@PathVariable("id") Long skuId, HttpServletRequest request) {
        return itemService.index(skuId);
    }
}
