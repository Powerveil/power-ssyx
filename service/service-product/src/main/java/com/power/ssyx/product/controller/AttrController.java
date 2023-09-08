package com.power.ssyx.product.controller;

import com.power.ssyx.common.result.Result;
import com.power.ssyx.model.product.Attr;
import com.power.ssyx.product.service.AttrService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Powerveil
 * @Date 2023/8/5 15:22
 */
@Api(tags = "商品属性接口")
@RestController
//@CrossOrigin
@RequestMapping("/admin/product/attr")
public class AttrController {

    @Autowired
    private AttrService attrService;


    @ApiOperation("根据分组id获取属性列表")
    @GetMapping("/{groupId}")
    public Result getListByGroupId(@PathVariable("groupId") Long groupId) {
        return attrService.getListByGroupId(groupId);
    }

    //2.根据id查询商品属性
    @ApiOperation("根据id查询商品属性")
    @GetMapping("/get/{id}")
    public Result get(@PathVariable(name = "id") Long id) {
        return attrService.get(id);
    }

    //3.添加商品属性
    @ApiOperation("添加商品属性")
    @PostMapping("/save")
    public Result saveAttr(@RequestBody Attr category) {
        return attrService.saveAttr(category); // TODO 使用DTO id什么的不能传进来
    }

    //4.修改商品属性
    @ApiOperation("修改商品属性")
    @PutMapping("/update")
    public Result updateAttrById(@RequestBody Attr category) {
        return attrService.updateAttrById(category); // TODO 使用DTO id什么的不能传进来
    }

    //5.根据id删除商品属性
    @ApiOperation("根据id删除商品属性")
    @DeleteMapping("/remove/{id}")
    public Result deleteAttrById(@PathVariable(name = "id") Long id) {
        return attrService.deleteAttrById(id);
    }

    //6.批量删除商品属性
    @ApiOperation("批量删除商品属性")
    @DeleteMapping("/batchRemove")
    public Result deleteAttrByIds(@RequestBody List<Long> ids) {
        return attrService.deleteAttrByIds(ids);
    }
}
