package com.power.ssyx.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.common.result.ResultCodeEnum;
import com.power.ssyx.model.product.AttrGroup;
import com.power.ssyx.product.mapper.AttrGroupMapper;
import com.power.ssyx.product.service.AttrGroupService;
import com.power.ssyx.vo.product.AttrGroupQueryVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * @author power
 * @description 针对表【attr_group(属性分组)】的数据库操作Service实现
 * @createDate 2023-08-05 15:19:29
 */
@Service
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupMapper, AttrGroup>
        implements AttrGroupService {
    @Override
    public Result getPageList(Integer page, Integer limit, AttrGroupQueryVo attrGroupQueryVo) {
        Page<AttrGroup> pageParam = new Page<>(page, limit);

        LambdaQueryWrapper<AttrGroup> queryWrapper = new LambdaQueryWrapper<>();
        // 根据名字模糊查询
        if (!Objects.isNull(attrGroupQueryVo) && StringUtils.hasText(attrGroupQueryVo.getName())) {
            queryWrapper.like(AttrGroup::getName, attrGroupQueryVo.getName());
        }

        IPage<AttrGroup> result = baseMapper.selectPage(pageParam, queryWrapper);
        return Result.ok(result);
    }

    @Override
    public Result get(Long id) {
        AttrGroup attrGroup = this.getById(id);
        return Result.ok(attrGroup);
    }

    @Override
    public Result saveAttrGroup(AttrGroup attrGroup) {
        // TODO 一定要使用DTO接受数据！！！
        attrGroup.setId(null);// 不能这样做
        // 组名需要存在
        String attrGroupName = attrGroup.getName();
        if (!StringUtils.hasText(attrGroupName)) {
            return Result.build(null, ResultCodeEnum.ATTR_GROUP_NAME_IS_BLANK);
        }
        // 数据库不能有相同的组名
        LambdaQueryWrapper<AttrGroup> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AttrGroup::getName, attrGroupName);
        if (count(queryWrapper) > 0) {
            return Result.build(null, ResultCodeEnum.ATTR_GROUP_IS_EXIST);
        }
        if (this.save(attrGroup)) {
            return Result.ok(null);
        }
        return Result.fail("添加属性分组失败");
    }

    @Override
    public Result updateAttrGroupById(AttrGroup attrGroup) {
        // Id不能为空
        if (Objects.isNull(attrGroup.getId())) {
            return Result.build(null, ResultCodeEnum.ID_IS_NULL);
        }
        // 组名需要存在
        String attrGroupName = attrGroup.getName();
        if (!StringUtils.hasText(attrGroupName)) {
            return Result.build(null, ResultCodeEnum.ATTR_GROUP_NAME_IS_BLANK);
        }
        // 数据库不能有相同的属性分组名
        LambdaQueryWrapper<AttrGroup> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AttrGroup::getName, attrGroupName);
        AttrGroup one = this.getOne(queryWrapper);
        if (!Objects.isNull(one) && !one.getId().equals(attrGroup.getId())) {
            return Result.build(null, ResultCodeEnum.ATTR_GROUP_IS_EXIST);
        }
        if (this.updateById(attrGroup)) {
            return Result.ok(null);
        }
        return Result.fail("更新属性分组失败");
    }

    @Override
    public Result deleteAttrGroupById(Long id) {
        if (this.removeById(id)) {
            return Result.ok(null);
        }
        return Result.fail("删除属性分组失败");
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public Result deleteAttrGroupByIds(List<Long> ids) {
        if (this.removeByIds(ids)) {
            return Result.ok(null);
        }
        return Result.fail("批量删除属性分组失败");
    }

    @Override
    public Result findAllList() {
        LambdaQueryWrapper<AttrGroup> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(AttrGroup::getId);
        List<AttrGroup> list = list(queryWrapper);
        return Result.ok(list);
    }
}




