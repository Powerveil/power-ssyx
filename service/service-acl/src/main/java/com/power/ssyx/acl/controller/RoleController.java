package com.power.ssyx.acl.controller;

import com.power.ssyx.acl.service.RoleService;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.model.acl.Role;
import com.power.ssyx.vo.acl.RoleQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Powerveil
 * @Date 2023/7/24 19:51
 */
@Api(tags = "角色接口")
@RestController
@CrossOrigin
@RequestMapping("/admin/acl/role")
public class RoleController {

    //注入service
    @Autowired
    private RoleService roleService;

    //1. 角色列表（条件分页查询）
    @ApiOperation("角色列表（条件分页查询）")
    @GetMapping("/{current}/{limit}")
    public Result pageList(@PathVariable(name = "current") Integer current,
                           @PathVariable(name = "limit") Integer limit,
                           RoleQueryVo roleQueryVo) {
        return roleService.pageList(current, limit, roleQueryVo);
    }

    //2.根据id查询角色
    @ApiOperation("根据id查询角色")
    @GetMapping("/get/{id}")
    public Result get(@PathVariable(name = "id") Integer id) {
        return roleService.get(id);
    }

    //3.添加角色
    @ApiOperation("添加角色")
    @PostMapping("/save")
    public Result saveRole(@RequestBody Role role) {
        return roleService.saveRole(role); // TODO 使用DTO id什么的不能传进来
    }

    //4.修改角色
    @ApiOperation("修改角色")
    @PutMapping("/update")
    public Result updateRoleById(@RequestBody Role role) {
        return roleService.updateRoleById(role); // TODO 使用DTO id什么的不能传进来
    }

    //5.根据id删除角色
    @ApiOperation("根据id删除角色")
    @DeleteMapping("/remove/{id}")
    public Result deleteRoleById(@PathVariable(name = "id") Integer id) {
        return roleService.deleteRoleById(id);
    }

    //6.批量删除角色
    @ApiOperation("批量删除角色")
    @DeleteMapping("/batchRemove")
    public Result deleteRoleByIds(@RequestBody List<Long> ids) {
        return roleService.deleteRoleByIds(ids);
    }


}
