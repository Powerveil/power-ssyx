package com.power.ssyx.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.model.product.Category;
import com.power.ssyx.vo.product.CategoryVo;

import java.util.List;

/**
 * @author power
 * @description 针对表【category(商品三级分类)】的数据库操作Service
 * @createDate 2023-08-05 15:19:29
 */
public interface CategoryService extends IService<Category> {

    Result getPageList(Integer page, Integer limit, CategoryVo categoryVo);

    Result get(Integer id);

    Result saveCategory(Category category);

    Result updateCategoryById(Category category);

    Result deleteCategoryById(Integer id);

    Result deleteCategoryByIds(List<Long> ids);

    Result findAllList();

    List<Category> getCategoryListByIds(List<Long> ids);
}
