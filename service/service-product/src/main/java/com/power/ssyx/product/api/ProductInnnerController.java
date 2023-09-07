package com.power.ssyx.product.api;

import com.power.ssyx.model.product.Category;
import com.power.ssyx.model.product.SkuInfo;
import com.power.ssyx.product.service.CategoryService;
import com.power.ssyx.product.service.SkuInfoService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @ApiOperation("根据分类Id获取分类信息")
    @GetMapping("/inner/getCategory/{categoryId}")
    public Category getCategory(@PathVariable(value = "categoryId") Long categoryId) {
        Category category = categoryService.getById(categoryId);
        return category;
    }

    @ApiOperation("根据categoryIds获取category列表")
    @GetMapping("/inner/getCategoryListByIds")
    public List<Category> getCategoryListByIds(@RequestParam("ids") List<Long> ids) {
        return categoryService.getCategoryListByIds(ids);
    }


    // 根据skuId获取sku信息
    @ApiOperation("根据skuId获取sku信息")
    @GetMapping("/inner/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable(value = "skuId") Long skuId) {
        return skuInfoService.getById(skuId);
    }

    @ApiOperation("根据skuIds获取sku列表")
    @GetMapping("/inner/findSkuInfoList")
    public List<SkuInfo> getSkuListByIds(@RequestParam("ids") List<Long> ids) {
        return skuInfoService.getSkuListByIds(ids);
    }

    // 根据关键字匹配sku列表
    @ApiOperation("根据关键字匹配sku列表")
    @GetMapping("/inner/findSkuInfoByKeyword/{keyword}")
    public List<SkuInfo> findSkuInfoByKeyword(@PathVariable(value = "keyword") String keyword) {
        return skuInfoService.findSkuInfoByKeyword(keyword);
    }
}
