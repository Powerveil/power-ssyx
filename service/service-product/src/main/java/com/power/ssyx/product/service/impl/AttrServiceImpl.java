package com.power.ssyx.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.common.result.ResultCodeEnum;
import com.power.ssyx.model.product.Attr;
import com.power.ssyx.product.mapper.AttrMapper;
import com.power.ssyx.product.service.AttrService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * @author power
 * @description 针对表【attr(商品属性)】的数据库操作Service实现
 * @createDate 2023-08-05 15:19:29
 */
@Service
public class AttrServiceImpl extends ServiceImpl<AttrMapper, Attr>
        implements AttrService {

    @Override
    public Result get(Long id) {
        Attr attr = this.getById(id);
        return Result.ok(attr);
    }

    @Override
    public Result saveAttr(Attr attr) {
        // TODO 一定要使用DTO接受数据！！！
        attr.setId(null);// 不能这样做
        // 商品属性名需要存在
        String attrName = attr.getName();
        if (!StringUtils.hasText(attrName)) {
            return Result.build(null, ResultCodeEnum.ATTR_GROUP_NAME_IS_BLANK);
        }
        // 数据库不能有相同的商品属性名
        LambdaQueryWrapper<Attr> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Attr::getName, attrName);
        if (count(queryWrapper) > 0) {
            return Result.build(null, ResultCodeEnum.ATTR_GROUP_IS_EXIST);
        }
        if (this.save(attr)) {
            return Result.ok(null);
        }
        return Result.fail("添加属性分组失败");
    }

    @Override
    public Result updateAttrById(Attr attr) {
        // Id不能为空
        if (Objects.isNull(attr.getId())) {
            return Result.build(null, ResultCodeEnum.ID_IS_NULL);
        }
        // 商品属性名需要存在
        String attrName = attr.getName();
        if (!StringUtils.hasText(attrName)) {
            return Result.build(null, ResultCodeEnum.ATTR_GROUP_NAME_IS_BLANK);
        }
        // 数据库不能有相同的属性分组名
        LambdaQueryWrapper<Attr> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Attr::getName, attrName);
        Attr one = getOne(queryWrapper);
        if (!one.getId().equals(attr.getId())) {
            return Result.build(null, ResultCodeEnum.ATTR_GROUP_IS_EXIST);
        }
        if (this.updateById(attr)) {
            return Result.ok(null);
        }
        return Result.fail("更新属性分组失败");
    }

    @Override
    public Result deleteAttrById(Long id) {
        if (this.removeById(id)) {
            return Result.ok(null);
        }
        return Result.fail("删除属性分组失败");
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public Result deleteAttrByIds(List<Long> ids) {
        if (this.removeByIds(ids)) {
            return Result.ok(null);
        }
        return Result.fail("批量删除属性分组失败");
    }

    @Override
    public Result getListByGroupId(Long groupId) {
        LambdaQueryWrapper<Attr> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(!Objects.isNull(groupId), Attr::getAttrGroupId, groupId);
        List<Attr> list = list(queryWrapper);
        return Result.ok(list);
    }

}




