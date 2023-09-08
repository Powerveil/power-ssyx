package com.power.ssyx.acl.controller;

import com.power.ssyx.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Powerveil
 * @Date 2023/7/23 17:01
 */
@Api(tags = "index接口")
@RestController
@RequestMapping("/admin/acl/index")
//@CrossOrigin
public class IndexController {

    //1.login 登录
    //2.getInfo 获取信息
    //3.logout 退出

    @ApiOperation("登录")
    @PostMapping("/login")
    public Result login() {
        // 返回token值
        Map<String, String> map = new HashMap<>();
        map.put("token", "Powerveil Hello World!");
        return Result.ok(map);
    }

    @ApiOperation("获取用户信息")
    @GetMapping("/info")
    public Result getInfo() {
        Map<String, String> map = new HashMap<>();
        map.put("name", "Powerveil");
        map.put("avatar", "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        return Result.ok(map);
    }

    @ApiOperation("退出")
    @PostMapping("/logout")
    public Result logout() {
        return Result.ok(null);
    }


//    @GetMapping("/test")
//    public AA test() {
//        AA aa = new AA();
//        aa.setId(0);
//        aa.setName("");
//        aa.setMy_id(0);
//        ArrayList<String> list = new ArrayList<>();
//        list.add("张三");
//        list.add("李四");
//        list.add("王五");
//        aa.setList(list);
//        return aa;
//    }
}
