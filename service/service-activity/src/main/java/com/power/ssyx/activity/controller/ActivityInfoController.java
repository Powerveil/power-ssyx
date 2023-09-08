package com.power.ssyx.activity.controller;

import com.power.ssyx.activity.service.ActivityInfoService;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.model.activity.ActivityInfo;
import com.power.ssyx.vo.activity.ActivityRuleVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Powerveil
 * @Date 2023/8/21 22:56
 */
@RestController
@RequestMapping("/admin/activity/activityInfo")
//@CrossOrigin
public class ActivityInfoController {

    @Autowired
    private ActivityInfoService activityInfoService;

    @ApiOperation("活动列表")
    @GetMapping("/{page}/{limit}")
    public Result getPageList(@PathVariable("page") Integer page,
                              @PathVariable("limit") Integer limit) {
        return activityInfoService.getPageList(page, limit);
    }

    //2.根据id查询活动
    @ApiOperation("根据id查询活动")
    @GetMapping("/get/{id}")
    public Result get(@PathVariable(name = "id") Integer id) {
        return activityInfoService.get(id);
    }

    //3.添加活动
    @ApiOperation("添加活动")
    @PostMapping("/save")
    public Result saveActivityInfo(@RequestBody ActivityInfo activityInfo) {
        return activityInfoService.saveActivityInfo(activityInfo); // TODO 使用DTO id什么的不能传进来
    }

    //4.修改活动
    @ApiOperation("修改活动")
    @PutMapping("/update")
    public Result updateActivityInfoById(@RequestBody ActivityInfo activityInfo) {
        return activityInfoService.updateActivityInfoById(activityInfo); // TODO 使用DTO id什么的不能传进来
    }

    //5.根据id删除活动
    @ApiOperation("根据id删除活动")
    @DeleteMapping("/remove/{id}")
    public Result deleteActivityInfoById(@PathVariable(name = "id") Integer id) {
        return activityInfoService.deleteActivityInfoById(id);
    }

    //6.批量删除活动
    @ApiOperation("批量删除活动")
    @DeleteMapping("/batchRemove")
    public Result deleteActivityInfoByIds(@RequestBody List<Long> ids) {
        return activityInfoService.deleteActivityInfoByIds(ids);
    }

    // 营销活动规则相关接口

    // 1.根据活动id获取活动规则数据
    @GetMapping("/findActivityRuleList/{id}")
    public Result findActivityRuleList(@PathVariable(value = "id") Long id) {
        return activityInfoService.findActivityRuleList(id);
    }

    // 2.在活动里面添加规则数据

    @ApiOperation("")
    @PostMapping("/saveActivityRule")
    public Result saveActivityRule(@RequestBody ActivityRuleVo activityRuleVo) {
        return activityInfoService.saveActivityRule(activityRuleVo);
    }

    // 3.根据关键字查询匹配sku信息

    @ApiOperation("")
    @GetMapping("/findSkuInfoByKeyword/{keyword}")
    public Result findSkuInfoByKeyword(@PathVariable(value = "keyword") String keyword) {
        return activityInfoService.findSkuInfoByKeyword(keyword);
    }
}
