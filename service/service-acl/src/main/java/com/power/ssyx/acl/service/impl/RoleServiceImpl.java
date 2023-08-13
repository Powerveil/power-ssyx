package com.power.ssyx.acl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.power.ssyx.acl.mapper.RoleMapper;
import com.power.ssyx.acl.service.AdminRoleService;
import com.power.ssyx.acl.service.RoleService;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.common.result.ResultCodeEnum;
import com.power.ssyx.model.acl.AdminRole;
import com.power.ssyx.model.acl.Role;
import com.power.ssyx.vo.acl.RoleQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Powerveil
 * @Date 2023/7/24 19:54
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {


    @Autowired
    private AdminRoleService adminRoleService;

    @Override
    public Result pageList(Integer current, Integer limit, RoleQueryVo roleQueryVo) {
        //1.创建page对象，传递当前页和每页记录数
        // current：当前页
        // limit：每页显示记录数
        Page<Role> pageParam = new Page<>(current, limit);
        //2.调用service方法实现条件分页查询，返回分页对象
        IPage<Role> pageModel = this.selectRolePage(pageParam, roleQueryVo);
        return Result.ok(pageModel);
    }

    @Override
    public IPage<Role> selectRolePage(Page<Role> pageParam, RoleQueryVo roleQueryVo) {
        // 获取条件值
        String roleName = roleQueryVo.getRoleName();

        // 判断吗条件值是否为空，不为空封装查询条件
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(roleName)) {
            queryWrapper.like(Role::getRoleName, roleName);
        }
        queryWrapper.orderByAsc(Role::getCreateTime);
        // 调用方法实现条件分页查询
        IPage<Role> page = baseMapper.selectPage(pageParam, queryWrapper);
        // 返回分页对象
        return page;
    }

    @Override
    public Result get(Integer id) {
        Role role = this.getById(id);
        return Result.ok(role);
    }

    @Override
    public Result saveRole(Role role) {
        // TODO 一定要使用DTO接受数据！！！
        role.setId(null);// 不能这样做
        // 权限名需要存在
        String roleName = role.getRoleName();
        if (!StringUtils.hasText(roleName)) {
            return Result.build(null, ResultCodeEnum.ROLE_NAME_IS_BLANK);
        }
        // 数据库不能有相同的权限名
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Role::getRoleName, roleName);
        if (count(queryWrapper) > 0) {
            return Result.build(null, ResultCodeEnum.ROLE_IS_EXIST);
        }
//        System.out.print(role.getId() + "：");
//        System.out.println(role);
        if (this.save(role)) {
//            System.out.print(role.getId() + "：");
//            System.out.println(role);
//            System.out.println(role.getCreateTime());
//            System.out.println(role.getUpdateTime());
            return Result.ok(null);
        }
        return Result.fail("添加角色失败");
    }

    @Override
    public Result updateRoleById(Role role) {
        // Id不能为空
        if (Objects.isNull(role.getId())) {
            return Result.build(null, ResultCodeEnum.ID_IS_NULL);
        }
        // 权限名需要存在
        String roleName = role.getRoleName();
        if (!StringUtils.hasText(roleName)) {
            return Result.build(null, ResultCodeEnum.ROLE_NAME_IS_BLANK);
        }
        // 数据库不能有相同的权限名
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Role::getRoleName, roleName);
        Role one = getOne(queryWrapper);
        if (!Objects.isNull(one) && !one.getId().equals(role.getId())) {
            return Result.build(null, ResultCodeEnum.ROLE_IS_EXIST);
        }
        if (this.updateById(role)) {
            return Result.ok(null);
        }
        return Result.fail("更新角色失败");
    }

    @Override
    public Result deleteRoleById(Integer id) {
        if (this.removeById(id)) {
            return Result.ok(null);
        }
        return Result.fail("删除角色失败");
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public Result deleteRoleByIds(List<Long> ids) {
        if (this.removeByIds(ids)) {
            return Result.ok(null);
        }
        return Result.fail("批量删除角色失败");
    }

    @Override
    public Map<String, Object> getRoleByAdminId(Integer adminId) {

        List<Role> allRolesList = list();

        LambdaQueryWrapper<AdminRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdminRole::getAdminId, adminId);

        List<Long> roleIds = adminRoleService.list(queryWrapper).stream().map(AdminRole::getRoleId).collect(Collectors.toList());


        List<Role> assignRoleList = new ArrayList<>();
        for (Role role : allRolesList) {
            if (roleIds.contains(role.getId())) {
                assignRoleList.add(role);
            }
        }

        Map<String, Object> map = new HashMap<>();
        map.put("allRolesList", allRolesList);
        map.put("assignRoles", assignRoleList);
        return map;
    }

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public Result saveAdminRole(Long adminId, Long[] roleId) {
        LambdaQueryWrapper<AdminRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdminRole::getAdminId, adminId);
        adminRoleService.remove(queryWrapper);

        List<AdminRole> list = new ArrayList<>();

        for (int i = 0; i < roleId.length; i++) {
            AdminRole adminRole = new AdminRole();
            adminRole.setAdminId(adminId);
            adminRole.setRoleId(roleId[i]);
            list.add(adminRole);
        }
        adminRoleService.saveBatch(list);

        return Result.ok(null);
    }
}
