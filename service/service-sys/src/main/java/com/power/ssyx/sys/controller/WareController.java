package com.power.ssyx.sys.controller;

import com.power.ssyx.common.result.Result;
import com.power.ssyx.sys.service.WareService;
import com.power.ssyx.vo.product.WareQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Powerveil
 * @Date 2023/8/3 23:01
 */
@RestController
@CrossOrigin
@Api(tags = "仓库接口")
@RequestMapping("/admin/sys/ware")
public class WareController {
    @Autowired
    private WareService wareService;


    @ApiOperation("分页查询仓库列表")
    @GetMapping("/{page}/{limit}")
    public Result getPageList(@PathVariable("page") Integer page,
                              @PathVariable("limit") Integer limit,
                              WareQueryVo wareQueryVo) {
        return wareService.getPageList(page, limit, wareQueryVo);
    }

    @ApiOperation("查询所有仓库")
    @GetMapping("/findAllList")
    public Result findAllList() {
        return wareService.findAllList();
    }
}
