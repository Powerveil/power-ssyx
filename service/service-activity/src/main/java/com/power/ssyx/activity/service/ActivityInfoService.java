package com.power.ssyx.activity.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.model.activity.ActivityInfo;
import com.power.ssyx.vo.activity.ActivityRuleVo;

import java.util.List;
import java.util.Map;

/**
 * @author power
 * @description 针对表【activity_info(活动表)】的数据库操作Service
 * @createDate 2023-08-21 22:53:23
 */
public interface ActivityInfoService extends IService<ActivityInfo> {

    Result getPageList(Integer page, Integer limit);

    Result get(Integer id);

    Result saveActivityInfo(ActivityInfo activityInfo);

    Result updateActivityInfoById(ActivityInfo activityInfo);

    Result deleteActivityInfoById(Integer id);

    Result deleteActivityInfoByIds(List<Long> ids);

    Result findActivityRuleList(Long id);

    Result saveActivityRule(ActivityRuleVo activityRuleVo);

    Result findSkuInfoByKeyword(String keyword);

    Map<Long, List<String>> findActivity(List<Long> skuIdList);
}
