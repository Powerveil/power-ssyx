package com.power.ssyx.sys.controller;

import com.power.ssyx.common.result.Result;
import com.power.ssyx.model.sys.Ware;
import com.power.ssyx.sys.service.WareService;
import com.power.ssyx.vo.product.WareQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Powerveil
 * @Date 2023/8/3 23:01
 */
@RestController
//@CrossOrigin
@Api(tags = "仓库接口")
@RequestMapping("/admin/sys/ware")
public class WareController {
    @Autowired
    private WareService wareService;


    @ApiOperation("分页查询仓库列表")
    @GetMapping("/{page}/{limit}")
    public Result getPageList(@PathVariable("page") Integer page,
                              @PathVariable("limit") Integer limit,
                              WareQueryVo wareQueryVo) {
        return wareService.getPageList(page, limit, wareQueryVo);
    }

    // 根据id查询仓库
    @ApiOperation("根据id查询仓库")
    @GetMapping("/get/{id}")
    public Result get(@PathVariable(name = "id") Long id) {
        return wareService.get(id);
    }

    // 添加仓库
    @ApiOperation("添加仓库")
    @PostMapping("/save")
    public Result saveWare(@RequestBody Ware ware) {
        return wareService.saveWare(ware);
    }

    // 修改仓库
    @ApiOperation("修改仓库")
    @PutMapping("/update")
    public Result updateWareById(@RequestBody Ware ware) {
        return wareService.updateWareById(ware); // TODO 使用DTO id什么的不能传进来
    }

    // 删除仓库 removeById
    @ApiOperation("删除仓库")
    @DeleteMapping("/remove/{id}")
    public Result removeWareById(@PathVariable("id") Long id) {
        return wareService.removeWareById(id);
    }

    // 批量删除仓库
    @ApiOperation("批量删除仓库")
    @DeleteMapping("/batchRemove")
    public Result deleteWareByIds(@RequestBody List<Long> ids) {
        return wareService.deleteWareByIds(ids);
    }

    @ApiOperation("查询所有仓库")
    @GetMapping("/findAllList")
    public Result findAllList() {
        return wareService.findAllList();
    }
}
