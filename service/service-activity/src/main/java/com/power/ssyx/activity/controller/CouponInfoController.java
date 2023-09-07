package com.power.ssyx.activity.controller;

import com.power.ssyx.activity.service.CouponInfoService;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.model.activity.CouponInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Powerveil
 * @Date 2023/9/7 22:19
 */
@RestController
@RequestMapping("/admin/activity/couponInfo")
@CrossOrigin
public class CouponInfoController {
    @Autowired
    private CouponInfoService couponInfoService;

    @ApiOperation("优惠卷列表")
    @GetMapping("/{page}/{limit}")
    public Result getPageList(@PathVariable("page") Integer page,
                              @PathVariable("limit") Integer limit) {
        return couponInfoService.getPageList(page, limit);
    }

    //2.根据id查询优惠卷
    @ApiOperation("根据id查询优惠卷")
    @GetMapping("/get/{id}")
    public Result get(@PathVariable(name = "id") Integer id) {
        return couponInfoService.get(id);
    }

    //3.添加优惠卷
    @ApiOperation("添加优惠卷")
    @PostMapping("/save")
    public Result saveCouponInfo(@RequestBody CouponInfo couponInfo) {
        return couponInfoService.saveCouponInfo(couponInfo); // TODO 使用DTO id什么的不能传进来
    }

    //4.修改优惠卷
    @ApiOperation("修改优惠卷")
    @PutMapping("/update")
    public Result updateCouponInfoById(@RequestBody CouponInfo couponInfo) {
        return couponInfoService.updateCouponInfoById(couponInfo); // TODO 使用DTO id什么的不能传进来
    }

    //5.根据id删除优惠卷
    @ApiOperation("根据id删除优惠卷")
    @DeleteMapping("/remove/{id}")
    public Result deleteCouponInfoById(@PathVariable(name = "id") Integer id) {
        return couponInfoService.deleteCouponInfoById(id);
    }

    //6.批量删除优惠卷
    @ApiOperation("批量删除优惠卷")
    @DeleteMapping("/batchRemove")
    public Result deleteCouponInfoByIds(@RequestBody List<Long> ids) {
        return couponInfoService.deleteCouponInfoByIds(ids);
    }

    // 营销优惠卷规则相关接口

    // 1.根据优惠卷id获取优惠卷规则数据
    @GetMapping("/findCouponRuleList/{id}")
    public Result findCouponRuleList(@PathVariable(value = "id") Long id) {
        return couponInfoService.findCouponRuleList(id);
    }

//    // 2.在优惠卷里面添加规则数据
//
//    @ApiOperation("")
//    @PostMapping("/saveCouponRule")
//    public Result saveActivityRule(@RequestBody ActivityRuleVo activityRuleVo) {
//        return couponInfoService.saveCouponRule(activityRuleVo);
//    }
//
//    // 3.根据关键字查询匹配sku信息
//
//    @ApiOperation("")
//    @GetMapping("/findCouponByKeyword/{keyword}")
//    public Result findCouponByKeyword(@PathVariable(value = "keyword") String keyword) {
//        return couponInfoService.findCouponByKeyword(keyword);
//    }
}
