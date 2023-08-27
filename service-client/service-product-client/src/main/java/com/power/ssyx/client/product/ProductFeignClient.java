package com.power.ssyx.client.product;

import com.power.ssyx.model.product.Category;
import com.power.ssyx.model.product.SkuInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author Powerveil
 * @Date 2023/8/19 17:52
 */
@FeignClient(value = "service-product")
public interface ProductFeignClient {

    @GetMapping("/api/product/inner/getCategory/{categoryId}")
    public Category getCategory(@PathVariable("categoryId") Long categoryId);

    @GetMapping("/api/product/inner/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable("skuId") Long skuId);

    //    @ApiOperation("根据skuIds获取sku列表")
    @GetMapping("/api/product/inner/findSkuInfoList")
    public List<SkuInfo> getSkuListByIds(@RequestParam("ids") List<Long> ids);


    @ApiOperation("根据关键字匹配sku列表")
    @GetMapping("/api/product/inner/findSkuInfoByKeyword/{keyword}")
    public List<SkuInfo> findSkuInfoByKeyword(@PathVariable(value = "keyword") String keyword);
}
