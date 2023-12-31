package com.power.ssyx.acl.controller;

import com.power.ssyx.acl.service.PermissionService;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.model.acl.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Powerveil
 * @Date 2023/8/2 20:17
 */
@RestController
@RequestMapping("/admin/acl/permission")
@Api(tags = "菜单管理")
//@CrossOrigin //跨域
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    // 查询所有菜单
    @ApiOperation("查询所有菜单")
    @GetMapping("")
    public Result getPermissionList() {
        return permissionService.getPermissionList();
    }

    // 添加菜单
    @ApiOperation("添加菜单")
    @PostMapping("/save")
    public Result savePermission(@RequestBody Permission permission) {
        return permissionService.savePermission(permission);
    }

    // 修改菜单
    @ApiOperation("修改菜单")
    @PutMapping("/update")
    public Result updatePermission(@RequestBody Permission permission) {
        return permissionService.updatePermission(permission);
    }


    // 递归删除菜单
    @ApiOperation("递归删除菜单")
    @DeleteMapping("/remove/{id}")
    public Result removePermission(@PathVariable("id") Long id) {
        return permissionService.removePermission(id);
    }

    // 实现有问题 没有找到使用这两个接口地方 暂时不实现
//    // 查看某个角色的权限列表
//    @ApiOperation("查看某个角色的权限列表")
//    @GetMapping("/toAssign/{roleId}")
//    public Result toAssign(@PathVariable("roleId") Long roleId) {
//        return permissionService.toAssign(roleId);
//    }
//
//
//    // 给某个角色授权
//    @ApiOperation("给某个角色授权")
//    @PostMapping("/doAssign")
//    public Result doAssign(@RequestParam("roleId") Long roleId,
//                           @RequestParam("permissionId") Long[] permissionId) {
//        return permissionService.doAssign(roleId, permissionId);
//    }

}
