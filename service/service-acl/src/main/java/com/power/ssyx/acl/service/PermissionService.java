package com.power.ssyx.acl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.model.acl.Permission;

/**
 * @author Powerveil
 * @Date 2023/8/2 20:20
 */
public interface PermissionService extends IService<Permission> {
    Result getPermissionList();

    Result savePermission(Permission permission);

    Result updatePermission(Permission permission);

    Result removePermission(Long id);

    Result doAssign(Long roleId, Long[] permissionId);

    Result toAssign(Long roleId);
}
