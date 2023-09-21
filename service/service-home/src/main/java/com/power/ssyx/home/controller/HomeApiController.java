package com.power.ssyx.home.controller;

import com.power.ssyx.common.result.Result;
import com.power.ssyx.home.service.HomeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Powerveil
 * @Date 2023/9/20 17:47
 */
@Api(tags = "首页接口")
@RestController
@RequestMapping("/api/home")
public class HomeApiController {

    @Autowired
    private HomeService homeService;

    @ApiOperation("首页数据显示接口")
    @GetMapping("/index")
    public Result index(HttpServletRequest request) {
        return homeService.homeData(request);
    }

}
