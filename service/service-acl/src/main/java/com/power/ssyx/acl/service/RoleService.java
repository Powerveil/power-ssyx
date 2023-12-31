package com.power.ssyx.acl.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.model.acl.Role;
import com.power.ssyx.vo.acl.RoleQueryVo;

import java.util.List;
import java.util.Map;

/**
 * @author Powerveil
 * @Date 2023/7/24 19:53
 */
public interface RoleService extends IService<Role> {
    Result pageList(Integer current, Integer limit, RoleQueryVo roleQueryVo);

    IPage<Role> selectRolePage(Page<Role> pageParam, RoleQueryVo roleQueryVo);

    Result get(Long id);

    Result saveRole(Role role);

    Result updateRoleById(Role role);

    Result deleteRoleById(Long id);

    Result deleteRoleByIds(List<Long> ids);

    Map<String, Object> getRoleByAdminId(Long adminId);

    //为用户进行角色分配
    Result saveAdminRole(Long adminId, Long[] roleId);
}
