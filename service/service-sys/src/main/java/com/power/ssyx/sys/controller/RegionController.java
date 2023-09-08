package com.power.ssyx.sys.controller;

import com.power.ssyx.common.result.Result;
import com.power.ssyx.sys.service.RegionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Powerveil
 * @Date 2023/8/3 23:06
 */
@RestController
//@CrossOrigin
@Api(tags = "区域接口")
@RequestMapping("/admin/sys/region")
public class RegionController {

    @Autowired
    private RegionService regionService;

    @ApiOperation("根据区域关键字查询区域列表信息")
    @GetMapping("/findRegionByKeyword/{keyword}")
    public Result findRegionByKeyword(@PathVariable("keyword") String keyword) {
        return regionService.findRegionByKeyword(keyword);
    }


}
