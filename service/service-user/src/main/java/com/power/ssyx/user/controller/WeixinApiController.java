package com.power.ssyx.user.controller;

import com.power.ssyx.common.auth.AuthContextHolder;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.model.user.User;
import com.power.ssyx.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Powerveil
 * @Date 2023/9/14 18:29
 */
@RestController
@RequestMapping("/api/user/weixin")
public class WeixinApiController {

    @Autowired
    private UserService userService;


    // 用户微信授权登录
    @ApiOperation(value = "微信登录获取openid(小程序)")
    @GetMapping("/wxLogin/{code}")
    public Result loginWx(@PathVariable String code) {
        return userService.loginWx(code);
    }

    @PostMapping("/auth/updateUser")
    @ApiOperation(value = "更新用户昵称与头像")
    public Result updateUser(@RequestBody User user) {
        // 获取当前登录用户id
        User user1 = userService.getById(AuthContextHolder.getUserId());
        // 把昵称更新为微信用户
        user1.setNickName(user.getNickName().replaceAll("[ue000-uefff]", "*"));
        user1.setPhotoUrl(user.getPhotoUrl());
        userService.updateById(user1);
        return Result.ok(null);
    }
}
