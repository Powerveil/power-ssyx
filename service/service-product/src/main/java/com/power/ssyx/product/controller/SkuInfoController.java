package com.power.ssyx.product.controller;

import com.power.ssyx.common.result.Result;
import com.power.ssyx.product.service.SkuInfoService;
import com.power.ssyx.vo.product.SkuInfoQueryVo;
import com.power.ssyx.vo.product.SkuInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Powerveil
 * @Date 2023/8/5 15:23
 */
@Api(tags = "sku信息接口")
@RestController
@CrossOrigin
@RequestMapping("/admin/product/skuInfo")
public class SkuInfoController {

    @Autowired
    private SkuInfoService skuInfoService;


    @ApiOperation("sku信息列表")
    @GetMapping("/{page}/{limit}")
    public Result getPageList(@PathVariable("page") Integer page,
                              @PathVariable("limit") Integer limit,
                              SkuInfoQueryVo skuInfoQueryVo) {
        return skuInfoService.getPageList(page, limit, skuInfoQueryVo);
    }

    //2.根据id查询sku信息
    @ApiOperation("根据id查询sku信息")
    @GetMapping("/get/{id}")
    public Result get(@PathVariable(name = "id") Long id) {
        return skuInfoService.get(id);
    }

    //3.添加sku信息
    @ApiOperation("添加sku信息")
    @PostMapping("/save")
    public Result saveSkuInfo(@RequestBody SkuInfoVo skuInfoVo) {
        return skuInfoService.saveSkuInfo(skuInfoVo); // TODO 使用DTO id什么的不能传进来
    }

    //4.修改sku信息
    @ApiOperation("修改sku信息")
    @PutMapping("/update")
    public Result updateSkuInfoById(@RequestBody SkuInfoVo skuInfoVo) {
        return skuInfoService.updateSkuInfoById(skuInfoVo); // TODO 使用DTO id什么的不能传进来
    }

    //5.根据id删除sku信息
    @ApiOperation("根据id删除sku信息")
    @DeleteMapping("/remove/{id}")
    public Result deleteSkuInfoById(@PathVariable(name = "id") Long id) {
        return skuInfoService.deleteSkuInfoById(id);
    }

    //6.批量删除sku信息
    @ApiOperation("批量删除sku信息")
    @DeleteMapping("/batchRemove")
    public Result deleteSkuInfoByIds(@RequestBody List<Long> ids) {
        return skuInfoService.deleteSkuInfoByIds(ids);
    }
}
