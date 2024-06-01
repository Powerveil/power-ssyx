package com.power.ssyx.product.api;

import com.power.ssyx.model.product.Category;
import com.power.ssyx.model.product.SkuInfo;
import com.power.ssyx.product.service.CategoryService;
import com.power.ssyx.product.service.SkuInfoService;
import com.power.ssyx.vo.product.SkuInfoVo;
import com.power.ssyx.vo.product.SkuStockLockVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

//    @ApiOperation("根据skuIds获取sku列表")
//    @PostMapping("/inner/findSkuInfoList")
//    public List<SkuInfo> getSkuListByIds(@RequestBody List<Long> ids) {
//        return skuInfoService.getSkuListByIds(ids);
//    }

    // 根据关键字匹配sku列表
    @ApiOperation("根据关键字匹配sku列表")
    @GetMapping("/inner/findSkuInfoByKeyword/{keyword}")
    public List<SkuInfo> findSkuInfoByKeyword(@PathVariable(value = "keyword") String keyword) {
        return skuInfoService.findSkuInfoByKeyword(keyword);
    }

    // 获取所有分类
    @GetMapping("/inner/findAllCategoryList")
    public List<Category> findAllCategoryList() {
        return categoryService.findAllCategoryList();
    }

    // 获取信任专享商品
    @GetMapping("/inner/findNewPersonSkuInfoList")
    public List<SkuInfo> findNewPersonSkuInfoList() {
        return skuInfoService.findNewPersonSkuInfoList();
    }

    // 根据skuId获取sku信息
    @GetMapping("/inner/getSkuInfoVo/{skuId}")
    public SkuInfoVo getSkuInfoVo(@PathVariable("skuId") Long skuId) {
        return skuInfoService.getSkuInfoVo(skuId);
    }

    // 验证和锁定库存
    @ApiOperation(value = "锁定库存")
    @PostMapping("/inner/checkAndLock/{orderNo}")
    public Boolean checkAndLock(@RequestBody List<SkuStockLockVo> skuStockLockVoList,
                                @PathVariable String orderNo) {
        return skuInfoService.checkAndLock(skuStockLockVoList, orderNo);
    }

    // 解锁并取消库存
    @ApiOperation(value = "锁定库存")
    @PostMapping("/inner/unlockStockAndCancel/{orderNo}")
    public Boolean unlockStockAndCancel(@RequestBody Map<Long, Integer> map,
                                        @PathVariable String orderNo) {
        return skuInfoService.unlockStockAndCancel(map, orderNo);
    }

}
