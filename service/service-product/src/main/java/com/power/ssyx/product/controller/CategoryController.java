package com.power.ssyx.product.controller;

import com.power.ssyx.common.result.Result;
import com.power.ssyx.model.product.Category;
import com.power.ssyx.product.service.CategoryService;
import com.power.ssyx.vo.product.CategoryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Powerveil
 * @Date 2023/8/5 15:23
 */
@Api(tags = "商品分类接口")
@RestController
@CrossOrigin
@RequestMapping("/admin/product/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @ApiOperation("商品分类列表")
    @GetMapping("/{page}/{limit}")
    public Result getPageList(@PathVariable("page") Integer page,
                              @PathVariable("limit") Integer limit,
                              CategoryVo categoryVo) {
        return categoryService.getPageList(page, limit, categoryVo);
    }

    //2.根据id查询商品分类
    @ApiOperation("根据id查询商品分类")
    @GetMapping("/get/{id}")
    public Result get(@PathVariable(name = "id") Integer id) {
        return categoryService.get(id);
    }

    //3.添加商品分类
    @ApiOperation("添加商品分类")
    @PostMapping("/save")
    public Result saveCategory(@RequestBody Category category) {
        return categoryService.saveCategory(category); // TODO 使用DTO id什么的不能传进来
    }

    //4.修改商品分类
    @ApiOperation("修改商品分类")
    @PutMapping("/update")
    public Result updateCategoryById(@RequestBody Category category) {
        return categoryService.updateCategoryById(category); // TODO 使用DTO id什么的不能传进来
    }

    //5.根据id删除商品分类
    @ApiOperation("根据id删除商品分类")
    @DeleteMapping("/remove/{id}")
    public Result deleteCategoryById(@PathVariable(name = "id") Integer id) {
        return categoryService.deleteCategoryById(id);
    }

    //6.批量删除商品分类
    @ApiOperation("批量删除商品分类")
    @DeleteMapping("/batchRemove")
    public Result deleteCategoryByIds(@RequestBody List<Long> ids) {
        return categoryService.deleteCategoryByIds(ids);
    }

    @ApiOperation("查询所有商品分类")
    @GetMapping("/findAllList")
    public Result findAllList() {
        return categoryService.findAllList();
    }


}
