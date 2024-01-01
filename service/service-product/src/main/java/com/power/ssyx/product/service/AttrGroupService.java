package com.power.ssyx.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.model.product.AttrGroup;
import com.power.ssyx.vo.product.AttrGroupQueryVo;

import java.util.List;

/**
 * @author power
 * @description 针对表【attr_group(属性分组)】的数据库操作Service
 * @createDate 2023-08-05 15:19:29
 */
public interface AttrGroupService extends IService<AttrGroup> {

    Result getPageList(Integer page, Integer limit, AttrGroupQueryVo attrGroupQueryVo);

    Result get(Long id);

    Result saveAttrGroup(AttrGroup category);

    Result updateAttrGroupById(AttrGroup category);

    Result deleteAttrGroupById(Long id);

    Result deleteAttrGroupByIds(List<Long> ids);

    Result findAllList();

}
