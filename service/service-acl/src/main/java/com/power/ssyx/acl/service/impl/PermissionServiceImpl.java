package com.power.ssyx.acl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.power.ssyx.acl.mapper.PermissionMapper;
import com.power.ssyx.acl.service.PermissionService;
import com.power.ssyx.acl.service.RolePermissionService;
import com.power.ssyx.common.exception.SsyxException;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.common.result.ResultCodeEnum;
import com.power.ssyx.common.utils.ParamCheckUtils;
import com.power.ssyx.model.acl.Permission;
import com.power.ssyx.model.acl.RolePermission;
import com.power.ssyx.model.base.BaseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Powerveil
 * @Date 2023/8/2 20:21
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {


    @Autowired
    private RolePermissionService rolePermissionService;

    @Override
    public Result getPermissionList() {

        LambdaQueryWrapper<Permission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Permission::getPid);


        List<Permission> list = list(queryWrapper);

        // 创建最终数据封装List集合
        List<Permission> trees = new ArrayList<>();
        // 遍历所有菜单List集合，得到一层数据，pid=0
        for (Permission permission : list) {
            if (permission.getPid() == 0) {
                permission.setLevel(1);
                trees.add(findChildren(permission, list));
            }
        }


        return Result.ok(trees);
    }

    /**
     * 将当前权限及其子权限添加到 trees 列表中。
     *
     * @param permission 当前权限对象
     * @param list       完整的权限列表
     * @return
     */
    private Permission findChildren(Permission permission, List<Permission> list) {
        permission.setChildren(new ArrayList<>());
        for (Permission temp : list) {
            if (temp.getPid().equals(permission.getId())) {
                temp.setLevel(permission.getLevel() + 1);
                permission.getChildren().add(findChildren(temp, list));
            }
        }
        return permission;
    }

    private List<Permission> getChildrens(Long id) {
        LambdaQueryWrapper<Permission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Permission::getPid, id);
        queryWrapper.orderByAsc(Permission::getPid);

        List<Permission> list = list(queryWrapper);
        for (Permission permission : list) {
            permission.setChildren(getChildrens(permission.getId()));
        }
        return list;
    }

    @Override
    public Result savePermission(Permission permission) {
        save(permission);
        return Result.ok(null);
    }

    @Override
    public Result updatePermission(Permission permission) {
        updateById(permission);
        return Result.ok(null);
    }

    /**
     * 通过递归方式获取给定权限及其所有子权限的 ID，并将它们存储在一个列表中。然后，批量删除这些权限记录。
     * @param id
     * @return 要删除的权限的 id
     */
    @Override
    public Result removePermission(Long id) {
//        deleteById(id);
        List<Long> ids = new ArrayList<>();
        // 获取所有子权限的 ID
        getAllChildrenPermission(id, ids);

        ids.add(id);
        baseMapper.deleteBatchIds(ids);
        return Result.ok(null);
    }


    // TODO
    @Override
    public Result doAssign(Long roleId, Long[] permissionId) {
        if (ParamCheckUtils.validateParams(roleId, permissionId)) {
            throw new SsyxException(ResultCodeEnum.PARAM_ERROR);
        }
        List<RolePermission> allPermissions = rolePermissionService.list();
        LambdaQueryWrapper<RolePermission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RolePermission::getRoleId, roleId);


        List<RolePermission> list = rolePermissionService.list(queryWrapper);


        Map<String, Object> map = new HashMap<>();
        map.put("allPermissions", allPermissions);
        return Result.ok(map);
    }

    // TODO
    @Override
    public Result toAssign(Long roleId) {
        Map<String, Object> map = new HashMap<>();
        map.put("allPermissions", new Permission());
        return Result.ok(map);
    }

    private void deleteById(Long id) {
        LambdaQueryWrapper<Permission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Permission::getPid, id);
        List<Permission> list = list(queryWrapper);
        if (Objects.isNull(list)) {
            removeById(id);
            return;
        }
        for (Permission permission : list) {
            deleteById(permission.getId());
        }
        removeById(id);
    }

    private void getAllChildrenPermission(Long id, List<Long> permissions) {
        LambdaQueryWrapper<Permission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Permission::getPid, id);
        List<Long> list = list(queryWrapper)
                .stream()
                .map(BaseEntity::getId)
                .collect(Collectors.toList());
        for (Long temp : list) {
            // 递归调用用于获取当前权限的子权限
            getAllChildrenPermission(temp, permissions);
        }
        permissions.addAll(list);
    }


}
