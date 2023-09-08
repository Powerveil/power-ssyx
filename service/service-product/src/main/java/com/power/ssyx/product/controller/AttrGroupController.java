package com.power.ssyx.product.controller;

import com.power.ssyx.common.result.Result;
import com.power.ssyx.model.product.AttrGroup;
import com.power.ssyx.product.service.AttrGroupService;
import com.power.ssyx.vo.product.AttrGroupQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Powerveil
 * @Date 2023/8/5 15:23
 */
@Api(tags = "属性分组接口")
@RestController
//@CrossOrigin
@RequestMapping("/admin/product/attrGroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @ApiOperation("属性分组列表")
    @GetMapping("/{page}/{limit}")
    public Result getPageList(@PathVariable("page") Integer page,
                              @PathVariable("limit") Integer limit,
                              AttrGroupQueryVo attrGroupQueryVo) {
        return attrGroupService.getPageList(page, limit, attrGroupQueryVo);
    }

    //2.根据id查询属性分组
    @ApiOperation("根据id查询属性分组")
    @GetMapping("/get/{id}")
    public Result get(@PathVariable(name = "id") Integer id) {
        return attrGroupService.get(id);
    }

    //3.添加属性分组
    @ApiOperation("添加属性分组")
    @PostMapping("/save")
    public Result saveAttrGroup(@RequestBody AttrGroup category) {
        return attrGroupService.saveAttrGroup(category); // TODO 使用DTO id什么的不能传进来
    }

    //4.修改属性分组
    @ApiOperation("修改属性分组")
    @PutMapping("/update")
    public Result updateAttrGroupById(@RequestBody AttrGroup category) {
        return attrGroupService.updateAttrGroupById(category); // TODO 使用DTO id什么的不能传进来
    }

    //5.根据id删除属性分组
    @ApiOperation("根据id删除属性分组")
    @DeleteMapping("/remove/{id}")
    public Result deleteAttrGroupById(@PathVariable(name = "id") Integer id) {
        return attrGroupService.deleteAttrGroupById(id);
    }

    //6.批量删除属性分组
    @ApiOperation("批量删除属性分组")
    @DeleteMapping("/batchRemove")
    public Result deleteAttrGroupByIds(@RequestBody List<Long> ids) {
        return attrGroupService.deleteAttrGroupByIds(ids);
    }

    @ApiOperation("查询所有属性分组")
    @GetMapping("/findAllList")
    public Result findAllList() {
        return attrGroupService.findAllList();
    }
}
