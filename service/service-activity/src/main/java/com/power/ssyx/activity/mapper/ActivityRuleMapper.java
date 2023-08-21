package com.power.ssyx.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.power.ssyx.model.activity.ActivityRule;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author power
 * @description 针对表【activity_rule(优惠规则)】的数据库操作Mapper
 * @createDate 2023-08-21 22:53:23
 * @Entity com.power.ssyx.activity.domain.ActivityRule
 */
@Mapper
public interface ActivityRuleMapper extends BaseMapper<ActivityRule> {

}




