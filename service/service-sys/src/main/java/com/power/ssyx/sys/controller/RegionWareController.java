package com.power.ssyx.sys.controller;

import com.power.ssyx.common.result.Result;
import com.power.ssyx.model.sys.RegionWare;
import com.power.ssyx.sys.service.RegionWareService;
import com.power.ssyx.vo.sys.RegionWareQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Powerveil
 * @Date 2023/8/3 20:58
 */
@RestController
//@CrossOrigin
@Api(tags = "区域仓库接口")
@RequestMapping("/admin/sys/regionWare")
public class RegionWareController {

    @Autowired(required = true)
    private RegionWareService regionWareService;


    // 获取开通区域列表
    @ApiOperation("获取开通区域列表")
    @GetMapping("/{page}/{limit}")
    public Result getPageList(@PathVariable("page") Integer page,
                              @PathVariable("limit") Integer limit,
                              RegionWareQueryVo regionWareQueryVo) {
        return regionWareService.getPageList(page, limit, regionWareQueryVo);
    }

    // 根据id查询开通区域
    @ApiOperation("根据id查询开通区域")
    @GetMapping("/get/{id}")
    public Result get(@PathVariable(name = "id") Long id) {
        return regionWareService.get(id);
    }

    // 添加开通区域
    @ApiOperation("添加开通区域")
    @PostMapping("/save")
    public Result saveRegionWare(@RequestBody RegionWare regionWare) {
        return regionWareService.saveRegionWare(regionWare);
    }

    // 删除开通区域 removeById
    @ApiOperation("删除开通区域")
    @DeleteMapping("/remove/{id}")
    public Result removeRegionWareById(@PathVariable("id") Long id) {
        return regionWareService.removeRegionWareById(id);
    }

    // 更新开通区域状态 updateStatus
    @ApiOperation("取消开通区域")
    @PostMapping("/updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable("id") Long id,
                               @PathVariable("status") Integer status) {
        return regionWareService.updateStatus(id, status);
    }

    // 批量删除开通区域
    @ApiOperation("批量删除开通区域")
    @DeleteMapping("/batchRemove")
    public Result deleteRegionWareByIds(@RequestBody List<Long> ids) {
        return regionWareService.deleteRegionWareByIds(ids);
    }
}
