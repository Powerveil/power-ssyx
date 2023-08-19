package com.power.ssyx.product.api;

import com.power.ssyx.model.product.Category;
import com.power.ssyx.model.product.SkuInfo;
import com.power.ssyx.product.service.CategoryService;
import com.power.ssyx.product.service.SkuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Powerveil
 * @Date 2023/8/19 17:41
 */
@RestController
@RequestMapping("/api/product")
public class ProductInnnerController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SkuInfoService skuInfoService;

    // 根据分裂Id获取分类信息
    @GetMapping("/inner/getCategory/{categoryId}")
    public Category getCategory(@PathVariable("categoryId") Long categoryId) {
        Category category = categoryService.getById(categoryId);
        return category;
    }

    // 根据skuId获取sku信息
    @GetMapping("inner/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable("skuId") Long skuId) {
        return skuInfoService.getById(skuId);
    }
}
