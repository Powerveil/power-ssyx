package com.power.ssyx.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.power.ssyx.model.product.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author power
 * @description 针对表【category(商品三级分类)】的数据库操作Mapper
 * @createDate 2023-08-05 15:19:29
 * @Entity com.power.ssyx.product.domain.Category
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

}




