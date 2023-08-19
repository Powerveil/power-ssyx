package com.power.ssyx.search.controller;

import com.power.ssyx.common.result.Result;
import com.power.ssyx.search.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Powerveil
 * @Date 2023/8/19 17:30
 */
@RestController
@RequestMapping("/api/search/sku")
public class SkuApiController {

    @Autowired
    private SkuService skuService;

    @GetMapping("/inner/upperSku/{skuId}")
    public Result upperSku(@PathVariable("skuId") Long skuId) {
        return skuService.upperSku(skuId);
    }

    @GetMapping("/inner/lowerSku/{skuId}")
    public Result lowerSku(@PathVariable("skuId") Long skuId) {
        return skuService.lowerSku(skuId);
    }
}
