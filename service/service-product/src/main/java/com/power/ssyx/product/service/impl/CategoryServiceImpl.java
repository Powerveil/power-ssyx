package com.power.ssyx.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.common.result.ResultCodeEnum;
import com.power.ssyx.model.product.Category;
import com.power.ssyx.product.mapper.CategoryMapper;
import com.power.ssyx.product.service.CategoryService;
import com.power.ssyx.vo.product.CategoryVo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * @author power
 * @description 针对表【category(商品三级分类)】的数据库操作Service实现
 * @createDate 2023-08-05 15:19:29
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
        implements CategoryService {

    @Override
    public Result getPageList(Integer page, Integer limit, CategoryVo categoryVo) {
        Page<Category> pageParam = new Page<>(page, limit);

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        // 根据名字模糊查询
        if (!Objects.isNull(categoryVo) && StringUtils.hasText(categoryVo.getName())) {
            queryWrapper.like(Category::getName, categoryVo.getName());
        }

        IPage<Category> result = baseMapper.selectPage(pageParam, queryWrapper);
        return Result.ok(result);
    }

    @Override
    public Result get(Long id) {
        Category category = this.getById(id);
        return Result.ok(category);
    }

    @Override
    public Result saveCategory(Category category) {
        // TODO 一定要使用DTO接受数据！！！
        category.setId(null);// 不能这样做
        // 权限名需要存在
        String categoryName = category.getName();
        if (!StringUtils.hasText(categoryName)) {
            return Result.build(null, ResultCodeEnum.CATEGORY_NAME_IS_BLANK);
        }
        // 数据库不能有相同的权限名
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getName, categoryName);
        if (this.count(queryWrapper) > 0) {
            return Result.build(null, ResultCodeEnum.CATEGORY_IS_EXIST);
        }
        if (this.save(category)) {
            return Result.ok(null);
        }
        return Result.fail("添加商品分类失败");
    }

    @Override
    public Result updateCategoryById(Category category) {
        // Id不能为空
        if (Objects.isNull(category.getId())) {
            return Result.build(null, ResultCodeEnum.ID_IS_NULL);
        }
        // 权限名需要存在
        String categoryName = category.getName();
        if (!StringUtils.hasText(categoryName)) {
            return Result.build(null, ResultCodeEnum.CATEGORY_NAME_IS_BLANK);
        }
        // 数据库不能有相同的商品分类名
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getName, categoryName);
        Category one = this.getOne(queryWrapper);
        if (Objects.isNull(one)) {
            return Result.build(null, ResultCodeEnum.DATA_ERROR);
        }
        if (!one.getId().equals(category.getId())) {
            return Result.build(null, ResultCodeEnum.CATEGORY_IS_EXIST);
        }
        if (this.updateById(category)) {
            return Result.ok(null);
        }
        return Result.fail("更新商品分类失败");
    }

    @Override
    public Result deleteCategoryById(Long id) {
        if (this.removeById(id)) {
            return Result.ok(null);
        }
        return Result.fail("删除商品分类失败");
    }

    @Override
    public Result deleteCategoryByIds(List<Long> ids) {
        if (this.removeByIds(ids)) {
            return Result.ok(null);
        }
        return Result.fail("批量删除商品分类失败");
    }

    @Override
    public Result findAllList() {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort);
        List<Category> list = this.list(queryWrapper);
        return Result.ok(list);
    }

    @Override
    public List<Category> getCategoryListByIds(List<Long> ids) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Category::getId, ids);
        List<Category> categoryList = this.list(queryWrapper);
        return categoryList;
    }

    @Override
    public List<Category> findAllCategoryList() {
        return this.list();
    }
}