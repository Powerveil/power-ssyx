package com.power.ssyx.client.product;

import com.power.ssyx.model.product.Category;
import com.power.ssyx.model.product.SkuInfo;
import com.power.ssyx.vo.product.SkuInfoVo;
import com.power.ssyx.vo.product.SkuStockLockVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Powerveil
 * @Date 2023/8/19 17:52
 */
@FeignClient(value = "service-product")
public interface ProductFeignClient {

    @GetMapping("/api/product/inner/getCategory/{categoryId}")
    public Category getCategory(@PathVariable("categoryId") Long categoryId);

    @GetMapping("/api/product/inner/getCategoryListByIds")
    public List<Category> getCategoryListByIds(@RequestParam("ids") List<Long> ids);

    @GetMapping("/api/product/inner/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable("skuId") Long skuId);

    //    @ApiOperation("根据skuIds获取sku列表")
    @GetMapping("/api/product/inner/findSkuInfoList")
    public List<SkuInfo> getSkuListByIds(@RequestParam("ids") List<Long> ids);

    // 内部调用暂时不需要改
//    @PostMapping("/api/product/inner/findSkuInfoList")
//    public List<SkuInfo> getSkuListByIds(@RequestBody List<Long> ids);


    @ApiOperation("根据关键字匹配sku列表")
    @GetMapping("/api/product/inner/findSkuInfoByKeyword/{keyword}")
    public List<SkuInfo> findSkuInfoByKeyword(@PathVariable(value = "keyword") String keyword);

    @GetMapping("/api/product/inner/findAllCategoryList")
    public List<Category> findAllCategoryList();

    @GetMapping("/api/product/inner/findNewPersonSkuInfoList")
    public List<SkuInfo> findNewPersonSkuInfoList();

    @GetMapping("/api/product/inner/getSkuInfoVo/{skuId}")
    public SkuInfoVo getSkuInfoVo(@PathVariable("skuId") Long skuId);

    // 验证和锁定库存
    @ApiOperation(value = "锁定库存")
    @PostMapping("/api/product/inner/checkAndLock/{orderNo}")
    public Boolean checkAndLock(@RequestBody List<SkuStockLockVo> skuStockLockVoList,
                                @PathVariable String orderNo);
}
