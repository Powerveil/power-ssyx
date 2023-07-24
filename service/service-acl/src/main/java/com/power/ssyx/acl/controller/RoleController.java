package com.power.ssyx.acl.controller;

import com.power.ssyx.acl.service.RoleService;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.vo.acl.RoleQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    //3.添加角色

    //4.修改角色

    //5.根据id删除角色

    //6.批量删除角色

}
