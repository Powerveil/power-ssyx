package com.power.ssyx.acl.controller;

import com.power.ssyx.acl.service.AdminService;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.model.acl.Admin;
import com.power.ssyx.vo.acl.AdminQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Powerveil
 * @Date 2023/7/29 16:10
 */
@Api(tags = "用户接口")
@RestController
@CrossOrigin
@RequestMapping("/admin/acl/user")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // 1.用户列表（条件分页查询）
    @ApiOperation("用户列表")
    @GetMapping("/{page}/{limit}")
    public Result getPageList(@PathVariable("page") Integer page,
                              @PathVariable("limit") Integer limit,
                              AdminQueryVo adminQueryVo) {
        return adminService.getPageList(page, limit, adminQueryVo);
    }

    //2.根据id查询用户
    @ApiOperation("根据id查询用户")
    @GetMapping("/get/{id}")
    public Result get(@PathVariable(name = "id") Integer id) {
        return adminService.get(id);
    }

    //3.添加用户
    @ApiOperation("添加用户")
    @PostMapping("/save")
    public Result saveAdmin(@RequestBody Admin admin) {
        return adminService.saveAdmin(admin);// TODO 使用DTO id什么的不能传进来
    }

    //4.修改用户
    @ApiOperation("修改用户")
    @PutMapping("/update")
    public Result updateAdminById(@RequestBody Admin admin) {
        return adminService.updateAdminById(admin); // TODO 使用DTO id什么的不能传进来
    }

    //5.根据id删除用户
    @ApiOperation("根据id删除用户")
    @DeleteMapping("/remove/{id}")
    public Result deleteAdminById(@PathVariable(name = "id") Integer id) {
        return adminService.deleteAdminById(id);
    }

    //6.批量删除用户
    @ApiOperation("批量删除用户")
    @DeleteMapping("/batchRemove")
    public Result deleteAdminByIds(@RequestBody List<Long> ids) {
        return adminService.deleteAdminByIds(ids);
    }


    // 2.用户添加
    // 3.用户修改（id查询和修改）
    // 4.用户添加（id和批量删除）
}
