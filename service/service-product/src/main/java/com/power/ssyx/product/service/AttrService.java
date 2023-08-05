package com.power.ssyx.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.model.product.Attr;

import java.util.List;

/**
 * @author power
 * @description 针对表【attr(商品属性)】的数据库操作Service
 * @createDate 2023-08-05 15:19:29
 */
public interface AttrService extends IService<Attr> {

    Result get(Long id);

    Result saveAttr(Attr category);

    Result updateAttrById(Attr category);

    Result deleteAttrById(Long id);

    Result deleteAttrByIds(List<Long> ids);

    Result getListByGroupId(Long groupId);
}
